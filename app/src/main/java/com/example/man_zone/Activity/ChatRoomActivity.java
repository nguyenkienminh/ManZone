package com.example.man_zone.Activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.bumptech.glide.Glide;
import com.example.man_zone.Adapter.ChatMessageAdapter;
import com.example.man_zone.Model.ChatMessageModel;
import com.example.man_zone.R;
import com.example.man_zone.databinding.ActivityChatRoomBinding;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

public class ChatRoomActivity extends BaseActivity {
    private ActivityChatRoomBinding binding;
    private ChatMessageAdapter messageAdapter;
    private List<ChatMessageModel> messages;
    private String conversationId;
    private String conversationTitle;
    private String participantType;
    private String currentUserId;
    private Uri selectedImageUri;

    private ActivityResultLauncher<Intent> imagePickerLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityChatRoomBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        initData();
        initViews();
        setupRecyclerView();
        loadMessages();
        setupImagePicker();
    }

    private void initData() {
        // Get data from intent
        conversationId = getIntent().getStringExtra("conversation_id");
        conversationTitle = getIntent().getStringExtra("conversation_title");
        participantType = getIntent().getStringExtra("participant_type");

        // Get current user ID from SharedPreferences
        SharedPreferences sharedPreferences = getSharedPreferences("user_data", MODE_PRIVATE);
        currentUserId = sharedPreferences.getString("email", "user");

        messages = new ArrayList<>();
    }

    private void initViews() {
        binding.btnBack.setOnClickListener(v -> finish());
        binding.btnSend.setOnClickListener(v -> sendMessage());
        binding.btnAttachImage.setOnClickListener(v -> openImagePicker());
        binding.btnRemoveImage.setOnClickListener(v -> removeSelectedImage());

        // Set header text
        binding.tvChatTitle.setText(conversationTitle);
        binding.tvParticipantType.setText(getString(R.string.chat_with_participant, participantType));
    }

    private void setupRecyclerView() {
        messageAdapter = new ChatMessageAdapter(this, currentUserId);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setStackFromEnd(true); // Start from bottom
        binding.rvMessages.setLayoutManager(layoutManager);
        binding.rvMessages.setAdapter(messageAdapter);
    }

    private void setupImagePicker() {
        imagePickerLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    selectedImageUri = result.getData().getData();
                    showImagePreview();
                }
            }
        );
    }

    private void loadMessages() {
        // For demonstration, add some sample messages
        // In a real app, this would load from a database or API
        if (conversationId.equals("1")) {
            // Technical Support conversation
            messages.add(new ChatMessageModel(
                UUID.randomUUID().toString(),
                conversationId,
                "staff_1",
                "Support Agent",
                "staff",
                "Hello! Thank you for contacting technical support. How can I assist you today?",
                null,
                new Date(System.currentTimeMillis() - 3600000),
                true
            ));
        } else if (conversationId.equals("2")) {
            // Order Inquiry conversation
            messages.add(new ChatMessageModel(
                UUID.randomUUID().toString(),
                conversationId,
                "admin_1",
                "Admin",
                "admin",
                "Your order has been processed successfully and is now being prepared for shipment.",
                null,
                new Date(System.currentTimeMillis() - 7200000),
                true
            ));
        }

        messageAdapter.updateMessages(messages);
        scrollToBottom();
    }

    private void sendMessage() {
        String messageText = binding.etMessage.getText().toString().trim();

        if (TextUtils.isEmpty(messageText) && selectedImageUri == null) {
            Toast.makeText(this, "Please enter a message or select an image", Toast.LENGTH_SHORT).show();
            return;
        }

        // Create new message
        ChatMessageModel newMessage = new ChatMessageModel(
            UUID.randomUUID().toString(),
            conversationId,
            currentUserId,
            "You",
            "user",
            messageText,
            selectedImageUri != null ? selectedImageUri.toString() : null,
            new Date(),
            true
        );

        messages.add(newMessage);
        messageAdapter.addMessage(newMessage);

        // Clear input
        binding.etMessage.setText("");
        removeSelectedImage();

        scrollToBottom();

        // Simulate response from staff/admin after a short delay
        simulateResponse();
    }

    private void simulateResponse() {
        // Simulate a response from staff or admin after 2 seconds
        binding.rvMessages.postDelayed(() -> {
            String responseText = participantType.equals("staff") ?
                "Thank you for your message. We're looking into this and will get back to you shortly." :
                "Your request has been received and is being processed by our team.";

            ChatMessageModel response = new ChatMessageModel(
                UUID.randomUUID().toString(),
                conversationId,
                participantType + "_1",
                participantType.equals("staff") ? "Support Agent" : "Admin",
                participantType,
                responseText,
                null,
                new Date(),
                true
            );

            messages.add(response);
            messageAdapter.addMessage(response);
            scrollToBottom();
        }, 2000);
    }

    private void openImagePicker() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
        imagePickerLauncher.launch(intent);
    }

    private void showImagePreview() {
        if (selectedImageUri != null) {
            binding.imagePreviewLayout.setVisibility(View.VISIBLE);
            Glide.with(this)
                .load(selectedImageUri)
                .into(binding.ivImagePreview);
        }
    }

    private void removeSelectedImage() {
        selectedImageUri = null;
        binding.imagePreviewLayout.setVisibility(View.GONE);
    }

    private void scrollToBottom() {
        if (messageAdapter.getItemCount() > 0) {
            binding.rvMessages.smoothScrollToPosition(messageAdapter.getItemCount() - 1);
        }
    }
}
