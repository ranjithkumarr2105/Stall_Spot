package com.simats.foodstall; // Use your package name

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.Toast;

import com.simats.foodstall.model.StallDelete;

public class Adelete_stallActivity extends AppCompatActivity {

    private EditText stallNameEditText, ownerIdEditText, ownerNameEditText, latitudeEditText, longitudeEditText, reasonEditText;
    private Switch closedSwitch;
    private Button saveButton, deleteButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.adelete_stall);

        // Find views by their IDs
        stallNameEditText = findViewById(R.id.stallNameEditText);
        ownerIdEditText = findViewById(R.id.ownerIdEditText);
        ownerNameEditText = findViewById(R.id.ownerNameEditText);
        latitudeEditText = findViewById(R.id.latitudeEditText);
        longitudeEditText = findViewById(R.id.longitudeEditText);
        reasonEditText = findViewById(R.id.reasonEditText);
        closedSwitch = findViewById(R.id.closedSwitch);
        saveButton = findViewById(R.id.saveButton);
        deleteButton = findViewById(R.id.deleteButton);

        // Get dummy data and populate the fields
        StallDelete stallDetails = getDummyStallDetails();
        populateForm(stallDetails);

        // Back button listener
        findViewById(R.id.backButton).setOnClickListener(v -> onBackPressed());

        // Handle the switch state change to show/hide the reason field
        closedSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                reasonEditText.setVisibility(View.VISIBLE);
            } else {
                reasonEditText.setVisibility(View.GONE);
            }
        });

        // Save Changes button listener
        saveButton.setOnClickListener(v -> {
            // Add your logic to save the updated stall details here
            Toast.makeText(this, "Changes saved successfully.", Toast.LENGTH_SHORT).show();
        });

        // Delete Stall button listener
        deleteButton.setOnClickListener(v -> {
            // Add your logic to delete the stall here
            Toast.makeText(this, "Stall deleted.", Toast.LENGTH_SHORT).show();
            finish(); // Close the activity after deletion
        });
    }

    private StallDelete getDummyStallDetails() {
        return new StallDelete(
                "Food Fiesta",
                "S12345678",
                "John Smith",
                "12.345",
                "78.901",
                false,
                ""
        );
    }

    private void populateForm(StallDelete stall) {
        stallNameEditText.setText(stall.stallName);
        ownerIdEditText.setText(stall.ownerId);
        ownerNameEditText.setText(stall.ownerName);
        latitudeEditText.setText(stall.latitude);
        longitudeEditText.setText(stall.longitude);
        closedSwitch.setChecked(stall.isClosed);
        if (stall.isClosed) {
            reasonEditText.setText(stall.closeReason);
            reasonEditText.setVisibility(View.VISIBLE);
        } else {
            reasonEditText.setVisibility(View.GONE);
        }
    }
}
