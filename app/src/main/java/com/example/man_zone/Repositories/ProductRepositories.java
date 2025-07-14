package com.example.man_zone.Repositories;

import com.example.man_zone.ApiClient.ApiClient;
import com.example.man_zone.Interfaces.CategoryService;
import com.example.man_zone.Interfaces.ProductService;

public class ProductRepositories {
    public static ProductService getProductService() {
        return ApiClient.getClient().create(ProductService.class);
    }
    public static CategoryService getCategoryService() {
        return ApiClient.getClient().create(CategoryService.class);
    }
}
