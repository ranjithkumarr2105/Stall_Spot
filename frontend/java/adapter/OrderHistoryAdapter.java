package com.simats.foodstall.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import com.simats.foodstall.OreceiptActivity;
import com.simats.foodstall.R;
import com.simats.foodstall.model.OOrder;
import com.simats.foodstall.model.OrderItem;
import java.util.List;
import java.util.Locale;

public class OrderHistoryAdapter extends RecyclerView.Adapter<OrderHistoryAdapter.OrderViewHolder> {

    private List<OOrder> orderList;

    public OrderHistoryAdapter(List<OOrder> orderList) {
        this.orderList = orderList;
    }

    @NonNull
    @Override
    public OrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_order_history, parent, false);
        return new OrderViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderViewHolder holder, int position) {
        holder.bind(orderList.get(position));
    }

    @Override
    public int getItemCount() {
        return orderList.size();
    }

    @SuppressLint("NotifyDataSetChanged")
    public void updateList(List<OOrder> newList) {
        orderList = newList;
        notifyDataSetChanged();
    }

    static class OrderViewHolder extends RecyclerView.ViewHolder {
        TextView orderIdTextView, orderDateTextView, viewReceiptButton;
        LinearLayout itemsContainer;
        // --- NEW VIEW FOR THE STATUS TAG ---
        TextView orderStatusTag;

        OrderViewHolder(@NonNull View itemView) {
            super(itemView);
            orderIdTextView = itemView.findViewById(R.id.orderIdTextView);
            orderDateTextView = itemView.findViewById(R.id.orderDateTextView);
            itemsContainer = itemView.findViewById(R.id.itemsContainer);
            viewReceiptButton = itemView.findViewById(R.id.viewReceiptButton);
            // --- BIND THE NEW VIEW ---
            orderStatusTag = itemView.findViewById(R.id.orderStatusTag);
        }

        void bind(OOrder order) {
            orderIdTextView.setText(order.getDisplayOrderId());
            orderDateTextView.setText(order.getOrderDate());

            // Logic for the green/red status tag (This part is correct)
            String status = order.getOrderStatus();
            orderStatusTag.setText(status);
            Context context = itemView.getContext();
            int colorRes;
            if ("Delivered".equalsIgnoreCase(status)) {
                colorRes = R.color.status_delivered_green;
            } else if ("Rejected".equalsIgnoreCase(status)) {
                colorRes = R.color.status_rejected_red;
            } else {
                colorRes = R.color.text_secondary;
            }
            orderStatusTag.setBackgroundTintList(ContextCompat.getColorStateList(context, colorRes));

            itemsContainer.removeAllViews();
            LayoutInflater inflater = LayoutInflater.from(itemView.getContext());

            // --- TEMPORARY DEBUGGING CODE ---
            for (OrderItem item : order.getOrderItems()) {
                View itemView = inflater.inflate(R.layout.receipt_row, itemsContainer, false);
                ImageView itemIcon = itemView.findViewById(R.id.itemIcon);
                TextView itemNameAndQty = itemView.findViewById(R.id.labelTextView);
                TextView itemPrice = itemView.findViewById(R.id.valueTextView);

                String itemParcelStatus = item.getParcelStatus();

                // THIS WILL PRINT THE RAW STATUS TO YOUR LOGCAT
                Log.d("ICON_DEBUG", "Item: " + item.getName() + ", Parcel Status from Server: '" + itemParcelStatus + "'");

                // THIS WILL SHOW THE STATUS ON THE SCREEN NEXT TO THE ITEM NAME
                itemNameAndQty.setText(String.format(Locale.getDefault(), "%s × %d [%s]", item.getName(), item.getQuantity(), itemParcelStatus));


                if (itemParcelStatus == null) {
                    itemParcelStatus = "dine-in";
                }

                switch (itemParcelStatus.toLowerCase()) {
                    case "parcel":
                        itemIcon.setImageResource(R.drawable.ic_bag1);
                        itemIcon.setColorFilter(0xFF00B5B0);
                        break;
                    case "pre-parcel":
                        itemIcon.setImageResource(R.drawable.ic_clock1);
                        itemIcon.setColorFilter(0xFF64B5F6);
                        break;
                    default: // "dine-in"
                        itemIcon.setImageResource(R.drawable.ic_dine_in);
                        itemIcon.clearColorFilter();
                        break;
                }

                itemPrice.setText(String.format(Locale.getDefault(), "₹%.2f", item.getPrice() * item.getQuantity()));
                itemsContainer.addView(itemView);
            }
            // --- END OF TEMPORARY CODE ---


            viewReceiptButton.setOnClickListener(v -> {
                Intent intent = new Intent(context, OreceiptActivity.class);
                intent.putExtra("ORDER_DETAILS", order);
                context.startActivity(intent);
            });
        }
    }
}