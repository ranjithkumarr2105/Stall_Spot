package com.simats.foodstall;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;

import com.simats.foodstall.adapter.FaqAdapter;
import com.simats.foodstall.model.FaqItem;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class Ofrequent_questionActivity extends AppCompatActivity {

    private RecyclerView faqRecyclerView;
    private FaqAdapter adapter;
    private List<FaqItem> faqList = new ArrayList<>();
    private EditText searchEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ofrequent_question);

        findViewById(R.id.backButton).setOnClickListener(v -> onBackPressed());

        faqRecyclerView = findViewById(R.id.faqRecyclerView);
        searchEditText = findViewById(R.id.searchEditText);
        faqRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Load the questions and answers
        loadFaqData();

        // Setup the adapter and recycler view
        adapter = new FaqAdapter(new ArrayList<>(faqList));
        faqRecyclerView.setAdapter(adapter);

        // Setup the search functionality
        setupSearch();
    }

    private void setupSearch() {
        searchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                filter(s.toString());
            }
        });
    }

    private void filter(String text) {
        List<FaqItem> filteredList = new ArrayList<>();
        for (FaqItem item : faqList) {
            // Search in both question and answer for the text
            if (item.getQuestion().toLowerCase(Locale.ROOT).contains(text.toLowerCase(Locale.ROOT)) ||
                    item.getAnswer().toLowerCase(Locale.ROOT).contains(text.toLowerCase(Locale.ROOT))) {
                filteredList.add(item);
            }
        }
        adapter.filterList(filteredList);
    }


    private void loadFaqData() {
        // Here you would fetch your FAQs from a server or a local resource.
        // For now, we use dummy data.
        faqList.clear();

        // Account Management Questions
        faqList.add(new FaqItem("How do I change my stall's name?", "You can change your stall's name and other details from the 'Profile' screen, accessible from the bottom navigation bar."));
        faqList.add(new FaqItem("How can I reset my password?", "To reset your password, go to the 'Settings' page, and select the 'Change Password' option."));

        // Order Management Questions
        faqList.add(new FaqItem("Where can I see my pending orders?", "All new orders that need your approval will appear on the 'Pending Orders' card on your home screen. Tap it to view the full list."));
        faqList.add(new FaqItem("What happens when I approve an order?", "When an order is approved, it is moved to the 'Approved' list in your 'Today's Orders' screen. The customer is also notified that their order is being prepared."));
        faqList.add(new FaqItem("How do I view my order history?", "You can view all past orders by tapping on the 'Orders' tab in the bottom navigation. This screen provides filters for different date ranges and statuses."));

        // Menu Management Questions
        faqList.add(new FaqItem("How do I update my menu?", "You can update your menu from the 'Menu' tab in the bottom navigation. Add or remove items, change prices, and set today's special."));
        faqList.add(new FaqItem("How do I set my stall as 'Not Working' for the day?", "On the 'Menu Management' screen, there is a 'Working Today?' switch. Simply turn it off to temporarily close your stall."));
    }
}