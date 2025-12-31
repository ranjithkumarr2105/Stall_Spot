package com.simats.foodstall;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

public class OhelpActivity extends AppCompatActivity {

    // --- UPDATED PART START ---
    private FrameLayout loadingOverlay;
    private ImageView loadingIcon;
    private TextView loadingText;
    // --- UPDATED PART END ---

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ohelp);

        findViewById(R.id.backButton).setOnClickListener(v -> onBackPressed());



        // --- UPDATED PART START ---
        // Find views for the animation
        loadingOverlay = findViewById(R.id.loadingOverlay);
        loadingIcon = findViewById(R.id.loadingIcon);
        loadingText = findViewById(R.id.loadingText);

        // Update "Send Message" button to use the animation

        // Update card click listeners with correct navigation
        findViewById(R.id.faqCard).setOnClickListener(v -> {
            startActivity(new Intent(this, Ofrequent_questionActivity.class));
        });

        findViewById(R.id.callSupportCard).setOnClickListener(v -> {
            // Create an Intent to open the phone dialer with the support number
            Intent dialIntent = new Intent(Intent.ACTION_DIAL);
            dialIntent.setData(Uri.parse("tel:+911234567890")); // Replace with your actual support number
            startActivity(dialIntent);
        });

        findViewById(R.id.emailSupportCard).setOnClickListener(v -> {
            // Create an Intent to open an email client with pre-filled details
            Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts(
                    "mailto","support@stallspot.com", null));
            emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Support Request - Stall Spot Owner");
            emailIntent.putExtra(Intent.EXTRA_TEXT, "Hello Stall Spot Support,\n\nI have an issue:\n\n");
            startActivity(Intent.createChooser(emailIntent, "Send Email..."));
        });
        // --- UPDATED PART END ---
    }

    // --- UPDATED PART START ---
    // Added the showLoadingOverlay method to enable the animation
    private void showLoadingOverlay(String message, Runnable onComplete) {
        loadingText.setText(message);
        loadingOverlay.setVisibility(View.VISIBLE);
        Animation rotation = AnimationUtils.loadAnimation(this, R.anim.hourglass_rotation);
        loadingIcon.startAnimation(rotation);

        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            loadingIcon.clearAnimation();
            loadingOverlay.setVisibility(View.GONE);
            if (onComplete != null) {
                onComplete.run();
            }
        }, 1500); // 1.5-second delay to simulate network/sending activity
    }
    // --- UPDATED PART END ---
}