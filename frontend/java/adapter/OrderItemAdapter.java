package com.simats.foodstall.adapter;

import android.app.TimePickerDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.switchmaterial.SwitchMaterial;
import com.simats.foodstall.R;
import com.simats.foodstall.model.OrderItem;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class OrderItemAdapter extends RecyclerView.Adapter<OrderItemAdapter.ViewHolder> {

    public interface OnDataChangedListener {
        void onDataChanged();
        void onEndTimeSet(String endTime);
    }

    private final List<OrderItem> items;
    private final Context context;
    private final OnDataChangedListener listener;

    public OrderItemAdapter(List<OrderItem> items, Context context, OnDataChangedListener listener) {
        this.items = items;
        this.context = context;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.card_confirm_order_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bind(items.get(position));
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView itemName, quantityTextView, itemPrice, timePickerText;
        Button increaseButton, decreaseButton;
        SwitchMaterial preParcelSwitch, parcelSwitch;
        LinearLayout timePickerLayout;
        ImageView deleteButton;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            itemName = itemView.findViewById(R.id.itemNameTextView);
            quantityTextView = itemView.findViewById(R.id.quantityTextView);
            itemPrice = itemView.findViewById(R.id.priceTextView);
            increaseButton = itemView.findViewById(R.id.increaseButton);
            decreaseButton = itemView.findViewById(R.id.decreaseButton);
            parcelSwitch = itemView.findViewById(R.id.parcelSwitch);
            preParcelSwitch = itemView.findViewById(R.id.preParcelSwitch);
            timePickerLayout = itemView.findViewById(R.id.timePickerLayout);
            timePickerText = itemView.findViewById(R.id.timePickerTextView);
            deleteButton = itemView.findViewById(R.id.deleteItemButton);
        }

        void bind(OrderItem item) {
            itemName.setText(item.getName());
            quantityTextView.setText(String.valueOf(item.getQuantity()));
            itemPrice.setText(String.format(Locale.getDefault(), "₹%.2f", item.getPrice() * item.getQuantity()));

            // --- START OF MODIFIED SECTION (Fixing the error) ---
            // Remove old listeners to prevent loops
            parcelSwitch.setOnCheckedChangeListener(null);
            preParcelSwitch.setOnCheckedChangeListener(null);

            // Get the status string from the model
            String status = item.getParcelStatus();

            // Set switches based on the status string
            parcelSwitch.setChecked("Parcel".equalsIgnoreCase(status));
            preParcelSwitch.setChecked("Pre-parcel".equalsIgnoreCase(status));

            // Show/hide time picker based on the status string
            timePickerLayout.setVisibility("Pre-parcel".equalsIgnoreCase(status) ? View.VISIBLE : View.GONE);
            timePickerText.setText("Click to set time"); // Reset text
            timePickerLayout.setOnClickListener(v -> showTimePickerDialog(item));
            // --- END OF MODIFIED SECTION ---

            increaseButton.setOnClickListener(v -> {
                item.incrementQuantity();
                notifyItemChanged(getAdapterPosition());
                listener.onDataChanged();
            });

            decreaseButton.setOnClickListener(v -> {
                item.decrementQuantity();
                if (item.getQuantity() < 1) {
                    item.setQuantity(1);
                }
                notifyItemChanged(getAdapterPosition());
                listener.onDataChanged();
            });

            // --- START OF MODIFIED SECTION (Updating the logic) ---
            parcelSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
                if (isChecked) {
                    item.setParcelStatus("Parcel");
                    // Ensure the other switch is off
                    if (preParcelSwitch.isChecked()) {
                        preParcelSwitch.setChecked(false);
                    }
                } else {
                    // If unchecked, revert to Dine-in
                    item.setParcelStatus("Dine-in");
                }
                notifyItemChanged(getAdapterPosition());
                listener.onDataChanged();
            });

            preParcelSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
                if (isChecked) {
                    item.setParcelStatus("Pre-parcel");
                    // Ensure the other switch is off
                    if (parcelSwitch.isChecked()) {
                        parcelSwitch.setChecked(false);
                    }
                    showTimePickerDialog(item);
                } else {
                    item.setParcelStatus("Dine-in");
                }
                notifyItemChanged(getAdapterPosition());
                listener.onDataChanged();
            });
            // --- END OF MODIFIED SECTION ---

            deleteButton.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {
                    items.remove(position);
                    notifyItemRemoved(position);
                    notifyItemRangeChanged(position, items.size());
                    listener.onDataChanged();
                }
            });
        }

        private void showTimePickerDialog(OrderItem item) {
            Calendar calendar = Calendar.getInstance();
            TimePickerDialog dialog = new TimePickerDialog(context, (view, hourOfDay, minute) -> {
                String amPm = hourOfDay >= 12 ? "PM" : "AM";
                int hourIn12 = hourOfDay % 12 == 0 ? 12 : hourOfDay % 12;
                String time = String.format(Locale.getDefault(), "%d:%02d %s", hourIn12, minute, amPm);

                // This no longer saves time to the item, it reports it to the Activity
                timePickerText.setText(time);
                if (listener != null) {
                    listener.onEndTimeSet(time);
                }
                notifyItemChanged(getAdapterPosition()); // To refresh UI if needed

            }, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), false);

            dialog.setOnCancelListener(dialogInterface -> {
                // If user cancels, uncheck the pre-parcel switch
                item.setParcelStatus("Dine-in");
                notifyItemChanged(getAdapterPosition());
                listener.onDataChanged();
            });
            dialog.show();
        }
    }
}