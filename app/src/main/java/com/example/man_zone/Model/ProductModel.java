package com.example.man_zone.Model;

import java.io.Serializable;
import java.util.List;

public class ProductModel implements Serializable {
    private int id;
    private String name;
    private String description;
    private double price;
    private List<String> imageUrls;
    private CategoryModel category;

    // Constructor
    public ProductModel(int id, String name, String description, double price, List<String> imageUrls, CategoryModel category) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.price = price;
        this.imageUrls = imageUrls;
        this.category = category;
    }

    // Getters & Setters

    public String getMainImage() {
        if (imageUrls != null && !imageUrls.isEmpty()) {
            return imageUrls.get(0);
        }
        return ""; // Hoặc trả về ảnh default nếu muốn
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public List<String> getImageUrls() {
        return imageUrls;
    }

    public void setImageUrls(List<String> imageUrls) {
        this.imageUrls = imageUrls;
    }

    public CategoryModel getCategory() {
        return category;
    }

    public void setCategory(CategoryModel category) {
        this.category = category;
    }
}
