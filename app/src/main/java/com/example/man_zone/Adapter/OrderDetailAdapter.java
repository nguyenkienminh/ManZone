package com.example.man_zone.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.man_zone.Model.OrderDetailModel;
import com.example.man_zone.R;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

public class OrderDetailAdapter extends RecyclerView.Adapter<OrderDetailAdapter.OrderDetailViewHolder> {
    private List<OrderDetailModel> orderDetails;
    private Context context;

    public OrderDetailAdapter(List<OrderDetailModel> orderDetails, Context context) {
        this.orderDetails = orderDetails;
        this.context = context;
    }

    @NonNull
    @Override
    public OrderDetailViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.viewholder_order_detail, parent, false);
        return new OrderDetailViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderDetailViewHolder holder, int position) {
        OrderDetailModel detail = orderDetails.get(position);

        // Set product name
        holder.tvProductName.setText(detail.getProductName());

        // Set quantity
        holder.tvQuantity.setText(String.format("Qty: %d", detail.getQuantity()));

        // Set price
        holder.tvPrice.setText(formatCurrency(detail.getPrice()));

        // Set subtotal
        holder.tvSubtotal.setText(formatCurrency(detail.getSubtotal()));

        // Load product image
        if (detail.getProductImageUrl() != null && !detail.getProductImageUrl().isEmpty()) {
            Glide.with(context)
                    .load(detail.getProductImageUrl())
                    .placeholder(R.drawable.image_placeholder)
                    .error(R.drawable.image_placeholder)
                    .into(holder.ivProductImage);
        } else {
            holder.ivProductImage.setImageResource(R.drawable.image_placeholder);
        }
    }

    @Override
    public int getItemCount() {
        return orderDetails != null ? orderDetails.size() : 0;
    }

    private String formatCurrency(double amount) {
        return NumberFormat.getNumberInstance(Locale.forLanguageTag("vi-VN")).format(amount) + " VND";
    }

    public static class OrderDetailViewHolder extends RecyclerView.ViewHolder {
        ImageView ivProductImage;
        TextView tvProductName, tvQuantity, tvPrice, tvSubtotal;

        public OrderDetailViewHolder(@NonNull View itemView) {
            super(itemView);
            ivProductImage = itemView.findViewById(R.id.ivProductImage);
            tvProductName = itemView.findViewById(R.id.tvProductName);
            tvQuantity = itemView.findViewById(R.id.tvQuantity);
            tvPrice = itemView.findViewById(R.id.tvPrice);
            tvSubtotal = itemView.findViewById(R.id.tvSubtotal);
        }
    }
}
