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

import org.json.JSONObject; // Needed for parsing JSON

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class Uprivacy_policyActivity extends AppCompatActivity {

    private static final String TAG = "UprivacyPolicyActivity";
    // Define the key used to fetch this specific policy from the database
    private static final String POLICY_KEY = "user_privacy";

    // UI Elements
    private TextView policyTextView;
    private FrameLayout loadingOverlay;
    private ImageView loadingIcon;
    private TextView loadingText; // Optional: If you have text in your loading overlay

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Ensure your layout file includes a TextView with id "policyTextView"
        // and the FrameLayout loading overlay structure
        setContentView(R.layout.uprivacy_policy);

        // Link Views
        policyTextView = findViewById(R.id.policyTextView);
        loadingOverlay = findViewById(R.id.loadingOverlay);
        loadingIcon = findViewById(R.id.loadingIcon);
        loadingText = findViewById(R.id.loadingText); // Link this if it exists in your XML

        // Set up the back button
        findViewById(R.id.backButton).setOnClickListener(v -> onBackPressed());

        // Fetch content when activity starts
        fetchPolicyContent();
    }

    private void fetchPolicyContent() {
        startLoadingAnimation("Loading Policy..."); // Show loading indicator

        ApiClient.getClient().create(ApiService.class).getPolicyContent(POLICY_KEY).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                stopLoadingAnimation(); // Hide loading indicator
                if (response.isSuccessful() && response.body() != null) {
                    try {
                        // Read the response body as a string
                        String responseString = response.body().string();
                        // Parse the JSON string
                        JSONObject jsonObject = new JSONObject(responseString);

                        // Check if the API call was successful according to your JSON structure
                        if ("success".equals(jsonObject.optString("status"))) {
                            String content = jsonObject.optString("content", "Policy content not available.");
                            // Use Html.fromHtml for basic formatting (bold, paragraphs) if needed
                            policyTextView.setText(Html.fromHtml(content, Html.FROM_HTML_MODE_LEGACY));
                        } else {
                            // Show error message from the server response
                            String message = jsonObject.optString("message", "Failed to load policy.");
                            Log.e(TAG, "API Error fetching policy: " + message);
                            policyTextView.setText(message); // Display error in the text view
                            Toast.makeText(Uprivacy_policyActivity.this, message, Toast.LENGTH_SHORT).show();
                        }
                    } catch (Exception e) { // Catch potential IO or JSON parsing errors
                        Log.e(TAG, "Error parsing policy response", e);
                        policyTextView.setText("Error displaying policy content.");
                        Toast.makeText(Uprivacy_policyActivity.this, "Error displaying policy.", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    // Handle HTTP errors (404, 500, etc.)
                    Log.e(TAG, "API call failed. Code: " + response.code() + ", Message: " + response.message());
                    policyTextView.setText("Failed to load policy. Please check connection.");
                    Toast.makeText(Uprivacy_policyActivity.this, "Failed to load policy. Server error.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {
                stopLoadingAnimation(); // Hide loading indicator
                Log.e(TAG, "Network error fetching policy", t);
                policyTextView.setText("Network Error. Please try again later.");
                Toast.makeText(Uprivacy_policyActivity.this, "Network Error.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // --- Loading Animation Methods ---
    private void startLoadingAnimation(String message) {
        if (loadingOverlay != null && loadingIcon != null) {
            if(loadingText != null) loadingText.setText(message); // Set text if available
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