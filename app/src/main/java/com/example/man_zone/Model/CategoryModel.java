package com.example.man_zone.Model;

public class CategoryModel {

    private String name;
    private int id;
    private int imageResId;

    public CategoryModel() {
    }

    public CategoryModel(String name, int id, int imageResId) {
        this.name = name;
        this.id = id;
        this.imageResId = imageResId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getImageResId() {
        return imageResId;
    }

    public void setImageResId(int imageResId) {
        this.imageResId = imageResId;
    }
}
