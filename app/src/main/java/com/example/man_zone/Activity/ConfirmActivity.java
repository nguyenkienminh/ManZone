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
import com.example.man_zone.Model.ProductModel;
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
    private ImageView btnBack;
    private OrderService orderService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_confirm);
            //Calling OrderService
            orderService = ApiClient.getOrderService();

            StrictMode.ThreadPolicy policy = new
                    StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);

            // ZaloPay SDK Init

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

                Log.d("Amount", cartManager.getTotal() + "");
//            lblZpTransToken.setVisibility(View.VISIBLE);
                String code = data.getString("returncode");
                Toast.makeText(getApplicationContext(), "return_code: " + code, Toast.LENGTH_LONG).show();

                if (code.equals("1")) {
//                lblZpTransToken.setText("zptranstoken");
                    setupListeners(data);
                    //IsDone();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

        } catch (Exception e) {
            Log.e("ERROR", "Error initializing activity", e);
            Toast.makeText(this, "Error initializing payment system", Toast.LENGTH_LONG).show();
            finish();
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
            createOrder(1);
            Intent intent = new Intent(this, OrderActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        });
    }

    private void processPayment(JSONObject data) {

        try {
            try{
            String token = data.getString("zp_trans_token");
            ZaloPaySDK.getInstance().payOrder(ConfirmActivity.this, token, "demozpdk://app", new PayOrderListener() {
                @Override
                public void onPaymentSucceeded(String s, String s1, String s2) {
                    createOrder(2);
                    cartManager.clearCart();
                    showPaymentSuccessDialog();


                }

                @Override
                public void onPaymentCanceled(String s, String s1) {
                    createOrder(2);
                    cartManager.clearCart();
                    showPaymentCanceledDialog();

                }

                @Override
                public void onPaymentError(ZaloPayError zaloPayError, String s, String s1) {
                    showPaymentFailedDialog();
                }
            });

        } catch (JSONException e) {
            throw new RuntimeException(e);
        }

            // Clear cart after successful payment


        } catch (Exception e) {
            Log.e("ERROR", "Error processing payment", e);
            Toast.makeText(this, "Error processing payment", Toast.LENGTH_LONG).show();
        }
    }

    private void showPaymentSuccessDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Payment Successful")
                .setMessage("Thank you for your purchase!")
                .setPositiveButton("OK", (dialog, which) -> {
                    Intent intent = new Intent(this, OrderActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    finish();
                })
                .setCancelable(false)
                .show();
    }


    private void showPaymentFailedDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Payment Failed")
                .setMessage("Please pay again! Or choose other options")
                .setPositiveButton("OK", (dialog, which) -> {
                    Intent intent = new Intent(this, OrderActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    finish();
                })
                .setCancelable(false)
                .show();
    }

    private void showPaymentCanceledDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Payment Canceled")
                .setMessage("Payment Canceled; We're sorry about that")
                .setPositiveButton("OK", (dialog, which) -> {
                    Intent intent = new Intent(this, OrderActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    finish();
                })
                .setCancelable(false)
                .show();
    }
    private String formatPrice(double price) {
        return NumberFormat.getNumberInstance(new Locale("vi", "VN"))
                .format(price) + " VND";
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

    public void createOrder(int paymentId) {
        OrderModel orderRequest = new OrderModel();
        SharedPreferences sharedPreferences = getSharedPreferences("user_data", MODE_PRIVATE);
        String customerId = sharedPreferences.getString("customerId", null);
        if (customerId == null) {
            Toast.makeText(this, "Customer ID not found", Toast.LENGTH_SHORT).show();
            return;
        }

        orderRequest.setCustomerId(UUID.fromString(customerId));
//        orderRequest.setCustomerId(UUID.fromString("1ae9c350-f3e5-4802-b1af-a7f4f5c2800e")); // Replace with actual customer ID logic
        orderRequest.setDiscount(0);
        orderRequest.setPromotionCode("Promotion 01");
        orderRequest.setCpId(null);
        orderRequest.setAccumulatedPoint(500);
        orderRequest.setCounterId(1);
        orderRequest.setPaymentId(paymentId);

        List<OrderDetail> orderDetails = new ArrayList<>();

        for (CartItem item : cartManager.getCartItems()) {
            OrderDetail detail = new OrderDetail();
            detail.setProductId(String.valueOf(item.getProductId()));
            detail.setQuantity(item.getQuantity());
            detail.setUnitPrice(item.getPrice());
            orderDetails.add(detail);
        }


        orderRequest.setOrderDetail(orderDetails);

        orderService.createOrder(orderRequest).enqueue(new Callback<SimpleResponse>() {
            @Override
            public void onResponse(Call<SimpleResponse> call, Response<SimpleResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    String message = response.body().getMessage();
                    Log.d("CreateOrder", "Response: " + message);
                    Toast.makeText(ConfirmActivity.this, message, Toast.LENGTH_SHORT).show();

                } else {
                    Log.e("CreateOrder", "Order creation failed: " + response.message());
                    Toast.makeText(ConfirmActivity.this, "Order creation failed", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<SimpleResponse> call, Throwable t) {
                Log.e("CreateOrder", "API call failed: " + t.getMessage());
                Toast.makeText(ConfirmActivity.this, "API call failed", Toast.LENGTH_SHORT).show();
            }
        });
    }
}