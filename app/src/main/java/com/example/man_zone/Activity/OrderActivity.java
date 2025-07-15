package com.example.man_zone.Activity;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.man_zone.Adapter.EnhancedOrderAdapter;
import com.example.man_zone.Adapter.OrderAdapter;
import com.example.man_zone.Model.MyOrdersResponse;
import com.example.man_zone.Model.OrderModel;
import com.example.man_zone.R;
import com.example.man_zone.Repositories.OrderRepositories;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OrderActivity extends BaseActivity {
    private List<OrderModel> orderList;
    private EnhancedOrderAdapter enhancedOrderAdapter;
    private OrderAdapter orderAdapter; // Keep for backward compatibility
    private RecyclerView orderRecyclerView;
    private SwipeRefreshLayout swipeRefreshLayout;
    private ProgressBar progressBar;
    private LinearLayout emptyStateLayout;

    private static final int DEFAULT_PAGE = 0;
    private static final int DEFAULT_SIZE = 10;
    private boolean isLoading = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order);

        initViews();
        setupRecyclerView();
        setupSwipeRefresh();
        loadOrders();
    }

    private void initViews() {
        ImageView btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> finish());

        orderRecyclerView = findViewById(R.id.rvOrderList);
        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout);
        progressBar = findViewById(R.id.progressBar);
        emptyStateLayout = findViewById(R.id.emptyStateLayout);

        orderList = new ArrayList<>();
    }

    private void setupRecyclerView() {
        orderRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        enhancedOrderAdapter = new EnhancedOrderAdapter(orderList, this);
        orderRecyclerView.setAdapter(enhancedOrderAdapter);
    }

    private void setupSwipeRefresh() {
        swipeRefreshLayout.setOnRefreshListener(this::loadOrders);
        swipeRefreshLayout.setColorSchemeResources(
                android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);
    }

    private void loadOrders() {
        if (isLoading)
            return;

        isLoading = true;
        showLoading(true);

        // Get token from SharedPreferences
        SharedPreferences sharedPreferences = getSharedPreferences("user_data", MODE_PRIVATE);
        String token = sharedPreferences.getString("token", "");

        if (token.isEmpty()) {
            Toast.makeText(this, "Authentication required. Please login again.", Toast.LENGTH_LONG).show();
            showLoading(false);
            isLoading = false;
            return;
        }

        // Call the new API endpoint
        Call<MyOrdersResponse> call = OrderRepositories.getOrderService()
                .getMyOrders("Bearer " + token, DEFAULT_PAGE, DEFAULT_SIZE);

        call.enqueue(new Callback<MyOrdersResponse>() {
            @Override
            public void onResponse(Call<MyOrdersResponse> call, Response<MyOrdersResponse> response) {
                isLoading = false;
                showLoading(false);

                if (response.isSuccessful() && response.body() != null) {
                    MyOrdersResponse ordersResponse = response.body();

                    if (ordersResponse.isSuccess() && ordersResponse.getData() != null) {
                        List<OrderModel> orders = ordersResponse.getData().getContent();
                        updateOrdersList(orders);
                        Log.d("API_SUCCESS", "Orders loaded: " + orders.size());
                    } else {
                        String message = ordersResponse.getMessage() != null ? ordersResponse.getMessage()
                                : "Failed to load orders";
                        Toast.makeText(OrderActivity.this, message, Toast.LENGTH_SHORT).show();
                        updateOrdersList(new ArrayList<>());
                    }
                } else {
                    handleApiError(response.code());
                }
            }

            @Override
            public void onFailure(Call<MyOrdersResponse> call, Throwable t) {
                isLoading = false;
                showLoading(false);
                Log.e("API_FAILURE", "Call failed: " + t.getMessage(), t);
                Toast.makeText(OrderActivity.this, "Network error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                updateOrdersList(new ArrayList<>());
            }
        });
    }

    private void updateOrdersList(List<OrderModel> orders) {
        orderList.clear();
        if (orders != null) {
            orderList.addAll(orders);
        }

        enhancedOrderAdapter.updateOrders(orderList);
        updateEmptyState();
    }

    private void updateEmptyState() {
        boolean isEmpty = orderList.isEmpty();
        emptyStateLayout.setVisibility(isEmpty ? View.VISIBLE : View.GONE);
        orderRecyclerView.setVisibility(isEmpty ? View.GONE : View.VISIBLE);
    }

    private void showLoading(boolean show) {
        swipeRefreshLayout.setRefreshing(show);
        progressBar.setVisibility(show ? View.VISIBLE : View.GONE);
    }

    private void handleApiError(int errorCode) {
        String errorMessage;
        switch (errorCode) {
            case 401:
                errorMessage = "Authentication failed. Please login again.";
                break;
            case 403:
                errorMessage = "Access denied.";
                break;
            case 404:
                errorMessage = "Orders not found.";
                break;
            case 500:
                errorMessage = "Server error. Please try again later.";
                break;
            default:
                errorMessage = "Failed to load orders. Error code: " + errorCode;
                break;
        }
        Toast.makeText(this, errorMessage, Toast.LENGTH_LONG).show();
        updateOrdersList(new ArrayList<>());
    }
}