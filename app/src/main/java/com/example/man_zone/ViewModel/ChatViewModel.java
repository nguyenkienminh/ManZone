package com.example.man_zone.ViewModel;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.example.man_zone.Model.ConversationModel;
import com.example.man_zone.Model.ConversationResponse;
import com.example.man_zone.Model.SingleConversationResponse;
import com.example.man_zone.Model.MessageResponse;
import com.example.man_zone.Model.ChatMessage;
import com.example.man_zone.Repositories.ChatRepository;
import com.example.man_zone.Utils.PrefsHelper;
import com.example.man_zone.websocket.WebSocketManager;

import java.util.List;

public class ChatViewModel extends AndroidViewModel {
    private static final String PREFS_NAME = "ManZonePrefs";
    private static final String USER_ID_KEY = "userId";

    private ChatRepository repository;
    private WebSocketManager webSocketManager;

    // LiveData for UI
    private MutableLiveData<List<ConversationModel>> conversations = new MutableLiveData<>();
    private MutableLiveData<List<MessageResponse.Message>> messages = new MutableLiveData<>();
    private MutableLiveData<String> error = new MutableLiveData<>();
    private MutableLiveData<Boolean> loading = new MutableLiveData<>();
    private MutableLiveData<MessageResponse.Message> newMessage = new MutableLiveData<>();
    private MutableLiveData<ConversationModel> newConversation = new MutableLiveData<>();
    private MutableLiveData<ConversationModel> conversationUpdated = new MutableLiveData<>();
    private MutableLiveData<Boolean> connectionStatus = new MutableLiveData<>();

    // Track subscription state to prevent duplicate calls
    private volatile boolean isSubscribing = false;

    public ChatViewModel(@NonNull Application application) {
        super(application);
        repository = new ChatRepository(application);
        webSocketManager = WebSocketManager.getInstance();

        setupWebSocketListeners();
    }

    private void setupWebSocketListeners() {
        webSocketManager.setChatMessageListener(message -> {
            Log.d("CHAT_VIEW_MODEL", "Received WebSocket message: " + message);
            if (message != null) {
                Log.d("CHAT_VIEW_MODEL", "Posting new message to LiveData");
                // Only add if this is not from the current user (to avoid duplicates)
                // Since we already add sent messages locally
                int currentUserId = getCurrentUserId();
                if (message.senderId != currentUserId) {
                    newMessage.postValue(message);
                } else {
                    Log.d("CHAT_VIEW_MODEL", "Skipping own message from WebSocket to avoid duplicate");
                }
            } else {
                Log.w("CHAT_VIEW_MODEL", "Received null message");
            }
        });

        webSocketManager.setConversationListener(conversation -> {
            newConversation.postValue(conversation);
        });

        webSocketManager.setConversationUpdateListener(conversations -> {
            Log.d("CHAT_VIEW_MODEL", "Received conversation updates: " + conversations.size() + " conversations");
            for (ConversationModel conversation : conversations) {
                Log.d("CHAT_VIEW_MODEL", "Updated conversation: " + conversation.getId() +
                        ", done: " + conversation.isDone());
                conversationUpdated.postValue(conversation);
            }
        });

        webSocketManager.setConnectionListener(new WebSocketManager.ConnectionListener() {
            @Override
            public void onConnected() {
                connectionStatus.postValue(true);
                // Auto-resubscribe to necessary topics when connected
                Log.d("CHAT_VIEW_MODEL", "WebSocket connected, resubscribing to topics");
                webSocketManager.subscribeToNewConversations();
                webSocketManager.subscribeToConversationUpdates();
            }

            @Override
            public void onDisconnected() {
                connectionStatus.postValue(false);
            }

            @Override
            public void onError(Exception error) {
                ChatViewModel.this.error.postValue("Connection error: " + error.getMessage());
                connectionStatus.postValue(false);
            }
        });
    }

    public void connectWebSocket() {
        webSocketManager.setContext(getApplication().getApplicationContext());
        webSocketManager.connect();
    }

    public void disconnectWebSocket() {
        webSocketManager.disconnect();
    }

    public void subscribeToConversation(int conversationId) {
        Log.d("CHAT_VIEW_MODEL", "Attempting to subscribe to conversation: " + conversationId +
                " (isSubscribing: " + isSubscribing + ", current: " + webSocketManager.getCurrentConversationId()
                + ")");

        // Prevent multiple rapid subscription calls
        if (isSubscribing) {
            Log.w("CHAT_VIEW_MODEL", "Subscription already in progress, ignoring request");
            return;
        }

        // If already subscribed to this conversation, no need to subscribe again
        if (webSocketManager.getCurrentConversationId() == conversationId) {
            Log.d("CHAT_VIEW_MODEL", "Already subscribed to conversation: " + conversationId);
            return;
        }

        isSubscribing = true;

        // Ensure WebSocket is connected before subscribing
        if (!webSocketManager.isConnected()) {
            Log.w("CHAT_VIEW_MODEL", "WebSocket not connected, attempting connection before subscription");

            // Don't force reconnect if already attempting to reconnect
            if (webSocketManager.getReconnectAttempts() > 0) {
                Log.d("CHAT_VIEW_MODEL", "Already attempting reconnection, waiting for completion");
                isSubscribing = false;
                return;
            }

            // Start connection
            webSocketManager.connect();

            // Set up a temporary connection listener to subscribe once connected
            webSocketManager.setConnectionListener(new WebSocketManager.ConnectionListener() {
                @Override
                public void onConnected() {
                    Log.d("CHAT_VIEW_MODEL",
                            "WebSocket connected, now subscribing to conversation: " + conversationId);
                    webSocketManager.subscribeToConversation(conversationId);
                    isSubscribing = false; // Reset flag
                    // Restore original connection listener
                    setupWebSocketListeners();
                }

                @Override
                public void onDisconnected() {
                    Log.d("CHAT_VIEW_MODEL", "WebSocket disconnected during connection attempt");
                    isSubscribing = false; // Reset flag
                    // Restore original connection listener
                    setupWebSocketListeners();
                }

                @Override
                public void onError(Exception error) {
                    Log.e("CHAT_VIEW_MODEL", "WebSocket connection error during subscription", error);
                    isSubscribing = false; // Reset flag
                    // Restore original connection listener
                    setupWebSocketListeners();
                }
            });
        } else {
            Log.d("CHAT_VIEW_MODEL", "WebSocket already connected, subscribing to conversation: " + conversationId);
            webSocketManager.subscribeToConversation(conversationId);
            isSubscribing = false; // Reset flag
        }
    }

    public void unsubscribeFromConversation(int conversationId) {
        Log.d("CHAT_VIEW_MODEL", "Unsubscribing from conversation: " + conversationId);
        webSocketManager.unsubscribeFromConversation(conversationId);
    }

    public void subscribeToNewConversations() {
        webSocketManager.subscribeToNewConversations();
    }

    public void subscribeToConversationUpdates() {
        Log.d("CHAT_VIEW_MODEL", "Subscribing to conversation status updates");
        webSocketManager.subscribeToConversationUpdates();
    }

    public boolean isWebSocketConnected() {
        return webSocketManager.isConnected();
    }

    public void loadUserConversations() {
        loading.setValue(true);
        int userId = getCurrentUserId();

        if (userId == -1) {
            error.setValue("User not logged in");
            loading.setValue(false);
            return;
        }

        repository.getUserConversations(userId, new ChatRepository.ConversationListCallback() {
            @Override
            public void onSuccess(ConversationResponse response) {
                loading.setValue(false);
                if (response.isSuccess()) {
                    conversations.setValue(response.getData());
                } else {
                    error.setValue(response.getMessage());
                }
            }

            @Override
            public void onError(String errorMsg) {
                loading.setValue(false);
                error.setValue(errorMsg);
            }
        });
    }

    public void createConversation(String title) {
        loading.setValue(true);
        int userId = getCurrentUserId();

        if (userId == -1) {
            error.setValue("User not logged in");
            loading.setValue(false);
            return;
        }

        repository.createConversation(title, userId, new ChatRepository.ConversationCallback() {
            @Override
            public void onSuccess(SingleConversationResponse response) {
                loading.setValue(false);
                if (response.isSuccess()) {
                    // Refresh conversations list
                    loadUserConversations();
                } else {
                    error.setValue(response.getMessage());
                }
            }

            @Override
            public void onError(String errorMsg) {
                loading.setValue(false);
                error.setValue(errorMsg);
            }
        });
    }

    public void loadMessages(int conversationId, int page) {
        loading.setValue(true);

        repository.getMessages(conversationId, page, 20, "DESC", new ChatRepository.MessageListCallback() {
            @Override
            public void onSuccess(MessageResponse response) {
                loading.setValue(false);
                if (response.success) {
                    Log.d("CHAT_VIEW_MODEL", "Loaded messages count: " +
                            (response.data.content != null ? response.data.content.size() : 0));

                    // Log each message details for debugging
                    if (response.data.content != null) {
                        for (MessageResponse.Message msg : response.data.content) {
                            Log.d("CHAT_VIEW_MODEL", "Message ID: " + msg.id +
                                    ", Text: " + msg.message +
                                    ", CreatedAt: " + msg.createdAt +
                                    ", SenderId: " + msg.senderId +
                                    ", SenderEmail: " + msg.senderEmail);
                        }
                    }

                    messages.setValue(response.data.content);
                } else {
                    error.setValue(response.message);
                }
            }

            @Override
            public void onError(String errorMsg) {
                loading.setValue(false);
                error.setValue(errorMsg);
            }
        });
    }

    public void sendMessage(int conversationId, String messageText, String imageUrl) {
        int userId = getCurrentUserId();

        if (userId == -1) {
            error.setValue("User not logged in");
            return;
        }

        String type = (imageUrl != null && !imageUrl.isEmpty()) ? "IMAGE" : "TEXT";
        ChatMessage message = new ChatMessage(conversationId, userId, messageText, imageUrl, type);

        // Send via WebSocket
        webSocketManager.sendMessage(message);

        // Immediately add to local UI so user sees their message right away
        String email = PrefsHelper.getEmail(getApplication());

        MessageResponse.Message localMessage = new MessageResponse.Message();
        localMessage.id = System.currentTimeMillis(); // Temporary ID
        localMessage.senderId = userId;
        localMessage.senderEmail = email;
        localMessage.message = messageText;
        localMessage.imageUrl = imageUrl;
        localMessage.type = type;

        // Create timestamp that matches the format the API uses and the adapter expects
        // Use a simpler format without 'Z' suffix to avoid timezone confusion
        java.text.SimpleDateFormat simpleFormat = new java.text.SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss",
                java.util.Locale.getDefault());
        localMessage.createdAt = simpleFormat.format(new java.util.Date());
        localMessage.updatedAt = localMessage.createdAt;

        Log.d("CHAT_VIEW_MODEL", "Created local message timestamp: " + localMessage.createdAt);

        // Add to UI immediately
        newMessage.postValue(localMessage);

        Log.d("CHAT_VIEW_MODEL", "Added sent message to local UI: " + messageText);
    }

    public void markConversationAsDone(int conversationId) {
        repository.markConversationAsDone(conversationId, new ChatRepository.SimpleCallback() {
            @Override
            public void onSuccess() {
                // Refresh conversations list
                loadUserConversations();
            }

            @Override
            public void onError(String errorMsg) {
                error.setValue(errorMsg);
            }
        });
    }

    private int getCurrentUserId() {
        SharedPreferences prefs = getApplication().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        return prefs.getInt(USER_ID_KEY, -1);
    }

    // Getters for LiveData
    public MutableLiveData<List<ConversationModel>> getConversations() {
        return conversations;
    }

    public MutableLiveData<List<MessageResponse.Message>> getMessages() {
        return messages;
    }

    public MutableLiveData<String> getError() {
        return error;
    }

    public MutableLiveData<Boolean> getLoading() {
        return loading;
    }

    public MutableLiveData<MessageResponse.Message> getNewMessage() {
        return newMessage;
    }

    public MutableLiveData<ConversationModel> getNewConversation() {
        return newConversation;
    }

    public MutableLiveData<ConversationModel> getConversationUpdated() {
        return conversationUpdated;
    }

    public MutableLiveData<Boolean> getConnectionStatus() {
        return connectionStatus;
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        webSocketManager.disconnect();
    }
}
