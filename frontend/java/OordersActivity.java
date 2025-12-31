package com.simats.foodstall;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.simats.foodstall.adapter.OrderHistoryAdapter;
import com.simats.foodstall.model.OOrder;
import com.simats.foodstall.model.OrderItem;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OordersActivity extends AppCompatActivity {

    private RecyclerView ordersRecyclerView;
    private OrderHistoryAdapter adapter;
    private EditText searchEditText;
    private TextView datePickerTextView;
    private CardView datePickerCard;
    private TextView dineInOrderCount, parcelOrderCount, preParcelOrderCount;
    private FrameLayout loadingOverlay;
    private ImageView loadingIcon;

    // MODIFIED: Renamed variable for clarity
    private List<OOrder> allFetchedOrdersForSelectedDate = new ArrayList<>();
    private final Calendar selectedDate = Calendar.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.oorders);

        bindViews();

        ordersRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new OrderHistoryAdapter(new ArrayList<>());
        ordersRecyclerView.setAdapter(adapter);

        updateDateDisplay();
        setupDatePicker();
        setupSearchListener();
        setupBottomNavigation();

        loadOrderDataFromServer();
    }

    private void bindViews() {
        ordersRecyclerView = findViewById(R.id.ordersRecyclerView);
        searchEditText = findViewById(R.id.searchEditText);
        datePickerTextView = findViewById(R.id.datePickerTextView);
        datePickerCard = findViewById(R.id.datePickerCard);
        dineInOrderCount = findViewById(R.id.dineInOrderCount);
        parcelOrderCount = findViewById(R.id.parcelOrderCount);
        preParcelOrderCount = findViewById(R.id.preParcelOrderCount);
        loadingOverlay = findViewById(R.id.loadingOverlay);
        loadingIcon = findViewById(R.id.loadingIcon);
    }

    private void setupDatePicker() {
        datePickerCard.setOnClickListener(v -> {
            DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                    (view, year, month, dayOfMonth) -> {
                        selectedDate.set(year, month, dayOfMonth);
                        updateDateDisplay();
                        loadOrderDataFromServer();
                    },
                    selectedDate.get(Calendar.YEAR),
                    selectedDate.get(Calendar.MONTH),
                    selectedDate.get(Calendar.DAY_OF_MONTH)
            );
            datePickerDialog.show();
        });
    }

    private void setupSearchListener() {
        searchEditText.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) { applyFilters(); }
            @Override public void afterTextChanged(Editable s) {}
        });
    }

    private void updateDateDisplay() {
        SimpleDateFormat sdf = new SimpleDateFormat("MMMM dd, yyyy", Locale.getDefault());
        datePickerTextView.setText(sdf.format(selectedDate.getTime()));
    }

    private void loadOrderDataFromServer() {
        loadingOverlay.setVisibility(View.VISIBLE);
        Animation rotation = AnimationUtils.loadAnimation(this, R.anim.hourglass_rotation);
        loadingIcon.startAnimation(rotation);

        SharedPreferences sharedPreferences = getSharedPreferences("owner_prefs", MODE_PRIVATE);
        String stallId = sharedPreferences.getString("stall_id", null);
        if (stallId == null) {
            Toast.makeText(this, "Error: Stall ID not found. Please log in again.", Toast.LENGTH_LONG).show();
            loadingOverlay.setVisibility(View.GONE);
            loadingIcon.clearAnimation();
            return;
        }

        SimpleDateFormat apiDateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        String dateString = apiDateFormat.format(selectedDate.getTime());

        ApiService apiService = ApiClient.getClient().create(ApiService.class);
        Call<List<OOrder>> call = apiService.getOrderHistory(stallId, dateString);

        call.enqueue(new Callback<List<OOrder>>() {
            @Override
            public void onResponse(@NonNull Call<List<OOrder>> call, @NonNull Response<List<OOrder>> response) {
                loadingOverlay.setVisibility(View.GONE);
                loadingIcon.clearAnimation();

                if (response.isSuccessful() && response.body() != null) {
                    // MODIFIED: Store all fetched orders (Delivered and Rejected) directly
                    allFetchedOrdersForSelectedDate.clear();
                    allFetchedOrdersForSelectedDate.addAll(response.body());
                    applyFilters();

                    if (allFetchedOrdersForSelectedDate.isEmpty()) {
                        Toast.makeText(OordersActivity.this, "No orders found for this date.", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    String errorBody = "Unknown error";
                    if (response.errorBody() != null) {
                        try {
                            errorBody = response.errorBody().string();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    Log.e("OordersActivity", "API Error: " + response.code() + " - " + errorBody);
                    Toast.makeText(OordersActivity.this, "Failed to load orders. Server responded with an error.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<OOrder>> call, @NonNull Throwable t) {
                loadingOverlay.setVisibility(View.GONE);
                loadingIcon.clearAnimation();
                Log.e("OordersActivity", "Network Failure", t);
                Toast.makeText(OordersActivity.this, "Network Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void applyFilters() {
        // --- MODIFIED: Separate logic for summary counts and recycler view list ---

        // 1. Calculate Summary Counts (ONLY from Delivered orders)
        int dineInCount = 0;
        int parcelCount = 0;
        int preParcelCount = 0;

        // Filter the list to get only delivered orders for counting
        List<OOrder> deliveredOrdersForSummary = allFetchedOrdersForSelectedDate.stream()
                .filter(order -> "Delivered".equalsIgnoreCase(order.getOrderStatus()))
                .collect(Collectors.toList());

        for (OOrder order : deliveredOrdersForSummary) {
            for (OrderItem item : order.getOrderItems()) {
                String status = item.getParcelStatus();
                if (status == null) {
                    status = "dine-in";
                }
                switch (status.toLowerCase()) {
                    case "parcel":
                        parcelCount++;
                        break;
                    case "pre-parcel":
                        preParcelCount++;
                        break;
                    case "dine-in":
                    default:
                        dineInCount++;
                        break;
                }
            }
        }

        dineInOrderCount.setText(String.format(Locale.getDefault(), "%d Items", dineInCount));
        parcelOrderCount.setText(String.format(Locale.getDefault(), "%d Items", parcelCount));
        preParcelOrderCount.setText(String.format(Locale.getDefault(), "%d Items", preParcelCount));

        // 2. Prepare list for RecyclerView (using ALL fetched orders)
        String searchQuery = searchEditText.getText().toString().toLowerCase(Locale.ROOT).trim();
        List<OOrder> finalList;
        if (searchQuery.isEmpty()) {
            finalList = allFetchedOrdersForSelectedDate;
        } else {
            finalList = allFetchedOrdersForSelectedDate.stream()
                    .filter(order -> order.getDisplayOrderId().toLowerCase(Locale.ROOT).contains(searchQuery))
                    .collect(Collectors.toList());
        }
        adapter.updateList(finalList);
    }

    private void setupBottomNavigation() {
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation_bar);
        bottomNavigationView.setSelectedItemId(R.id.nav_owner_orders);
        bottomNavigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.nav_owner_orders) {
                return true;
            } else if (itemId == R.id.nav_owner_home) {
                startActivity(new Intent(getApplicationContext(), OhomeActivity.class));
                overridePendingTransition(0, 0);
                return true;
            } else if (itemId == R.id.nav_owner_menu) {
                startActivity(new Intent(getApplicationContext(), OmenuActivity.class));
                overridePendingTransition(0, 0);
                return true;
            } else if (itemId == R.id.nav_owner_profile) {
                startActivity(new Intent(getApplicationContext(), OprofileActivity.class));
                overridePendingTransition(0, 0);
                return true;
            }
            return false;
        });
    }
}