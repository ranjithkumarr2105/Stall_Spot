package com.simats.foodstall.adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.simats.foodstall.fragment.ApprovedOrdersFragment;
import com.simats.foodstall.fragment.CancelledOrdersFragment;

public class OrdersPagerAdapter extends FragmentStateAdapter {

    public OrdersPagerAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        if (position == 1) {
            return new CancelledOrdersFragment();
        }
        return new ApprovedOrdersFragment();
    }

    @Override
    public int getItemCount() {
        return 2; // We have two tabs: Approved and Cancelled
    }
}