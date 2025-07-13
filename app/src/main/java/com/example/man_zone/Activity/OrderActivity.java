package com.example.man_zone.Activity;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.man_zone.Adapter.OrderAdapter;
import com.example.man_zone.Model.OrderModel;
import com.example.man_zone.R;
import com.example.man_zone.Repositories.OrderRepositories;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Response;

public class OrderActivity extends BaseActivity {
    private List<OrderModel> orderList;
    private OrderAdapter orderAdapter;
    private RecyclerView orderRecyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order);

        ImageView btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        orderRecyclerView = findViewById(R.id.rvOrderList);
        orderRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        SharedPreferences sharedPreferences = getSharedPreferences("user_data", MODE_PRIVATE);
        String customerId = sharedPreferences.getString("customerId", "N/A");

        orderList = new ArrayList<>();
        try {
            Call<List<OrderModel>> call = OrderRepositories.getOrderService().getOrderByCustomerId(customerId);
            call.enqueue(new retrofit2.Callback<List<OrderModel>>() {
                @SuppressLint("NotifyDataSetChanged")
                @Override
                public void onResponse(Call<List<OrderModel>> call, Response<List<OrderModel>> response) {
                    Log.d("API_RESPONSE", "onResponse triggered");
                    if (!response.isSuccessful()) {
                        Log.e("API_ERROR", "Response not successful: " + response.code());
                        return;
                    }
                    if (response.body() == null) {
                        Log.e("API_ERROR", "Response body is null");
                        return;
                    }
                    List<OrderModel> responseProducts = response.body();
                    orderList.addAll(responseProducts);
                    orderAdapter.notifyDataSetChanged();
                    Log.d("API_SUCCESS", "Products loaded: " + orderList.size());
                }
                @Override
                public void onFailure(Call<List<OrderModel>> call, Throwable throwable) {
                    Log.e("API_FAILURE", "Call failed: " + throwable.getMessage());
                    throwable.printStackTrace();
                }
            });
        } catch (Exception ex) {
            Log.e("API_EXCEPTION", "Exception in loadProducts: " + ex.getMessage());
            ex.printStackTrace();
        }

        orderAdapter = new OrderAdapter(orderList, this);
        orderRecyclerView.setAdapter(orderAdapter);
    }
}