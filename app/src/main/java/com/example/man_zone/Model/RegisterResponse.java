package com.example.man_zone.Model;

import com.google.gson.annotations.SerializedName;

public class RegisterResponse {

    @SerializedName("success")
    private boolean isSuccess;

    @SerializedName("message")
    private String message;

    @SerializedName("data")
    private Object data; // Use Object if "data" could be various types, or change to specific type if known

    // Getters and Setters
    public boolean isSuccess() {
        return isSuccess;
    }

    public void setIsSuccess(boolean isSuccess) {
        this.isSuccess = isSuccess;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }
}
