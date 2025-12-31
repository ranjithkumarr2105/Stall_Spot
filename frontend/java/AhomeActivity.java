package com.simats.foodstall;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.simats.foodstall.model.AdminHomeCounts;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AhomeActivity extends AppCompatActivity {

    private TextView totalStallsCount, approvedStallsCount, rejectedStallsCount, pendingStallsCount;
    private BarChart barChart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ahome);

        bindViews();
        setupTopBar();
        setupActionCards();
        setupBottomNavigation();

        // Fetch live data from the server
        fetchHomeData();
    }

    private void bindViews() {
        // Bind card views to set their click listeners later
        View totalStallsCard = findViewById(R.id.totalStallsCard);
        View approvedStallsCard = findViewById(R.id.approvedStallsCard);
        View rejectedStallsCard = findViewById(R.id.rejectedStallsCard);
        View pendingStallsCard = findViewById(R.id.pendingStallsCard);

        // Bind the TextViews inside the cards that will be updated with data
        totalStallsCount = totalStallsCard.findViewById(R.id.metricCount);
        approvedStallsCount = approvedStallsCard.findViewById(R.id.metricCount);
        rejectedStallsCount = rejectedStallsCard.findViewById(R.id.metricCount);
        pendingStallsCount = pendingStallsCard.findViewById(R.id.metricCount);

        barChart = findViewById(R.id.barChart);

        // Set static icons and titles for the metric cards
        ((ImageView) totalStallsCard.findViewById(R.id.metricIcon)).setImageResource(R.drawable.ic_totalstalls);
        ((TextView) totalStallsCard.findViewById(R.id.metricTitle)).setText("Total Stalls");
        ((ImageView) approvedStallsCard.findViewById(R.id.metricIcon)).setImageResource(R.drawable.ic_approved);
        ((TextView) approvedStallsCard.findViewById(R.id.metricTitle)).setText("Approved");
        ((ImageView) rejectedStallsCard.findViewById(R.id.metricIcon)).setImageResource(R.drawable.ic_rejected);
        ((TextView) rejectedStallsCard.findViewById(R.id.metricTitle)).setText("Rejected");
        ((ImageView) pendingStallsCard.findViewById(R.id.metricIcon)).setImageResource(R.drawable.ic_pending);
        ((TextView) pendingStallsCard.findViewById(R.id.metricTitle)).setText("Pending");

        // Set click listeners for navigation
        totalStallsCard.setOnClickListener(v -> startActivity(new Intent(AhomeActivity.this, Aall_stallsActivity.class)));
        approvedStallsCard.setOnClickListener(v -> startActivity(new Intent(AhomeActivity.this, Aapproved_stallsActivity.class)));
        rejectedStallsCard.setOnClickListener(v -> startActivity(new Intent(AhomeActivity.this, Arejected_stallsActivity.class)));
        pendingStallsCard.setOnClickListener(v -> startActivity(new Intent(AhomeActivity.this, Apending_stallsActivity.class)));
    }

    private void fetchHomeData() {
        // You can add a SwipeRefreshLayout to your ahome.xml and show its loading indicator here
        ApiClient.getClient().create(ApiService.class).getAdminHomeCounts().enqueue(new Callback<AdminHomeCounts>() {
            @Override
            public void onResponse(@NonNull Call<AdminHomeCounts> call, @NonNull Response<AdminHomeCounts> response) {
                if (response.isSuccessful() && response.body() != null && "success".equals(response.body().getStatus())) {
                    AdminHomeCounts.CountsData data = response.body().getData();
                    updateMetricCards(data);
                    updateBarChart(data);
                } else {
                    Toast.makeText(AhomeActivity.this, "Failed to load dashboard counts.", Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onFailure(@NonNull Call<AdminHomeCounts> call, @NonNull Throwable t) {
                Toast.makeText(AhomeActivity.this, "Network Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateMetricCards(AdminHomeCounts.CountsData data) {
        totalStallsCount.setText(String.valueOf(data.getTotalStalls()));
        approvedStallsCount.setText(String.valueOf(data.getApprovedStalls()));
        rejectedStallsCount.setText(String.valueOf(data.getRejectedStalls()));
        pendingStallsCount.setText(String.valueOf(data.getPendingStalls()));
    }

    private void updateBarChart(AdminHomeCounts.CountsData data) {
        ArrayList<BarEntry> entries = new ArrayList<>();
        entries.add(new BarEntry(0, data.getApprovedStalls()));
        entries.add(new BarEntry(1, data.getRejectedStalls()));
        entries.add(new BarEntry(2, data.getPendingStalls()));

        BarDataSet dataSet = new BarDataSet(entries, "Stall Status");
        dataSet.setColors(Color.parseColor("#4CAF50"), Color.parseColor("#F44336"), Color.parseColor("#FFC107"));
        dataSet.setValueTextColor(Color.BLACK);
        dataSet.setValueTextSize(12f);

        BarData barData = new BarData(dataSet);
        barChart.setData(barData);
        barChart.getDescription().setEnabled(false);
        barChart.getLegend().setEnabled(false);
        barChart.setDrawValueAboveBar(true);
        barChart.setFitBars(true);

        XAxis xAxis = barChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(false);
        final String[] labels = new String[]{"Approved", "Rejected", "Pending"};
        xAxis.setValueFormatter(new IndexAxisValueFormatter(labels));
        xAxis.setGranularity(1f);

        barChart.getAxisLeft().setAxisMinimum(0);
        barChart.getAxisRight().setEnabled(false);
        barChart.invalidate(); // Refresh the chart
    }

    private void setupTopBar() {
        TextView welcomeTextView = findViewById(R.id.welcomeTextView);
        welcomeTextView.setText("Hi, Admin");

        ImageView settingsIcon = findViewById(R.id.settingsIcon);
        settingsIcon.setOnClickListener(v -> {
            startActivity(new Intent(AhomeActivity.this, AsettingsActivity.class));
        });
    }

    private void setupActionCards() {
        setActionCardData(findViewById(R.id.setLocationAction), R.drawable.ic_location1, "Set Location", Color.parseColor("#2196F3"),
                v -> startActivity(new Intent(AhomeActivity.this, Aset_locationActivity.class)));

        setActionCardData(findViewById(R.id.editStallDetailsAction), R.drawable.ic_settings_edit_stall, "Edit Stall Details", Color.parseColor("#00BCD4"),
                v -> startActivity(new Intent(AhomeActivity.this, AeditstallActivity.class)));

        setActionCardData(findViewById(R.id.viewAnalysisAction), R.drawable.ic_settings_analysis, "Analysis", Color.parseColor("#8BC34A"),
                v -> startActivity(new Intent(AhomeActivity.this, AanalysisActivity.class)));
    }

    private void setActionCardData(View cardView, int iconResId, String title, int tintColor, View.OnClickListener listener) {
        ImageView icon = cardView.findViewById(R.id.actionIcon);
        TextView titleText = cardView.findViewById(R.id.actionTitle);
        icon.setImageResource(iconResId);
        icon.setColorFilter(tintColor);
        titleText.setText(title);
        cardView.setOnClickListener(listener);
    }

    private void setupBottomNavigation() {
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation_bar);
        bottomNavigationView.setSelectedItemId(R.id.nav_admin_home);

        bottomNavigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.nav_admin_home) {
                return true;
            } else if (itemId == R.id.nav_admin_new_stall) {
                startActivity(new Intent(getApplicationContext(), Anew_stallActivity.class));
                overridePendingTransition(0, 0);
                return true;
            } else if (itemId == R.id.nav_admin_view_reports) {
                startActivity(new Intent(getApplicationContext(), Aview_reportsActivity.class));
                overridePendingTransition(0, 0);
                return true;
            } else if (itemId == R.id.nav_admin_profile) {
                startActivity(new Intent(getApplicationContext(), AprofileActivity.class));
                overridePendingTransition(0, 0);
                return true;
            }
            return false;
        });
    }
}