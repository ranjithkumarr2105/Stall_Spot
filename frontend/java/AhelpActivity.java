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

public class AhelpActivity extends AppCompatActivity {

    private FrameLayout loadingOverlay;
    private ImageView loadingIcon;
    private TextView loadingText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ahelp);

        findViewById(R.id.backButton).setOnClickListener(v -> onBackPressed());
        loadingOverlay = findViewById(R.id.loadingOverlay);
        loadingIcon = findViewById(R.id.loadingIcon);
        loadingText = findViewById(R.id.loadingText);


        // --- UPDATED PART START ---
        // Navigation is now specific to Admin activities
        findViewById(R.id.faqCard).setOnClickListener(v -> {
            startActivity(new Intent(this, AfaqActivity.class));
        });

        findViewById(R.id.callSupportCard).setOnClickListener(v -> {
            Intent dialIntent = new Intent(Intent.ACTION_DIAL);
            dialIntent.setData(Uri.parse("tel:+911234567890")); // Admin support number
            startActivity(dialIntent);
        });

        findViewById(R.id.emailSupportCard).setOnClickListener(v -> {
            Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts(
                    "mailto","admin.support@stallspot.com", null));
            emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Admin Support Request - Stall Spot");
            startActivity(Intent.createChooser(emailIntent, "Send Email..."));
        });
        // --- UPDATED PART END ---
    }

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
        }, 1500);
    }
}