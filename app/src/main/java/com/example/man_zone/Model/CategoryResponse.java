package com.example.man_zone.Model;

import java.util.List;

public class CategoryResponse {
    private boolean success;
    private String message;
    private CategoryData data;

    // Getter Setter

    public CategoryData getData() {
        return data;
    }

    public boolean isSuccess() {
        return success;
    }

    public String getMessage() {
        return message;
    }

    // Inner class
    public static class CategoryData {
        private List<CategoryModel> content;

        public List<CategoryModel> getContent() {
            return content;
        }
    }
}
