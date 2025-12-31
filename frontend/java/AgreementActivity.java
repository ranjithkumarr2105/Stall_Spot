package com.simats.foodstall;

import android.content.Intent;
import android.content.SharedPreferences; // Import SharedPreferences
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;

import com.simats.foodstall.model.StatusResponse;

import org.json.JSONObject;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AgreementActivity extends AppCompatActivity {

    private TextView textViewAgreementContent;
    private CheckBox checkBoxAgreement;
    private Button buttonAccept;
    private String ownerPhoneNumber;
    private static final String TAG = "AgreementActivity";

    // [NEW] Added variables for stall details
    private String stallId;
    private String stallName;

    // [NEW] Variables for loading animation
    private FrameLayout loadingOverlay;
    private ImageView loadingIcon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_agreement); // Corrected layout name

        // Get data from Intent
        ownerPhoneNumber = getIntent().getStringExtra("OWNER_PHONE");
        // [NEW] Get stall ID and name from Intent
        stallId = getIntent().getStringExtra("STALL_ID");
        stallName = getIntent().getStringExtra("STALL_NAME");

        if (ownerPhoneNumber == null || ownerPhoneNumber.isEmpty() || stallId == null || stallId.isEmpty()) {
            Toast.makeText(this, "Error: Owner identifier or Stall ID not found. Please log in again.", Toast.LENGTH_LONG).show();
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return;
        }
        // [NEW] Log the received stall details
        Log.d(TAG, "Received Stall ID: " + stallId + ", Stall Name: " + stallName);


        textViewAgreementContent = findViewById(R.id.textViewAgreementContent);
        checkBoxAgreement = findViewById(R.id.checkBoxAgreement);
        buttonAccept = findViewById(R.id.buttonAccept);
        // [NEW] Link loading overlay views
        loadingOverlay = findViewById(R.id.loadingOverlay);
        loadingIcon = findViewById(R.id.loadingIcon);

        fetchAgreementContent();

        checkBoxAgreement.setOnCheckedChangeListener((buttonView, isChecked) -> {
            // Only enable button if not loading
            buttonAccept.setEnabled(isChecked && (loadingOverlay == null || loadingOverlay.getVisibility() == View.GONE));
        });

        buttonAccept.setOnClickListener(v -> {
            if (checkBoxAgreement.isChecked()) {
                acceptAgreement();
            }
        });

        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                Toast.makeText(AgreementActivity.this, "You must accept the terms to continue.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // [NEW] Added standard loading animation methods
    private void startLoadingAnimation() {
        if (loadingOverlay != null) {
            loadingOverlay.setVisibility(View.VISIBLE);
            Animation rotation = AnimationUtils.loadAnimation(this, R.anim.hourglass_rotation);
            loadingIcon.startAnimation(rotation);
        }
        buttonAccept.setEnabled(false); // Disable button while loading
        checkBoxAgreement.setEnabled(false); // Disable checkbox while loading
    }

    // [NEW] Added standard loading animation methods
    private void stopLoadingAnimation() {
        if (loadingOverlay != null && loadingOverlay.getVisibility() == View.VISIBLE) {
            loadingIcon.clearAnimation();
            loadingOverlay.setVisibility(View.GONE);
        }
        // Re-enable based on checkbox state
        checkBoxAgreement.setEnabled(true);
        buttonAccept.setEnabled(checkBoxAgreement.isChecked());
    }


    private void fetchAgreementContent() {
        // [NEW] Use new animation method
        startLoadingAnimation();
        ApiClient.getClient().create(ApiService.class).getOwnerAgreement().enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                // [NEW] Use new animation method
                stopLoadingAnimation();
                if (response.isSuccessful() && response.body() != null) {
                    try {
                        String responseString = response.body().string();
                        JSONObject jsonObject = new JSONObject(responseString);
                        if ("success".equals(jsonObject.getString("status"))) {
                            String content = jsonObject.getString("content");
                            textViewAgreementContent.setText(Html.fromHtml(content, Html.FROM_HTML_MODE_LEGACY));
                        } else {
                            textViewAgreementContent.setText("Failed to load agreement: " + jsonObject.getString("message"));
                        }
                    } catch (Exception e) {
                        Log.e(TAG, "Error parsing agreement response", e);
                        textViewAgreementContent.setText("Error displaying agreement.");
                    }
                } else {
                    textViewAgreementContent.setText("Failed to load agreement. Please check your connection.");
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                // [NEW] Use new animation method
                stopLoadingAnimation();
                Log.e(TAG, "Network error fetching agreement", t);
                textViewAgreementContent.setText("Network Error. Please try again later.");
            }
        });
    }

    private void acceptAgreement() {
        // [NEW] Use new animation method
        startLoadingAnimation();
        ApiClient.getClient().create(ApiService.class).acceptOwnerAgreement(ownerPhoneNumber).enqueue(new Callback<StatusResponse>() {
            @Override
            public void onResponse(Call<StatusResponse> call, Response<StatusResponse> response) {
                // [NEW] Use new animation method
                stopLoadingAnimation();
                if (response.isSuccessful() && response.body() != null && "success".equals(response.body().getStatus())) {
                    Toast.makeText(AgreementActivity.this, "Agreement accepted! Welcome.", Toast.LENGTH_SHORT).show();

                    // [FIX] Save the correct stall ID and name to SharedPreferences
                    SharedPreferences sharedPreferences = getSharedPreferences("owner_prefs", MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString("stall_id", stallId); // Use the variable from Intent
                    editor.putString("stall_name", stallName); // Use the variable from Intent
                    editor.putBoolean("isLoggedIn", true);
                    editor.apply();
                    Log.d(TAG, "Saved to SharedPreferences - stall_id: " + stallId + ", stall_name: " + stallName);

                    Intent intent = new Intent(AgreementActivity.this, OhomeActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();
                } else {
                    Toast.makeText(AgreementActivity.this, "Failed to accept agreement. Please try again.", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<StatusResponse> call, Throwable t) {
                // [NEW] Use new animation method
                stopLoadingAnimation();
                Log.e(TAG, "Network error accepting agreement", t);
                Toast.makeText(AgreementActivity.this, "Network Error: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }
}