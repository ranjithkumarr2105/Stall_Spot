package com.simats.foodstall;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.github.mikephil.charting.charts.HorizontalBarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.google.android.material.appbar.MaterialToolbar;
import com.simats.foodstall.adapter.AnalysisAdapter;
import com.simats.foodstall.model.AnalysisData;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AanalysisActivity extends AppCompatActivity {

    private HorizontalBarChart topStallsChart;
    private RecyclerView comparisonRecyclerView;
    private AnalysisAdapter analysisAdapter;
    private FrameLayout loadingOverlay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.aanalysis);

        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setNavigationOnClickListener(v -> finish());

        topStallsChart = findViewById(R.id.topStallsChart);
        comparisonRecyclerView = findViewById(R.id.comparisonRecyclerView);
        loadingOverlay = findViewById(R.id.loadingOverlayAnalysis);

        setupRecyclerView();
        fetchAnalysisData();
    }

    private void setupRecyclerView() {
        comparisonRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        analysisAdapter = new AnalysisAdapter(new ArrayList<>());
        comparisonRecyclerView.setAdapter(analysisAdapter);
    }

    private void fetchAnalysisData() {
        loadingOverlay.setVisibility(View.VISIBLE);
        ApiClient.getClient().create(ApiService.class).getAnalysisData().enqueue(new Callback<AnalysisData>() {
            @Override
            public void onResponse(@NonNull Call<AnalysisData> call, @NonNull Response<AnalysisData> response) {
                loadingOverlay.setVisibility(View.GONE);
                if (response.isSuccessful() && response.body() != null && "success".equals(response.body().getStatus())) {
                    AnalysisData.Data data = response.body().getData();
                    if (data != null) {
                        setupTopStallsChart(data.getTopStalls());
                        analysisAdapter.updateData(data.getComparison());
                    } else {
                        Toast.makeText(AanalysisActivity.this, "No analysis data found.", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(AanalysisActivity.this, "Failed to load analysis data.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<AnalysisData> call, @NonNull Throwable t) {
                loadingOverlay.setVisibility(View.GONE);
                Toast.makeText(AanalysisActivity.this, "Network Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setupTopStallsChart(List<AnalysisData.TopStall> topStalls) {
        if (topStalls == null || topStalls.isEmpty()) {
            topStallsChart.clear();
            topStallsChart.invalidate();
            return;
        }

        Collections.reverse(topStalls);

        ArrayList<BarEntry> entries = new ArrayList<>();
        ArrayList<String> labels = new ArrayList<>();
        for (int i = 0; i < topStalls.size(); i++) {
            entries.add(new BarEntry(i, (float) topStalls.get(i).getCurrentRevenue()));
            labels.add(topStalls.get(i).getStallName());
        }

        BarDataSet dataSet = new BarDataSet(entries, "Revenue");
        dataSet.setColors(ContextCompat.getColor(this, R.color.brand_pink));
        dataSet.setValueTextColor(Color.BLACK);
        dataSet.setValueTextSize(10f);
        dataSet.setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                return String.format(Locale.getDefault(), "₹%,.0f", value);
            }
        });

        BarData barData = new BarData(dataSet);
        barData.setBarWidth(0.6f);

        topStallsChart.setData(barData);
        topStallsChart.getDescription().setEnabled(false);
        topStallsChart.getLegend().setEnabled(false);
        topStallsChart.setDrawValueAboveBar(true);
        topStallsChart.setFitBars(true);

        XAxis xAxis = topStallsChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(false);
        xAxis.setGranularity(1f);
        xAxis.setValueFormatter(new IndexAxisValueFormatter(labels));
        xAxis.setLabelCount(labels.size());

        YAxis leftAxis = topStallsChart.getAxisLeft();
        leftAxis.setAxisMinimum(0f);
        leftAxis.setEnabled(false);

        YAxis rightAxis = topStallsChart.getAxisRight();
        rightAxis.setEnabled(false);

        topStallsChart.setExtraOffsets(10f, 10f, 80f, 10f);
        topStallsChart.animateY(1000);
        topStallsChart.invalidate();
    }
}