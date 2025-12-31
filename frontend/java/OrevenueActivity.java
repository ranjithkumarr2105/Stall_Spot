package com.simats.foodstall;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.simats.foodstall.adapter.RevenueDetailAdapter;
import com.simats.foodstall.model.RevenueDetail;
import com.simats.foodstall.model.RevenueResponse;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OrevenueActivity extends AppCompatActivity {

    private FrameLayout loadingOverlay;
    private ImageView loadingIcon;
    private TextView todaysRevenueText, weeksRevenueText, monthsRevenueText;
    private RecyclerView revenueDetailsRecyclerView;
    private LineChart revenueLineChart;
    private RevenueDetailAdapter adapter;
    private List<RevenueDetail> revenueDetailsList = new ArrayList<>();

    private final ActivityResultLauncher<Intent> createFileLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == AppCompatActivity.RESULT_OK && result.getData() != null) {
                    Uri uri = result.getData().getData();
                    if (uri != null) {
                        String csvContent = generateCsvContent();
                        writeCsvToFile(uri, csvContent);
                    }
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.orevenue);

        bindViews();
        setupRecyclerView();
        fetchRevenueDataFromServer();
    }

    private void bindViews() {
        findViewById(R.id.backButton).setOnClickListener(v -> finish());
        loadingOverlay = findViewById(R.id.loadingOverlay);
        loadingIcon = findViewById(R.id.loadingIcon);
        todaysRevenueText = findViewById(R.id.todaysRevenueText);
        weeksRevenueText = findViewById(R.id.weeksRevenueText);
        monthsRevenueText = findViewById(R.id.monthsRevenueText);
        revenueDetailsRecyclerView = findViewById(R.id.revenueDetailsRecyclerView);
        revenueLineChart = findViewById(R.id.revenueLineChart);
        Button exportCsvButton = findViewById(R.id.exportCsvButton);
        exportCsvButton.setOnClickListener(v -> exportToCsv());
    }

    private void setupRecyclerView() {
        revenueDetailsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new RevenueDetailAdapter(revenueDetailsList);
        revenueDetailsRecyclerView.setAdapter(adapter);
    }

    private void fetchRevenueDataFromServer() {
        loadingOverlay.setVisibility(View.VISIBLE);
        Animation rotation = AnimationUtils.loadAnimation(this, R.anim.hourglass_rotation);
        loadingIcon.startAnimation(rotation);

        SharedPreferences prefs = getSharedPreferences("owner_prefs", MODE_PRIVATE);
        String stallId = prefs.getString("stall_id", null);

        if (stallId == null) {
            Toast.makeText(this, "Stall ID not found. Please log in again.", Toast.LENGTH_LONG).show();
            hideLoading();
            return;
        }

        ApiClient.getClient().create(ApiService.class).getRevenueData(stallId).enqueue(new Callback<RevenueResponse>() {
            @Override
            public void onResponse(@NonNull Call<RevenueResponse> call, @NonNull Response<RevenueResponse> response) {
                hideLoading();
                if (response.isSuccessful() && response.body() != null && "success".equals(response.body().getStatus())) {
                    updateUiWithData(response.body().getData());
                } else {
                    String errorBody = "Unknown error";
                    if (response.errorBody() != null) {
                        try {
                            errorBody = response.errorBody().string();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    Log.e("OrevenueActivity", "API Error: " + response.code() + " - " + errorBody);
                    Toast.makeText(OrevenueActivity.this, "Failed to load revenue data.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<RevenueResponse> call, @NonNull Throwable t) {
                hideLoading();
                Log.e("OrevenueActivity", "API call failed", t);
                Toast.makeText(OrevenueActivity.this, "Network Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void hideLoading() {
        loadingOverlay.setVisibility(View.GONE);
        loadingIcon.clearAnimation();
    }

    private void updateUiWithData(RevenueResponse.RevenueData data) {
        if (data == null) return;

        todaysRevenueText.setText(String.format(Locale.getDefault(), "₹%.2f", data.getTodaysRevenue()));
        weeksRevenueText.setText(String.format(Locale.getDefault(), "₹%.2f", data.getThisWeeksRevenue()));
        monthsRevenueText.setText(String.format(Locale.getDefault(), "₹%.2f", data.getThisMonthsRevenue()));

        revenueDetailsList.clear();
        if (data.getDailyDetails() != null) {
            revenueDetailsList.addAll(data.getDailyDetails());
        }
        adapter.updateList(revenueDetailsList);

        if (!revenueDetailsList.isEmpty()) {
            setupRevenueChart(revenueDetailsList);
        }
    }

    private void setupRevenueChart(List<RevenueDetail> details) {
        ArrayList<Entry> entries = new ArrayList<>();
        ArrayList<RevenueDetail> reversedDetails = new ArrayList<>(details);
        Collections.reverse(reversedDetails); // Reverse for chronological order on the chart

        for (int i = 0; i < reversedDetails.size(); i++) {
            entries.add(new Entry(i, (float) reversedDetails.get(i).getRevenue()));
        }

        LineDataSet dataSet = new LineDataSet(entries, "Daily Revenue");
        dataSet.setColor(ContextCompat.getColor(this, R.color.brand_pink));
        dataSet.setValueTextColor(ContextCompat.getColor(this, R.color.text_primary));
        dataSet.setCircleColor(ContextCompat.getColor(this, R.color.brand_pink));
        dataSet.setCircleHoleColor(Color.WHITE);
        dataSet.setLineWidth(2f);
        dataSet.setCircleRadius(4f);
        dataSet.setDrawValues(false); // Hide values on points

        LineData lineData = new LineData(dataSet);
        revenueLineChart.setData(lineData);

        // Customize Chart Appearance
        revenueLineChart.getDescription().setEnabled(false);
        revenueLineChart.getLegend().setEnabled(false);
        revenueLineChart.setDrawGridBackground(false);

        // Customize X-Axis
        XAxis xAxis = revenueLineChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(false);
        xAxis.setGranularity(1f);
        xAxis.setValueFormatter(new ValueFormatter() {
            private final SimpleDateFormat apiFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            private final SimpleDateFormat displayFormat = new SimpleDateFormat("dd MMM", Locale.getDefault());
            @Override
            public String getFormattedValue(float value) {
                try {
                    Date date = apiFormat.parse(reversedDetails.get((int) value).getDate());
                    return displayFormat.format(date);
                } catch (ParseException | IndexOutOfBoundsException e) {
                    return "";
                }
            }
        });

        // Customize Y-Axis
        revenueLineChart.getAxisRight().setEnabled(false); // Hide right axis
        revenueLineChart.getAxisLeft().setDrawGridLines(true);
        revenueLineChart.getAxisLeft().setGridColor(Color.LTGRAY);

        revenueLineChart.animateX(1000); // Animate chart drawing
        revenueLineChart.invalidate(); // Refresh chart
    }

    private void exportToCsv() {
        if (revenueDetailsList.isEmpty()) {
            Toast.makeText(this, "No data available to export.", Toast.LENGTH_SHORT).show();
            return;
        }

        Intent intent = new Intent(Intent.ACTION_CREATE_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("text/csv");
        String fileName = "Revenue_Report_" + new SimpleDateFormat("yyyyMMdd", Locale.getDefault()).format(new Date()) + ".csv";
        intent.putExtra(Intent.EXTRA_TITLE, fileName);

        createFileLauncher.launch(intent);
    }

    private String generateCsvContent() {
        StringBuilder csvBuilder = new StringBuilder();
        csvBuilder.append("Date,Orders,Revenue\n");

        for (RevenueDetail detail : revenueDetailsList) {
            csvBuilder.append(detail.getDate()).append(",");
            csvBuilder.append(detail.getOrders()).append(",");
            csvBuilder.append(String.format(Locale.US, "%.2f", detail.getRevenue()));
            csvBuilder.append("\n");
        }
        return csvBuilder.toString();
    }

    private void writeCsvToFile(Uri uri, String content) {
        try (OutputStream outputStream = getContentResolver().openOutputStream(uri)) {
            if (outputStream != null) {
                outputStream.write(content.getBytes(StandardCharsets.UTF_8));
                Toast.makeText(this, "Report saved successfully!", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            Log.e("OrevenueActivity", "Error writing CSV file", e);
            Toast.makeText(this, "Failed to save report.", Toast.LENGTH_SHORT).show();
        }
    }
}