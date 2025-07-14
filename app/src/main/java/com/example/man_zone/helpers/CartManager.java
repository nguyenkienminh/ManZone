package com.example.man_zone.helpers;

import android.content.Context;
import android.content.SharedPreferences;
import com.example.man_zone.Model.CartItem;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class CartManager {
    private static CartManager instance;
    private List<CartItem> cartItems;
    private static final double TAX_RATE = 0.05;
    private static final double DELIVERY_FEE = 30000;
    private String appliedCoupon = "";
    private NotificationHelper notificationHelper;
    private static final String PREF_NAME = "CartPreferences";
    private static final String CART_ITEMS_KEY = "cart_items";
    private static final String APPLIED_COUPON_KEY = "applied_coupon";

    private Context context;
    private SharedPreferences sharedPreferences;
    private Gson gson;

    private CartManager(Context context) {
        this.context = context.getApplicationContext();
        this.sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        this.gson = new Gson();
        this.notificationHelper = new NotificationHelper(context);
        loadCart();
        checkAndNotifyCart();
    }

    private void checkAndNotifyCart() {
        if (!cartItems.isEmpty()) {
            int totalItems = 0;
            for (CartItem item : cartItems) {
                totalItems += item.getQuantity();
            }
            notificationHelper.showCartNotification(totalItems);
        }
    }

    public static CartManager getInstance(Context context) {
        if (instance == null) {
            instance = new CartManager(context);
        }
        return instance;
    }

    private void loadCart() {
        String cartJson = sharedPreferences.getString(CART_ITEMS_KEY, null);
        appliedCoupon = sharedPreferences.getString(APPLIED_COUPON_KEY, "");

        if (cartJson != null) {
            Type type = new TypeToken<ArrayList<CartItem>>() {}.getType();
            cartItems = gson.fromJson(cartJson, type);
        } else {
            cartItems = new ArrayList<>();
        }
    }

    private void saveCart() {
        String cartJson = gson.toJson(cartItems);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(CART_ITEMS_KEY, cartJson);
        editor.putString(APPLIED_COUPON_KEY, appliedCoupon);
        editor.apply();
    }

    public void addToCart(CartItem newItem) {
        boolean itemExists = false;

        for (CartItem existingItem : cartItems) {
            if (existingItem.getProductId() == newItem.getProductId()) {
                existingItem.setQuantity(existingItem.getQuantity() + newItem.getQuantity());
                itemExists = true;
                break;
            }
        }

        if (!itemExists) {
            cartItems.add(newItem);
        }

        saveCart();
        checkAndNotifyCart();
    }

    public void removeFromCart(CartItem itemToRemove) {
        cartItems.removeIf(item -> item.getProductId() == itemToRemove.getProductId());
        saveCart();
    }

    public void updateItemQuantity(CartItem itemToUpdate, int quantity) {
        for (CartItem item : cartItems) {
            if (item.getProductId() == itemToUpdate.getProductId()) {
                item.setQuantity(quantity);
                break;
            }
        }
        saveCart();
    }


    // Find item in cart by productId
    public CartItem findCartItemById(int productId) {
        for (CartItem item : cartItems) {
            if (item.getProductId() == productId) {
                return item;
            }
        }
        return null;
    }

    public void clearCart() {
        cartItems.clear();
        appliedCoupon = "";
        saveCart();
    }

    public List<CartItem> getCartItems() {
        return cartItems;
    }

    public double getSubtotal() {
        double subtotal = 0;
        for (CartItem item : cartItems) {
            subtotal += item.getTotalPrice();
        }
        return subtotal;
    }

    public double getTax() {
        return getSubtotal() * TAX_RATE;
    }

    public double getDeliveryFee() {
        return cartItems.isEmpty() ? 0 : DELIVERY_FEE;
    }

    public double getTotal() {
        return getSubtotal() + getTax() + getDeliveryFee();
    }

    public boolean applyCoupon(String couponCode) {
        if (couponCode.equals("DISCOUNT10")) {
            appliedCoupon = couponCode;
            saveCart();
            return true;
        }
        return false;
    }

    public String getAppliedCoupon() {
        return appliedCoupon;
    }
}