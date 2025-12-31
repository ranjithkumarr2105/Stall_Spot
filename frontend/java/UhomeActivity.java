package com.simats.foodstall;

// Imports (including permissions) remain the same as the previous correct version
import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import android.Manifest;
import android.content.pm.PackageManager;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.core.content.ContextCompat; // Keep this one
// --- End Imports ---

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
// Removed duplicate ContextCompat import
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.simats.foodstall.adapter.PopularDishesAdapter;
import com.simats.foodstall.adapter.SpecialsAdapter;
import com.simats.foodstall.adapter.StallsAdapter;
import com.simats.foodstall.model.HomeDataResponse;
import com.simats.foodstall.model.Stall;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UhomeActivity extends AppCompatActivity {

    // Member variables remain the same
    private RecyclerView specialsRecyclerView, popularDishesRecyclerView, stallsRecyclerView;
    private SpecialsAdapter specialsAdapter;
    private PopularDishesAdapter popularDishesAdapter;
    private StallsAdapter stallsAdapter;
    private List<HomeDataResponse.SpecialDish> allSpecials = new ArrayList<>();
    private List<HomeDataResponse.PopularDish> allPopularDishes = new ArrayList<>();
    private List<Stall> allStalls = new ArrayList<>();
    private Button openNowBtn, topRatedBtn, favoriteBtn;
    private TextView welcomeTextView;
    private EditText searchBar;
    private FrameLayout loadingOverlay;
    private ImageView loadingIcon;
    private String studentId;
    private String activeFilter = "open_now";
    private ActivityResultLauncher<String> requestPermissionLauncher;
    private String stallIdToNavigate = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.uhome);

        bindViews();
        setFilterButtonSelected(openNowBtn);

        SharedPreferences sharedPreferences = getSharedPreferences("StallSpotPrefs", MODE_PRIVATE);
        studentId = sharedPreferences.getString("STUDENT_ID", null);
        String userName = sharedPreferences.getString("USER_NAME", "User");
        // Defend against null TextView
        if (welcomeTextView != null) {
            welcomeTextView.setText("Hi, " + userName);
        }

        if (studentId == null || studentId.isEmpty()) {
            Toast.makeText(this, "Error: Could not verify user. Please log in again.", Toast.LENGTH_LONG).show();
            startActivity(new Intent(this, LoginActivity.class).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
            finish();
            return;
        }

        registerPermissionLauncher();
        setupRecyclerViews();
        fetchHomeData();
        setupFilterAndSearch();
        setupBottomNavigation();
    }

    private void registerPermissionLauncher() {
        requestPermissionLauncher =
                registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
                    if (isGranted) {
                        Log.d("Permission", "Location permission granted after request.");
                        if (stallIdToNavigate != null) {
                            startFetchLocationActivity(stallIdToNavigate);
                        }
                    } else {
                        Log.d("Permission", "Location permission denied.");
                        Toast.makeText(this, "Location permission is needed to show distance and navigate.", Toast.LENGTH_LONG).show();
                    }
                    stallIdToNavigate = null;
                });
    }


    private void bindViews(){
        welcomeTextView = findViewById(R.id.welcomeTextView);
        searchBar = findViewById(R.id.search_bar);
        loadingOverlay = findViewById(R.id.loadingOverlay);
        loadingIcon = findViewById(R.id.loadingIcon);
        openNowBtn = findViewById(R.id.openNowBtn);
        topRatedBtn = findViewById(R.id.topRatedBtn);
        favoriteBtn = findViewById(R.id.favoriteBtn);
        findViewById(R.id.settingsIcon).setOnClickListener(v -> startActivity(new Intent(UhomeActivity.this, UsettingsActivity.class)));
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (studentId != null) {
            // fetchHomeData(); // Still commented out as per your previous version
        }
    }

    private void setupRecyclerViews() {
        specialsRecyclerView = findViewById(R.id.specials_recycler_view);
        specialsRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        specialsAdapter = new SpecialsAdapter();
        specialsRecyclerView.setAdapter(specialsAdapter);
        specialsAdapter.setOnItemClickListener(dish -> {
            if (dish.isStallOpen()) {
                handleItemClick(dish.getStallId());
            } else {
                Toast.makeText(this, dish.getStallName() + " is currently closed.", Toast.LENGTH_SHORT).show();
            }
        });


        popularDishesRecyclerView = findViewById(R.id.popular_dishes_recycler_view);
        popularDishesRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        popularDishesAdapter = new PopularDishesAdapter();
        popularDishesRecyclerView.setAdapter(popularDishesAdapter);
        popularDishesAdapter.setOnItemClickListener(dish -> {
            if (dish.isStallOpen()) {
                handleItemClick(dish.getStallId());
            } else {
                Toast.makeText(this, dish.getStallName() + " is currently closed.", Toast.LENGTH_SHORT).show();
            }
        });


        stallsRecyclerView = findViewById(R.id.stalls_recycler_view);
        stallsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        stallsAdapter = new StallsAdapter();
        stallsRecyclerView.setAdapter(stallsAdapter);
        stallsAdapter.setOnItemClickListener(stall -> {
            if (stall.isOpen()) {
                handleItemClick(stall.getStallId());
            } else {
                Toast.makeText(this, stall.getStallName() + " is currently closed.", Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void handleItemClick(String stallId) {
        if (stallId == null || stallId.isEmpty()) {
            Toast.makeText(this, "Error: Stall ID not found.", Toast.LENGTH_SHORT).show();
            return;
        }

        if (ContextCompat.checkSelfPermission(
                this, Manifest.permission.ACCESS_FINE_LOCATION) ==
                PackageManager.PERMISSION_GRANTED) {
            Log.d("Permission", "Location permission already granted.");
            startFetchLocationActivity(stallId);
        } else {
            Log.d("Permission", "Location permission not granted. Requesting...");
            stallIdToNavigate = stallId;
            requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION);
        }
    }

    // Method to start the Ufetch_locationActivity
    private void startFetchLocationActivity(String stallId) {
        if (stallId != null && !stallId.isEmpty()) {
            Log.d(TAG, "Starting Ufetch_locationActivity for stallId: " + stallId); // Log before starting
            Intent intent = new Intent(UhomeActivity.this, Ufetch_locationActivity.class);
            intent.putExtra("STALL_ID", stallId);
            // [THE FIX] Added the missing extra to indicate the user flow
            intent.putExtra("NAVIGATE_TO_MENU", true);
            startActivity(intent);
        } else {
            // Use the correct TAG defined in this class
            Log.e("UhomeActivity", "Attempted to startFetchLocationActivity with null or empty stallId");
        }
    }


    private void fetchHomeData() {
        showLoadingOverlay();
        ApiClient.getClient().create(ApiService.class).getHomeData(studentId).enqueue(new Callback<HomeDataResponse>() {
            @Override
            public void onResponse(@NonNull Call<HomeDataResponse> call, @NonNull Response<HomeDataResponse> response) {
                hideLoadingOverlay();
                if (response.isSuccessful() && response.body() != null && "success".equals(response.body().getStatus())) {
                    updateUI(response.body().getData());
                } else {
                    Toast.makeText(UhomeActivity.this, "Failed to load home screen data.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<HomeDataResponse> call, @NonNull Throwable t) {
                hideLoadingOverlay();
                Toast.makeText(UhomeActivity.this, "Network Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                Log.e("UhomeFailure", "API Call Failed: ", t);
            }
        });
    }

    private void updateUI(HomeDataResponse.HomeData data) {
        if (data == null) return;
        if (welcomeTextView != null && data.getUserFullname() != null) {
            welcomeTextView.setText("Hi, " + data.getUserFullname());
        }

        if (data.getSpecials() != null) {
            allSpecials.clear();
            allSpecials.addAll(data.getSpecials());
        } else {
            allSpecials.clear();
        }
        if (data.getPopularDishes() != null) {
            allPopularDishes.clear();
            allPopularDishes.addAll(data.getPopularDishes());
        } else {
            allPopularDishes.clear();
        }
        if (data.getStalls() != null) {
            allStalls.clear();
            allStalls.addAll(data.getStalls());
        } else {
            allStalls.clear();
        }

        performSearchAndFilter();
    }

    private void setupFilterAndSearch() {
        // Unchanged
        openNowBtn.setOnClickListener(v -> { activeFilter = "open_now"; setFilterButtonSelected(openNowBtn); performSearchAndFilter(); });
        topRatedBtn.setOnClickListener(v -> { activeFilter = "top_rated"; setFilterButtonSelected(topRatedBtn); performSearchAndFilter(); });
        favoriteBtn.setOnClickListener(v -> { activeFilter = "favorite"; setFilterButtonSelected(favoriteBtn); performSearchAndFilter(); });
        searchBar.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) { performSearchAndFilter(); }
            @Override public void afterTextChanged(Editable s) {}
        });
    }

    private void performSearchAndFilter() {
        // Unchanged
        String query = searchBar.getText().toString().toLowerCase(Locale.getDefault());

        List<HomeDataResponse.SpecialDish> filteredSpecials;
        if (allSpecials != null) {
            filteredSpecials = allSpecials.stream()
                    .filter(dish -> (dish.getDishName() != null && dish.getDishName().toLowerCase().contains(query)) ||
                            (dish.getStallName() != null && dish.getStallName().toLowerCase().contains(query)) ||
                            String.valueOf(dish.getPrice()).contains(query))
                    .collect(Collectors.toList());
        } else {
            filteredSpecials = new ArrayList<>();
        }
        if (specialsAdapter != null) specialsAdapter.submitList(filteredSpecials);

        List<HomeDataResponse.PopularDish> filteredPopular;
        if(allPopularDishes != null) {
            filteredPopular = allPopularDishes.stream()
                    .filter(dish -> (dish.getDishName() != null && dish.getDishName().toLowerCase().contains(query)) ||
                            (dish.getStallName() != null && dish.getStallName().toLowerCase().contains(query)) ||
                            String.valueOf(dish.getPrice()).contains(query))
                    .collect(Collectors.toList());
        } else {
            filteredPopular = new ArrayList<>();
        }
        if (popularDishesAdapter != null) popularDishesAdapter.submitList(filteredPopular);

        List<Stall> searchedStalls;
        if (allStalls != null) {
            searchedStalls = allStalls.stream()
                    .filter(stall -> stall.getStallName() != null && stall.getStallName().toLowerCase(Locale.getDefault()).contains(query))
                    .collect(Collectors.toList());
        } else {
            searchedStalls = new ArrayList<>();
        }

        List<Stall> finalFilteredStalls;
        switch (activeFilter) {
            case "top_rated":
                finalFilteredStalls = new ArrayList<>(searchedStalls);
                Collections.sort(finalFilteredStalls, (s1, s2) -> Double.compare(s2.getRating(), s1.getRating()));
                break;
            case "favorite":
                finalFilteredStalls = searchedStalls.stream().filter(Stall::isFavorite).collect(Collectors.toList());
                break;
            case "open_now":
            default:
                finalFilteredStalls = searchedStalls.stream().filter(Stall::isOpen).collect(Collectors.toList());
                break;
        }
        if (stallsAdapter != null) stallsAdapter.submitList(finalFilteredStalls);
    }

    private void setFilterButtonSelected(Button selectedButton) {
        // Unchanged
        Button[] allButtons = {openNowBtn, topRatedBtn, favoriteBtn};
        for (Button button : allButtons) {
            if (button == selectedButton) {
                button.setBackgroundResource(R.drawable.button_filter_active);
                button.setTextColor(ContextCompat.getColor(this, R.color.brand_pink));
            } else {
                button.setBackgroundResource(R.drawable.button_filter_inactive);
                button.setTextColor(ContextCompat.getColor(this, R.color.white));
            }
        }
    }

    private void showLoadingOverlay() {
        // Unchanged
        loadingOverlay.setVisibility(View.VISIBLE);
        Animation rotation = AnimationUtils.loadAnimation(this, R.anim.hourglass_rotation);
        loadingIcon.startAnimation(rotation);
    }

    private void hideLoadingOverlay() {
        // Unchanged
        if (loadingOverlay.getVisibility() == View.VISIBLE) {
            loadingIcon.clearAnimation();
            loadingOverlay.setVisibility(View.GONE);
        }
    }

    private void setupBottomNavigation() {
        // Unchanged
        BottomNavigationView bnv = findViewById(R.id.bottom_navigation_bar);
        bnv.setSelectedItemId(R.id.nav_home);
        bnv.setOnItemSelectedListener(item -> {
            int i = item.getItemId();
            if(i == R.id.nav_home) return true;
            else if (i == R.id.nav_orders) { startActivity(new Intent(getApplicationContext(), UordersActivity.class)); overridePendingTransition(0,0); finish(); return true; }
            else if (i == R.id.nav_wallet) { startActivity(new Intent(getApplicationContext(), UwalletActivity.class)); overridePendingTransition(0,0); finish(); return true; }
            else if (i == R.id.nav_profile) { startActivity(new Intent(getApplicationContext(), UeditprofileActivity.class)); overridePendingTransition(0,0); finish(); return true; }
            return false;
        });
    }
}