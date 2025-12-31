package com.simats.foodstall;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.simats.foodstall.model.StatusResponse;

import java.util.regex.Pattern;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ConfirmPasswordActivity extends AppCompatActivity {

    private TextInputEditText tempPasswordEditText, newPasswordEditText, confirmNewPasswordEditText;
    private TextInputLayout newPasswordLayout, confirmNewPasswordLayout;
    private Button savePasswordButton, skipButton;
    private FrameLayout loadingOverlay;
    private ImageView loadingIcon;

    private String userId; // Can be student_id for user, or email for owner
    private String userRole; // "user" or "owner"
    private String userName;

    private static final String TAG = "ConfirmPasswordActivity";

    private static final Pattern PASSWORD_PATTERN =
            Pattern.compile("^" +
                    "(?=.*[0-9])" +
                    "(?=.*[a-z])" +
                    "(?=.*[A-Z])" +
                    "(?=.*[@#$%^&+=!])" +
                    "(?=\\S+$)" +
                    ".{6,8}" +
                    "$");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirm_password);

        tempPasswordEditText = findViewById(R.id.tempPasswordEditText);
        newPasswordEditText = findViewById(R.id.newPasswordEditText);
        confirmNewPasswordEditText = findViewById(R.id.confirmNewPasswordEditText);
        newPasswordLayout = findViewById(R.id.newPasswordLayout);
        confirmNewPasswordLayout = findViewById(R.id.confirmNewPasswordLayout);
        savePasswordButton = findViewById(R.id.savePasswordButton);
        skipButton = findViewById(R.id.skipButton);
        loadingOverlay = findViewById(R.id.loadingOverlay);
        loadingIcon = findViewById(R.id.loadingIcon);

        Intent intent = getIntent();
        userId = intent.getStringExtra("USER_ID");
        userRole = intent.getStringExtra("USER_ROLE");
        userName = intent.getStringExtra("USER_NAME");
        String tempPassword = intent.getStringExtra("TEMP_PASSWORD");

        if (userId == null || userRole == null || tempPassword == null) {
            Log.e(TAG, "Missing required data from Intent.");
            Toast.makeText(this, "Error: Missing registration data.", Toast.LENGTH_LONG).show();
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return;
        }

        tempPasswordEditText.setText(tempPassword);

        savePasswordButton.setOnClickListener(v -> {
            if (validateNewPassword()) {
                setNewPassword(newPasswordEditText.getText().toString());
            }
        });

        skipButton.setOnClickListener(v -> {
            Log.d(TAG, "User skipped setting new password. Proceeding with temporary password.");
            // [UPDATE 1] Pass the temp password and flag to the next step
            proceedToHomeScreen(tempPasswordEditText.getText().toString(), true);
        });
    }

    private boolean validateNewPassword() {
        String newPass = newPasswordEditText.getText().toString();
        String confirmPass = confirmNewPasswordEditText.getText().toString();
        boolean isValid = true;

        newPasswordLayout.setError(null);
        confirmNewPasswordLayout.setError(null);

        if (newPass.isEmpty()) {
            newPasswordLayout.setError("New password cannot be empty");
            isValid = false;
        } else if (!PASSWORD_PATTERN.matcher(newPass).matches()) {
            newPasswordLayout.setError("6-8 chars, must include A-Z, a-z, 0-9, and a symbol (@#$%^&+=!)");
            isValid = false;
        }

        if (confirmPass.isEmpty()) {
            confirmNewPasswordLayout.setError("Confirmation password cannot be empty");
            isValid = false;
        } else if (isValid && !newPass.equals(confirmPass)) {
            confirmNewPasswordLayout.setError("Passwords do not match");
            isValid = false;
        }

        return isValid;
    }

    private void setNewPassword(String newPassword) {
        startLoadingAnimation();

        ApiService apiService = ApiClient.getClient().create(ApiService.class);
        Call<StatusResponse> call = apiService.setPassword(userRole, userId, newPassword);

        call.enqueue(new Callback<StatusResponse>() {
            @Override
            public void onResponse(@NonNull Call<StatusResponse> call, @NonNull Response<StatusResponse> response) {
                stopLoadingAnimation();

                if (response.isSuccessful() && response.body() != null) {
                    Toast.makeText(ConfirmPasswordActivity.this, response.body().getMessage(), Toast.LENGTH_SHORT).show();
                    if ("success".equals(response.body().getStatus())) {
                        // [UPDATE 2] Pass the new password and flag to the next step
                        proceedToHomeScreen(newPassword, true);
                    }
                } else {
                    Log.e(TAG, "Set Password Failed - Code: " + response.code() + ", Message: " + response.message());
                    Toast.makeText(ConfirmPasswordActivity.this, "Failed to set password. Server error.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<StatusResponse> call, @NonNull Throwable t) {
                stopLoadingAnimation();
                Log.e(TAG, "Network Error (Set Password): " + t.getMessage(), t);
                Toast.makeText(ConfirmPasswordActivity.this, "Network Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    // [UPDATE 3] Changed method signature
    private void proceedToHomeScreen(String passwordToForward, boolean isFromGoogle) {
        Intent homeIntent;
        SharedPreferences sharedPreferences;
        SharedPreferences.Editor editor;

        if ("user".equals(userRole)) {
            sharedPreferences = getSharedPreferences("StallSpotPrefs", MODE_PRIVATE);
            editor = sharedPreferences.edit();
            editor.putString("STUDENT_ID", userId);
            editor.putString("USER_NAME", userName);
            editor.putString("LOGGED_IN_ROLE", "student");
            editor.apply();
            homeIntent = new Intent(this, UhomeActivity.class);

        } else if ("owner".equals(userRole)) {
            Toast.makeText(this, "Password confirmed. Please enter your stall details.", Toast.LENGTH_LONG).show();
            homeIntent = new Intent(this, OstalldetailsActivity.class);

            // This was your existing fix
            homeIntent.putExtra("USER_NAME", userName);
            homeIntent.putExtra("OWNER_EMAIL", userId);

            // [UPDATE 4] Add the new extras to pass to OstalldetailsActivity
            homeIntent.putExtra("OWNER_PASSWORD", passwordToForward);
            homeIntent.putExtra("IS_GOOGLE_SIGNUP", isFromGoogle);

        } else {
            Log.e(TAG, "Unknown role: " + userRole);
            Toast.makeText(this, "Error determining role.", Toast.LENGTH_SHORT).show();
            homeIntent = new Intent(this, LoginActivity.class);
        }

        homeIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(homeIntent);
        finish();
    }

    private void startLoadingAnimation() {
        loadingOverlay.setVisibility(View.VISIBLE);
        Animation rotation = AnimationUtils.loadAnimation(this, R.anim.hourglass_rotation);
        loadingIcon.startAnimation(rotation);
        savePasswordButton.setEnabled(false);
        skipButton.setEnabled(false);
    }

    private void stopLoadingAnimation() {
        loadingIcon.clearAnimation();
        loadingOverlay.setVisibility(View.GONE);
        savePasswordButton.setEnabled(true);
        skipButton.setEnabled(true);
    }
}