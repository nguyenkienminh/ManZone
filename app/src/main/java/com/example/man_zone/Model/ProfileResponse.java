package com.example.man_zone.Model;

public class ProfileResponse {
    private boolean success;
    private String message;
    private UserData data;

    public boolean isSuccess() { return success; }
    public String getMessage() { return message; }
    public UserData getData() { return data; }

    public static class UserData {
            private int id;
            private String firstName;
            private String lastName;
            private String phoneNumber;
            private String avatarUrl;
            private String email;
            private String address;

            public int getId() { return id; }
            public String getFirstName() { return firstName; }
            public String getLastName() { return lastName; }
            public String getPhoneNumber() { return phoneNumber; }
            public String getAvatarUrl() { return avatarUrl; }
            public String getEmail() { return email; }
            public String getAddress() { return address; }
        }

}
