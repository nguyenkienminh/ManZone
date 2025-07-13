package com.example.man_zone.Model;

import java.io.Serializable;

public class FavoriteModel implements Serializable {
    private ProductModel product;

    public FavoriteModel(ProductModel product) {
        this.product = product;
    }

    public ProductModel getProduct() {
        return product;
    }

    public void setProduct(ProductModel product) {
        this.product = product;
    }
}
