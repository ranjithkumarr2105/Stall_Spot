package com.simats.foodstall;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.viewpager2.widget.ViewPager2;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.simats.foodstall.adapter.OrderStatusAdapter;
import com.simats.foodstall.adapter.OrdersPagerAdapter;
import com.simats.foodstall.model.OrderStatus;

public class Otoday_ordersActivity extends AppCompatActivity implements OrderStatusAdapter.OnReceiptClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.otoday_orders);

        ImageView backButton = findViewById(R.id.backButton);
        backButton.setOnClickListener(v -> onBackPressed());

        TabLayout tabLayout = findViewById(R.id.tabLayout);
        ViewPager2 viewPager = findViewById(R.id.viewPager);

        OrdersPagerAdapter pagerAdapter = new OrdersPagerAdapter(this);
        viewPager.setAdapter(pagerAdapter);

        new TabLayoutMediator(tabLayout, viewPager, (tab, position) -> {
            if (position == 0) {
                tab.setText("Approved");
            } else {
                tab.setText("Cancelled");
            }
        }).attach();

        SearchView searchView = findViewById(R.id.searchView);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                // ... (search logic is unchanged)
                return true;
            }
        });
    }

    // This method is called when the "View Receipt" button is clicked in either fragment
    @Override
    public void onViewReceiptClick(OrderStatus order) {
        Intent intent = new Intent(this, Oview_receiptActivity.class);
        // You can pass the order details to the receipt screen here
        // intent.putExtra("ORDER_DETAILS", order);
        startActivity(intent);
    }
}