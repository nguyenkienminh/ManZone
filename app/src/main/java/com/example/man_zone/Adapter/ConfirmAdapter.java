package com.example.man_zone.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.man_zone.Model.CartItem;
import com.example.man_zone.R;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

public class ConfirmAdapter extends RecyclerView.Adapter<ConfirmAdapter.ViewHolder> {
    private List<CartItem> cartItems;

    public ConfirmAdapter(List<CartItem> cartItems) {
        this.cartItems = cartItems;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View inflate = LayoutInflater.from(parent.getContext()).inflate(R.layout.viewholder_confirm_item, parent, false);
        return new ViewHolder(inflate);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        CartItem item = cartItems.get(position);

        // Hiển thị thông tin cơ bản
        holder.titleTxt.setText(item.getName());
        holder.quantityTxt.setText("x" + item.getQuantity());
        holder.priceTxt.setText(formatPrice(item.getPrice() * item.getQuantity()));

        // Product ID (int nên không cần check null)
        if (holder.productIdTxt != null) {
            holder.productIdTxt.setText("ID: " + item.getProductId());
        }

        // Ảnh sản phẩm
        if (holder.productImg != null && item.getImg() != null) {
            Glide.with(holder.itemView.getContext())
                    .load(item.getImg())
                    .into(holder.productImg);
        }
    }


    @Override
    public int getItemCount() {
        return cartItems.size();
    }

    private String formatPrice(double price) {
        return NumberFormat.getNumberInstance(new Locale("vi", "VN"))
                .format(price) + " VND";
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        // Basic info
        TextView titleTxt, quantityTxt, priceTxt;

        // Additional product info
        TextView productIdTxt, weightTxt, descriptionTxt;
        TextView statusTxt, manufactureCostTxt, stonePriceTxt, markupRateTxt;
        ImageView productImg;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            // Basic info views
            titleTxt = itemView.findViewById(R.id.confirmItemTitle);
            quantityTxt = itemView.findViewById(R.id.confirmItemQuantity);
            priceTxt = itemView.findViewById(R.id.confirmItemPrice);

            // Additional product info views (nếu có trong layout)
            productIdTxt = itemView.findViewById(R.id.confirmItemProductId);
            weightTxt = itemView.findViewById(R.id.confirmItemWeight);
            descriptionTxt = itemView.findViewById(R.id.confirmItemDescription);
            statusTxt = itemView.findViewById(R.id.confirmItemStatus);
            manufactureCostTxt = itemView.findViewById(R.id.confirmItemManufactureCost);
            stonePriceTxt = itemView.findViewById(R.id.confirmItemStonePrice);
            markupRateTxt = itemView.findViewById(R.id.confirmItemMarkupRate);
            productImg = itemView.findViewById(R.id.confirmItemImage);
        }
    }
}