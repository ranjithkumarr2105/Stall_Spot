package com.simats.foodstall;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AnticipateOvershootInterpolator; // For bounce effect
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    // Define animation durations
    private static final long PIN_DROP_DURATION = 800;
    private static final long MORPH_DURATION = 600;
    private static final long TEXT_FADE_DURATION = 500;
    private static final long START_DELAY = 300; // Initial delay before animation starts
    private static final long NAVIGATE_DELAY = 2500; // Total time before navigating (adjust as needed)

    private ImageView mapPinIcon;
    private ImageView logoImageView;
    private TextView appNameTextView;
    private TextView sloganTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        // Remove button logic
        // Button getStartedButton = findViewById(R.id.getStartedButton);
        // getStartedButton.setOnClickListener(...) // Removed

        // Find views for animation
        mapPinIcon = findViewById(R.id.mapPinIcon);
        logoImageView = findViewById(R.id.imageView2);
        appNameTextView = findViewById(R.id.appName);
        sloganTextView = findViewById(R.id.slogan);

        // Start animation sequence after a short delay
        new Handler(Looper.getMainLooper()).postDelayed(this::startAnimationSequence, START_DELAY);

        // Navigate to LoginActivity after a longer delay
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            Intent intent = new Intent(MainActivity.this, SubscriptionActivity.class);
            startActivity(intent);
            // Use a fade transition to the next activity (optional)
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            finish(); // Prevent going back to splash screen
        }, NAVIGATE_DELAY);
    }

    private void startAnimationSequence() {
        // --- Animation 1: Pin Drop ---
        // Calculate target Y position (relative to its current position to land near center)
        // This is approximate; adjust value as needed based on your layout
        float targetY = logoImageView.getY() - mapPinIcon.getHeight() * 1; // Example target

        ObjectAnimator pinTranslateY = ObjectAnimator.ofFloat(mapPinIcon, "translationY", 0f, targetY);
        pinTranslateY.setInterpolator(new AnticipateOvershootInterpolator(1.5f)); // Adds bounce
        pinTranslateY.setDuration(PIN_DROP_DURATION);

        ObjectAnimator pinFadeIn = ObjectAnimator.ofFloat(mapPinIcon, "alpha", 0f, 1f);
        pinFadeIn.setDuration(PIN_DROP_DURATION / 2); // Fade in faster

        AnimatorSet pinDropSet = new AnimatorSet();
        pinDropSet.playTogether(pinTranslateY, pinFadeIn);

        // --- Animation 2: Morph (Simulated: Pin fade out, Logo scale/fade in) ---
        ObjectAnimator pinFadeOut = ObjectAnimator.ofFloat(mapPinIcon, "alpha", 1f, 0f);
        pinFadeOut.setDuration(MORPH_DURATION);

        ObjectAnimator logoScaleX = ObjectAnimator.ofFloat(logoImageView, "scaleX", 0.5f, 1f);
        logoScaleX.setDuration(MORPH_DURATION);
        ObjectAnimator logoScaleY = ObjectAnimator.ofFloat(logoImageView, "scaleY", 0.5f, 1f);
        logoScaleY.setDuration(MORPH_DURATION);
        ObjectAnimator logoFadeIn = ObjectAnimator.ofFloat(logoImageView, "alpha", 0f, 1f);
        logoFadeIn.setDuration(MORPH_DURATION);

        AnimatorSet morphSet = new AnimatorSet();
        morphSet.playTogether(pinFadeOut, logoScaleX, logoScaleY, logoFadeIn);

        // --- Animation 3: Text Fade In ---
        ObjectAnimator appNameFadeIn = ObjectAnimator.ofFloat(appNameTextView, "alpha", 0f, 1f);
        appNameFadeIn.setDuration(TEXT_FADE_DURATION);
        ObjectAnimator sloganFadeIn = ObjectAnimator.ofFloat(sloganTextView, "alpha", 0f, 1f);
        sloganFadeIn.setDuration(TEXT_FADE_DURATION);
        sloganFadeIn.setStartDelay(150); // Slightly delay slogan

        AnimatorSet textFadeSet = new AnimatorSet();
        textFadeSet.playTogether(appNameFadeIn, sloganFadeIn);

        // --- Chain the animations ---
        AnimatorSet mainSequence = new AnimatorSet();
        mainSequence.playSequentially(pinDropSet, morphSet, textFadeSet);
        mainSequence.setInterpolator(new AccelerateDecelerateInterpolator()); // Smooth overall feel
        mainSequence.start();
    }
}