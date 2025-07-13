package com.example.man_zone.Repositories;

import com.example.man_zone.ApiClient.ApiClient;
import com.example.man_zone.Interfaces.ProductService;

public class ProductRepositories {
    public static ProductService getProductService() {
        return ApiClient.getClient().create(ProductService.class);
    }
}
