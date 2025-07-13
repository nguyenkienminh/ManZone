package com.example.man_zone.Model;

public class LoginResponse {
    private boolean success;
    private String message;
    private UserModel data; // Change Object to User

    // Getters and setters
    public boolean isSuccess() {
        return success;
    }

    public String getMessage() {
        return message;
    }

    public UserModel getData() {
        return data;
    }
}
