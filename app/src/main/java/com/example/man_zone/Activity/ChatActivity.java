package com.example.man_zone.Activity;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.man_zone.Adapter.ChatConversationAdapter;
import com.example.man_zone.Model.ChatConversationModel;
import com.example.man_zone.R;
import com.example.man_zone.databinding.ActivityChatBinding;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

public class ChatActivity extends BaseActivity {
    private ActivityChatBinding binding;
    private ChatConversationAdapter conversationAdapter;
    private List<ChatConversationModel> conversations;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityChatBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        initViews();
        initConversations();
        setupRecyclerView();
        loadConversations();
    }

    private void initViews() {
        binding.btnBack.setOnClickListener(v -> finish());
        binding.btnNewConversation.setOnClickListener(v -> showNewConversationDialog());
    }

    private void initConversations() {
        conversations = new ArrayList<>();
        conversationAdapter = new ChatConversationAdapter(this);
    }

    private void setupRecyclerView() {
        binding.rvConversations.setLayoutManager(new LinearLayoutManager(this));
        binding.rvConversations.setAdapter(conversationAdapter);
    }

    private void loadConversations() {
        // For now, we'll use mock data. In a real app, this would load from a database or API
        conversations.clear();

        // Add some sample conversations for demonstration
        conversations.add(new ChatConversationModel(
            "1",
            "Technical Support",
            "Thank you for contacting us. How can we help?",
            new Date(System.currentTimeMillis() - 3600000), // 1 hour ago
            "staff",
            false
        ));

        conversations.add(new ChatConversationModel(
            "2",
            "Order Inquiry",
            "Your order has been processed successfully.",
            new Date(System.currentTimeMillis() - 7200000), // 2 hours ago
            "admin",
            true
        ));

        conversationAdapter.updateConversations(conversations);
        updateEmptyState();
    }

    private void updateEmptyState() {
        if (conversations.isEmpty()) {
            binding.emptyStateLayout.setVisibility(View.VISIBLE);
            binding.rvConversations.setVisibility(View.GONE);
        } else {
            binding.emptyStateLayout.setVisibility(View.GONE);
            binding.rvConversations.setVisibility(View.VISIBLE);
        }
    }

    private void showNewConversationDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("New Conversation");

        View dialogView = getLayoutInflater().inflate(R.layout.dialog_new_conversation, null);
        EditText etTitle = dialogView.findViewById(R.id.etConversationTitle);

        builder.setView(dialogView);
        builder.setPositiveButton("Create", (dialog, which) -> {
            String title = etTitle.getText().toString().trim();
            if (!title.isEmpty()) {
                createNewConversation(title);
            }
        });
        builder.setNegativeButton("Cancel", null);

        builder.show();
    }

    private void createNewConversation(String title) {
        // Get current user email from SharedPreferences
        SharedPreferences sharedPreferences = getSharedPreferences("user_data", MODE_PRIVATE);
        String userEmail = sharedPreferences.getString("email", "user");

        // Create new conversation
        String conversationId = UUID.randomUUID().toString();
        ChatConversationModel newConversation = new ChatConversationModel(
            conversationId,
            title,
            "Conversation started",
            new Date(),
            "staff", // Default to staff, user can choose in the chat room
            false
        );

        conversations.add(0, newConversation); // Add to top of list
        conversationAdapter.updateConversations(conversations);
        updateEmptyState();

        // Navigate to chat room
        Intent intent = new Intent(this, ChatRoomActivity.class);
        intent.putExtra("conversation_id", conversationId);
        intent.putExtra("conversation_title", title);
        intent.putExtra("participant_type", "staff");
        startActivity(intent);
    }
}
