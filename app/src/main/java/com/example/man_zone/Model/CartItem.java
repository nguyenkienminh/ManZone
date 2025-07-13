package com.example.man_zone.Model;

import java.util.UUID;

public class CartItem {
    // Original CartItem fields
    private int quantity;
    private double totalPrice;

    // Fields from ProductModel
    private UUID productId;
    private String name;
    private String barcode;
    private double manufactureCost;
    private double weight;
    private String description;
    private int counterId;
    private int typeId;
    private String img;
    private String certificateUrl;
    private ProductModel.ProductStatuses productStatus;
    private double price;
    private double markupRate;
    private ProductModel.Units weightUnit;
    private double stonePrice;

    // Constructor using ProductModel
    public CartItem(ProductModel product, int quantity) {
        this.productId = product.getProductId();
        this.name = product.getName();
        this.barcode = product.getBarcode();
        this.manufactureCost = product.getManufactureCost();
        this.weight = product.getWeight();
        this.description = product.getDescription();
        this.counterId = product.getCounterId();
        this.typeId = product.getTypeId();
        this.img = product.getImg();
        this.certificateUrl = product.getCertificateUrl();
        this.productStatus = product.getProductStatus();
        this.price = product.getPrice();
        this.markupRate = product.getMarkupRate();
        this.weightUnit = product.getWeightUnit();
        this.stonePrice = product.getStonePrice();

        this.quantity = quantity;
        this.totalPrice = price * quantity;
    }

    // Simple constructor
    public CartItem(String name, double price, String img, int quantity) {
        this.name = name;
        this.price = price;
        this.img = img;
        this.quantity = quantity;
        this.totalPrice = price * quantity;
    }

    // Getters and setters for all fields
    public UUID getProductId() {
        return productId;
    }

    public void setProductId(UUID productId) {
        this.productId = productId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getBarcode() {
        return barcode;
    }

    public void setBarcode(String barcode) {
        this.barcode = barcode;
    }

    public double getManufactureCost() {
        return manufactureCost;
    }

    public void setManufactureCost(double manufactureCost) {
        this.manufactureCost = manufactureCost;
    }

    public double getWeight() {
        return weight;
    }

    public void setWeight(double weight) {
        this.weight = weight;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getCounterId() {
        return counterId;
    }

    public void setCounterId(int counterId) {
        this.counterId = counterId;
    }

    public int getTypeId() {
        return typeId;
    }

    public void setTypeId(int typeId) {
        this.typeId = typeId;
    }

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }

    public String getCertificateUrl() {
        return certificateUrl;
    }

    public void setCertificateUrl(String certificateUrl) {
        this.certificateUrl = certificateUrl;
    }

    public ProductModel.ProductStatuses getProductStatus() {
        return productStatus;
    }

    public void setProductStatus(ProductModel.ProductStatuses productStatus) {
        this.productStatus = productStatus;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
        this.totalPrice = this.price * this.quantity;
    }

    public double getMarkupRate() {
        return markupRate;
    }

    public void setMarkupRate(double markupRate) {
        this.markupRate = markupRate;
    }

    public ProductModel.Units getWeightUnit() {
        return weightUnit;
    }

    public void setWeightUnit(ProductModel.Units weightUnit) {
        this.weightUnit = weightUnit;
    }

    public double getStonePrice() {
        return stonePrice;
    }

    public void setStonePrice(double stonePrice) {
        this.stonePrice = stonePrice;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
        this.totalPrice = this.price * this.quantity;
    }

    public double getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(double totalPrice) {
        this.totalPrice = totalPrice;
    }
}