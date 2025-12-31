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

public class Ufrequent_questionActivity extends AppCompatActivity {

    private RecyclerView faqRecyclerView;
    private FaqAdapter adapter;
    private List<FaqItem> faqList = new ArrayList<>();
    private EditText searchEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ufrequent_question);

        findViewById(R.id.backButton).setOnClickListener(v -> onBackPressed());

        faqRecyclerView = findViewById(R.id.faqRecyclerView);
        searchEditText = findViewById(R.id.searchEditText);
        faqRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Load the user-specific questions and answers
        loadUserFaqData();

        // Setup the adapter and recycler view
        // We pass a copy of the list to the adapter for filtering
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


    private void loadUserFaqData() {
        // Here you would fetch your FAQs from a server or a local resource.
        // This dummy data is tailored for the user/student.
        faqList.clear();

        // Ordering Questions
        faqList.add(new FaqItem("How do I place an order?", "Simply browse the stalls or dishes, tap on an item to view its details, and click the 'Add to Cart' button. Once you're ready, proceed to checkout from your cart."));
        faqList.add(new FaqItem("Can I schedule an order for later?", "Yes! When adding an item to your cart, you will see an option for 'Pre-Parcel'. Select this and choose your desired pickup time."));

        // Payment Questions
        faqList.add(new FaqItem("What payment methods are accepted?", "We support a variety of payment methods, including UPI (GPay, PhonePe, etc.), credit/debit cards, and in some cases, Cash on Delivery (COD). Payment options may vary by stall."));

        // Order Status & History
        faqList.add(new FaqItem("How can I track my order status?", "You can view the real-time status of your current and past orders by tapping the 'Orders' tab in the bottom navigation bar."));
        faqList.add(new FaqItem("Can I cancel my order?", "You can cancel an order only before the stall owner has approved it. Go to your 'Orders' page, find the pending order, and you will see a 'Cancel' option if it is still available."));

        // Account & Preferences
        faqList.add(new FaqItem("How do I save a favorite stall?", "On any stall's menu page, you will find a heart icon near the stall name. Tap it to add the stall to your favorites list for quick access later."));
        faqList.add(new FaqItem("Why does the app need my location?", "We use your location to show you the stalls that are nearest to you, provide accurate distance information, and estimate walk times, helping you make the best choice."));
    }
}
