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
import com.example.man_zone.Model.CartItem;
import com.example.man_zone.R;
import com.example.man_zone.helpers.CartManager;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.ViewHolder> {

    private final Context context;
    private List<CartItem> cartItems;
    private OnItemRemovedListener onItemRemovedListener;
    private OnQuantityChangedListener onQuantityChangedListener;
    private final CartManager cartManager;

    // Maximum quantity allowed per item
    private static final int MAX_QUANTITY = 99;

    public CartAdapter(Context context, List<CartItem> cartItems) {
        this.context = context;
        this.cartItems = cartItems;
        this.cartManager = CartManager.getInstance(context);
    }

    public void setOnItemRemovedListener(OnItemRemovedListener listener) {
        this.onItemRemovedListener = listener;
    }

    public void setOnQuantityChangedListener(OnQuantityChangedListener listener) {
        this.onQuantityChangedListener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.viewholder_cart, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        CartItem item = cartItems.get(position);

        // Set item details
        holder.tvName.setText(item.getName());
        holder.price.setText(formatPrice(item.getPrice()));
        holder.priceforMany.setText(formatPrice(item.getTotalPrice()));
        holder.quantity.setText(String.valueOf(item.getQuantity()));

        // Load image using Glide with error handling
        if (item.getImg() != null && !item.getImg().isEmpty()) {
            Glide.with(context)
                    .load(item.getImg())
                    .centerCrop()
                    .into(holder.imageView);
        }

        // Handle quantity increase
        holder.btnPlus.setOnClickListener(v -> {
            if (item.getQuantity() < MAX_QUANTITY) {
                int newQuantity = item.getQuantity() + 1;
                updateItemQuantity(item, newQuantity, position);
            } else {
                Toast.makeText(context, "Maximum quantity reached", Toast.LENGTH_SHORT).show();
            }
        });

        // Handle quantity decrease or removal
        holder.btnMinus.setOnClickListener(v -> {
            if (item.getQuantity() > 1) {
                int newQuantity = item.getQuantity() - 1;
                updateItemQuantity(item, newQuantity, position);
            } else {
                showRemoveConfirmation(item, position);
            }
        });

        // Add long click listener for direct removal
        holder.itemView.setOnLongClickListener(v -> {
            showRemoveConfirmation(item, position);
            return true;
        });
    }

    private void updateItemQuantity(CartItem item, int newQuantity, int position) {
        if (newQuantity > 0 && newQuantity <= MAX_QUANTITY) {
            item.setQuantity(newQuantity);
            cartManager.updateItemQuantity(item, newQuantity);
            notifyItemChanged(position);

            if (onQuantityChangedListener != null) {
                onQuantityChangedListener.onQuantityChanged(item, newQuantity);
            }
        }
    }

    private void showRemoveConfirmation(CartItem item, int position) {
        // You can implement a custom dialog here
        // For now, directly removing the item
        removeItem(item, position);
    }

    private void removeItem(CartItem item, int position) {
        cartManager.removeFromCart(item);
        cartItems.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, cartItems.size());

        if (onItemRemovedListener != null) {
            onItemRemovedListener.onItemRemoved();
        }
    }

    private String formatPrice(double price) {
        NumberFormat formatter = NumberFormat.getNumberInstance(new Locale("vi", "VN"));
        formatter.setMinimumFractionDigits(0);
        formatter.setMaximumFractionDigits(0);
        return formatter.format(price) + " VND";
    }

    @Override
    public int getItemCount() {
        return cartItems != null ? cartItems.size() : 0;
    }

    public void updateData(List<CartItem> newCartItems) {
        this.cartItems = newCartItems;
        notifyDataSetChanged();
    }

    public interface OnItemRemovedListener {
        void onItemRemoved();
    }

    public interface OnQuantityChangedListener {
        void onQuantityChanged(CartItem item, int quantity);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView, btnPlus, btnMinus;
        TextView tvName, price, priceforMany, quantity;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.imageView8);
            tvName = itemView.findViewById(R.id.tvName);
            price = itemView.findViewById(R.id.price);
            priceforMany = itemView.findViewById(R.id.priceforMany);
            quantity = itemView.findViewById(R.id.textView20);
            btnPlus = itemView.findViewById(R.id.btnPLus);
            btnMinus = itemView.findViewById(R.id.btnMinus);
        }
    }
}