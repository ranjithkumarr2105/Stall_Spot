package com.simats.foodstall;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomnavigation.BottomNavigationView;

// [FIX] Import the correct adapter and model
import com.simats.foodstall.adapter.AdminStallListAdapter;
import com.simats.foodstall.model.AdminStall;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

// [FIX] Implement the correct listener
public class Anew_stallActivity extends AppCompatActivity implements AdminStallListAdapter.OnStallClickListener {

    private RecyclerView newStallsRecyclerView;
    private FrameLayout loadingOverlay;
    private ImageView loadingIcon;
    private TextView emptyViewText;

    // [FIX] Use the new adapter
    private AdminStallListAdapter adapter;
    // [FIX] Use the new model
    private List<AdminStall> stallList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.anew_stall);

        newStallsRecyclerView = findViewById(R.id.newStallsRecyclerView);
        loadingOverlay = findViewById(R.id.loadingOverlay);
        loadingIcon = findViewById(R.id.loadingIcon);
        emptyViewText = findViewById(R.id.emptyViewText);

        setupRecyclerView();
        setupBottomNavigation();
    }

    @Override
    protected void onResume() {
        super.onResume();
        fetchPendingStalls();
    }

    private void setupRecyclerView() {
        newStallsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        stallList = new ArrayList<>();
        // [FIX] Initialize the correct adapter
        adapter = new AdminStallListAdapter(AdminStallListAdapter.VIEW_TYPE_STATUS, this);
        newStallsRecyclerView.setAdapter(adapter);
    }

    private void fetchPendingStalls() {
        startLoadingAnimation();
        if (emptyViewText != null) emptyViewText.setVisibility(View.GONE);
        newStallsRecyclerView.setVisibility(View.GONE);

        ApiService apiService = ApiClient.getClient().create(ApiService.class);
        // [FIX] Call the new API endpoint
        Call<List<AdminStall>> call = apiService.getStallsByStatus("pending");

        call.enqueue(new Callback<List<AdminStall>>() {
            @Override
            public void onResponse(@NonNull Call<List<AdminStall>> call, @NonNull Response<List<AdminStall>> response) {
                stopLoadingAnimation();
                // [FIX] Handle the new List<AdminStall> response
                if (response.isSuccessful() && response.body() != null) {
                    List<AdminStall> fetchedStalls = response.body();

                    if (!fetchedStalls.isEmpty()) {
                        stallList.clear();
                        stallList.addAll(fetchedStalls);
                        adapter.updateStalls(fetchedStalls); // Use the adapter's update method
                        newStallsRecyclerView.setVisibility(View.VISIBLE);
                    } else {
                        if (emptyViewText != null) emptyViewText.setVisibility(View.VISIBLE);
                    }
                } else {
                    Toast.makeText(Anew_stallActivity.this, "Failed to load stalls.", Toast.LENGTH_SHORT).show();
                    if (emptyViewText != null) emptyViewText.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<AdminStall>> call, @NonNull Throwable t) {
                stopLoadingAnimation();
                Log.e("FetchStallsFailure", "onFailure: " + t.getMessage());
                Toast.makeText(Anew_stallActivity.this, "Network Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                if (emptyViewText != null) {
                    emptyViewText.setText("A network error occurred.");
                    emptyViewText.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    /**
     * [FIX] This method now matches the AdminStallListAdapter.OnStallClickListener
     */
    @Override
    public void onViewClick(AdminStall stall) {
        Intent intent = new Intent(Anew_stallActivity.this, Astall_detailsActivity.class);
        // [FIX] Send the correct object with the correct key
        intent.putExtra("STALL_DATA", stall);
        startActivity(intent);
    }

    private void startLoadingAnimation() {
        if (loadingOverlay != null && loadingIcon != null) {
            loadingOverlay.setVisibility(View.VISIBLE);
            Animation rotation = AnimationUtils.loadAnimation(this, R.anim.hourglass_rotation);
            loadingIcon.startAnimation(rotation);
        }
    }

    private void stopLoadingAnimation() {
        if (loadingOverlay != null && loadingIcon != null) {
            loadingIcon.clearAnimation();
            loadingOverlay.setVisibility(View.GONE);
        }
    }

    private void setupBottomNavigation() {
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation_bar);
        bottomNavigationView.setSelectedItemId(R.id.nav_admin_new_stall);

        bottomNavigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.nav_admin_home) {
                startActivity(new Intent(getApplicationContext(), AhomeActivity.class));
                overridePendingTransition(0, 0);
                finish();
                return true;
            } else if (itemId == R.id.nav_admin_new_stall) {
                return true;
            } else if (itemId == R.id.nav_admin_view_reports) {
                startActivity(new Intent(getApplicationContext(), Aview_reportsActivity.class));
                overridePendingTransition(0, 0);
                finish();
                return true;
            } else if (itemId == R.id.nav_admin_profile) {
                startActivity(new Intent(getApplicationContext(), AprofileActivity.class));
                overridePendingTransition(0, 0);
                finish();
                return true;
            }
            return false;
        });
    }
}