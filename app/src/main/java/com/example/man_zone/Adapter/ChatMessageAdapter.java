package com.example.man_zone.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.man_zone.Model.ChatMessageModel;
import com.example.man_zone.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class ChatMessageAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final int VIEW_TYPE_MESSAGE_SENT = 1;
    private static final int VIEW_TYPE_MESSAGE_RECEIVED = 2;

    private List<ChatMessageModel> messages;
    private Context context;
    private String currentUserId;

    public ChatMessageAdapter(Context context, String currentUserId) {
        this.context = context;
        this.currentUserId = currentUserId;
        this.messages = new ArrayList<>();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());

        if (viewType == VIEW_TYPE_MESSAGE_SENT) {
            View view = inflater.inflate(R.layout.item_message_sent, parent, false);
            return new SentMessageViewHolder(view);
        } else {
            View view = inflater.inflate(R.layout.item_message_received, parent, false);
            return new ReceivedMessageViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ChatMessageModel message = messages.get(position);

        if (holder instanceof SentMessageViewHolder) {
            ((SentMessageViewHolder) holder).bind(message);
        } else {
            ((ReceivedMessageViewHolder) holder).bind(message);
        }
    }

    @Override
    public int getItemCount() {
        return messages.size();
    }

    @Override
    public int getItemViewType(int position) {
        ChatMessageModel message = messages.get(position);
        return message.getSenderId().equals(currentUserId) ?
            VIEW_TYPE_MESSAGE_SENT : VIEW_TYPE_MESSAGE_RECEIVED;
    }

    public void addMessage(ChatMessageModel message) {
        messages.add(message);
        notifyItemInserted(messages.size() - 1);
    }

    public void updateMessages(List<ChatMessageModel> newMessages) {
        this.messages = newMessages;
        notifyDataSetChanged();
    }

    private class SentMessageViewHolder extends RecyclerView.ViewHolder {
        private TextView messageText;
        private TextView timeText;
        private ImageView messageImage;

        public SentMessageViewHolder(@NonNull View itemView) {
            super(itemView);
            messageText = itemView.findViewById(R.id.tvMessageText);
            timeText = itemView.findViewById(R.id.tvMessageTime);
            messageImage = itemView.findViewById(R.id.ivMessageImage);
        }

        public void bind(ChatMessageModel message) {
            if (message.getMessageText() != null && !message.getMessageText().isEmpty()) {
                messageText.setVisibility(View.VISIBLE);
                messageText.setText(message.getMessageText());
            } else {
                messageText.setVisibility(View.GONE);
            }

            if (message.getImageUrl() != null && !message.getImageUrl().isEmpty()) {
                itemView.findViewById(R.id.imageContainer).setVisibility(View.VISIBLE);
                messageImage.setVisibility(View.VISIBLE);
                Glide.with(context)
                    .load(message.getImageUrl())
                    .placeholder(R.drawable.image_placeholder)
                    .into(messageImage);
            } else {
                itemView.findViewById(R.id.imageContainer).setVisibility(View.GONE);
                messageImage.setVisibility(View.GONE);
            }

            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm", Locale.getDefault());
            timeText.setText(sdf.format(message.getTimestamp()));
        }
    }

    private class ReceivedMessageViewHolder extends RecyclerView.ViewHolder {
        private TextView messageText;
        private TextView timeText;
        private TextView senderName;
        private ImageView messageImage;

        public ReceivedMessageViewHolder(@NonNull View itemView) {
            super(itemView);
            messageText = itemView.findViewById(R.id.tvMessageText);
            timeText = itemView.findViewById(R.id.tvMessageTime);
            senderName = itemView.findViewById(R.id.tvSenderName);
            messageImage = itemView.findViewById(R.id.ivMessageImage);
        }

        public void bind(ChatMessageModel message) {
            senderName.setText(String.format("%s (%s)", message.getSenderName(), message.getSenderType()));

            if (message.getMessageText() != null && !message.getMessageText().isEmpty()) {
                messageText.setVisibility(View.VISIBLE);
                messageText.setText(message.getMessageText());
            } else {
                messageText.setVisibility(View.GONE);
            }

            if (message.getImageUrl() != null && !message.getImageUrl().isEmpty()) {
                itemView.findViewById(R.id.imageContainer).setVisibility(View.VISIBLE);
                messageImage.setVisibility(View.VISIBLE);
                Glide.with(context)
                    .load(message.getImageUrl())
                    .placeholder(R.drawable.image_placeholder)
                    .into(messageImage);
            } else {
                itemView.findViewById(R.id.imageContainer).setVisibility(View.GONE);
                messageImage.setVisibility(View.GONE);
            }

            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm", Locale.getDefault());
            timeText.setText(sdf.format(message.getTimestamp()));
        }
    }
}
