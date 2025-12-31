package com.simats.foodstall.adapter;

import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.simats.foodstall.R;
import com.simats.foodstall.TextDrawableUtil;
import com.simats.foodstall.model.StallEdit;
import java.util.ArrayList;
import java.util.List;
import de.hdodenhof.circleimageview.CircleImageView;

public class AeditstallAdapter extends RecyclerView.Adapter<AeditstallAdapter.StallViewHolder> {

    private List<StallEdit> stallsList;
    private final OnStallEditClickListener listener;

    public interface OnStallEditClickListener {
        void onEditClick(StallEdit stall);
    }

    public AeditstallAdapter(List<StallEdit> stallsList, OnStallEditClickListener listener) {
        this.stallsList = new ArrayList<>(stallsList);
        this.listener = listener;
    }

    @NonNull
    @Override
    public StallViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.card_edit_stall, parent, false);
        return new StallViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull StallViewHolder holder, int position) {
        StallEdit stall = stallsList.get(position);

        // --- UPDATED: Load image with TextDrawableUtil as placeholder ---
        Drawable initialsDrawable = TextDrawableUtil.getInitialDrawable(stall.getStallName());

        Glide.with(holder.itemView.getContext())
                .load(stall.getProfilePhoto()) // Try to load the real image URL
                .placeholder(initialsDrawable) // Show initials while loading
                .error(initialsDrawable)       // Show initials if loading fails
                .into(holder.stallImage);
        // --- END OF UPDATE ---

        holder.stallName.setText(stall.getStallName());
        holder.ownerId.setText(stall.getStallId());
        holder.ownerName.setText(stall.getOwnerName());

        holder.editButton.setOnClickListener(v -> {
            if (listener != null) {
                listener.onEditClick(stall);
            }
        });
    }

    @Override
    public int getItemCount() {
        return stallsList.size();
    }

    public void filterList(List<StallEdit> filteredList) {
        stallsList = filteredList;
        notifyDataSetChanged();
    }

    public static class StallViewHolder extends RecyclerView.ViewHolder {
        CircleImageView stallImage;
        TextView stallName, ownerId, ownerName;
        Button editButton;

        public StallViewHolder(@NonNull View itemView) {
            super(itemView);
            stallImage = itemView.findViewById(R.id.stallImage);
            stallName = itemView.findViewById(R.id.stallNameTextView);
            ownerName = itemView.findViewById(R.id.ownerNameTextView);
            ownerId = itemView.findViewById(R.id.ownerIdTextView);
            editButton = itemView.findViewById(R.id.editButton);
        }
    }
}