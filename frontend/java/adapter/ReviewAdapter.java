package com.simats.foodstall.adapter;

import android.graphics.drawable.GradientDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.RatingBar;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.simats.foodstall.R;
import com.simats.foodstall.TextDrawableUtil;
import com.simats.foodstall.model.Review;
import java.util.List;

public class ReviewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    public interface OnReviewInteractionListener {
        void onDeleteClick(Review review, int position);
    }

    private final List<Review> reviews;
    private String currentUserId;
    private OnReviewInteractionListener listener;

    private static final int VIEW_TYPE_USER_REVIEW = 1;
    private static final int VIEW_TYPE_OWNER_REVIEW = 2;

    /**
     * Constructor for USER-FACING reviews (UviewmenuActivity).
     * This version includes the user ID and listener for delete functionality.
     */
    public ReviewAdapter(List<Review> reviews, String currentUserId, OnReviewInteractionListener listener) {
        this.reviews = reviews;
        this.currentUserId = currentUserId;
        this.listener = listener;
    }

    /**
     * Constructor for OWNER-FACING reviews (OwnerReviewsFragment).
     * This is the constructor your fragment needs, which fixes the error.
     */
    public ReviewAdapter(List<Review> reviews) {
        this.reviews = reviews;
        this.currentUserId = null;
        this.listener = null;
    }

    public void removeItem(int position) {
        reviews.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, reviews.size());
    }

    public void updateList(List<Review> newReviews) {
        this.reviews.clear();
        this.reviews.addAll(newReviews);
        notifyDataSetChanged();
    }

    public void addReviewAtTop(Review newReview) {
        this.reviews.add(0, newReview);
        notifyItemInserted(0);
    }

    @Override
    public int getItemViewType(int position) {
        // If the listener is null, we know it's the owner's view.
        if (listener == null) {
            return VIEW_TYPE_OWNER_REVIEW;
        } else {
            return VIEW_TYPE_USER_REVIEW;
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == VIEW_TYPE_OWNER_REVIEW) {
            // NOTE: This assumes you have a layout named 'item_review_card.xml' for the owner's side.
            // If your layout has a different name, please change it here.
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_review_card, parent, false);
            return new OwnerReviewViewHolder(view);
        } else {
            // This uses the professional layout we created for the user's view.
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_customer_review, parent, false);
            return new UserReviewViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Review review = reviews.get(position);
        if (holder.getItemViewType() == VIEW_TYPE_OWNER_REVIEW) {
            ((OwnerReviewViewHolder) holder).bind(review);
        } else {
            ((UserReviewViewHolder) holder).bind(review);
        }
    }

    @Override
    public int getItemCount() {
        return reviews.size();
    }

    // ViewHolder for the Owner's simple review list
    public static class OwnerReviewViewHolder extends RecyclerView.ViewHolder {
        TextView stallName, reviewerName, reviewContent, reviewDate;
        RatingBar ratingBar;

        public OwnerReviewViewHolder(@NonNull View itemView) {
            super(itemView);
            stallName = itemView.findViewById(R.id.stallNameTextView);
            reviewerName = itemView.findViewById(R.id.reviewerNameTextView);
            reviewContent = itemView.findViewById(R.id.reviewContentTextView);
            reviewDate = itemView.findViewById(R.id.reviewDateTextView);
            ratingBar = itemView.findViewById(R.id.ratingBar);
        }

        void bind(Review review) {
            stallName.setText(review.getStallName());
            reviewerName.setText(review.getReviewerName());
            reviewContent.setText(review.getReviewText());
            reviewDate.setText(review.getReviewDate());
            ratingBar.setRating(review.getRating());
        }
    }

    // ViewHolder for the User's professional review list (with delete button)
    public class UserReviewViewHolder extends RecyclerView.ViewHolder {
        TextView initials, name, date, text;
        RatingBar ratingBar;
        ImageButton deleteButton;

        public UserReviewViewHolder(@NonNull View itemView) {
            super(itemView);
            initials = itemView.findViewById(R.id.reviewerInitials);
            name = itemView.findViewById(R.id.reviewerName);
            date = itemView.findViewById(R.id.reviewDate);
            text = itemView.findViewById(R.id.reviewText);
            ratingBar = itemView.findViewById(R.id.reviewRatingBar);
            deleteButton = itemView.findViewById(R.id.deleteReviewButton);
        }

        public void bind(final Review review) {
            name.setText(review.getReviewerName());
            text.setText(review.getReviewText());
            ratingBar.setRating(review.getRating());
            initials.setText(TextDrawableUtil.getInitials(review.getReviewerName()));

            GradientDrawable bg = (GradientDrawable) initials.getBackground().mutate();
            bg.setColor(TextDrawableUtil.getColor(review.getReviewerName()));

            if (review.getReviewDate() != null && review.getReviewDate().length() >= 10) {
                date.setText(review.getReviewDate().substring(0, 10));
            } else {
                date.setText(review.getReviewDate());
            }

            if (currentUserId != null && currentUserId.equals(review.getStudentId())) {
                deleteButton.setVisibility(View.VISIBLE);
                deleteButton.setOnClickListener(v -> {
                    if (listener != null) {
                        listener.onDeleteClick(review, getAdapterPosition());
                    }
                });
            } else {
                deleteButton.setVisibility(View.GONE);
                deleteButton.setOnClickListener(null);
            }
        }
    }
}