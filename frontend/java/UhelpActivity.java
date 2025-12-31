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

public class UhelpActivity extends AppCompatActivity {

    private FrameLayout loadingOverlay;
    private ImageView loadingIcon;
    private TextView loadingText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.uhelp);

        // Find views
        findViewById(R.id.backButton).setOnClickListener(v -> onBackPressed());

        loadingOverlay = findViewById(R.id.loadingOverlay);
        loadingIcon = findViewById(R.id.loadingIcon);
        loadingText = findViewById(R.id.loadingText);

        // --- UPDATED PART START ---
        // Setup all click listeners with correct navigation and animations


        // Navigate to the User's FAQ Activity
        findViewById(R.id.faqCard).setOnClickListener(v -> {
            startActivity(new Intent(this, Ufrequent_questionActivity.class));
        });

        // Open the phone dialer with a pre-filled number
        findViewById(R.id.callSupportCard).setOnClickListener(v -> {
            Intent dialIntent = new Intent(Intent.ACTION_DIAL);
            dialIntent.setData(Uri.parse("tel:+911234567890")); // Replace with your actual support number
            startActivity(dialIntent);
        });

        // Open an email client with pre-filled details
        findViewById(R.id.emailSupportCard).setOnClickListener(v -> {
            Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts(
                    "mailto","support@stallspot.com", null));
            emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Support Request - Stall Spot User");
            emailIntent.putExtra(Intent.EXTRA_TEXT, "Hello Stall Spot Support,\n\nI have an issue:\n\n");
            startActivity(Intent.createChooser(emailIntent, "Send Email..."));
        });
        // --- UPDATED PART END ---
    }

    // --- UPDATED PART START ---
    // Added the animation method
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
        }, 1500); // 1.5-second delay to simulate sending
    }
    // --- UPDATED PART END ---
}
