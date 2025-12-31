package com.simats.foodstall;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.appbar.MaterialToolbar;
import com.simats.foodstall.model.StallEdit;
import com.simats.foodstall.model.StatusResponse;

import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AupdatestallActivity extends AppCompatActivity {

    private TextView stallNameTextView, ownerNameTextView, fssaiTextView, contactTextView, ownerIdTextView;
    private EditText latitudeEditText, longitudeEditText;
    private Button autoFetchLocationButton, deleteButton, updateButton;
    private FrameLayout loadingOverlay;
    private ImageView loadingIcon;

    private StallEdit currentStall;

    private final ActivityResultLauncher<Intent> autoFetchResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == AppCompatActivity.RESULT_OK && result.getData() != null) {
                    Intent data = result.getData();
                    double latitude = data.getDoubleExtra("latitude", 0.0);
                    double longitude = data.getDoubleExtra("longitude", 0.0);

                    latitudeEditText.setText(String.format(Locale.US, "%.6f", latitude));
                    longitudeEditText.setText(String.format(Locale.US, "%.6f", longitude));
                    Toast.makeText(this, "Location fetched successfully!", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "Could not fetch location.", Toast.LENGTH_SHORT).show();
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.aupdatestall);

        bindViews();
        setupToolbar();

        currentStall = getIntent().getParcelableExtra("STALL_DATA");
        if (currentStall == null) {
            Toast.makeText(this, "Error: Could not load stall data.", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        populateStallData();

        updateButton.setOnClickListener(v -> updateLocation());
        deleteButton.setOnClickListener(v -> showDeleteConfirmationDialog());
        autoFetchLocationButton.setOnClickListener(v -> {
            Intent intent = new Intent(this, Ufetch_locationActivity.class);
            autoFetchResultLauncher.launch(intent);
        });
    }

    private void setupToolbar() {
        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setNavigationOnClickListener(v -> finish());
    }

    private void bindViews() {
        stallNameTextView = findViewById(R.id.stallNameTextView);
        ownerNameTextView = findViewById(R.id.ownerNameTextViewDisplay);
        fssaiTextView = findViewById(R.id.fssaiTextView);
        contactTextView = findViewById(R.id.contactTextView);
        ownerIdTextView = findViewById(R.id.ownerIdTextView);
        latitudeEditText = findViewById(R.id.latitudeEditText);
        longitudeEditText = findViewById(R.id.longitudeEditText);
        autoFetchLocationButton = findViewById(R.id.autoFetchLocationButton);
        deleteButton = findViewById(R.id.deleteButton);
        updateButton = findViewById(R.id.updateButton);
        loadingOverlay = findViewById(R.id.loadingOverlay);
        loadingIcon = findViewById(R.id.loadingIcon);

        View stallNameLayout = findViewById(R.id.stallNameLayout);
        ((ImageView) stallNameLayout.findViewById(R.id.fieldIcon)).setImageResource(R.drawable.ic_store);
        ((TextView) stallNameLayout.findViewById(R.id.fieldLabel)).setText("Stall Name");

        View ownerNameLayout = findViewById(R.id.ownerNameLayout);
        ((ImageView) ownerNameLayout.findViewById(R.id.fieldIcon)).setImageResource(R.drawable.ic_person_outline);
        ((TextView) ownerNameLayout.findViewById(R.id.fieldLabel)).setText("Owner Name");

        View fssaiLayout = findViewById(R.id.fssaiLayout);
        ((ImageView) fssaiLayout.findViewById(R.id.fieldIcon)).setImageResource(R.drawable.ic_fssai1);
        ((TextView) fssaiLayout.findViewById(R.id.fieldLabel)).setText("FSSAI Number");

        View contactLayout = findViewById(R.id.contactLayout);
        ((ImageView) contactLayout.findViewById(R.id.fieldIcon)).setImageResource(R.drawable.ic_phone1);
        ((TextView) contactLayout.findViewById(R.id.fieldLabel)).setText("Contact Number");

        View stallIdLayout = findViewById(R.id.stallIdLayout);
        ((ImageView) stallIdLayout.findViewById(R.id.fieldIcon)).setImageResource(R.drawable.ic_badge);
        ((TextView) stallIdLayout.findViewById(R.id.fieldLabel)).setText("Stall ID (Not Editable)");
    }

    private void populateStallData() {
        stallNameTextView.setText(currentStall.getStallName());
        ownerNameTextView.setText(currentStall.getOwnerName());
        fssaiTextView.setText(currentStall.getFssaiNumber());
        contactTextView.setText(currentStall.getContactNumber());
        ownerIdTextView.setText(currentStall.getStallId());
        latitudeEditText.setText(currentStall.getLatitude());
        longitudeEditText.setText(currentStall.getLongitude());
    }

    private void updateLocation() {
        String latitude = latitudeEditText.getText().toString().trim();
        String longitude = longitudeEditText.getText().toString().trim();

        if (latitude.isEmpty() || longitude.isEmpty()) {
            Toast.makeText(this, "Latitude and Longitude cannot be empty.", Toast.LENGTH_SHORT).show();
            return;
        }

        showLoadingOverlay(); // Corrected call
        ApiClient.getClient().create(ApiService.class)
                .updateStallLocation(currentStall.getStallId(), latitude, longitude)
                .enqueue(new Callback<StatusResponse>() {
                    @Override
                    public void onResponse(Call<StatusResponse> call, Response<StatusResponse> response) {
                        hideLoadingOverlay();
                        if (response.isSuccessful() && response.body() != null && "success".equals(response.body().getStatus())) {
                            Toast.makeText(AupdatestallActivity.this, "Location updated successfully!", Toast.LENGTH_SHORT).show();
                            finish();
                        } else {
                            Toast.makeText(AupdatestallActivity.this, "Failed to update location.", Toast.LENGTH_SHORT).show();
                        }
                    }
                    @Override
                    public void onFailure(Call<StatusResponse> call, Throwable t) {
                        hideLoadingOverlay();
                        Toast.makeText(AupdatestallActivity.this, "Network Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void showDeleteConfirmationDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Delete Stall")
                .setMessage("Are you sure you want to permanently delete '" + currentStall.getStallName() + "'? This action cannot be undone.")
                .setPositiveButton("Delete", (dialog, which) -> deleteStall())
                .setNegativeButton("Cancel", null)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }

    private void deleteStall() {
        showLoadingOverlay(); // Corrected call
        ApiClient.getClient().create(ApiService.class).deleteStall(currentStall.getStallId())
                .enqueue(new Callback<StatusResponse>() {
                    @Override
                    public void onResponse(Call<StatusResponse> call, Response<StatusResponse> response) {
                        hideLoadingOverlay();
                        if (response.isSuccessful() && response.body() != null && "success".equals(response.body().getStatus())) {
                            Toast.makeText(AupdatestallActivity.this, "Stall deleted.", Toast.LENGTH_SHORT).show();
                            setResult(RESULT_OK);
                            finish();
                        } else {
                            Toast.makeText(AupdatestallActivity.this, "Failed to delete stall.", Toast.LENGTH_SHORT).show();
                        }
                    }
                    @Override
                    public void onFailure(Call<StatusResponse> call, Throwable t) {
                        hideLoadingOverlay();
                        Toast.makeText(AupdatestallActivity.this, "Network Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    // Simplified showLoadingOverlay method
    private void showLoadingOverlay() {
        loadingOverlay.setVisibility(View.VISIBLE);
        Animation rotation = AnimationUtils.loadAnimation(this, R.anim.hourglass_rotation);
        loadingIcon.startAnimation(rotation);
    }

    private void hideLoadingOverlay() {
        loadingIcon.clearAnimation();
        loadingOverlay.setVisibility(View.GONE);
    }
}