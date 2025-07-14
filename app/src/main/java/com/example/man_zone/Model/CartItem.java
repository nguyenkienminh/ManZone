package com.example.man_zone.Model;

import java.io.Serializable;

public class CartItem implements Serializable {
    private int productId;
    private String name;
    private String img;
    private double price;
    private int quantity;
    private double totalPrice;

    public CartItem(ProductModel product, int quantity) {
        this.productId = product.getId(); // id là int
        this.name = product.getName();
        if (product.getImageUrls() != null && !product.getImageUrls().isEmpty()) {
            this.img = product.getImageUrls().get(0);
        } else {
            this.img = "";
        }
        this.price = product.getPrice();
        this.quantity = quantity;
        this.totalPrice = price * quantity;
    }

    // Getters & Setters
    public int getProductId() {
        return productId;
    }

    public void setProductId(int productId) { // ✅ Đúng kiểu int
        this.productId = productId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
        updateTotalPrice();
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
        updateTotalPrice();
    }

    public double getTotalPrice() {
        return totalPrice;
    }

    private void updateTotalPrice() {
        this.totalPrice = this.price * this.quantity;
    }
}
