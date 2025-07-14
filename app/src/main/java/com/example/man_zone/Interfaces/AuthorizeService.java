package com.example.man_zone.Interfaces;

import com.example.man_zone.Model.LoginRequest;
import com.example.man_zone.Model.LoginResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface AuthorizeService {
    String LOGIN_ENDPOINT = "auth"; // Base path for the authorize service

    @POST(LOGIN_ENDPOINT + "/login") // Complete endpoint for login
    Call<LoginResponse> login(@Body LoginRequest request);

}
