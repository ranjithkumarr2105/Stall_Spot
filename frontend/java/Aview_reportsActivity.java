package com.simats.foodstall;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.simats.foodstall.adapter.ReportAdapter;
import com.simats.foodstall.model.ReportItem;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Aview_reportsActivity extends AppCompatActivity implements ReportAdapter.OnReportItemClickListener {

    private ReportAdapter adapter;
    private SwipeRefreshLayout swipeRefreshLayout;
    private TextView lastUpdatedTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.aview_reports);

        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout);
        lastUpdatedTextView = findViewById(R.id.lastUpdatedTextView);
        RecyclerView reportsRecyclerView = findViewById(R.id.reportsRecyclerView);

        reportsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new ReportAdapter(new ArrayList<>(), this);
        reportsRecyclerView.setAdapter(adapter);

        swipeRefreshLayout.setOnRefreshListener(this::fetchStallRankings);
        setupBottomNavigation();
        fetchStallRankings();
    }

    private void fetchStallRankings() {
        swipeRefreshLayout.setRefreshing(true);

        ApiClient.getClient().create(ApiService.class).getStallRankings().enqueue(new Callback<List<ReportItem>>() {
            @Override
            public void onResponse(@NonNull Call<List<ReportItem>> call, @NonNull Response<List<ReportItem>> response) {
                swipeRefreshLayout.setRefreshing(false);
                if (response.isSuccessful() && response.body() != null) {
                    adapter.updateData(response.body());
                    String currentTime = new SimpleDateFormat("hh:mm a", Locale.getDefault()).format(new Date());
                    lastUpdatedTextView.setText("Updated Today, " + currentTime);
                } else {
                    Toast.makeText(Aview_reportsActivity.this, "Failed to load reports.", Toast.LENGTH_SHORT).show();
                    Log.e("Aview_reports", "API Error: " + response.code());
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<ReportItem>> call, @NonNull Throwable t) {
                swipeRefreshLayout.setRefreshing(false);
                Toast.makeText(Aview_reportsActivity.this, "Network Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                Log.e("Aview_reports", "Network Failure", t);
            }
        });
    }

    @Override
    public void onDownloadStatementClick(ReportItem item) {
        if (item.getInvoiceId() == null) {
            Toast.makeText(this, "No invoice to download.", Toast.LENGTH_SHORT).show();
            return;
        }

        // --- NEW LOGIC: CHOOSE PDF SCRIPT BASED ON STATUS ---
        String phpScript;
        String fileNamePrefix;

        if ("paid".equalsIgnoreCase(item.getRentStatus())) {
            phpScript = "generate_rent_receipt_pdf.php";
            fileNamePrefix = "RentReceipt_";
        } else {
            phpScript = "generate_owner_statement_pdf.php";
            fileNamePrefix = "OwnerStatement_";
        }
        // --- END OF NEW LOGIC ---

        String url = ApiClient.BASE_URL + phpScript + "?invoice_id=" + item.getInvoiceId();

        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));
        String fileName = fileNamePrefix + item.getStallId() + "_" + item.getInvoiceId() + ".pdf";
        request.setTitle(fileNamePrefix.replace("_", " "));
        request.setDescription("Downloading " + fileName);
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, fileName);

        DownloadManager downloadManager = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
        if (downloadManager != null) {
            downloadManager.enqueue(request);
            Toast.makeText(this, "Statement download started...", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Unable to access Download Manager.", Toast.LENGTH_SHORT).show();
        }
    }

    private void setupBottomNavigation() {
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation_bar);
        bottomNavigationView.setSelectedItemId(R.id.nav_admin_view_reports);

        bottomNavigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.nav_admin_view_reports) {
                return true;
            } else if (itemId == R.id.nav_admin_home) {
                startActivity(new Intent(getApplicationContext(), AhomeActivity.class));
                overridePendingTransition(0, 0);
                finish();
                return true;
            } else if (itemId == R.id.nav_admin_new_stall) {
                startActivity(new Intent(getApplicationContext(), Anew_stallActivity.class));
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