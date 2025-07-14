package com.example.man_zone.Model;

import java.io.Serializable;

public class CategoryModel implements Serializable {
    private int id;
    private String name;
    private String description;
    private int imageResId;


    public CategoryModel(int id, String name, String description) {
        this.id = id;
        this.name = name;
        this.description = description;
    }

    // Getters & Setters
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

    public int getImageResId() { return imageResId; }
    public void setImageResId(int imageResId) { this.imageResId = imageResId; }
}
