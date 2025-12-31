package com.simats.foodstall;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class OrejectedActivity extends AppCompatActivity {

    // [NEW] Class variables to store owner data
    private String rejectionReason;
    private String ownerPhone;
    private String ownerEmail;
    private String ownerName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.orejected); // Use your new professional layout

        TextView reasonTextView = findViewById(R.id.reasonTextView);
        Button resubmitButton = findViewById(R.id.resubmitButton);
        Button logoutButton = findViewById(R.id.logoutButton);

        // Get all data from LoginActivity
        Intent intent = getIntent();
        rejectionReason = intent.getStringExtra("REJECTION_REASON");
        ownerPhone = intent.getStringExtra("OWNER_PHONE");
        ownerEmail = intent.getStringExtra("OWNER_EMAIL");
        ownerName = intent.getStringExtra("USER_NAME");

        // Set the reason text on the screen
        if (rejectionReason != null && !rejectionReason.isEmpty()) {
            reasonTextView.setText(rejectionReason);
        } else {
            reasonTextView.setText("No specific reason was provided by the admin.");
        }

        // --- [FIX] This logic now passes data to the resubmit screen ---
        resubmitButton.setOnClickListener(v -> {
            Intent editIntent = new Intent(this, OstalldetailsActivity.class);

            // Pass all the owner's data so the form can be pre-filled
            editIntent.putExtra("OWNER_PHONE", ownerPhone);
            editIntent.putExtra("OWNER_EMAIL", ownerEmail);
            editIntent.putExtra("USER_NAME", ownerName);

            // Tell OstalldetailsActivity this is a manual login, not Google
            editIntent.putExtra("IS_GOOGLE_SIGNUP", false);

            // [NEW] Add this flag to tell the next screen to ask for the password
            editIntent.putExtra("IS_RESUBMIT", true);

            startActivity(editIntent);
            finish();
        });

        // --- This adds the logout confirmation dialog ---
        logoutButton.setOnClickListener(v -> {
            showLogoutConfirmationDialog();
        });
    }

    private void showLogoutConfirmationDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Logout")
                .setMessage("Are you sure you want to logout?")
                .setPositiveButton("Yes", (dialog, which) -> {
                    Toast.makeText(this, "Logging out...", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(OrejectedActivity.this, LoginActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();
                })
                .setNegativeButton("No", null) // "No" does nothing, just closes the dialog
                .show();
    }
}