package com.simats.foodstall.adapter;

import android.graphics.drawable.GradientDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.simats.foodstall.ApiClient;
import com.simats.foodstall.R;
import com.simats.foodstall.TextDrawableUtil;
import com.simats.foodstall.model.HomeDataResponse;
import java.util.Locale;
import java.util.Objects;

public class SpecialsAdapter extends ListAdapter<HomeDataResponse.SpecialDish, SpecialsAdapter.SpecialViewHolder> {

    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(HomeDataResponse.SpecialDish dish); // Interface remains the same
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    public SpecialsAdapter() {
        super(DIFF_CALLBACK);
    }

    private static final DiffUtil.ItemCallback<HomeDataResponse.SpecialDish> DIFF_CALLBACK = new DiffUtil.ItemCallback<HomeDataResponse.SpecialDish>() {
        @Override
        public boolean areItemsTheSame(@NonNull HomeDataResponse.SpecialDish oldItem, @NonNull HomeDataResponse.SpecialDish newItem) {
            // Assuming dishName + stallId is unique enough for an item check
            return Objects.equals(oldItem.getStallId(), newItem.getStallId()) &&
                    Objects.equals(oldItem.getDishName(), newItem.getDishName());
        }

        @Override
        public boolean areContentsTheSame(@NonNull HomeDataResponse.SpecialDish oldItem, @NonNull HomeDataResponse.SpecialDish newItem) {
            // Compare all relevant fields for content change detection
            return Objects.equals(oldItem.getDishName(), newItem.getDishName()) &&
                    Objects.equals(oldItem.getStallName(), newItem.getStallName()) &&
                    oldItem.getPrice() == newItem.getPrice() &&
                    Objects.equals(oldItem.getImageUrl(), newItem.getImageUrl()) &&
                    oldItem.isStallOpen() == newItem.isStallOpen();
        }
    };

    @NonNull
    @Override
    public SpecialViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_special_item, parent, false);
        return new SpecialViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SpecialViewHolder holder, int position) {
        HomeDataResponse.SpecialDish currentDish = getItem(position);
        if (currentDish != null) {
            holder.bind(currentDish, listener);
        }
    }

    static class SpecialViewHolder extends RecyclerView.ViewHolder {
        ImageView dishImageView;
        TextView dishInitialsTextView, dishNameTextView, stallNameTextView, priceTextView;
        ImageView lockIcon;

        public SpecialViewHolder(@NonNull View itemView) {
            super(itemView);
            dishImageView = itemView.findViewById(R.id.specialDishImage);
            dishInitialsTextView = itemView.findViewById(R.id.specialDishInitials); // Assuming you have this ID
            dishNameTextView = itemView.findViewById(R.id.specialDishName);
            stallNameTextView = itemView.findViewById(R.id.specialStallName);
            priceTextView = itemView.findViewById(R.id.specialDishPrice);
            lockIcon = itemView.findViewById(R.id.lockIcon); // Assuming you added this ID to your XML
        }

        public void bind(final HomeDataResponse.SpecialDish dish, final OnItemClickListener listener) {
            dishNameTextView.setText(dish.getDishName());
            stallNameTextView.setText(dish.getStallName());
            priceTextView.setText(String.format(Locale.getDefault(), "₹%.2f", dish.getPrice()));

            String imageUrl = dish.getImageUrl();
            if (imageUrl != null && !imageUrl.isEmpty()) {
                if(dishInitialsTextView != null) dishInitialsTextView.setVisibility(View.GONE);
                dishImageView.setVisibility(View.VISIBLE);
                String fullUrl = ApiClient.BASE_URL + "uploads/" + imageUrl;
                Glide.with(itemView.getContext()).load(fullUrl).placeholder(R.drawable.editprofile).error(R.drawable.editprofile).into(dishImageView);
            } else {
                dishImageView.setVisibility(View.GONE); // Hide image view if no URL
                if(dishInitialsTextView != null) {
                    dishInitialsTextView.setVisibility(View.VISIBLE);
                    dishInitialsTextView.setText(TextDrawableUtil.getInitials(dish.getDishName()));
                    try {
                        GradientDrawable bg = (GradientDrawable) dishInitialsTextView.getBackground().mutate();
                        bg.setColor(TextDrawableUtil.getColor(dish.getDishName()));
                    } catch (Exception e) {/* Fails silently */}
                }
            }

            if (dish.isStallOpen()) {
                if(lockIcon != null) lockIcon.setVisibility(View.GONE);
                itemView.setAlpha(1.0f);
                // This click listener correctly calls the interface method
                itemView.setOnClickListener(v -> {
                    if (listener != null) {
                        listener.onItemClick(dish);
                    }
                });
            } else {
                if(lockIcon != null) lockIcon.setVisibility(View.VISIBLE);
                itemView.setAlpha(0.6f);
                itemView.setOnClickListener(null); // Disable click if closed
            }
        }
    }
}