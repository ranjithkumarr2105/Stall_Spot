package com.simats.foodstall.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;
import com.simats.foodstall.R;
import com.simats.foodstall.model.OOrder;
import com.simats.foodstall.model.OrderItem; // Import OrderItem
import java.util.List; // Import List
import java.util.Locale;

public class OwnerPendingOrdersAdapter extends ListAdapter<OOrder, OwnerPendingOrdersAdapter.OrderViewHolder> {

    private final OnOrderActionListener actionListener;

    public interface OnOrderActionListener {
        void onApproveClicked(OOrder order);
        void onRejectClicked(OOrder order);
    }

    public OwnerPendingOrdersAdapter(OnOrderActionListener listener) {
        super(DIFF_CALLBACK);
        this.actionListener = listener;
    }

    private static final DiffUtil.ItemCallback<OOrder> DIFF_CALLBACK = new DiffUtil.ItemCallback<OOrder>() {
        @Override
        public boolean areItemsTheSame(@NonNull OOrder oldItem, @NonNull OOrder newItem) {
            return oldItem.getDisplayOrderId().equals(newItem.getDisplayOrderId());
        }
        @Override
        public boolean areContentsTheSame(@NonNull OOrder oldItem, @NonNull OOrder newItem) {
            // This line will now work because OOrder has the getItemsJson() method
            return oldItem.getItemsJson().equals(newItem.getItemsJson()) && oldItem.getOrderStatus().equals(newItem.getOrderStatus());
        }
    };

    @NonNull
    @Override
    public OrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_owner_pending_order, parent, false);
        return new OrderViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderViewHolder holder, int position) {
        holder.bind(getItem(position));
    }

    class OrderViewHolder extends RecyclerView.ViewHolder {
        TextView orderIdTextView, studentIdTextView, orderTimeTextView, subtotalTextView, parcelFeeTextView, grandTotalTextView, parcelEndTimeTextView;
        LinearLayout itemsContainer;
        RelativeLayout parcelFeeLayout;
        Button approveButton, rejectButton;

        public OrderViewHolder(@NonNull View itemView) {
            super(itemView);
            orderIdTextView = itemView.findViewById(R.id.orderIdTextView);
            studentIdTextView = itemView.findViewById(R.id.studentIdTextView);
            orderTimeTextView = itemView.findViewById(R.id.orderTimeTextView);
            itemsContainer = itemView.findViewById(R.id.itemsContainer);
            subtotalTextView = itemView.findViewById(R.id.subtotalTextView);
            parcelFeeTextView = itemView.findViewById(R.id.parcelFeeTextView);
            grandTotalTextView = itemView.findViewById(R.id.grandTotalTextView);
            parcelEndTimeTextView = itemView.findViewById(R.id.parcelEndTimeTextView);
            parcelFeeLayout = itemView.findViewById(R.id.parcelFeeLayout);
            approveButton = itemView.findViewById(R.id.approveButton);
            rejectButton = itemView.findViewById(R.id.rejectButton);
        }

        public void bind(final OOrder order) {
            if (order == null) return;

            orderIdTextView.setText(order.getDisplayOrderId());
            studentIdTextView.setText(order.getStudentId());
            orderTimeTextView.setText(order.getOrderDate());
            grandTotalTextView.setText(String.format(Locale.getDefault(), "₹%.2f", order.getTotalAmount()));
            subtotalTextView.setText(String.format(Locale.getDefault(), "₹%.2f", order.getSubtotal()));

            if (order.getParcelFee() > 0) {
                parcelFeeLayout.setVisibility(View.VISIBLE);
                parcelFeeTextView.setText(String.format(Locale.getDefault(), "₹%.2f", order.getParcelFee()));
            } else {
                parcelFeeLayout.setVisibility(View.GONE);
            }
            String pickupTime = order.getPickupTime();
            if (pickupTime != null && !pickupTime.isEmpty()) {
                parcelEndTimeTextView.setVisibility(View.VISIBLE);
                parcelEndTimeTextView.setText("Pickup Before: " + pickupTime);
            } else {
                parcelEndTimeTextView.setVisibility(View.GONE);
            }

            itemsContainer.removeAllViews();
            LayoutInflater inflater = LayoutInflater.from(itemView.getContext());

            // --- START OF REFINED SECTION ---
            // We now use the clean getOrderItems() method instead of parsing JSON here
            List<OrderItem> items = order.getOrderItems();

            if (items.isEmpty()) {
                // Fallback to the summary text if the list is empty for any reason
                TextView errorText = new TextView(itemView.getContext());
                errorText.setText(order.getItemsSummary());
                itemsContainer.addView(errorText);
            } else {
                for (OrderItem item : items) {
                    View itemView = inflater.inflate(R.layout.item_owner_order_row, itemsContainer, false);

                    ImageView itemIcon = itemView.findViewById(R.id.itemIcon);
                    TextView itemDetails = itemView.findViewById(R.id.itemDetailsTextView);
                    String status = item.getParcelStatus() != null ? item.getParcelStatus() : "dine-in";

                    switch (status.toLowerCase()) {
                        case "parcel":
                            itemIcon.setImageResource(R.drawable.ic_bag1);
                            itemIcon.setColorFilter(0xFF00B5B0); // Parcel Color
                            break;
                        case "pre-parcel":
                            itemIcon.setImageResource(R.drawable.ic_clock1);
                            itemIcon.setColorFilter(0xFF64B5F6); // Pre-parcel Color
                            break;
                        default: // "dine-in"
                            itemIcon.setImageResource(R.drawable.ic_dine_in);
                            // UPDATED: Explicitly set the original color to prevent recycling issues
                            itemIcon.setColorFilter(0xFFFFB74D);
                            break;
                    }
                    String itemText = String.format(Locale.getDefault(), "%s × %d", item.getName(), item.getQuantity());
                    itemDetails.setText(itemText);
                    itemsContainer.addView(itemView);
                }
            }
            // --- END OF REFINED SECTION ---

            approveButton.setOnClickListener(v -> actionListener.onApproveClicked(order));
            rejectButton.setOnClickListener(v -> actionListener.onRejectClicked(order));
        }
    }
}