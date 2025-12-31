package com.simats.foodstall;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import org.osmdroid.util.GeoPoint;
import java.util.Locale;

public class Ulocation_preferenceActivity extends AppCompatActivity {

    private Button savePreferencesButton;
    private GeoPoint selectedLocation;

    private FrameLayout loadingOverlay;
    private ImageView loadingIcon;
    private TextView loadingText;

    // --- UPDATED PART START ---
    // Views for the new result section
    private LinearLayout locationResultLayout;
    private TextView selectedAddressTextView;
    private TextView latitudeTextView;
    private TextView longitudeTextView;
    // --- UPDATED PART END ---

    private final ActivityResultLauncher<Intent> autoDetectResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == AppCompatActivity.RESULT_OK && result.getData() != null) {
                    handleLocationResult(result.getData());
                }
            });

    private final ActivityResultLauncher<Intent> mapPickerResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == AppCompatActivity.RESULT_OK && result.getData() != null) {
                    handleLocationResult(result.getData());
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ulocation_preference);

        // Find views
        savePreferencesButton = findViewById(R.id.savePreferencesButton);
        loadingOverlay = findViewById(R.id.loadingOverlay);
        loadingIcon = findViewById(R.id.loadingIcon);
        loadingText = findViewById(R.id.loadingText);

        // --- UPDATED PART START ---
        // Find new result views
        locationResultLayout = findViewById(R.id.locationResultLayout);
        selectedAddressTextView = findViewById(R.id.selectedAddressTextView);
        latitudeTextView = findViewById(R.id.latitudeTextView);
        longitudeTextView = findViewById(R.id.longitudeTextView);
        // --- UPDATED PART END ---

        setupClickListeners();
    }

    private void setupClickListeners() {
        findViewById(R.id.backButton).setOnClickListener(v -> finish());
        findViewById(R.id.skipForNowTextView).setOnClickListener(v -> {
            Toast.makeText(this, "You can set this later in Settings", Toast.LENGTH_SHORT).show();
            finish();
        });

        findViewById(R.id.autoDetectCard).setOnClickListener(v -> {
            Intent intent = new Intent(this, Ufetch_locationActivity.class);
            autoDetectResultLauncher.launch(intent);
        });

        findViewById(R.id.mapPickerCard).setOnClickListener(v -> {
            Intent mapIntent = new Intent(this, Umap_pickerActivity.class);
            mapPickerResultLauncher.launch(mapIntent);
        });

        savePreferencesButton.setOnClickListener(v -> {
            if (selectedLocation != null) {
                showLoadingOverlay("Saving...", () -> {
                    LocationManager.saveUserLocation(this, selectedLocation);
                    Toast.makeText(this, "Location preference saved!", Toast.LENGTH_SHORT).show();
                    finish();
                });
            } else {
                Toast.makeText(this, "Please select a location first.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void handleLocationResult(Intent data) {
        double latitude = data.getDoubleExtra("latitude", 0.0);
        double longitude = data.getDoubleExtra("longitude", 0.0);
        String address = data.getStringExtra("address");

        if (latitude != 0.0 && longitude != 0.0) {
            selectedLocation = new GeoPoint(latitude, longitude);
            // --- UPDATED PART START ---
            // Show and populate the new result section
            displayLocationDetails(address, latitude, longitude);
            // --- UPDATED PART END ---
            savePreferencesButton.setEnabled(true);
        }
    }

    // --- UPDATED PART START ---
    // New method to populate the dedicated result views
    private void displayLocationDetails(String address, double latitude, double longitude) {
        // Make the entire result section visible
        locationResultLayout.setVisibility(View.VISIBLE);

        if (address != null && !address.isEmpty()) {
            selectedAddressTextView.setText(address);
        } else {
            selectedAddressTextView.setText("Address not found");
        }

        latitudeTextView.setText(String.format(Locale.getDefault(), "%.4f", latitude));
        longitudeTextView.setText(String.format(Locale.getDefault(), "%.4f", longitude));
    }
    // --- UPDATED PART END ---

    private void showLoadingOverlay(String message, Runnable onComplete) {
        loadingText.setText(message);
        loadingOverlay.setVisibility(View.VISIBLE);
        Animation rotation = AnimationUtils.loadAnimation(this, R.anim.hourglass_rotation);
        loadingIcon.startAnimation(rotation);

        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            loadingIcon.clearAnimation();
            loadingOverlay.setVisibility(View.GONE);
            if (onComplete != null) {
                onComplete.run();
            }
        }, 1500);
    }
}