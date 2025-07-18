package com.example.man_zone.Interfaces;

import com.example.man_zone.Model.MyOrdersResponse;
import com.example.man_zone.Model.OrderCreateRequest;
import com.example.man_zone.Model.OrderModel;
import com.example.man_zone.ViewModel.SimpleResponse;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface OrderService {
        String ORDERS = "orders";

        @GET(ORDERS + "/my-orders")
        Call<MyOrdersResponse> getMyOrders(
                        @Header("Authorization") String token,
                        @Query("page") int page,
                        @Query("size") int size);

        // Legacy method (keeping for backward compatibility)
        @GET(ORDERS + "/my-orders")
        Call<List<OrderModel>> getOrderByCustomerId(
                        @Query("customerId") String id);

        @POST(ORDERS)
        Call<SimpleResponse> createOrder(
                        @Header("Authorization") String token,
                        @Body OrderCreateRequest orderRequest);

}