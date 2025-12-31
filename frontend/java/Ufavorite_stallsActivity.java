package com.simats.foodstall;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.simats.foodstall.adapter.FavoriteStallsAdapter;
import com.simats.foodstall.model.FavoriteStall;
import com.simats.foodstall.model.FavoriteStallsResponse;
import com.simats.foodstall.model.StatusResponse;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Ufavorite_stallsActivity extends AppCompatActivity {

    private ArrayList<FavoriteStall> favoriteStalls = new ArrayList<>();
    private FavoriteStallsAdapter adapter;
    private String studentId;
    private TextView emptyView; // To show when no favorites are found

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ufavorite_stalls);

        findViewById(R.id.backButton).setOnClickListener(v -> finish());
        // Note: Make sure to add a TextView with the id 'emptyView' to your ufavorite_stalls.xml
        emptyView = findViewById(R.id.emptyView);

        SharedPreferences prefs = getSharedPreferences("StallSpotPrefs", MODE_PRIVATE);
        studentId = prefs.getString("STUDENT_ID", null);

        if (studentId == null) {
            Toast.makeText(this, "Could not verify user. Please log in again.", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        RecyclerView recyclerView = findViewById(R.id.favoritesRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        adapter = new FavoriteStallsAdapter(favoriteStalls, (stall, position) -> {
            // Handle the click on the remove (heart) icon in the list
            unfavoriteStall(stall, position);
        });
        recyclerView.setAdapter(adapter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Fetch fresh data every time the screen is shown
        fetchFavorites();
    }

    private void fetchFavorites() {
        // TODO: Show a loading indicator
        if (emptyView != null) emptyView.setVisibility(View.GONE);

        ApiClient.getClient().create(ApiService.class).getFavoriteStalls(studentId).enqueue(new Callback<FavoriteStallsResponse>() {
            @Override
            public void onResponse(Call<FavoriteStallsResponse> call, Response<FavoriteStallsResponse> response) {
                // TODO: Hide loading indicator
                if (response.isSuccessful() && response.body() != null && "success".equals(response.body().getStatus())) {
                    favoriteStalls.clear();
                    favoriteStalls.addAll(response.body().getFavorites());
                    adapter.notifyDataSetChanged();

                    if (favoriteStalls.isEmpty()) {
                        if (emptyView != null) emptyView.setVisibility(View.VISIBLE);
                    }
                }
            }

            @Override
            public void onFailure(Call<FavoriteStallsResponse> call, Throwable t) {
                // TODO: Hide loading indicator
                Toast.makeText(Ufavorite_stallsActivity.this, "Failed to load favorites.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void unfavoriteStall(FavoriteStall stall, int position) {
        ApiClient.getClient().create(ApiService.class).toggleFavorite(studentId, stall.getStallId()).enqueue(new Callback<StatusResponse>() {
            @Override
            public void onResponse(Call<StatusResponse> call, Response<StatusResponse> response) {
                if (response.isSuccessful() && response.body() != null && "success".equals(response.body().getStatus())) {
                    Toast.makeText(Ufavorite_stallsActivity.this, stall.getName() + " removed from favorites", Toast.LENGTH_SHORT).show();
                    favoriteStalls.remove(position);
                    adapter.notifyItemRemoved(position);
                    if (favoriteStalls.isEmpty()) {
                        if (emptyView != null) emptyView.setVisibility(View.VISIBLE);
                    }
                } else {
                    Toast.makeText(Ufavorite_stallsActivity.this, "Could not remove favorite.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<StatusResponse> call, Throwable t) {
                Toast.makeText(Ufavorite_stallsActivity.this, "Network error.", Toast.LENGTH_SHORT).show();
            }
        });
    }
}