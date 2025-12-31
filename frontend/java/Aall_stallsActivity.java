package com.simats.foodstall;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
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

public class Aall_stallsActivity extends AppCompatActivity {

    private AdminStallListAdapter adapter;
    private SwipeRefreshLayout swipeRefreshLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.aall_stalls);

        findViewById(R.id.backButton).setOnClickListener(v -> onBackPressed());

        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout);
        RecyclerView allStallsRecyclerView = findViewById(R.id.allStallsRecyclerView);
        allStallsRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Use the new universal adapter with the general view type
        adapter = new AdminStallListAdapter(AdminStallListAdapter.VIEW_TYPE_GENERAL, null);
        allStallsRecyclerView.setAdapter(adapter);

        swipeRefreshLayout.setOnRefreshListener(() -> fetchStalls("all"));
        fetchStalls("all");
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
                    Toast.makeText(Aall_stallsActivity.this, "Failed to load stalls.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<AdminStall>> call, @NonNull Throwable t) {
                swipeRefreshLayout.setRefreshing(false);
                Toast.makeText(Aall_stallsActivity.this, "Network Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}