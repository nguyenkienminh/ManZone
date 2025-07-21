package com.example.man_zone.Model;

import java.time.Instant;

public class ConversationModel {
    private int id;
    private Integer userId;
    private String email;
    private String title;
    private boolean done;
    private Instant createdAt;
    private Instant updatedAt;

    // Constructors
    public ConversationModel() {
    }

    public ConversationModel(int id, Integer userId, String email, String title, boolean done, Instant createdAt,
            Instant updatedAt) {
        this.id = id;
        this.userId = userId;
        this.email = email;
        this.title = title;
        this.done = done;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
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

    public boolean isDone() {
        return done;
    }

    public void setDone(boolean done) {
        this.done = done;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Instant updatedAt) {
        this.updatedAt = updatedAt;
    }
}
