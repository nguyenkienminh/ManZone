package com.example.man_zone.Model;

import java.util.List;  // ✅ Import List


public class ProductResponse {
    private boolean success;
    private String message;
    private Data data;

    public boolean isSuccess() { return success; }
    public String getMessage() { return message; }
    public Data getData() { return data; }

    public static class Data {
        public List<ProductModel> content;

        public List<ProductModel> getContent() { return content; }
    }
}
