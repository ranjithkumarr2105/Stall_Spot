package com.simats.foodstall;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.widget.NestedScrollView;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.button.MaterialButtonToggleGroup;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.simats.foodstall.model.LoginResponse; // Reusing LoginResponse
import com.simats.foodstall.model.StatusResponse;

import java.util.regex.Pattern;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import android.app.Activity;

public class OsignupActivity extends AppCompatActivity {

    // --- UI ELEMENT DECLARATIONS ---
    private TextInputEditText fullnameEditText, phoneEditText, passwordEditText, confirmPasswordEditText;
    private TextInputLayout fullnameLayout, phoneLayout, passwordLayout, confirmPasswordLayout;
    private Button signupBtn;
    private TextView loginText;
    private NestedScrollView scrollView;
    private MaterialButton googleSignUpButton;
    private ProgressBar progressBar;
    private MaterialButtonToggleGroup toggleButtonGroup;

    // --- PASSWORD PATTERN ---
    private static final Pattern PASSWORD_PATTERN =
            Pattern.compile("^" +
                    "(?=.*[0-9])" +         // at least 1 digit
                    "(?=.*[a-z])" +         // at least 1 lower case letter
                    "(?=.*[A-Z])" +         // at least 1 upper case letter
                    "(?=.*[@#$%^&+=!])" +    // at least 1 special character
                    "(?=\\S+$)" +           // no white spaces
                    ".{6,8}" +              // at least 6 and at most 8 characters
                    "$");

    // --- NEW: Google Sign In variables ---
    private GoogleSignInClient mGoogleSignInClient;
    private ActivityResultLauncher<Intent> googleSignInLauncher;
    private static final String TAG = "OsignupActivity";

    // SIMULATED DATABASE REMOVED

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.osignup);

        // --- Linking all UI elements ---
        fullnameEditText = findViewById(R.id.fullnameEditText);
        phoneEditText = findViewById(R.id.phoneEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        confirmPasswordEditText = findViewById(R.id.confirmPasswordEditText);
        fullnameLayout = findViewById(R.id.fullnameLayout);
        phoneLayout = findViewById(R.id.phoneLayout);
        passwordLayout = findViewById(R.id.passwordLayout);
        confirmPasswordLayout = findViewById(R.id.confirmPasswordLayout);
        signupBtn = findViewById(R.id.signupBtn);
        loginText = findViewById(R.id.loginText);
        scrollView = findViewById(R.id.scrollView);
        googleSignUpButton = findViewById(R.id.googleSignUpButton);
        progressBar = findViewById(R.id.progressBar);
        toggleButtonGroup = findViewById(R.id.toggleButtonGroup);

        // --- Configure Google Sign In ---
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        // --- Register Google Sign In Launcher ---
        googleSignInLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    progressBar.setVisibility(View.GONE);
                    googleSignUpButton.setEnabled(true);
                    signupBtn.setEnabled(true);
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        Intent data = result.getData();
                        Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
                        try {
                            GoogleSignInAccount account = task.getResult(ApiException.class);
                            Log.d(TAG, "Google Sign In Success, attempting backend auth for owner signup.");
                            authenticateWithBackend(account);
                        } catch (ApiException e) {
                            Log.w(TAG, "Google sign in failed during owner signup", e);
                            Toast.makeText(this, "Google Sign In Failed: " + e.getStatusCode(), Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Log.d(TAG, "Google Sign In Canceled/Failed during owner signup, result code: " + result.getResultCode());
                    }
                });

        // SIGNUP BUTTON LOGIC
        signupBtn.setOnClickListener(v -> {
            if (validateInputs()) {
                performOwnerRegistration(
                        fullnameEditText.getText().toString().trim(),
                        phoneEditText.getText().toString().trim(),
                        passwordEditText.getText().toString()
                );
            }
        });

        // LOGIN TEXT LISTENER (unchanged)
        loginText.setOnClickListener(v -> {
            Intent intent = new Intent(OsignupActivity.this, LoginActivity.class);
            startActivity(intent);
            overridePendingTransition(0, 0);
        });

        // TOGGLE BUTTON LISTENER (unchanged)
        toggleButtonGroup.addOnButtonCheckedListener((group, checkedId, isChecked) -> {
            if (isChecked && checkedId == R.id.userBtn) {
                Intent intent = new Intent(OsignupActivity.this, UsignupActivity.class);
                startActivity(intent);
                overridePendingTransition(0, 0);
                group.check(R.id.ownerBtn);
            }
        });

        // --- GOOGLE SIGN UP BUTTON ---
        if (googleSignUpButton != null) {
            googleSignUpButton.setOnClickListener(v -> signInWithGoogle());
        } else {
            Log.e(TAG, "googleSignUpButton is null! Check XML ID.");
        }


    }

    // --- Validation Methods (unchanged) ---
    private boolean validateFullName() { /* ... unchanged ... */ return true;}
    private boolean validatePhone() { /* ... unchanged ... */ return true;}
    private boolean validatePassword() { /* ... unchanged ... */ return true;}
    private boolean validateConfirmPassword() { /* ... unchanged ... */ return true;}
    private boolean validateInputs() { /* ... unchanged ... */ return true;}

    // --- Regular Owner Registration (unchanged) ---
    private void performOwnerRegistration(String fullName, String phone, String password) {
        progressBar.setVisibility(View.VISIBLE);
        signupBtn.setEnabled(false);
        if (googleSignUpButton != null) googleSignUpButton.setEnabled(false);

        ApiService apiService = ApiClient.getClient().create(ApiService.class);
        Call<StatusResponse> call = apiService.registerOwner(fullName, phone, password, password);

        call.enqueue(new Callback<StatusResponse>() {
            @Override
            public void onResponse(Call<StatusResponse> call, Response<StatusResponse> response) {
                progressBar.setVisibility(View.GONE);
                signupBtn.setEnabled(true);
                if (googleSignUpButton != null) googleSignUpButton.setEnabled(true);

                if (response.isSuccessful() && response.body() != null) {
                    StatusResponse statusResponse = response.body();
                    if ("success".equals(statusResponse.getStatus())) {
                        Toast.makeText(OsignupActivity.this, statusResponse.getMessage(), Toast.LENGTH_LONG).show();
                        Intent intent = new Intent(OsignupActivity.this, OstalldetailsActivity.class);
                        intent.putExtra("OWNER_PHONE", phone);
                        intent.putExtra("OWNER_PASSWORD", password);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                        finish();
                    } else {
                        Toast.makeText(OsignupActivity.this, statusResponse.getMessage(), Toast.LENGTH_LONG).show();
                    }
                } else {
                    Log.e(TAG, "Regular owner signup failed. Code: " + response.code() + ", Message: " + response.message());
                    Toast.makeText(OsignupActivity.this, "Registration failed. Server error.", Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onFailure(Call<StatusResponse> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                signupBtn.setEnabled(true);
                if (googleSignUpButton != null) googleSignUpButton.setEnabled(true);
                Log.e(TAG, "Network Error (Regular Owner Signup): " + t.getMessage(), t);
                Toast.makeText(OsignupActivity.this, "Network Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    // --- ADDED: signInWithGoogle() Method ---
    private void signInWithGoogle() {
        Log.d(TAG, "Attempting explicit sign out before sign in...");
        progressBar.setVisibility(View.VISIBLE);
        if (googleSignUpButton != null) googleSignUpButton.setEnabled(false);
        if (signupBtn != null) signupBtn.setEnabled(false);

        if (mGoogleSignInClient != null) {
            mGoogleSignInClient.signOut().addOnCompleteListener(this, task -> {
                Log.d(TAG, "Sign out complete (or failed), creating Google Sign In Intent...");
                Intent signInIntent = mGoogleSignInClient.getSignInIntent();

                Log.d(TAG, "Intent created. Launching Google Sign In UI...");
                progressBar.setVisibility(View.VISIBLE);
                if (googleSignUpButton != null) googleSignUpButton.setEnabled(false);
                if (signupBtn != null) signupBtn.setEnabled(false);

                googleSignInLauncher.launch(signInIntent);
            });
        } else {
            Log.e(TAG, "mGoogleSignInClient is null, cannot proceed with sign out/sign in.");
            progressBar.setVisibility(View.GONE);
            if (googleSignUpButton != null) googleSignUpButton.setEnabled(true);
            if (signupBtn != null) signupBtn.setEnabled(true);
            Toast.makeText(this, "Google Sign-In not initialized.", Toast.LENGTH_SHORT).show();
        }
    }
    // --- End of signInWithGoogle() Method ---


    // --- MODIFIED: Authenticate with Backend ---
    private void authenticateWithBackend(GoogleSignInAccount acct) {
        String idToken = acct.getIdToken();
        if (idToken == null) {
            Toast.makeText(this, "Failed to get Google ID token.", Toast.LENGTH_SHORT).show();
            return;
        }
        Log.d(TAG, "Got ID Token, sending to backend for owner verification/registration.");
        progressBar.setVisibility(View.VISIBLE);
        googleSignUpButton.setEnabled(false);
        signupBtn.setEnabled(false);

        ApiService apiService = ApiClient.getClient().create(ApiService.class);
        Call<LoginResponse> call = apiService.handleGoogleSignIn(idToken, "owner"); // Hint 'owner'

        call.enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                progressBar.setVisibility(View.GONE);
                googleSignUpButton.setEnabled(true);
                signupBtn.setEnabled(true);

                if (response.isSuccessful() && response.body() != null) {
                    LoginResponse loginResponse = response.body();
                    Log.d(TAG, "Backend Google Auth Response (Owner Signup): Status=" + loginResponse.getStatus() + ", Role=" + loginResponse.getRole());

                    // --- MODIFICATION START: Check for new owner status ---
                    if ("new_google_owner".equals(loginResponse.getStatus()) || "new_owner_google".equals(loginResponse.getRole())) {
                        // New owner registered via Google
                        Toast.makeText(OsignupActivity.this, loginResponse.getMessage(), Toast.LENGTH_LONG).show();
                        if (loginResponse.getData() != null && loginResponse.getData().getTempPassword() != null) {
                            // Go to ConfirmPasswordActivity
                            Intent intent = new Intent(OsignupActivity.this, ConfirmPasswordActivity.class);
                            // Pass owner's email as the identifier, studentId is irrelevant here
                            intent.putExtra("USER_ID", loginResponse.getData().getEmail());
                            intent.putExtra("TEMP_PASSWORD", loginResponse.getData().getTempPassword());
                            intent.putExtra("USER_ROLE", "owner"); // Indicate role
                            intent.putExtra("USER_NAME", loginResponse.getData().getFullname()); // Pass name for potential use
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);
                            finish();
                        } else {
                            // Data missing, fallback to login
                            Log.e(TAG, "New Google owner response missing data (email or tempPassword).");
                            Toast.makeText(OsignupActivity.this, "Registration incomplete. Please try logging in.", Toast.LENGTH_LONG).show();
                            startActivity(new Intent(OsignupActivity.this, LoginActivity.class));
                            finish();
                        }
                    } // --- MODIFICATION END ---
                    else if ("success".equals(loginResponse.getStatus())) {
                        // Existing owner logged in via Google
                        Toast.makeText(OsignupActivity.this, loginResponse.getMessage(), Toast.LENGTH_SHORT).show();
                        handleLoginResponse(loginResponse, acct); // Regular login flow
                    } else {
                        // Handle other errors from backend
                        Toast.makeText(OsignupActivity.this, loginResponse.getMessage(), Toast.LENGTH_LONG).show();
                    }
                } else {
                    Log.e(TAG, "Backend Google Auth failed (Owner Signup). Code: " + response.code() + ", Message: " + response.message());
                    Toast.makeText(OsignupActivity.this, "Backend authentication failed.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<LoginResponse> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                googleSignUpButton.setEnabled(true);
                signupBtn.setEnabled(true);
                Log.e(TAG, "Network Error (Google Auth Owner Signup): " + t.getMessage(), t);
                Toast.makeText(OsignupActivity.this, "Network Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    // --- Handler for successful EXISTING Google Sign-In (Owner) ---
    private void handleLoginResponse(LoginResponse loginResponse, GoogleSignInAccount googleAccount) {
        String role = loginResponse.getRole();
        LoginResponse.UserData userData = loginResponse.getData();
        Intent intent = null;
        SharedPreferences sharedPreferences;
        SharedPreferences.Editor editor;

        switch (role) {
            case "owner_approved":
                sharedPreferences = getSharedPreferences("owner_prefs", MODE_PRIVATE);
                editor = sharedPreferences.edit();
                if (userData != null) {
                    editor.putString("stall_id", userData.getStallId());
                    editor.putString("stall_name", userData.getStallName());
                }
                editor.putBoolean("isLoggedIn", true);
                editor.apply();
                intent = new Intent(OsignupActivity.this, OhomeActivity.class);
                break;
            case "owner_status_check":
                int stallStatus = (userData != null) ? userData.getStallStatus() : 0;
                if (stallStatus == 1) { // Approved
                    intent = new Intent(OsignupActivity.this, OapprovedActivity.class);
                    if (userData != null) intent.putExtra("STALL_ID", userData.getStallId());
                } else if (stallStatus == -1) { // Rejected
                    intent = new Intent(OsignupActivity.this, OrejectedActivity.class);
                    if (userData != null) intent.putExtra("REJECTION_REASON", userData.getRejectionReason());
                } else { // Pending or needs details
                    if (userData != null && userData.getPhonenumber() != null && userData.getStallId() == null) {
                        intent = new Intent(OsignupActivity.this, OstalldetailsActivity.class);
                        if(googleAccount != null) intent.putExtra("USER_NAME", googleAccount.getDisplayName());
                        if(userData != null) intent.putExtra("OWNER_PHONE", userData.getPhonenumber());
                    } else {
                        intent = new Intent(OsignupActivity.this, OpendingActivity.class);
                    }
                }
                break;
            default:
                Toast.makeText(this, "Login failed. Account may exist with a different role.", Toast.LENGTH_LONG).show();
                intent = new Intent(OsignupActivity.this, LoginActivity.class);
                break;
        }

        if (intent != null) {
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        }
    }
}