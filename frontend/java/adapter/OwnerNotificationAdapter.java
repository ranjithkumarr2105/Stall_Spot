package com.simats.foodstall.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.simats.foodstall.R;
import com.simats.foodstall.model.OwnerNotification;
import java.util.List;

public class OwnerNotificationAdapter extends RecyclerView.Adapter<OwnerNotificationAdapter.NotificationViewHolder> {
    private final List<OwnerNotification> notificationsList;

    public OwnerNotificationAdapter(List<OwnerNotification> notificationsList) {
        this.notificationsList = notificationsList;
    }

    @NonNull
    @Override
    public NotificationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.card_owner_notification, parent, false);
        return new NotificationViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull NotificationViewHolder holder, int position) {
        OwnerNotification notification = notificationsList.get(position);
        holder.title.setText(notification.title);
        holder.message.setText(notification.message);
        holder.time.setText(notification.time);
        holder.icon.setImageResource(notification.iconResId);
    }

    @Override
    public int getItemCount() {
        return notificationsList.size();
    }

    public static class NotificationViewHolder extends RecyclerView.ViewHolder {
        public TextView title, message, time;
        public ImageView icon;

        public NotificationViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.notificationTitle);
            message = itemView.findViewById(R.id.notificationMessage);
            time = itemView.findViewById(R.id.notificationTime);
            icon = itemView.findViewById(R.id.notificationIcon);
        }
    }
}