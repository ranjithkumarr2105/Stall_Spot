package com.simats.foodstall;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.ActionMode;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.gson.Gson;
import com.simats.foodstall.adapter.UserOrdersAdapter;
import com.simats.foodstall.model.StatusResponse;
import com.simats.foodstall.model.UserOrder;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UordersActivity extends AppCompatActivity implements UserOrdersAdapter.OnOrderClickListener {

    private RecyclerView ordersRecyclerView;
    private UserOrdersAdapter adapter;
    private String studentId;

    private List<UserOrder> fullOrderList = new ArrayList<>();
    private List<UserOrder> filteredOrderList = new ArrayList<>();

    private boolean isInSelectionMode = false;
    private ActionMode actionMode;
    private ActionMode.Callback actionModeCallback;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.uorders);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ordersRecyclerView = findViewById(R.id.ordersRecyclerView);

        SharedPreferences prefs = getSharedPreferences("StallSpotPrefs", MODE_PRIVATE);
        studentId = prefs.getString("STUDENT_ID", null);

        setupBottomNavigation();
        setupRecyclerView();
        setupActionMode();
        fetchOrderHistory();
    }

    private void setupRecyclerView() {
        ordersRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new UserOrdersAdapter(filteredOrderList, this);
        ordersRecyclerView.setAdapter(adapter);
    }

    private void fetchOrderHistory() {
        if (studentId == null) {
            Toast.makeText(this, "Could not find student ID. Please log in again.", Toast.LENGTH_SHORT).show();
            return;
        }

        ApiClient.getClient().create(ApiService.class).getUserOrderHistory(studentId).enqueue(new Callback<List<UserOrder>>() {
            @Override
            public void onResponse(@NonNull Call<List<UserOrder>> call, @NonNull Response<List<UserOrder>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    fullOrderList.clear();
                    fullOrderList.addAll(response.body());
                    filterOrders("");

                    if(fullOrderList.isEmpty()){
                        Toast.makeText(UordersActivity.this, "You have no past orders.", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(UordersActivity.this, "Failed to load order history.", Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onFailure(@NonNull Call<List<UserOrder>> call, @NonNull Throwable t) {
                Log.e("UordersActivity", "Network error", t);
                Toast.makeText(UordersActivity.this, "Network error. Please try again.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.uorders_menu, menu);
        MenuItem searchItem = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) searchItem.getActionView();
        searchView.setQueryHint("Search by Stall or Order ID...");

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                filterOrders(newText);
                return true;
            }
        });
        return true;
    }

    private void filterOrders(String query) {
        filteredOrderList.clear();
        if (query.isEmpty()) {
            filteredOrderList.addAll(fullOrderList);
        } else {
            String lowerCaseQuery = query.toLowerCase(Locale.getDefault());
            for (UserOrder order : fullOrderList) {
                if (order.getStallName().toLowerCase(Locale.getDefault()).contains(lowerCaseQuery) ||
                        order.getDisplayOrderId().toLowerCase(Locale.getDefault()).contains(lowerCaseQuery)) {
                    filteredOrderList.add(order);
                }
            }
        }
        adapter.notifyDataSetChanged();
    }

    private void setupActionMode() {
        actionModeCallback = new ActionMode.Callback() {
            @Override
            public boolean onCreateActionMode(ActionMode mode, Menu menu) {
                mode.getMenuInflater().inflate(R.menu.contextual_delete_menu, menu);
                return true;
            }

            @Override
            public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
                return false;
            }

            @Override
            public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
                if (item.getItemId() == R.id.action_delete) {
                    showDeleteConfirmationDialog();
                    return true;
                }
                return false;
            }

            @Override
            public void onDestroyActionMode(ActionMode mode) {
                isInSelectionMode = false;
                adapter.setInSelectionMode(false);
                actionMode = null;
            }
        };
    }

    private void setupBottomNavigation() {
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation_bar);
        bottomNavigationView.setSelectedItemId(R.id.nav_orders);
        bottomNavigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.nav_home) {
                startActivity(new Intent(getApplicationContext(), UhomeActivity.class));
                overridePendingTransition(0, 0);
                finish();
                return true;
            } else if (itemId == R.id.nav_orders) {
                return true;
            } else if (itemId == R.id.nav_wallet) {
                startActivity(new Intent(getApplicationContext(), UwalletActivity.class));
                overridePendingTransition(0, 0);
                finish();
                return true;
            } else if (itemId == R.id.nav_profile) {
                startActivity(new Intent(getApplicationContext(), UeditprofileActivity.class));
                overridePendingTransition(0, 0);
                finish();
                return true;
            }
            return false;
        });
    }

    @Override
    public void onViewReceiptClick(UserOrder order) {
        if (isInSelectionMode) return;
        Intent intent = new Intent(this, UDetailedReceiptActivity.class);
        intent.putExtra("ORDER_DETAILS", order);
        startActivity(intent);
    }

    @Override
    public void onItemClick(int position) {
        if (isInSelectionMode) {
            toggleSelection(position);
        }
    }

    @Override
    public void onItemLongClick(int position) {
        if (!isInSelectionMode) {
            isInSelectionMode = true;
            adapter.setInSelectionMode(true);
            actionMode = startSupportActionMode(actionModeCallback);
            toggleSelection(position);
        }
    }

    private void toggleSelection(int position) {
        adapter.toggleSelection(position);
        int count = adapter.getSelectedItemCount();
        if (count == 0 && actionMode != null) {
            actionMode.finish();
        } else if (actionMode != null) {
            actionMode.setTitle(count + " selected");
            actionMode.invalidate();
        }
    }

    private void showDeleteConfirmationDialog() {
        int count = adapter.getSelectedItemCount();
        new AlertDialog.Builder(this)
                .setTitle("Delete Orders")
                .setMessage("Are you sure you want to delete these " + count + " orders? This action cannot be undone.")
                .setPositiveButton("Delete", (dialog, which) -> deleteSelectedOrders())
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void deleteSelectedOrders() {
        List<UserOrder> selectedOrders = adapter.getSelectedItems();
        if (studentId == null || selectedOrders.isEmpty()) {
            return;
        }

        List<Integer> orderIdsToDelete = selectedOrders.stream()
                .map(UserOrder::getOrderId)
                .collect(Collectors.toList());

        String orderIdsJson = new Gson().toJson(orderIdsToDelete);

        ApiClient.getClient().create(ApiService.class).deleteUserOrders(studentId, orderIdsJson).enqueue(new Callback<StatusResponse>() {
            @Override
            public void onResponse(@NonNull Call<StatusResponse> call, @NonNull Response<StatusResponse> response) {
                if(response.isSuccessful() && response.body() != null && "success".equals(response.body().getStatus())) {
                    Toast.makeText(UordersActivity.this, "Orders deleted successfully", Toast.LENGTH_SHORT).show();
                    fetchOrderHistory();
                } else {
                    Toast.makeText(UordersActivity.this, "Failed to delete orders", Toast.LENGTH_SHORT).show();
                }
                if (actionMode != null) {
                    actionMode.finish();
                }
            }

            @Override
            public void onFailure(@NonNull Call<StatusResponse> call, @NonNull Throwable t) {
                Toast.makeText(UordersActivity.this, "Network error while deleting", Toast.LENGTH_SHORT).show();
                if (actionMode != null) {
                    actionMode.finish();
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        if (isInSelectionMode) {
            if (actionMode != null) {
                actionMode.finish();
            }
        } else {
            super.onBackPressed();
        }
    }
}