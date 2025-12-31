package com.simats.foodstall.adapter;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import com.simats.foodstall.R;
import com.simats.foodstall.TextDrawableUtil;
import com.simats.foodstall.model.AnalysisData;

import java.util.List;
import java.util.Locale;

public class AnalysisAdapter extends RecyclerView.Adapter<AnalysisAdapter.AnalysisViewHolder> {

    private final List<AnalysisData.ComparisonItem> comparisonList;

    public AnalysisAdapter(List<AnalysisData.ComparisonItem> comparisonList) {
        this.comparisonList = comparisonList;
    }

    @SuppressLint("NotifyDataSetChanged")
    public void updateData(List<AnalysisData.ComparisonItem> newList) {
        comparisonList.clear();
        if (newList != null) {
            comparisonList.addAll(newList);
        }
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public AnalysisViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_analysis_item, parent, false);
        return new AnalysisViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AnalysisViewHolder holder, int position) {
        AnalysisData.ComparisonItem item = comparisonList.get(position);
        holder.bind(item);
    }

    @Override
    public int getItemCount() {
        return comparisonList.size();
    }

    static class AnalysisViewHolder extends RecyclerView.ViewHolder {
        ImageView stallIcon;
        TextView stallName, currentMonthRevenue, previousMonthRevenue, percentageChange;
        ImageView trendIcon;

        public AnalysisViewHolder(@NonNull View itemView) {
            super(itemView);
            stallIcon = itemView.findViewById(R.id.stallIcon);
            stallName = itemView.findViewById(R.id.stallNameTextView);
            currentMonthRevenue = itemView.findViewById(R.id.currentMonthRevenueTextView);
            previousMonthRevenue = itemView.findViewById(R.id.previousMonthRevenueTextView);
            percentageChange = itemView.findViewById(R.id.percentageChangeTextView);
            trendIcon = itemView.findViewById(R.id.trendIcon);
        }

        void bind(AnalysisData.ComparisonItem item) {
            stallName.setText(item.getStallName());
            stallIcon.setImageDrawable(TextDrawableUtil.getInitialDrawable(item.getStallName()));
            currentMonthRevenue.setText(String.format(Locale.getDefault(), "₹%,.2f", item.getCurrentMonthRevenue()));
            previousMonthRevenue.setText(String.format(Locale.getDefault(), "Last Month: ₹%,.2f", item.getPreviousMonthRevenue()));

            double current = item.getCurrentMonthRevenue();
            double previous = item.getPreviousMonthRevenue();
            double change = 0;
            if (previous > 0) {
                change = ((current - previous) / previous) * 100;
            } else if (current > 0) {
                change = 100.0;
            }

            if (change > 0.1) {
                percentageChange.setText(String.format(Locale.getDefault(), "+%.1f%%", change));
                percentageChange.setTextColor(ContextCompat.getColor(itemView.getContext(), R.color.green));
                trendIcon.setImageResource(R.drawable.ic_trending_up);
                trendIcon.setColorFilter(ContextCompat.getColor(itemView.getContext(), R.color.green));
            } else if (change < -0.1) {
                percentageChange.setText(String.format(Locale.getDefault(), "%.1f%%", change));
                percentageChange.setTextColor(ContextCompat.getColor(itemView.getContext(), R.color.red));
                trendIcon.setImageResource(R.drawable.ic_trending_down);
                trendIcon.setColorFilter(ContextCompat.getColor(itemView.getContext(), R.color.red));
            } else {
                percentageChange.setText("0.0%");
                percentageChange.setTextColor(Color.GRAY);
                trendIcon.setImageResource(R.drawable.ic_trending_flat);
                trendIcon.setColorFilter(Color.GRAY);
            }
        }
    }
}