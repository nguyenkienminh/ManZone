package com.example.man_zone.Model;

import java.util.Date;

public class ChatMessageModel {
    private String id;
    private String conversationId;
    private String senderId;
    private String senderName;
    private String senderType; // "user", "staff", "admin"
    private String messageText;
    private String imageUrl;
    private Date timestamp;
    private boolean isRead;

    public ChatMessageModel() {}

    public ChatMessageModel(String id, String conversationId, String senderId, String senderName,
                           String senderType, String messageText, String imageUrl, Date timestamp, boolean isRead) {
        this.id = id;
        this.conversationId = conversationId;
        this.senderId = senderId;
        this.senderName = senderName;
        this.senderType = senderType;
        this.messageText = messageText;
        this.imageUrl = imageUrl;
        this.timestamp = timestamp;
        this.isRead = isRead;
    }

    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getConversationId() { return conversationId; }
    public void setConversationId(String conversationId) { this.conversationId = conversationId; }

    public String getSenderId() { return senderId; }
    public void setSenderId(String senderId) { this.senderId = senderId; }

    public String getSenderName() { return senderName; }
    public void setSenderName(String senderName) { this.senderName = senderName; }

    public String getSenderType() { return senderType; }
    public void setSenderType(String senderType) { this.senderType = senderType; }

    public String getMessageText() { return messageText; }
    public void setMessageText(String messageText) { this.messageText = messageText; }

    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }

    public Date getTimestamp() { return timestamp; }
    public void setTimestamp(Date timestamp) { this.timestamp = timestamp; }

    public boolean isRead() { return isRead; }
    public void setRead(boolean read) { isRead = read; }
}
