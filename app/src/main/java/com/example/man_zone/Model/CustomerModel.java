package com.example.man_zone.Model;

import com.google.gson.annotations.SerializedName;

public class CustomerModel {

    @SerializedName("firstName")
    private String firstName;

    @SerializedName("lastName")
    private String lastName;

    @SerializedName("password")
    private String password;

    @SerializedName("phoneNumber")
    private String phoneNumber;

    @SerializedName("email")
    private String email;

    @SerializedName("address")
    private String address;

    // Constructor
    public CustomerModel(String firstName, String lastName, String password, String phoneNumber, String email, String address) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.password = password;
        this.phoneNumber = phoneNumber;
        this.email = email;
        this.address = address;
    }

    // Getters and Setters

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) { this.firstName = firstName; }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) { this.lastName = lastName; }

    public String getPassword() { return password; }

    public void setPassword(String password) { this.password = password; }

    public String getPhoneNumber() { return phoneNumber; }

    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }

    public String getEmail() { return email; }

    public void setEmail(String email) { this.email = email; }

    public String getAddress() { return address; }

    public void setAddress(String address) { this.address = address; }
}
