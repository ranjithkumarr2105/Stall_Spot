package com.simats.foodstall.adapter;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.simats.foodstall.R;
import com.simats.foodstall.model.UserOrder;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

public class UserOrdersAdapter extends RecyclerView.Adapter<UserOrdersAdapter.OrderViewHolder> {

    private final List<UserOrder> orderList;
    private final OnOrderClickListener clickListener;
    private boolean isInSelectionMode = false;
    private final Set<UserOrder> selectedItems = new HashSet<>();

    public interface OnOrderClickListener {
        void onViewReceiptClick(UserOrder order);
        void onItemClick(int position);
        void onItemLongClick(int position);
    }

    public UserOrdersAdapter(List<UserOrder> orderList, OnOrderClickListener listener) {
        this.orderList = orderList;
        this.clickListener = listener;
    }

    @NonNull
    @Override
    public OrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_order, parent, false);
        return new OrderViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderViewHolder holder, int position) {
        UserOrder order = orderList.get(position);
        holder.bind(order, clickListener, isInSelectionMode, selectedItems.contains(order));
    }

    @Override
    public int getItemCount() {
        return orderList.size();
    }

    // --- Methods to control selection mode ---
    public void setInSelectionMode(boolean inSelectionMode) {
        this.isInSelectionMode = inSelectionMode;
        if (!inSelectionMode) {
            selectedItems.clear();
        }
        notifyDataSetChanged();
    }

    public void toggleSelection(int position) {
        UserOrder order = orderList.get(position);
        if (selectedItems.contains(order)) {
            selectedItems.remove(order);
        } else {
            selectedItems.add(order);
        }
        notifyItemChanged(position);
    }

    public int getSelectedItemCount() {
        return selectedItems.size();
    }

    public List<UserOrder> getSelectedItems() {
        return new ArrayList<>(selectedItems);
    }

    public void clearSelections() {
        selectedItems.clear();
        notifyDataSetChanged();
    }

    // --- ViewHolder ---
    static class OrderViewHolder extends RecyclerView.ViewHolder {
        CardView cardView;
        TextView stallName, orderId, orderTime, orderStatus, totalPrice;
        Button viewReceiptButton;
        ImageView selectionIcon;

        public OrderViewHolder(@NonNull View itemView) {
            super(itemView);
            cardView = itemView.findViewById(R.id.cardView);
            stallName = itemView.findViewById(R.id.stallNameTextView);
            orderId = itemView.findViewById(R.id.orderIdTextView);
            orderTime = itemView.findViewById(R.id.orderTimeTextView);
            orderStatus = itemView.findViewById(R.id.orderStatusTextView);
            totalPrice = itemView.findViewById(R.id.totalPriceTextView);
            viewReceiptButton = itemView.findViewById(R.id.viewReceiptButton);
            selectionIcon = itemView.findViewById(R.id.selection_icon);
        }

        public void bind(final UserOrder order, final OnOrderClickListener listener, boolean isInSelectionMode, boolean isSelected) {
            stallName.setText(order.getStallName());
            orderId.setText(order.getDisplayOrderId());
            orderTime.setText(order.getOrderDate());
            orderStatus.setText(order.getOrderStatus());
            totalPrice.setText(String.format(Locale.getDefault(), "₹%.2f", order.getTotalAmount()));

            // --- START OF STYLE UPDATE ---
            // This logic now sets the background instead of just the text color.
            orderStatus.setTextColor(Color.WHITE);
            switch (order.getOrderStatus().toLowerCase()) {
                case "delivered":
                    orderStatus.setBackgroundResource(R.drawable.status_delivered_background);
                    break;
                case "cancelled":
                case "rejected":
                    orderStatus.setBackgroundResource(R.drawable.status_rejected_background);
                    break;
                case "pending":
                default:
                    orderStatus.setBackgroundResource(R.drawable.status_pending_background);
                    break;
            }
            // --- END OF STYLE UPDATE ---

            // Handle selection UI
            if (isSelected) {
                cardView.setCardBackgroundColor(Color.parseColor("#E0E0E0"));
                selectionIcon.setVisibility(View.VISIBLE);
            } else {
                cardView.setCardBackgroundColor(Color.WHITE);
                selectionIcon.setVisibility(View.GONE);
            }

            viewReceiptButton.setOnClickListener(v -> {
                if (!isInSelectionMode) {
                    listener.onViewReceiptClick(order);
                }
            });

            itemView.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onItemClick(getAdapterPosition());
                }
            });

            itemView.setOnLongClickListener(v -> {
                if (listener != null) {
                    listener.onItemLongClick(getAdapterPosition());
                }
                return true;
            });
        }
    }
}