package com.simats.foodstall;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class OapprovedActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.oapproved);

        TextView ownerIdTextView = findViewById(R.id.ownerIdTextView);
        Button loginButton = findViewById(R.id.loginButton);

        Intent intent = getIntent();
        String stallId = intent.getStringExtra("STALL_ID");

        if (stallId != null) {
            ownerIdTextView.setText(stallId);
        } else {
            ownerIdTextView.setText("ID Not Found");
        }

        // --- THIS IS THE FIX ---
        // This button now correctly navigates back to the Login screen.
        loginButton.setOnClickListener(v -> {
            Intent loginIntent = new Intent(OapprovedActivity.this, LoginActivity.class);
            loginIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(loginIntent);
            finish();
        });
    }
}