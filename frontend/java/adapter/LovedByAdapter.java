package com.simats.foodstall.adapter;

import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
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
import de.hdodenhof.circleimageview.CircleImageView;

public class LovedByAdapter extends ListAdapter<MenuItem, LovedByAdapter.LovedByViewHolder> {

    private final OnAddItemClickListener listener;

    public interface OnAddItemClickListener {
        void onAddItemClick(MenuItem item);
    }

    public LovedByAdapter(OnAddItemClickListener listener) {
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
    public LovedByViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_loved_by_item, parent, false);
        return new LovedByViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull LovedByViewHolder holder, int position) {
        holder.bind(getItem(position));
    }

    class LovedByViewHolder extends RecyclerView.ViewHolder {
        TextView itemName, itemPrice;
        CircleImageView itemImage;
        Button addButton;

        LovedByViewHolder(@NonNull View itemView) {
            super(itemView);
            itemImage = itemView.findViewById(R.id.lovedItemImageView);
            itemName = itemView.findViewById(R.id.lovedItemNameTextView);
            itemPrice = itemView.findViewById(R.id.lovedItemPriceTextView);
            addButton = itemView.findViewById(R.id.lovedItemAddButton);
        }

        void bind(final MenuItem item) {
            itemName.setText(item.getName());
            itemPrice.setText(String.format(Locale.getDefault(), "₹%.2f", item.getPrice()));

            String imageUrl = item.getImageUrl();
            if (imageUrl != null && !imageUrl.isEmpty()) {
                String fullUrl = ApiClient.BASE_URL + "uploads/" + imageUrl;
                Glide.with(itemView.getContext())
                        .load(fullUrl)
                        .placeholder(R.drawable.ic_camera_background)
                        .into(itemImage);
            } else {
                Drawable initialsDrawable = TextDrawableUtil.getInitialDrawable(item.getName());
                itemImage.setImageDrawable(initialsDrawable);
            }

            addButton.setOnClickListener(v -> listener.onAddItemClick(item));
        }
    }
}