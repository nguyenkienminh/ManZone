package com.example.man_zone.Activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.man_zone.Adapter.ChatMessageAdapter;
import com.example.man_zone.R;
import com.example.man_zone.Utils.PrefsHelper;
import com.example.man_zone.ViewModel.ChatViewModel;
import com.example.man_zone.databinding.ActivityChatRoomBinding;

public class ChatRoomActivity extends BaseActivity {
    private ActivityChatRoomBinding binding;
    private ChatMessageAdapter messageAdapter;
    private ChatViewModel chatViewModel;
    private int conversationId;
    private String conversationTitle;
    private boolean isConversationDone;
    private long currentUserId;
    private Uri selectedImageUri;

    private ActivityResultLauncher<Intent> imagePickerLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityChatRoomBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        initData();
        initViewModel();
        initViews();
        setupRecyclerView();
        observeViewModel();
        setupImagePicker();

        // Load messages and connect to WebSocket
        chatViewModel.loadMessages(conversationId, 0);

        // Only connect WebSocket if not already connected
        if (!chatViewModel.isWebSocketConnected()) {
            chatViewModel.connectWebSocket();
        }
        chatViewModel.subscribeToConversation(conversationId);
        chatViewModel.subscribeToConversationUpdates();
    }

    private void initData() {
        Intent intent = getIntent();
        conversationId = intent.getIntExtra("conversation_id", -1);
        conversationTitle = intent.getStringExtra("conversation_title");
        isConversationDone = intent.getBooleanExtra("conversation_done", false);

        Log.d("CHAT_ROOM_DEBUG", "Conversation ID: " + conversationId + ", Title: " + conversationTitle + ", Done: "
                + isConversationDone);

        if (conversationId == -1) {
            Toast.makeText(this, "Invalid conversation", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Get current user ID from SharedPreferences
        SharedPreferences prefs = getSharedPreferences("ManZonePrefs", MODE_PRIVATE);
        currentUserId = prefs.getInt("userId", -1);

        // Debug logging
        String token = PrefsHelper.getToken(this);
        String email = PrefsHelper.getEmail(this);
        boolean isLoggedIn = PrefsHelper.isLoggedIn(this);
        Log.d("CHAT_ROOM_DEBUG", "Token present: " + (!token.isEmpty()));
        Log.d("CHAT_ROOM_DEBUG", "Email: " + email);
        Log.d("CHAT_ROOM_DEBUG", "User ID: " + currentUserId);
        Log.d("CHAT_ROOM_DEBUG", "Is logged in: " + isLoggedIn);

        if (currentUserId == -1) {
            // Try migration in case user logged in with old version
            PrefsHelper.migrateOldPrefs(this);
            currentUserId = PrefsHelper.getUserId(this);
            Log.d("CHAT_ROOM_DEBUG", "After migration - User ID: " + currentUserId);
        }

        if (currentUserId == -1) {
            Toast.makeText(this, "Please login first", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
    }

    private void initViewModel() {
        chatViewModel = new ViewModelProvider(this).get(ChatViewModel.class);
    }

    private void initViews() {
        // Set title with status indicator
        String titleText = conversationTitle != null ? conversationTitle : "Chat Support";
        if (isConversationDone) {
            titleText += " (Completed)";
        }
        binding.tvConversationTitle.setText(titleText);

        // Set click listeners
        binding.btnBack.setOnClickListener(v -> finish());
        binding.btnSend.setOnClickListener(v -> sendMessage());
        binding.btnAttach.setOnClickListener(v -> openImagePicker());

        // Disable message input if conversation is done
        if (isConversationDone) {
            setupReadOnlyMode();
        }

        // Set up swipe to refresh
        if (binding.swipeRefreshLayout != null) {
            binding.swipeRefreshLayout.setOnRefreshListener(() -> {
                chatViewModel.loadMessages(conversationId, 0);
            });
        }
    }

    private void setupRecyclerView() {
        messageAdapter = new ChatMessageAdapter(this, currentUserId);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setReverseLayout(true); // Show newest messages at bottom
        binding.recyclerViewMessages.setLayoutManager(layoutManager);
        binding.recyclerViewMessages.setAdapter(messageAdapter);
    }

    private void observeViewModel() {
        chatViewModel.getMessages().observe(this, messages -> {
            Log.d("CHAT_ROOM_ACTIVITY", "Messages received: " + (messages != null ? messages.size() : 0));
            if (messages != null) {
                messageAdapter.updateMessages(messages);
                if (binding.swipeRefreshLayout != null) {
                    binding.swipeRefreshLayout.setRefreshing(false);
                }

                // Scroll to bottom for new messages
                if (!messages.isEmpty()) {
                    binding.recyclerViewMessages.scrollToPosition(0);
                }
            }
        });

        chatViewModel.getNewMessage().observe(this, message -> {
            if (message != null) {
                Log.d("CHAT_ROOM_ACTIVITY", "Received new message: " + message.message);
                messageAdapter.addMessage(message);
                binding.recyclerViewMessages.scrollToPosition(0);
            } else {
                Log.w("CHAT_ROOM_ACTIVITY", "Received null message in observer");
            }
        });

        chatViewModel.getError().observe(this, error -> {
            if (error != null) {
                Log.e("CHAT_ROOM_ACTIVITY", "Error received: " + error);
                Toast.makeText(this, error, Toast.LENGTH_SHORT).show();
                if (binding.swipeRefreshLayout != null) {
                    binding.swipeRefreshLayout.setRefreshing(false);
                }
            }
        });

        chatViewModel.getLoading().observe(this, loading -> {
            if (loading != null && binding.progressBar != null) {
                binding.progressBar.setVisibility(loading ? View.VISIBLE : View.GONE);
            }
        });

        chatViewModel.getConversationUpdated().observe(this, conversation -> {
            if (conversation != null && conversation.getId() == conversationId) {
                Log.d("CHAT_ROOM_ACTIVITY", "Current conversation updated: " + conversation.getId() +
                        ", done: " + conversation.isDone());

                // Update conversation status in UI
                updateConversationStatus(conversation.isDone());

                // Store updated conversation data
                isConversationDone = conversation.isDone();
            }
        });
    }

    private void setupImagePicker() {
        imagePickerLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        selectedImageUri = result.getData().getData();
                        if (selectedImageUri != null) {
                            // Show selected image preview
                            binding.imagePreviewLayout.setVisibility(View.VISIBLE);
                            binding.imagePreview.setImageURI(selectedImageUri);

                            binding.btnRemoveImage.setOnClickListener(v -> {
                                selectedImageUri = null;
                                binding.imagePreviewLayout.setVisibility(View.GONE);
                            });
                        }
                    }
                });
    }

    private void openImagePicker() {
        if (isConversationDone) {
            Toast.makeText(this, "This conversation has been completed. You cannot send new messages.",
                    Toast.LENGTH_LONG).show();
            return;
        }
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        imagePickerLauncher.launch(intent);
    }

    private void sendMessage() {
        Log.d("CHAT_ROOM_ACTIVITY", "sendMessage() called");

        // Check if conversation is done
        if (isConversationDone) {
            Toast.makeText(this, "This conversation has been completed. You cannot send new messages.",
                    Toast.LENGTH_LONG).show();
            return;
        }

        String messageText = binding.etMessage.getText().toString().trim();
        String imageUrl = null;

        // Handle image upload (you'll need to implement image upload to your server)
        if (selectedImageUri != null) {
            // For now, we'll use a placeholder. In a real app, upload the image first
            imageUrl = selectedImageUri.toString(); // This should be replaced with actual upload
        }

        if (TextUtils.isEmpty(messageText) && selectedImageUri == null) {
            Toast.makeText(this, "Please enter a message or select an image", Toast.LENGTH_SHORT).show();
            return;
        }

        Log.d("CHAT_ROOM_ACTIVITY", "Sending message: " + messageText);

        // Send message via WebSocket
        chatViewModel.sendMessage(conversationId, messageText, imageUrl);

        // Clear input
        binding.etMessage.setText("");
        selectedImageUri = null;
        binding.imagePreviewLayout.setVisibility(View.GONE);

        Log.d("CHAT_ROOM_ACTIVITY", "Message sent and input cleared");
    }

    private void updateConversationStatus(boolean isDone) {
        Log.d("CHAT_ROOM_ACTIVITY", "Updating conversation status, done: " + isDone);

        if (isDone) {
            setupReadOnlyMode();
            Toast.makeText(this, "This conversation has been marked as completed",
                    Toast.LENGTH_LONG).show();
        } else {
            // If conversation is no longer done, re-enable input
            binding.tvConversationStatus.setVisibility(View.GONE);
            binding.etMessage.setEnabled(true);
            binding.etMessage.setHint("Type a message...");
            binding.etMessage.setBackgroundColor(getResources().getColor(android.R.color.white, null));
            binding.btnSend.setEnabled(true);
            binding.btnAttach.setEnabled(true);
        }
    }

    private void setupReadOnlyMode() {
        // Show status banner
        binding.tvConversationStatus.setVisibility(View.VISIBLE);

        // Disable message input
        binding.etMessage.setEnabled(false);
        binding.etMessage.setHint("This conversation has been completed");
        binding.etMessage.setBackgroundColor(getResources().getColor(android.R.color.darker_gray, null));

        // Disable send button
        binding.btnSend.setEnabled(false);
        binding.btnSend.setAlpha(0.5f);

        // Disable attach button
        binding.btnAttach.setEnabled(false);
        binding.btnAttach.setAlpha(0.5f);

        Log.d("CHAT_ROOM_ACTIVITY", "Read-only mode enabled - conversation is completed");
    }

    @Override
    protected void onDestroy() {
        Log.d("CHAT_ROOM_ACTIVITY", "onDestroy() called");
        super.onDestroy();
        if (chatViewModel != null) {
            // Only unsubscribe from this conversation, don't disconnect the entire
            // WebSocket
            // as other parts of the app might still need it
            chatViewModel.unsubscribeFromConversation(conversationId);

            // Only disconnect WebSocket if the activity is finishing (not just being
            // paused/stopped)
            if (isFinishing()) {
                Log.d("CHAT_ROOM_ACTIVITY", "Activity is finishing, disconnecting WebSocket");
                chatViewModel.disconnectWebSocket();
            } else {
                Log.d("CHAT_ROOM_ACTIVITY", "Activity not finishing, keeping WebSocket connected");
            }
        }
    }

    @Override
    protected void onPause() {
        Log.d("CHAT_ROOM_ACTIVITY", "onPause() called - keeping subscription active");
        super.onPause();
        // Don't unsubscribe on pause - let the WebSocketManager handle subscription
        // lifecycle
    }

    @Override
    protected void onResume() {
        Log.d("CHAT_ROOM_ACTIVITY", "onResume() called");
        super.onResume();
        if (chatViewModel != null) {
            Log.d("CHAT_ROOM_ACTIVITY", "Ensuring subscription to conversation: " + conversationId);
            // Only subscribe if not already subscribed to this conversation
            chatViewModel.subscribeToConversation(conversationId);

            // Reload messages to ensure we have the latest messages
            // This handles cases where messages were received while away from the
            // conversation
            Log.d("CHAT_ROOM_ACTIVITY", "Reloading messages on resume to catch up");
            chatViewModel.loadMessages(conversationId, 0);
        }
    }

    @Override
    public void finish() {
        Log.d("CHAT_ROOM_ACTIVITY", "finish() called");
        super.finish();
    }
}
