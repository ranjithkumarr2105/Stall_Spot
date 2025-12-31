package com.simats.foodstall.adapter;

import android.graphics.drawable.GradientDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.google.android.material.card.MaterialCardView;
import com.simats.foodstall.ApiClient;
import com.simats.foodstall.R;
import com.simats.foodstall.TextDrawableUtil;
import com.simats.foodstall.model.OMenuItem;
import java.util.Locale;
import de.hdodenhof.circleimageview.CircleImageView;

public class OOwnerMenuAdapter extends ListAdapter<OMenuItem, OOwnerMenuAdapter.MenuViewHolder> {

    private final OnMenuItemClickListener listener;

    public interface OnMenuItemClickListener {
        void onEditClick(OMenuItem item);
        void onDeleteClick(OMenuItem item);
    }

    public OOwnerMenuAdapter(OnMenuItemClickListener listener) {
        super(DIFF_CALLBACK);
        this.listener = listener;
    }

    private static final DiffUtil.ItemCallback<OMenuItem> DIFF_CALLBACK = new DiffUtil.ItemCallback<OMenuItem>() {
        @Override
        public boolean areItemsTheSame(@NonNull OMenuItem oldItem, @NonNull OMenuItem newItem) {
            return oldItem.getItemId() == newItem.getItemId();
        }

        @Override
        public boolean areContentsTheSame(@NonNull OMenuItem oldItem, @NonNull OMenuItem newItem) {
            return oldItem.equals(newItem);
        }
    };

    @NonNull
    @Override
    public MenuViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_owner_menu_item, parent, false);
        return new MenuViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MenuViewHolder holder, int position) {
        holder.bind(getItem(position));
    }

    class MenuViewHolder extends RecyclerView.ViewHolder {
        TextView itemName, itemPrice, itemCategory, itemInitialsTextView;
        ImageButton editButton, deleteButton;
        ImageView specialIcon;
        CircleImageView itemImageView;
        MaterialCardView cardView;

        public MenuViewHolder(@NonNull View itemView) {
            super(itemView);
            itemName = itemView.findViewById(R.id.itemNameTextView);
            itemPrice = itemView.findViewById(R.id.itemPriceTextView);
            itemCategory = itemView.findViewById(R.id.itemCategoryTextView);
            editButton = itemView.findViewById(R.id.editButton);
            deleteButton = itemView.findViewById(R.id.deleteButton);
            specialIcon = itemView.findViewById(R.id.specialIcon);
            itemImageView = itemView.findViewById(R.id.itemImageView);
            itemInitialsTextView = itemView.findViewById(R.id.itemInitialsTextView);
            cardView = (MaterialCardView) itemView;
        }

        public void bind(final OMenuItem item) {
            itemName.setText(item.getName());
            itemPrice.setText(String.format(Locale.getDefault(),"₹%.2f",item.getPrice()));
            itemCategory.setText(item.getCategory());

            if ("Today's Special".equalsIgnoreCase(item.getCategory())) {
                specialIcon.setVisibility(View.VISIBLE);
                cardView.setStrokeWidth(4);
            } else {
                specialIcon.setVisibility(View.GONE);
                cardView.setStrokeWidth(0);
            }

            String imageUrl = item.getItemImage();
            if (imageUrl != null && !imageUrl.isEmpty()) {
                itemInitialsTextView.setVisibility(View.GONE);
                itemImageView.setVisibility(View.VISIBLE);
                String fullUrl = ApiClient.BASE_URL + "uploads/" + imageUrl;
                Glide.with(itemView.getContext())
                        .load(fullUrl)
                        .placeholder(R.drawable.ic_camera_background)
                        .error(R.drawable.ic_camera_background)
                        .into(itemImageView);
            } else {
                itemImageView.setVisibility(View.GONE);
                itemInitialsTextView.setVisibility(View.VISIBLE);
                itemInitialsTextView.setText(TextDrawableUtil.getInitials(item.getName()));
                GradientDrawable background = (GradientDrawable) itemInitialsTextView.getBackground().mutate();
                background.setColor(TextDrawableUtil.getColor(item.getName()));
            }

            editButton.setOnClickListener(v -> listener.onEditClick(item));
            deleteButton.setOnClickListener(v -> listener.onDeleteClick(item));
        }
    }
}