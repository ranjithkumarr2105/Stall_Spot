package com.simats.foodstall.adapter;

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
import com.simats.foodstall.model.Transaction;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

public class TransactionAdapter extends RecyclerView.Adapter<TransactionAdapter.TransactionViewHolder> {

    private List<Transaction> transactions;
    private final OnTransactionClickListener clickListener;
    private boolean isInSelectionMode = false;
    private final Set<Transaction> selectedItems = new HashSet<>();

    public interface OnTransactionClickListener {
        void onItemClick(Transaction transaction);
        void onItemLongClick(Transaction transaction);
    }

    public TransactionAdapter(List<Transaction> transactions, OnTransactionClickListener listener) {
        this.transactions = transactions;
        this.clickListener = listener;
    }

    public void updateList(List<Transaction> newTransactions) {
        this.transactions.clear();
        if (newTransactions != null) {
            this.transactions.addAll(newTransactions);
        }
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public TransactionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_transaction_item, parent, false);
        return new TransactionViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TransactionViewHolder holder, int position) {
        Transaction transaction = transactions.get(position);
        boolean isSelected = selectedItems.contains(transaction);
        holder.bind(transaction, isSelected, clickListener, isInSelectionMode);
    }

    @Override
    public int getItemCount() {
        return transactions.size();
    }

    public void setInSelectionMode(boolean inSelectionMode) {
        this.isInSelectionMode = inSelectionMode;
        if (!inSelectionMode) {
            selectedItems.clear();
        }
        notifyDataSetChanged();
    }

    public void toggleSelection(Transaction transaction) {
        if (selectedItems.contains(transaction)) {
            selectedItems.remove(transaction);
        } else {
            selectedItems.add(transaction);
        }
        notifyItemChanged(transactions.indexOf(transaction));
    }

    public int getSelectedItemCount() { return selectedItems.size(); }
    public List<Transaction> getSelectedItems() { return new ArrayList<>(selectedItems); }


    static class TransactionViewHolder extends RecyclerView.ViewHolder {
        ImageView transactionIcon;
        TextView transactionTitle, transactionTimestamp, transactionAmount, transactionStatus;

        public TransactionViewHolder(@NonNull View itemView) {
            super(itemView);
            transactionIcon = itemView.findViewById(R.id.transactionIcon);
            transactionTitle = itemView.findViewById(R.id.transactionTitle);
            transactionTimestamp = itemView.findViewById(R.id.transactionTimestamp);
            transactionAmount = itemView.findViewById(R.id.transactionAmount);
            transactionStatus = itemView.findViewById(R.id.transactionStatus);
        }

        void bind(Transaction transaction, boolean isSelected, OnTransactionClickListener listener, boolean isInSelectionMode) {
            transactionTimestamp.setText(formatDateTime(transaction.getTransactionDate()));
            transactionStatus.setText("Completed"); // Default status

            String type = transaction.getTransactionType();

            // --- NEW LOGIC TO HANDLE ALL 3 TRANSACTION TYPES ---
            if ("Added Money".equalsIgnoreCase(type)) {
                transactionIcon.setImageResource(R.drawable.ic_add_money);
                transactionTitle.setText("Added Money");
                transactionAmount.setText(String.format(Locale.getDefault(), "+ ₹%.2f", transaction.getAmount()));
                transactionAmount.setTextColor(ContextCompat.getColor(itemView.getContext(), R.color.green));
                transactionStatus.setTextColor(ContextCompat.getColor(itemView.getContext(), R.color.green));

            } else if ("Refund".equalsIgnoreCase(type)) {
                transactionIcon.setImageResource(R.drawable.ic_refund); // New Refund Icon
                transactionTitle.setText(transaction.getDescription()); // e.g., "Refunded from ORD-123"
                transactionAmount.setText(String.format(Locale.getDefault(), "+ ₹%.2f", transaction.getAmount()));
                transactionAmount.setTextColor(ContextCompat.getColor(itemView.getContext(), R.color.green));
                transactionStatus.setTextColor(ContextCompat.getColor(itemView.getContext(), R.color.green));

            } else { // "Order Payment"
                transactionIcon.setImageResource(R.drawable.ic_order_payment); // You may need to create this icon
                transactionTitle.setText(transaction.getDescription()); // e.g., "Paid to Stall Name"
                transactionAmount.setText(String.format(Locale.getDefault(), "- ₹%.2f", transaction.getAmount()));
                transactionAmount.setTextColor(ContextCompat.getColor(itemView.getContext(), R.color.status_rejected_red));
                transactionStatus.setTextColor(ContextCompat.getColor(itemView.getContext(), R.color.status_rejected_red));
            }
            // --- END OF NEW LOGIC ---

            itemView.setBackgroundColor(isSelected ? Color.parseColor("#E0E0E0") : Color.TRANSPARENT);
            itemView.setOnClickListener(v -> {
                if (isInSelectionMode && listener != null) {
                    listener.onItemClick(transaction);
                }
            });
            itemView.setOnLongClickListener(v -> {
                if (listener != null) {
                    listener.onItemLongClick(transaction);
                }
                return true;
            });
        }

        private String formatDateTime(String dateString) {
            if (dateString == null) return "";
            SimpleDateFormat dbFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
            SimpleDateFormat displayFormat = new SimpleDateFormat("dd MMM, hh:mm a", Locale.getDefault());
            try {
                Date date = dbFormat.parse(dateString);
                return displayFormat.format(date);
            } catch (ParseException e) {
                return dateString;
            }
        }
    }
}