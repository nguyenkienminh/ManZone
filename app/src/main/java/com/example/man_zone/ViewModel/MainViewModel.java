package com.example.man_zone.ViewModel;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.man_zone.Model.CategoryModel;
import com.example.man_zone.Model.CategoryResponse;
import com.example.man_zone.Model.ProductModel;
import com.example.man_zone.Model.ProductResponse;
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
        sliderList.add(new SliderModel(R.drawable.banner1, "Discount Summer"));
        sliderList.add(new SliderModel(R.drawable.banner2, "Discount 10%"));
        sliderList.add(new SliderModel(R.drawable.banner3, "Discount ManAccessories"));

        slider.setValue(sliderList);
    }

    public void loadCategories() {
        Call<CategoryResponse> call = ProductRepositories.getCategoryService().getCategories();
        call.enqueue(new retrofit2.Callback<CategoryResponse>() {
            @Override
            public void onResponse(Call<CategoryResponse> call, Response<CategoryResponse> response) {
                if (response.isSuccessful() && response.body() != null) {

                    // Lấy danh sách category từ data.content
                    List<CategoryModel> fetchedCategories = response.body().getData().getContent();

                    // Gán hình cho từng category
                    for (CategoryModel category : fetchedCategories) {
                        switch (category.getName().toLowerCase()) {
                            case "watches":
                                category.setImageResId(R.drawable.watches);
                                break;
                            case "ties":
                                category.setImageResId(R.drawable.ties);
                                break;
                            case "belts":
                                category.setImageResId(R.drawable.belts);
                                break;
                            case "sneakers":
                                category.setImageResId(R.drawable.sneakers);
                                break;
                            case "hats":
                                category.setImageResId(R.drawable.hats);
                                break;
                            default:
                                // Nếu category chưa có hình định sẵn thì có thể để mặc định hoặc bỏ qua
                                category.setImageResId(R.drawable.black_bg);
                                break;
                        }
                    }

                    // Cập nhật dữ liệu vào MutableLiveData
                    categories.setValue(fetchedCategories);
                    Log.d("CATEGORY_SUCCESS", "Categories loaded: " + fetchedCategories.size());
                } else {
                    Log.e("CATEGORY_ERROR", "Response unsuccessful or body null");
                }
            }

            @Override
            public void onFailure(Call<CategoryResponse> call, Throwable t) {
                Log.e("CATEGORY_FAILURE", "API Call failed: " + t.getMessage());
            }
        });
    }



    public void loadProducts(boolean loadAll) {
        List<ProductModel> productList = new ArrayList<>();
        try {
            Call<ProductResponse> call = ProductRepositories.getProductService().getProducts();

            call.enqueue(new retrofit2.Callback<ProductResponse>() {
                @Override
                public void onResponse(Call<ProductResponse> call, Response<ProductResponse> response) {
                    Log.d("API_RESPONSE", "onResponse triggered");

                    if (!response.isSuccessful() || response.body() == null) {
                        Log.e("API_ERROR", "Response not successful or body null");
                        return;
                    }

                    List<ProductModel> responseProducts = response.body().getData().getContent();

                    if (loadAll) {
                        productList.addAll(responseProducts);
                    } else {
                        int limit = Math.min(responseProducts.size(), 4);
                        for (int i = 0; i < limit; i++) {
                            productList.add(responseProducts.get(i));
                        }
                    }

                    products.setValue(productList);
                    Log.d("API_SUCCESS", "Products loaded: " + productList.size());
                }

                @Override
                public void onFailure(Call<ProductResponse> call, Throwable throwable) {
                    Log.e("API_FAILURE", "Call failed: " + throwable.getMessage());
                }
            });

        } catch (Exception ex) {
            Log.e("API_EXCEPTION", "Exception in loadProducts: " + ex.getMessage());
        }
    }
}
