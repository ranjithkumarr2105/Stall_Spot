package com.simats.foodstall;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.simats.foodstall.model.OrderItem;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class UreceiptActivity extends AppCompatActivity {

    private ArrayList<OrderItem> orderItems;
    private double totalAmount, subtotal, parcelFee;
    private String paymentMethod, displayOrderId, pickupTime;

    private LinearLayout itemsContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ureceipt);

        itemsContainer = findViewById(R.id.itemsContainer);

        Intent intent = getIntent();
        orderItems = intent.getParcelableArrayListExtra("RECEIPT_ORDER_ITEMS");
        totalAmount = intent.getDoubleExtra("TOTAL_AMOUNT", 0.0);
        subtotal = intent.getDoubleExtra("SUBTOTAL", 0.0);
        parcelFee = intent.getDoubleExtra("PARCEL_FEE", 0.0);
        paymentMethod = intent.getStringExtra("PAYMENT_METHOD");
        displayOrderId = intent.getStringExtra("DISPLAY_ORDER_ID");
        pickupTime = intent.getStringExtra("PICKUP_TIME");

        if (orderItems == null || displayOrderId == null) {
            Toast.makeText(this, "Error: Could not load receipt details.", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        populateReceiptDetails();

        findViewById(R.id.viewOrdersButton).setOnClickListener(v -> {
            startActivity(new Intent(this, UordersActivity.class));
            finish();
        });

        findViewById(R.id.backToHomeButton).setOnClickListener(v -> {
            Intent homeIntent = new Intent(this, UhomeActivity.class);
            homeIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(homeIntent);
            finish();
        });
    }

    private void populateReceiptDetails() {
        setRowText(R.id.orderIdRow, "Order ID", displayOrderId);

        String currentTime = new SimpleDateFormat("dd MMM, h:mm a", Locale.getDefault()).format(new Date());
        setRowText(R.id.orderTimeRow, "Order Time", currentTime);

        setRowText(R.id.paymentMethodRow, "Paid via", paymentMethod);

        View pickupTimeRow = findViewById(R.id.pickupTimeRow);
        if (pickupTime != null && !pickupTime.isEmpty()) {
            pickupTimeRow.setVisibility(View.VISIBLE);
            setRowText(R.id.pickupTimeRow, "Pickup Time", pickupTime);
        } else {
            pickupTimeRow.setVisibility(View.GONE);
        }

        itemsContainer.removeAllViews();
        LayoutInflater inflater = LayoutInflater.from(this);

        // --- START OF MODIFIED SECTION ---
        for (OrderItem item : orderItems) {
            View itemView = inflater.inflate(R.layout.receipt_row, itemsContainer, false);
            ImageView itemIcon = itemView.findViewById(R.id.itemIcon); // Get the new ImageView
            TextView itemNameQty = itemView.findViewById(R.id.labelTextView);
            TextView itemPrice = itemView.findViewById(R.id.valueTextView);

            // Set icon based on the item's parcelStatus
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
                    default: // "dine-in"
                        itemIcon.setImageResource(R.drawable.ic_dine_in);
                        break;
                }
            }

            String itemDescription = String.format(Locale.getDefault(), "%s × %d", item.getName(), item.getQuantity());

            itemNameQty.setText(itemDescription);
            itemPrice.setText(String.format(Locale.getDefault(), "₹%.2f", item.getPrice() * item.getQuantity()));
            itemsContainer.addView(itemView);
        }
        // --- END OF MODIFIED SECTION ---

        setRowText(R.id.subtotalRow, "Subtotal", String.format(Locale.getDefault(), "₹%.2f", subtotal));

        View parcelFeeRow = findViewById(R.id.parcelFeeRow);
        if (parcelFee > 0) {
            parcelFeeRow.setVisibility(View.VISIBLE);
            setRowText(R.id.parcelFeeRow, "Parcel Fee", String.format(Locale.getDefault(), "₹%.2f", parcelFee));
        } else {
            parcelFeeRow.setVisibility(View.GONE);
        }
        setRowText(R.id.totalPaidRow, "Total Paid", String.format(Locale.getDefault(), "₹%.2f", totalAmount));
    }

    private void setRowText(int rowViewId, String label, String value) {
        View row = findViewById(rowViewId);
        if (row != null) {
            TextView labelView = row.findViewById(R.id.labelTextView);
            TextView valueView = row.findViewById(R.id.valueTextView);
            if (labelView != null) labelView.setText(label);
            if (valueView != null) valueView.setText(value);
        }
    }
}