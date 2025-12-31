package com.simats.foodstall.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import com.simats.foodstall.R;
import com.simats.foodstall.UpiApp;

import java.util.List;

public class UpiAppAdapter extends RecyclerView.Adapter<UpiAppAdapter.ViewHolder> {

    public interface OnUpiAppSelectedListener {
        void onUpiAppSelected(UpiApp upiApp);
    }

    private final List<UpiApp> upiApps;
    private final OnUpiAppSelectedListener listener;
    private int selectedPosition = -1;
    private final Context context;

    public UpiAppAdapter(Context context, List<UpiApp> upiApps, OnUpiAppSelectedListener listener) {
        this.context = context;
        this.upiApps = upiApps;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_upi_app, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bind(upiApps.get(position));
    }

    @Override
    public int getItemCount() {
        return upiApps.size();
    }

    public void clearSelection() {
        int previouslySelected = selectedPosition;
        selectedPosition = -1;
        if (previouslySelected != -1) {
            notifyItemChanged(previouslySelected);
        }
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        CardView cardView;
        ImageView logo;
        TextView name;
        RadioButton radioButton;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            cardView = (CardView) itemView;
            logo = itemView.findViewById(R.id.upiAppLogo);
            name = itemView.findViewById(R.id.upiAppName);
            radioButton = itemView.findViewById(R.id.upiRadioButton);
        }

        void bind(UpiApp upiApp) {
            logo.setImageResource(upiApp.getLogoResId());
            name.setText(upiApp.getName());

            if (getAdapterPosition() == selectedPosition) {
                radioButton.setChecked(true);
                cardView.setBackground(ContextCompat.getDrawable(context, R.drawable.upi_card_selected_background));
            } else {
                radioButton.setChecked(false);
                // Use the theme-aware color for the unselected state
                cardView.setCardBackgroundColor(ContextCompat.getColor(context, R.color.card_background));
                cardView.setBackground(null); // Remove special background
            }

            itemView.setOnClickListener(v -> {
                if (getAdapterPosition() == RecyclerView.NO_POSITION) return;

                int previousSelected = selectedPosition;
                selectedPosition = getAdapterPosition();

                if (previousSelected != -1) {
                    notifyItemChanged(previousSelected);
                }
                notifyItemChanged(selectedPosition);

                listener.onUpiAppSelected(upiApp);
            });
        }
    }
}