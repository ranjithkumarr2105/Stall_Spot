package com.simats.foodstall;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.ActionMode;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.gson.Gson;
import com.simats.foodstall.adapter.TransactionAdapter;
import com.simats.foodstall.model.StatusResponse;
import com.simats.foodstall.model.Transaction;
import com.simats.foodstall.model.WalletDetailsResponse;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UwalletActivity extends AppCompatActivity implements TransactionAdapter.OnTransactionClickListener {

    private TextView walletBalanceTextView;
    private RecyclerView transactionsRecyclerView;
    private TransactionAdapter adapter;
    private SwipeRefreshLayout swipeRefreshLayout;
    private String studentId;
    private double currentBalance = 0.0;
    private List<Transaction> transactionList = new ArrayList<>();

    private ActionMode actionMode;
    private ActionMode.Callback actionModeCallback;
    private boolean isInSelectionMode = false;

    private final ActivityResultLauncher<Intent> addMoneyLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK) {
                    fetchWalletDetails();
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.uwallet);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        bindViews();

        SharedPreferences prefs = getSharedPreferences("StallSpotPrefs", MODE_PRIVATE);
        studentId = prefs.getString("STUDENT_ID", null);

        if (studentId == null) {
            Toast.makeText(this, "Error: Could not verify user.", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        setupRecyclerView();
        setupActionMode();

        Button addMoneyButton = findViewById(R.id.addMoneyButton);
        addMoneyButton.setOnClickListener(v -> {
            Intent intent = new Intent(UwalletActivity.this, Uadd_moneyActivity.class);
            intent.putExtra("SOURCE_ACTIVITY", "WALLET");
            intent.putExtra("CURRENT_BALANCE", currentBalance);
            addMoneyLauncher.launch(intent);
        });

        swipeRefreshLayout.setOnRefreshListener(this::fetchWalletDetails);
        setupBottomNavigation();
        fetchWalletDetails();
    }

    private void bindViews() {
        walletBalanceTextView = findViewById(R.id.walletBalanceTextView);
        transactionsRecyclerView = findViewById(R.id.transactionsRecyclerView);
        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout);
    }

    private void setupRecyclerView() {
        transactionsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new TransactionAdapter(transactionList, this);
        transactionsRecyclerView.setAdapter(adapter);
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

    private void fetchWalletDetails() {
        swipeRefreshLayout.setRefreshing(true);
        ApiClient.getClient().create(ApiService.class).getWalletDetails(studentId).enqueue(new Callback<WalletDetailsResponse>() {
            @Override
            public void onResponse(@NonNull Call<WalletDetailsResponse> call, @NonNull Response<WalletDetailsResponse> response) {
                swipeRefreshLayout.setRefreshing(false);
                if (response.isSuccessful() && response.body() != null && "success".equals(response.body().getStatus())) {
                    WalletDetailsResponse.WalletData data = response.body().getData();
                    currentBalance = data.getBalance();
                    walletBalanceTextView.setText(String.format(Locale.getDefault(), "₹%.2f", currentBalance));
                    List<Transaction> transactions = data.getTransactions() != null ? data.getTransactions() : new ArrayList<>();
                    adapter.updateList(transactions);
                } else {
                    Log.e("UwalletActivity", "API Error: " + response.code());
                    Toast.makeText(UwalletActivity.this, "Failed to load wallet details.", Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onFailure(@NonNull Call<WalletDetailsResponse> call, @NonNull Throwable t) {
                swipeRefreshLayout.setRefreshing(false);
                Log.e("UwalletActivity", "Network Failure", t);
                Toast.makeText(UwalletActivity.this, "Network Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onItemClick(Transaction transaction) {
        if (isInSelectionMode) {
            toggleSelection(transaction);
        }
    }

    @Override
    public void onItemLongClick(Transaction transaction) {
        if (!isInSelectionMode) {
            isInSelectionMode = true;
            adapter.setInSelectionMode(true);
            actionMode = startSupportActionMode(actionModeCallback);
            toggleSelection(transaction);
        }
    }

    private void toggleSelection(Transaction transaction) {
        adapter.toggleSelection(transaction);
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
                .setTitle("Delete Transactions")
                .setMessage("Are you sure you want to delete these " + count + " transactions? This action cannot be undone.")
                .setPositiveButton("Delete", (dialog, which) -> deleteSelectedTransactions())
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void deleteSelectedTransactions() {
        List<Transaction> selected = adapter.getSelectedItems();
        if (studentId == null || selected.isEmpty()) return;

        List<Integer> idsToDelete = selected.stream().map(Transaction::getTransactionId).collect(Collectors.toList());
        String idsJson = new Gson().toJson(idsToDelete);

        ApiClient.getClient().create(ApiService.class).deleteWalletTransactions(studentId, idsJson).enqueue(new Callback<StatusResponse>() {
            @Override
            public void onResponse(@NonNull Call<StatusResponse> call, @NonNull Response<StatusResponse> response) {
                if(response.isSuccessful() && response.body() != null && "success".equals(response.body().getStatus())) {
                    Toast.makeText(UwalletActivity.this, "Transactions deleted", Toast.LENGTH_SHORT).show();
                    fetchWalletDetails();
                } else {
                    Toast.makeText(UwalletActivity.this, "Failed to delete transactions", Toast.LENGTH_SHORT).show();
                }
                if (actionMode != null) {
                    actionMode.finish();
                }
            }

            @Override
            public void onFailure(@NonNull Call<StatusResponse> call, @NonNull Throwable t) {
                Toast.makeText(UwalletActivity.this, "Network error while deleting", Toast.LENGTH_SHORT).show();
                if (actionMode != null) {
                    actionMode.finish();
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        if (isInSelectionMode) {
            if (actionMode != null) actionMode.finish();
        } else {
            super.onBackPressed();
        }
    }

    private void setupBottomNavigation() {
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation_bar);
        bottomNavigationView.setSelectedItemId(R.id.nav_wallet);
        bottomNavigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.nav_home) {
                startActivity(new Intent(getApplicationContext(), UhomeActivity.class));
                overridePendingTransition(0, 0);
                finish();
                return true;
            } else if (itemId == R.id.nav_orders) {
                startActivity(new Intent(getApplicationContext(), UordersActivity.class));
                overridePendingTransition(0, 0);
                finish();
                return true;
            } else if (itemId == R.id.nav_wallet) {
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
}