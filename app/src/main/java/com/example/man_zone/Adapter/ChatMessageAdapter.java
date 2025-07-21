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
import com.example.man_zone.Model.MessageResponse;
import com.example.man_zone.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

public class ChatMessageAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final int VIEW_TYPE_MESSAGE_SENT = 1;
    private static final int VIEW_TYPE_MESSAGE_RECEIVED = 2;

    private List<MessageResponse.Message> messages;
    private Context context;
    private long currentUserId;

    public ChatMessageAdapter(Context context, long currentUserId) {
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
        MessageResponse.Message message = messages.get(position);

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
        MessageResponse.Message message = messages.get(position);
        return message.senderId == currentUserId ? VIEW_TYPE_MESSAGE_SENT : VIEW_TYPE_MESSAGE_RECEIVED;
    }

    public void addMessage(MessageResponse.Message message) {
        android.util.Log.d("CHAT_MESSAGE_ADAPTER",
                "Adding message: " + message.message + " from sender: " + message.senderId);
        messages.add(0, message); // Add to beginning for newest first
        notifyItemInserted(0);
        android.util.Log.d("CHAT_MESSAGE_ADAPTER", "Total messages now: " + messages.size());
    }

    public void updateMessages(List<MessageResponse.Message> newMessages) {
        this.messages.clear();
        this.messages.addAll(newMessages);
        notifyDataSetChanged();
    }

    private String formatTime(String isoTimeString) {
        android.util.Log.d("CHAT_MESSAGE_ADAPTER", "Formatting time for: " + isoTimeString);

        if (isoTimeString == null || isoTimeString.isEmpty()) {
            android.util.Log.w("CHAT_MESSAGE_ADAPTER", "Time string is null or empty");
            return "";
        }

        // First, handle microseconds by converting them to milliseconds
        String normalizedTimeString = normalizeMicroseconds(isoTimeString);
        android.util.Log.d("CHAT_MESSAGE_ADAPTER", "Normalized time string: " + normalizedTimeString);

        // Common date formats that APIs might return
        String[] dateFormats = {
                "yyyy-MM-dd'T'HH:mm:ss'Z'", // ISO format with Z
                "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", // ISO format with milliseconds and Z
                "yyyy-MM-dd'T'HH:mm:ss", // ISO format without Z
                "yyyy-MM-dd'T'HH:mm:ss.SSS", // ISO format with milliseconds, no Z
                "yyyy-MM-dd HH:mm:ss", // Simple format with space
                "yyyy-MM-dd'T'HH:mm:ssXXX", // ISO format with timezone offset
                "yyyy-MM-dd'T'HH:mm:ss.SSSXXX" // ISO format with milliseconds and timezone
        };

        for (String format : dateFormats) {
            try {
                SimpleDateFormat isoFormat = new SimpleDateFormat(format, Locale.getDefault());
                if (format.contains("Z") && !format.contains("XXX")) {
                    isoFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
                }
                Date date = isoFormat.parse(normalizedTimeString);

                SimpleDateFormat displayFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());
                String formattedTime = displayFormat.format(date);
                android.util.Log.d("CHAT_MESSAGE_ADAPTER",
                        "Successfully formatted time: " + formattedTime + " using format: " + format);
                return formattedTime;
            } catch (ParseException e) {
                // Continue to next format
                android.util.Log.d("CHAT_MESSAGE_ADAPTER",
                        "Failed to parse with format " + format + ": " + e.getMessage());
            }
        }

        android.util.Log.e("CHAT_MESSAGE_ADAPTER", "Failed to parse time with all formats: " + normalizedTimeString);

        // If all else fails, try to extract time using regex
        try {
            // Try to extract HH:mm pattern from the string
            java.util.regex.Pattern timePattern = java.util.regex.Pattern.compile("(\\d{2}):(\\d{2})");
            java.util.regex.Matcher matcher = timePattern.matcher(normalizedTimeString);
            if (matcher.find()) {
                String extractedTime = matcher.group(1) + ":" + matcher.group(2);
                android.util.Log.d("CHAT_MESSAGE_ADAPTER", "Extracted time using regex: " + extractedTime);
                return extractedTime;
            }
        } catch (Exception e) {
            android.util.Log.e("CHAT_MESSAGE_ADAPTER", "Regex extraction failed", e);
        }

        // Last resort: return the original string if it looks like time
        if (normalizedTimeString.length() <= 10 && normalizedTimeString.contains(":")) {
            android.util.Log.d("CHAT_MESSAGE_ADAPTER",
                    "Returning original string as fallback: " + normalizedTimeString);
            return normalizedTimeString;
        }

        return "";
    }

    /**
     * Normalizes microsecond timestamps to millisecond timestamps for
     * SimpleDateFormat compatibility
     * Converts formats like "2025-07-21T16:25:01.693576Z" to
     * "2025-07-21T16:25:01.693Z"
     */
    private String normalizeMicroseconds(String isoTimeString) {
        try {
            // Pattern to match timestamps with fractional seconds
            java.util.regex.Pattern microsecondPattern = java.util.regex.Pattern
                    .compile("(\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2}:\\d{2})\\.(\\d{6,})(Z|[+-]\\d{2}:\\d{2})?");
            java.util.regex.Matcher matcher = microsecondPattern.matcher(isoTimeString);

            if (matcher.find()) {
                String basePart = matcher.group(1); // yyyy-MM-ddTHH:mm:ss
                String fractionPart = matcher.group(2); // fractional seconds (6+ digits)
                String timezonePart = matcher.group(3); // Z or timezone offset

                // Truncate fractional seconds to 3 digits (milliseconds)
                String truncatedFraction = fractionPart.length() >= 3 ? fractionPart.substring(0, 3) : fractionPart;

                // Reconstruct the timestamp
                String normalized = basePart + "." + truncatedFraction + (timezonePart != null ? timezonePart : "");
                android.util.Log.d("CHAT_MESSAGE_ADAPTER",
                        "Converted microseconds: " + isoTimeString + " -> " + normalized);
                return normalized;
            }
        } catch (Exception e) {
            android.util.Log.w("CHAT_MESSAGE_ADAPTER", "Failed to normalize microseconds: " + e.getMessage());
        }

        // Return original string if no microseconds pattern found
        return isoTimeString;
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

        public void bind(MessageResponse.Message message) {
            android.util.Log.d("CHAT_MESSAGE_ADAPTER", "Binding sent message - ID: " + message.id +
                    ", Message: " + message.message + ", CreatedAt: " + message.createdAt);

            if (message.message != null && !message.message.isEmpty()) {
                messageText.setVisibility(View.VISIBLE);
                messageText.setText(message.message);
            } else {
                messageText.setVisibility(View.GONE);
            }

            if (message.imageUrl != null && !message.imageUrl.isEmpty()) {
                messageImage.setVisibility(View.VISIBLE);
                Glide.with(context)
                        .load(message.imageUrl)
                        .into(messageImage);
            } else {
                messageImage.setVisibility(View.GONE);
            }

            timeText.setText(formatTime(message.createdAt));
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

        public void bind(MessageResponse.Message message) {
            android.util.Log.d("CHAT_MESSAGE_ADAPTER", "Binding received message - ID: " + message.id +
                    ", Message: " + message.message + ", CreatedAt: " + message.createdAt +
                    ", Sender: " + message.senderEmail);

            if (message.message != null && !message.message.isEmpty()) {
                messageText.setVisibility(View.VISIBLE);
                messageText.setText(message.message);
            } else {
                messageText.setVisibility(View.GONE);
            }

            if (message.imageUrl != null && !message.imageUrl.isEmpty()) {
                messageImage.setVisibility(View.VISIBLE);
                Glide.with(context)
                        .load(message.imageUrl)
                        .into(messageImage);
            } else {
                messageImage.setVisibility(View.GONE);
            }

            senderName.setText(message.senderEmail != null ? message.senderEmail : "Support");
            timeText.setText(formatTime(message.createdAt));
        }
    }
}
