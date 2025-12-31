package com.simats.foodstall;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import com.razorpay.Checkout;
import com.razorpay.PaymentData;
import com.razorpay.PaymentResultWithDataListener;
import com.simats.foodstall.model.RazorpayOrderResponse;
import com.simats.foodstall.model.StatusResponse;
import org.json.JSONObject;
import java.io.IOException;
import java.util.Locale;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Uadd_moneyActivity extends AppCompatActivity implements PaymentResultWithDataListener {

    private EditText amountEditText;
    private Button addMoneyPayButton;
    private FrameLayout loadingOverlay;
    private ImageView loadingIcon;
    private TextView currentBalanceTextView;
    private String studentId, studentEmail, studentPhone, sourceActivity;
    private double amountToAdd = 0.0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.uadd_money);

        Checkout.preload(getApplicationContext());
        bindViews();

        sourceActivity = getIntent().getStringExtra("SOURCE_ACTIVITY");
        double currentBalance = getIntent().getDoubleExtra("CURRENT_BALANCE", 0.0);
        currentBalanceTextView.setText(String.format(Locale.getDefault(), "₹%.2f", currentBalance));

        SharedPreferences prefs = getSharedPreferences("StallSpotPrefs", MODE_PRIVATE);
        studentId = prefs.getString("STUDENT_ID", "");
        studentEmail = prefs.getString("USER_EMAIL", "test@example.com");
        studentPhone = prefs.getString("USER_PHONE", "9876543210");

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setNavigationOnClickListener(v -> finish());

        setupClickListeners();
    }

    private void bindViews() {
        amountEditText = findViewById(R.id.amountEditText);
        addMoneyPayButton = findViewById(R.id.addMoneyPayButton);
        loadingOverlay = findViewById(R.id.loadingOverlay);
        loadingIcon = findViewById(R.id.loadingIcon);
        currentBalanceTextView = findViewById(R.id.currentBalanceTextView);
    }

    private void setupClickListeners() {
        addMoneyPayButton.setOnClickListener(v -> startAddMoneyFlow());

        findViewById(R.id.add100Button).setOnClickListener(v -> addAmount(100));
        findViewById(R.id.add500Button).setOnClickListener(v -> addAmount(500));
        findViewById(R.id.add1000Button).setOnClickListener(v -> addAmount(1000));

        amountEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                try {
                    if (s.toString().isEmpty()) {
                        amountToAdd = 0.0;
                    } else {
                        amountToAdd = Double.parseDouble(s.toString());
                    }
                } catch (NumberFormatException e) {
                    amountToAdd = 0.0;
                }
            }
            @Override
            public void afterTextChanged(Editable s) {}
        });
    }

    private void addAmount(double amount) {
        try {
            String currentAmountStr = amountEditText.getText().toString();
            if (currentAmountStr.isEmpty()) {
                amountToAdd = 0.0;
            } else {
                amountToAdd = Double.parseDouble(currentAmountStr);
            }
        } catch (NumberFormatException e) {
            amountToAdd = 0.0;
        }
        amountToAdd += amount;
        amountEditText.setText(String.format(Locale.getDefault(), "%.0f", amountToAdd));
        amountEditText.setSelection(amountEditText.getText().length());
    }

    private void startAddMoneyFlow() {
        String amountStr = amountEditText.getText().toString();
        if (amountStr.isEmpty() || Double.parseDouble(amountStr) <= 0) {
            Toast.makeText(this, "Please enter a valid amount", Toast.LENGTH_SHORT).show();
            return;
        }
        amountToAdd = Double.parseDouble(amountStr);
        showLoading(true);

        ApiClient.getClient().create(ApiService.class).createRazorpayOrder(amountToAdd).enqueue(new Callback<RazorpayOrderResponse>() {
            @Override
            public void onResponse(Call<RazorpayOrderResponse> call, Response<RazorpayOrderResponse> response) {
                if (response.isSuccessful() && response.body() != null && "success".equals(response.body().getStatus())) {
                    initiateRazorpayPayment(response.body());
                } else {
                    showLoading(false);
                    Toast.makeText(Uadd_moneyActivity.this, "Could not connect to payment gateway.", Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onFailure(Call<RazorpayOrderResponse> call, Throwable t) {
                showLoading(false);
                Toast.makeText(Uadd_moneyActivity.this, "Network Error. Please try again.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void initiateRazorpayPayment(RazorpayOrderResponse orderResponse) {
        final Activity activity = this;
        final Checkout co = new Checkout();
        try {
            JSONObject options = new JSONObject();
            options.put("name", "Stall Spot Wallet");
            options.put("description", "Add Money to Wallet");
            options.put("order_id", orderResponse.getOrderId());
            options.put("theme.color", "#FF6B6B");
            options.put("currency", "INR");
            options.put("amount", orderResponse.getAmountInPaise());
            JSONObject prefill = new JSONObject();
            prefill.put("email", studentEmail);
            prefill.put("contact", studentPhone);
            options.put("prefill", prefill);
            showLoading(false);
            co.open(activity, options);
        } catch (Exception e) {
            showLoading(false);
            Toast.makeText(activity, "Error in payment: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onPaymentSuccess(String razorpayPaymentId, PaymentData paymentData) {
        showLoading(true);
        updateWalletOnServer(razorpayPaymentId, paymentData.getOrderId(), paymentData.getSignature());
    }

    @Override
    public void onPaymentError(int code, String description, PaymentData paymentData) {
        showLoading(false);
        Toast.makeText(this, "Payment failed: " + description, Toast.LENGTH_LONG).show();
    }

    private void updateWalletOnServer(String paymentId, String orderId, String signature) {
        ApiClient.getClient().create(ApiService.class)
                .addMoneyToWallet(studentId, amountToAdd, paymentId, orderId, signature)
                .enqueue(new Callback<StatusResponse>() {
                    @Override
                    public void onResponse(Call<StatusResponse> call, Response<StatusResponse> response) {
                        showLoading(false);
                        if (response.isSuccessful() && response.body() != null && "success".equals(response.body().getStatus())) {
                            Intent intent = new Intent(Uadd_moneyActivity.this, Upayment_addedActivity.class);
                            intent.putExtra("ADDED_AMOUNT", String.valueOf(amountToAdd));
                            intent.putExtra("SOURCE_ACTIVITY", sourceActivity);
                            startActivity(intent);
                            setResult(RESULT_OK);
                            finish();
                        } else {
                            String errorMsg = "Payment successful, but failed to update wallet.";
                            if (response.errorBody() != null) {
                                try {
                                    String errorBody = response.errorBody().string();
                                    Log.e("AddMoneyError", "API Error: " + response.code() + " - " + errorBody);
                                } catch (IOException e) {
                                    Log.e("AddMoneyError", "Error parsing error body", e);
                                }
                            }
                            Toast.makeText(Uadd_moneyActivity.this, errorMsg + " Please contact support.", Toast.LENGTH_LONG).show();
                        }
                    }
                    @Override
                    public void onFailure(Call<StatusResponse> call, Throwable t) {
                        showLoading(false);
                        Log.e("AddMoneyError", "Network Failure", t);
                        Toast.makeText(Uadd_moneyActivity.this, "Network error updating wallet. Contact support.", Toast.LENGTH_LONG).show();
                    }
                });
    }

    private void showLoading(boolean show) {
        if (loadingOverlay != null) {
            if (show) {
                addMoneyPayButton.setEnabled(false);
                loadingOverlay.setVisibility(View.VISIBLE);
                Animation rotation = AnimationUtils.loadAnimation(this, R.anim.hourglass_rotation);
                loadingIcon.startAnimation(rotation);
            } else {
                loadingIcon.clearAnimation();
                loadingOverlay.setVisibility(View.GONE);
                addMoneyPayButton.setEnabled(true);
            }
        }
    }
}