package com.simats.foodstall;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.google.android.material.appbar.MaterialToolbar;
import com.simats.foodstall.model.UserOrder;
import com.simats.foodstall.model.UserOrderItem;

import java.util.Locale;

public class UDetailedReceiptActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.udetailed_receipt);

        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setNavigationOnClickListener(v -> finish());

        Button backButton = findViewById(R.id.backButton);
        backButton.setOnClickListener(v -> finish());

        UserOrder order = getIntent().getParcelableExtra("ORDER_DETAILS");

        if (order == null) {
            Toast.makeText(this, "Error: Could not load receipt details.", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        populateReceiptDetails(order);
    }

    private void populateReceiptDetails(UserOrder order) {
        // Existing Views
        TextView orderIdText = findViewById(R.id.receiptOrderId);
        TextView orderTimeText = findViewById(R.id.receiptOrderTime);
        TextView stallNameText = findViewById(R.id.receiptStallName);
        TextView paymentMethodText = findViewById(R.id.receiptPaymentMethod);
        LinearLayout itemsContainer = findViewById(R.id.receiptItemsContainer);
        TextView subtotalText = findViewById(R.id.receiptSubtotal);
        TextView parcelFeeText = findViewById(R.id.receiptParcelFee);
        View parcelFeeLayout = findViewById(R.id.receiptParcelFeeLayout);
        TextView totalPaidText = findViewById(R.id.receiptTotalPaid);
        TextView pickupTimeText = findViewById(R.id.receiptPickupTime);
        View pickupTimeLayout = findViewById(R.id.receiptPickupTimeLayout);

        // --- NEW VIEWS FOR REFUND DETAILS ---
        LinearLayout refundDetailsLayout = findViewById(R.id.refundDetailsLayout);
        TextView receiptStatusText = findViewById(R.id.receiptStatus);
        TextView refundMethodText = findViewById(R.id.receiptRefundMethod);
        TextView refundTimeText = findViewById(R.id.receiptRefundTime);

        // Populate existing views
        orderIdText.setText(order.getDisplayOrderId());
        orderTimeText.setText(order.getOrderDate());
        stallNameText.setText(order.getStallName());
        paymentMethodText.setText(order.getPaymentMethod());
        subtotalText.setText(String.format(Locale.getDefault(), "₹%.2f", order.getSubtotal()));

        if (order.getParcelFee() > 0) {
            parcelFeeLayout.setVisibility(View.VISIBLE);
            parcelFeeText.setText(String.format(Locale.getDefault(), "₹%.2f", order.getParcelFee()));
        } else {
            parcelFeeLayout.setVisibility(View.GONE);
        }

        String pickupTime = order.getPickupTime();
        if (pickupTime != null && !pickupTime.isEmpty() && !pickupTime.equalsIgnoreCase("00:00:00")) {
            pickupTimeLayout.setVisibility(View.VISIBLE);
            pickupTimeText.setText(pickupTime);
        } else {
            pickupTimeLayout.setVisibility(View.GONE);
        }

        // --- NEW LOGIC TO SHOW/HIDE REFUND DETAILS ---
        if ("Rejected".equalsIgnoreCase(order.getOrderStatus())) {
            refundDetailsLayout.setVisibility(View.VISIBLE);
            receiptStatusText.setText(order.getOrderStatus());
            refundMethodText.setText("In-App Wallet"); // As per our refund logic

            String refundTime = order.getRefundTimestamp();
            if (refundTime != null && !refundTime.isEmpty()) {
                refundTimeText.setText(refundTime);
            } else {
                refundTimeText.setText("N/A");
            }

            totalPaidText.setText(String.format(Locale.getDefault(), "₹%.2f (Refunded)", order.getTotalAmount()));
            totalPaidText.setTextColor(ContextCompat.getColor(this, R.color.status_rejected_red));
        } else {
            refundDetailsLayout.setVisibility(View.GONE);
            totalPaidText.setText(String.format(Locale.getDefault(), "₹%.2f", order.getTotalAmount()));
            totalPaidText.setTextColor(ContextCompat.getColor(this, R.color.text_primary)); // Reset to default color
        }
        // --- END OF NEW LOGIC ---

        // Populate items (This part is unchanged)
        itemsContainer.removeAllViews();
        LayoutInflater inflater = LayoutInflater.from(this);
        for (UserOrderItem item : order.getItems()) {
            View itemView = inflater.inflate(R.layout.receipt_row, itemsContainer, false);

            ImageView itemIcon = itemView.findViewById(R.id.itemIcon);
            TextView itemNameQty = itemView.findViewById(R.id.labelTextView);
            TextView itemPrice = itemView.findViewById(R.id.valueTextView);

            String status = item.getParcelStatus();
            if (status != null) {
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
                        break;
                }
            }
            itemNameQty.setText(String.format(Locale.getDefault(), "%s × %d", item.getName(), item.getQuantity()));
            itemPrice.setText(String.format(Locale.getDefault(), "₹%.2f", item.getPrice() * item.getQuantity()));
            itemsContainer.addView(itemView);
        }
    }
}