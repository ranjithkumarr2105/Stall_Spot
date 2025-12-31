package com.simats.foodstall;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.content.res.ColorStateList; // Import this
import android.graphics.Color; // Import this
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.material.appbar.MaterialToolbar;
import com.simats.foodstall.model.StatusResponse;
// The model import is now correct
import com.simats.foodstall.model.AdminStall;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Astall_detailsActivity extends AppCompatActivity {

    private TextView stallNameValue, ownerNameValue, stallInitialsTextView;
    private Button approveButton, rejectButton;
    private FrameLayout loadingOverlay;
    private ImageView loadingIcon;
    private AdminStall stallDetails; // This is now the updated AdminStall

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.astall_details);

        MaterialToolbar topBar = findViewById(R.id.topBar);
        topBar.setNavigationOnClickListener(v -> finish());

        bindViews();

        // This line is now correct and will get the full object
        stallDetails = getIntent().getParcelableExtra("STALL_DATA");

        if (stallDetails == null) {
            Toast.makeText(this, "Error: Stall details not found.", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        populateStallDetails();

        approveButton.setOnClickListener(v -> updateStallStatus("1"));

        rejectButton.setOnClickListener(v -> {
            Intent intent = new Intent(Astall_detailsActivity.this, AreasonActivity.class);
            intent.putExtra("STALL_EMAIL", stallDetails.getEmail());
            intent.putExtra("STATUS_UPDATE", "-1");
            intent.putExtra("STALL_NAME", stallDetails.getStallName());
            intent.putExtra("OWNER_NAME", stallDetails.getOwnerName());
            startActivity(intent);
        });
    }

    private void bindViews() {
        stallNameValue = findViewById(R.id.stallNameValue);
        ownerNameValue = findViewById(R.id.ownerNameValue);
        stallInitialsTextView = findViewById(R.id.stallInitialsTextView);
        approveButton = findViewById(R.id.approveButton);
        rejectButton = findViewById(R.id.rejectButton);
        loadingOverlay = findViewById(R.id.loadingOverlay);
        loadingIcon = findViewById(R.id.loadingIcon);
    }

    private void populateStallDetails() {
        stallNameValue.setText(stallDetails.getStallName());
        ownerNameValue.setText("by " + stallDetails.getOwnerName());
        stallInitialsTextView.setText(TextDrawableUtil.getInitials(stallDetails.getStallName()));
        GradientDrawable bg = (GradientDrawable) stallInitialsTextView.getBackground().mutate();
        bg.setColor(TextDrawableUtil.getColor(stallDetails.getStallName()));

        // [DESIGN UPDATE] Passing unique colors to each row
        // These methods will no longer show errors
        setupDetailRow(findViewById(R.id.emailRow), R.drawable.ic_email1, "Email Address", stallDetails.getEmail(), "#D44638");
        setupDetailRow(findViewById(R.id.phoneRow), R.drawable.ic_phone1, "Phone Number", stallDetails.getPhoneNumber(), "#34A853");
        setupDetailRow(findViewById(R.id.addressRow), R.drawable.ic_address1, "Full Address", stallDetails.getFullAddress(), "#4285F4");
        setupDetailRow(findViewById(R.id.fssaiRow), R.drawable.ic_fssai1, "FSSAI Number", stallDetails.getFssaiNumber(), "#FBBC05");
        setupDetailRow(findViewById(R.id.dateRow), R.drawable.ic_date1, "Date Requested", stallDetails.getDateRequested(), "#757575");
    }

    // [DESIGN UPDATE] Added a 'hexColor' parameter to set the icon color
    private void setupDetailRow(View rowView, int iconRes, String label, String value, String hexColor) {
        ImageView icon = rowView.findViewById(R.id.detailIcon);
        TextView labelTextView = rowView.findViewById(R.id.detailLabel);
        TextView valueTextView = rowView.findViewById(R.id.detailValue);

        icon.setImageResource(iconRes);
        // This line sets the unique color for the icon
        icon.setImageTintList(ColorStateList.valueOf(Color.parseColor(hexColor)));

        labelTextView.setText(label);
        valueTextView.setText(value != null && !value.isEmpty() ? value : "Not Provided");
    }

    private void updateStallStatus(String status) {
        startLoadingAnimation();
        approveButton.setEnabled(false);
        rejectButton.setEnabled(false);

        ApiService apiService = ApiClient.getClient().create(ApiService.class);
        Call<StatusResponse> call = apiService.updateStallStatus(stallDetails.getEmail(), status, null);

        call.enqueue(new Callback<StatusResponse>() {
            @Override
            public void onResponse(@NonNull Call<StatusResponse> call, @NonNull Response<StatusResponse> response) {
                hideLoadingOverlay();
                if (response.isSuccessful() && response.body() != null && "success".equals(response.body().getStatus())) {
                    StatusResponse apiResponse = response.body();
                    Toast.makeText(Astall_detailsActivity.this, "Stall has been approved.", Toast.LENGTH_LONG).show();
                    Intent approvalIntent = new Intent(Astall_detailsActivity.this, AapprovalActivity.class);
                    approvalIntent.putExtra("STALL_ID", apiResponse.getStallId());
                    approvalIntent.putExtra("STALL_NAME", stallDetails.getStallName());
                    startActivity(approvalIntent);
                    finish();
                } else {
                    String errorMsg = (response.body() != null) ? response.body().getMessage() : "Failed to approve.";
                    Toast.makeText(Astall_detailsActivity.this, "Failed: " + errorMsg, Toast.LENGTH_LONG).show();
                    approveButton.setEnabled(true);
                    rejectButton.setEnabled(true);
                }
            }

            @Override
            public void onFailure(@NonNull Call<StatusResponse> call, @NonNull Throwable t) {
                hideLoadingOverlay();
                approveButton.setEnabled(true);
                rejectButton.setEnabled(true);
                Log.e("API_FAILURE", "Failed to update stall status", t);
                Toast.makeText(Astall_detailsActivity.this, "Network Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void startLoadingAnimation() {
        if (loadingOverlay != null) {
            loadingOverlay.setVisibility(View.VISIBLE);
            Animation rotation = AnimationUtils.loadAnimation(this, R.anim.hourglass_rotation);
            loadingIcon.startAnimation(rotation);
        }
    }

    private void hideLoadingOverlay() {
        if (loadingOverlay != null && loadingOverlay.getVisibility() == View.VISIBLE) {
            loadingIcon.clearAnimation();
            loadingOverlay.setVisibility(View.GONE);
        }
    }
}