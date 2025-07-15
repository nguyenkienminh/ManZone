package com.example.man_zone.Model;

import java.util.List;

public class ConversationResponse {
    private boolean success;
    private String message;
    private List<ConversationModel> data;
    private String errors;

    // Getters and Setters
    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public List<ConversationModel> getData() {
        return data;
    }

    public void setData(List<ConversationModel> data) {
        this.data = data;
    }

    public String getErrors() {
        return errors;
    }

    public void setErrors(String errors) {
        this.errors = errors;
    }
}
