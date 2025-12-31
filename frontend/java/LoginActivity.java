package com.simats.foodstall;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.simats.foodstall.model.LoginResponse;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {

    private TextInputEditText idEditText;
    private TextInputEditText passwordEditText;
    private TextView signupText, forgotPasswordText;
    private Button loginBtn;
    private MaterialButton googleSignInButton;
    private ProgressBar progressBar;
    private GoogleSignInClient mGoogleSignInClient;
    private ActivityResultLauncher<Intent> googleSignInLauncher;
    private static final String TAG = "LoginActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);

        idEditText = findViewById(R.id.idEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        loginBtn = findViewById(R.id.loginBtn);
        signupText = findViewById(R.id.signupText);
        googleSignInButton = findViewById(R.id.googleSignInButton);
        forgotPasswordText = findViewById(R.id.forgotPasswordText);
        progressBar = findViewById(R.id.progressBar);

        // Configure Google Sign In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        googleSignInLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    Log.d(TAG, "Google Sign In Result Received. Result Code: " + result.getResultCode());
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        Intent data = result.getData();
                        Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
                        try {
                            GoogleSignInAccount account = task.getResult(ApiException.class);
                            Log.d(TAG, "Google Account obtained successfully. Email: " + (account != null ? account.getEmail() : "null"));
                            if (account != null) {
                                authenticateWithBackend(account);
                            } else {
                                Log.e(TAG, "GoogleSignInAccount is null after successful result.");
                                Toast.makeText(this, "Google Sign In Failed (Null Account)", Toast.LENGTH_SHORT).show();
                                resetButtonsAndProgress();
                            }
                        } catch (ApiException e) {
                            Log.e(TAG, "Google sign in failed in launcher", e);
                            Toast.makeText(this, "Google Sign In Failed: " + e.getStatusCode(), Toast.LENGTH_SHORT).show();
                            resetButtonsAndProgress();
                        }
                    } else {
                        Log.d(TAG, "Google Sign In Canceled or Failed, result code: " + result.getResultCode());
                        resetButtonsAndProgress();
                    }
                });
        googleSignInButton.setOnClickListener(v -> signInWithGoogle());

        loginBtn.setOnClickListener(v -> {
            String userId = idEditText.getText().toString().trim();
            String password = passwordEditText.getText().toString().trim();

            if (userId.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Please enter ID and password", Toast.LENGTH_SHORT).show();
                return;
            }
            // --- UPDATED ADMIN PASSWORD LOGIC HERE ---
            if (userId.equals("Admin") && password.equals("Ad@123")) {
                Toast.makeText(this, "Logging in as Admin...", Toast.LENGTH_SHORT).show();
                SharedPreferences sharedPreferences = getSharedPreferences("StallSpotPrefs", MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("LOGGED_IN_ROLE", "admin");
                editor.apply();
                Intent intent = new Intent(LoginActivity.this, AhomeActivity.class);
                startActivity(intent);
                overridePendingTransition(0, 0); // Added transition
                finish();
            } else {
                performLogin(userId, password);
            }
        });

        signupText.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, UsignupActivity.class);
            startActivity(intent);
            overridePendingTransition(0, 0); // Added transition
        });

        forgotPasswordText.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, ForgotPasswordActivity.class);
            startActivity(intent);
            overridePendingTransition(0, 0); // Added transition
        });
    }

    // --- Regular Email/Password Login ---
    private void performLogin(String identifier, String password) {
        setLoadingState(true);

        ApiClient.getClient().create(ApiService.class).loginUser(identifier, password).enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                setLoadingState(false);
                if (response.isSuccessful() && response.body() != null) {
                    LoginResponse loginResponse = response.body();
                    if ("success".equals(loginResponse.getStatus())) {
                        Toast.makeText(LoginActivity.this, loginResponse.getMessage(), Toast.LENGTH_SHORT).show();
                        handleLoginResponse(loginResponse);
                    } else {
                        Toast.makeText(LoginActivity.this, loginResponse.getMessage(), Toast.LENGTH_LONG).show();
                    }
                } else {
                    Log.e(TAG, "Regular Login failed. Code: " + response.code() + ", Message: " + response.message());
                    Toast.makeText(LoginActivity.this, "Login failed. Server error or Invalid Credentials.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<LoginResponse> call, Throwable t) {
                setLoadingState(false);
                Log.e(TAG, "Network Error (Regular Login): " + t.getMessage(), t);
                Toast.makeText(LoginActivity.this, "Network Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    // --- Starts the Google Sign In flow ---
    private void signInWithGoogle() {
        Log.d(TAG, "Attempting explicit sign out before sign in...");
        setLoadingState(true);

        if (mGoogleSignInClient != null) {
            mGoogleSignInClient.signOut().addOnCompleteListener(this, task -> {
                Log.d(TAG, "Sign out complete (or failed), creating Google Sign In Intent...");
                Intent signInIntent = mGoogleSignInClient.getSignInIntent();
                Log.d(TAG, "Intent created. Launching Google Sign In UI...");
                googleSignInLauncher.launch(signInIntent);
            });
        } else {
            Log.e(TAG, "mGoogleSignInClient is null during signInWithGoogle.");
            setLoadingState(false);
            Toast.makeText(this, "Google Sign-In not initialized.", Toast.LENGTH_SHORT).show();
        }
    }

    // --- Authenticate with Backend using Google ID Token ---
    private void authenticateWithBackend(GoogleSignInAccount acct) {
        String idToken = acct.getIdToken();
        if (idToken == null) {
            Log.e(TAG, "ID Token is NULL after Google Sign In.");
            Toast.makeText(this, "Failed to get Google ID token.", Toast.LENGTH_SHORT).show();
            resetButtonsAndProgress();
            return;
        }
        Log.d(TAG, "ID Token acquired (length " + idToken.length() + "). Sending to backend...");
        setLoadingState(true);

        ApiService apiService = ApiClient.getClient().create(ApiService.class);
        Call<LoginResponse> call = apiService.handleGoogleSignIn(idToken, "user");

        Log.d(TAG, "Calling backend API (handleGoogleSignIn)...");
        call.enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                setLoadingState(false);
                Log.d(TAG, "Backend API Response - Code: " + response.code() + ", Successful: " + response.isSuccessful());

                if (response.isSuccessful() && response.body() != null) {
                    LoginResponse loginResponse = response.body();
                    Log.d(TAG, "Backend Google Auth Response Status: " + loginResponse.getStatus() + ", Role: " + loginResponse.getRole());

                    String status = loginResponse.getStatus();
                    String role = loginResponse.getRole();

                    if ("success".equals(status)) {
                        Toast.makeText(LoginActivity.this, loginResponse.getMessage(), Toast.LENGTH_SHORT).show();
                        handleLoginResponse(loginResponse);
                    } else if ("new_google_user".equals(status) || "new_user_google".equals(role)) {
                        Toast.makeText(LoginActivity.this, loginResponse.getMessage(), Toast.LENGTH_LONG).show();
                        goToConfirmPasswordActivity(loginResponse, "user");
                    } else if ("google_owner_registered".equals(status) || "new_owner_google".equals(role)) {
                        Toast.makeText(LoginActivity.this, loginResponse.getMessage(), Toast.LENGTH_LONG).show();
                        goToConfirmPasswordActivity(loginResponse, "owner");
                    } else {
                        Toast.makeText(LoginActivity.this, loginResponse.getMessage(), Toast.LENGTH_LONG).show();
                    }
                } else {
                    Log.e(TAG, "Backend Google Auth failed. Code: " + response.code() + ", Message: " + response.message());
                    Toast.makeText(LoginActivity.this, "Backend authentication failed.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<LoginResponse> call, Throwable t) {
                setLoadingState(false);
                Log.e(TAG, "Backend API Call Failed (Google Auth): " + t.getMessage(), t);
                Toast.makeText(LoginActivity.this, "Network Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    // --- CENTRALIZED LOGIN RESPONSE HANDLER ---
    private void handleLoginResponse(LoginResponse loginResponse) {
        String role = loginResponse.getRole();
        if (role == null) {
            Toast.makeText(this, "Login failed: Role not specified.", Toast.LENGTH_SHORT).show();
            return;
        }

        LoginResponse.UserData userData = loginResponse.getData();
        // Updated the data check to be more lenient for new roles
        if (userData == null && !role.equals("admin")) {
            Toast.makeText(LoginActivity.this, "User/Owner data missing.", Toast.LENGTH_SHORT).show();
            return;
        }

        Intent intent = null;
        SharedPreferences sharedPreferences;
        SharedPreferences.Editor editor;

        switch (role) {
            case "student":
                sharedPreferences = getSharedPreferences("StallSpotPrefs", MODE_PRIVATE);
                editor = sharedPreferences.edit();
                if (userData != null) {
                    editor.putString("STUDENT_ID", userData.getStudentId());
                    editor.putString("USER_NAME", userData.getFullname());
                }
                editor.putString("LOGGED_IN_ROLE", "student");
                editor.apply();
                intent = new Intent(LoginActivity.this, UhomeActivity.class);
                break;

            case "admin":
                sharedPreferences = getSharedPreferences("StallSpotPrefs", MODE_PRIVATE);
                editor = sharedPreferences.edit();
                editor.putString("LOGGED_IN_ROLE", "admin");
                editor.apply();
                intent = new Intent(LoginActivity.this, AhomeActivity.class);
                break;

            case "owner_approved":
                sharedPreferences = getSharedPreferences("owner_prefs", MODE_PRIVATE);
                editor = sharedPreferences.edit();
                if (userData != null) {
                    editor.putString("stall_id", userData.getStallId());
                    editor.putString("stall_name", userData.getStallName());
                }
                editor.putBoolean("isLoggedIn", true);
                editor.apply();
                intent = new Intent(LoginActivity.this, OhomeActivity.class);
                break;

            case "owner_agreement_pending":
                // [FIX] Ensure userData is not null before accessing it
                if (userData != null && userData.getPhonenumber() != null && userData.getStallId() != null && userData.getStallName() != null) {
                    intent = new Intent(LoginActivity.this, AgreementActivity.class);
                    // [FIX] Pass all three required extras
                    intent.putExtra("OWNER_PHONE", userData.getPhonenumber());
                    intent.putExtra("STALL_ID", userData.getStallId());
                    intent.putExtra("STALL_NAME", userData.getStallName());
                } else {
                    Toast.makeText(this, "Error: Cannot proceed without owner phone, stall ID, or stall name.", Toast.LENGTH_LONG).show();
                    Log.e(TAG, "owner_agreement_pending missing data: phone=" + (userData != null ? userData.getPhonenumber():"null") + ", id=" + (userData != null ? userData.getStallId():"null") + ", name=" + (userData != null ? userData.getStallName():"null"));
                    return; // Stop processing if data is missing
                }
                break;

            // Case for your requested flow
            case "owner_approval_pending_view":
                if (userData != null && userData.getStallId() != null) {
                    intent = new Intent(LoginActivity.this, OapprovedActivity.class);
                    intent.putExtra("STALL_ID", userData.getStallId());
                } else {
                    Toast.makeText(this, "Error: Stall ID not found for approval.", Toast.LENGTH_SHORT).show();
                    return;
                }
                break;

            case "owner_status_check":
                // This block is now for pending or rejected
                int stallStatus = (userData != null) ? userData.getStallStatus() : 0;

                if (stallStatus == -1) {
                    // Go to OrejectedActivity and pass all data
                    intent = new Intent(LoginActivity.this, OrejectedActivity.class);
                    // [FIX] Add null checks for safety
                    intent.putExtra("REJECTION_REASON", userData != null ? userData.getRejectionReason() : null);
                    intent.putExtra("OWNER_PHONE", userData != null ? userData.getPhonenumber() : null);
                    intent.putExtra("OWNER_EMAIL", userData != null ? userData.getEmail() : null);
                    intent.putExtra("USER_NAME", userData != null ? userData.getFullname() : null);

                } else {
                    // Status is 0 (Pending) or null
                    if (userData != null && userData.getPhonenumber() != null && userData.getStallId() == null) {
                        // Pass all data to OstalldetailsActivity
                        intent = new Intent(LoginActivity.this, OstalldetailsActivity.class);
                        intent.putExtra("OWNER_PHONE", userData.getPhonenumber());
                        intent.putExtra("OWNER_EMAIL", userData.getEmail());
                        intent.putExtra("USER_NAME", userData.getFullname());
                        intent.putExtra("IS_GOOGLE_SIGNUP", false); // This is a manual login
                    } else {
                        // Details are submitted, but still pending
                        intent = new Intent(LoginActivity.this, OpendingActivity.class);
                    }
                }
                break;

            default:
                Toast.makeText(LoginActivity.this, "Unknown role: " + role, Toast.LENGTH_SHORT).show();
                return;
        }

        if (intent != null) {
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            overridePendingTransition(0, 0); // Added transition
            finish();
        }
    }

    // --- Navigate to ConfirmPasswordActivity ---
    private void goToConfirmPasswordActivity(LoginResponse loginResponse, String userRole) {
        if (loginResponse.getData() == null || loginResponse.getData().getTempPassword() == null) {
            Log.e(TAG, "New Google " + userRole + " response missing data (ID or tempPassword).");
            Toast.makeText(LoginActivity.this, "Registration incomplete. Please try again.", Toast.LENGTH_LONG).show();
            return;
        }

        Intent intent = new Intent(LoginActivity.this, ConfirmPasswordActivity.class);
        String identifier = "user".equals(userRole) ? loginResponse.getData().getStudentId() : loginResponse.getData().getEmail();
        intent.putExtra("USER_ID", identifier);
        intent.putExtra("TEMP_PASSWORD", loginResponse.getData().getTempPassword());
        intent.putExtra("USER_ROLE", userRole);
        intent.putExtra("USER_NAME", loginResponse.getData().getFullname());

        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        overridePendingTransition(0, 0); // Added transition
        finish();
    }

    // --- Helper to manage loading state ---
    private void setLoadingState(boolean isLoading) {
        if (isLoading) {
            progressBar.setVisibility(View.VISIBLE);
            loginBtn.setEnabled(false);
            googleSignInButton.setEnabled(false);
        } else {
            progressBar.setVisibility(View.GONE);
            loginBtn.setEnabled(true);
            googleSignInButton.setEnabled(true);
        }
    }

    // --- Helper to reset UI after Google Sign In failure/cancel ---
    private void resetButtonsAndProgress() {
        progressBar.setVisibility(View.GONE);
        googleSignInButton.setEnabled(true);
        loginBtn.setEnabled(true);
    }
}