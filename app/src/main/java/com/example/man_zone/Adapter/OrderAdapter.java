package com.example.man_zone.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.man_zone.Model.OrderModel;
import com.example.man_zone.R;

import java.util.List;

public class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.OrderViewHolder> {
    private List<OrderModel> orders;
    private Context context;

    public OrderAdapter(List<OrderModel> orders, Context context) {
        this.orders = orders;
        this.context = context;
    }

    @NonNull
    @Override
    public OrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.viewholder_order, parent, false);
        return new OrderViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderViewHolder holder, int position) {
        OrderModel order = orders.get(position);
        holder.tvOrderId.setText(String.valueOf("#" + order.getOrderId()));
        if (order.getOrderStatus().equals("0")){
            holder.tvStatus.setText("Created");
        } else if (order.getOrderStatus().equals("1")) {
            holder.tvStatus.setText("Paying");
        } else if (order.getOrderStatus().equals("2")) {
            holder.tvStatus.setText("Completed");
        } else if (order.getOrderStatus().equals("3")) {
            holder.tvStatus.setText("Cancelled");
        }
        holder.tvDate.setText(order.getOrderDate());
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(context, "You clicked in order " + order.getOrderId(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return orders.size();
    }

    public static class OrderViewHolder extends RecyclerView.ViewHolder {
        TextView tvOrderId, tvStatus, tvDate;

        public OrderViewHolder(@NonNull View itemView) {
            super(itemView);
            tvOrderId = itemView.findViewById(R.id.tvOrderId);
            tvStatus = itemView.findViewById(R.id.tvStatus);
            tvDate = itemView.findViewById(R.id.tvDate);
        }
    }
}
