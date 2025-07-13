package com.example.man_zone.Repositories;

import com.example.man_zone.ApiClient.ApiClient;
import com.example.man_zone.Interfaces.OrderService;

public class OrderRepositories {
    public static OrderService getOrderService() {
        return ApiClient.getClient().create(OrderService.class);
    }
}
