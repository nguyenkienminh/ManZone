package com.example.man_zone.Adapter;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.man_zone.Activity.DetailActivity;
import com.example.man_zone.Interfaces.ProductService;
import com.example.man_zone.Model.ProductModel;
import com.example.man_zone.R;
import com.example.man_zone.Repositories.ProductRepositories;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ProductViewHolder> {
    private List<ProductModel> products;
    private Context context;
    private ProductService productService;

    public ProductAdapter(Context context, List<ProductModel> products) {
        this.context = context;
        this.products = products;
        productService = ProductRepositories.getProductService();
    }

    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.viewholder_best_seller_product, parent, false);
        return new ProductViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductViewHolder holder, int position) {
        ProductModel product = products.get(position);
        holder.tvName.setText(product.getName());
        holder.tvPrice.setText(NumberFormat.getCurrencyInstance(new Locale("vi", "VN"))
                .format(product.getPrice())
                .replace("₫", " đ"));
        if (product.getImageUrls() != null && !product.getImageUrls().isEmpty()) {
            Glide.with(context).load(product.getImageUrls().get(0)).into(holder.picProduct);
        } else {
            holder.picProduct.setImageResource(R.drawable.black_bg); // ảnh mặc định nếu ko có ảnh
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, DetailActivity.class);
                Bundle bundle = new Bundle();
                bundle.putInt("productId", product.getId());
                bundle.putString("title", product.getName());
                bundle.putDouble("price", product.getPrice());
                bundle.putString("description", product.getDescription());
                String imgUrl = "";
                if (product.getImageUrls() != null && !product.getImageUrls().isEmpty()) {
                    imgUrl = product.getImageUrls().get(0);
                }
                bundle.putString("img", imgUrl);

                intent.putExtras(bundle);
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return products.size();
    }

    public void updateProductList(List<ProductModel> newList) {
        this.products.clear();
        this.products.addAll(newList);
        notifyDataSetChanged();
    }

    public static class ProductViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvPrice, tvRating;
        ImageView picProduct;

        public ProductViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvName);
            tvPrice = itemView.findViewById(R.id.tvprice);
            picProduct = itemView.findViewById(R.id.picBestSeller);
        }
    }
}