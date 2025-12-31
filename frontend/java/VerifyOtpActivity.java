package com.simats.foodstall;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
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

public class VerifyOtpActivity extends AppCompatActivity {

    private TextInputEditText otpEditText;
    private Button verifyButton;
    private TextView emailTextView;
    private String userEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verify_otp);

        Toolbar toolbar = findViewById(R.id.toolbar);
        otpEditText = findViewById(R.id.otpEditText);
        verifyButton = findViewById(R.id.verifyButton);
        emailTextView = findViewById(R.id.emailTextView);

        toolbar.setNavigationOnClickListener(v -> onBackPressed());

        userEmail = getIntent().getStringExtra("USER_EMAIL");
        if (userEmail == null || userEmail.isEmpty()) {
            Toast.makeText(this, "Error: Email not found.", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        emailTextView.setText("We've sent a 6-digit code to:\n" + userEmail);

        verifyButton.setOnClickListener(v -> {
            String otp = otpEditText.getText().toString().trim();
            // Your existing logic for OTP length is preserved
            if (otp.length() == 6) {
                verifyOtp(userEmail, otp);
            } else {
                otpEditText.setError("Please enter the 6-digit OTP.");
            }
        });
    }

    private void verifyOtp(String email, String otp) {
        verifyButton.setEnabled(false);

        // 2. The API call now correctly expects and uses StatusResponse
        ApiClient.getClient().create(ApiService.class).verifyOtp(email, otp).enqueue(new Callback<StatusResponse>() {
            @Override
            public void onResponse(Call<StatusResponse> call, Response<StatusResponse> response) {
                verifyButton.setEnabled(true);
                // 3. The response body is now correctly handled as a StatusResponse
                if (response.isSuccessful() && response.body() != null && "success".equals(response.body().getStatus())) {
                    Toast.makeText(VerifyOtpActivity.this, response.body().getMessage(), Toast.LENGTH_SHORT).show();

                    Intent intent = new Intent(VerifyOtpActivity.this, CreateNewPasswordActivity.class);
                    intent.putExtra("USER_EMAIL", email);
                    intent.putExtra("USER_OTP", otp); // Pass the verified OTP for the final step
                    startActivity(intent);
                    finish();
                } else {
                    String errorMessage = (response.body() != null) ? response.body().getMessage() : "Verification failed. Invalid OTP.";
                    Toast.makeText(VerifyOtpActivity.this, errorMessage, Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<StatusResponse> call, Throwable t) {
                verifyButton.setEnabled(true);
                Toast.makeText(VerifyOtpActivity.this, "Network Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
