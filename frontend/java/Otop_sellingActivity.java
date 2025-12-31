package com.simats.foodstall;

import android.content.SharedPreferences;
import android.graphics.Color;
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

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.google.android.material.button.MaterialButtonToggleGroup;
import com.simats.foodstall.adapter.NeedsAttentionAdapter;
import com.simats.foodstall.adapter.TopPerformerAdapter;
import com.simats.foodstall.model.ProductAnalyticsResponse;
import com.simats.foodstall.model.TopPerformer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Otop_sellingActivity extends AppCompatActivity {

    private FrameLayout loadingOverlay;
    private ImageView loadingIcon;
    private TextView totalRevenueText, totalOrdersText, totalRevenueLabel;
    private MaterialButtonToggleGroup filterToggleGroup;
    private RecyclerView topPerformersRecyclerView, needsAttentionRecyclerView;
    private BarChart salesOverviewChart;
    private TopPerformerAdapter topPerformerAdapter;
    private NeedsAttentionAdapter needsAttentionAdapter;
    private String currentFilter = "overall";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.otop_selling);

        bindViews();
        setupRecyclerViews();
        setupFilterListener();
        fetchAnalyticsData();
    }

    private void bindViews() {
        findViewById(R.id.backButton).setOnClickListener(v -> finish());
        loadingOverlay = findViewById(R.id.loadingOverlay);
        loadingIcon = findViewById(R.id.loadingIcon);
        totalRevenueText = findViewById(R.id.totalRevenueText);
        totalOrdersText = findViewById(R.id.totalOrdersText);
        totalRevenueLabel = findViewById(R.id.totalRevenueLabel);
        filterToggleGroup = findViewById(R.id.filterToggleGroup);
        topPerformersRecyclerView = findViewById(R.id.topPerformersRecyclerView);
        needsAttentionRecyclerView = findViewById(R.id.needsAttentionRecyclerView);
        salesOverviewChart = findViewById(R.id.salesOverviewChart);
    }

    private void setupRecyclerViews() {
        topPerformersRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        needsAttentionRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        // --- START OF FIX: Removed 'this' from the constructor call ---
        topPerformerAdapter = new TopPerformerAdapter(new ArrayList<>());
        // --- END OF FIX ---

        needsAttentionAdapter = new NeedsAttentionAdapter(new ArrayList<>());

        topPerformersRecyclerView.setAdapter(topPerformerAdapter);
        needsAttentionRecyclerView.setAdapter(needsAttentionAdapter);
    }

    private void setupFilterListener() {
        filterToggleGroup.addOnButtonCheckedListener((group, checkedId, isChecked) -> {
            if (isChecked) {
                if (checkedId == R.id.overallButton) {
                    currentFilter = "overall";
                    totalRevenueLabel.setText("Total Revenue (Overall)");
                } else if (checkedId == R.id.yearButton) {
                    currentFilter = "year";
                    totalRevenueLabel.setText("Total Revenue (This Year)");
                }
                fetchAnalyticsData();
            }
        });
    }

    private void fetchAnalyticsData() {
        showLoading();
        SharedPreferences prefs = getSharedPreferences("owner_prefs", MODE_PRIVATE);
        String stallId = prefs.getString("stall_id", null);

        if (stallId == null) {
            Toast.makeText(this, "Stall ID not found.", Toast.LENGTH_LONG).show();
            hideLoading();
            return;
        }

        ApiClient.getClient().create(ApiService.class).getProductAnalytics(stallId, currentFilter).enqueue(new Callback<ProductAnalyticsResponse>() {
            @Override
            public void onResponse(@NonNull Call<ProductAnalyticsResponse> call, @NonNull Response<ProductAnalyticsResponse> response) {
                hideLoading();
                if (response.isSuccessful() && response.body() != null && "success".equals(response.body().getStatus())) {
                    updateUI(response.body().getData());
                } else {
                    handleApiError(response);
                }
            }
            @Override
            public void onFailure(@NonNull Call<ProductAnalyticsResponse> call, @NonNull Throwable t) {
                hideLoading();
                Log.e("TopSelling", "API call failed", t);
                Toast.makeText(Otop_sellingActivity.this, "Network Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showLoading() {
        loadingOverlay.setVisibility(View.VISIBLE);
        Animation rotation = AnimationUtils.loadAnimation(this, R.anim.hourglass_rotation);
        loadingIcon.startAnimation(rotation);
    }

    private void hideLoading() {
        loadingOverlay.setVisibility(View.GONE);
        loadingIcon.clearAnimation();
    }

    private void updateUI(ProductAnalyticsResponse.AnalyticsData data) {
        if (data == null) return;

        totalRevenueText.setText(String.format(Locale.getDefault(), "₹%.2f", data.getTotalRevenue()));
        totalOrdersText.setText(String.valueOf(data.getTotalOrders()));

        // --- START OF FIX: Removed 'this' from the constructor call ---
        topPerformerAdapter = new TopPerformerAdapter(data.getTopPerformers() != null ? data.getTopPerformers() : new ArrayList<>());
        // --- END OF FIX ---
        topPerformersRecyclerView.setAdapter(topPerformerAdapter);

        needsAttentionAdapter = new NeedsAttentionAdapter(data.getNeedsAttention() != null ? data.getNeedsAttention() : new ArrayList<>());
        needsAttentionRecyclerView.setAdapter(needsAttentionAdapter);

        if (data.getTopPerformers() != null && !data.getTopPerformers().isEmpty()) {
            setupBarChart(data.getTopPerformers());
        } else {
            salesOverviewChart.clear();
            salesOverviewChart.invalidate();
        }
    }

    private void setupBarChart(List<TopPerformer> topPerformers) {
        List<BarEntry> entries = new ArrayList<>();
        List<String> labels = new ArrayList<>();

        List<TopPerformer> reversedList = new ArrayList<>(topPerformers);
        Collections.reverse(reversedList);

        for (int i = 0; i < reversedList.size(); i++) {
            entries.add(new BarEntry(i, (float) reversedList.get(i).getTotalRevenue()));
            labels.add(TextDrawableUtil.getInitials(reversedList.get(i).getItemName()));
        }

        BarDataSet dataSet = new BarDataSet(entries, "Revenue");
        dataSet.setColor(Color.parseColor("#FF6B6B"));
        dataSet.setValueTextColor(Color.DKGRAY);
        dataSet.setValueTextSize(10f);

        BarData barData = new BarData(dataSet);
        barData.setBarWidth(0.6f);

        salesOverviewChart.setData(barData);
        salesOverviewChart.setFitBars(true);
        salesOverviewChart.getDescription().setEnabled(false);
        salesOverviewChart.getLegend().setEnabled(false);
        salesOverviewChart.animateY(1000);

        XAxis xAxis = salesOverviewChart.getXAxis();
        xAxis.setValueFormatter(new IndexAxisValueFormatter(labels));
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setGranularity(1f);
        xAxis.setDrawGridLines(false);
        xAxis.setTextSize(12f);

        salesOverviewChart.getAxisRight().setEnabled(false);
        salesOverviewChart.getAxisLeft().setAxisMinimum(0f);
        salesOverviewChart.invalidate();
    }

    private void handleApiError(Response<?> response) {
        String errorBody = "Unknown error";
        if (response.errorBody() != null) {
            try {
                errorBody = response.errorBody().string();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        Log.e("TopSelling", "API Error: " + response.code() + " - " + errorBody);
        Toast.makeText(Otop_sellingActivity.this, "Failed to load analytics data.", Toast.LENGTH_SHORT).show();
    }
}