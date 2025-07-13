package com.example.man_zone.Interfaces;

import com.example.man_zone.Model.OrderModel;
import com.example.man_zone.ViewModel.SimpleResponse;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface OrderService {
    String ORDERS = "Order";

    @GET(ORDERS + "/GetOrderByCustomerId")
    Call<List<OrderModel>> getOrderByCustomerId(
            @Query("customerId") String id
    );

    @POST(ORDERS + "/AddNewSelling")
    Call<SimpleResponse> createOrder(@Body OrderModel orderRequest);
}