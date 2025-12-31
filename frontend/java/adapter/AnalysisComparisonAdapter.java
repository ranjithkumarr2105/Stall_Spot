package com.simats.foodstall.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.simats.foodstall.R;
import com.simats.foodstall.model.AnalysisComparison;
import java.util.List;

public class AnalysisComparisonAdapter extends RecyclerView.Adapter<AnalysisComparisonAdapter.AnalysisViewHolder> {
    private final List<AnalysisComparison> comparisonList;

    public AnalysisComparisonAdapter(List<AnalysisComparison> comparisonList) {
        this.comparisonList = comparisonList;
    }

    @NonNull
    @Override
    public AnalysisViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.card_analysis_comparison, parent, false);
        return new AnalysisViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AnalysisViewHolder holder, int position) {
        AnalysisComparison data = comparisonList.get(position);
        holder.stallName.setText(data.stallName);
        holder.pastMonth.setText(data.pastMonth);
        holder.presentMonth.setText(data.presentMonth);
        holder.percentageChange.setText(data.percentageChange + "%");

        // Change color based on performance change
        if (data.percentageChange >= 0) {
            holder.percentageChange.setTextColor(holder.itemView.getResources().getColor(android.R.color.holo_green_dark));
        } else {
            holder.percentageChange.setTextColor(holder.itemView.getResources().getColor(android.R.color.holo_red_dark));
        }
    }

    @Override
    public int getItemCount() {
        return comparisonList.size();
    }

    public static class AnalysisViewHolder extends RecyclerView.ViewHolder {
        public TextView stallName, pastMonth, presentMonth, percentageChange;

        public AnalysisViewHolder(@NonNull View itemView) {
            super(itemView);
            stallName = itemView.findViewById(R.id.stallNameTextView);
            pastMonth = itemView.findViewById(R.id.pastMonthTextView);
            presentMonth = itemView.findViewById(R.id.presentMonthTextView);
            percentageChange = itemView.findViewById(R.id.percentageChangeTextView);
        }
    }
}
