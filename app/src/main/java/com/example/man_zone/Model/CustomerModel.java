package com.example.man_zone.Model;

import com.google.gson.annotations.SerializedName;

public class CustomerModel {

    @SerializedName("name")
    private String name;

    @SerializedName("phone")
    private String phone;

    @SerializedName("email")
    private String email;

    @SerializedName("address")
    private String address;

    @SerializedName("password")
    private String password;

    @SerializedName("customerGender")
    private int customerGender;

    // Constructor
    public CustomerModel(String name, String phone, String email, String address, String password, int customerGender) {
        this.name = name;
        this.phone = phone;
        this.email = email;
        this.address = address;
        this.password = password;
        this.customerGender = customerGender;
    }

    // Getters and Setters
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public int getCustomerGender() { return customerGender; }
    public void setCustomerGender(int customerGender) { this.customerGender = customerGender; }
}
