package com.example.man_zone.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.man_zone.Adapter.CartAdapter;
import com.example.man_zone.R;
import com.example.man_zone.helpers.CartManager;

import java.text.NumberFormat;
import java.util.Locale;

public class CartActivity extends BaseActivity {
    private RecyclerView recyclerView;
    private CartAdapter cartAdapter;
    private TextView subtotalTv, deliveryTv, taxTv, totalTv;
    private EditText couponEt;
    private Button btnCoupon, btnCheckout;
    private ImageView btnBack;
    private CartManager cartManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        // Initialize CartManager with context
        cartManager = CartManager.getInstance(this);

        initViews();
        setupRecyclerView();
        loadSavedState();
        updatePrices();
        setupListeners();
    }

    private void initViews() {
        recyclerView = findViewById(R.id.cartView);
        subtotalTv = findViewById(R.id.subtotal);
        deliveryTv = findViewById(R.id.delivery);
        taxTv = findViewById(R.id.tax);
        totalTv = findViewById(R.id.total);
        couponEt = findViewById(R.id.editTextText2);
        btnCoupon = findViewById(R.id.btnCoupon);
        btnCheckout = findViewById(R.id.btnCheckout);
        btnBack = findViewById(R.id.btnBack);
    }

    private void loadSavedState() {
        // Restore saved coupon if exists
        String savedCoupon = cartManager.getAppliedCoupon();
        if (!savedCoupon.isEmpty()) {
            couponEt.setText(savedCoupon);
        }
    }

    private void setupRecyclerView() {
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        cartAdapter = new CartAdapter(this, cartManager.getCartItems());
        recyclerView.setAdapter(cartAdapter);

        // Update UI when item is removed
        cartAdapter.setOnItemRemovedListener(() -> {
            updatePrices();
            checkEmptyState();
        });

        // Add quantity change listener
        cartAdapter.setOnQuantityChangedListener((item, quantity) -> {
            cartManager.updateItemQuantity(item, quantity);
            updatePrices();
        });
    }

    private void checkEmptyState() {
        if (cartManager.getCartItems().isEmpty()) {
            // You can add a TextView for empty state and show/hide it here
            btnCheckout.setEnabled(false);
            btnCoupon.setEnabled(false);
        } else {
            btnCheckout.setEnabled(true);
            btnCoupon.setEnabled(true);
        }
    }

    private void setupListeners() {
        btnBack.setOnClickListener(v -> finish());

        btnCoupon.setOnClickListener(v -> {
            String couponCode = couponEt.getText().toString().trim();
            if (!couponCode.isEmpty()) {
                if (cartManager.applyCoupon(couponCode)) {
                    Toast.makeText(this, "Coupon applied successfully!", Toast.LENGTH_SHORT).show();
                    updatePrices();
                } else {
                    Toast.makeText(this, "Invalid coupon code", Toast.LENGTH_SHORT).show();
                    couponEt.setText("");
                }
            }
        });

        btnCheckout.setOnClickListener(v -> {
            if (cartManager.getCartItems().isEmpty()) {
                Toast.makeText(this, "Your cart is empty!", Toast.LENGTH_SHORT).show();
                return;
            }
            proceedToCheckout();
        });
    }

    private void proceedToCheckout() {
        if (cartManager.getCartItems().isEmpty()) {
            Toast.makeText(this, "Your cart is empty!", Toast.LENGTH_SHORT).show();
            return;
        }
        Intent intent = new Intent(this, ConfirmActivity.class);
        startActivity(intent);
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
    protected void onResume() {
        super.onResume();
        // Refresh cart data when activity resumes
        cartAdapter.notifyDataSetChanged();
        updatePrices();
        checkEmptyState();
    }

    // Optional: Clear any resources when activity is destroyed
    @Override
    protected void onDestroy() {
        super.onDestroy();
        cartAdapter.setOnItemRemovedListener(null);
        cartAdapter.setOnQuantityChangedListener(null);
    }
}