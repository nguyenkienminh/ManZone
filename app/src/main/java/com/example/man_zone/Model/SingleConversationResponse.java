package com.example.man_zone.Model;

public class SingleConversationResponse {
    private boolean success;
    private String message;
    private ConversationModel data;
    private String errors;

    public SingleConversationResponse() {
    }

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

    public ConversationModel getData() {
        return data;
    }

    public void setData(ConversationModel data) {
        this.data = data;
    }

    public String getErrors() {
        return errors;
    }

    public void setErrors(String errors) {
        this.errors = errors;
    }
}
