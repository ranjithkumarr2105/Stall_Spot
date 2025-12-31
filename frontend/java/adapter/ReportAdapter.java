package com.simats.foodstall.adapter;

import android.annotation.SuppressLint;
import android.util.Log; // Import Log
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast; // Import Toast

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.simats.foodstall.ApiClient; // Import ApiClient
import com.simats.foodstall.ApiService; // Import ApiService
import com.simats.foodstall.R;
import com.simats.foodstall.model.ReportItem;
import com.simats.foodstall.model.StatusResponse; // Import StatusResponse

import java.util.List;
import java.util.Locale;

import retrofit2.Call; // Import Call
import retrofit2.Callback; // Import Callback
import retrofit2.Response; // Import Response

// Removed adapter reference passing
public class ReportAdapter extends RecyclerView.Adapter<ReportAdapter.ReportViewHolder> {
    private final List<ReportItem> reportItems;
    private final OnReportItemClickListener listener;
    private static final String TAG = "ReportAdapter"; // Add TAG for logging

    public interface OnReportItemClickListener {
        void onDownloadStatementClick(ReportItem item);
        // Added listener for acknowledge API call start/finish if needed by activity
        // void onAcknowledgeStart(ReportItem item);
        // void onAcknowledgeComplete(ReportItem item, boolean success);
    }

    public ReportAdapter(List<ReportItem> reportItems, OnReportItemClickListener listener) {
        this.reportItems = reportItems;
        this.listener = listener;
    }

    @SuppressLint("NotifyDataSetChanged")
    public void updateData(List<ReportItem> newReportItems) {
        // Removed isRentHidden reset
        reportItems.clear();
        if (newReportItems != null) {
            reportItems.addAll(newReportItems);
        }
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ReportViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_report_item, parent, false);
        // Removed adapter passing
        return new ReportViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ReportViewHolder holder, int position) {
        ReportItem item = reportItems.get(position);
        holder.bind(item, listener);
    }

    @Override
    public int getItemCount() {
        return reportItems.size();
    }

    // Removed adapter reference
    public static class ReportViewHolder extends RecyclerView.ViewHolder {
        TextView rankTextView, stallNameTextView, stallIdTextView, totalRevenueTextView, bestSellingItemTextView;
        ImageView trendIcon;
        LinearLayout rentDetailsLayout;
        TextView rentAmountTextView, rentStatusTextView;
        ImageButton downloadStatementButton;
        ImageView paidTickIcon;
        // Removed adapter field

        // Removed adapter from constructor
        public ReportViewHolder(@NonNull View itemView) {
            super(itemView);
            // this.adapter = adapter; // Removed
            rankTextView = itemView.findViewById(R.id.rankTextView);
            stallNameTextView = itemView.findViewById(R.id.stallNameTextView);
            stallIdTextView = itemView.findViewById(R.id.stallIdTextView);
            totalRevenueTextView = itemView.findViewById(R.id.totalRevenueTextView);
            bestSellingItemTextView = itemView.findViewById(R.id.bestSellingItemTextView);
            trendIcon = itemView.findViewById(R.id.trendIcon);
            rentDetailsLayout = itemView.findViewById(R.id.rentDetailsLayout);
            rentAmountTextView = itemView.findViewById(R.id.rentAmountTextView);
            rentStatusTextView = itemView.findViewById(R.id.rentStatusTextView);
            downloadStatementButton = itemView.findViewById(R.id.downloadStatementButton);
            paidTickIcon = itemView.findViewById(R.id.paidTickIcon);
        }

        public void bind(final ReportItem item, final OnReportItemClickListener listener) {
            // Bind non-rent data (unchanged)
            rankTextView.setText(item.getRank());
            stallNameTextView.setText(item.getStallName());
            stallIdTextView.setText(item.getStallId());
            totalRevenueTextView.setText(String.format(Locale.getDefault(), "₹%.2f", item.getTotalRevenue()));
            bestSellingItemTextView.setText(item.getBestSellingItem());

            // Trend Icon Logic (unchanged)
            try { /* ... trend icon logic ... */ } catch (NumberFormatException e) { /* ... */ }

            // --- [MODIFIED] Rent Logic now relies on backend sending data ---
            // If invoiceId is null (meaning backend determined it's paid AND acknowledged), hide the layout.
            if (item.getInvoiceId() == null) {
                rentDetailsLayout.setVisibility(View.GONE);
                // Clear listeners just in case view is recycled
                paidTickIcon.setOnClickListener(null);
                downloadStatementButton.setOnClickListener(null);
            }
            // Otherwise, if invoiceId is present, show the details
            else if (item.getInvoiceId() > 0 && item.getRentAmount() != null) {
                rentDetailsLayout.setVisibility(View.VISIBLE);

                double lateFee = (item.getLateFee() != null ? item.getLateFee() : 0.0);
                double totalRent = item.getRentAmount() + lateFee;

                rentAmountTextView.setText(String.format(Locale.getDefault(), "Rent: ₹%.2f", totalRent));

                if ("paid".equalsIgnoreCase(item.getRentStatus())) {
                    rentStatusTextView.setText("PAID");
                    rentStatusTextView.setTextColor(ContextCompat.getColor(itemView.getContext(), R.color.green));
                    paidTickIcon.setVisibility(View.VISIBLE);
                    paidTickIcon.setEnabled(true); // Ensure icon is clickable initially

                    // --- [MODIFIED] Tick Icon OnClickListener ---
                    paidTickIcon.setOnClickListener(v -> {
                        Log.d(TAG, "Tick clicked for invoice ID: " + item.getInvoiceId());
                        paidTickIcon.setEnabled(false); // Prevent multiple clicks
                        // Optionally show a small progress indicator here
                        // if (listener != null) listener.onAcknowledgeStart(item);

                        ApiService apiService = ApiClient.getClient().create(ApiService.class);
                        apiService.acknowledgeRentPayment(item.getInvoiceId()).enqueue(new Callback<StatusResponse>() {
                            @Override
                            public void onResponse(@NonNull Call<StatusResponse> call, @NonNull Response<StatusResponse> response) {
                                // Re-enable icon ONLY if API failed, otherwise it stays hidden
                                if (response.isSuccessful() && response.body() != null && "success".equals(response.body().getStatus())) {
                                    Log.d(TAG, "Acknowledge successful for invoice ID: " + item.getInvoiceId());
                                    // SUCCESS: Hide the layout immediately for visual feedback
                                    rentDetailsLayout.setVisibility(View.GONE);
                                    // No need to call notifyItemChanged, direct UI update is enough
                                    // if (listener != null) listener.onAcknowledgeComplete(item, true);
                                } else {
                                    Log.e(TAG, "Acknowledge failed for invoice ID: " + item.getInvoiceId() + " - " + response.message());
                                    Toast.makeText(itemView.getContext(), "Failed to acknowledge payment.", Toast.LENGTH_SHORT).show();
                                    paidTickIcon.setEnabled(true); // Re-enable on failure
                                    // if (listener != null) listener.onAcknowledgeComplete(item, false);
                                }
                            }

                            @Override
                            public void onFailure(@NonNull Call<StatusResponse> call, @NonNull Throwable t) {
                                Log.e(TAG, "Acknowledge network error for invoice ID: " + item.getInvoiceId(), t);
                                Toast.makeText(itemView.getContext(), "Network error.", Toast.LENGTH_SHORT).show();
                                paidTickIcon.setEnabled(true); // Re-enable on failure
                                // if (listener != null) listener.onAcknowledgeComplete(item, false);
                            }
                        });
                    });
                    // --- End Tick Icon OnClickListener ---

                } else { // Rent is unpaid or overdue
                    rentStatusTextView.setText("UNPAID");
                    if (lateFee > 0) {
                        rentAmountTextView.setText(String.format(Locale.getDefault(), "Rent: ₹%.2f (Late Fee: ₹%.2f)", totalRent, lateFee));
                        rentStatusTextView.setText("OVERDUE");
                    }
                    rentStatusTextView.setTextColor(ContextCompat.getColor(itemView.getContext(), R.color.red));
                    paidTickIcon.setVisibility(View.GONE);
                    paidTickIcon.setOnClickListener(null); // Clear listener
                }

                // Existing download listener (unchanged)
                downloadStatementButton.setOnClickListener(v -> {
                    if (listener != null) {
                        listener.onDownloadStatementClick(item);
                    }
                });

            } else {
                // This case should ideally not happen if backend logic is correct,
                // but hide as a fallback if invoiceId exists but amount doesn't.
                rentDetailsLayout.setVisibility(View.GONE);
                paidTickIcon.setOnClickListener(null);
                downloadStatementButton.setOnClickListener(null);
            }
        }
    }
}
