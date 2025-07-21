package com.example.man_zone.Model;

import java.util.List;

/**
 * Response model for /topic/conversation-done WebSocket messages
 * Handles the structure: {"success": true, "data": [...]}
 */
public class ConversationUpdateResponse {
    private boolean success;
    private List<ConversationModel> data;

    // Constructors
    public ConversationUpdateResponse() {
    }

    public ConversationUpdateResponse(boolean success, List<ConversationModel> data) {
        this.success = success;
        this.data = data;
    }

    // Getters and Setters
    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public List<ConversationModel> getData() {
        return data;
    }

    public void setData(List<ConversationModel> data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "ConversationUpdateResponse{" +
                "success=" + success +
                ", data=" + (data != null ? data.size() + " conversations" : "null") +
                '}';
    }
}
