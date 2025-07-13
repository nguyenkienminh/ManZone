package com.example.man_zone.Activity;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.man_zone.Adapter.ProductAdapter;
import com.example.man_zone.Model.ProductModel;
import com.example.man_zone.R;
import com.example.man_zone.ViewModel.MainViewModel;

import java.util.ArrayList;
import java.util.List;

public class ProductActivity extends AppCompatActivity {
    private List<ProductModel> allProducts;
    private List<ProductModel> productList;
    private ProductAdapter productAdapter;
    private RecyclerView productRecyclerView;
    private MainViewModel viewModel = new MainViewModel();
    private EditText searchEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product);

        ImageView btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> finish());

        productRecyclerView = findViewById(R.id.rvProductList);
        productRecyclerView.setLayoutManager(new GridLayoutManager(this, 2));

        searchEditText = findViewById(R.id.editTextSearch);
        initSearchFunctionality();

        viewModel.getProducts().observe(this, products -> {
            if (products != null && !products.isEmpty()) {
                allProducts = new ArrayList<>(products);
                productList = new ArrayList<>(products);
                productRecyclerView.setLayoutManager(new GridLayoutManager(this, 2));
                productAdapter = new ProductAdapter(this, productList);
                productRecyclerView.setAdapter(productAdapter);
            } else {
                Log.d("ProductActivity", "No products found or products are empty.");
            }
        });
        viewModel.loadProducts(true);
    }

    private void initSearchFunctionality() {
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
}