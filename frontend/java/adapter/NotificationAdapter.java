package com.simats.foodstall.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.simats.foodstall.R;
import com.simats.foodstall.model.Notification;

import java.util.List;

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.NotificationViewHolder> {

    private final List<Notification> notificationList;
    private final Context context;

    public NotificationAdapter(Context context, List<Notification> notificationList) {
        this.context = context;
        this.notificationList = notificationList;
    }

    @NonNull
    @Override
    public NotificationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_notification, parent, false);
        return new NotificationViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NotificationViewHolder holder, int position) {
        Notification notification = notificationList.get(position);

        holder.titleTextView.setText(notification.getTitle());
        holder.messageTextView.setText(notification.getMessage());
        holder.timestampTextView.setText(notification.getTimestamp());

        // Set the icon based on the notification type
        switch (notification.getType().toLowerCase()) {
            case "offer":
                holder.iconImageView.setImageResource(R.drawable.ic_offer);
                break;
            case "popular":
                holder.iconImageView.setImageResource(R.drawable.ic_popular);
                break;
            case "new":
            default:
                holder.iconImageView.setImageResource(R.drawable.ic_new_stall);
                break;
        }

        // Set the visual state based on whether the notification has been read
        if (notification.isRead()) {
            holder.contentLayout.setBackgroundColor(Color.WHITE);
            holder.unreadIndicator.setVisibility(View.GONE);
        } else {
            holder.contentLayout.setBackgroundColor(ContextCompat.getColor(context, R.color.unread_background));
            holder.unreadIndicator.setVisibility(View.VISIBLE);
        }

        // Handle click on the notification item
        holder.itemView.setOnClickListener(v -> {
            if (!notification.isRead()) {
                notification.setRead(true);
                notifyItemChanged(position); // Update the view to reflect the change
            }
            // You can add navigation logic here if needed
        });
    }

    @Override
    public int getItemCount() {
        return notificationList.size();
    }

    static class NotificationViewHolder extends RecyclerView.ViewHolder {
        ImageView iconImageView;
        TextView titleTextView;
        TextView messageTextView;
        TextView timestampTextView;
        View unreadIndicator;
        LinearLayout contentLayout;

        public NotificationViewHolder(@NonNull View itemView) {
            super(itemView);
            iconImageView = itemView.findViewById(R.id.notificationIcon);
            titleTextView = itemView.findViewById(R.id.notificationTitle);
            messageTextView = itemView.findViewById(R.id.notificationMessage);
            timestampTextView = itemView.findViewById(R.id.notificationTimestamp);
            unreadIndicator = itemView.findViewById(R.id.unreadIndicator);
            contentLayout = itemView.findViewById(R.id.notificationContentLayout);
        }
    }
}