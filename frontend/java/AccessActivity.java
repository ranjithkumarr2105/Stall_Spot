package com.simats.foodstall;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class AccessActivity extends AppCompatActivity {

    SharedPreferences preferences;
    Switch switchLocation, switchMessage, switchDate, switchTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.access);

        preferences = getSharedPreferences("Permissions", MODE_PRIVATE);

        setupCard(findViewById(R.id.cardLocation), R.drawable.ic_location, "Location Access",
                "Allow access to your device to find nearby food stalls", "location");

        setupCard(findViewById(R.id.cardMessage), R.drawable.ic_message, "Message Access",
                "Enable message access to receive updates and alerts", "message");

        setupCard(findViewById(R.id.cardDate), R.drawable.ic_date1, "Date Access",
                "Enable date access to sync with your daily preferences and menu availability", "date");

        setupCard(findViewById(R.id.cardTime), R.drawable.ic_time, "Time Access",
                "Allow access to your system time for accurate scheduling and menu timing", "time");

        Button btnAllow = findViewById(R.id.btnAllow);
        btnAllow.setOnClickListener(v -> {
            Toast.makeText(this, "Permissions saved!", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(AccessActivity.this, UhomeActivity.class);
            startActivity(intent);
            finish(); // Optional: so user can't come back to this screen with back button
        });

        findViewById(R.id.textSkip).setOnClickListener(v -> finish());
    }

    private void setupCard(View view, int iconRes, String title, String desc, String key) {
        ImageView icon = view.findViewById(R.id.permission_icon);
        TextView titleText = view.findViewById(R.id.permission_title);
        TextView descText = view.findViewById(R.id.permission_desc);
        Switch sw = view.findViewById(R.id.permission_switch);

        icon.setImageResource(iconRes);
        titleText.setText(title);
        descText.setText(desc);
        sw.setChecked(preferences.getBoolean(key, false));

        sw.setOnCheckedChangeListener((buttonView, isChecked) -> {
            preferences.edit().putBoolean(key, isChecked).apply();
        });
    }
}
