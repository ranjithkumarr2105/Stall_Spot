package com.simats.foodstall.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.simats.foodstall.R;
import com.simats.foodstall.model.FaqItem;

import java.util.List;

public class FaqAdapter extends RecyclerView.Adapter<FaqAdapter.FaqViewHolder> {

    private List<FaqItem> faqItemList;

    public FaqAdapter(List<FaqItem> faqItemList) {
        this.faqItemList = faqItemList;
    }

    @NonNull
    @Override
    public FaqViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_faq, parent, false);
        return new FaqViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FaqViewHolder holder, int position) {
        FaqItem item = faqItemList.get(position);
        holder.questionTextView.setText(item.getQuestion());
        holder.answerTextView.setText(item.getAnswer());

        // Set visibility and arrow rotation based on the expanded state
        boolean isExpanded = item.isExpanded();
        holder.answerTextView.setVisibility(isExpanded ? View.VISIBLE : View.GONE);
        holder.arrowImageView.setRotation(isExpanded ? 180f : 0f);

        // Handle click to expand/collapse
        holder.itemView.setOnClickListener(v -> {
            item.setExpanded(!item.isExpanded());
            notifyItemChanged(position);
        });
    }

    @Override
    public int getItemCount() {
        return faqItemList.size();
    }

    // Method for the search functionality
    public void filterList(List<FaqItem> filteredList) {
        this.faqItemList = filteredList;
        notifyDataSetChanged();
    }

    static class FaqViewHolder extends RecyclerView.ViewHolder {
        TextView questionTextView, answerTextView;
        ImageView arrowImageView;

        public FaqViewHolder(@NonNull View itemView) {
            super(itemView);
            questionTextView = itemView.findViewById(R.id.questionTextView);
            answerTextView = itemView.findViewById(R.id.answerTextView);
            arrowImageView = itemView.findViewById(R.id.arrowImageView);
        }
    }
}