package com.simats.foodstall.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.simats.foodstall.R;
import com.simats.foodstall.model.AStallLocation;
import java.util.List;
import java.util.Locale;

public class StallLocationAdapter extends RecyclerView.Adapter<StallLocationAdapter.LocationViewHolder> {

    public interface OnLocationInteractionListener {
        void onEdit(AStallLocation location);
        void onDelete(AStallLocation location);
    }

    private final List<AStallLocation> locations;
    private final OnLocationInteractionListener listener;

    public StallLocationAdapter(List<AStallLocation> locations, OnLocationInteractionListener listener) {
        this.locations = locations;
        this.listener = listener;
    }

    @NonNull
    @Override
    public LocationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_stall_location, parent, false);
        return new LocationViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull LocationViewHolder holder, int position) {
        holder.bind(locations.get(position));
    }

    @Override
    public int getItemCount() {
        return locations.size();
    }

    class LocationViewHolder extends RecyclerView.ViewHolder {
        TextView stallNameTextView, coordinatesTextView;
        ImageButton editButton, deleteButton;

        public LocationViewHolder(@NonNull View itemView) {
            super(itemView);
            stallNameTextView = itemView.findViewById(R.id.stallNameTextView);
            coordinatesTextView = itemView.findViewById(R.id.coordinatesTextView);
            editButton = itemView.findViewById(R.id.editButton);
            deleteButton = itemView.findViewById(R.id.deleteButton);
        }

        public void bind(final AStallLocation location) {
            String nameAndId = String.format("%s (%s)", location.getStallName(), location.getStallId());
            stallNameTextView.setText(nameAndId);
            String coords = String.format(Locale.US, "Lat: %.4f, Lon: %.4f", location.getLatitude(), location.getLongitude());
            coordinatesTextView.setText(coords);

            editButton.setOnClickListener(v -> listener.onEdit(location));
            deleteButton.setOnClickListener(v -> listener.onDelete(location));
        }
    }
}