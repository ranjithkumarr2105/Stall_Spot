    package com.simats.foodstall;

    import android.os.Bundle;

    import androidx.appcompat.app.AppCompatActivity;
    import androidx.viewpager2.widget.ViewPager2;
    import com.google.android.material.appbar.MaterialToolbar;
    import com.google.android.material.tabs.TabLayout;
    import com.google.android.material.tabs.TabLayoutMediator;
    import com.simats.foodstall.adapter.OOrdersPagerAdapter;

    public class OTordersActivity extends AppCompatActivity {
        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.otorders);

            MaterialToolbar toolbar = findViewById(R.id.toolbar);
            setSupportActionBar(toolbar);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            toolbar.setNavigationOnClickListener(v -> finish());

            ViewPager2 viewPager = findViewById(R.id.viewPager);
            TabLayout tabLayout = findViewById(R.id.tabLayout);

            OOrdersPagerAdapter pagerAdapter = new OOrdersPagerAdapter(this);
            viewPager.setAdapter(pagerAdapter);

            new TabLayoutMediator(tabLayout, viewPager,
                    (tab, position) -> {
                        if (position == 0) {
                            tab.setText("Approved");
                        } else {
                            tab.setText("Rejected");
                        }
                    }
            ).attach();
        }
    }