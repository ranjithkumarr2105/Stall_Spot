package com.simats.foodstall.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.simats.foodstall.R;
import com.simats.foodstall.TextDrawableUtil;
import com.simats.foodstall.model.FavoriteStall;
import java.util.ArrayList;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;

public class FavoriteStallsAdapter extends RecyclerView.Adapter<FavoriteStallsAdapter.FavoriteViewHolder> {

    // Listener to handle when the remove (heart) icon is clicked
    public interface OnFavoriteRemoveClickListener {
        void onRemoveClick(FavoriteStall stall, int position);
    }

    private final ArrayList<FavoriteStall> favoriteStalls;
    private final OnFavoriteRemoveClickListener listener;

    public FavoriteStallsAdapter(ArrayList<FavoriteStall> favoriteStalls, OnFavoriteRemoveClickListener listener) {
        this.favoriteStalls = favoriteStalls;
        this.listener = listener;
    }

    @NonNull
    @Override
    public FavoriteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // NOTE: Make sure you have a layout file named 'card_favorite_stall.xml'
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_favorite_stall, parent, false);
        return new FavoriteViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FavoriteViewHolder holder, int position) {
        holder.bind(favoriteStalls.get(position));
    }

    @Override
    public int getItemCount() {
        return favoriteStalls.size();
    }

    class FavoriteViewHolder extends RecyclerView.ViewHolder {
        CircleImageView stallImageView;
        TextView stallNameTextView, stallRatingTextView;
        ImageView removeFavoriteButton;

        public FavoriteViewHolder(@NonNull View itemView) {
            super(itemView);
            // NOTE: Ensure these IDs match your 'card_favorite_stall.xml' layout
            stallImageView = itemView.findViewById(R.id.favoriteStallImageView);
            stallNameTextView = itemView.findViewById(R.id.favoriteStallNameTextView);
            stallRatingTextView = itemView.findViewById(R.id.favoriteStallRatingTextView);
            removeFavoriteButton = itemView.findViewById(R.id.removeFavoriteButton);
        }

        void bind(final FavoriteStall stall) {
            stallNameTextView.setText(stall.getName());
            stallRatingTextView.setText(String.format(Locale.getDefault(), "%.1f ★", stall.getRating()));

            // --- START OF UPDATED LOGIC ---
            // This now handles both real images and initials, fixing the error.
            String imageUrl = stall.getImageUrl();
            if (imageUrl != null && !imageUrl.isEmpty()) {
                // TODO: Add Glide/Picasso here to load the real image from the URL
                // e.g., Glide.with(itemView.getContext()).load(BASE_URL + "uploads/" + imageUrl).into(stallImageView);
                stallImageView.setImageResource(R.drawable.ic_sample_dish); // Placeholder
            } else {
                // If no image is available, create and set the initials drawable
                stallImageView.setImageDrawable(TextDrawableUtil.getInitialDrawable(stall.getName()));
            }
            // --- END OF UPDATED LOGIC ---

            removeFavoriteButton.setOnClickListener(v -> {
                if (listener != null) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        listener.onRemoveClick(favoriteStalls.get(position), position);
                    }
                }
            });
        }
    }
}