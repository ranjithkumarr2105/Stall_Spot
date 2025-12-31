package com.simats.foodstall;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.simats.foodstall.adapter.StallLocationAdapter;
import com.simats.foodstall.model.AStallLocation;
import com.simats.foodstall.model.StallLocationResponse;
import com.simats.foodstall.model.StatusResponse;
import java.util.List;
import java.util.Locale;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Aset_locationActivity extends AppCompatActivity {

    private EditText stallIdEditText, stallNameEditText, latitudeEditText, longitudeEditText;
    private LinearLayout manualCoordinatesLayout;
    private FrameLayout loadingOverlay;
    private ImageView loadingIcon;
    private TextView loadingText;
    private BottomSheetDialog bottomSheetDialog;

    private final ActivityResultLauncher<Intent> autoFetchResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == AppCompatActivity.RESULT_OK && result.getData() != null) {
                    Intent data = result.getData();
                    double latitude = data.getDoubleExtra("latitude", 0.0);
                    double longitude = data.getDoubleExtra("longitude", 0.0);

                    latitudeEditText.setText(String.format(Locale.US, "%.6f", latitude));
                    longitudeEditText.setText(String.format(Locale.US, "%.6f", longitude));
                    manualCoordinatesLayout.setVisibility(View.VISIBLE);
                    Toast.makeText(this, "Location fetched successfully!", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "Could not fetch location.", Toast.LENGTH_SHORT).show();
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.aset_location);

        stallIdEditText = findViewById(R.id.stallIdEditText);
        stallNameEditText = findViewById(R.id.stallNameEditText);
        latitudeEditText = findViewById(R.id.latitudeEditText);
        longitudeEditText = findViewById(R.id.longitudeEditText);
        manualCoordinatesLayout = findViewById(R.id.manualCoordinatesLayout);
        CardView autoFetchCard = findViewById(R.id.autoFetchCard);
        CardView manualEntryCard = findViewById(R.id.manualEntryCard);
        Button setLocationButton = findViewById(R.id.setLocationButton);
        loadingOverlay = findViewById(R.id.loadingOverlay);
        loadingIcon = findViewById(R.id.loadingIcon);
        loadingText = findViewById(R.id.loadingText);

        findViewById(R.id.backButton).setOnClickListener(v -> finish());

        autoFetchCard.setOnClickListener(v -> {
            Intent intent = new Intent(this, Ufetch_locationActivity.class);
            autoFetchResultLauncher.launch(intent);
        });

        manualEntryCard.setOnClickListener(v -> {
            manualCoordinatesLayout.setVisibility(manualCoordinatesLayout.getVisibility() == View.VISIBLE ? View.GONE : View.VISIBLE);
            if (manualCoordinatesLayout.getVisibility() == View.VISIBLE) {
                latitudeEditText.requestFocus();
            }
        });

        setLocationButton.setOnClickListener(v -> saveLocationToDatabase());
    }

    private void saveLocationToDatabase() {
        String stallId = stallIdEditText.getText().toString().trim();
        String latitudeStr = latitudeEditText.getText().toString().trim();
        String longitudeStr = longitudeEditText.getText().toString().trim();

        if (stallId.isEmpty() || latitudeStr.isEmpty() || longitudeStr.isEmpty()) {
            Toast.makeText(this, "Stall ID and coordinates are required.", Toast.LENGTH_SHORT).show();
            return;
        }

        startLoadingAnimation("Setting Location...");
        ApiClient.getClient().create(ApiService.class)
                .setStallLocation("set_location", stallId, latitudeStr, longitudeStr)
                .enqueue(new Cb("Location set successfully!", "Failed to set location. Check if Stall ID is correct and approved."));
    }

    private void fetchAndShowSavedLocations() {
        startLoadingAnimation("Fetching Locations...");
        ApiClient.getClient().create(ApiService.class).getStallLocations("get_locations")
                .enqueue(new Callback<StallLocationResponse>() {
                    @Override
                    public void onResponse(@NonNull Call<StallLocationResponse> call, @NonNull Response<StallLocationResponse> response) {
                        hideLoadingAnimation();
                        if (response.isSuccessful() && response.body() != null && "success".equals(response.body().getStatus())) {
                            showSavedLocationsDialog(response.body().getLocations());
                        } else {
                            Toast.makeText(Aset_locationActivity.this, "Failed to fetch locations.", Toast.LENGTH_SHORT).show();
                        }
                    }
                    @Override
                    public void onFailure(@NonNull Call<StallLocationResponse> call, @NonNull Throwable t) {
                        hideLoadingAnimation();
                        Toast.makeText(Aset_locationActivity.this, "Network Error.", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void showSavedLocationsDialog(List<AStallLocation> locations) {
        if (locations == null || locations.isEmpty()) {
            new AlertDialog.Builder(this).setTitle("Saved Locations").setMessage("No locations have been saved yet.").setPositiveButton("OK", null).show();
            return;
        }

        bottomSheetDialog = new BottomSheetDialog(this, R.style.BottomSheetDialogTheme);
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_location_list, findViewById(android.R.id.content), false);
        bottomSheetDialog.setContentView(dialogView);

        RecyclerView recyclerView = dialogView.findViewById(R.id.locationsRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));

        StallLocationAdapter adapter = new StallLocationAdapter(locations, new StallLocationAdapter.OnLocationInteractionListener() {
            @Override
            public void onEdit(AStallLocation location) {
                stallIdEditText.setText(location.getStallId());
                stallNameEditText.setText(location.getStallName());
                stallNameEditText.setEnabled(false);

                latitudeEditText.setText(String.format(Locale.US, "%.6f", location.getLatitude()));
                longitudeEditText.setText(String.format(Locale.US, "%.6f", location.getLongitude()));
                manualCoordinatesLayout.setVisibility(View.VISIBLE);
                bottomSheetDialog.dismiss();
            }
            @Override
            public void onDelete(AStallLocation location) {
                new AlertDialog.Builder(Aset_locationActivity.this)
                        .setTitle("Delete Location")
                        .setMessage("Are you sure you want to delete the location for " + location.getStallName() + "? This cannot be undone.")
                        .setPositiveButton("Delete", (d, w) -> deleteLocationFromDatabase(location))
                        .setNegativeButton("Cancel", null)
                        .show();
            }
        });
        recyclerView.setAdapter(adapter);

        dialogView.findViewById(R.id.closeButton).setOnClickListener(v -> bottomSheetDialog.dismiss());
        bottomSheetDialog.show();
    }

    private void deleteLocationFromDatabase(AStallLocation location) {
        startLoadingAnimation("Deleting Location...");
        ApiClient.getClient().create(ApiService.class).deleteStallLocation("delete_location", location.getStallId())
                .enqueue(new Cb("Location deleted.", "Failed to delete location."));
    }

    private class Cb implements Callback<StatusResponse> {
        private final String successMsg;
        private final String errorMsg;
        Cb(String s, String e){ this.successMsg = s; this.errorMsg = e; }
        @Override
        public void onResponse(@NonNull Call<StatusResponse> call, @NonNull Response<StatusResponse> response) {
            hideLoadingAnimation();
            if(response.isSuccessful() && response.body() != null && "success".equals(response.body().getStatus())) {
                Toast.makeText(Aset_locationActivity.this, successMsg, Toast.LENGTH_SHORT).show();
                if(bottomSheetDialog != null && bottomSheetDialog.isShowing()) {
                    bottomSheetDialog.dismiss();
                    fetchAndShowSavedLocations();
                }
                clearInputFields();
            } else {
                String serverError = (response.body() != null) ? response.body().getMessage() : errorMsg;
                Toast.makeText(Aset_locationActivity.this, serverError, Toast.LENGTH_LONG).show();
            }
        }
        @Override
        public void onFailure(@NonNull Call<StatusResponse> call, @NonNull Throwable t) {
            hideLoadingAnimation();
            Toast.makeText(Aset_locationActivity.this, "Network Error.", Toast.LENGTH_SHORT).show();
        }
    }

    private void startLoadingAnimation(String text) {
        if(loadingOverlay != null) {
            loadingText.setText(text);
            loadingOverlay.setVisibility(View.VISIBLE);
            Animation rotation = AnimationUtils.loadAnimation(this, R.anim.hourglass_rotation);
            loadingIcon.startAnimation(rotation);
        }
    }

    private void hideLoadingAnimation() {
        if(loadingOverlay != null) {
            loadingIcon.clearAnimation();
            loadingOverlay.setVisibility(View.GONE);
        }
    }

    private void clearInputFields(){
        stallIdEditText.setText("");
        stallNameEditText.setText("");
        stallNameEditText.setEnabled(true);
        latitudeEditText.setText("");
        longitudeEditText.setText("");
        manualCoordinatesLayout.setVisibility(View.GONE);
        stallIdEditText.requestFocus();
    }
}