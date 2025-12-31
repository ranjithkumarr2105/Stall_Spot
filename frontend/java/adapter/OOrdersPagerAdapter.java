package com.simats.foodstall.adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.simats.foodstall.fragment.TApprovedOrdersFragment;
import com.simats.foodstall.fragment.TRejectedOrdersFragment;

public class OOrdersPagerAdapter extends FragmentStateAdapter {
    public OOrdersPagerAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        if (position == 0) {
            // UPDATED to use your new fragment name
            return new TApprovedOrdersFragment();
        } else {
            // UPDATED to use your new fragment name
            return new TRejectedOrdersFragment();
        }
    }

    @Override
    public int getItemCount() {
        return 2; // We have 2 tabs: Approved and Rejected
    }
}