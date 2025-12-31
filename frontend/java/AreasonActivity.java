package com.simats.foodstall;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
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
import com.simats.foodstall.model.StatusResponse;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AreasonActivity extends AppCompatActivity {

    private EditText reasonEditText;
    private Button sendMessageButton;
    private FrameLayout loadingOverlay;
    private ImageView loadingIcon;
    private TextView stallNameTextView, ownerNameTextView;

    private String email, status, stallName, ownerName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.areason);

        reasonEditText = findViewById(R.id.reasonEditText);
        sendMessageButton = findViewById(R.id.sendMessageButton);
        loadingOverlay = findViewById(R.id.loadingOverlay);
        loadingIcon = findViewById(R.id.loadingIcon);
        stallNameTextView = findViewById(R.id.stallNameTextView);
        ownerNameTextView = findViewById(R.id.ownerNameTextView);

        MaterialToolbar topBar = findViewById(R.id.topBar);
        topBar.setNavigationOnClickListener(v -> finish());

        Intent intent = getIntent();
        email = intent.getStringExtra("STALL_EMAIL");
        status = intent.getStringExtra("STATUS_UPDATE");
        stallName = intent.getStringExtra("STALL_NAME");
        ownerName = intent.getStringExtra("OWNER_NAME");

        if (email == null || status == null || stallName == null || ownerName == null) {
            Toast.makeText(this, "Error: Missing required data.", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Populate the stall and owner names
        stallNameTextView.setText(stallName);
        ownerNameTextView.setText("by " + ownerName);

        sendMessageButton.setOnClickListener(v -> {
            String reason = reasonEditText.getText().toString().trim();
            if (reason.isEmpty()) {
                Toast.makeText(AreasonActivity.this, "Please provide a reason for rejection.", Toast.LENGTH_SHORT).show();
                return;
            }
            updateStallStatus(reason);
        });
    }

    private void updateStallStatus(String reason) {
        startLoadingAnimation();
        sendMessageButton.setEnabled(false);

        ApiService apiService = ApiClient.getClient().create(ApiService.class);
        Call<StatusResponse> call = apiService.updateStallStatus(email, status, reason);

        call.enqueue(new Callback<StatusResponse>() {
            @Override
            public void onResponse(@NonNull Call<StatusResponse> call, @NonNull Response<StatusResponse> response) {
                hideLoadingAnimation();
                sendMessageButton.setEnabled(true);

                if (response.isSuccessful() && response.body() != null && "success".equals(response.body().getStatus())) {
                    Toast.makeText(AreasonActivity.this, "Rejection message sent.", Toast.LENGTH_LONG).show();

                    // Navigate to the rejection success screen
                    Intent intent = new Intent(AreasonActivity.this, ArejectionActivity.class);
                    intent.putExtra("STALL_NAME", stallName);
                    intent.putExtra("REJECTION_REASON", reason);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    finish();
                } else {
                    String errorMsg = (response.body() != null) ? response.body().getMessage() : "Server error.";
                    Toast.makeText(AreasonActivity.this, "Failed to update: " + errorMsg, Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<StatusResponse> call, @NonNull Throwable t) {
                hideLoadingAnimation();
                sendMessageButton.setEnabled(true);
                Log.e("API_FAILURE", "Failed to update stall status", t);
                Toast.makeText(AreasonActivity.this, "Network Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void startLoadingAnimation() {
        if (loadingOverlay != null) {
            loadingOverlay.setVisibility(View.VISIBLE);
            Animation rotation = AnimationUtils.loadAnimation(this, R.anim.hourglass_rotation);
            loadingIcon.startAnimation(rotation);
        }
    }

    private void hideLoadingAnimation() {
        if (loadingOverlay != null && loadingOverlay.getVisibility() == View.VISIBLE) {
            loadingIcon.clearAnimation();
            loadingOverlay.setVisibility(View.GONE);
        }
    }
}