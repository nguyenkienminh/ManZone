package com.example.man_zone.Model;

public class ChatMessage {
    private int conversationId;
    private int senderId;
    private String messageText;
    private String imageUrl;
    private String type;

    public ChatMessage() {
    }

    public ChatMessage(int conversationId, int senderId, String messageText, String imageUrl, String type) {
        this.conversationId = conversationId;
        this.senderId = senderId;
        this.messageText = messageText;
        this.imageUrl = imageUrl;
        this.type = type;
    }

    public int getConversationId() {
        return conversationId;
    }

    public void setConversationId(int conversationId) {
        this.conversationId = conversationId;
    }

    public int getSenderId() {
        return senderId;
    }

    public void setSenderId(int senderId) {
        this.senderId = senderId;
    }

    public String getMessageText() {
        return messageText;
    }

    public void setMessageText(String messageText) {
        this.messageText = messageText;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
