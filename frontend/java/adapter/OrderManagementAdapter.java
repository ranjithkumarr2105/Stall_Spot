package com.simats.foodstall.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.simats.foodstall.R;
import com.simats.foodstall.model.Order;
import com.simats.foodstall.model.OrderItem;
import java.util.List;
import java.util.Locale;

public class OrderManagementAdapter extends RecyclerView.Adapter<OrderManagementAdapter.OrderViewHolder> {

    private final Context context;
    private List<Order> orderList;
    private final OrderClickListener clickListener; // UPDATED listener

    // NEW Interface for handling clicks
    public interface OrderClickListener {
        void onMarkAsCompleteClick(int position);
        void onViewReceiptClick(int position);
    }

    public OrderManagementAdapter(Context context, List<Order> orderList, OrderClickListener listener) {
        this.context = context;
        this.orderList = orderList;
        this.clickListener = listener;
    }

    @NonNull
    @Override
    public OrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_order_management, parent, false);
        return new OrderViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderViewHolder holder, int position) {
        Order order = orderList.get(position);

        holder.orderIdTextView.setText("#" + order.getOrderId());
        holder.orderTimeTextView.setText(order.getOrderTime());
        holder.paymentMethodTextView.setText(order.getPaymentMethod());

        // Reset views for recycling
        holder.markAsCompleteButton.setVisibility(View.GONE);
        holder.viewReceiptButton.setVisibility(View.GONE);
        holder.cancellationReasonTextView.setVisibility(View.GONE);
        holder.itemsContainer.removeAllViews();

        switch (order.getStatus()) {
            case "In Progress":
                holder.statusIcon.setImageResource(R.drawable.ic_status_in_progress);
                holder.statusTextView.setText("Preparing");
                holder.statusTextView.setTextColor(Color.parseColor("#FFA500"));
                holder.totalLabelTextView.setText(String.format(Locale.getDefault(), "Total: ₹%.0f", order.getTotalPrice()));
                holder.markAsCompleteButton.setVisibility(View.VISIBLE);
                break;
            case "Completed":
                holder.statusIcon.setImageResource(R.drawable.ic_status_completed);
                holder.statusTextView.setText("Completed");
                holder.statusTextView.setTextColor(Color.parseColor("#28A745"));
                holder.totalLabelTextView.setText(String.format(Locale.getDefault(), "Total: ₹%.0f", order.getTotalPrice()));
                holder.viewReceiptButton.setVisibility(View.VISIBLE);
                break;
            case "Cancelled": // UPDATED spelling
                holder.statusIcon.setImageResource(R.drawable.ic_status_canceled);
                holder.statusTextView.setText("Cancelled"); // UPDATED spelling
                holder.statusTextView.setTextColor(Color.parseColor("#DC3545"));
                holder.totalLabelTextView.setText(String.format(Locale.getDefault(), "Refunded: ₹%.0f", order.getTotalPrice()));
                holder.cancellationReasonTextView.setVisibility(View.VISIBLE);
                holder.cancellationReasonTextView.setText(order.getCancellationReason());
                break;
        }

        LayoutInflater inflater = LayoutInflater.from(context);
        for (OrderItem item : order.getOrderedItems()) {
            View itemRow = inflater.inflate(R.layout.item_receipt_row, holder.itemsContainer, false);
            TextView itemName = itemRow.findViewById(R.id.itemNameTextView);
            TextView itemPrice = itemRow.findViewById(R.id.itemPriceTextView);
            itemName.setText(String.format(Locale.getDefault(), "%s x%d", item.getName(), item.getQuantity()));
            itemPrice.setText(String.format(Locale.getDefault(), "₹%.0f", item.getPrice() * item.getQuantity()));
            holder.itemsContainer.addView(itemRow);
        }

        // SET new click listeners
        holder.markAsCompleteButton.setOnClickListener(v -> clickListener.onMarkAsCompleteClick(holder.getAdapterPosition()));
        holder.viewReceiptButton.setOnClickListener(v -> clickListener.onViewReceiptClick(holder.getAdapterPosition()));
    }

    @Override
    public int getItemCount() { return orderList.size(); }

    public void filterList(List<Order> filteredList) {
        this.orderList = filteredList;
        notifyDataSetChanged();
    }

    // NEW method to safely get an item
    public Order getItem(int position) {
        return orderList.get(position);
    }

    static class OrderViewHolder extends RecyclerView.ViewHolder {
        ImageView statusIcon;
        TextView orderIdTextView, statusTextView, orderTimeTextView, cancellationReasonTextView;
        LinearLayout itemsContainer;
        TextView totalLabelTextView, paymentMethodTextView;
        Button markAsCompleteButton, viewReceiptButton;

        public OrderViewHolder(@NonNull View itemView) {
            super(itemView);
            statusIcon = itemView.findViewById(R.id.statusIcon);
            orderIdTextView = itemView.findViewById(R.id.orderIdTextView);
            statusTextView = itemView.findViewById(R.id.statusTextView);
            orderTimeTextView = itemView.findViewById(R.id.orderTimeTextView);
            cancellationReasonTextView = itemView.findViewById(R.id.cancellationReasonTextView);
            itemsContainer = itemView.findViewById(R.id.itemsContainer);
            totalLabelTextView = itemView.findViewById(R.id.totalLabelTextView);
            paymentMethodTextView = itemView.findViewById(R.id.paymentMethodTextView);
            markAsCompleteButton = itemView.findViewById(R.id.markAsCompleteButton);
            viewReceiptButton = itemView.findViewById(R.id.viewReceiptButton);
        }
    }
}