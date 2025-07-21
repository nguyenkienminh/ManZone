package com.example.man_zone.websocket;

import android.content.Context;
import android.util.Log;

import com.example.man_zone.Model.ChatMessage;
import com.example.man_zone.Model.ConversationModel;
import com.example.man_zone.Model.ConversationUpdateResponse;
import com.example.man_zone.Model.MessageResponse;
import com.example.man_zone.Model.WebSocketMessage;
import com.example.man_zone.Utils.PrefsHelper;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.neovisionaries.ws.client.WebSocket;
import com.neovisionaries.ws.client.WebSocketAdapter;
import com.neovisionaries.ws.client.WebSocketException;
import com.neovisionaries.ws.client.WebSocketFactory;
import com.neovisionaries.ws.client.WebSocketFrame;

import java.io.IOException;
import java.lang.reflect.Type;
import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class WebSocketManager {
    private static final String TAG = "WebSocketManager";
    private static WebSocketManager instance;
    private WebSocket webSocket;
    private Gson gson;
    private boolean isConnected = false;
    private ExecutorService executorService = Executors.newSingleThreadExecutor();
    private Context context;
    private static final String WS_URL = "wss://manzone.wizlab.io.vn/ws-chat/websocket";
    private int reconnectAttempts = 0;
    private static final int MAX_RECONNECT_ATTEMPTS = 5;
    private static final long RECONNECT_DELAY_MS = 3000; // 3 seconds

    // Callbacks
    private ChatMessageListener chatMessageListener;
    private ConversationListener conversationListener;
    private ConnectionListener connectionListener;
    private ConversationUpdateListener conversationUpdateListener;

    // STOMP variables
    private String sessionId;
    private Map<String, String> subscriptions;
    private int currentConversationId = -1; // Track currently subscribed conversation

    public interface ChatMessageListener {
        void onMessageReceived(MessageResponse.Message message);
    }

    public interface ConversationListener {
        void onNewConversation(ConversationModel conversation);
    }

    public interface ConversationUpdateListener {
        void onConversationsUpdated(List<ConversationModel> conversations);
    }

    public interface ConnectionListener {
        void onConnected();

        void onDisconnected();

        void onError(Exception error);
    }

    private WebSocketManager() {
        // Configure Gson to handle Instant serialization/deserialization
        gson = new GsonBuilder()
                .registerTypeAdapter(Instant.class, new InstantAdapter())
                .create();
        subscriptions = new HashMap<>();
    }

    public static synchronized WebSocketManager getInstance() {
        if (instance == null) {
            instance = new WebSocketManager();
        }
        return instance;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    private void scheduleReconnect() {
        if (reconnectAttempts < MAX_RECONNECT_ATTEMPTS) {
            reconnectAttempts++;
            Log.d(TAG, "Scheduling reconnection attempt " + reconnectAttempts + "/" + MAX_RECONNECT_ATTEMPTS +
                    " in " + RECONNECT_DELAY_MS + "ms (Current connection state: " + isConnected + ")");

            executorService.execute(() -> {
                try {
                    Thread.sleep(RECONNECT_DELAY_MS);
                    Log.d(TAG, "Attempting reconnection " + reconnectAttempts +
                            " (Current connection state before reconnect: " + isConnected + ")");

                    // Only reconnect if we're actually disconnected
                    if (!isConnected || webSocket == null || !webSocket.isOpen()) {
                        connectToWebSocket();
                    } else {
                        Log.w(TAG, "Skipping reconnection - WebSocket is already connected");
                        reconnectAttempts--; // Revert the attempt counter
                    }
                } catch (InterruptedException e) {
                    Log.e(TAG, "Reconnection interrupted", e);
                    Thread.currentThread().interrupt();
                }
            });
        } else {
            Log.e(TAG, "Max reconnection attempts reached. Connection failed.");
            reconnectAttempts = 0; // Reset for next manual connection attempt
            if (connectionListener != null) {
                connectionListener.onError(new Exception("Max reconnection attempts reached"));
            }
        }
    }

    private void connectToWebSocket() {
        try {
            // Check if already connected
            if (isConnected && webSocket != null && webSocket.isOpen()) {
                Log.w(TAG, "WebSocket already connected, skipping connection attempt");
                return;
            }

            // Clean up existing connection if any
            if (webSocket != null) {
                Log.d(TAG, "Cleaning up existing WebSocket connection");
                try {
                    webSocket.disconnect();
                } catch (Exception e) {
                    Log.w(TAG, "Error cleaning up existing connection: " + e.getMessage());
                }
            }

            String token = "";
            if (context != null) {
                token = PrefsHelper.getToken(context);
                Log.d(TAG, "Using token for WebSocket: " + (!token.isEmpty() ? "present" : "missing"));
            }

            WebSocketFactory factory = new WebSocketFactory();
            Log.d(TAG, "Connecting to WebSocket URL: " + WS_URL);
            webSocket = factory.createSocket(WS_URL);

            // Add authentication header if token is available
            if (!token.isEmpty()) {
                String authHeader = "Bearer " + token;
                webSocket.addHeader("Authorization", authHeader);
                Log.d(TAG, "Added Authorization header: Bearer <token>");
            }

            webSocket.addListener(new WebSocketAdapter() {
                @Override
                public void onConnected(WebSocket websocket, Map<String, List<String>> headers) {
                    Log.d(TAG, "WebSocket connected successfully to: " + WS_URL);
                    isConnected = true;
                    reconnectAttempts = 0; // Reset reconnection counter on successful connection

                    // Only clear subscriptions on first connection or after explicit disconnect
                    // Don't clear on auto-reconnections to preserve subscription state
                    if (subscriptions.isEmpty()) {
                        Log.d(TAG, "Fresh connection - no existing subscriptions to preserve");
                    } else {
                        Log.d(TAG, "Reconnection - preserving existing subscription state");
                        logSubscriptionStatus();
                    }

                    // Send STOMP CONNECT frame
                    sendStompConnect();

                    if (connectionListener != null) {
                        connectionListener.onConnected();
                    }
                }

                @Override
                public void onTextMessage(WebSocket websocket, String text) {
                    Log.d(TAG, "Received: " + text);
                    handleStompMessage(text);
                }

                @Override
                public void onDisconnected(WebSocket websocket, WebSocketFrame serverCloseFrame,
                        WebSocketFrame clientCloseFrame, boolean closedByServer) {
                    Log.d(TAG, "WebSocket disconnected (closedByServer: " + closedByServer +
                            ", serverCloseFrame: "
                            + (serverCloseFrame != null ? serverCloseFrame.getCloseCode() : "null") +
                            ", clientCloseFrame: "
                            + (clientCloseFrame != null ? clientCloseFrame.getCloseCode() : "null") + ")");
                    isConnected = false;

                    if (connectionListener != null) {
                        connectionListener.onDisconnected();
                    }

                    // Auto-reconnect if not disconnected intentionally
                    if (closedByServer) {
                        Log.d(TAG, "Connection lost by server, attempting to reconnect...");
                        scheduleReconnect();
                    } else {
                        Log.d(TAG, "Connection closed by client, not attempting to reconnect");
                    }
                }

                @Override
                public void onError(WebSocket websocket, WebSocketException cause) {
                    Log.e(TAG, "WebSocket error: " + cause.getMessage() +
                            " (Current connection state: " + isConnected + ")");
                    isConnected = false;

                    // Only schedule reconnect if we're not already trying to reconnect
                    if (reconnectAttempts == 0) {
                        Log.d(TAG, "Error triggered reconnection");
                        scheduleReconnect();
                    } else {
                        Log.w(TAG, "Already attempting reconnection, ignoring error");
                    }
                }

                @Override
                public void onConnectError(WebSocket websocket, WebSocketException exception) {
                    Log.e(TAG, "WebSocket connect error: " + exception.getMessage() +
                            " (Reconnect attempts: " + reconnectAttempts + ")");

                    // Only schedule reconnect if we're not already trying to reconnect
                    if (reconnectAttempts == 0) {
                        Log.d(TAG, "Connect error triggered reconnection");
                        scheduleReconnect();
                    } else {
                        Log.w(TAG, "Already attempting reconnection, ignoring connect error");
                    }
                }
            });

            webSocket.connect();

        } catch (IOException | WebSocketException e) {
            Log.e(TAG, "Failed to connect", e);
            scheduleReconnect();
        }
    }

    public void connect() {
        Log.d(TAG, "Starting WebSocket connection to: " + WS_URL +
                " (Current state: isConnected=" + isConnected +
                ", reconnectAttempts=" + reconnectAttempts + ")");

        // Prevent multiple simultaneous connection attempts
        if (isConnected && webSocket != null && webSocket.isOpen()) {
            Log.w(TAG, "WebSocket already connected, ignoring connect request");
            return;
        }

        reconnectAttempts = 0; // Reset counter for manual connection
        executorService.execute(() -> connectToWebSocket());
    }

    public void disconnect() {
        Log.d(TAG, "Disconnecting WebSocket");
        reconnectAttempts = MAX_RECONNECT_ATTEMPTS; // Prevent auto-reconnection

        if (webSocket != null && webSocket.isOpen()) {
            // Send STOMP DISCONNECT frame
            sendStompDisconnect();
            webSocket.disconnect();
        }
        isConnected = false;

        // Clear subscriptions on explicit disconnect
        clearSubscriptions();
    }

    public void forceReconnect() {
        Log.d(TAG, "Force reconnecting WebSocket (Current state: isConnected=" + isConnected +
                ", isOpen=" + (webSocket != null ? webSocket.isOpen() : "null") + ")");

        // Only force reconnect if we're actually disconnected or having issues
        if (isConnected && webSocket != null && webSocket.isOpen()) {
            Log.w(TAG, "WebSocket appears to be working fine, skipping force reconnect");
            return;
        }

        disconnect();
        // Reset reconnect attempts and connect
        reconnectAttempts = 0;
        connect();
    }

    public void shutdown() {
        disconnect();
        if (executorService != null && !executorService.isShutdown()) {
            executorService.shutdown();
        }
    }

    private void sendStompConnect() {
        // Use standard STOMP CONNECT frame - authentication handled by WebSocket header
        String connectFrame = "CONNECT\n" +
                "accept-version:1.0,1.1,2.0\n" +
                "heart-beat:10000,10000\n" +
                "\n" +
                "\0";

        Log.d(TAG, "Sending STOMP CONNECT frame");
        if (webSocket != null && webSocket.isOpen()) {
            webSocket.sendText(connectFrame);
        }
    }

    private void sendStompDisconnect() {
        String disconnectFrame = "DISCONNECT\n" +
                "receipt:disconnect-" + UUID.randomUUID().toString() + "\n" +
                "\n" +
                "\0";

        if (webSocket != null && webSocket.isOpen()) {
            webSocket.sendText(disconnectFrame);
        }
    }

    public void subscribeToConversation(int conversationId) {
        String destination = "/topic/conversation." + conversationId;
        String subscriptionId = "sub-" + conversationId;

        Log.d(TAG, "Request to subscribe to conversation: " + conversationId +
                " (current: " + currentConversationId + ")");

        // If already subscribed to this exact conversation, do nothing
        if (currentConversationId == conversationId && subscriptions.containsKey(destination)) {
            Log.d(TAG, "Already subscribed to conversation: " + conversationId);
            logSubscriptionStatus();
            return;
        }

        // Unsubscribe from current conversation if different
        if (currentConversationId != -1 && currentConversationId != conversationId) {
            Log.d(TAG, "Unsubscribing from previous conversation: " + currentConversationId);
            unsubscribeFromConversation(currentConversationId);
        }

        // Subscribe to new conversation
        if (webSocket != null && webSocket.isOpen()) {
            String subscribeFrame = "SUBSCRIBE\n" +
                    "id:" + subscriptionId + "\n" +
                    "destination:" + destination + "\n" +
                    "\n" +
                    "\0";

            webSocket.sendText(subscribeFrame);
            subscriptions.put(destination, subscriptionId);
            currentConversationId = conversationId;
            Log.d(TAG, "Successfully subscribed to conversation: " + conversationId +
                    " with destination: " + destination);
            logSubscriptionStatus();
        } else {
            Log.w(TAG, "Cannot subscribe to conversation " + conversationId +
                    " - WebSocket not connected (isOpen: " +
                    (webSocket != null ? webSocket.isOpen() : "null") + ")");
        }
    }

    public void unsubscribeFromConversation(int conversationId) {
        String destination = "/topic/conversation." + conversationId;
        String subscriptionId = subscriptions.get(destination);

        Log.d(TAG, "Request to unsubscribe from conversation: " + conversationId +
                " (current: " + currentConversationId + ")");

        if (subscriptionId != null) {
            if (webSocket != null && webSocket.isOpen()) {
                String unsubscribeFrame = "UNSUBSCRIBE\n" +
                        "id:" + subscriptionId + "\n" +
                        "\n" +
                        "\0";

                webSocket.sendText(unsubscribeFrame);
                Log.d(TAG, "Sent unsubscribe frame for conversation: " + conversationId);
            } else {
                Log.w(TAG, "Cannot send unsubscribe frame for conversation " + conversationId
                        + " - WebSocket not connected");
            }

            subscriptions.remove(destination);

            // Reset current conversation if unsubscribing from it
            if (currentConversationId == conversationId) {
                currentConversationId = -1;
                Log.d(TAG, "Reset current conversation tracking");
            }

            Log.d(TAG, "Removed subscription for conversation: " + conversationId + " from local cache");
            logSubscriptionStatus();
        } else {
            Log.w(TAG, "No subscription found for conversation: " + conversationId);
        }
    }

    public void subscribeToNewConversations() {
        String destination = "/topic/new-conversation";
        String subscriptionId = "sub-new-conversations";

        String subscribeFrame = "SUBSCRIBE\n" +
                "id:" + subscriptionId + "\n" +
                "destination:" + destination + "\n" +
                "\n" +
                "\0";

        if (webSocket != null && webSocket.isOpen()) {
            webSocket.sendText(subscribeFrame);
            subscriptions.put(destination, subscriptionId);
            Log.d(TAG, "Subscribed to new conversations");
        }
    }

    public void subscribeToConversationUpdates() {
        String destination = "/topic/conversation-done";
        String subscriptionId = "sub-conversation-updates";

        String subscribeFrame = "SUBSCRIBE\n" +
                "id:" + subscriptionId + "\n" +
                "destination:" + destination + "\n" +
                "\n" +
                "\0";

        if (webSocket != null && webSocket.isOpen()) {
            webSocket.sendText(subscribeFrame);
            subscriptions.put(destination, subscriptionId);
            Log.d(TAG, "Subscribed to conversation status updates");
        } else {
            Log.w(TAG, "Cannot subscribe to conversation updates - WebSocket not connected");
        }
    }

    public void sendMessage(ChatMessage message) {
        String destination = "/app/chat.sendMessage";
        String messageBody = gson.toJson(message);

        String sendFrame = "SEND\n" +
                "destination:" + destination + "\n" +
                "content-type:application/json\n" +
                "content-length:" + messageBody.length() + "\n" +
                "\n" +
                messageBody + "\0";

        if (webSocket != null && webSocket.isOpen()) {
            webSocket.sendText(sendFrame);
            Log.d(TAG, "Message sent: " + messageBody);
        }
    }

    private void handleStompMessage(String message) {
        try {
            Log.d(TAG, "Handling STOMP message: " + message);
            String[] lines = message.split("\n");
            String command = lines[0];
            Log.d(TAG, "STOMP command: " + command);

            switch (command) {
                case "CONNECTED":
                    handleConnectedFrame(message);
                    break;
                case "MESSAGE":
                    handleMessageFrame(message);
                    break;
                case "ERROR":
                    handleErrorFrame(message);
                    break;
                case "RECEIPT":
                    handleReceiptFrame(message);
                    break;
                default:
                    Log.d(TAG, "Unknown STOMP command: " + command);
            }
        } catch (Exception e) {
            Log.e(TAG, "Error handling STOMP message", e);
        }
    }

    private void handleConnectedFrame(String frame) {
        // Extract session ID if needed
        Log.d(TAG, "STOMP connected");
    }

    private void handleMessageFrame(String frame) {
        try {
            Log.d(TAG, "Handling MESSAGE frame: " + frame);

            // Parse the STOMP message
            String[] parts = frame.split("\n\n", 2);
            if (parts.length < 2) {
                Log.w(TAG, "Invalid message frame format - no body separator found");
                return;
            }

            String headers = parts[0];
            String body = parts[1].replace("\0", "");

            Log.d(TAG, "Message headers: " + headers);
            Log.d(TAG, "Message body: " + body);

            // Extract destination from headers
            String destination = null;
            for (String line : headers.split("\n")) {
                if (line.startsWith("destination:")) {
                    destination = line.substring("destination:".length());
                    break;
                }
            }

            Log.d(TAG, "Message destination: " + destination);

            if (destination != null) {
                if (destination.startsWith("/topic/conversation.")) {
                    Log.d(TAG, "Processing chat message for conversation");
                    try {
                        // Try to parse as WebSocket message format first
                        WebSocketMessage wsMessage = gson.fromJson(body, WebSocketMessage.class);
                        Log.d(TAG, "Parsed WebSocket message: " + gson.toJson(wsMessage));

                        // Convert to MessageResponse.Message format for UI consistency
                        MessageResponse.Message chatMessage = wsMessage.toMessageResponse();
                        Log.d(TAG, "Converted to UI message format: " + gson.toJson(chatMessage));

                        if (chatMessageListener != null) {
                            Log.d(TAG, "Calling chat message listener");
                            chatMessageListener.onMessageReceived(chatMessage);
                        } else {
                            Log.w(TAG, "Chat message listener is null");
                        }
                    } catch (Exception e) {
                        Log.e(TAG, "Failed to parse as WebSocket message, trying legacy format", e);
                        try {
                            // Fallback to legacy MessageResponse.Message format
                            MessageResponse.Message chatMessage = gson.fromJson(body, MessageResponse.Message.class);
                            Log.d(TAG, "Parsed legacy chat message: " + gson.toJson(chatMessage));
                            if (chatMessageListener != null) {
                                Log.d(TAG, "Calling chat message listener with legacy format");
                                chatMessageListener.onMessageReceived(chatMessage);
                            } else {
                                Log.w(TAG, "Chat message listener is null");
                            }
                        } catch (Exception e2) {
                            Log.e(TAG, "Failed to parse message in any format", e2);
                        }
                    }
                } else if (destination.equals("/topic/new-conversation")) {
                    Log.d(TAG, "Processing new conversation");
                    // Handle new conversation
                    ConversationModel conversation = gson.fromJson(body, ConversationModel.class);
                    if (conversationListener != null) {
                        conversationListener.onNewConversation(conversation);
                    } else {
                        Log.w(TAG, "Conversation listener is null");
                    }
                } else if (destination.equals("/topic/conversation-done")) {
                    Log.d(TAG, "Processing conversation status update");
                    // Handle conversation status update (when conversation is marked as done)
                    try {
                        ConversationUpdateResponse response = gson.fromJson(body, ConversationUpdateResponse.class);
                        Log.d(TAG, "Parsed conversation update response: " + response.toString());

                        if (response.isSuccess() && response.getData() != null) {
                            Log.d(TAG, "Received " + response.getData().size() + " updated conversations");
                            if (conversationUpdateListener != null) {
                                Log.d(TAG, "Calling conversation update listener with conversation list");
                                conversationUpdateListener.onConversationsUpdated(response.getData());
                            } else {
                                Log.w(TAG, "Conversation update listener is null");
                            }
                        } else {
                            Log.w(TAG, "Conversation update response failed or has no data: success=" +
                                    response.isSuccess() + ", data=" + response.getData());
                        }
                    } catch (Exception e) {
                        Log.e(TAG, "Failed to parse conversation update response", e);
                    }
                } else {
                    Log.w(TAG, "Unknown destination: " + destination);
                }
            } else {
                Log.w(TAG, "No destination found in message headers");
            }
        } catch (Exception e) {
            Log.e(TAG, "Error handling message frame", e);
        }
    }

    private void handleErrorFrame(String frame) {
        Log.e(TAG, "STOMP error: " + frame);
    }

    private void handleReceiptFrame(String frame) {
        Log.d(TAG, "STOMP receipt: " + frame);
    }

    public boolean isConnected() {
        return isConnected && webSocket != null && webSocket.isOpen();
    }

    public int getReconnectAttempts() {
        return reconnectAttempts;
    }

    public String getWebSocketUrl() {
        return WS_URL;
    }

    private void clearSubscriptions() {
        Log.d(TAG, "Clearing subscription cache (count: " + subscriptions.size() +
                ", current conversation: " + currentConversationId + ")");
        subscriptions.clear();
        currentConversationId = -1; // Reset current conversation tracking
    }

    public boolean isSubscribedToConversation(int conversationId) {
        String destination = "/topic/conversation." + conversationId;
        return subscriptions.containsKey(destination);
    }

    public void logSubscriptionStatus() {
        Log.d(TAG, "Current subscriptions count: " + subscriptions.size() +
                ", active conversation: " + currentConversationId);
        for (Map.Entry<String, String> entry : subscriptions.entrySet()) {
            Log.d(TAG, "Subscription: " + entry.getKey() + " -> " + entry.getValue());
        }
    }

    public int getCurrentConversationId() {
        return currentConversationId;
    }

    // Method to force unsubscribe from all conversations
    public void unsubscribeFromAllConversations() {
        Log.d(TAG, "Unsubscribing from all conversations");

        // Create a copy of the keys to avoid concurrent modification
        Map<String, String> conversationSubs = new HashMap<>();
        for (Map.Entry<String, String> entry : subscriptions.entrySet()) {
            if (entry.getKey().startsWith("/topic/conversation.")) {
                conversationSubs.put(entry.getKey(), entry.getValue());
            }
        }

        // Unsubscribe from each conversation
        for (Map.Entry<String, String> entry : conversationSubs.entrySet()) {
            String destination = entry.getKey();
            String subscriptionId = entry.getValue();

            if (webSocket != null && webSocket.isOpen()) {
                String unsubscribeFrame = "UNSUBSCRIBE\n" +
                        "id:" + subscriptionId + "\n" +
                        "\n" +
                        "\0";
                webSocket.sendText(unsubscribeFrame);
                Log.d(TAG, "Sent unsubscribe frame for destination: " + destination);
            }

            subscriptions.remove(destination);
        }

        currentConversationId = -1;
        logSubscriptionStatus();
    }

    // Setters for listeners
    public void setChatMessageListener(ChatMessageListener listener) {
        this.chatMessageListener = listener;
    }

    public void setConversationListener(ConversationListener listener) {
        this.conversationListener = listener;
    }

    public void setConversationUpdateListener(ConversationUpdateListener listener) {
        this.conversationUpdateListener = listener;
    }

    public void setConnectionListener(ConnectionListener listener) {
        this.connectionListener = listener;
    }

    /**
     * Custom Gson adapter for Instant serialization/deserialization
     * Handles various ISO-8601 formats including microseconds
     */
    private static class InstantAdapter implements JsonSerializer<Instant>, JsonDeserializer<Instant> {
        @Override
        public JsonElement serialize(Instant src, Type typeOfSrc, JsonSerializationContext context) {
            return context.serialize(src.toString());
        }

        @Override
        public Instant deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
                throws JsonParseException {
            try {
                String instantString = json.getAsString();

                // Handle microseconds by truncating to milliseconds if needed
                if (instantString.contains(".") && instantString.endsWith("Z")) {
                    String[] parts = instantString.split("\\.");
                    if (parts.length == 2) {
                        String secondsPart = parts[0];
                        String fractionPart = parts[1].substring(0, parts[1].length() - 1); // Remove 'Z'

                        // Truncate or pad fractional seconds to 9 digits (nanoseconds)
                        if (fractionPart.length() > 9) {
                            fractionPart = fractionPart.substring(0, 9);
                        } else {
                            while (fractionPart.length() < 9) {
                                fractionPart += "0";
                            }
                        }

                        instantString = secondsPart + "." + fractionPart + "Z";
                    }
                }

                return Instant.parse(instantString);
            } catch (Exception e) {
                throw new JsonParseException("Failed to parse Instant: " + json.getAsString(), e);
            }
        }
    }
}
