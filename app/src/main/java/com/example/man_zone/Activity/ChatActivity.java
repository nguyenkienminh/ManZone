package com.example.man_zone.Activity;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.man_zone.Adapter.ChatConversationAdapter;
import com.example.man_zone.R;
import com.example.man_zone.ViewModel.ChatViewModel;
import com.example.man_zone.databinding.ActivityChatBinding;

public class ChatActivity extends BaseActivity {
    private ActivityChatBinding binding;
    private ChatConversationAdapter conversationAdapter;
    private ChatViewModel chatViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityChatBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        initViewModel();
        initViews();
        setupRecyclerView();
        observeViewModel();

        // Load conversations
        chatViewModel.loadUserConversations();

        // Connect WebSocket - subscriptions will be handled automatically when
        // connected
        chatViewModel.connectWebSocket();
    }

    private void initViewModel() {
        chatViewModel = new ViewModelProvider(this).get(ChatViewModel.class);
    }

    private void initViews() {
        binding.btnBack.setOnClickListener(v -> finish());
        binding.btnNewConversation.setOnClickListener(v -> showNewConversationDialog());

        // Set up pull to refresh if available
        if (binding.swipeRefreshLayout != null) {
            binding.swipeRefreshLayout.setOnRefreshListener(() -> {
                chatViewModel.loadUserConversations();
            });
        }
    }

    private void setupRecyclerView() {
        conversationAdapter = new ChatConversationAdapter(this);
        binding.recyclerViewConversations.setLayoutManager(new LinearLayoutManager(this));
        binding.recyclerViewConversations.setAdapter(conversationAdapter);

        conversationAdapter.setOnConversationClickListener(conversation -> {
            Log.d("CHAT_ACTIVITY", "Conversation clicked: ID=" + conversation.getId() + ", Title="
                    + conversation.getTitle() + ", Done=" + conversation.isDone());
            Intent intent = new Intent(ChatActivity.this, ChatRoomActivity.class);
            intent.putExtra("conversation_id", conversation.getId());
            intent.putExtra("conversation_title", conversation.getTitle());
            intent.putExtra("conversation_done", conversation.isDone());
            startActivity(intent);
        });
    }

    private void observeViewModel() {
        chatViewModel.getConversations().observe(this, conversations -> {
            if (conversations != null) {
                conversationAdapter.updateConversations(conversations);
                if (binding.swipeRefreshLayout != null) {
                    binding.swipeRefreshLayout.setRefreshing(false);
                }

                if (conversations.isEmpty()) {
                    if (binding.textViewEmptyState != null) {
                        binding.textViewEmptyState.setVisibility(View.VISIBLE);
                    }
                    binding.recyclerViewConversations.setVisibility(View.GONE);
                } else {
                    if (binding.textViewEmptyState != null) {
                        binding.textViewEmptyState.setVisibility(View.GONE);
                    }
                    binding.recyclerViewConversations.setVisibility(View.VISIBLE);
                }
            }
        });

        chatViewModel.getError().observe(this, error -> {
            if (error != null) {
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

        chatViewModel.getNewConversation().observe(this, conversation -> {
            if (conversation != null) {
                // Refresh conversations when a new one is created
                chatViewModel.loadUserConversations();
            }
        });

        chatViewModel.getConversationUpdated().observe(this, conversation -> {
            if (conversation != null) {
                Log.d("CHAT_ACTIVITY", "Conversation updated: " + conversation.getId() +
                        ", done: " + conversation.isDone());
                // Update the specific conversation in the adapter
                conversationAdapter.updateConversation(conversation);
            }
        });

        chatViewModel.getConnectionStatus().observe(this, connected -> {
            // You can show connection status in UI if needed
            if (connected != null && connected) {
                // Connected to WebSocket
            } else {
                // Disconnected from WebSocket
            }
        });
    }

    private void showNewConversationDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_new_conversation, null);
        builder.setView(dialogView);

        EditText etTitle = dialogView.findViewById(R.id.etConversationTitle);

        builder.setTitle("New Conversation")
                .setPositiveButton("Create", (dialog, which) -> {
                    String title = etTitle.getText().toString().trim();
                    if (title.isEmpty()) {
                        title = "New Support Request";
                    }
                    chatViewModel.createConversation(title);
                })
                .setNegativeButton("Cancel", null);

        builder.create().show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (chatViewModel != null) {
            chatViewModel.disconnectWebSocket();
        }
    }
}
