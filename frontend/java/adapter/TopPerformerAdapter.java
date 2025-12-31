package com.simats.foodstall.adapter;

import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.simats.foodstall.R;
import com.simats.foodstall.TextDrawableUtil;
import com.simats.foodstall.model.TopPerformer;
import java.util.List;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;

public class TopPerformerAdapter extends RecyclerView.Adapter<TopPerformerAdapter.TopPerformerViewHolder> {
    private final List<TopPerformer> performersList;

    public TopPerformerAdapter(List<TopPerformer> performersList) {
        this.performersList = performersList;
    }

    @NonNull
    @Override
    public TopPerformerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_top_performer, parent, false);
        return new TopPerformerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TopPerformerViewHolder holder, int position) {
        TopPerformer performer = performersList.get(position);

        // Set rank number and background color based on position
        holder.rankTextView.setText(String.valueOf(position + 1));
        switch (position) {
            case 0: // 1st place
                holder.rankTextView.getBackground().setTint(Color.parseColor("#EAB308")); // Gold
                break;
            case 1: // 2nd place
                holder.rankTextView.getBackground().setTint(Color.parseColor("#A1A1AA")); // Silver
                break;
            case 2: // 3rd place
                holder.rankTextView.getBackground().setTint(Color.parseColor("#CD7F32")); // Bronze
                break;
        }

        // --- START OF UI FIX: Use TextDrawableUtil to generate initials ---
        Drawable textDrawable = TextDrawableUtil.getInitialDrawable(performer.getItemName());
        holder.dishImage.setImageDrawable(textDrawable);
        // --- END OF UI FIX ---

        holder.dishName.setText(performer.getItemName());
        String detailsText = String.format(Locale.getDefault(), "%d units • ₹%.2f",
                performer.getTotalQuantity(), performer.getTotalRevenue());
        holder.details.setText(detailsText);
    }

    @Override
    public int getItemCount() {
        return performersList.size();
    }

    public static class TopPerformerViewHolder extends RecyclerView.ViewHolder {
        TextView rankTextView;
        CircleImageView dishImage;
        TextView dishName, details;

        public TopPerformerViewHolder(@NonNull View itemView) {
            super(itemView);
            rankTextView = itemView.findViewById(R.id.rankTextView);
            dishImage = itemView.findViewById(R.id.dishImage);
            dishName = itemView.findViewById(R.id.dishName);
            details = itemView.findViewById(R.id.details);
        }
    }
}