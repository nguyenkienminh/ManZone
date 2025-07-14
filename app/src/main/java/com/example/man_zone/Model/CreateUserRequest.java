package com.example.man_zone.Model;

public class CreateUserRequest {
    private String firstName;
    private String lastName;
    private String password;
    private String phoneNumber;
    private String email;
    private String address;

    public CreateUserRequest(String firstName, String lastName, String password, String phoneNumber, String email, String address) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.password = password;
        this.phoneNumber = phoneNumber;
        this.email = email;
        this.address = address;
    }

}
