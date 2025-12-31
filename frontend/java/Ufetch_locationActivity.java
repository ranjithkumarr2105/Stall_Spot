package com.simats.foodstall;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log; // Make sure Log is imported
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;

public class Ufetch_locationActivity extends AppCompatActivity {

    private static final int LOCATION_PERMISSION_REQUEST = 1001;
    private static final String TAG = "UfetchLocationActivity"; // Ensure TAG is defined
    private FusedLocationProviderClient fusedLocationClient;

    private String stallId = null;
    private boolean navigateToMenu = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ufetch_location);
        Log.d(TAG, "onCreate: Activity started."); // Log start

        ImageView locationPulseIcon = findViewById(R.id.locationPulseIcon);
        // [Defensive Coding] Add null check for animation view
        if (locationPulseIcon != null) {
            Animation pulse = AnimationUtils.loadAnimation(this, R.anim.anim_location_pulse);
            locationPulseIcon.startAnimation(pulse);
        } else {
            Log.e(TAG, "locationPulseIcon view not found in layout!");
        }


        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        Intent intent = getIntent();
        if (intent != null) {
            stallId = intent.getStringExtra("STALL_ID");
            navigateToMenu = intent.getBooleanExtra("NAVIGATE_TO_MENU", false);
            // [MORE LOGGING] Log exactly what was received
            Log.d(TAG, "onCreate: Received Intent extras - stallId=" + stallId + ", navigateToMenu=" + navigateToMenu);
        } else {
            Log.w(TAG, "onCreate: Intent is null.");
        }

        checkPermissionAndFetchLocation();
    }

    private void checkPermissionAndFetchLocation() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Log.d(TAG, "checkPermission: Permission NOT granted. Requesting...");
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_REQUEST);
        } else {
            Log.d(TAG, "checkPermission: Permission already granted. Calling fetchLocation...");
            fetchLocation();
        }
    }

    private void fetchLocation() {
        Log.d(TAG, "fetchLocation: Attempting to get last location..."); // Log entry
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(this, "Permission required to fetch location.", Toast.LENGTH_SHORT).show();
            Log.w(TAG, "fetchLocation: Permission check failed unexpectedly.");
            finishWithError();
            return;
        }

        fusedLocationClient.getLastLocation().addOnSuccessListener(this, location -> {
            // [MORE LOGGING] Log location result immediately
            Log.d(TAG, "fetchLocation: getLastLocation success. Location object: " + (location != null ? "obtained" : "NULL"));

            // Use Handler for delay (as before)
            new Handler(Looper.getMainLooper()).postDelayed(() -> {
                if (location != null) {
                    // [MORE LOGGING] Log before the conditional check
                    Log.d(TAG, "fetchLocation: Location obtained. Checking flow: navigateToMenu=" + navigateToMenu + ", stallId=" + stallId);

                    if (navigateToMenu && stallId != null && !stallId.isEmpty()) {
                        // USER FLOW
                        Log.i(TAG, "fetchLocation: USER FLOW DETECTED. Preparing intent for UviewmenuActivity."); // Use INFO level for clarity
                        Intent menuIntent = new Intent(Ufetch_locationActivity.this, UviewmenuActivity.class);
                        menuIntent.putExtra("STALL_ID", stallId);
                        menuIntent.putExtra("USER_LATITUDE", location.getLatitude());
                        menuIntent.putExtra("USER_LONGITUDE", location.getLongitude());

                        try {
                            Log.i(TAG, "fetchLocation: Starting UviewmenuActivity...");
                            startActivity(menuIntent);
                            Log.i(TAG, "fetchLocation: Finishing Ufetch_locationActivity (USER FLOW).");
                            finish(); // Finish AFTER starting the next activity
                        } catch (Exception e) {
                            Log.e(TAG, "fetchLocation: Error starting UviewmenuActivity", e);
                            Toast.makeText(this, "Error navigating to menu.", Toast.LENGTH_SHORT).show();
                            finishWithError(); // Finish if starting next activity failed
                        }

                    } else {
                        // ADMIN FLOW or Error in User Flow data
                        if (!navigateToMenu) {
                            Log.w(TAG, "fetchLocation: ADMIN FLOW DETECTED (navigateToMenu is false). Returning result.");
                        } else {
                            Log.w(TAG, "fetchLocation: User flow intended, but stallId is null or empty. Returning result.");
                        }
                        Intent resultIntent = new Intent();
                        resultIntent.putExtra("latitude", location.getLatitude());
                        resultIntent.putExtra("longitude", location.getLongitude());
                        setResult(RESULT_OK, resultIntent);
                        Log.i(TAG, "fetchLocation: Finishing Ufetch_locationActivity (ADMIN FLOW / ERROR).");
                        finish();
                    }

                } else {
                    Log.e(TAG, "fetchLocation: Location object was NULL after delay.");
                    Toast.makeText(this, "Could not get current location. Please ensure GPS is enabled and try again.", Toast.LENGTH_LONG).show();
                    finishWithError();
                }
            }, 500); // Reduced delay slightly, 1500ms might be too long if location is quick
        }).addOnFailureListener(this, e -> {
            Log.e(TAG, "fetchLocation: getLastLocation FAILED", e);
            Toast.makeText(this, "Error getting location: " + e.getMessage(), Toast.LENGTH_LONG).show();
            finishWithError();
        });
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERMISSION_REQUEST) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.d(TAG, "onRequestPermissionsResult: Permission GRANTED. Calling fetchLocation...");
                fetchLocation();
            } else {
                Log.w(TAG, "onRequestPermissionsResult: Permission DENIED.");
                Toast.makeText(this, "Location permission was denied. Cannot proceed.", Toast.LENGTH_SHORT).show();
                finishWithError();
            }
        }
    }

    private void finishWithError() {
        Log.d(TAG, "finishWithError: Setting result CANCELED and finishing.");
        setResult(RESULT_CANCELED);
        finish();
    }
}