package com.simats.foodstall;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

public class OpendingActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.opending);

        Button logoutButton = findViewById(R.id.logoutButton);

        logoutButton.setOnClickListener(v -> {
            // Show the confirmation dialog when the logout button is clicked
            showLogoutConfirmationDialog();
        });
    }

    private void showLogoutConfirmationDialog() {
        // Create a new AlertDialog builder
        new AlertDialog.Builder(this)
                .setTitle("Logout")
                .setMessage("Are you sure you want to logout?")
                // The "Yes" button
                .setPositiveButton("Yes", (dialog, which) -> {
                    // Code to run when "Yes" is clicked
                    Toast.makeText(this, "Logging out...", Toast.LENGTH_SHORT).show();

                    // Navigate back to the Login screen
                    Intent intent = new Intent(OpendingActivity.this, LoginActivity.class);
                    // Clear all previous activities from the stack
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();
                })
                // The "No" button
                .setNegativeButton("No", (dialog, which) -> {
                    // Code to run when "No" is clicked
                    dialog.dismiss(); // Just close the dialog
                })
                .show(); // Display the dialog
    }
}