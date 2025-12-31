package com.simats.foodstall.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.simats.foodstall.R;
import com.simats.foodstall.model.NotificationAdmin;
import java.util.List;

public class NotificationAdminAdapter extends RecyclerView.Adapter<NotificationAdminAdapter.NotificationViewHolder> {

    private final List<NotificationAdmin> notifications;

    public NotificationAdminAdapter(List<NotificationAdmin> notifications) {
        this.notifications = notifications;
    }

    @NonNull
    @Override
    public NotificationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.anotifications_item, parent, false);
        return new NotificationViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NotificationViewHolder holder, int position) {
        NotificationAdmin notification = notifications.get(position);
        holder.title.setText(notification.getTitle());
        holder.message.setText(notification.getMessage());
        holder.time.setText(notification.getTime());

        // This logic now works because newNotificationIndicator is found correctly
        if (notification.isNew()) {
            holder.newNotificationIndicator.setVisibility(View.VISIBLE);
        } else {
            holder.newNotificationIndicator.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return notifications.size();
    }

    public static class NotificationViewHolder extends RecyclerView.ViewHolder {
        TextView title, message, time;
        ImageView newNotificationIndicator; // This view is now correctly declared

        public NotificationViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.notificationTitle);
            message = itemView.findViewById(R.id.notificationMessage);
            time = itemView.findViewById(R.id.notificationTime);
            newNotificationIndicator = itemView.findViewById(R.id.newNotificationIndicator); // And found here
        }
    }
}