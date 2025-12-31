package com.simats.foodstall;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class ArejectionActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.arejection); // Make sure your XML is named arejection.xml

        TextView stallNameTextView = findViewById(R.id.stallNameTextView);
        TextView reasonTextView = findViewById(R.id.reasonTextView);
        Button backButton = findViewById(R.id.backToListButton);

        // Get the data that was passed from AreasonActivity
        Intent intent = getIntent();
        String stallName = intent.getStringExtra("STALL_NAME");
        String rejectionReason = intent.getStringExtra("REJECTION_REASON");

        if (stallName != null) {
            stallNameTextView.setText("Stall '" + stallName + "' Has Been Rejected");
        }

        // Display the reason that was passed from the previous screen
        if (rejectionReason != null) {
            reasonTextView.setText(rejectionReason);
        } else {
            reasonTextView.setText("No reason provided.");
        }

        backButton.setOnClickListener(v -> {
            Intent backIntent = new Intent(ArejectionActivity.this, Anew_stallActivity.class);
            backIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(backIntent);
            finish();
        });
    }
}