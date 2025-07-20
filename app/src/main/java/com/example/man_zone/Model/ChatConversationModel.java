package com.example.man_zone.Model;

import java.util.Date;

public class ChatConversationModel {
    private String id;
    private String userId;
    private String email;
    private String title;
    private String createdAt;

    public ChatConversationModel() {}

    public ChatConversationModel(String id, String userId, String email, String title, String createdAt) {
        this.id = id;
        this.userId = userId;
        this.email = email;
        this.title = title;
        this.createdAt = createdAt;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }
}
