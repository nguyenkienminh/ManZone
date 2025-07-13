package com.example.man_zone.Model;

import java.io.Serializable;
import java.util.UUID;

public class ProductModel implements Serializable {
    private UUID productId;
    private String name;
    private String barcode;
    private double manufactureCost;
    private double weight;
    private int quantity;
    private String description;
    private int counterId;
    private int typeId;
    private String img;
    private String certificateUrl;
    private ProductStatuses productStatus;
    private double price;
    private double markupRate;
    private Units weightUnit;
    private double stonePrice;

    public ProductModel(String name, double price, String description, String img) {
        this.name = name;
        this.price = price;
        this.description = description;
        this.img = img;
    }
    public ProductModel(UUID productId,String name, double price, String description, String img) {
        this.productId = productId;
        this.name = name;
        this.price = price;
        this.description = description;
        this.img = img;
    }

    // Enum for Units
    public enum Units {
        g,
        ct
    }

    // Enum for ProductStatuses
    public enum ProductStatuses {
        Active,
        Deactive
    }

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

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
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

    public ProductStatuses getProductStatus() {
        return productStatus;
    }

    public void setProductStatus(ProductStatuses productStatus) {
        this.productStatus = productStatus;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public double getMarkupRate() {
        return markupRate;
    }

    public void setMarkupRate(double markupRate) {
        this.markupRate = markupRate;
    }

    public Units getWeightUnit() {
        return weightUnit;
    }

    public void setWeightUnit(Units weightUnit) {
        this.weightUnit = weightUnit;
    }

    public double getStonePrice() {
        return stonePrice;
    }

    public void setStonePrice(double stonePrice) {
        this.stonePrice = stonePrice;
    }



}
