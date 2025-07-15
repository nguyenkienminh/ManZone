package com.example.man_zone.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.man_zone.Model.OrderModel;
import com.example.man_zone.R;

import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class EnhancedOrderAdapter extends RecyclerView.Adapter<EnhancedOrderAdapter.OrderViewHolder> {
    private List<OrderModel> orders;
    private Context context;

    public EnhancedOrderAdapter(List<OrderModel> orders, Context context) {
        this.orders = orders;
        this.context = context;
    }

    @NonNull
    @Override
    public OrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.viewholder_order_enhanced, parent, false);
        return new OrderViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderViewHolder holder, int position) {
        OrderModel order = orders.get(position);

        // Set order ID
        holder.tvOrderId.setText(String.format("#%d", order.getId()));

        // Set status with appropriate color
        holder.tvStatus.setText(order.getStatus());
        setStatusColor(holder.tvStatus, order.getStatus());

        // Set customer information
        if (order.getUser() != null) {
            holder.tvCustomerName.setText(order.getUser().getFullName());
        } else if (order.getCustomerName() != null && !order.getCustomerName().isEmpty()) {
            holder.tvCustomerName.setText(order.getCustomerName());
        } else {
            holder.tvCustomerName.setText("N/A");
        }

        holder.tvShippingAddress.setText(order.getShippingAddress() != null ? order.getShippingAddress() : "N/A");
        holder.tvPhoneNumber.setText(order.getPhoneNumber() != null ? order.getPhoneNumber() : "N/A");

        // Set total amount
        holder.tvTotalAmount.setText(formatCurrency(order.getTotalAmount()));

        // Set order date
        holder.tvOrderDate.setText(formatDate(order.getCreatedAt()));

        // Set note if available
        if (order.getNote() != null && !order.getNote().trim().isEmpty()) {
            holder.tvNote.setVisibility(View.VISIBLE);
            holder.tvNote.setText("Note: " + order.getNote());
        } else {
            holder.tvNote.setVisibility(View.GONE);
        }

        // Setup order details RecyclerView
        if (order.getOrderDetails() != null && !order.getOrderDetails().isEmpty()) {
            OrderDetailAdapter detailAdapter = new OrderDetailAdapter(order.getOrderDetails(), context);
            holder.rvOrderDetails.setLayoutManager(new LinearLayoutManager(context));
            holder.rvOrderDetails.setAdapter(detailAdapter);
        }

        // Set click listener for the entire order item
        holder.itemView.setOnClickListener(v -> {
            Toast.makeText(context, "Order #" + order.getId() + " details", Toast.LENGTH_SHORT).show();
            // Here you can implement navigation to order detail screen
        });
    }

    @Override
    public int getItemCount() {
        return orders != null ? orders.size() : 0;
    }

    private void setStatusColor(TextView statusView, String status) {
        int backgroundColor;
        switch (status.toUpperCase()) {
            case "PENDING":
                backgroundColor = 0xFFFFC107; // Amber
                break;
            case "PROCESSING":
                backgroundColor = 0xFF2196F3; // Blue
                break;
            case "SHIPPED":
                backgroundColor = 0xFF9C27B0; // Purple
                break;
            case "DELIVERED":
                backgroundColor = 0xFF4CAF50; // Green
                break;
            case "CANCELLED":
                backgroundColor = 0xFFF44336; // Red
                break;
            default:
                backgroundColor = 0xFF607D8B; // Blue Grey
                break;
        }
        statusView.setBackgroundColor(backgroundColor);
    }

    private String formatCurrency(double amount) {
        return NumberFormat.getNumberInstance(Locale.forLanguageTag("vi-VN")).format(amount) + " VND";
    }

    private String formatDate(String dateString) {
        if (dateString == null)
            return "N/A";

        try {
            SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSSSS'Z'", Locale.getDefault());
            SimpleDateFormat outputFormat = new SimpleDateFormat("MMM dd, yyyy", Locale.getDefault());
            Date date = inputFormat.parse(dateString);
            return outputFormat.format(date);
        } catch (ParseException e) {
            // Try alternative format
            try {
                SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.getDefault());
                SimpleDateFormat outputFormat = new SimpleDateFormat("MMM dd, yyyy", Locale.getDefault());
                Date date = inputFormat.parse(dateString);
                return outputFormat.format(date);
            } catch (ParseException ex) {
                return dateString; // Return original if parsing fails
            }
        }
    }

    public void updateOrders(List<OrderModel> newOrders) {
        this.orders = newOrders;
        notifyDataSetChanged();
    }

    public static class OrderViewHolder extends RecyclerView.ViewHolder {
        TextView tvOrderId, tvStatus, tvCustomerName, tvShippingAddress, tvPhoneNumber;
        TextView tvTotalAmount, tvOrderDate, tvNote;
        RecyclerView rvOrderDetails;

        public OrderViewHolder(@NonNull View itemView) {
            super(itemView);
            tvOrderId = itemView.findViewById(R.id.tvOrderId);
            tvStatus = itemView.findViewById(R.id.tvStatus);
            tvCustomerName = itemView.findViewById(R.id.tvCustomerName);
            tvShippingAddress = itemView.findViewById(R.id.tvShippingAddress);
            tvPhoneNumber = itemView.findViewById(R.id.tvPhoneNumber);
            tvTotalAmount = itemView.findViewById(R.id.tvTotalAmount);
            tvOrderDate = itemView.findViewById(R.id.tvOrderDate);
            tvNote = itemView.findViewById(R.id.tvNote);
            rvOrderDetails = itemView.findViewById(R.id.rvOrderDetails);
        }
    }
}
