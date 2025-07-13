package com.example.man_zone.Interfaces;


import com.example.man_zone.Model.ProductModel;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

import java.util.List;

public interface ProductService {
    String PRODUCTS = "Product";

    @GET(PRODUCTS + "/GetAllProducts")
    Call<List<ProductModel>> getProducts();

    @GET(PRODUCTS + "/{id}")
    Call<ProductModel> getProductById(@Path("id") int id);

    @POST(PRODUCTS)
    Call<ProductModel> addProduct(@Body ProductModel product); // <ProductModel>


}
