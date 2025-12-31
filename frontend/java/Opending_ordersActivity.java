package com.simats.foodstall;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout; // Import SwipeRefreshLayout

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

import com.simats.foodstall.adapter.OwnerPendingOrdersAdapter;
import com.simats.foodstall.model.OGetOrdersResponse;
import com.simats.foodstall.model.OOrder;
import com.simats.foodstall.model.StatusResponse;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Opending_ordersActivity extends AppCompatActivity implements OwnerPendingOrdersAdapter.OnOrderActionListener {

    private RecyclerView pendingOrdersRecyclerView;
    private OwnerPendingOrdersAdapter adapter;
    private List<OOrder> allPendingOrders = new ArrayList<>();
    private EditText searchEditText;
    private FrameLayout loadingOverlay;
    private ImageView loadingIcon;
    private TextView emptyView;
    private SwipeRefreshLayout swipeRefreshLayout; // Add SwipeRefreshLayout variable
    private String stallId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.opending_orders);

        // --- BUG FIX: Reading from the correct SharedPreferences file ---
        SharedPreferences prefs = getSharedPreferences("owner_prefs", MODE_PRIVATE);
        stallId = prefs.getString("stall_id", null);

        if (stallId == null) {
            Toast.makeText(this, "Stall ID not found. Please log in again.", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        bindViews();
        setupRecyclerView();
        setupSearch();
        setupRefreshListener();
        fetchPendingOrders(); // Initial fetch
    }

    private void bindViews() {
        findViewById(R.id.backButton).setOnClickListener(v -> onBackPressed());
        searchEditText = findViewById(R.id.searchEditText);
        pendingOrdersRecyclerView = findViewById(R.id.pendingOrdersRecyclerView);
        loadingOverlay = findViewById(R.id.loadingOverlay);
        loadingIcon = findViewById(R.id.loadingIcon);
        emptyView = findViewById(R.id.emptyView);
        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout); // Bind SwipeRefreshLayout
    }

    private void setupRefreshListener() {
        swipeRefreshLayout.setOnRefreshListener(this::fetchPendingOrders);
    }

    private void setupRecyclerView() {
        pendingOrdersRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new OwnerPendingOrdersAdapter(this);
        pendingOrdersRecyclerView.setAdapter(adapter);
    }

    private void setupSearch() {
        searchEditText.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) { filter(s.toString()); }
            @Override public void afterTextChanged(Editable s) {}
        });
    }

    private void filter(String text) {
        String lowerCaseQuery = text.toLowerCase();
        if (text.isEmpty()) {
            adapter.submitList(allPendingOrders);
            return;
        }
        List<OOrder> filteredList = new ArrayList<>();
        for (OOrder order : allPendingOrders) {
            boolean idMatches = order.getDisplayOrderId() != null && order.getDisplayOrderId().toLowerCase().contains(lowerCaseQuery);
            boolean studentIdMatches = order.getStudentId() != null && order.getStudentId().toLowerCase().contains(lowerCaseQuery);
            if (idMatches || studentIdMatches) {
                filteredList.add(order);
            }
        }
        adapter.submitList(filteredList);
    }

    private void fetchPendingOrders() {
        if (!swipeRefreshLayout.isRefreshing()) {
            showLoading(true);
        }

        ApiClient.getClient().create(ApiService.class).getOwnerOrders(stallId, "pending")
                .enqueue(new Callback<OGetOrdersResponse>() {
                    @Override
                    public void onResponse(@NonNull Call<OGetOrdersResponse> call, @NonNull Response<OGetOrdersResponse> response) {
                        showLoading(false);
                        if (response.isSuccessful() && response.body() != null && "success".equals(response.body().getStatus())) {
                            allPendingOrders.clear();
                            if (response.body().getOrders() != null) {
                                allPendingOrders.addAll(response.body().getOrders());
                            }
                            adapter.submitList(allPendingOrders);
                        } else {
                            emptyView.setText("Failed to load pending orders.");
                        }
                        updateEmptyView();
                    }

                    @Override
                    public void onFailure(@NonNull Call<OGetOrdersResponse> call, @NonNull Throwable t) {
                        showLoading(false);
                        emptyView.setText("Network Error.");
                        updateEmptyView();
                    }
                });
    }

    private void updateOrderStatus(OOrder order, String newStatus) {
        showLoading(true);
        ApiClient.getClient().create(ApiService.class)
                .updateOwnerOrderStatus(stallId, order.getDisplayOrderId(), newStatus)
                .enqueue(new Callback<StatusResponse>() {
                    @Override
                    public void onResponse(@NonNull Call<StatusResponse> call, @NonNull Response<StatusResponse> response) {
                        showLoading(false);
                        if (response.isSuccessful() && response.body() != null && "success".equals(response.body().getStatus())) {
                            Toast.makeText(Opending_ordersActivity.this, "Order " + newStatus, Toast.LENGTH_SHORT).show();
                            allPendingOrders.remove(order);
                            adapter.submitList(new ArrayList<>(allPendingOrders)); // Update list after removal
                            updateEmptyView();
                        } else {
                            // --- START: Missing Else Block ---
                            String errorMessage = "Failed to update order status.";
                            // Try to get a more specific error from the server response if possible
                            if (response.errorBody() != null) {
                                try {
                                    // Assuming error response is like {"status":"error", "message":"..."}
                                    JSONObject errorObj = new JSONObject(response.errorBody().string());
                                    errorMessage = errorObj.optString("message", errorMessage);
                                } catch (Exception e) {
                                    Log.e("OpendingOrders", "Error parsing error body", e);
                                }
                            } else if (response.body() != null) {
                                // If body exists but status wasn't "success"
                                errorMessage = response.body().getMessage();
                            }
                            Log.e("OpendingOrders", "Update Status Failed - Code: " + response.code() + ", Msg: " + errorMessage);
                            Toast.makeText(Opending_ordersActivity.this, errorMessage, Toast.LENGTH_LONG).show();
                            // --- END: Missing Else Block ---
                        }
                    }
                    @Override
                    public void onFailure(@NonNull Call<StatusResponse> call, @NonNull Throwable t) {
                        showLoading(false);
                        Toast.makeText(Opending_ordersActivity.this, "Network Error.", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void updateEmptyView() {
        if (allPendingOrders.isEmpty()) {
            emptyView.setVisibility(View.VISIBLE);
        } else {
            emptyView.setVisibility(View.GONE);
        }
    }

    @Override
    public void onApproveClicked(OOrder order) {
        updateOrderStatus(order, "Delivered");
    }

    @Override
    public void onRejectClicked(OOrder order) {
        updateOrderStatus(order, "Rejected");
    }

    private void showLoading(boolean show) {
        if (show) {
            loadingOverlay.setVisibility(View.VISIBLE);
            Animation rotation = AnimationUtils.loadAnimation(this, R.anim.hourglass_rotation);
            loadingIcon.startAnimation(rotation);
        } else {
            loadingIcon.clearAnimation();
            loadingOverlay.setVisibility(View.GONE);
            if (swipeRefreshLayout.isRefreshing()) {
                swipeRefreshLayout.setRefreshing(false);
            }
        }
    }
}