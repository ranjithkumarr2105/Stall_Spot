package com.simats.foodstall;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import java.util.Locale;

public class Upayment_addedActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.upayment_added);

        TextView amountAddedTextView = findViewById(R.id.amountAddedTextView);
        Button doneButton = findViewById(R.id.doneButton);

        Intent intent = getIntent();
        String amount = intent.getStringExtra("ADDED_AMOUNT");
        String source = intent.getStringExtra("SOURCE_ACTIVITY");

        if (amount != null && !amount.isEmpty()) {
            double amountValue = Double.parseDouble(amount);
            amountAddedTextView.setText(String.format(Locale.getDefault(), "₹%.2f", amountValue));
        }

        // This "smart" logic changes the button based on where the user came from
        if ("WALLET".equals(source)) {
            doneButton.setText("Back to Wallet");
            doneButton.setOnClickListener(v -> {
                Intent walletIntent = new Intent(Upayment_addedActivity.this, UwalletActivity.class);
                walletIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(walletIntent);
                finish();
            });
        } else { // Default or "PAYMENT"
            doneButton.setText("Back to Payment");
            doneButton.setOnClickListener(v -> {
                // Simply finish this screen to return to Upayment_methodActivity
                finish();
            });
        }
    }
}