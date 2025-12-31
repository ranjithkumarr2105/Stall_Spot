package com.simats.foodstall;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;

import com.simats.foodstall.adapter.NotificationAdapter;
import com.simats.foodstall.model.Notification;

import java.util.ArrayList;
import java.util.List;

public class UnotificationsActivity extends AppCompatActivity {

    // --- UPDATED PART START ---
    private RecyclerView notificationsRecyclerView;
    private LinearLayout emptyStateLayout;
    private NotificationAdapter adapter;
    private List<Notification> notificationsList = new ArrayList<>();
    // --- UPDATED PART END ---

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.unotifications);

        findViewById(R.id.backButton).setOnClickListener(v -> onBackPressed());

        // --- UPDATED PART START ---
        // Find views from the new layout
        notificationsRecyclerView = findViewById(R.id.notificationsRecyclerView);
        emptyStateLayout = findViewById(R.id.emptyStateLayout);
        notificationsRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Load dummy data
        loadDummyNotifications();

        // Setup Adapter
        adapter = new NotificationAdapter(this, notificationsList);
        notificationsRecyclerView.setAdapter(adapter);

        // Check if the list is empty and update the UI accordingly
        checkEmptyState();
        // --- UPDATED PART END ---
    }

    // --- UPDATED PART START ---
    // NEW METHOD to check if the list is empty
    private void checkEmptyState() {
        if (notificationsList.isEmpty()) {
            notificationsRecyclerView.setVisibility(View.GONE);
            emptyStateLayout.setVisibility(View.VISIBLE);
        } else {
            notificationsRecyclerView.setVisibility(View.VISIBLE);
            emptyStateLayout.setVisibility(View.GONE);
        }
    }

    // Updated dummy data to include the isRead flag
    private void loadDummyNotifications() {
        notificationsList.clear();
        notificationsList.add(new Notification("New", "New Stall Opened Nearby!", "BBQ Express is now open near you with a 10% opening discount.", "500m away", "10 mins ago", false));
        notificationsList.add(new Notification("Offer", "Limited-Time Offer at Tasty Truck", "Get 20% off on all menu items today! Valid until 8 PM.", "2.1 km away", "1 hour ago", false));
        notificationsList.add(new Notification("Popular", "Street Food Festival This Weekend", "Join us for the biggest street food gathering with 30+ vendors.", "1.2 km away", "Today 4:30 PM", true));
        notificationsList.add(new Notification("Offer", "Buy One Get One Free!", "On all milkshakes at Shake It Up. Don't miss out!", "1.5 km away", "Yesterday", true));
    }
    // --- UPDATED PART END ---
}