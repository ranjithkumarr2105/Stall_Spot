package com.simats.foodstall;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

// --- IMPORT ADDED for the correct model class and CircleImageView ---
import com.simats.foodstall.model.StallDetails;
import de.hdodenhof.circleimageview.CircleImageView;

import java.util.List;

public class StallDetailsAdapter extends RecyclerView.Adapter<StallDetailsAdapter.StallDetailsViewHolder> {

    // UPDATED: Now uses a List of the concrete StallDetails class
    private List<StallDetails> stallList;
    private OnStallDetailsClickListener listener;

    // UPDATED: The listener interface now correctly uses the StallDetails class
    public interface OnStallDetailsClickListener {
        void onViewClick(StallDetails stall);
    }

    public StallDetailsAdapter(List<StallDetails> stallList, OnStallDetailsClickListener listener) {
        this.stallList = stallList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public StallDetailsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_requested_stall, parent, false);
        return new StallDetailsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull StallDetailsViewHolder holder, int position) {
        // UPDATED: Gets a StallDetails object from the list
        StallDetails currentStall = stallList.get(position);
        holder.bind(currentStall, listener);
    }

    @Override
    public int getItemCount() {
        return stallList.size();
    }

    static class StallDetailsViewHolder extends RecyclerView.ViewHolder {
        private CircleImageView stallImageView;
        private TextView stallNameTextView, ownerNameTextView, requestedDateTextView;
        private Button viewButton;

        public StallDetailsViewHolder(@NonNull View itemView) {
            super(itemView);
            stallImageView = itemView.findViewById(R.id.stallImage);
            stallNameTextView = itemView.findViewById(R.id.stallNameTextView);
            ownerNameTextView = itemView.findViewById(R.id.ownerNameTextView);
            requestedDateTextView = itemView.findViewById(R.id.requestedDateTextView);
            viewButton = itemView.findViewById(R.id.viewButton);
        }

        // UPDATED: Binds a StallDetails object
        public void bind(final StallDetails stall, final OnStallDetailsClickListener listener) {
            stallNameTextView.setText(stall.getStallName());
            ownerNameTextView.setText(stall.getOwnerName());
            // Using the correct getter method from the StallDetails class
            requestedDateTextView.setText("Phone: ".concat(stall.getPhoneNumber()));

            // Use the TextDrawableUtil to generate and set the initials image
            if (stall.getStallName() != null && !stall.getStallName().isEmpty()) {
                stallImageView.setImageDrawable(TextDrawableUtil.getInitialDrawable(stall.getStallName()));
            }

            viewButton.setOnClickListener(v -> listener.onViewClick(stall));
        }
    }
}