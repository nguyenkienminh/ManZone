package com.example.man_zone.ViewModel;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.man_zone.Model.CategoryModel;
import com.example.man_zone.Model.ProductModel;
import com.example.man_zone.Model.SliderModel;
import com.example.man_zone.R;
import com.example.man_zone.Repositories.ProductRepositories;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Response;

public class MainViewModel extends ViewModel {
    private MutableLiveData<List<SliderModel>> slider = new MutableLiveData<>();
    private MutableLiveData<List<CategoryModel>> categories = new MutableLiveData<>();
    private MutableLiveData<List<ProductModel>> products = new MutableLiveData<>();

    public LiveData<List<SliderModel>> getSlider() {
        return slider;
    }

    public LiveData<List<CategoryModel>> getCategories() {
        return categories;
    }

    public LiveData<List<ProductModel>> getProducts() {
        return products;
    }

    public void loadSlider() {
        List<SliderModel> sliderList = new ArrayList<>();
        sliderList.add(new SliderModel(R.drawable.banner1, "Discount 10/10"));
        sliderList.add(new SliderModel(R.drawable.banner2, "Discount 10%"));
        sliderList.add(new SliderModel(R.drawable.banner3, "Discount Christmas"));

        slider.setValue(sliderList);
    }

    public void loadCategories() {
        List<CategoryModel> categoryList = new ArrayList<>();
        categoryList.add(new CategoryModel("Gold", 1, R.drawable.gold));
        categoryList.add(new CategoryModel("Silver", 2, R.drawable.silver));
        categoryList.add(new CategoryModel("Diamond", 3, R.drawable.diamond));
        categoryList.add(new CategoryModel("Necklace", 4, R.drawable.necklace));
        categoryList.add(new CategoryModel("Bracelet", 5, R.drawable.bracelet));

        categories.setValue(categoryList);
    }

    public void loadProducts(boolean loadAll) {
        List<ProductModel> productList = new ArrayList<>();
        try {
            Call<List<ProductModel>> call = ProductRepositories.getProductService().getProducts();
            call.enqueue(new retrofit2.Callback<List<ProductModel>>() {
                @Override
                public void onResponse(Call<List<ProductModel>> call, Response<List<ProductModel>> response) {
                    Log.d("API_RESPONSE", "onResponse triggered");
                    if (!response.isSuccessful()) {
                        Log.e("API_ERROR", "Response not successful: " + response.code());
                        return;
                    }

                    if (response.body() == null) {
                        Log.e("API_ERROR", "Response body is null");
                        return;
                    }

                    List<ProductModel> responseProducts = response.body();

                    if (loadAll) {
                        // Thêm toàn bộ sản phẩm
                        productList.addAll(responseProducts);
                    } else {
                        // Chỉ thêm 2 sản phẩm đầu tiên
                        int limit = Math.min(responseProducts.size(), 2);
                        for (int i = 0; i < limit; i++) {
                            productList.add(responseProducts.get(i));
                        }
                    }

                    products.setValue(productList);
                    Log.d("API_SUCCESS", "Products loaded: " + productList.size());
                }

                @Override
                public void onFailure(Call<List<ProductModel>> call, Throwable throwable) {
                    Log.e("API_FAILURE", "Call failed: " + throwable.getMessage());
                    throwable.printStackTrace();
                }
            });
        } catch (Exception ex) {
            Log.e("API_EXCEPTION", "Exception in loadProducts: " + ex.getMessage());
            ex.printStackTrace();
        }
    }
}
