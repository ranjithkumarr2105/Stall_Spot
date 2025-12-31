package com.simats.foodstall;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.Priority;
import com.simats.foodstall.adapter.LovedByAdapter;
import com.simats.foodstall.adapter.MenuItemAdapter;
import com.simats.foodstall.adapter.ReviewAdapter;
import com.simats.foodstall.model.MenuItem;
import com.simats.foodstall.model.OrderItem;
import com.simats.foodstall.model.Review;
import com.simats.foodstall.model.StallMenuResponse;
import com.simats.foodstall.model.StatusResponse;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.stream.Collectors;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UviewmenuActivity extends AppCompatActivity implements ReviewAdapter.OnReviewInteractionListener {

    private static final int CONFIRM_ORDER_REQUEST_CODE = 1;
    private ArrayList<OrderItem> cartItems = new ArrayList<>();
    private FrameLayout loadingOverlay;
    private Button viewCartButton;
    private LinearLayout bottomCartBar, locationInfoLayout;
    private TextView cartDetailsText, stallNameTitle, distanceTextView, walkTimeTextView;
    private EditText searchBarEditText;
    private ImageView searchButton, favoriteButton;
    private RecyclerView menuRecyclerView, lovedByRecyclerView, reviewsRecyclerView;
    private CardView todaysSpecialCard, popularDishCard;
    private MenuItemAdapter menuAdapter;
    private LovedByAdapter lovedByAdapter;
    private ReviewAdapter reviewAdapter;
    private List<MenuItem> allMenuItems = new ArrayList<>();
    private List<Review> allReviews = new ArrayList<>();
    private String stallId, studentId, studentName;
    private boolean isFavorite = false;

    private FusedLocationProviderClient fusedLocationClient;
    private LocationCallback locationCallback;
    private Location userLocation;
    private StallMenuResponse.StallMenuData stallData;
    private final ActivityResultLauncher<String> requestPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
                if (isGranted) {
                    startLocationUpdates();
                } else {
                    Toast.makeText(this, "Location permission is required to show distance.", Toast.LENGTH_LONG).show();
                    locationInfoLayout.setVisibility(View.GONE);
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.uviewmenu);

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        SharedPreferences sp = getSharedPreferences("StallSpotPrefs", MODE_PRIVATE);
        studentId = sp.getString("STUDENT_ID", null);
        studentName = sp.getString("USER_NAME", "You");
        stallId = getIntent().getStringExtra("STALL_ID");

        bindViews();

        if (stallId == null || studentId == null) {
            Toast.makeText(this, "Error: Missing data.", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        setupClickListeners();
        setupRecyclerViews();
        fetchStallData();
    }

    @Override
    protected void onResume() {
        super.onResume();
        checkPermissionAndStartLocationUpdates();
    }

    @Override
    protected void onPause() {
        super.onPause();
        stopLocationUpdates();
    }

    private void bindViews() {
        loadingOverlay = findViewById(R.id.loadingOverlay);
        viewCartButton = findViewById(R.id.viewCartButton);
        bottomCartBar = findViewById(R.id.bottomCartBar);
        cartDetailsText = findViewById(R.id.cartDetailsText);
        menuRecyclerView = findViewById(R.id.menu_recycler_view);
        lovedByRecyclerView = findViewById(R.id.loved_by_recycler_view);
        reviewsRecyclerView = findViewById(R.id.reviews_recycler_view);
        stallNameTitle = findViewById(R.id.shop_name_title);
        searchButton = findViewById(R.id.searchButton);
        searchBarEditText = findViewById(R.id.searchBarEditText);
        todaysSpecialCard = findViewById(R.id.todaysSpecialCard);
        popularDishCard = findViewById(R.id.popularDishCard);
        favoriteButton = findViewById(R.id.favoriteButton);
        distanceTextView = findViewById(R.id.distanceTextView);
        walkTimeTextView = findViewById(R.id.walkTimeTextView);
        locationInfoLayout = findViewById(R.id.locationInfoLayout);
    }

    private void setupRecyclerViews() {
        lovedByRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        lovedByAdapter = new LovedByAdapter(this::showParcelTypeDialog);
        lovedByRecyclerView.setAdapter(lovedByAdapter);

        menuRecyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        menuAdapter = new MenuItemAdapter(this::showParcelTypeDialog);
        menuRecyclerView.setAdapter(menuAdapter);

        reviewsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        reviewAdapter = new ReviewAdapter(new ArrayList<>(), studentId, this);
        reviewsRecyclerView.setAdapter(reviewAdapter);

        menuRecyclerView.setNestedScrollingEnabled(false);
        lovedByRecyclerView.setNestedScrollingEnabled(false);
        reviewsRecyclerView.setNestedScrollingEnabled(false);
    }

    private void setupClickListeners() {
        findViewById(R.id.backButton).setOnClickListener(v -> onBackPressed());
        findViewById(R.id.searchButton).setOnClickListener(v -> toggleSearchBar());
        ImageButton addReviewButton = findViewById(R.id.addReviewButton);
        addReviewButton.setOnClickListener(v -> showAddReviewDialog());
        favoriteButton.setOnClickListener(v -> toggleFavoriteStatusOnServer());

        searchBarEditText.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) { filterMenu(s.toString()); }
            @Override public void afterTextChanged(Editable s) {}
        });

        viewCartButton.setOnClickListener(v -> {
            if (cartItems.isEmpty()) {
                Toast.makeText(this, "Your cart is empty!", Toast.LENGTH_SHORT).show();
                return;
            }
            Intent intent = new Intent(UviewmenuActivity.this, Uconfirm_orderActivity.class);
            intent.putParcelableArrayListExtra("CART_ITEMS", cartItems);
            intent.putExtra("STALL_ID", stallId);
            startActivityForResult(intent, CONFIRM_ORDER_REQUEST_CODE);
        });
    }

    private void checkPermissionAndStartLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION);
        } else {
            startLocationUpdates();
        }
    }

    private void startLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        LocationRequest locationRequest = new LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 10000)
                .setWaitForAccurateLocation(false)
                .setMinUpdateIntervalMillis(5000)
                .build();

        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(@NonNull LocationResult locationResult) {
                for (Location location : locationResult.getLocations()) {
                    if (location != null) {
                        userLocation = location;
                        calculateAndDisplayDistance();
                    }
                }
            }
        };

        fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper());
    }

    private void stopLocationUpdates() {
        if (locationCallback != null) {
            fusedLocationClient.removeLocationUpdates(locationCallback);
        }
    }

    private void fetchStallData() {
        showLoadingOverlay();
        ApiClient.getClient().create(ApiService.class).getStallMenu(stallId, studentId).enqueue(new Callback<StallMenuResponse>() {
            @Override
            public void onResponse(Call<StallMenuResponse> call, Response<StallMenuResponse> response) {
                hideLoadingOverlay();
                if (response.isSuccessful() && response.body() != null && "success".equals(response.body().getStatus())) {
                    stallData = response.body().getData();
                    updateUiWithData(stallData);
                    calculateAndDisplayDistance();
                } else {
                    Toast.makeText(UviewmenuActivity.this, "Failed to load menu.", Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onFailure(Call<StallMenuResponse> call, Throwable t) {
                hideLoadingOverlay();
                Log.e("ViewMenu", "API Failure: " + t.getMessage());
                Toast.makeText(UviewmenuActivity.this, "Network Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void calculateAndDisplayDistance() {
        if (userLocation == null || stallData == null) {
            return;
        }
        StallMenuResponse.StallDetails stallDetails = stallData.getStallDetails();
        if (stallDetails.getLatitude() == 0.0 && stallDetails.getLongitude() == 0.0) {
            locationInfoLayout.setVisibility(View.GONE);
            return;
        }
        Location stallLocation = new Location("stall");
        stallLocation.setLatitude(stallDetails.getLatitude());
        stallLocation.setLongitude(stallDetails.getLongitude());
        float distanceInMeters = userLocation.distanceTo(stallLocation);

        double walkTimeInMinutes = (distanceInMeters / 1.4) / 60.0;
        String walkTimeString;

        if (distanceInMeters < 10) {
            walkTimeString = "You are here";
        } else if (walkTimeInMinutes < 1.0) {
            walkTimeString = "< 1 min walk";
        } else {
            walkTimeString = String.format(Locale.getDefault(), "%d min walk", (int) Math.ceil(walkTimeInMinutes));
        }

        distanceTextView.setText(String.format(Locale.getDefault(), "%.0f meters away", distanceInMeters));
        walkTimeTextView.setText(walkTimeString);
        locationInfoLayout.setVisibility(View.VISIBLE);
    }

    private void updateUiWithData(StallMenuResponse.StallMenuData data) {
        stallNameTitle.setText(data.getStallDetails().getStallName());
        isFavorite = data.isFavorite();
        updateFavoriteIcon();
        MenuItem special = data.getTodaysSpecial();
        if (special != null) {
            todaysSpecialCard.setVisibility(View.VISIBLE);
            ImageView specialImage = todaysSpecialCard.findViewById(R.id.todaysSpecialImage);
            TextView specialName = todaysSpecialCard.findViewById(R.id.todaysSpecialName);
            TextView specialPrice = todaysSpecialCard.findViewById(R.id.todaysSpecialPrice);
            Button specialAddButton = todaysSpecialCard.findViewById(R.id.specialAddToCartButton);
            specialName.setText(special.getName());
            specialPrice.setText(String.format(Locale.getDefault(), "₹%.2f", special.getPrice()));
            if (special.getImageUrl() != null && !special.getImageUrl().isEmpty()) {
                Glide.with(this).load(ApiClient.BASE_URL + "uploads/" + special.getImageUrl()).placeholder(TextDrawableUtil.getInitialDrawable(special.getName())).into(specialImage);
            } else {
                specialImage.setImageDrawable(TextDrawableUtil.getInitialDrawable(special.getName()));
            }
            specialAddButton.setOnClickListener(v -> showParcelTypeDialog(special));
        } else {
            todaysSpecialCard.setVisibility(View.GONE);
        }
        MenuItem popularDish = data.getPopularDish();
        if (popularDish != null) {
            popularDishCard.setVisibility(View.VISIBLE);
            TextView lovedByTextView = popularDishCard.findViewById(R.id.lovedByTextView);
            lovedByTextView.setText(String.format(Locale.getDefault(), "❤️ Loved by %d+ students", popularDish.getSoldCount()));
            List<MenuItem> popularList = new ArrayList<>();
            popularList.add(popularDish);
            lovedByAdapter.submitList(popularList);
        } else {
            popularDishCard.setVisibility(View.GONE);
        }
        allMenuItems.clear();
        allMenuItems.addAll(data.getFullMenu());
        menuAdapter.submitList(allMenuItems);
        allReviews.clear();
        allReviews.addAll(data.getReviews());
        reviewAdapter.updateList(allReviews);
    }

    private void filterMenu(String query) {
        String lowerCaseQuery = query.toLowerCase(Locale.ROOT);
        if (allMenuItems == null) return;
        List<MenuItem> filteredList = allMenuItems.stream()
                .filter(item -> item.getName().toLowerCase().contains(lowerCaseQuery) || String.valueOf(item.getPrice()).contains(lowerCaseQuery))
                .collect(Collectors.toList());
        menuAdapter.submitList(filteredList);
    }

    private void updateFavoriteIcon() {
        if (isFavorite) {
            favoriteButton.setImageResource(R.drawable.ic_heart_filled);
        } else {
            favoriteButton.setImageResource(R.drawable.ic_heart_outline);
        }
    }

    private void toggleFavoriteStatusOnServer() {
        isFavorite = !isFavorite;
        updateFavoriteIcon();
        ApiClient.getClient().create(ApiService.class).toggleFavorite(studentId, stallId).enqueue(new Callback<StatusResponse>() {
            @Override
            public void onResponse(Call<StatusResponse> call, Response<StatusResponse> response) {
                if (response.isSuccessful() && response.body() != null && "success".equals(response.body().getStatus())) {
                    Toast.makeText(UviewmenuActivity.this, response.body().getMessage(), Toast.LENGTH_SHORT).show();
                } else {
                    isFavorite = !isFavorite;
                    updateFavoriteIcon();
                    Toast.makeText(UviewmenuActivity.this, "Could not update favorite status.", Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onFailure(Call<StatusResponse> call, Throwable t) {
                isFavorite = !isFavorite;
                updateFavoriteIcon();
                Toast.makeText(UviewmenuActivity.this, "Network error. Could not update favorite.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void toggleSearchBar() {
        View searchBarContainer = findViewById(R.id.searchBarContainer);
        if (searchBarContainer.getVisibility() == View.GONE) {
            stallNameTitle.setVisibility(View.GONE);
            searchBarContainer.setVisibility(View.VISIBLE);
            searchBarEditText.requestFocus();
        } else {
            stallNameTitle.setVisibility(View.VISIBLE);
            searchBarContainer.setVisibility(View.GONE);
            searchBarEditText.setText("");
        }
    }

    private void showAddReviewDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_add_review, null);
        builder.setView(dialogView);
        final RatingBar ratingBar = dialogView.findViewById(R.id.reviewRatingBar);
        final EditText reviewEditText = dialogView.findViewById(R.id.reviewEditText);
        builder.setTitle("Add Your Review")
                .setPositiveButton("Submit", (dialog, id) -> {
                    float rating = ratingBar.getRating();
                    String reviewText = reviewEditText.getText().toString().trim();
                    if (rating == 0) {
                        Toast.makeText(this, "Please provide a rating.", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    submitReviewToServer(rating, reviewText);
                })
                .setNegativeButton("Cancel", (dialog, id) -> dialog.cancel());
        builder.create().show();
    }

    private void submitReviewToServer(float rating, String reviewText) {
        showLoadingOverlay();
        ApiClient.getClient().create(ApiService.class).submitReview(stallId, studentId, rating, reviewText)
                .enqueue(new Callback<StatusResponse>() {
                    @Override
                    public void onResponse(Call<StatusResponse> call, Response<StatusResponse> response) {
                        hideLoadingOverlay();
                        if (response.isSuccessful() && response.body() != null && "success".equals(response.body().getStatus())) {
                            Toast.makeText(UviewmenuActivity.this, "Review submitted!", Toast.LENGTH_SHORT).show();
                            fetchStallData();
                        } else {
                            Toast.makeText(UviewmenuActivity.this, "Failed to submit review.", Toast.LENGTH_SHORT).show();
                        }
                    }
                    @Override
                    public void onFailure(Call<StatusResponse> call, Throwable t) {
                        hideLoadingOverlay();
                        Toast.makeText(UviewmenuActivity.this, "Network Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    @Override
    public void onDeleteClick(Review review, int position) {
        new AlertDialog.Builder(this)
                .setTitle("Delete Review")
                .setMessage("Are you sure you want to delete your review?")
                .setPositiveButton("Delete", (dialog, which) -> deleteReviewFromServer(review, position))
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void deleteReviewFromServer(Review review, int position) {
        showLoadingOverlay();
        ApiClient.getClient().create(ApiService.class).deleteReview(stallId, review.getStudentId())
                .enqueue(new Callback<StatusResponse>() {
                    @Override
                    public void onResponse(Call<StatusResponse> call, Response<StatusResponse> response) {
                        hideLoadingOverlay();
                        if (response.isSuccessful() && response.body() != null && "success".equals(response.body().getStatus())) {
                            Toast.makeText(UviewmenuActivity.this, "Review deleted.", Toast.LENGTH_SHORT).show();
                            allReviews.remove(position);
                            reviewAdapter.updateList(allReviews);
                        } else {
                            Toast.makeText(UviewmenuActivity.this, "Failed to delete review.", Toast.LENGTH_SHORT).show();
                        }
                    }
                    @Override
                    public void onFailure(Call<StatusResponse> call, Throwable t) {
                        hideLoadingOverlay();
                        Toast.makeText(UviewmenuActivity.this, "Network error.", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    // --- START OF MODIFIED SECTION ---
    private void showParcelTypeDialog(MenuItem item) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_select_parcel_type, null);
        builder.setView(dialogView);

        final AlertDialog dialog = builder.create();

        TextView dialogTitle = dialogView.findViewById(R.id.dialog_title);
        // CORRECTED: Changed variable types from Button to LinearLayout to match the XML
        LinearLayout dineInButton = dialogView.findViewById(R.id.dineInButton);
        LinearLayout parcelButton = dialogView.findViewById(R.id.parcelButton);
        LinearLayout preParcelButton = dialogView.findViewById(R.id.preParcelButton);

        dialogTitle.setText("Add \"" + item.getName() + "\"?");

        dineInButton.setOnClickListener(v -> {
            processAddItemToCart(item, "Dine-in");
            dialog.dismiss();
        });

        parcelButton.setOnClickListener(v -> {
            processAddItemToCart(item, "Parcel");
            dialog.dismiss();
        });

        preParcelButton.setOnClickListener(v -> {
            processAddItemToCart(item, "Pre-parcel");
            dialog.dismiss();
        });

        dialog.show();
    }
    // --- END OF MODIFIED SECTION ---

    private void processAddItemToCart(MenuItem item, String parcelStatus) {
        for (OrderItem orderItem : cartItems) {
            if (Objects.equals(orderItem.getName(), item.getName()) && Objects.equals(orderItem.getParcelStatus(), parcelStatus)) {
                orderItem.incrementQuantity();
                updateCartBar();
                Toast.makeText(this, "Added another " + item.getName() + " (" + parcelStatus + ")", Toast.LENGTH_SHORT).show();
                return;
            }
        }
        cartItems.add(new OrderItem(item.getName(), 1, item.getPrice(), parcelStatus));
        updateCartBar();
        Toast.makeText(this, "Added " + item.getName() + " (" + parcelStatus + ")", Toast.LENGTH_SHORT).show();
    }

    private void updateCartBar() {
        if (cartItems.isEmpty()) {
            bottomCartBar.setVisibility(View.GONE);
        } else {
            bottomCartBar.setVisibility(View.VISIBLE);
            int totalItems = 0;
            double totalPrice = 0;
            for (OrderItem orderItem : cartItems) {
                totalItems += orderItem.getQuantity();
                totalPrice += orderItem.getQuantity() * orderItem.getPrice();
            }
            cartDetailsText.setText(String.format(Locale.getDefault(), "%d items • ₹%.2f", totalItems, totalPrice));
        }
    }

    private void showLoadingOverlay() {
        if (loadingOverlay != null) {
            loadingOverlay.setVisibility(View.VISIBLE);
            ImageView loadingIcon = loadingOverlay.findViewById(R.id.loadingIcon);
            if (loadingIcon != null) {
                Animation rotation = AnimationUtils.loadAnimation(this, R.anim.hourglass_rotation);
                loadingIcon.startAnimation(rotation);
            }
        }
    }

    private void hideLoadingOverlay() {
        if (loadingOverlay != null && loadingOverlay.getVisibility() == View.VISIBLE) {
            ImageView loadingIcon = loadingOverlay.findViewById(R.id.loadingIcon);
            if (loadingIcon != null) loadingIcon.clearAnimation();
            loadingOverlay.setVisibility(View.GONE);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CONFIRM_ORDER_REQUEST_CODE && resultCode == RESULT_OK) {
            if (data != null && data.hasExtra("UPDATED_CART_ITEMS")) {
                cartItems = data.getParcelableArrayListExtra("UPDATED_CART_ITEMS");
                updateCartBar();
            }
        }
    }
}