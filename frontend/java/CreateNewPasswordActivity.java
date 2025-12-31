package com.simats.foodstall;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import com.google.android.material.textfield.TextInputEditText;

// --- START OF FIX ---
// 1. Import the new, correct StatusResponse class from the model package
import com.simats.foodstall.model.StatusResponse;
// --- END OF FIX ---

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CreateNewPasswordActivity extends AppCompatActivity {

    private TextInputEditText newPasswordEditText, confirmPasswordEditText;
    private Button resetPasswordButton;
    private String userEmail;
    private String userOtp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_new_password);

        Toolbar toolbar = findViewById(R.id.toolbar);
        newPasswordEditText = findViewById(R.id.newPasswordEditText);
        confirmPasswordEditText = findViewById(R.id.confirmPasswordEditText);
        resetPasswordButton = findViewById(R.id.resetPasswordButton);

        toolbar.setNavigationOnClickListener(v -> onBackPressed());

        userEmail = getIntent().getStringExtra("USER_EMAIL");
        userOtp = getIntent().getStringExtra("USER_OTP");

        if (userEmail == null || userOtp == null) {
            Toast.makeText(this, "Error: Verification data missing.", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        resetPasswordButton.setOnClickListener(v -> {
            String newPass = newPasswordEditText.getText().toString();
            String confirmPass = confirmPasswordEditText.getText().toString();

            if (newPass.isEmpty() || newPass.length() < 6) {
                newPasswordEditText.setError("Password must be at least 6 characters");
                return;
            }
            if (!newPass.equals(confirmPass)) {
                confirmPasswordEditText.setError("Passwords do not match");
                return;
            }

            resetPassword(userEmail, userOtp, newPass);
        });
    }

    private void resetPassword(String email, String otp, String password) {
        // ... show progress bar
        resetPasswordButton.setEnabled(false);

        // 2. The API call now correctly expects and uses StatusResponse
        ApiClient.getClient().create(ApiService.class).resetPassword(email, otp, password).enqueue(new Callback<StatusResponse>() {
            @Override
            public void onResponse(Call<StatusResponse> call, Response<StatusResponse> response) {
                resetPasswordButton.setEnabled(true);
                // 3. The response body is now correctly handled as a StatusResponse
                if (response.isSuccessful() && response.body() != null && "success".equals(response.body().getStatus())) {
                    Toast.makeText(CreateNewPasswordActivity.this, "Password reset successfully! Please log in.", Toast.LENGTH_LONG).show();

                    Intent intent = new Intent(CreateNewPasswordActivity.this, LoginActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();
                } else {
                    String errorMessage = (response.body() != null) ? response.body().getMessage() : "Password reset failed.";
                    Toast.makeText(CreateNewPasswordActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<StatusResponse> call, Throwable t) {
                resetPasswordButton.setEnabled(true);
                Toast.makeText(CreateNewPasswordActivity.this, "Network Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}