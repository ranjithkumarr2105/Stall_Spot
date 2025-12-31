package com.simats.foodstall;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONObject;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OpolicyActivity extends AppCompatActivity {

    private static final String TAG = "OpolicyActivity";
    // Define the key used to fetch this specific policy from the database
    private static final String POLICY_KEY = "owner_terms";

    // UI Elements
    private TextView policyTextView;
    private FrameLayout loadingOverlay;
    private ImageView loadingIcon;
    private TextView loadingText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Ensure your layout file includes a TextView with id "policyTextView"
        // and the FrameLayout loading overlay structure
        setContentView(R.layout.opolicy);

        // Link Views
        policyTextView = findViewById(R.id.policyTextView);
        loadingOverlay = findViewById(R.id.loadingOverlay);
        loadingIcon = findViewById(R.id.loadingIcon);
        loadingText = findViewById(R.id.loadingText);

        // Set up the back button
        findViewById(R.id.backButton).setOnClickListener(v -> onBackPressed());

        // Fetch content when activity starts
        fetchPolicyContent();
    }

    private void fetchPolicyContent() {
        startLoadingAnimation("Loading Terms..."); // Show loading indicator

        ApiClient.getClient().create(ApiService.class).getPolicyContent(POLICY_KEY).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                stopLoadingAnimation(); // Hide loading indicator
                if (response.isSuccessful() && response.body() != null) {
                    try {
                        String responseString = response.body().string();
                        JSONObject jsonObject = new JSONObject(responseString);

                        if ("success".equals(jsonObject.optString("status"))) {
                            String content = jsonObject.optString("content", "Terms content not available.");
                            policyTextView.setText(Html.fromHtml(content, Html.FROM_HTML_MODE_LEGACY));
                        } else {
                            String message = jsonObject.optString("message", "Failed to load terms.");
                            Log.e(TAG, "API Error fetching terms: " + message);
                            policyTextView.setText(message);
                            Toast.makeText(OpolicyActivity.this, message, Toast.LENGTH_SHORT).show();
                        }
                    } catch (Exception e) {
                        Log.e(TAG, "Error parsing terms response", e);
                        policyTextView.setText("Error displaying terms content.");
                        Toast.makeText(OpolicyActivity.this, "Error displaying terms.", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Log.e(TAG, "API call failed. Code: " + response.code() + ", Message: " + response.message());
                    policyTextView.setText("Failed to load terms. Please check connection.");
                    Toast.makeText(OpolicyActivity.this, "Failed to load terms. Server error.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {
                stopLoadingAnimation(); // Hide loading indicator
                Log.e(TAG, "Network error fetching terms", t);
                policyTextView.setText("Network Error. Please try again later.");
                Toast.makeText(OpolicyActivity.this, "Network Error.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // --- Loading Animation Methods ---
    private void startLoadingAnimation(String message) {
        if (loadingOverlay != null && loadingIcon != null) {
            if(loadingText != null) loadingText.setText(message);
            loadingOverlay.setVisibility(View.VISIBLE);
            Animation rotation = AnimationUtils.loadAnimation(this, R.anim.hourglass_rotation);
            loadingIcon.startAnimation(rotation);
        } else {
            Log.e(TAG, "Loading overlay views not found in layout!");
        }
    }

    private void stopLoadingAnimation() {
        if (loadingOverlay != null && loadingIcon != null && loadingOverlay.getVisibility() == View.VISIBLE) {
            loadingIcon.clearAnimation();
            loadingOverlay.setVisibility(View.GONE);
        }
    }
}