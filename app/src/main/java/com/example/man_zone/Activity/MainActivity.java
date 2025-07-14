package com.example.man_zone.Activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.CompositePageTransformer;
import androidx.viewpager2.widget.MarginPageTransformer;

import com.example.man_zone.Adapter.CategoryAdapter;
import com.example.man_zone.Adapter.ProductAdapter;
import com.example.man_zone.Adapter.SliderAdapter;
import com.example.man_zone.Model.ProductModel;
import com.example.man_zone.Model.SliderModel;
import com.example.man_zone.R;
import com.example.man_zone.ViewModel.MainViewModel;
import com.example.man_zone.databinding.ActivityMainBinding;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends BaseActivity {
    private ActivityMainBinding binding;
    private MainViewModel viewModel = new MainViewModel();
    private List<ProductModel> allProducts = new ArrayList<>();
    private ProductAdapter productAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        initBanners();
        initCategory();
        initProducts();
        initSearchFunctionality();

        View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(0);

        Window window = getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);

        binding.btnHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, MapActivity.class);
                startActivity(intent);
            }
        });

            SharedPreferences sharedPreferences = getSharedPreferences("user_data", MODE_PRIVATE);
            TextView mainName = findViewById(R.id.tvNameMain);
            String userName = sharedPreferences.getString("email", "N/A");
            mainName.setText(userName);

        binding.btnCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, CartActivity.class);
                startActivity(intent);
            }
        });

        binding.btnOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, OrderActivity.class);
                startActivity(intent);
            }
        });

        binding.btnProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, ProfileActivity.class);
                startActivity(intent);
            }
        });

        binding.btnWishlist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, WishlistActivity.class);
                startActivity(intent);
            }
        });

        binding.seeAllProducts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, ProductActivity.class);
                startActivity(intent);
            }
        });
    }

    private void initSearchFunctionality() {
        EditText searchEditText = binding.editTextText;
        searchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                filterProducts(s.toString());
            }
        });
    }

    private void filterProducts(String searchText) {
        List<ProductModel> filteredList = new ArrayList<>();
        for (ProductModel product : allProducts) {
            if (product.getName().toLowerCase().contains(searchText.toLowerCase())) {
                filteredList.add(product);
            }
        }
        if (productAdapter != null) {
            productAdapter.updateProductList(filteredList);
        }
    }

    private void initCategory() {
        binding.progressBar.setVisibility(View.VISIBLE);
        viewModel.getCategories().observe(this, categories -> {
            if (categories != null && !categories.isEmpty()) {
                binding.recyclerViewCategory.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
                CategoryAdapter categoryAdapter = new CategoryAdapter(categories);
                binding.recyclerViewCategory.setAdapter(categoryAdapter);
            } else {
                Log.d("MainActivity", "No categories found or categories are empty.");
            }
            binding.progressBarCategory.setVisibility(View.GONE);
        });
        viewModel.loadCategories();
    }

    private void initBanners() {
        binding.progressBar.setVisibility(View.VISIBLE);
        viewModel.getSlider().observe(this, banners -> {
            setupBanners(banners);
            binding.progressBar.setVisibility(View.GONE);
        });
        viewModel.loadSlider();
    }

    private void initProducts() {
        binding.progressBarBestSeller.setVisibility(View.VISIBLE);
        viewModel.getProducts().observe(this, products -> {
            if (products != null && !products.isEmpty()) {
                allProducts = new ArrayList<>(products);
                binding.recyclerViewBestSeller.setLayoutManager(new GridLayoutManager(this, 2));
                productAdapter = new ProductAdapter(this, products);
                binding.recyclerViewBestSeller.setAdapter(productAdapter);
            } else {
                Log.d("MainActivity", "No products found or products are empty.");
            }
            binding.progressBarBestSeller.setVisibility(View.GONE);
        });
        viewModel.loadProducts(false);
    }

    private void setupBanners(List<SliderModel> images) {
        binding.viewPager2.setAdapter(new SliderAdapter(images, binding.viewPager2));
        binding.viewPager2.setClipToPadding(false);
        binding.viewPager2.setClipChildren(false);
        binding.viewPager2.setOffscreenPageLimit(3);
        binding.viewPager2.getChildAt(0).setOverScrollMode(RecyclerView.OVER_SCROLL_NEVER);

        CompositePageTransformer transformer = new CompositePageTransformer();
        transformer.addTransformer(new MarginPageTransformer(40));
        binding.viewPager2.setPageTransformer(transformer);

        if (images.size() > 1) {
            binding.dotIndicator.setVisibility(View.VISIBLE);
            binding.dotIndicator.attachTo(binding.viewPager2);
        }
    }
}