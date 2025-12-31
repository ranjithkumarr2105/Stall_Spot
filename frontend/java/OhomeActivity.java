package com.simats.foodstall;

import android.app.Activity;
import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.card.MaterialCardView;
import com.razorpay.Checkout;
import com.razorpay.PaymentData;
import com.razorpay.PaymentResultWithDataListener;
import com.simats.foodstall.model.DashboardResponse;
import com.simats.foodstall.model.RazorpayOrderResponse;
import com.simats.foodstall.model.StatusResponse;

import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OhomeActivity extends AppCompatActivity implements PaymentResultWithDataListener {

    private SwipeRefreshLayout swipeRefreshLayout;
    private FrameLayout loadingOverlay;
    private ImageView loadingIcon;
    private TextView analyticsFilterText, stallNameTitle;
    private CardView ordersTodayCard, revenueTodayCard, pendingOrdersCard, topSellingCard;
    private String stallId;
    private LineChart revenueChart;
    private BarChart peakHoursChart;
    private List<DashboardResponse.RevenueTrendPoint> fullRevenueTrendData;
    private String ownerEmail, ownerPhone;

    private MaterialCardView rentDueCard;
    private TextView revenueAmountText, rentAmountText;
    private Button payRentButton;
    private ImageButton downloadReportButton;
    private DashboardResponse.RentDetails currentRentDetails;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ohome);

        Checkout.preload(getApplicationContext());

        bindViews();

        SharedPreferences sharedPreferences = getSharedPreferences("owner_prefs", MODE_PRIVATE);
        stallId = sharedPreferences.getString("stall_id", null);
        String stallName = sharedPreferences.getString("stall_name", "Owner Dashboard");
        ownerEmail = sharedPreferences.getString("owner_email", "default@example.com");
        ownerPhone = sharedPreferences.getString("owner_phone", "9999999999");
        stallNameTitle.setText(stallName);


        if (stallId == null || stallId.isEmpty()) {
            Toast.makeText(this, "Error: Could not verify owner. Please log in again.", Toast.LENGTH_LONG).show();
            Intent loginIntent = new Intent(this, LoginActivity.class);
            loginIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(loginIntent);
            finish();
            return;
        }

        swipeRefreshLayout.setOnRefreshListener(() -> fetchDashboardData(stallId));
        fetchDashboardData(stallId);

        findViewById(R.id.settingsIcon).setOnClickListener(v -> startActivity(new Intent(this, OsettingsActivity.class)));
        analyticsFilterText.setOnClickListener(v -> showAnalyticsFilterDialog());
        setupBottomNavigation();
    }

    private void bindViews() {
        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout);
        loadingOverlay = findViewById(R.id.loadingOverlay);
        loadingIcon = findViewById(R.id.loadingIcon);
        analyticsFilterText = findViewById(R.id.analyticsFilterText);
        stallNameTitle = findViewById(R.id.stallNameTitle);
        ordersTodayCard = findViewById(R.id.ordersTodayCard);
        revenueTodayCard = findViewById(R.id.revenueTodayCard);
        pendingOrdersCard = findViewById(R.id.pendingOrdersCard);
        topSellingCard = findViewById(R.id.topSellingCard);
        revenueChart = findViewById(R.id.revenueChart);
        peakHoursChart = findViewById(R.id.peakHoursChart);
        rentDueCard = findViewById(R.id.rentDueCard);
        revenueAmountText = findViewById(R.id.revenueAmountText);
        rentAmountText = findViewById(R.id.rentAmountText);
        payRentButton = findViewById(R.id.payRentButton);
        downloadReportButton = findViewById(R.id.downloadReportButton);
    }

    private void fetchDashboardData(String stallId) {
        if (!swipeRefreshLayout.isRefreshing()) {
            showLoadingOverlay(null);
        }
        ApiClient.getClient().create(ApiService.class).getDashboardData(stallId).enqueue(new Callback<DashboardResponse>() {
            @Override
            public void onResponse(Call<DashboardResponse> call, Response<DashboardResponse> response) {
                new Handler(Looper.getMainLooper()).postDelayed(() -> {
                    hideLoadingOverlay();
                    if (swipeRefreshLayout.isRefreshing()) {
                        swipeRefreshLayout.setRefreshing(false);
                    }
                    if (response.isSuccessful() && response.body() != null && "success".equals(response.body().getStatus())) {
                        updateUI(response.body().getData());
                    } else {
                        Toast.makeText(OhomeActivity.this, "Failed to load dashboard data.", Toast.LENGTH_SHORT).show();
                    }
                }, 500);
            }

            @Override
            public void onFailure(Call<DashboardResponse> call, Throwable t) {
                new Handler(Looper.getMainLooper()).postDelayed(() -> {
                    hideLoadingOverlay();
                    if (swipeRefreshLayout.isRefreshing()) {
                        swipeRefreshLayout.setRefreshing(false);
                    }
                    Log.e("DashboardFailure", "onFailure: " + t.getMessage());
                    Toast.makeText(OhomeActivity.this, "Network Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                }, 500);
            }
        });
    }

    private void updateUI(DashboardResponse.DashboardData data) {
        if (data == null) return;
        setMetricCardData(ordersTodayCard, R.drawable.ic_orders, String.valueOf(data.getOrdersToday()), "Orders Today");
        setMetricCardData(revenueTodayCard, R.drawable.ic_wallet, String.format(Locale.getDefault(), "₹%.2f", data.getRevenueToday()), "Revenue Today");
        setMetricCardData(pendingOrdersCard, R.drawable.ic_clock1, String.valueOf(data.getPendingOrders()), "Pending Orders");
        setMetricCardData(topSellingCard, R.drawable.ic_dish, data.getTopSelling(), "Top Selling");

        ordersTodayCard.setOnClickListener(v -> startActivity(new Intent(OhomeActivity.this, OTordersActivity.class)));
        pendingOrdersCard.setOnClickListener(v -> startActivity(new Intent(OhomeActivity.this, Opending_ordersActivity.class)));
        revenueTodayCard.setOnClickListener(v -> startActivity(new Intent(OhomeActivity.this, OrevenueActivity.class)));
        topSellingCard.setOnClickListener(v -> startActivity(new Intent(OhomeActivity.this, Otop_sellingActivity.class)));

        fullRevenueTrendData = data.getRevenueTrend();
        analyticsFilterText.setText("Last 7 days");
        filterAndDisplayRevenueChart(7);
        if (data.getPeakHours() != null && !data.getPeakHours().isEmpty()) {
            setupPeakHoursChart(data.getPeakHours());
        } else {
            peakHoursChart.clear();
            peakHoursChart.invalidate();
        }

        currentRentDetails = data.getRentDetails();
        if (currentRentDetails != null) {
            rentDueCard.setVisibility(View.VISIBLE);
            revenueAmountText.setText(String.format(Locale.getDefault(), "₹%.2f", currentRentDetails.getTotalRevenue()));
            rentAmountText.setText(String.format(Locale.getDefault(), "₹%.2f", currentRentDetails.getRentAmount()));
            payRentButton.setOnClickListener(v -> startRentPayment());
            downloadReportButton.setOnClickListener(v -> downloadSalesReportPdf());
        } else {
            rentDueCard.setVisibility(View.GONE);
        }
    }

    private void downloadSalesReportPdf() {
        if (currentRentDetails == null) {
            Toast.makeText(this, "No rent details available to generate report.", Toast.LENGTH_SHORT).show();
            return;
        }

        String month = String.valueOf(currentRentDetails.getInvoiceMonth());
        String year = String.valueOf(currentRentDetails.getInvoiceYear());
        String url = ApiClient.BASE_URL + "generate_report_pdf.php?stall_id=" + stallId + "&month=" + month + "&year=" + year;

        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));
        String fileName = "SalesReport_" + stallId + "_" + month + "_" + year + ".pdf";
        request.setTitle("Monthly Sales Report");
        request.setDescription("Downloading " + fileName);
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, fileName);

        DownloadManager downloadManager = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
        if (downloadManager != null) {
            downloadManager.enqueue(request);
            Toast.makeText(this, "Report download started...", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Unable to access Download Manager.", Toast.LENGTH_SHORT).show();
        }
    }

    private void startRentPayment() {
        if (currentRentDetails == null) {
            Toast.makeText(this, "Rent details not available.", Toast.LENGTH_SHORT).show();
            return;
        }
        showLoadingOverlay(null);
        ApiClient.getClient().create(ApiService.class)
                .createRentOrder(stallId, currentRentDetails.getInvoiceId(), currentRentDetails.getRentAmount())
                .enqueue(new Callback<RazorpayOrderResponse>() {
                    @Override
                    public void onResponse(Call<RazorpayOrderResponse> call, Response<RazorpayOrderResponse> response) {
                        if (response.isSuccessful() && response.body() != null && "success".equals(response.body().getStatus())) {
                            initiateRazorpayPayment(response.body());
                        } else {
                            hideLoadingOverlay();
                            Toast.makeText(OhomeActivity.this, "Could not connect to payment gateway.", Toast.LENGTH_SHORT).show();
                        }
                    }
                    @Override
                    public void onFailure(Call<RazorpayOrderResponse> call, Throwable t) {
                        hideLoadingOverlay();
                        Toast.makeText(OhomeActivity.this, "Network Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void initiateRazorpayPayment(RazorpayOrderResponse orderResponse) {
        final Activity activity = this;
        final Checkout co = new Checkout();
        try {
            JSONObject options = new JSONObject();
            options.put("name", "Stall Spot Rent");
            options.put("description", "Monthly Rent Payment for Invoice #" + currentRentDetails.getInvoiceId());
            options.put("order_id", orderResponse.getOrderId());
            options.put("theme.color", "#FF6B6B");
            options.put("currency", "INR");
            options.put("amount", orderResponse.getAmountInPaise());
            JSONObject prefill = new JSONObject();
            prefill.put("email", ownerEmail);
            prefill.put("contact", ownerPhone);
            options.put("prefill", prefill);
            hideLoadingOverlay();
            co.open(activity, options);
        } catch (Exception e) {
            Toast.makeText(activity, "Error in payment: " + e.getMessage(), Toast.LENGTH_LONG).show();
            hideLoadingOverlay();
        }
    }

    @Override
    public void onPaymentSuccess(String razorpayPaymentId, PaymentData paymentData) {
        showLoadingOverlay(null);
        ApiClient.getClient().create(ApiService.class)
                .verifyRentPayment(paymentData.getPaymentId(), paymentData.getOrderId(), paymentData.getSignature(), currentRentDetails.getInvoiceId())
                .enqueue(new Callback<StatusResponse>() {
                    @Override
                    public void onResponse(Call<StatusResponse> call, Response<StatusResponse> response) {
                        hideLoadingOverlay();
                        if (response.isSuccessful() && response.body() != null && "success".equals(response.body().getStatus())) {
                            // --- THIS IS THE UPDATED PART ---
                            showRentReceiptDialog(paymentData); // Show receipt instead of Toast
                        } else {
                            Toast.makeText(OhomeActivity.this, "Payment successful, but verification failed on server.", Toast.LENGTH_LONG).show();
                        }
                    }
                    @Override
                    public void onFailure(Call<StatusResponse> call, Throwable t) {
                        hideLoadingOverlay();
                        Toast.makeText(OhomeActivity.this, "Payment successful, but a network error occurred during verification.", Toast.LENGTH_LONG).show();
                    }
                });
    }

    @Override
    public void onPaymentError(int code, String description, PaymentData paymentData) {
        Toast.makeText(this, "Payment failed: " + description, Toast.LENGTH_LONG).show();
    }

    // --- NEW METHODS TO SHOW RECEIPT DIALOG AND HANDLE PDF DOWNLOAD ---
    private void showRentReceiptDialog(PaymentData paymentData) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_rent_receipt, null);
        builder.setView(dialogView);
        builder.setCancelable(false);

        TextView receiptAmount = dialogView.findViewById(R.id.receiptAmount);
        TextView receiptPaymentId = dialogView.findViewById(R.id.receiptPaymentId);
        TextView receiptPaymentDate = dialogView.findViewById(R.id.receiptPaymentDate);
        TextView receiptInvoiceId = dialogView.findViewById(R.id.receiptInvoiceId);
        Button downloadButton = dialogView.findViewById(R.id.downloadReceiptButton);
        Button closeButton = dialogView.findViewById(R.id.closeReceiptButton);

        View lateFeeLayout = dialogView.findViewById(R.id.receiptLateFeeLayout);
        TextView lateFeeAmountText = dialogView.findViewById(R.id.receiptLateFeeAmount);

        // Calculate total amount paid including late fee
        double lateFee = (currentRentDetails.getLateFee() != null ? currentRentDetails.getLateFee() : 0.0);
        double totalPaid = currentRentDetails.getRentAmount() + lateFee;

        receiptAmount.setText(String.format(Locale.getDefault(), "₹%.2f", totalPaid));
        receiptPaymentId.setText(paymentData.getPaymentId());
        receiptInvoiceId.setText(String.format(Locale.getDefault(), "#%d", currentRentDetails.getInvoiceId()));

        String currentDate = new SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault()).format(new Date());
        receiptPaymentDate.setText(currentDate);

        // Conditionally show late fee row
        if (lateFee > 0) {
            lateFeeLayout.setVisibility(View.VISIBLE);
            lateFeeAmountText.setText(String.format(Locale.getDefault(), "₹%.2f", lateFee));
        } else {
            lateFeeLayout.setVisibility(View.GONE);
        }

        final AlertDialog dialog = builder.create();

        downloadButton.setOnClickListener(v -> {
            downloadRentReceiptPdf();
            Toast.makeText(this, "Receipt download started...", Toast.LENGTH_SHORT).show();
        });

        closeButton.setOnClickListener(v -> {
            dialog.dismiss();
            fetchDashboardData(stallId);
        });

        dialog.show();
    }
    private void downloadRentReceiptPdf() {
        if (currentRentDetails == null) {
            Toast.makeText(this, "Cannot download receipt, invoice details are missing.", Toast.LENGTH_SHORT).show();
            return;
        }

        String url = ApiClient.BASE_URL + "generate_rent_receipt_pdf.php?invoice_id=" + currentRentDetails.getInvoiceId();

        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));
        String fileName = "RentReceipt_" + stallId + "_" + currentRentDetails.getInvoiceId() + ".pdf";
        request.setTitle("Rent Payment Receipt");
        request.setDescription("Downloading " + fileName);
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, fileName);

        DownloadManager downloadManager = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
        if (downloadManager != null) {
            downloadManager.enqueue(request);
        } else {
            Toast.makeText(this, "Unable to access Download Manager.", Toast.LENGTH_SHORT).show();
        }
    }


    private void showAnalyticsFilterDialog() {
        final String[] options = {"Last 7 days", "Last 30 days"};
        new AlertDialog.Builder(this)
                .setTitle("Select Time Range")
                .setItems(options, (dialog, which) -> {
                    analyticsFilterText.setText(options[which]);
                    int days = (which == 0) ? 7 : 30;
                    filterAndDisplayRevenueChart(days);
                })
                .show();
    }

    private void filterAndDisplayRevenueChart(int days) {
        if (fullRevenueTrendData == null || fullRevenueTrendData.isEmpty()) {
            revenueChart.clear();
            revenueChart.invalidate();
            return;
        }
        List<DashboardResponse.RevenueTrendPoint> filteredList;
        if (fullRevenueTrendData.size() > days) {
            filteredList = fullRevenueTrendData.subList(fullRevenueTrendData.size() - days, fullRevenueTrendData.size());
        } else {
            filteredList = fullRevenueTrendData;
        }
        setupRevenueChart(filteredList);
    }

    private void setupRevenueChart(List<DashboardResponse.RevenueTrendPoint> trendData) {
        ArrayList<Entry> entries = new ArrayList<>();
        final ArrayList<String> xLabels = new ArrayList<>();
        SimpleDateFormat apiFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        SimpleDateFormat displayFormat = new SimpleDateFormat("dd MMM", Locale.getDefault());
        for (int i = 0; i < trendData.size(); i++) {
            entries.add(new Entry(i, (float) trendData.get(i).getRevenue()));
            try {
                Date date = apiFormat.parse(trendData.get(i).getDate());
                xLabels.add(displayFormat.format(date != null ? date : new Date()));
            } catch (ParseException e) {
                xLabels.add("");
            }
        }

        LineDataSet dataSet = new LineDataSet(entries, "Revenue");
        dataSet.setColor(ContextCompat.getColor(this, R.color.brand_pink));
        dataSet.setCircleColor(ContextCompat.getColor(this, R.color.brand_pink));
        dataSet.setCircleHoleColor(Color.WHITE);
        dataSet.setLineWidth(2.5f);
        dataSet.setCircleRadius(5f);
        dataSet.setDrawValues(false);
        dataSet.setMode(LineDataSet.Mode.CUBIC_BEZIER);
        dataSet.setDrawFilled(true);
        dataSet.setFillDrawable(ContextCompat.getDrawable(this, R.drawable.chart_fade_red));
        LineData lineData = new LineData(dataSet);
        revenueChart.setData(lineData);
        revenueChart.getDescription().setEnabled(false);
        revenueChart.getLegend().setEnabled(false);
        XAxis xAxis = revenueChart.getXAxis();
        xAxis.setValueFormatter(new IndexAxisValueFormatter(xLabels));
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(false);
        xAxis.setGranularity(1f);
        revenueChart.getAxisRight().setEnabled(false);
        revenueChart.animateX(1000);
        revenueChart.invalidate();
    }

    private void setupPeakHoursChart(List<DashboardResponse.PeakHourPoint> peakHoursData) {
        ArrayList<BarEntry> entries = new ArrayList<>();
        float[] hourlyOrders = new float[24];
        for(DashboardResponse.PeakHourPoint point : peakHoursData) {
            if(point.getHour() >= 0 && point.getHour() < 24) {
                hourlyOrders[point.getHour()] = point.getOrderCount();
            }
        }
        for(int i = 0; i < 24; i++) {
            entries.add(new BarEntry(i, hourlyOrders[i]));
        }

        BarDataSet dataSet = new BarDataSet(entries, "Orders");
        dataSet.setDrawValues(true);

        dataSet.setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                if (value == 0) return "";
                return String.valueOf((int) value);
            }
        });
        dataSet.setValueTextSize(10f);
        dataSet.setValueTextColor(Color.DKGRAY);

        dataSet.setGradientColor(Color.parseColor("#FFCDD2"), Color.parseColor("#FF6B6B"));

        BarData barData = new BarData(dataSet);
        barData.setBarWidth(0.6f);

        peakHoursChart.setData(barData);
        peakHoursChart.getDescription().setEnabled(false);
        peakHoursChart.getLegend().setEnabled(false);
        peakHoursChart.setFitBars(true);
        peakHoursChart.setDrawGridBackground(false);
        peakHoursChart.setScaleEnabled(false);

        XAxis xAxis = peakHoursChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(false);
        xAxis.setGranularity(1f);
        xAxis.setLabelCount(6);
        xAxis.setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                int hour = (int) value;
                if (hour == 0) return "12AM";
                if (hour < 12) return hour + "AM";
                if (hour == 12) return "12PM";
                return (hour - 12) + "PM";
            }
        });

        YAxis leftAxis = peakHoursChart.getAxisLeft();
        leftAxis.setAxisMinimum(0f);
        leftAxis.setGranularity(1f);

        peakHoursChart.getAxisRight().setEnabled(false);
        peakHoursChart.setExtraBottomOffset(10f);

        peakHoursChart.animateY(1000);
        peakHoursChart.invalidate();
    }

    private void setupBottomNavigation() {
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation_bar);
        bottomNavigationView.setSelectedItemId(R.id.nav_owner_home);
        bottomNavigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.nav_owner_home) {
                return true;
            } else if (itemId == R.id.nav_owner_orders) {
                startActivity(new Intent(getApplicationContext(), OordersActivity.class));
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

    private void setMetricCardData(View cardView, int iconResId, String count, String title) {
        ImageView icon = cardView.findViewById(R.id.metricIcon);
        TextView countText = cardView.findViewById(R.id.metricCount);
        TextView titleText = cardView.findViewById(R.id.metricTitle);
        icon.setImageResource(iconResId);
        icon.setColorFilter(Color.parseColor("#FF6B6B"));
        countText.setText(count);
        titleText.setText(title);
    }

    private void showLoadingOverlay(@Nullable Runnable onComplete) {
        loadingOverlay.setVisibility(View.VISIBLE);
        Animation rotation = AnimationUtils.loadAnimation(this, R.anim.hourglass_rotation);
        loadingIcon.startAnimation(rotation);
        if (onComplete != null) {
            new Handler(Looper.getMainLooper()).postDelayed(() -> {
                hideLoadingOverlay();
                onComplete.run();
            }, 1000);
        }
    }

    private void hideLoadingOverlay() {
        if (loadingOverlay != null && loadingOverlay.getVisibility() == View.VISIBLE) {
            loadingIcon.clearAnimation();
            loadingOverlay.setVisibility(View.GONE);
        }
    }
}