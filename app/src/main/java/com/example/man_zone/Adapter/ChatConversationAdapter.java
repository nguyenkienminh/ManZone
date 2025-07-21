package com.example.man_zone.Adapter;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.man_zone.Activity.ChatRoomActivity;
import com.example.man_zone.Model.ConversationModel;
import com.example.man_zone.R;

import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class ChatConversationAdapter extends RecyclerView.Adapter<ChatConversationAdapter.ViewHolder> {
    private List<ConversationModel> conversations;
    private Context context;
    private OnConversationClickListener onConversationClickListener;

    public interface OnConversationClickListener {
        void onConversationClick(ConversationModel conversation);
    }

    public ChatConversationAdapter(Context context) {
        this.context = context;
        this.conversations = new ArrayList<>();
    }

    public void setOnConversationClickListener(OnConversationClickListener listener) {
        this.onConversationClickListener = listener;
        Log.d("CONVERSATION_ADAPTER", "Click listener set: " + (listener != null));
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_chat_conversation, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ConversationModel conversation = conversations.get(position);
        holder.bind(conversation);
    }

    @Override
    public int getItemCount() {
        return conversations.size();
    }

    public void updateConversations(List<ConversationModel> newConversations) {
        Log.d("CONVERSATION_ADAPTER",
                "Updating conversations. Count: " + (newConversations != null ? newConversations.size() : 0));
        this.conversations.clear();
        if (newConversations != null) {
            this.conversations.addAll(newConversations);
            for (int i = 0; i < newConversations.size(); i++) {
                ConversationModel conv = newConversations.get(i);
                Log.d("CONVERSATION_ADAPTER",
                        "Conversation " + i + ": ID=" + conv.getId() + ", Title=" + conv.getTitle());
            }
        }
        notifyDataSetChanged();
    }

    public void updateConversation(ConversationModel updatedConversation) {
        Log.d("CONVERSATION_ADAPTER", "Updating single conversation: " + updatedConversation.getId() +
                ", done: " + updatedConversation.isDone());

        for (int i = 0; i < conversations.size(); i++) {
            if (conversations.get(i).getId() == updatedConversation.getId()) {
                conversations.set(i, updatedConversation);
                notifyItemChanged(i);
                Log.d("CONVERSATION_ADAPTER", "Updated conversation at position: " + i);
                return;
            }
        }

        Log.w("CONVERSATION_ADAPTER", "Conversation not found in list: " + updatedConversation.getId());
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView tvTitle;
        private TextView tvLastMessage;
        private TextView tvTime;
        private TextView tvStatus;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            Log.d("CONVERSATION_ADAPTER", "Creating ViewHolder");
            tvTitle = itemView.findViewById(R.id.tv_conversation_title);
            tvLastMessage = itemView.findViewById(R.id.tv_last_message);
            tvTime = itemView.findViewById(R.id.tv_time);
            tvStatus = itemView.findViewById(R.id.tv_status);

            itemView.setOnClickListener(v -> {
                int position = getAdapterPosition();
                Log.d("CONVERSATION_ADAPTER", "Item clicked at position: " + position);
                if (position != RecyclerView.NO_POSITION && onConversationClickListener != null) {
                    ConversationModel clickedConversation = conversations.get(position);
                    Log.d("CONVERSATION_ADAPTER",
                            "Calling click listener for conversation: " + clickedConversation.getId());
                    onConversationClickListener.onConversationClick(clickedConversation);
                } else {
                    Log.w("CONVERSATION_ADAPTER", "Click ignored - position: " + position + ", listener: "
                            + (onConversationClickListener != null));
                }
            });
            Log.d("CONVERSATION_ADAPTER", "ViewHolder click listener attached");
        }

        public void bind(ConversationModel conversation) {
            tvTitle.setText(conversation.getTitle() != null ? conversation.getTitle() : "Chat Support");

            // Format creation time
            if (conversation.getCreatedAt() != null) {
                try {
                    // Convert Instant to LocalDateTime for formatting
                    LocalDateTime localDateTime = LocalDateTime.ofInstant(conversation.getCreatedAt(),
                            ZoneId.systemDefault());
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMM dd, HH:mm");
                    tvTime.setText(localDateTime.format(formatter));
                } catch (Exception e) {
                    // Fallback: try to parse as Date if it's actually a Date object
                    try {
                        SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, HH:mm", Locale.getDefault());
                        tvTime.setText(sdf.format(Date.from(conversation.getCreatedAt())));
                    } catch (Exception e2) {
                        Log.e("CONVERSATION_ADAPTER", "Failed to format date: " + e2.getMessage());
                        tvTime.setText("");
                    }
                }
            } else {
                tvTime.setText("");
            }

            // Show status with better visual indicators
            if (conversation.isDone()) {
                tvStatus.setText("DONE");
                tvStatus.setVisibility(View.VISIBLE);
                tvStatus.setBackgroundResource(R.drawable.grey_bg_circle);
                tvStatus.setTextColor(itemView.getContext().getResources().getColor(android.R.color.white));

                // Make the entire item appear more subdued
                itemView.setAlpha(0.7f);
                tvTitle.setTextColor(itemView.getContext().getResources().getColor(android.R.color.darker_gray));
                tvLastMessage.setText("Conversation completed");
            } else {
                tvStatus.setText("ACTIVE");
                tvStatus.setVisibility(View.VISIBLE);
                tvStatus.setBackgroundResource(R.drawable.green_bg_circle);
                tvStatus.setTextColor(itemView.getContext().getResources().getColor(android.R.color.white));

                // Reset appearance for active conversations
                itemView.setAlpha(1.0f);
                tvTitle.setTextColor(itemView.getContext().getResources().getColor(android.R.color.black));
                tvLastMessage.setText("Tap to open conversation");
            }
        }
    }
}
