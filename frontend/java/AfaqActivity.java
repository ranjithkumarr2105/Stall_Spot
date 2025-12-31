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

public class AfaqActivity extends AppCompatActivity {

    private RecyclerView faqRecyclerView;
    private FaqAdapter adapter;
    private List<FaqItem> faqList = new ArrayList<>();
    private EditText searchEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.afaq);

        findViewById(R.id.backButton).setOnClickListener(v -> onBackPressed());

        faqRecyclerView = findViewById(R.id.faqRecyclerView);
        searchEditText = findViewById(R.id.searchEditText);
        faqRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Load the Admin-specific questions and answers
        loadAdminFaqData();

        // Setup the adapter (reusing the same adapter as other FAQ screens)
        adapter = new FaqAdapter(new ArrayList<>(faqList));
        faqRecyclerView.setAdapter(adapter);

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
            if (item.getQuestion().toLowerCase(Locale.ROOT).contains(text.toLowerCase(Locale.ROOT)) ||
                    item.getAnswer().toLowerCase(Locale.ROOT).contains(text.toLowerCase(Locale.ROOT))) {
                filteredList.add(item);
            }
        }
        adapter.filterList(filteredList);
    }

    // This method contains questions and answers relevant to an Admin
    private void loadAdminFaqData() {
        faqList.clear();
        faqList.add(new FaqItem("How do I approve a new stall?", "Navigate to the 'New Stalls' tab on the home screen. Tap on a pending application to view its details. You can then choose to approve or reject the application."));
        faqList.add(new FaqItem("Where can I see performance analytics?", "The 'Analysis' section in Settings provides a detailed breakdown of revenue, top-selling items, and peak business hours across all stalls."));
        faqList.add(new FaqItem("How do I edit a stall's details?", "From the 'Settings' page, select 'Edit Stall Details'. You can search for a stall by its name or ID and update its information."));
        faqList.add(new FaqItem("Can I set a stall's location manually?", "Yes. From the 'Edit Stall Details' screen, there is an option to 'Set Location'. You can fetch the location via GPS or enter the coordinates manually for precision."));
        faqList.add(new FaqItem("How do I handle user reviews and complaints?", "The 'Reviews' tab on the home screen shows all user-submitted reviews. You can monitor feedback and address any issues reported by users."));
    }
}