package com.simats.foodstall.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.simats.foodstall.R;
import com.simats.foodstall.model.RequestedStall;
import java.util.List;
import de.hdodenhof.circleimageview.CircleImageView;

public class RequestedStallsAdapter extends RecyclerView.Adapter<RequestedStallsAdapter.StallViewHolder> {
    private final List<RequestedStall> stallsList;
    private final OnItemClickListener listener;

    // Interface to send click events back to the Activity
    public interface OnItemClickListener {
        void onViewClick(RequestedStall stall);
    }

    public RequestedStallsAdapter(List<RequestedStall> stallsList, OnItemClickListener listener) {
        this.stallsList = stallsList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public StallViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.card_requested_stall, parent, false);
        return new StallViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull StallViewHolder holder, int position) {
        RequestedStall stall = stallsList.get(position);

        // Use getters to populate the views
        holder.stallImage.setImageResource(stall.getStallImageResId());
        holder.stallName.setText(stall.getStallName());
        holder.ownerName.setText(stall.getOwnerName());
        holder.requestedDate.setText("Requested: " + stall.getRequestedDate());

        // Set the click listener for the view button
        holder.viewButton.setOnClickListener(v -> listener.onViewClick(stall));
    }

    @Override
    public int getItemCount() {
        return stallsList.size();
    }

    public static class StallViewHolder extends RecyclerView.ViewHolder {
        CircleImageView stallImage;
        TextView stallName, ownerName, requestedDate;
        Button viewButton; // Added the button here

        public StallViewHolder(@NonNull View itemView) {
            super(itemView);
            stallImage = itemView.findViewById(R.id.stallImage);
            stallName = itemView.findViewById(R.id.stallNameTextView);
            ownerName = itemView.findViewById(R.id.ownerNameTextView);
            requestedDate = itemView.findViewById(R.id.requestedDateTextView);
            viewButton = itemView.findViewById(R.id.viewButton); // Find the button by its ID
        }
    }
}