package com.example.man_zone.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.man_zone.Activity.ChatRoomActivity;
import com.example.man_zone.Model.ChatConversationModel;
import com.example.man_zone.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class ChatConversationAdapter extends RecyclerView.Adapter<ChatConversationAdapter.ViewHolder> {
    private List<ChatConversationModel> conversations;
    private Context context;

    public ChatConversationAdapter(Context context) {
        this.context = context;
        this.conversations = new ArrayList<>();
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
        ChatConversationModel conversation = conversations.get(position);
        holder.bind(conversation);
    }

    @Override
    public int getItemCount() {
        return conversations.size();
    }

    public void updateConversations(List<ChatConversationModel> newConversations) {
        this.conversations = newConversations;
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView titleTextView;
        private TextView lastMessageTextView;
        private TextView timeTextView;
        private TextView participantTypeTextView;
        private View unreadIndicator;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            titleTextView = itemView.findViewById(R.id.tvConversationTitle);
            lastMessageTextView = itemView.findViewById(R.id.tvLastMessage);
            timeTextView = itemView.findViewById(R.id.tvTime);
            participantTypeTextView = itemView.findViewById(R.id.tvParticipantType);
            unreadIndicator = itemView.findViewById(R.id.unreadIndicator);

            itemView.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {
                    ChatConversationModel conversation = conversations.get(position);
                    Intent intent = new Intent(context, ChatRoomActivity.class);
                    intent.putExtra("conversation_id", conversation.getId());
                    intent.putExtra("conversation_title", conversation.getTitle());
                    intent.putExtra("participant_type", conversation.getParticipantType());
                    context.startActivity(intent);
                }
            });
        }

        public void bind(ChatConversationModel conversation) {
            titleTextView.setText(conversation.getTitle());
            lastMessageTextView.setText(conversation.getLastMessage() != null ?
                conversation.getLastMessage() : "No messages yet");

            if (conversation.getLastMessageTime() != null) {
                SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, HH:mm", Locale.getDefault());
                timeTextView.setText(sdf.format(conversation.getLastMessageTime()));
            } else {
                timeTextView.setText("");
            }

            participantTypeTextView.setText(conversation.getParticipantType().toUpperCase());
            unreadIndicator.setVisibility(conversation.isRead() ? View.GONE : View.VISIBLE);
        }
    }
}
