package com.simats.foodstall.adapter;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.simats.foodstall.R;
import com.simats.foodstall.model.RevenueDetail;
import java.util.List;
import java.util.Locale;

public class RevenueDetailAdapter extends RecyclerView.Adapter<RevenueDetailAdapter.RevenueViewHolder> {
    private List<RevenueDetail> revenueDetailsList;

    public RevenueDetailAdapter(List<RevenueDetail> revenueDetailsList) {
        this.revenueDetailsList = revenueDetailsList;
    }

    @NonNull
    @Override
    public RevenueViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.card_revenue_detail_item, parent, false);
        return new RevenueViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RevenueViewHolder holder, int position) {
        RevenueDetail detail = revenueDetailsList.get(position);
        holder.date.setText(detail.getDate());
        holder.orders.setText(String.valueOf(detail.getOrders()));
        // Format the revenue as currency
        holder.revenue.setText(String.format(Locale.getDefault(), "₹%.2f", detail.getRevenue()));
    }

    @Override
    public int getItemCount() {
        return revenueDetailsList.size();
    }

    @SuppressLint("NotifyDataSetChanged")
    public void updateList(List<RevenueDetail> newList) {
        this.revenueDetailsList = newList;
        notifyDataSetChanged();
    }

    public static class RevenueViewHolder extends RecyclerView.ViewHolder {
        public TextView date, orders, revenue;

        public RevenueViewHolder(@NonNull View itemView) {
            super(itemView);
            date = itemView.findViewById(R.id.dateTextView);
            orders = itemView.findViewById(R.id.ordersTextView);
            revenue = itemView.findViewById(R.id.revenueTextView);
        }
    }
}