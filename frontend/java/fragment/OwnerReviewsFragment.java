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

public class OwnerReviewsFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate the layout for this fragment, which is just a RecyclerView
        return inflater.inflate(R.layout.fragment_review_list, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        RecyclerView recyclerView = view.findViewById(R.id.reviewsRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        // Get the list of owner reviews using the updated helper method
        List<Review> ownerReviews = getDummyOwnerReviews();

        // The flexible ReviewAdapter will handle the rest
        ReviewAdapter adapter = new ReviewAdapter(ownerReviews);
        recyclerView.setAdapter(adapter);
    }

    /**
     * Creates sample data for Owner Reviews using the NEW constructor.
     * Constructor signature: (String stallName, String name, float rating, String text, String date)
     */
    private List<Review> getDummyOwnerReviews() {
        List<Review> reviews = new ArrayList<>();
        reviews.add(new Review("Golden Wok", "John Smith", 5.0f, "Excellent service and cleanliness maintained throughout the semester. Students consistently provide positive feedback about our food quality and service.", "2024-01-15"));
        reviews.add(new Review("Coffee Haven", "Sarah Johnson", 4.0f, "Great improvement in customer service this semester. We've implemented a new queue management system which has helped reduce waiting times significantly.", "2024-01-14"));
        reviews.add(new Review("Fresh Bites", "Mike Wilson", 4.5f, "Proud to maintain high standards of food safety and quality. Our new menu items have been well received by students.", "2024-01-13"));
        return reviews;
    }
}