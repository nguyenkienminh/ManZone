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
import com.example.man_zone.Model.CartItem;
import com.example.man_zone.R;
import com.example.man_zone.helpers.CartManager;

import java.text.NumberFormat;
import java.util.Locale;

public class CartActivity extends BaseActivity {

    private RecyclerView recyclerView;
    private CartAdapter cartAdapter;
    private TextView subtotalTv, deliveryTv, totalTv;
    private EditText couponEt;
    private Button btnCoupon, btnCheckout;
    private ImageView btnBack;
    private CartManager cartManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

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
        totalTv = findViewById(R.id.total);
        couponEt = findViewById(R.id.editTextText2);
        btnCoupon = findViewById(R.id.btnCoupon);
        btnCheckout = findViewById(R.id.btnCheckout);
        btnBack = findViewById(R.id.btnBack);
    }

    private void loadSavedState() {
        String savedCoupon = cartManager.getAppliedCoupon();
        if (!savedCoupon.isEmpty()) {
            couponEt.setText(savedCoupon);
        }
    }

    private void setupRecyclerView() {
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        cartAdapter = new CartAdapter(this, cartManager.getCartItems());
        recyclerView.setAdapter(cartAdapter);

        cartAdapter.setOnItemRemovedListener(item -> {
            cartManager.removeFromCart(item);
            cartAdapter.updateData(cartManager.getCartItems());
            updatePrices();
            checkEmptyState();
        });

        cartAdapter.setOnQuantityChangedListener((item, quantity) -> {
            cartManager.updateItemQuantity(item, quantity);
            cartAdapter.updateData(cartManager.getCartItems());
            updatePrices();
        });
    }

    private void checkEmptyState() {
        boolean isEmpty = cartManager.getCartItems().isEmpty();
        btnCheckout.setEnabled(!isEmpty);
        btnCoupon.setEnabled(!isEmpty);
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
        totalTv.setText(formatPrice(cartManager.getTotal()));
    }

    @Override
    protected void onResume() {
        super.onResume();
        cartAdapter.updateData(cartManager.getCartItems());
        updatePrices();
        checkEmptyState();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        cartAdapter.setOnItemRemovedListener(null);
        cartAdapter.setOnQuantityChangedListener(null);
    }
}
