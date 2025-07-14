package com.example.man_zone.Interfaces;


import com.example.man_zone.Model.CustomerModel;
import com.example.man_zone.Model.ProfileResponse;
import com.example.man_zone.Model.RegisterResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Header;

public interface CustomerService {
    String CUSTOMER_ENDPOINT = "users";

    @POST(CUSTOMER_ENDPOINT)
    Call<RegisterResponse> addCustomer(@Body CustomerModel customer);

    @GET(CUSTOMER_ENDPOINT + "/profile")
    Call<ProfileResponse> getProfile(@Header("Authorization") String token);
}
