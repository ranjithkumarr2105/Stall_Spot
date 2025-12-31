package com.simats.foodstall;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.text.method.PasswordTransformationMethod; // Import for password visibility
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Toast;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.simats.foodstall.model.StatusResponse;
import java.util.regex.Pattern;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OstalldetailsActivity extends AppCompatActivity {

    private TextInputEditText stallNameEditText, ownerNameEditText, phoneNumberEditText, emailEditText, addressEditText, fssaiNumberEditText, passwordEditText;
    private TextInputLayout stallNameLayout, ownerNameLayout, phoneLayout, emailLayout, addressLayout, fssaiLayout, passwordLayout;
    private Button submitButton, cancelButton;
    private FrameLayout loadingOverlay;
    private ImageView loadingIcon;
    private static final String TAG = "OstalldetailsActivity";

    private boolean isGoogleSignup = false;

    // PASSWORD PATTERN (only needed if password validation runs)
    private static final Pattern PASSWORD_PATTERN =
            Pattern.compile("^" +
                    "(?=.*[0-9])" +         // at least 1 digit
                    "(?=.*[a-z])" +         // at least 1 lower case letter
                    "(?=.*[A-Z])" +         // at least 1 upper case letter
                    "(?=.*[@#$%^&+=!])" +    // at least 1 special character
                    "(?=\\S+$)" +           // no white spaces
                    ".{6,8}" +              // at least 6 and at most 8 characters
                    "$");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ostalldetails);

        // Link EditTexts
        stallNameEditText = findViewById(R.id.stallNameEditText);
        ownerNameEditText = findViewById(R.id.ownerNameEditText);
        phoneNumberEditText = findViewById(R.id.phoneNumberEditText);
        emailEditText = findViewById(R.id.emailEditText);
        addressEditText = findViewById(R.id.addressEditText);
        fssaiNumberEditText = findViewById(R.id.fssaiNumberEditText);
        passwordEditText = findViewById(R.id.passwordEditText);

        // Link TextInputLayouts
        stallNameLayout = findViewById(R.id.stallNameLayout);
        ownerNameLayout = findViewById(R.id.ownerNameLayout);
        phoneLayout = findViewById(R.id.phoneLayout);
        emailLayout = findViewById(R.id.emailLayout);
        addressLayout = findViewById(R.id.addressLayout);
        fssaiLayout = findViewById(R.id.fssaiLayout);
        passwordLayout = findViewById(R.id.passwordLayout);

        // Link Buttons and Overlays
        submitButton = findViewById(R.id.submitButton);
        cancelButton = findViewById(R.id.cancelButton);
        loadingOverlay = findViewById(R.id.loadingOverlay);
        loadingIcon = findViewById(R.id.loadingIcon);

        Intent intent = getIntent();
        String userName = intent.getStringExtra("USER_NAME");
        String ownerPhone = intent.getStringExtra("OWNER_PHONE");
        String ownerPassword = intent.getStringExtra("OWNER_PASSWORD");
        String ownerEmail = intent.getStringExtra("OWNER_EMAIL");

        // Check if this is a Google signup
        isGoogleSignup = intent.getBooleanExtra("IS_GOOGLE_SIGNUP", false);
        // [NEW] Check if this is a resubmit
        boolean isResubmit = intent.getBooleanExtra("IS_RESUBMIT", false);


        if (userName != null && !userName.isEmpty()) {
            ownerNameEditText.setText(userName);
        }

        // Handle Phone
        if (ownerPhone != null && !ownerPhone.isEmpty()) {
            phoneNumberEditText.setText(ownerPhone);
            // [FIX] Make phone number editable for resubmit, but not for initial signup
            if (isResubmit) {
                phoneNumberEditText.setEnabled(true);
                phoneLayout.setHint("Phone Number");
            } else {
                phoneNumberEditText.setEnabled(false);
                phoneLayout.setHint("Phone Number (Verified)");
            }
        } else {
            phoneNumberEditText.setEnabled(true);
            phoneLayout.setHint("Phone Number");
        }

        // Handle Email
        if (ownerEmail != null && !ownerEmail.isEmpty()) {
            emailEditText.setText(ownerEmail);
        }


        // --- [NEW] UPDATED Password Handling ---
        passwordLayout.setVisibility(View.VISIBLE);
        passwordEditText.setVisibility(View.VISIBLE);

        if (isResubmit) {
            // This is a rejected owner resubmitting.
            passwordEditText.setEnabled(true); // Make it editable
            passwordLayout.setPasswordVisibilityToggleEnabled(true); // Show the eye icon
            passwordEditText.setTransformationMethod(new PasswordTransformationMethod()); // Hide typing

            passwordEditText.setText(""); // Ensure it's empty
            passwordLayout.setHint("Re-enter Password to Verify");
            passwordLayout.setHelperText("Please re-enter your password to submit changes.");

            // We must set isGoogleSignup to false for validation to run
            isGoogleSignup = false;

        } else {
            // This is the original signup flow (Google or Manual)
            passwordEditText.setEnabled(false); // Always disabled in this screen
            passwordLayout.setPasswordVisibilityToggleEnabled(false); // Always hide toggle
            passwordEditText.setTransformationMethod(null); // Show plain text

            if (ownerPassword != null && !ownerPassword.isEmpty()) {
                passwordEditText.setText(ownerPassword);

                if (isGoogleSignup) {
                    // Hint for Google users
                    passwordLayout.setHint("Password (Verified via Google)");
                    passwordLayout.setHelperText("This is your app password. You can log in with this or Google.");
                } else {
                    // Hint for Manual signup users
                    passwordLayout.setHint("Confirm Your Password (Verified)");
                    passwordLayout.setHelperText(null);
                }
            } else {
                // This is an error state (e.g., manual signup didn't pass password)
                passwordLayout.setHint("Error: Password not found");
                passwordEditText.setText("");
                Log.e(TAG, "OwnerPassword was not passed to OstalldetailsActivity!");
            }
        }
        // --- End of Password Handling ---

        submitButton.setOnClickListener(v -> {
            String currentPhone = phoneNumberEditText.getText().toString().trim();
            String currentEmail = emailEditText.getText().toString().trim();

            if (validateAllInputs(currentPhone, currentEmail)) {
                String stallName = stallNameEditText.getText().toString().trim();
                String ownerName = ownerNameEditText.getText().toString().trim();
                String address = addressEditText.getText().toString().trim();
                String fssai = fssaiNumberEditText.getText().toString().trim();

                // Password is only sent for manual signups (and resubmits)
                String passwordToSend = passwordEditText.getText().toString();

                performStallSubmission(stallName, ownerName, currentPhone, currentEmail, address, fssai, passwordToSend);
            }
        });

        cancelButton.setOnClickListener(v -> {
            Intent cancelIntent = new Intent(OstalldetailsActivity.this, LoginActivity.class);
            startActivity(cancelIntent);
            overridePendingTransition(0, 0);
            finish();
        });
    }

    // --- performStallSubmission (Updated signature) ---
    private void performStallSubmission(String stallName, String ownerName, String phone, String email, String address, String fssai, String password) {
        startLoadingAnimation();
        Log.d(TAG, "Submitting Stall Details - Phone: " + phone + ", Email: " + email);

        // Determine signup type to send to server
        String signupType = isGoogleSignup ? "google" : "manual";

        ApiService apiService = ApiClient.getClient().create(ApiService.class);
        // Pass signupType to the API call
        Call<StatusResponse> call = apiService.submitStallDetails(stallName, ownerName, phone, email, address, fssai, password, signupType);

        call.enqueue(new Callback<StatusResponse>() {
            @Override
            public void onResponse(Call<StatusResponse> call, Response<StatusResponse> response) {
                stopLoadingAnimation();
                if (response.isSuccessful() && response.body() != null) {
                    StatusResponse apiResponse = response.body();
                    Toast.makeText(OstalldetailsActivity.this, apiResponse.getMessage(), Toast.LENGTH_LONG).show();
                    if ("success".equals(apiResponse.getStatus())) {
                        Intent intent = new Intent(OstalldetailsActivity.this, OpendingActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                        finish();
                    }
                } else {
                    Log.e(TAG, "Stall Submission Failed - Code: " + response.code() + ", Message: " + response.message());
                    Toast.makeText(OstalldetailsActivity.this, "Submission failed. Server error.", Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onFailure(Call<StatusResponse> call, Throwable t) {
                stopLoadingAnimation();
                Log.e(TAG, "Network Error (Stall Submit): " + t.getMessage(), t);
                Toast.makeText(OstalldetailsActivity.this, "Network Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    // --- VALIDATION METHODS ---

    // Updated validatePhone
    private boolean validatePhone(String phone) {
        if (phoneNumberEditText.isEnabled()) {
            if (phone.isEmpty()) {
                phoneLayout.setError("Phone number is required");
                return false;
            } else if (!phone.matches("^[0-9]{10}$")) {
                phoneLayout.setError("Must be exactly 10 digits");
                return false;
            }
        }
        phoneLayout.setError(null);
        return true;
    }

    // Updated validateEmail
    private boolean validateEmail(String email) {
        if (email.isEmpty()) {
            emailLayout.setError("Email is required");
            return false;
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailLayout.setError("Please enter a valid email format");
            return false;
        } else if (!email.endsWith("@gmail.com")) {
            emailLayout.setError("Only @gmail.com addresses are allowed");
            return false;
        } else {
            emailLayout.setError(null);
            return true;
        }
    }


    // Updated validatePassword
    private boolean validatePassword() {
        // Validation is only relevant for MANUAL signup path (and resubmits).
        if (!isGoogleSignup) {
            String password = passwordEditText.getText().toString();
            if (password.isEmpty()) {
                passwordLayout.setError("Password confirmation missing");
                return false;
            } else if (!PASSWORD_PATTERN.matcher(password).matches()) {
                // This indicates an issue from the previous screen if it occurs
                passwordLayout.setError("Password format incorrect");
                Log.w(TAG, "Password validation failed for manually entered password in Ostalldetails!");
                return false;
            }
        }
        // If Google signup, or manual validation passes
        passwordLayout.setError(null);
        return true;
    }

    // Updated validateAllInputs
    private boolean validateAllInputs(String currentPhone, String currentEmail) {
        boolean isStallNameValid = validateStallName();
        boolean isOwnerNameValid = validateOwnerName();
        boolean isPhoneValid = validatePhone(currentPhone);
        boolean isEmailValid = validateEmail(currentEmail);
        boolean isAddressValid = validateAddress();
        boolean isFssaiValid = validateFssai();
        boolean isPasswordValid = validatePassword(); // Validation only runs for manual/resubmit

        return isStallNameValid && isOwnerNameValid && isPhoneValid && isEmailValid &&
                isAddressValid && isFssaiValid && isPasswordValid;
    }

    // --- Unchanged Validation Methods ---
    private boolean validateStallName() {
        String stallName = stallNameEditText.getText().toString().trim();
        if (stallName.isEmpty()) {
            stallNameLayout.setError("Stall name is required");
            return false;
        } else if (!stallName.matches("^[a-zA-Z\\s]+$")) {
            stallNameLayout.setError("Only alphabets and spaces are allowed");
            return false;
        } else {
            stallNameLayout.setError(null);
            return true;
        }
    }
    private boolean validateOwnerName() {
        String ownerName = ownerNameEditText.getText().toString().trim();
        if (ownerName.isEmpty()) {
            ownerNameLayout.setError("Owner name is required");
            return false;
        } else if (!ownerName.matches("^[a-zA-Z\\s]+$")) {
            ownerNameLayout.setError("Only alphabets and spaces are allowed");
            return false;
        } else {
            ownerNameLayout.setError(null);
            return true;
        }
    }
    private boolean validateAddress() {
        String address = addressEditText.getText().toString().trim();
        if (address.isEmpty()) {
            addressLayout.setError("Full address is required");
            return false;
        } else {
            addressLayout.setError(null);
            return true;
        }
    }
    private boolean validateFssai() {
        String fssai = fssaiNumberEditText.getText().toString().trim();
        if (fssai.isEmpty()) {
            fssaiLayout.setError("FSSAI number is required");
            return false;
        } else if (!fssai.matches("^[0-9]{14}$")) {
            fssaiLayout.setError("Must be exactly 14 digits");
            return false;
        } else {
            fssaiLayout.setError(null);
            return true;
        }
    }


    // --- LOADING ANIMATION (Unchanged) ---
    private void startLoadingAnimation() {
        loadingOverlay.setVisibility(View.VISIBLE);
        Animation rotation = AnimationUtils.loadAnimation(this, R.anim.hourglass_rotation);
        loadingIcon.startAnimation(rotation);
        submitButton.setEnabled(false);
        cancelButton.setEnabled(false);
    }
    private void stopLoadingAnimation() {
        loadingIcon.clearAnimation();
        loadingOverlay.setVisibility(View.GONE);
        submitButton.setEnabled(true);
        cancelButton.setEnabled(true);
    }
}