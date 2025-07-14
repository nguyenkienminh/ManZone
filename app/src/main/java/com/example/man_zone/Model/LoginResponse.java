package com.example.man_zone.Model;

public class LoginResponse {
    private boolean success;
    private String message;
    private TokenData data;

    public boolean isSuccess() { return success; }
    public String getMessage() { return message; }
    public TokenData getData() { return data; }

    // Inner class (TokenData) để viết chung file
    public static class TokenData {
        private String token;

        public String getToken() { return token; }
    }
}
