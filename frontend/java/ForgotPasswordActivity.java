package com.simats.foodstall;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
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

public class ForgotPasswordActivity extends AppCompatActivity {

    private TextInputEditText emailEditText;
    private Button sendOtpButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        Toolbar toolbar = findViewById(R.id.toolbar);
        emailEditText = findViewById(R.id.emailEditText);
        sendOtpButton = findViewById(R.id.sendOtpButton);

        toolbar.setNavigationOnClickListener(v -> onBackPressed());

        sendOtpButton.setOnClickListener(v -> {
            String email = emailEditText.getText().toString().trim();
            if (email.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                emailEditText.setError("Please enter a valid email address");
                return;
            }
            sendOtp(email);
        });
    }

    private void sendOtp(String email) {
        sendOtpButton.setEnabled(false);

        // 2. The API call now correctly expects and uses StatusResponse
        ApiClient.getClient().create(ApiService.class).sendOtp(email).enqueue(new Callback<StatusResponse>() {
            @Override
            public void onResponse(Call<StatusResponse> call, Response<StatusResponse> response) {
                sendOtpButton.setEnabled(true);
                if (response.isSuccessful() && response.body() != null) {
                    // 3. The response body is now correctly handled as a StatusResponse
                    Toast.makeText(ForgotPasswordActivity.this, response.body().getMessage(), Toast.LENGTH_LONG).show();
                    if ("success".equals(response.body().getStatus())) {
                        Intent intent = new Intent(ForgotPasswordActivity.this, VerifyOtpActivity.class);
                        intent.putExtra("USER_EMAIL", email);
                        startActivity(intent);
                    }
                } else {
                    Toast.makeText(ForgotPasswordActivity.this, "Server error.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<StatusResponse> call, Throwable t) {
                sendOtpButton.setEnabled(true);
                Toast.makeText(ForgotPasswordActivity.this, "Network error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}