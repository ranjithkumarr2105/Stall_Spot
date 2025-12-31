package com.simats.foodstall; // Use your package name

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

public class SearchpageActivity extends AppCompatActivity {

    private EditText searchEditText;
    private ImageView backButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.searchpage);

        // Find views by their IDs
        searchEditText = findViewById(R.id.searchEditText);
        backButton = findViewById(R.id.backButton);

        // Set up the back button
        backButton.setOnClickListener(v -> onBackPressed());

        // Set up search bar listener
        searchEditText.setOnEditorActionListener((v, actionId, event) -> {
            // Add search query logic here
            Toast.makeText(this, "Searching for: " + v.getText().toString(), Toast.LENGTH_SHORT).show();
            return true;
        });
    }
}
