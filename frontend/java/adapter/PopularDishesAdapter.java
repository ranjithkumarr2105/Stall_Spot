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
import de.hdodenhof.circleimageview.CircleImageView;

public class PopularDishesAdapter extends ListAdapter<HomeDataResponse.PopularDish, PopularDishesAdapter.PopularViewHolder> {

    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(HomeDataResponse.PopularDish dish); // Interface remains the same
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    public PopularDishesAdapter() {
        super(DIFF_CALLBACK);
    }

    private static final DiffUtil.ItemCallback<HomeDataResponse.PopularDish> DIFF_CALLBACK = new DiffUtil.ItemCallback<HomeDataResponse.PopularDish>() {
        @Override
        public boolean areItemsTheSame(@NonNull HomeDataResponse.PopularDish oldItem, @NonNull HomeDataResponse.PopularDish newItem) {
            // Assuming dishName + stallId is unique enough
            return Objects.equals(oldItem.getStallId(), newItem.getStallId()) &&
                    Objects.equals(oldItem.getDishName(), newItem.getDishName());
        }

        @Override
        public boolean areContentsTheSame(@NonNull HomeDataResponse.PopularDish oldItem, @NonNull HomeDataResponse.PopularDish newItem) {
            // Compare all relevant fields
            return Objects.equals(oldItem.getDishName(), newItem.getDishName()) &&
                    Objects.equals(oldItem.getStallName(), newItem.getStallName()) &&
                    oldItem.getPrice() == newItem.getPrice() &&
                    Objects.equals(oldItem.getImageUrl(), newItem.getImageUrl()) &&
                    oldItem.isStallOpen() == newItem.isStallOpen();
        }
    };

    @NonNull
    @Override
    public PopularViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_popular_dish, parent, false);
        return new PopularViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PopularViewHolder holder, int position) {
        holder.bind(getItem(position), listener);
    }

    static class PopularViewHolder extends RecyclerView.ViewHolder {
        TextView dishName, stallName, price, initials;
        CircleImageView dishImage;
        ImageView lockIcon;

        public PopularViewHolder(@NonNull View itemView) {
            super(itemView);
            dishName = itemView.findViewById(R.id.popularDishName);
            stallName = itemView.findViewById(R.id.popularStallName);
            price = itemView.findViewById(R.id.popularDishPrice);
            initials = itemView.findViewById(R.id.popularDishInitials); // Assuming you have this ID
            dishImage = itemView.findViewById(R.id.popularDishImage);
            lockIcon = itemView.findViewById(R.id.lockIcon); // Assuming you added this ID
        }

        public void bind(final HomeDataResponse.PopularDish dish, final OnItemClickListener listener) {
            dishName.setText(dish.getDishName());
            stallName.setText(dish.getStallName());
            price.setText(String.format(Locale.getDefault(), "₹%.2f", dish.getPrice()));

            String imageUrl = dish.getImageUrl();
            if (imageUrl != null && !imageUrl.isEmpty()) {
                if(initials != null) initials.setVisibility(View.GONE);
                dishImage.setVisibility(View.VISIBLE);
                String fullUrl = ApiClient.BASE_URL + "uploads/" + imageUrl;
                Glide.with(itemView.getContext()).load(fullUrl).placeholder(R.drawable.editprofile).error(R.drawable.editprofile).into(dishImage);
            } else {
                dishImage.setVisibility(View.GONE);
                if(initials != null) {
                    initials.setVisibility(View.VISIBLE);
                    initials.setText(TextDrawableUtil.getInitials(dish.getDishName()));
                    try {
                        GradientDrawable background = (GradientDrawable) initials.getBackground().mutate();
                        background.setColor(TextDrawableUtil.getColor(dish.getDishName()));
                    } catch (Exception e) { /* Fails silently */ }
                }
            }

            if (dish.isStallOpen()) {
                if(lockIcon != null) lockIcon.setVisibility(View.GONE);
                itemView.setAlpha(1.0f);
                // This click listener correctly calls the interface method
                itemView.setOnClickListener(v -> {
                    if(listener != null) listener.onItemClick(dish);
                });
            } else {
                if(lockIcon != null) lockIcon.setVisibility(View.VISIBLE);
                itemView.setAlpha(0.6f);
                itemView.setOnClickListener(null); // Disable click if closed
            }
        }
    }
}