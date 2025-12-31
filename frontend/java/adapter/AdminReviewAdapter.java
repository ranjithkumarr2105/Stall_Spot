package com.simats.foodstall.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.simats.foodstall.R;
import com.simats.foodstall.model.AdminReview;
import java.util.List;

public class AdminReviewAdapter extends RecyclerView.Adapter<AdminReviewAdapter.ReviewViewHolder> {
    private final List<AdminReview> reviewsList;

    public AdminReviewAdapter(List<AdminReview> reviewsList) {
        this.reviewsList = reviewsList;
    }

    @NonNull
    @Override
    public ReviewViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.card_admin_review, parent, false);
        return new ReviewViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ReviewViewHolder holder, int position) {
        AdminReview review = reviewsList.get(position);
        holder.stallName.setText(review.stallName);
        holder.ownerName.setText(review.ownerName);
        holder.date.setText(review.date);
        holder.message.setText(review.message);

        // Dynamically set the star rating
        for (int i = 0; i < holder.stars.length; i++) {
            if (i < review.rating) {
                holder.stars[i].setVisibility(View.VISIBLE);
            } else {
                holder.stars[i].setVisibility(View.GONE);
            }
        }
    }

    @Override
    public int getItemCount() {
        return reviewsList.size();
    }

    public static class ReviewViewHolder extends RecyclerView.ViewHolder {
        TextView stallName, ownerName, date, message;
        ImageView[] stars;

        public ReviewViewHolder(@NonNull View itemView) {
            super(itemView);
            stallName = itemView.findViewById(R.id.stallNameTextView);
            ownerName = itemView.findViewById(R.id.ownerNameTextView);
            date = itemView.findViewById(R.id.reviewDateTextView);
            message = itemView.findViewById(R.id.reviewMessageTextView);

            // Find all the star ImageViews
            stars = new ImageView[5];
            stars[0] = itemView.findViewById(R.id.star1);
            stars[1] = itemView.findViewById(R.id.star2);
            stars[2] = itemView.findViewById(R.id.star3);
            stars[3] = itemView.findViewById(R.id.star4);
            stars[4] = itemView.findViewById(R.id.star5);
        }
    }
}