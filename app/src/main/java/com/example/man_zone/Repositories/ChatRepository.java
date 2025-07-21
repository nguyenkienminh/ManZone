package com.example.man_zone.Repositories;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.example.man_zone.ApiClient.ApiClient;
import com.example.man_zone.Interfaces.ConversationService;
import com.example.man_zone.Interfaces.MessageService;
import com.example.man_zone.Model.ConversationResponse;
import com.example.man_zone.Model.SingleConversationResponse;
import com.example.man_zone.Model.CreateConversationRequest;
import com.example.man_zone.Model.UpdateConversationRequest;
import com.example.man_zone.Model.MessageResponse;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ChatRepository {
    private static final String TAG = "ChatRepository";
    private static final String PREFS_NAME = "ManZonePrefs";
    private static final String TOKEN_KEY = "token";

    private ConversationService conversationService;
    private MessageService messageService;
    private Context context;

    public interface ConversationListCallback {
        void onSuccess(ConversationResponse response);

        void onError(String error);
    }

    public interface ConversationCallback {
        void onSuccess(SingleConversationResponse response);

        void onError(String error);
    }

    public interface MessageListCallback {
        void onSuccess(MessageResponse response);

        void onError(String error);
    }

    public interface SimpleCallback {
        void onSuccess();

        void onError(String error);
    }

    public ChatRepository(Context context) {
        this.context = context;
        this.conversationService = ApiClient.getConversationService();
        this.messageService = ApiClient.getMessageService();
    }

    private String getAuthToken() {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        String token = prefs.getString(TOKEN_KEY, "");
        return token.isEmpty() ? "" : "Bearer " + token;
    }

    public void getUserConversations(int userId, ConversationListCallback callback) {
        String token = getAuthToken();
        if (token.isEmpty()) {
            callback.onError("No authentication token found");
            return;
        }

        Call<ConversationResponse> call = conversationService.getConversationsByUserId(token, userId);
        call.enqueue(new Callback<ConversationResponse>() {
            @Override
            public void onResponse(Call<ConversationResponse> call, Response<ConversationResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onSuccess(response.body());
                } else {
                    callback.onError("Failed to get conversations: " + response.message());
                }
            }

            @Override
            public void onFailure(Call<ConversationResponse> call, Throwable t) {
                Log.e(TAG, "Error getting conversations", t);
                callback.onError("Network error: " + t.getMessage());
            }
        });
    }

    public void createConversation(String title, int userId, ConversationCallback callback) {
        String token = getAuthToken();
        if (token.isEmpty()) {
            callback.onError("No authentication token found");
            return;
        }

        CreateConversationRequest request = new CreateConversationRequest(title, userId);
        Call<SingleConversationResponse> call = conversationService.createConversation(token, request);

        call.enqueue(new Callback<SingleConversationResponse>() {
            @Override
            public void onResponse(Call<SingleConversationResponse> call,
                    Response<SingleConversationResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onSuccess(response.body());
                } else {
                    callback.onError("Failed to create conversation: " + response.message());
                }
            }

            @Override
            public void onFailure(Call<SingleConversationResponse> call, Throwable t) {
                Log.e(TAG, "Error creating conversation", t);
                callback.onError("Network error: " + t.getMessage());
            }
        });
    }

    public void updateConversation(int conversationId, String title, String status, ConversationCallback callback) {
        String token = getAuthToken();
        if (token.isEmpty()) {
            callback.onError("No authentication token found");
            return;
        }

        UpdateConversationRequest request = new UpdateConversationRequest(title, status);
        Call<SingleConversationResponse> call = conversationService.updateConversation(token, conversationId, request);

        call.enqueue(new Callback<SingleConversationResponse>() {
            @Override
            public void onResponse(Call<SingleConversationResponse> call,
                    Response<SingleConversationResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onSuccess(response.body());
                } else {
                    callback.onError("Failed to update conversation: " + response.message());
                }
            }

            @Override
            public void onFailure(Call<SingleConversationResponse> call, Throwable t) {
                Log.e(TAG, "Error updating conversation", t);
                callback.onError("Network error: " + t.getMessage());
            }
        });
    }

    public void getMessages(int conversationId, int page, int size, String sort, MessageListCallback callback) {
        String token = getAuthToken();
        if (token.isEmpty()) {
            callback.onError("No authentication token found");
            return;
        }

        Call<MessageResponse> call = messageService.getMessages(token, conversationId, page, size, sort);
        call.enqueue(new Callback<MessageResponse>() {
            @Override
            public void onResponse(Call<MessageResponse> call, Response<MessageResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    MessageResponse messageResponse = response.body();
                    Log.d(TAG, "Raw messages response received: success=" + messageResponse.success);

                    if (messageResponse.data != null && messageResponse.data.content != null) {
                        Log.d(TAG, "Messages count: " + messageResponse.data.content.size());

                        // Log raw message data to debug timestamp issue
                        for (int i = 0; i < Math.min(3, messageResponse.data.content.size()); i++) {
                            MessageResponse.Message msg = messageResponse.data.content.get(i);
                            Log.d(TAG, "RAW MESSAGE " + i + " - ID: " + msg.id +
                                    ", CreatedAt: '" + msg.createdAt + "'" +
                                    ", UpdatedAt: '" + msg.updatedAt + "'" +
                                    ", Message: '" + msg.message + "'" +
                                    ", Sender: " + msg.senderId);
                        }
                    }

                    callback.onSuccess(messageResponse);
                } else {
                    callback.onError("Failed to get messages: " + response.message());
                }
            }

            @Override
            public void onFailure(Call<MessageResponse> call, Throwable t) {
                Log.e(TAG, "Error getting messages", t);
                callback.onError("Network error: " + t.getMessage());
            }
        });
    }

    public void markConversationAsDone(int conversationId, SimpleCallback callback) {
        String token = getAuthToken();
        if (token.isEmpty()) {
            callback.onError("No authentication token found");
            return;
        }

        Call<Void> call = conversationService.markConversationAsDone(token, conversationId);
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    callback.onSuccess();
                } else {
                    callback.onError("Failed to mark conversation as done: " + response.message());
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Log.e(TAG, "Error marking conversation as done", t);
                callback.onError("Network error: " + t.getMessage());
            }
        });
    }
}
