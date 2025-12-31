package com.simats.foodstall.adapter;

import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.simats.foodstall.R;
import com.simats.foodstall.TextDrawableUtil;
import com.simats.foodstall.model.Stall;

import java.util.Locale;

public class StallsAdapter extends ListAdapter<Stall, StallsAdapter.StallViewHolder> {

    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(Stall stall); // Interface remains the same
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    public StallsAdapter() {
        super(DIFF_CALLBACK);
    }

    private static final DiffUtil.ItemCallback<Stall> DIFF_CALLBACK = new DiffUtil.ItemCallback<Stall>() {
        @Override
        public boolean areItemsTheSame(@NonNull Stall oldItem, @NonNull Stall newItem) {
            return oldItem.getStallId().equals(newItem.getStallId());
        }

        @Override
        public boolean areContentsTheSame(@NonNull Stall oldItem, @NonNull Stall newItem) {
            return oldItem.getStallName().equals(newItem.getStallName()) &&
                    oldItem.getRating() == newItem.getRating() &&
                    oldItem.isOpen() == newItem.isOpen() && // Added missing check from original code?
                    oldItem.isFavorite() == newItem.isFavorite(); // Also check favorite status
        }
    };

    @NonNull
    @Override
    public StallViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_food_stall, parent, false);
        return new StallViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull StallViewHolder holder, int position) {
        holder.bind(getItem(position), listener);
    }

    static class StallViewHolder extends RecyclerView.ViewHolder {
        TextView initials, name, rating, status, timings;

        public StallViewHolder(@NonNull View itemView) {
            super(itemView);
            initials = itemView.findViewById(R.id.stallInitials);
            name = itemView.findViewById(R.id.stallName);
            rating = itemView.findViewById(R.id.stallRating);
            status = itemView.findViewById(R.id.openStatus);
            timings = itemView.findViewById(R.id.stallTimings);
        }

        public void bind(final Stall stall, final OnItemClickListener listener) {
            name.setText(stall.getStallName());
            rating.setText(String.format(Locale.US, "%.1f", stall.getRating()));
            initials.setText(TextDrawableUtil.getInitials(stall.getStallName()));
            try {
                GradientDrawable bg = (GradientDrawable) initials.getBackground().mutate();
                bg.setColor(TextDrawableUtil.getColor(stall.getStallName()));
            } catch (Exception e) {
                // Fails silently
            }

            if (stall.isOpen()) {
                status.setText("Open Now");
                status.setTextColor(ContextCompat.getColor(itemView.getContext(), R.color.green));
                status.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
                itemView.setAlpha(1.0f);
                // This click listener correctly calls the interface method
                itemView.setOnClickListener(v -> {
                    if (listener != null) listener.onItemClick(stall);
                });
            } else {
                status.setText("Closed");
                status.setTextColor(Color.RED);
                status.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_profile_lock, 0, 0, 0);
                status.setCompoundDrawablePadding(8);
                itemView.setAlpha(0.6f);
                itemView.setOnClickListener(null); // Keep it unclickable if closed
            }

            String opening = stall.getOpeningHours();
            String closing = stall.getClosingHours();
            if (opening != null && !opening.isEmpty() && closing != null && !closing.isEmpty()) {
                timings.setText(String.format("%s - %s", opening, closing));
                timings.setVisibility(View.VISIBLE);
            } else {
                timings.setVisibility(View.GONE);
            }
        }
    }
}