package com.example.man_zone.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.man_zone.Model.ProductModel;
import com.example.man_zone.R;
import com.example.man_zone.helpers.FavoriteManager;
import com.google.android.material.imageview.ShapeableImageView;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

public class WishlistAdapter extends RecyclerView.Adapter<WishlistAdapter.WishlistViewHolder> {

    private List<ProductModel> productList;
    private Context context;
    private FavoriteManager favoriteManager;
    private OnItemClickListener listener;

    // Interface để xử lý sự kiện click
    public interface OnItemClickListener {
        void onItemClick(ProductModel product);
    }

    // Setter cho listener
    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    public WishlistAdapter(Context context, List<ProductModel> productList) {
        this.context = context;
        this.productList = productList;
        this.favoriteManager = FavoriteManager.getInstance(context);
    }

    @NonNull
    @Override
    public WishlistViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.wishlist_item, parent, false);
        return new WishlistViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull WishlistViewHolder holder, int position) {
        ProductModel product = productList.get(position);

        // Format giá tiền theo định dạng VND
        String formattedPrice = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"))
                .format(product.getPrice())
                .replace("₫", " đ");

        holder.tvProductName.setText(product.getName());
        holder.tvProductPrice.setText(formattedPrice);

        // Load ảnh sản phẩm
        String imgUrl = "";
        if (product.getImageUrls() != null && !product.getImageUrls().isEmpty()) {
            imgUrl = product.getImageUrls().get(0);
        }

        Glide.with(holder.itemView.getContext())
                .load(imgUrl)
                .into(holder.imgProduct);

        // Xử lý sự kiện click vào item
        holder.itemView.setOnClickListener(v -> {
            if (listener != null && holder.getAdapterPosition() != RecyclerView.NO_POSITION) {
                listener.onItemClick(productList.get(holder.getAdapterPosition()));
            }
        });

        // Xử lý sự kiện khi click nút xóa
        holder.btnFavorite.setOnClickListener(v -> {
            int currentPosition = holder.getAdapterPosition();
            if (currentPosition != RecyclerView.NO_POSITION) {
                ProductModel productToRemove = productList.get(currentPosition);

                // Xóa sản phẩm khỏi FavoriteManager
                favoriteManager.removeFromFavorites(productToRemove);

                // Xóa sản phẩm khỏi danh sách hiển thị
                productList.remove(currentPosition);
                notifyItemRemoved(currentPosition);
                notifyItemRangeChanged(currentPosition, productList.size());

                // Hiển thị thông báo
                Toast.makeText(context, "Đã xóa khỏi danh sách yêu thích", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return productList.size();
    }

    // Thêm phương thức để cập nhật danh sách
    public void updateList(List<ProductModel> newList) {
        this.productList = newList;
        notifyDataSetChanged();
    }

    static class WishlistViewHolder extends RecyclerView.ViewHolder {
        ShapeableImageView imgProduct;
        TextView tvProductName, tvProductPrice;
        ImageView btnFavorite;

        public WishlistViewHolder(@NonNull View itemView) {
            super(itemView);
            imgProduct = itemView.findViewById(R.id.imgProductWishList);
            tvProductName = itemView.findViewById(R.id.tvProductNameWishList);
            tvProductPrice = itemView.findViewById(R.id.tvProductPriceWishList);
            btnFavorite = itemView.findViewById(R.id.btnRemoveWishList);
        }
    }
}