package com.simats.foodstall;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import com.simats.foodstall.adapter.AeditstallAdapter;
import com.simats.foodstall.model.StallEdit;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AeditstallActivity extends AppCompatActivity implements AeditstallAdapter.OnStallEditClickListener {

    private AeditstallAdapter adapter;
    private List<StallEdit> originalStallsList = new ArrayList<>(); // Master list
    private RecyclerView stallsRecyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.aeditstall);

        findViewById(R.id.backButton).setOnClickListener(v -> onBackPressed());

        setupRecyclerView();
        setupSearchView();

        fetchStallsFromServer(); // Fetch live data instead of dummy data
    }

    private void setupRecyclerView() {
        stallsRecyclerView = findViewById(R.id.stallsRecyclerView);
        stallsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        // Initialize adapter with an empty list
        adapter = new AeditstallAdapter(new ArrayList<>(), this);
        stallsRecyclerView.setAdapter(adapter);
    }

    private void fetchStallsFromServer() {
        // You can add a loading indicator here
        ApiClient.getClient().create(ApiService.class).getAllStalls().enqueue(new Callback<List<StallEdit>>() {
            @Override
            public void onResponse(Call<List<StallEdit>> call, Response<List<StallEdit>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    originalStallsList.clear();
                    originalStallsList.addAll(response.body());
                    adapter.filterList(new ArrayList<>(originalStallsList)); // Display the full list initially
                } else {
                    Toast.makeText(AeditstallActivity.this, "Failed to load stalls.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<StallEdit>> call, Throwable t) {
                Toast.makeText(AeditstallActivity.this, "Network Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setupSearchView() {
        SearchView searchView = findViewById(R.id.searchView);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                filter(newText);
                return true;
            }
        });
    }

    private void filter(String text) {
        List<StallEdit> filteredList = new ArrayList<>();
        for (StallEdit item : originalStallsList) { // Filter from the original master list
            if (item.getStallName().toLowerCase(Locale.ROOT).contains(text.toLowerCase(Locale.ROOT)) ||
                    item.getOwnerName().toLowerCase(Locale.ROOT).contains(text.toLowerCase(Locale.ROOT))) {
                filteredList.add(item);
            }
        }
        adapter.filterList(filteredList);
    }

    @Override
    public void onEditClick(StallEdit stall) {
        Intent intent = new Intent(this, AupdatestallActivity.class);
        // Pass the entire StallEdit object to the next activity
        intent.putExtra("STALL_DATA", stall);
        startActivity(intent);
    }

    // The getDummyStallData() method is no longer needed and can be deleted.
}