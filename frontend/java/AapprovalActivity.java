package com.simats.foodstall;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class AapprovalActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // This links the Java code to your aapproval.xml layout file
        setContentView(R.layout.aapproval);

        // Find the views from your XML layout using their specific IDs
        TextView stallNameTextView = findViewById(R.id.stallNameTextView);
        TextView ownerIdTextView = findViewById(R.id.ownerIdTextView);
        Button backToListButton = findViewById(R.id.backToListButton);

        // Get the data that was passed from the Astall_detailsActivity
        Intent intent = getIntent();
        String stallName = intent.getStringExtra("STALL_NAME");
        String stallId = intent.getStringExtra("STALL_ID");

        // Set the text on the screen using the received data
        if (stallName != null) {
            // Formats the text exactly like your screenshot's example
            stallNameTextView.setText("Stall '" + stallName + "' Approved Successfully!");
        }

        if (stallId != null && !stallId.isEmpty()) {
            // Sets the generated Owner ID, fixing the "null" issue
            ownerIdTextView.setText("Owner ID: " + stallId);
        } else {
            ownerIdTextView.setText("Owner ID: Not Generated");
        }

        // Set the click listener for the "Back to New Stall List" button
        backToListButton.setOnClickListener(v -> {
            // Create an intent to go back to the list of new stalls
            Intent backIntent = new Intent(AapprovalActivity.this, Anew_stallActivity.class);

            // These flags clear the activity history so the admin doesn't go back
            // to the details screen of the stall they just approved.
            backIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);

            startActivity(backIntent);
            finish(); // Finish this current activity
        });
    }
}