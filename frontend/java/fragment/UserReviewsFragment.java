package com.simats.foodstall.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.simats.foodstall.R;
import com.simats.foodstall.adapter.ReviewAdapter;
import com.simats.foodstall.model.Review;
import java.util.ArrayList;
import java.util.List;

public class UserReviewsFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_review_list, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        RecyclerView recyclerView = view.findViewById(R.id.reviewsRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        // Get the list of user reviews
        List<Review> userReviews = getDummyUserReviews();

        // The same flexible ReviewAdapter is used here
        ReviewAdapter adapter = new ReviewAdapter(userReviews);
        recyclerView.setAdapter(adapter);
    }

    /**
     * Creates sample data for User Reviews using the NEW constructor.
     * Constructor signature: (String stallName, String name, float rating, String text, String date)
     */
    private List<Review> getDummyUserReviews() {
        List<Review> reviews = new ArrayList<>();
        reviews.add(new Review("Pizza Palace", "Alice", 4.0f, "The pizza was great, but the delivery took a bit longer than expected. Overall, a good experience.", "2024-01-12"));
        reviews.add(new Review("Taco Town", "Bob", 5.0f, "Best tacos in the area! The staff is always friendly and the food is consistently delicious. Highly recommend.", "2024-01-11"));
        reviews.add(new Review("Burger Barn", "Charlie", 3.0f, "The burger was decent, but the fries were a bit soggy. It's an okay option if you're in a hurry.", "2024-01-10"));
        return reviews;
    }
}