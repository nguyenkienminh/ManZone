//package com.example.project_prm392.Activity;
//
//import android.os.Bundle;
//import android.view.View;
//import android.widget.ImageView;
//import android.widget.TextView;
//import androidx.appcompat.app.AppCompatActivity;
//import androidx.recyclerview.widget.LinearLayoutManager;
//import androidx.recyclerview.widget.RecyclerView;
//import com.example.project_prm392.Adapter.WishlistAdapter;
//import com.example.project_prm392.Model.ProductModel;
//import com.example.project_prm392.R;
//import com.example.project_prm392.helpers.FavoriteManager;
//import java.util.List;
//
//public class WishlistActivity extends AppCompatActivity {
//    private RecyclerView recyclerViewWishlist;
//    private WishlistAdapter wishlistAdapter;
//    private TextView tvEmptyWishlist;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_wishlist);
//
//        initializeViews();
//        setupRecyclerView();
//        loadFavorites();
//
//        findViewById(R.id.btnBack).setOnClickListener(v -> onBackPressed());
//    }
//
//    private void initializeViews() {
//        recyclerViewWishlist = findViewById(R.id.recyclerViewWishlist);
//        tvEmptyWishlist = findViewById(R.id.tvEmptyWishlist);
//    }
//
//    private void setupRecyclerView() {
//        recyclerViewWishlist.setLayoutManager(new LinearLayoutManager(this));
//    }
//
//    private void loadFavorites() {
//        List<ProductModel> favoriteList = FavoriteManager.getInstance(this).getFavoriteList();
//        wishlistAdapter = new WishlistAdapter(this, favoriteList);
//        recyclerViewWishlist.setAdapter(wishlistAdapter);
//
//        // Show/hide empty state
//        if (favoriteList.isEmpty()) {
//            tvEmptyWishlist.setVisibility(View.VISIBLE);
//            recyclerViewWishlist.setVisibility(View.GONE);
//        } else {
//            tvEmptyWishlist.setVisibility(View.GONE);
//            recyclerViewWishlist.setVisibility(View.VISIBLE);
//        }
//    }
//
//  @Override
//    protected void onResume() {
//        super.onResume();
//        List<ProductModel> favorites = FavoriteManager.getInstance(this).getFavoriteList();
//        wishlistAdapter.updateList(favorites);
//    }
//}

package com.example.man_zone.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.man_zone.Adapter.WishlistAdapter;
import com.example.man_zone.Model.ProductModel;
import com.example.man_zone.R;
import com.example.man_zone.helpers.FavoriteManager;
import java.util.List;

public class WishlistActivity extends AppCompatActivity {
    private RecyclerView recyclerViewWishlist;
    private WishlistAdapter wishlistAdapter;
    private TextView tvEmptyWishlist;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wishlist);

        initializeViews();
        setupRecyclerView();
        loadFavorites();

        findViewById(R.id.btnBack).setOnClickListener(v -> onBackPressed());
    }

    private void initializeViews() {
        recyclerViewWishlist = findViewById(R.id.recyclerViewWishlist);
        tvEmptyWishlist = findViewById(R.id.tvEmptyWishlist);
    }

    private void setupRecyclerView() {
        recyclerViewWishlist.setLayoutManager(new LinearLayoutManager(this));
    }

    private void loadFavorites() {
        List<ProductModel> favoriteList = FavoriteManager.getInstance(this).getFavoriteList();
        wishlistAdapter = new WishlistAdapter(this, favoriteList);

        // Thêm xử lý sự kiện click vào item
        wishlistAdapter.setOnItemClickListener(product -> {
            Intent intent = new Intent(WishlistActivity.this, DetailActivity.class);
            // Thay đổi tên key cho phù hợp với DetailActivity
            intent.putExtra("productId", product.getId());
            intent.putExtra("title", product.getName());  // Thay vì "product_name"
            intent.putExtra("price", product.getPrice()); // Thay vì "product_price"
            intent.putExtra("description", product.getDescription()); // Thay vì "product_description"
            String imgUrl = "";
            if (product.getImageUrls() != null && !product.getImageUrls().isEmpty()) {
                imgUrl = product.getImageUrls().get(0);
            }
            intent.putExtra("img", imgUrl);     // Thay vì "product_image"
            startActivity(intent);
        });
        recyclerViewWishlist.setAdapter(wishlistAdapter);

        // Show/hide empty state
        if (favoriteList.isEmpty()) {
            tvEmptyWishlist.setVisibility(View.VISIBLE);
            recyclerViewWishlist.setVisibility(View.GONE);
        } else {
            tvEmptyWishlist.setVisibility(View.GONE);
            recyclerViewWishlist.setVisibility(View.VISIBLE);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        List<ProductModel> favorites = FavoriteManager.getInstance(this).getFavoriteList();
        wishlistAdapter.updateList(favorites);

        // Cập nhật trạng thái hiển thị
        if (favorites.isEmpty()) {
            tvEmptyWishlist.setVisibility(View.VISIBLE);
            recyclerViewWishlist.setVisibility(View.GONE);
        } else {
            tvEmptyWishlist.setVisibility(View.GONE);
            recyclerViewWishlist.setVisibility(View.VISIBLE);
        }
    }
}