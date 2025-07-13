package com.example.man_zone.Interfaces;

import com.example.man_zone.Model.LoginResponse;

import retrofit2.Call;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface AuthorizeService {
    String LOGIN_ENDPOINT = "Authorize"; // Base path for the authorize service

    @POST(LOGIN_ENDPOINT + "/LoginCustomer") // Complete endpoint for login
    Call<LoginResponse> login(
            @Query("email") String email, // Email parameter
            @Query("password") String password // Password parameter
    );
}
