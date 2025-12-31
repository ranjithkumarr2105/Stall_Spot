package com.simats.foodstall.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.simats.foodstall.R;
import com.simats.foodstall.model.StallDelete;
import java.util.List;

public class Adelete_stallAdapter extends RecyclerView.Adapter<Adelete_stallAdapter.StallViewHolder> {
    private final List<StallDelete> stallsList;

    public Adelete_stallAdapter(List<StallDelete> stallsList) {
        this.stallsList = stallsList;
    }

    @NonNull
    @Override
    public StallViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Corrected line to use the adelete_stall.xml layout file
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.adelete_stall, parent, false);
        return new StallViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull StallViewHolder holder, int position) {
        StallDelete stall = stallsList.get(position);
        holder.stallName.setText(stall.stallName);
        holder.ownerId.setText(stall.ownerId);
        holder.ownerName.setText(stall.ownerName);
        holder.latitude.setText(stall.latitude);
        holder.longitude.setText(stall.longitude);

        holder.closedSwitch.setChecked(stall.isClosed);
        if (stall.isClosed) {
            holder.reasonEditText.setVisibility(View.VISIBLE);
            holder.reasonEditText.setText(stall.closeReason);
        } else {
            holder.reasonEditText.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return stallsList.size();
    }

    public static class StallViewHolder extends RecyclerView.ViewHolder {
        TextView stallName, ownerId, ownerName, latitude, longitude;
        Button editButton, deleteButton;
        Switch closedSwitch;
        EditText reasonEditText;

        public StallViewHolder(@NonNull View itemView) {
            super(itemView);
            stallName = itemView.findViewById(R.id.stallNameTextView);
            ownerId = itemView.findViewById(R.id.ownerIdTextView);
            ownerName = itemView.findViewById(R.id.ownerNameTextView);
            latitude = itemView.findViewById(R.id.latitudeEditText);
            longitude = itemView.findViewById(R.id.longitudeEditText);
            editButton = itemView.findViewById(R.id.editButton);
            deleteButton = itemView.findViewById(R.id.deleteButton);
            closedSwitch = itemView.findViewById(R.id.closedSwitch);
            reasonEditText = itemView.findViewById(R.id.reasonEditText);
        }
    }
}