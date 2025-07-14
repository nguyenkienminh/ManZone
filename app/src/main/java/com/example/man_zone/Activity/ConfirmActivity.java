package com.example.man_zone.Activity;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.man_zone.Adapter.ConfirmAdapter;
import com.example.man_zone.ApiClient.ApiClient;
import com.example.man_zone.Interfaces.OrderService;
import com.example.man_zone.Model.CartItem;
import com.example.man_zone.Model.OrderDetail;
import com.example.man_zone.Model.OrderModel;
import com.example.man_zone.ApiClient.CreateOrder;
import com.example.man_zone.R;
import com.example.man_zone.ViewModel.SimpleResponse;
import com.example.man_zone.helpers.CartManager;
import com.paypal.android.sdk.payments.PayPalService;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import vn.zalopay.sdk.Environment;
import vn.zalopay.sdk.ZaloPayError;
import vn.zalopay.sdk.ZaloPaySDK;
import vn.zalopay.sdk.listeners.PayOrderListener;

public class ConfirmActivity extends BaseActivity {
    private RecyclerView recyclerView;
    private ConfirmAdapter confirmAdapter;
    private TextView subtotalTv, deliveryTv, taxTv, totalTv;
    private Button btnPayPaypal;
    private Button btnCOD;
    private CartManager cartManager;
    private OrderService orderService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirm);

        orderService = ApiClient.getOrderService();

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        ZaloPaySDK.init(2553, Environment.SANDBOX);

        cartManager = CartManager.getInstance(this);
        initViews();
        setupRecyclerView();
        updatePrices();

        Double total = cartManager.getTotal();
        String totalAmount = String.format("%.0f", total);
        CreateOrder orderApi = new CreateOrder();

        try {
            JSONObject data = orderApi.createOrder(totalAmount);
            Log.d("Amount", total + "");
            String code = data.getString("returncode");
            Toast.makeText(this, "return_code: " + code, Toast.LENGTH_LONG).show();

            if (code.equals("1")) {
                setupListeners(data);
            }
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Failed to init payment", Toast.LENGTH_SHORT).show();
        }
    }

    private void initViews() {
        recyclerView = findViewById(R.id.confirmRecyclerView);
        subtotalTv = findViewById(R.id.confirmSubtotal);
        deliveryTv = findViewById(R.id.confirmDelivery);
        taxTv = findViewById(R.id.confirmTax);
        totalTv = findViewById(R.id.confirmTotal);
        btnPayPaypal = findViewById(R.id.btnPayPaypal);
        btnCOD = findViewById(R.id.btnCOD);
    }

    private void setupRecyclerView() {
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        confirmAdapter = new ConfirmAdapter(cartManager.getCartItems());
        recyclerView.setAdapter(confirmAdapter);
    }

    private void setupListeners(JSONObject data) {
        btnPayPaypal.setOnClickListener(v -> {
            processPayment(data);
        });

        btnCOD.setOnClickListener(v -> {
            createOrderAndGoToOrderActivity(1);
        });
    }

    private void processPayment(JSONObject data) {
        try {
            if (data.has("zptranstoken")) {
                String token = data.getString("zptranstoken");
                ZaloPaySDK.getInstance().payOrder(this, token, "demozpdk://app", new PayOrderListener() {
                    @Override
                    public void onPaymentSucceeded(String s, String s1, String s2) {
                        createOrderAndGoToOrderActivity(2);
                    }

                    @Override
                    public void onPaymentCanceled(String s, String s1) {
                        createOrderAndGoToOrderActivity(2);
                    }

                    @Override
                    public void onPaymentError(ZaloPayError zaloPayError, String s, String s1) {
                        showPaymentFailedDialog();
                    }
                });
            } else {
                Toast.makeText(this, "Payment init failed (missing token)", Toast.LENGTH_LONG).show();
            }
        } catch (JSONException e) {
            e.printStackTrace();
            Toast.makeText(this, "Payment data error", Toast.LENGTH_LONG).show();
        }
    }

    private void createOrderAndGoToOrderActivity(int paymentId) {
        OrderModel orderRequest = buildOrderRequest(paymentId);
        orderService.createOrder(orderRequest).enqueue(new Callback<SimpleResponse>() {
            @Override
            public void onResponse(Call<SimpleResponse> call, Response<SimpleResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Toast.makeText(ConfirmActivity.this, "Order created: " + response.body().getMessage(), Toast.LENGTH_SHORT).show();
                    cartManager.clearCart();
                    goToOrderActivity();
                } else {
                    Toast.makeText(ConfirmActivity.this, "Order creation failed", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<SimpleResponse> call, Throwable t) {
                Toast.makeText(ConfirmActivity.this, "API call failed: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private OrderModel buildOrderRequest(int paymentId) {
        OrderModel orderRequest = new OrderModel();
        SharedPreferences sharedPreferences = getSharedPreferences("user_data", MODE_PRIVATE);
        String customerId = sharedPreferences.getString("customerId", null);
        orderRequest.setCustomerId(UUID.fromString(customerId));
        orderRequest.setDiscount(0);
        orderRequest.setPromotionCode("Promotion 01");
        orderRequest.setCpId(null);
        orderRequest.setAccumulatedPoint(500);
        orderRequest.setCounterId(1);
        orderRequest.setPaymentId(paymentId);

        List<OrderDetail> details = new ArrayList<>();
        for (CartItem item : cartManager.getCartItems()) {
            OrderDetail detail = new OrderDetail();
            detail.setProductId(String.valueOf(item.getProductId()));
            detail.setQuantity(item.getQuantity());
            detail.setUnitPrice(item.getPrice());
            details.add(detail);
        }
        orderRequest.setOrderDetail(details);
        return orderRequest;
    }

    private void goToOrderActivity() {
        Intent intent = new Intent(this, OrderActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }

    private void showPaymentFailedDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Payment Failed")
                .setMessage("Please try again or choose other options.")
                .setPositiveButton("OK", null)
                .show();
    }

    private String formatPrice(double price) {
        return NumberFormat.getNumberInstance(new Locale("vi", "VN")).format(price) + " VND";
    }

    private void updatePrices() {
        subtotalTv.setText(formatPrice(cartManager.getSubtotal()));
        deliveryTv.setText(formatPrice(cartManager.getDeliveryFee()));
        taxTv.setText(formatPrice(cartManager.getTax()));
        totalTv.setText(formatPrice(cartManager.getTotal()));
    }

    @Override
    protected void onDestroy() {
        stopService(new Intent(this, PayPalService.class));
        super.onDestroy();
    }
}
