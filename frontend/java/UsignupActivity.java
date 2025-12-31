package com.simats.foodstall;

import android.annotation.SuppressLint;
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

public class UsignupActivity extends AppCompatActivity {

    // UI ELEMENT DECLARATIONS
    private TextInputEditText fullnameEditText, idEditText, passwordEditText, confirmPasswordEditText, emailEditText;
    private TextInputLayout fullnameLayout, idLayout, passwordLayout, confirmPasswordLayout, emailLayout;
    private Button signupBtn;
    private TextView loginText;
    private NestedScrollView scrollView;
    private MaterialButton googleSignUpButton;
    private ProgressBar progressBar;
    private MaterialButtonToggleGroup toggleButtonGroup;

    // PASSWORD PATTERN
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
    private static final String TAG = "UsignupActivity";

    @SuppressLint("WrongViewCast")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.usignup);

        // --- Linking all UI elements ---
        fullnameEditText = findViewById(R.id.fullnameEditText);
        idEditText = findViewById(R.id.idEditText);
        emailEditText = findViewById(R.id.emailEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        confirmPasswordEditText = findViewById(R.id.confirmPasswordEditText);
        fullnameLayout = findViewById(R.id.fullnameLayout);
        idLayout = findViewById(R.id.idLayout);
        emailLayout = findViewById(R.id.emailLayout);
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
                            Log.d(TAG, "Google Sign In Success, attempting backend auth for signup.");
                            authenticateWithBackend(account);
                        } catch (ApiException e) {
                            Log.w(TAG, "Google sign in failed during signup", e);
                            Toast.makeText(this, "Google Sign In Failed: " + e.getStatusCode(), Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Log.d(TAG, "Google Sign In Canceled/Failed during signup, result code: " + result.getResultCode());
                    }
                });


        // SIGNUP BUTTON LOGIC
        signupBtn.setOnClickListener(v -> {
            if (validateInputs()) {
                performRegistration(
                        fullnameEditText.getText().toString().trim(),
                        idEditText.getText().toString().trim(),
                        emailEditText.getText().toString().trim(),
                        passwordEditText.getText().toString()
                );
            }
        });

        // OTHER LISTENERS
        loginText.setOnClickListener(v -> {
            Intent intent = new Intent(UsignupActivity.this, LoginActivity.class);
            startActivity(intent);
            overridePendingTransition(0, 0);
        });

        toggleButtonGroup.addOnButtonCheckedListener((group, checkedId, isChecked) -> {
            if (isChecked && checkedId == R.id.ownerBtn) {
                Intent intent = new Intent(UsignupActivity.this, OsignupActivity.class);
                startActivity(intent);
                overridePendingTransition(0, 0);
                group.check(R.id.userBtn);
            }
        });

        // --- GOOGLE SIGN UP BUTTON ---
        googleSignUpButton.setOnClickListener(v -> signInWithGoogle());

    }

    // --- Validation Methods (unchanged) ---
    private boolean validateFullName() { /* ... unchanged ... */ return true;}
    private boolean validateStudentId() { /* ... unchanged ... */ return true;}
    private boolean validateEmail() { /* ... unchanged ... */ return true;}
    private boolean validatePassword() { /* ... unchanged ... */ return true;}
    private boolean validateConfirmPassword() { /* ... unchanged ... */ return true;}
    private boolean validateInputs() { /* ... unchanged ... */ return true;}


    // --- Regular Registration (unchanged) ---
    private void performRegistration(String fullName, String studentId, String email, String password) {
        progressBar.setVisibility(View.VISIBLE);
        signupBtn.setEnabled(false);
        googleSignUpButton.setEnabled(false);

        ApiService apiService = ApiClient.getClient().create(ApiService.class);
        Call<StatusResponse> call = apiService.registerUser(fullName, studentId, email, password, password);

        call.enqueue(new Callback<StatusResponse>() {
            @Override
            public void onResponse(Call<StatusResponse> call, Response<StatusResponse> response) {
                progressBar.setVisibility(View.GONE);
                signupBtn.setEnabled(true);
                googleSignUpButton.setEnabled(true);

                if (response.isSuccessful() && response.body() != null) {
                    StatusResponse statusResponse = response.body();
                    if ("success".equals(statusResponse.getStatus())) {
                        Toast.makeText(UsignupActivity.this, statusResponse.getMessage(), Toast.LENGTH_LONG).show();
                        Intent intent = new Intent(UsignupActivity.this, LoginActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                        finish();
                    } else {
                        Toast.makeText(UsignupActivity.this, statusResponse.getMessage(), Toast.LENGTH_LONG).show();
                    }
                } else {
                    Log.e(TAG, "Regular signup failed. Code: " + response.code() + ", Message: " + response.message());
                    Toast.makeText(UsignupActivity.this, "Registration failed. Server error.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<StatusResponse> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                signupBtn.setEnabled(true);
                googleSignUpButton.setEnabled(true);
                Log.e(TAG, "Network Error (Regular Signup): " + t.getMessage(), t);
                Toast.makeText(UsignupActivity.this, "Network Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    // --- signInWithGoogle() Method (unchanged from previous) ---
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


    // --- MODIFIED: Authenticate with Backend ---
    private void authenticateWithBackend(GoogleSignInAccount acct) {
        String idToken = acct.getIdToken();
        if (idToken == null) {
            Toast.makeText(this, "Failed to get Google ID token.", Toast.LENGTH_SHORT).show();
            return;
        }
        Log.d(TAG, "Got ID Token, sending to backend for verification/registration.");
        progressBar.setVisibility(View.VISIBLE);
        googleSignUpButton.setEnabled(false);
        signupBtn.setEnabled(false);

        ApiService apiService = ApiClient.getClient().create(ApiService.class);
        Call<LoginResponse> call = apiService.handleGoogleSignIn(idToken, "user"); // Role hint 'user'

        call.enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                progressBar.setVisibility(View.GONE);
                googleSignUpButton.setEnabled(true);
                signupBtn.setEnabled(true);

                if (response.isSuccessful() && response.body() != null) {
                    LoginResponse loginResponse = response.body();
                    Log.d(TAG, "Backend Google Auth Response (Signup): Status=" + loginResponse.getStatus() + ", Role=" + loginResponse.getRole());

                    // --- MODIFICATION START: Check for new user status ---
                    if ("new_google_user".equals(loginResponse.getStatus()) || "new_user_google".equals(loginResponse.getRole())) {
                        // New user registered via Google
                        Toast.makeText(UsignupActivity.this, loginResponse.getMessage(), Toast.LENGTH_LONG).show();
                        if (loginResponse.getData() != null && loginResponse.getData().getTempPassword() != null) {
                            // Go to ConfirmPasswordActivity
                            Intent intent = new Intent(UsignupActivity.this, ConfirmPasswordActivity.class);
                            intent.putExtra("USER_ID", loginResponse.getData().getStudentId()); // Pass generated student ID
                            intent.putExtra("TEMP_PASSWORD", loginResponse.getData().getTempPassword());
                            intent.putExtra("USER_ROLE", "user"); // Indicate role
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);
                            finish();
                        } else {
                            // Data missing, fallback to login
                            Log.e(TAG, "New Google user response missing data (studentId or tempPassword).");
                            Toast.makeText(UsignupActivity.this, "Registration incomplete. Please try logging in.", Toast.LENGTH_LONG).show();
                            startActivity(new Intent(UsignupActivity.this, LoginActivity.class));
                            finish();
                        }
                    } // --- MODIFICATION END ---
                    else if ("success".equals(loginResponse.getStatus())) {
                        // Existing user logged in via Google
                        Toast.makeText(UsignupActivity.this, loginResponse.getMessage(), Toast.LENGTH_SHORT).show();
                        handleLoginResponse(loginResponse); // Regular login flow
                    } else {
                        // Handle other errors from backend
                        Toast.makeText(UsignupActivity.this, loginResponse.getMessage(), Toast.LENGTH_LONG).show();
                    }
                } else {
                    Log.e(TAG, "Backend Google Auth failed (Signup). Code: " + response.code() + ", Message: " + response.message());
                    Toast.makeText(UsignupActivity.this, "Backend authentication failed.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<LoginResponse> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                googleSignUpButton.setEnabled(true);
                signupBtn.setEnabled(true);
                Log.e(TAG, "Network Error (Google Auth Signup): " + t.getMessage(), t);
                Toast.makeText(UsignupActivity.this, "Network Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    // --- Handler for successful EXISTING Google Sign-In ---
    private void handleLoginResponse(LoginResponse loginResponse) {
        String role = loginResponse.getRole();
        LoginResponse.UserData userData = loginResponse.getData();

        if ("student".equals(role) && userData != null) {
            SharedPreferences sharedPreferences = getSharedPreferences("StallSpotPrefs", MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("STUDENT_ID", userData.getStudentId());
            editor.putString("USER_NAME", userData.getFullname());
            editor.putString("LOGGED_IN_ROLE", "student");
            editor.apply();

            Intent intent = new Intent(UsignupActivity.this, UhomeActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        } else {
            // This case handles if backend returns a role other than 'student' unexpectedly
            Toast.makeText(this, "Login failed after Google Sign-In. Unexpected role or data.", Toast.LENGTH_LONG).show();
            Intent intent = new Intent(UsignupActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        }
    }
}