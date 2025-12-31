package com.simats.foodstall.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.simats.foodstall.R;
import com.simats.foodstall.model.Order;
import com.simats.foodstall.model.OrderItem;

import java.util.ArrayList;
import java.util.Locale;

public class OrdersAdapter extends RecyclerView.Adapter<OrdersAdapter.ViewHolder> {

    public interface OnOrderClickListener {
        void onViewReceiptClick(Order order);
    }

    private final ArrayList<Order> orderList;
    private final Context context;
    private final OnOrderClickListener listener;

    public OrdersAdapter(Context context, ArrayList<Order> orderList, OnOrderClickListener listener) {
        this.context = context;
        this.orderList = orderList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_order, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bind(orderList.get(position));
    }

    @Override
    public int getItemCount() {
        return orderList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView stallName, orderId, orderTime, status, totalPrice;
        LinearLayout itemsContainer;
        Button viewReceiptButton;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            stallName = itemView.findViewById(R.id.stallNameTextView);
            orderId = itemView.findViewById(R.id.orderIdTextView);
            orderTime = itemView.findViewById(R.id.orderTimeTextView);
            status = itemView.findViewById(R.id.statusTextView);
            totalPrice = itemView.findViewById(R.id.totalPriceTextView);
            itemsContainer = itemView.findViewById(R.id.itemsContainer);
            viewReceiptButton = itemView.findViewById(R.id.viewReceiptButton);
        }

        void bind(Order order) {
            // Using getter methods to access the data
            stallName.setText(order.getStallName());
            orderId.setText(order.getOrderId());
            orderTime.setText(order.getOrderTime());
            status.setText(order.getStatus());
            totalPrice.setText(String.format(Locale.getDefault(), "₹%.2f", order.getTotalPrice()));

            // Set status background color dynamically
            switch (order.getStatus().toLowerCase()) {
                case "delivered":
                    status.setBackground(ContextCompat.getDrawable(context, R.drawable.status_delivered_background));
                    break;
                case "pending":
                    status.setBackground(ContextCompat.getDrawable(context, R.drawable.status_pending_background));
                    break;
                case "rejected":
                    status.setBackground(ContextCompat.getDrawable(context, R.drawable.status_rejected_background));
                    break;
                case "cancelled":
                    status.setBackground(ContextCompat.getDrawable(context, R.drawable.status_cancelled_background));
                    break;
                default: // For "Ready" or other statuses
                    status.setBackgroundColor(ContextCompat.getColor(context, R.color.blue));
                    break;
            }

            // Dynamically add the list of items
            itemsContainer.removeAllViews();
            LayoutInflater inflater = LayoutInflater.from(context);
            for (OrderItem item : order.getOrderedItems()) {
                TextView itemTextView = (TextView) inflater.inflate(R.layout.simple_list_item_order, itemsContainer, false);
                itemTextView.setText(String.format(Locale.getDefault(), "%s × %d", item.getName(), item.getQuantity()));
                itemsContainer.addView(itemTextView);
            }

            viewReceiptButton.setOnClickListener(v -> listener.onViewReceiptClick(order));
        }
    }
}