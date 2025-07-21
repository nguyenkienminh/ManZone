package com.example.man_zone.Model;

public class WebSocketMessage {
    private int conversationId;
    private int senderId;
    private String senderEmail;
    private String messageText;
    private String imageUrl;
    private MessageType type;
    private String createdAt;
    private long id;

    public WebSocketMessage() {
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

    public String getSenderEmail() {
        return senderEmail;
    }

    public void setSenderEmail(String senderEmail) {
        this.senderEmail = senderEmail;
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

    public MessageType getType() {
        return type;
    }

    public void setType(MessageType type) {
        this.type = type;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    // Convert to MessageResponse.Message for UI display
    public MessageResponse.Message toMessageResponse() {
        MessageResponse.Message message = new MessageResponse.Message();
        message.id = this.id;
        message.senderId = this.senderId;
        message.senderEmail = this.senderEmail;
        message.message = this.messageText;
        message.imageUrl = this.imageUrl;
        message.type = this.type != null ? this.type.toString() : "TEXT";
        message.createdAt = this.createdAt;
        message.updatedAt = this.createdAt;
        return message;
    }
}
