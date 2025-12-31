package com.simats.foodstall.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.simats.foodstall.R;
import com.simats.foodstall.model.OrderStatus;
import java.util.ArrayList;
import java.util.List;

public class OrderStatusAdapter extends RecyclerView.Adapter<OrderStatusAdapter.OrderViewHolder> {
    private List<OrderStatus> orderList;
    private final OnReceiptClickListener listener;

    public interface OnReceiptClickListener {
        void onViewReceiptClick(OrderStatus order);
    }

    public OrderStatusAdapter(List<OrderStatus> orderList, OnReceiptClickListener listener) {
        this.orderList = new ArrayList<>(orderList);
        this.listener = listener;
    }

    @NonNull
    @Override
    public OrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_order_status, parent, false);
        return new OrderViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderViewHolder holder, int position) {
        OrderStatus order = orderList.get(position);
        holder.stallName.setText(order.getStallName());
        holder.orderId.setText(order.getOrderId());
        holder.dateTime.setText(order.getDateTime());
        holder.items.setText(order.getItems());
        holder.price.setText(String.format("₹%.2f", order.getPrice()));
        holder.status.setText(order.getStatus());

        if ("Approved".equalsIgnoreCase(order.getStatus())) {
            holder.status.setBackgroundResource(R.drawable.status_approved_background);
        } else if ("Cancelled".equalsIgnoreCase(order.getStatus())) {
            holder.status.setBackgroundResource(R.drawable.status_cancelled_background);
        }

        holder.viewReceiptButton.setOnClickListener(v -> {
            if (listener != null) {
                listener.onViewReceiptClick(order);
            }
        });
    }

    @Override
    public int getItemCount() {
        return orderList.size();
    }

    public void filterList(List<OrderStatus> filteredList) {
        orderList = filteredList;
        notifyDataSetChanged();
    }

    static class OrderViewHolder extends RecyclerView.ViewHolder {
        TextView stallName, orderId, dateTime, items, price, status;
        Button viewReceiptButton;

        public OrderViewHolder(@NonNull View itemView) {
            super(itemView);
            stallName = itemView.findViewById(R.id.stallNameTextView);
            orderId = itemView.findViewById(R.id.orderIdTextView);
            dateTime = itemView.findViewById(R.id.dateTimeTextView);
            items = itemView.findViewById(R.id.itemsTextView);
            price = itemView.findViewById(R.id.priceTextView);
            status = itemView.findViewById(R.id.statusTextView);
            viewReceiptButton = itemView.findViewById(R.id.viewReceiptButton);
        }
    }
}