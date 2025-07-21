package com.example.man_zone.Model;

public class CreateConversationRequest {
    private String title;
    private int userId;

    public CreateConversationRequest() {
    }

    public CreateConversationRequest(String title, int userId) {
        this.title = title;
        this.userId = userId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }
}
