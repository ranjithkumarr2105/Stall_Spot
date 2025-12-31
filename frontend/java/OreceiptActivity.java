package com.simats.foodstall;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.simats.foodstall.model.OOrder;
import com.simats.foodstall.model.OrderItem;
import java.util.List;
import java.util.Locale;

public class OreceiptActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_oreceipt);

        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                finish();
            }
        });

        Button backButton = findViewById(R.id.backButton);
        backButton.setOnClickListener(v -> {
            finish();
        });

        OOrder order = getIntent().getParcelableExtra("ORDER_DETAILS");
        if (order != null) {
            populateReceipt(order);
        }
    }

    private void populateReceipt(OOrder order) {
        // Bind all the standard views from the layout
        TextView orderIdTextView = findViewById(R.id.receiptOrderId);
        TextView orderTimeTextView = findViewById(R.id.receiptOrderTime);
        LinearLayout itemsContainer = findViewById(R.id.receiptItemsContainer);
        TextView subtotalTextView = findViewById(R.id.receiptSubtotal);
        TextView parcelFeeTextView = findViewById(R.id.receiptParcelFee);
        TextView totalPaidTextView = findViewById(R.id.receiptTotalPaid);
        TextView studentIdTextView = findViewById(R.id.receiptStudentId);
        TextView pickupTimeTextView = findViewById(R.id.receiptPickupTime);
        View pickupTimeLayout = findViewById(R.id.receiptPickupTimeLayout);

        // --- NEW: Bind the views for the refund details section ---
        LinearLayout refundDetailsLayout = findViewById(R.id.refundDetailsLayout);
        TextView receiptStatusTextView = findViewById(R.id.receiptStatus);
        TextView receiptRefundStatusTextView = findViewById(R.id.receiptRefundStatus);
        TextView receiptRefundTimeTextView = findViewById(R.id.receiptRefundTime);

        // Populate the standard views with data
        orderIdTextView.setText(order.getDisplayOrderId());
        orderTimeTextView.setText(order.getOrderDate());
        studentIdTextView.setText(order.getStudentId());

        String pickupTime = order.getPickupTime();
        if (pickupTime != null && !pickupTime.isEmpty()) {
            pickupTimeLayout.setVisibility(View.VISIBLE);
            pickupTimeTextView.setText(pickupTime);
        } else {
            pickupTimeLayout.setVisibility(View.GONE);
        }

        // --- NEW LOGIC for Rejected Orders ---
        if ("Rejected".equalsIgnoreCase(order.getOrderStatus())) {
            // Show the refund details section
            refundDetailsLayout.setVisibility(View.VISIBLE);
            receiptStatusTextView.setText(order.getOrderStatus());
            receiptRefundStatusTextView.setText("Processed");

            // Display the refund timestamp if it exists
            String refundTime = order.getRefundTimestamp();
            if (refundTime != null && !refundTime.isEmpty()) {
                receiptRefundTimeTextView.setText(refundTime);
            } else {
                receiptRefundTimeTextView.setText("N/A");
            }

            // For a rejected order, "Total Paid" is conceptually zero after refund.
            // You can either show the original amount or show zero. Showing original is often clearer.
            totalPaidTextView.setText(String.format(Locale.getDefault(), "₹%.2f (Refunded)", order.getTotalAmount()));
            totalPaidTextView.setTextColor(ContextCompat.getColor(this, R.color.status_rejected_red));

        } else {
            // Hide the refund details for delivered orders
            refundDetailsLayout.setVisibility(View.GONE);
            totalPaidTextView.setText(String.format(Locale.getDefault(), "₹%.2f", order.getTotalAmount()));
        }
        // --- END OF NEW LOGIC ---

        subtotalTextView.setText(String.format(Locale.getDefault(), "₹%.2f", order.getSubtotal()));
        parcelFeeTextView.setText(String.format(Locale.getDefault(), "₹%.2f", order.getParcelFee()));

        itemsContainer.removeAllViews();
        List<OrderItem> items = order.getOrderItems();
        if (items != null) {
            for (OrderItem item : items) {
                View itemView = LayoutInflater.from(this).inflate(R.layout.item_receipt, itemsContainer, false);

                ImageView itemIcon = itemView.findViewById(R.id.itemIcon);
                TextView itemNameQty = itemView.findViewById(R.id.itemNameAndQty);
                TextView itemPrice = itemView.findViewById(R.id.itemPrice);
                String status = item.getParcelStatus();
                if (status == null) {
                    status = "dine-in";
                }

                switch (status.toLowerCase()) {
                    case "parcel":
                        itemIcon.setImageResource(R.drawable.ic_bag1);
                        itemIcon.setColorFilter(0xFF00B5B0);
                        break;
                    case "pre-parcel":
                        itemIcon.setImageResource(R.drawable.ic_clock1);
                        itemIcon.setColorFilter(0xFF64B5F6);
                        break;
                    default:
                        itemIcon.setImageResource(R.drawable.ic_dine_in);
                        itemIcon.clearColorFilter();
                        break;
                }
                itemNameQty.setText(String.format("%s × %d", item.getName(), item.getQuantity()));
                itemPrice.setText(String.format(Locale.getDefault(), "₹%.2f", item.getPrice() * item.getQuantity()));
                itemsContainer.addView(itemView);
            }
        }
    }
}