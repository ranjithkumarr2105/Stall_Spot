package com.simats.foodstall; // Replace with your actual package name

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.simats.foodstall.R;

public class SubscriptionActivity extends AppCompatActivity {

    private Button subscribeButton;
    private TextView maybeLaterTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_subscription);

        // Hide the action bar for a cleaner UI (optional)
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        initViews();
        setupListeners();
    }

    private void initViews() {
        subscribeButton = findViewById(R.id.subscribeButton);
        maybeLaterTextView = findViewById(R.id.maybeLaterTextView);
    }

    private void setupListeners() {
        // 1. Subscribe Action
        subscribeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Placeholder for Razorpay or other payment logic
                Toast.makeText(SubscriptionActivity.this, "Payment Integration Coming Soon!", Toast.LENGTH_SHORT).show();
            }
        });

        // 2. Maybe Later Action (Navigate to Login)
        maybeLaterTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SubscriptionActivity.this, LoginActivity.class);
                startActivity(intent);
                overridePendingTransition(0, 0);
                finish(); // Finish this activity so back button doesn't return here
            }
        });
    }
}