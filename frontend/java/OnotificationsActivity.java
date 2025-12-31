package com.simats.foodstall; // Use your package name

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;

import com.simats.foodstall.adapter.OwnerNotificationAdapter;
import com.simats.foodstall.model.OwnerNotification;

import java.util.ArrayList;
import java.util.List;

public class OnotificationsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.onotifications);

        // Back button listener
        findViewById(R.id.backButton).setOnClickListener(v -> onBackPressed());

        // Setup RecyclerView
        RecyclerView notificationsRecyclerView = findViewById(R.id.notificationsRecyclerView);
        notificationsRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Create dummy data
        List<OwnerNotification> notificationsList = getDummyNotifications();

        // Setup Adapter
        OwnerNotificationAdapter adapter = new OwnerNotificationAdapter(notificationsList);
        notificationsRecyclerView.setAdapter(adapter);
    }

    private List<OwnerNotification> getDummyNotifications() {
        List<OwnerNotification> notifications = new ArrayList<>();
        // Updated logic to assign icons based on the notification title
        notifications.add(new OwnerNotification("System Update", "App update v1.5 available now", "1h ago", R.drawable.ic_wrench));
        notifications.add(new OwnerNotification("New Stall Opened Nearby", "Chennai Dosa Corner opened 2 stalls away", "Just now", R.drawable.ic_wrench1));
        notifications.add(new OwnerNotification("Maintenance Notice", "Scheduled downtime tonight from 2–3 AM", "Yesterday", R.drawable.ic_wrench));
        notifications.add(new OwnerNotification("Safety Guidelines Updated", "New hygiene protocols added for monsoon", "2 days ago", R.drawable.ic_wrench));
        return notifications;
    }
}
