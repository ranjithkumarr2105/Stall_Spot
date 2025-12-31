package com.simats.foodstall.adapter;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.simats.foodstall.R;
import com.simats.foodstall.model.OOrder;

public class OOrdersAdapter extends ListAdapter<OOrder, OOrdersAdapter.OrderViewHolder> {

    private final OnReceiptClickListener listener;

    public interface OnReceiptClickListener {
        void onViewReceiptClick(OOrder order);
    }

    public OOrdersAdapter(OnReceiptClickListener listener) {
        super(DIFF_CALLBACK);
        this.listener = listener;
    }

    private static final DiffUtil.ItemCallback<OOrder> DIFF_CALLBACK = new DiffUtil.ItemCallback<OOrder>() {
        @Override
        public boolean areItemsTheSame(@NonNull OOrder oldItem, @NonNull OOrder newItem) {
            return oldItem.getDisplayOrderId().equals(newItem.getDisplayOrderId());
        }

        @Override
        public boolean areContentsTheSame(@NonNull OOrder oldItem, @NonNull OOrder newItem) {
            // A more robust check
            return oldItem.getDisplayOrderId().equals(newItem.getDisplayOrderId());
        }
    };

    @NonNull
    @Override
    public OrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_owner_order, parent, false);
        return new OrderViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderViewHolder holder, int position) {
        holder.bind(getItem(position), listener);
    }

    static class OrderViewHolder extends RecyclerView.ViewHolder {
        TextView orderIdTextView, orderDateTextView, orderItemsTextView, statusTextView, totalAmountTextView;
        Button viewReceiptButton;

        public OrderViewHolder(@NonNull View itemView) {
            super(itemView);
            orderIdTextView = itemView.findViewById(R.id.orderIdTextView);
            orderDateTextView = itemView.findViewById(R.id.orderDateTextView);
            orderItemsTextView = itemView.findViewById(R.id.orderItemsTextView);
            statusTextView = itemView.findViewById(R.id.statusTextView);
            totalAmountTextView = itemView.findViewById(R.id.totalAmountTextView);
            viewReceiptButton = itemView.findViewById(R.id.viewReceiptButton);
        }

        public void bind(final OOrder order, final OnReceiptClickListener listener) {
            orderIdTextView.setText(order.getDisplayOrderId());
            orderDateTextView.setText(order.getOrderDate());
            orderItemsTextView.setText(order.getItemsSummary());
            totalAmountTextView.setText(String.format("₹%.2f", order.getTotalAmount()));
            statusTextView.setText(order.getOrderStatus());

            // --- START OF STYLE UPDATE ---
            statusTextView.setTextColor(Color.WHITE);
            if ("Delivered".equals(order.getOrderStatus())) {
                statusTextView.setBackgroundResource(R.drawable.status_delivered_background);
                viewReceiptButton.setVisibility(View.VISIBLE);
                if (listener != null) {
                    viewReceiptButton.setOnClickListener(v -> listener.onViewReceiptClick(order));
                }
            } else if ("Rejected".equals(order.getOrderStatus())) {
                statusTextView.setBackgroundResource(R.drawable.status_rejected_background);
                viewReceiptButton.setVisibility(View.GONE);
            } else { // Pending
                statusTextView.setBackgroundResource(R.drawable.status_pending_background);
                viewReceiptButton.setVisibility(View.GONE);
            }
            // --- END OF STYLE UPDATE ---
        }
    }
}