package com.simats.foodstall;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import com.simats.foodstall.adapter.OrderItemAdapter;
import com.simats.foodstall.model.OrderItem;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class Uconfirm_orderActivity extends AppCompatActivity implements OrderItemAdapter.OnDataChangedListener {

    private ArrayList<OrderItem> orderItems;
    private TextView subtotalTextView, parcelFeeTextView, totalTextView, addMoreItemsButton;
    private Button proceedToPayButton;
    private LinearLayout parcelFeeLayout;
    private static final double PARCEL_FEE_PER_ITEM = 10.0;
    private LinearLayout preParcelLayout;
    private Spinner pickupTimeSpinner;
    private String stallId;
    private FrameLayout loadingOverlay;
    private ImageView loadingIcon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.uconfirm_order);

        orderItems = getIntent().getParcelableArrayListExtra("CART_ITEMS");
        if (orderItems == null) {
            orderItems = new ArrayList<>();
        }
        stallId = getIntent().getStringExtra("STALL_ID");

        bindViews();

        RecyclerView orderItemsRecyclerView = findViewById(R.id.orderItemsRecyclerView);
        orderItemsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        // The OrderItemAdapter you already updated will now work correctly here
        OrderItemAdapter adapter = new OrderItemAdapter(orderItems, this, this);
        orderItemsRecyclerView.setAdapter(adapter);

        addMoreItemsButton.setOnClickListener(v -> finish());
        proceedToPayButton.setOnClickListener(v -> proceedToPayment());

        updateBillDetails();
    }

    private void bindViews() {
        subtotalTextView = findViewById(R.id.subtotalTextView);
        parcelFeeTextView = findViewById(R.id.parcelFeeTextView);
        totalTextView = findViewById(R.id.totalTextView);
        proceedToPayButton = findViewById(R.id.proceedToPayButton);
        parcelFeeLayout = findViewById(R.id.parcelFeeLayout);
        addMoreItemsButton = findViewById(R.id.addMoreItemsButton);
        preParcelLayout = findViewById(R.id.preParcelLayout);
        pickupTimeSpinner = findViewById(R.id.pickupTimeSpinner);
        loadingOverlay = findViewById(R.id.loadingOverlay);
        loadingIcon = findViewById(R.id.loadingIcon);
    }

    private void proceedToPayment() {
        if (preParcelLayout.getVisibility() == View.VISIBLE && pickupTimeSpinner.getSelectedItem() == null) {
            Toast.makeText(this, "Please select a pickup time.", Toast.LENGTH_SHORT).show();
            return;
        }

        showLoadingOverlay(() -> {
            Intent intent = new Intent(Uconfirm_orderActivity.this, Upayment_methodActivity.class);
            intent.putParcelableArrayListExtra("FINAL_ORDER_ITEMS", orderItems);
            intent.putExtra("STALL_ID", stallId);
            if (preParcelLayout.getVisibility() == View.VISIBLE) {
                String selectedTime = pickupTimeSpinner.getSelectedItem().toString();
                intent.putExtra("PICKUP_TIME", selectedTime);
            }
            startActivity(intent);
        });
    }

    @Override
    public void finish() {
        Intent data = new Intent();
        data.putParcelableArrayListExtra("UPDATED_CART_ITEMS", orderItems);
        setResult(RESULT_OK, data);
        super.finish();
    }

    // --- START OF MODIFIED SECTION ---
    // This entire method has been updated to use getParcelStatus(), fixing the error.
    private void updateBillDetails() {
        if (orderItems.isEmpty()) {
            Toast.makeText(this, "Cart is now empty!", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        double subtotal = 0;
        double totalParcelFee = 0;
        boolean hasPreParcelItem = false;

        for (OrderItem item : orderItems) {
            subtotal += item.getPrice() * item.getQuantity();

            String status = item.getParcelStatus();
            if (status != null) {
                // Check if parcel fee applies
                if (status.equalsIgnoreCase("Parcel") || status.equalsIgnoreCase("Pre-parcel")) {
                    totalParcelFee += (PARCEL_FEE_PER_ITEM * item.getQuantity());
                }
                // Check if the pre-parcel layout should be potentially visible
                if (status.equalsIgnoreCase("Pre-parcel")) {
                    hasPreParcelItem = true;
                }
            }
        }

        if (totalParcelFee > 0) {
            parcelFeeLayout.setVisibility(View.VISIBLE);
            parcelFeeTextView.setText(String.format(Locale.getDefault(), "₹%.2f", totalParcelFee));
        } else {
            parcelFeeLayout.setVisibility(View.GONE);
        }

        // Only show the pre-parcel layout if an item is marked as such AND a time has been set.
        // The time is set via the onEndTimeSet listener.
        if (!hasPreParcelItem) {
            preParcelLayout.setVisibility(View.GONE);
        }

        double total = subtotal + totalParcelFee;
        subtotalTextView.setText(String.format(Locale.getDefault(), "₹%.2f", subtotal));
        totalTextView.setText(String.format(Locale.getDefault(), "₹%.2f", total));
        proceedToPayButton.setText(String.format(Locale.getDefault(), "Proceed to Pay ₹%.2f", total));
    }
    // --- END OF MODIFIED SECTION ---

    @Override
    public void onDataChanged() {
        updateBillDetails();
    }

    @Override
    public void onEndTimeSet(String endTime) {
        // This is called from the adapter when the user sets a "Ready By" time
        generateAndShowPickupTimes(endTime);
    }

    private void generateAndShowPickupTimes(String endTime) {
        if (endTime == null || endTime.isEmpty()) {
            preParcelLayout.setVisibility(View.GONE);
            return;
        }

        List<String> timeSlots = new ArrayList<>();
        SimpleDateFormat sdf = new SimpleDateFormat("h:mm a", Locale.US);
        try {
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(sdf.parse(endTime));

            calendar.add(Calendar.MINUTE, 5);

            for (int i = 0; i < 6; i++) {
                timeSlots.add(sdf.format(calendar.getTime()));
                calendar.add(Calendar.MINUTE, 5);
            }

            preParcelLayout.setVisibility(View.VISIBLE);
            ArrayAdapter<String> adapter = new ArrayAdapter<>(Uconfirm_orderActivity.this, R.layout.dropdown_item, timeSlots);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            pickupTimeSpinner.setAdapter(adapter);

        } catch (ParseException e) {
            e.printStackTrace();
            Toast.makeText(this, "Invalid time format.", Toast.LENGTH_SHORT).show();
            preParcelLayout.setVisibility(View.GONE);
        }
    }

    private void showLoadingOverlay(Runnable onComplete) {
        loadingOverlay.setVisibility(View.VISIBLE);
        Animation rotation = AnimationUtils.loadAnimation(this, R.anim.hourglass_rotation);
        loadingIcon.startAnimation(rotation);
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            if (loadingIcon != null) loadingIcon.clearAnimation();
            if (loadingOverlay != null) loadingOverlay.setVisibility(View.GONE);
            if (onComplete != null) {
                onComplete.run();
            }
        }, 800);
    }
}