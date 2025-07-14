package com.example.man_zone.Interfaces;

import com.example.man_zone.Model.CategoryModel;
import com.example.man_zone.Model.CategoryResponse;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;

public interface CategoryService {
    @GET("/categories")
    Call<CategoryResponse> getCategories();
}
