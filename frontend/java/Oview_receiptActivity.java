package com.simats.foodstall;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.simats.foodstall.model.DetailedOrder;
import com.simats.foodstall.model.ReceiptItem;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class Oview_receiptActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.oview_receipt);

        // Setup listeners for both back buttons for robust navigation
        findViewById(R.id.backButton).setOnClickListener(v -> onBackPressed());

        // Populate the receipt with details
        populateReceiptDetails();
    }

    private void populateReceiptDetails() {
        // --- In a real app, you would deserialize an Order object from the Intent ---
        // Dummy Data updated to test the new parcel logic correctly.
        List<ReceiptItem> items = new ArrayList<>();
        // This item has a parcel fee. Quantity is 2, so fee should be 2 * 10 = ₹20
        items.add(new ReceiptItem("Chicken Roll", 2, 40.00, true));
        // This item has NO parcel fee.
        items.add(new ReceiptItem("Tea", 3, 15.00, false));
        // This item has a parcel fee. Quantity is 1, so fee should be 1 * 10 = ₹10
        items.add(new ReceiptItem("Water Bottle", 1, 20.00, true));
        // TOTAL PARCEL FEE should be 20 + 10 = ₹30

        DetailedOrder order = new DetailedOrder(
                "ORD104780",
                "10 Jun, 9:00 AM",
                "Sivan Unavagam",
                "S36500654",
                "OWNER123",
                "GPay",
                "Pre-parcel", // Set order type to "Pre-parcel"
                "10 Jun, 10:00 AM",
                items
        );

        // Find all necessary views from the layout
        TextView orderIdTextView = findViewById(R.id.orderIdTextView);
        TextView studentIdTextView = findViewById(R.id.studentIdTextView);
        TextView stallNameTextView = findViewById(R.id.stallNameTextView);
        TextView orderTimeTextView = findViewById(R.id.orderTimeTextView);
        TextView paymentMethodTextView = findViewById(R.id.paymentMethodTextView);
        LinearLayout itemsContainer = findViewById(R.id.itemsContainer);
        TextView subtotalTextView = findViewById(R.id.subtotalTextView);
        TextView totalPaidTextView = findViewById(R.id.totalPaidTextView);

        LinearLayout parcelFeeLayout = findViewById(R.id.parcelFeeLayout);
        TextView parcelFeeTextView = findViewById(R.id.parcelFeeTextView);
        LinearLayout parcelEndTimeLayout = findViewById(R.id.parcelEndTimeLayout);
        TextView parcelEndTimeTextView = findViewById(R.id.parcelEndTimeTextView);

        // Populate header details from the order object
        orderIdTextView.setText(order.getOrderId());
        studentIdTextView.setText(order.getStudentId());
        stallNameTextView.setText(order.getStallName());
        orderTimeTextView.setText(order.getOrderTime());
        paymentMethodTextView.setText(order.getPaymentMethod());

        // Clear any old items before populating
        itemsContainer.removeAllViews();
        double subtotal = 0;
        double totalParcelFee = 0;
        final double PARCEL_FEE_PER_ITEM_QUANTITY = 10.00; // Define the fee as a constant

        // *** CORRECTED LOGIC START ***
        for (ReceiptItem item : order.getItems()) {
            double itemLineTotal = item.getPrice() * item.getQuantity();
            subtotal += itemLineTotal;

            // Check EACH item to see if it has a parcel fee
            if (item.isParcel()) {
                // Calculate fee based on this item's quantity ONLY
                totalParcelFee += item.getQuantity() * PARCEL_FEE_PER_ITEM_QUANTITY;
            }

            // Add the formatted row to the receipt display
            String itemNameAndQty = item.getName() + " x " + item.getQuantity();
            addItemToReceipt(itemsContainer, itemNameAndQty, itemLineTotal);
        }
        // *** CORRECTED LOGIC END ***

        // Display Parcel Fee section only if a fee was calculated
        if (totalParcelFee > 0) {
            parcelFeeLayout.setVisibility(View.VISIBLE);
            parcelFeeTextView.setText(String.format(Locale.getDefault(), "₹%.2f", totalParcelFee));
        } else {
            parcelFeeLayout.setVisibility(View.GONE);
        }

        // Display "Pickup Before" section only for "Pre-parcel" orders
        if ("Pre-parcel".equalsIgnoreCase(order.getParcelType())) {
            parcelEndTimeLayout.setVisibility(View.VISIBLE);
            parcelEndTimeTextView.setText(order.getParcelEndTime());
        } else {
            parcelEndTimeLayout.setVisibility(View.GONE);
        }

        // Calculate and display the final totals
        double totalPaid = subtotal + totalParcelFee;
        subtotalTextView.setText(String.format(Locale.getDefault(), "₹%.2f", subtotal));
        totalPaidTextView.setText(String.format(Locale.getDefault(), "₹%.2f", totalPaid));
    }

    /**
     * Inflates a new item row and adds it to the container.
     * @param container The LinearLayout to add the new row to.
     * @param itemName The formatted name and quantity of the item.
     * @param itemPrice The total price for that line item.
     */
    private void addItemToReceipt(LinearLayout container, String itemName, double itemPrice) {
        LayoutInflater inflater = LayoutInflater.from(this);
        View itemRow = inflater.inflate(R.layout.item_receipt_row, container, false);

        TextView itemNameTextView = itemRow.findViewById(R.id.itemNameTextView);
        TextView itemPriceTextView = itemRow.findViewById(R.id.itemPriceTextView);

        itemNameTextView.setText(itemName);
        itemPriceTextView.setText(String.format(Locale.getDefault(), "₹%.2f", itemPrice));

        container.addView(itemRow);
    }
}