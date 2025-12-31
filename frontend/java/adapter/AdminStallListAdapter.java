package com.simats.foodstall.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.simats.foodstall.R;
import com.simats.foodstall.TextDrawableUtil;
import com.simats.foodstall.model.AdminStall;
import java.util.ArrayList;
import java.util.List;
import de.hdodenhof.circleimageview.CircleImageView;

public class AdminStallListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final List<AdminStall> stallsList = new ArrayList<>();
    private final OnStallClickListener listener;
    private final int viewType;

    public static final int VIEW_TYPE_GENERAL = 1;
    public static final int VIEW_TYPE_STATUS = 2;

    public interface OnStallClickListener {
        void onViewClick(AdminStall stall);
    }

    public AdminStallListAdapter(int viewType, OnStallClickListener listener) {
        this.viewType = viewType;
        this.listener = listener;
    }

    @SuppressLint("NotifyDataSetChanged")
    public void updateStalls(List<AdminStall> newStalls) {
        stallsList.clear();
        stallsList.addAll(newStalls);
        notifyDataSetChanged();
    }

    @Override
    public int getItemViewType(int position) {
        return viewType;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == VIEW_TYPE_STATUS) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_admin_stall_status, parent, false);
            return new StatusViewHolder(view);
        } else { // VIEW_TYPE_GENERAL
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_admin_stall, parent, false);
            return new GeneralViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        AdminStall stall = stallsList.get(position);
        if (holder.getItemViewType() == VIEW_TYPE_STATUS) {
            ((StatusViewHolder) holder).bind(stall, listener);
        } else {
            ((GeneralViewHolder) holder).bind(stall);
        }
    }

    @Override
    public int getItemCount() {
        return stallsList.size();
    }

    // ViewHolder for card_admin_stall
    static class GeneralViewHolder extends RecyclerView.ViewHolder {
        CircleImageView stallImage;
        TextView stallName, ownerName, status;

        public GeneralViewHolder(@NonNull View itemView) {
            super(itemView);
            stallImage = itemView.findViewById(R.id.stallImage);
            stallName = itemView.findViewById(R.id.stallNameTextView);
            ownerName = itemView.findViewById(R.id.ownerNameTextView);
            status = itemView.findViewById(R.id.statusTextView);
        }

        void bind(AdminStall stall) {
            bindCommonData(stall, stallImage, stallName, ownerName, status, itemView.getContext());
        }
    }

    // ViewHolder for card_admin_stall_status
    static class StatusViewHolder extends RecyclerView.ViewHolder {
        CircleImageView stallImage;
        TextView stallName, ownerName, status, reason;
        Button viewButton;

        public StatusViewHolder(@NonNull View itemView) {
            super(itemView);
            stallImage = itemView.findViewById(R.id.stallImage);
            stallName = itemView.findViewById(R.id.stallNameTextView);
            ownerName = itemView.findViewById(R.id.ownerNameTextView);
            status = itemView.findViewById(R.id.statusTextView);
            reason = itemView.findViewById(R.id.reasonTextView);
            viewButton = itemView.findViewById(R.id.viewButton);
        }

        void bind(AdminStall stall, OnStallClickListener listener) {
            bindCommonData(stall, stallImage, stallName, ownerName, status, itemView.getContext());

            String rejectionReason = stall.getRejectionReason();
            if (rejectionReason != null && !rejectionReason.isEmpty() && stall.getApproval() == -1) {
                reason.setText("Reason: " + rejectionReason);
                reason.setVisibility(View.VISIBLE);
            } else {
                reason.setVisibility(View.GONE);
            }

            if (stall.getApproval() == 0) { // Pending
                viewButton.setVisibility(View.VISIBLE);
                viewButton.setOnClickListener(v -> {
                    if (listener != null) {
                        listener.onViewClick(stall);
                    }
                });
            } else {
                viewButton.setVisibility(View.GONE);
            }
        }
    }

    // Shared logic for binding data common to both view holders
    private static void bindCommonData(AdminStall stall, CircleImageView image, TextView name, TextView owner, TextView status, Context context) {
        name.setText(stall.getStallName());
        owner.setText(stall.getOwnerName());

        Drawable initialsDrawable = TextDrawableUtil.getInitialDrawable(stall.getStallName());
        Glide.with(context)
                .load(stall.getProfilePhoto())
                .placeholder(initialsDrawable)
                .error(initialsDrawable)
                .into(image);

        switch (stall.getApproval()) {
            case 1: // Approved
                status.setText("Approved");
                status.setBackgroundResource(R.drawable.status_approved_background);
                status.setTextColor(Color.WHITE);
                break;
            case -1: // Rejected
                status.setText("Rejected");
                status.setBackgroundResource(R.drawable.status_rejected_background);
                status.setTextColor(Color.WHITE);
                break;
            case 0: // Pending
            default:
                status.setText("Pending");
                status.setBackgroundResource(R.drawable.status_pending_background);
                status.setTextColor(Color.WHITE);
                break;
        }
    }
}