package com.example.man_zone.Activity;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.man_zone.Adapter.ConfirmAdapter;
import com.example.man_zone.ApiClient.ApiClient;
import com.example.man_zone.Interfaces.OrderService;
import com.example.man_zone.Model.CartItem;
import com.example.man_zone.Model.OrderCreateRequest;
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
    private EditText editCustomerName, editPhoneNumber, editAddress, editNote;
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

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);

        Log.d("ZaloPay", "onNewIntent được gọi - xử lý callback ZaloPay");
        ZaloPaySDK.getInstance().onResult(intent);
    }

    private void initViews() {
        recyclerView = findViewById(R.id.confirmRecyclerView);
        subtotalTv = findViewById(R.id.confirmSubtotal);
        deliveryTv = findViewById(R.id.confirmDelivery);
        taxTv = findViewById(R.id.confirmTax);
        totalTv = findViewById(R.id.confirmTotal);

        editCustomerName = findViewById(R.id.editCustomerName);
        editPhoneNumber = findViewById(R.id.editPhoneNumber);
        editAddress = findViewById(R.id.editAddress);
        editNote = findViewById(R.id.editNote);

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
                        Toast.makeText(ConfirmActivity.this, "Thanh toán đã bị hủy", Toast.LENGTH_SHORT).show();
                        Log.d("ZaloPay", "Thanh toán bị hủy bởi người dùng");
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
        Log.d("CreateOrder", "Bắt đầu gọi API tạo order với paymentMethod = " + paymentId);

        OrderCreateRequest orderRequest = new OrderCreateRequest();

        String name = editCustomerName.getText().toString().trim();
        String phone = editPhoneNumber.getText().toString().trim();
        String address = editAddress.getText().toString().trim();
        String note = editNote.getText().toString().trim();

        orderRequest.setCustomerName(name);
        orderRequest.setPhoneNumber(phone);
        orderRequest.setShippingAddress(address);
        orderRequest.setNote(note);
        orderRequest.setPaymentId(paymentId);

        Log.d("CreateOrder", "Thông tin người nhận: name=" + name + ", phone=" + phone + ", address=" + address + ", note=" + note);

        List<OrderCreateRequest.OrderDetailRequest> details = new ArrayList<>();
        for (CartItem item : cartManager.getCartItems()) {
            Log.d("CreateOrder", "Sản phẩm: productId=" + item.getProductId() + ", quantity=" + item.getQuantity());
            OrderCreateRequest.OrderDetailRequest detail = new OrderCreateRequest.OrderDetailRequest();
            detail.setProductId(item.getProductId());
            detail.setQuantity(item.getQuantity());
            details.add(detail);
        }

        orderRequest.setOrderDetails(details);

        SharedPreferences sharedPreferences = getSharedPreferences("user_data", MODE_PRIVATE);
        String token = sharedPreferences.getString("token", null);
        Log.d("CreateOrder", "Token lấy từ SharedPreferences: " + token);

        orderService.createOrder("Bearer " + token, orderRequest).enqueue(new Callback<SimpleResponse>() {
            @Override
            public void onResponse(Call<SimpleResponse> call, Response<SimpleResponse> response) {
                Log.d("CreateOrder", "API onResponse, code: " + response.code());

                if (response.isSuccessful() && response.body() != null) {
                    Log.d("CreateOrder", "Tạo order thành công: " + response.body().getMessage());
                    Toast.makeText(ConfirmActivity.this, "Order created: " + response.body().getMessage(), Toast.LENGTH_SHORT).show();
                    cartManager.clearCart();
                    goToOrderActivity();
                } else {
                    try {
                        String errorBody = response.errorBody() != null ? response.errorBody().string() : "null";
                        Log.e("CreateOrder", "Tạo order thất bại, code: " + response.code() + ", error: " + errorBody);
                    } catch (Exception e) {
                        Log.e("CreateOrder", "Lỗi khi đọc errorBody: " + e.getMessage());
                    }
                    Toast.makeText(ConfirmActivity.this, "Order creation failed", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<SimpleResponse> call, Throwable t) {
                Log.e("CreateOrder", "API call failed: " + t.getMessage());
                Toast.makeText(ConfirmActivity.this, "API call failed: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
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
