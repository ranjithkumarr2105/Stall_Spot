package com.simats.foodstall;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log; // Import Log
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull; // Import NonNull
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.simats.foodstall.model.StatusResponse; // Import StatusResponse

import java.util.regex.Pattern; // Import Pattern

import retrofit2.Call; // Import Call
import retrofit2.Callback; // Import Callback
import retrofit2.Response; // Import Response

public class UchangepasswordActivity extends AppCompatActivity {

    private static final String TAG = "ChangePasswordActivity"; // Added TAG

    // UI Elements for Step 1
    private TextInputLayout currentPasswordLayout;
    private TextInputEditText currentPasswordEditText;
    private Button verifyButton;

    // UI Elements for Step 2
    private TextInputLayout newPasswordLayout;
    private TextInputEditText newPasswordEditText;
    private TextInputLayout confirmPasswordLayout;
    private TextInputEditText confirmPasswordEditText;
    private Button updatePasswordButton;

    // Loading Overlay
    private FrameLayout loadingOverlay;
    private ImageView loadingIcon;
    private TextView loadingText;

    // Data passed from previous activity
    private String userRole; // "USER", "OWNER", "ADMIN"
    private String userId;   // The specific ID (student_id, stall_id/phone, admin_id)

    // Password Validation Regex
    private static final Pattern PASSWORD_PATTERN =
            Pattern.compile("^" +
                    "(?=.*[0-9])" +         // at least 1 digit
                    "(?=.*[a-z])" +         // at least 1 lower case letter
                    "(?=.*[A-Z])" +         // at least 1 upper case letter
                    "(?=.*[@#$%^&+=!])" +    // at least 1 special character in this set
                    "(?=\\S+$)" +           // no white spaces
                    ".{6,8}" +              // 6 to 8 characters long
                    "$");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.uchangepassword);

        // Get Role and ID from Intent (MUST BE PASSED FROM Settings Activities)
        Intent intent = getIntent();
        userRole = intent.getStringExtra("USER_ROLE");
        userId = intent.getStringExtra("USER_IDENTIFIER"); // Use a consistent key

        if (userRole == null || userId == null || userRole.isEmpty() || userId.isEmpty()) {
            Toast.makeText(this, "Error: User role or ID not provided.", Toast.LENGTH_LONG).show();
            Log.e(TAG, "Missing USER_ROLE or USER_IDENTIFIER in Intent");
            finish(); // Close activity if essential data is missing
            return;
        }
        Log.d(TAG, "Received Role: " + userRole + ", ID: " + userId);


        // Find views
        currentPasswordLayout = findViewById(R.id.currentPasswordLayout);
        currentPasswordEditText = findViewById(R.id.currentPasswordEditText);
        verifyButton = findViewById(R.id.verifyButton);

        newPasswordLayout = findViewById(R.id.newPasswordLayout);
        newPasswordEditText = findViewById(R.id.newPasswordEditText);
        confirmPasswordLayout = findViewById(R.id.confirmPasswordLayout);
        confirmPasswordEditText = findViewById(R.id.confirmPasswordEditText);
        updatePasswordButton = findViewById(R.id.updatePasswordButton);

        loadingOverlay = findViewById(R.id.loadingOverlay);
        loadingIcon = findViewById(R.id.loadingIcon);
        loadingText = findViewById(R.id.loadingText);

        findViewById(R.id.backButton).setOnClickListener(v -> onBackPressed());

        // Set initial UI state
        showStep1UI();

        // Set button listeners
        verifyButton.setOnClickListener(v -> verifyCurrentPassword());
        updatePasswordButton.setOnClickListener(v -> validateAndSetNewPassword());
    }

    // Show only Current Password field and Verify button
    private void showStep1UI() {
        currentPasswordLayout.setVisibility(View.VISIBLE);
        verifyButton.setVisibility(View.VISIBLE);

        newPasswordLayout.setVisibility(View.GONE);
        confirmPasswordLayout.setVisibility(View.GONE);
        updatePasswordButton.setVisibility(View.GONE);
    }

    // Show New Password, Confirm Password fields and Update button
    private void showStep2UI() {
        currentPasswordLayout.setVisibility(View.GONE);
        verifyButton.setVisibility(View.GONE);

        newPasswordLayout.setVisibility(View.VISIBLE);
        confirmPasswordLayout.setVisibility(View.VISIBLE);
        updatePasswordButton.setVisibility(View.VISIBLE);
    }

    // --- Step 1: Verify Current Password ---
    private void verifyCurrentPassword() {
        String currentPassword = currentPasswordEditText.getText().toString().trim();
        currentPasswordLayout.setError(null); // Clear previous error

        if (TextUtils.isEmpty(currentPassword)) {
            currentPasswordLayout.setError("Current password cannot be empty");
            return;
        }

        startLoadingAnimation("Verifying...");

        ApiService apiService = ApiClient.getClient().create(ApiService.class);
        Call<StatusResponse> call = apiService.verifyCurrentPassword(userRole, userId, currentPassword);

        call.enqueue(new Callback<StatusResponse>() {
            @Override
            public void onResponse(@NonNull Call<StatusResponse> call, @NonNull Response<StatusResponse> response) {
                stopLoadingAnimation();
                if (response.isSuccessful() && response.body() != null) {
                    if ("success".equals(response.body().getStatus())) {
                        Toast.makeText(UchangepasswordActivity.this, "Current password verified.", Toast.LENGTH_SHORT).show();
                        showStep2UI(); // Move to step 2
                    } else {
                        // Show error message from server (e.g., "Incorrect current password")
                        currentPasswordLayout.setError(response.body().getMessage());
                        // Toast.makeText(UchangepasswordActivity.this, response.body().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Log.e(TAG, "Verify API error: Code=" + response.code() + ", Message=" + response.message());
                    Toast.makeText(UchangepasswordActivity.this, "Verification failed. Server error.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<StatusResponse> call, @NonNull Throwable t) {
                stopLoadingAnimation();
                Log.e(TAG, "Verify API network failure", t);
                Toast.makeText(UchangepasswordActivity.this, "Network error during verification.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // --- Step 2: Validate and Set New Password ---
    private void validateAndSetNewPassword() {
        String newPassword = newPasswordEditText.getText().toString().trim();
        String confirmPassword = confirmPasswordEditText.getText().toString().trim();

        // Clear previous errors
        newPasswordLayout.setError(null);
        confirmPasswordLayout.setError(null);

        boolean isValid = true;

        if (TextUtils.isEmpty(newPassword)) {
            newPasswordLayout.setError("New password cannot be empty");
            isValid = false;
        } else if (!PASSWORD_PATTERN.matcher(newPassword).matches()) {
            // Give specific feedback based on regex
            newPasswordLayout.setError("Password must be 6-8 chars with uppercase, lowercase, digit, and symbol (@#$%^&+=!)");
            isValid = false;
        }

        if (TextUtils.isEmpty(confirmPassword)) {
            confirmPasswordLayout.setError("Confirmation password cannot be empty");
            isValid = false;
        } else if (isValid && !newPassword.equals(confirmPassword)) {
            confirmPasswordLayout.setError("Passwords do not match");
            isValid = false;
        }

        if (!isValid) {
            return; // Stop if validation fails
        }

        // If all client-side validation passes, call API
        setNewPassword(newPassword);
    }

    private void setNewPassword(String newPassword) {
        startLoadingAnimation("Updating Password...");

        ApiService apiService = ApiClient.getClient().create(ApiService.class);
        Call<StatusResponse> call = apiService.updatePassword(userRole, userId, newPassword);

        call.enqueue(new Callback<StatusResponse>() {
            @Override
            public void onResponse(@NonNull Call<StatusResponse> call, @NonNull Response<StatusResponse> response) {
                stopLoadingAnimation();
                if (response.isSuccessful() && response.body() != null) {
                    if ("success".equals(response.body().getStatus())) {
                        Toast.makeText(UchangepasswordActivity.this, "Password updated successfully!", Toast.LENGTH_SHORT).show();
                        finish(); // Go back to the settings screen
                    } else {
                        // Show error message from server
                        Toast.makeText(UchangepasswordActivity.this, "Update failed: " + response.body().getMessage(), Toast.LENGTH_LONG).show();
                    }
                } else {
                    Log.e(TAG, "Update API error: Code=" + response.code() + ", Message=" + response.message());
                    Toast.makeText(UchangepasswordActivity.this, "Update failed. Server error.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<StatusResponse> call, @NonNull Throwable t) {
                stopLoadingAnimation();
                Log.e(TAG, "Update API network failure", t);
                Toast.makeText(UchangepasswordActivity.this, "Network error during update.", Toast.LENGTH_SHORT).show();
            }
        });
    }


    // --- Loading Animation Methods ---
    private void startLoadingAnimation(String message) {
        loadingText.setText(message);
        loadingOverlay.setVisibility(View.VISIBLE);
        Animation rotation = AnimationUtils.loadAnimation(this, R.anim.hourglass_rotation);
        loadingIcon.startAnimation(rotation);
        // Disable buttons during loading
        verifyButton.setEnabled(false);
        updatePasswordButton.setEnabled(false);
    }

    private void stopLoadingAnimation() {
        if (loadingOverlay.getVisibility() == View.VISIBLE) {
            loadingIcon.clearAnimation();
            loadingOverlay.setVisibility(View.GONE);
        }
        // Re-enable buttons
        verifyButton.setEnabled(true);
        updatePasswordButton.setEnabled(true);
    }
}