package com.simats.foodstall; // Use your app's package name

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;

import com.simats.foodstall.adapter.NotificationAdminAdapter;
import com.simats.foodstall.model.NotificationAdmin;

import java.util.ArrayList;
import java.util.List;

public class AnotificationsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.anotifications);

        findViewById(R.id.backButton).setOnClickListener(v -> onBackPressed());

        RecyclerView notificationsRecyclerView = findViewById(R.id.notificationsRecyclerView);
        notificationsRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        List<NotificationAdmin> notificationsList = getDummyNotifications();
        NotificationAdminAdapter adapter = new NotificationAdminAdapter(notificationsList);
        notificationsRecyclerView.setAdapter(adapter);
    }

    private List<NotificationAdmin> getDummyNotifications() {
        List<NotificationAdmin> notifications = new ArrayList<>();
        notifications.add(new NotificationAdmin("New Stall Approval Request", "Veg Delight has requested stall approval", "Today, 10:30 AM", true));
        notifications.add(new NotificationAdmin("Reported Review", "A user reported a review for Campus Cafe", "Today, 9:15 AM", true));
        notifications.add(new NotificationAdmin("Menu Update", "Owner of Campus Cafe updated today's menu", "Yesterday, 4:15 PM", false));
        notifications.add(new NotificationAdmin("Daily Revenue Report", "Today's revenue report is now available", "Yesterday, 2:30 PM", false));
        notifications.add(new NotificationAdmin("New User Registration", "A new user registered for stall access", "Yesterday, 11:45 AM", false));
        return notifications;
    }
}
