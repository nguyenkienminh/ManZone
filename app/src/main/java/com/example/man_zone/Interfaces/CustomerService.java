package com.example.man_zone.Interfaces;


import com.example.man_zone.Model.CustomerModel;
import com.example.man_zone.Model.RegisterResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface CustomerService {
    String CUSTOMER_ENDPOINT = "Customer";

    @POST(CUSTOMER_ENDPOINT + "/AddCustomer")
    Call<RegisterResponse> addCustomer(@Body CustomerModel customer);
}
