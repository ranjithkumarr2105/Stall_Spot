package com.simats.foodstall;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import com.simats.foodstall.adapter.AdminStallListAdapter;
import com.simats.foodstall.model.AdminStall;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Apending_stallsActivity extends AppCompatActivity implements AdminStallListAdapter.OnStallClickListener {

    private AdminStallListAdapter adapter;
    private SwipeRefreshLayout swipeRefreshLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.apending_stalls); // Ensure you have an apending_stalls.xml with SwipeRefreshLayout

        findViewById(R.id.backButton).setOnClickListener(v -> onBackPressed());

        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout);
        RecyclerView pendingStallsRecyclerView = findViewById(R.id.pendingStallsRecyclerView);
        pendingStallsRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        adapter = new AdminStallListAdapter(AdminStallListAdapter.VIEW_TYPE_STATUS, this);
        pendingStallsRecyclerView.setAdapter(adapter);

        swipeRefreshLayout.setOnRefreshListener(() -> fetchStalls("pending"));
        fetchStalls("pending");
    }

    @Override
    public void onViewClick(AdminStall stall) {
        Intent intent = new Intent(this, Astall_detailsActivity.class);
        intent.putExtra("STALL_DATA", stall); // Pass the whole object
        startActivity(intent);
    }

    private void fetchStalls(String status) {
        swipeRefreshLayout.setRefreshing(true);
        ApiClient.getClient().create(ApiService.class).getStallsByStatus(status).enqueue(new Callback<List<AdminStall>>() {
            @Override
            public void onResponse(@NonNull Call<List<AdminStall>> call, @NonNull Response<List<AdminStall>> response) {
                swipeRefreshLayout.setRefreshing(false);
                if (response.isSuccessful() && response.body() != null) {
                    adapter.updateStalls(response.body());
                } else {
                    Toast.makeText(Apending_stallsActivity.this, "Failed to load pending stalls.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<AdminStall>> call, @NonNull Throwable t) {
                swipeRefreshLayout.setRefreshing(false);
                Toast.makeText(Apending_stallsActivity.this, "Network Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}