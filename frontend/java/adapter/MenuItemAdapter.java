package com.simats.foodstall.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.simats.foodstall.ApiClient;
import com.simats.foodstall.R;
import com.simats.foodstall.TextDrawableUtil;
import com.simats.foodstall.model.MenuItem;
import java.util.Locale;
import java.util.Objects;

public class MenuItemAdapter extends ListAdapter<MenuItem, MenuItemAdapter.MenuItemViewHolder> {

    private final OnAddItemClickListener listener;

    public interface OnAddItemClickListener {
        void onAddItemClick(MenuItem item);
    }

    public MenuItemAdapter(OnAddItemClickListener listener) {
        super(DIFF_CALLBACK);
        this.listener = listener;
    }

    private static final DiffUtil.ItemCallback<MenuItem> DIFF_CALLBACK = new DiffUtil.ItemCallback<MenuItem>() {
        @Override
        public boolean areItemsTheSame(@NonNull MenuItem oldItem, @NonNull MenuItem newItem) {
            return Objects.equals(oldItem.getName(), newItem.getName());
        }

        @Override
        public boolean areContentsTheSame(@NonNull MenuItem oldItem, @NonNull MenuItem newItem) {
            return oldItem.equals(newItem);
        }
    };

    @NonNull
    @Override
    public MenuItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_menu_item, parent, false);
        return new MenuItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MenuItemViewHolder holder, int position) {
        MenuItem item = getItem(position);
        if (item != null) {
            holder.bind(item);
        }
    }

    class MenuItemViewHolder extends RecyclerView.ViewHolder {
        ImageView itemImage;
        TextView itemInitials, itemName, itemPrice;
        Button addButton;

        public MenuItemViewHolder(@NonNull View itemView) {
            super(itemView);
            itemImage = itemView.findViewById(R.id.menuItemImage);
            itemInitials = itemView.findViewById(R.id.menuItemInitials);
            itemName = itemView.findViewById(R.id.menuItemName);
            itemPrice = itemView.findViewById(R.id.menuItemPrice);
            addButton = itemView.findViewById(R.id.menuItemAddButton);
        }

        void bind(final MenuItem item) {
            itemName.setText(item.getName());
            itemPrice.setText(String.format(Locale.getDefault(), "₹%.2f", item.getPrice()));

            String imageUrl = item.getImageUrl();
            if (imageUrl != null && !imageUrl.isEmpty()) {
                itemInitials.setVisibility(View.GONE);
                itemImage.setVisibility(View.VISIBLE);
                String fullUrl = ApiClient.BASE_URL + "uploads/" + imageUrl;
                Glide.with(itemView.getContext())
                        .load(fullUrl)
                        .placeholder(R.drawable.ic_camera_background)
                        .into(itemImage);
            } else {
                itemImage.setVisibility(View.GONE);
                itemInitials.setVisibility(View.VISIBLE);
                itemInitials.setText(TextDrawableUtil.getInitials(item.getName()));
                itemInitials.setBackgroundColor(TextDrawableUtil.getColor(item.getName()));
            }

            addButton.setOnClickListener(v -> listener.onAddItemClick(item));
        }
    }
}