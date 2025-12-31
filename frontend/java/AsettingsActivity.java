package com.simats.foodstall;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;

import com.bumptech.glide.Glide;
import com.simats.foodstall.model.AdminProfileResponse;
import com.simats.foodstall.model.StatusResponse;

import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class AsettingsActivity extends AppCompatActivity {

    private CircleImageView profileImage;
    private TextView userNameTextView;
    // Use the hardcoded ID consistently for API calls and Change Password intent
    private static final String ADMIN_ID = "admin";
    private static final String TAG = "AsettingsActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.asettings);

        profileImage = findViewById(R.id.profileImage);
        userNameTextView = findViewById(R.id.userName);

        LinearLayout helpSupportLayout = findViewById(R.id.helpSupportLayout);
        LinearLayout privacyLayout = findViewById(R.id.privacyLayout);
        LinearLayout changePasswordLayout = findViewById(R.id.changePasswordLayout);
        Button logoutButton = findViewById(R.id.logoutButton);
        Button deleteAccountButton = findViewById(R.id.deleteAccountButton);

        findViewById(R.id.backButton).setOnClickListener(v -> onBackPressed());

        findViewById(R.id.editProfileLayout).setOnClickListener(v -> {
            startActivity(new Intent(this, AprofileActivity.class));
        });

        changePasswordLayout.setOnClickListener(v -> {
            Intent intent = new Intent(AsettingsActivity.this, UchangepasswordActivity.class);
            intent.putExtra("USER_ROLE", "ADMIN"); // Correct role
            // [FIX] Pass the hardcoded ADMIN_ID as the identifier
            intent.putExtra("USER_IDENTIFIER", ADMIN_ID);
            startActivity(intent);
        });

        helpSupportLayout.setOnClickListener(v -> {
            startActivity(new Intent(this, AhelpActivity.class));
        });

        privacyLayout.setOnClickListener(v -> {
            startActivity(new Intent(this, Aprivacy_policyActivity.class));
        });

        logoutButton.setOnClickListener(v -> {
            showLogoutConfirmationDialog();
        });

        deleteAccountButton.setOnClickListener(v -> {
            showDeleteConfirmationDialog();
        });

        loadAdminProfile();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Reload profile info in case it was changed in AprofileActivity
        loadAdminProfile();
    }

    private void loadAdminProfile() {
        // Show placeholder while loading
        userNameTextView.setText("Loading...");
        profileImage.setImageResource(R.drawable.editprofile);

        ApiService apiService = ApiClient.getClient().create(ApiService.class);
        apiService.getAdminProfile(ADMIN_ID).enqueue(new Callback<AdminProfileResponse>() {
            @Override
            public void onResponse(@NonNull Call<AdminProfileResponse> call, @NonNull Response<AdminProfileResponse> response) {
                if (response.isSuccessful() && response.body() != null && "success".equals(response.body().getStatus())) {
                    AdminProfileResponse.AdminData data = response.body().getData();
                    if (data != null) {
                        userNameTextView.setText(data.getFullname()); // Set the actual name

                        String photoUrl = null;
                        if (data.getProfilePhoto() != null && !data.getProfilePhoto().isEmpty()) {
                            photoUrl = ApiClient.BASE_URL + "uploads/" + data.getProfilePhoto();
                            Log.d(TAG, "Loading admin profile image from URL: " + photoUrl);
                        } else {
                            Log.d(TAG, "Admin profile photo is null or empty in API response.");
                        }

                        Glide.with(AsettingsActivity.this)
                                .load(photoUrl)
                                .placeholder(R.drawable.editprofile)
                                .error(R.drawable.editprofile)
                                .into(profileImage);

                        // Save updated name and URL back to SharedPreferences (use specific keys)
                        SharedPreferences sharedPreferences = getSharedPreferences("StallSpotPrefs", MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putString("ADMIN_NAME", data.getFullname()); // Use ADMIN_NAME key
                        editor.putString("ADMIN_PROFILE_URL", data.getProfilePhoto()); // Use ADMIN_PROFILE_URL key
                        editor.apply();

                    } else {
                        Log.w(TAG, "API response successful, but admin data is null.");
                        userNameTextView.setText("Admin Name"); // Fallback default
                        profileImage.setImageResource(R.drawable.editprofile);
                    }
                } else {
                    Log.e(TAG, "Failed to load admin profile. Code: " + response.code() + ", Message: " + response.message());
                    // Attempt to load from prefs as fallback
                    SharedPreferences prefs = getSharedPreferences("StallSpotPrefs", MODE_PRIVATE);
                    userNameTextView.setText(prefs.getString("ADMIN_NAME", "Admin Name"));
                    profileImage.setImageResource(R.drawable.editprofile);
                    Toast.makeText(AsettingsActivity.this, "Failed to load profile.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<AdminProfileResponse> call, @NonNull Throwable t) {
                Log.e(TAG, "Network error loading admin profile", t);
                // Attempt to load from prefs as fallback
                SharedPreferences prefs = getSharedPreferences("StallSpotPrefs", MODE_PRIVATE);
                userNameTextView.setText(prefs.getString("ADMIN_NAME", "Admin Name"));
                profileImage.setImageResource(R.drawable.editprofile);
                Toast.makeText(AsettingsActivity.this, "Network error. Could not load profile.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showLogoutConfirmationDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Logout")
                .setMessage("Are you sure you want to logout?")
                .setPositiveButton("Yes", (dialog, which) -> {
                    SharedPreferences sharedPreferences = getSharedPreferences("StallSpotPrefs", MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.clear();
                    editor.apply();

                    Toast.makeText(this, "Logged out successfully", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(AsettingsActivity.this, LoginActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    finish();
                })
                .setNegativeButton("No", null)
                .show();
    }

    private void showDeleteConfirmationDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Delete Account")
                .setMessage("Are you absolutely sure you want to delete your admin account? This action is permanent and cannot be undone.")
                .setPositiveButton("Delete", (dialog, which) -> {
                    deleteAccount();
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void deleteAccount() {
        String adminId = ADMIN_ID; // Use the hardcoded ID

        Toast.makeText(this, "Deleting account...", Toast.LENGTH_SHORT).show();

        ApiClient.getClient().create(ApiService.class).deleteAccount(adminId, "admin").enqueue(new Callback<StatusResponse>() {
            @Override
            public void onResponse(@NonNull Call<StatusResponse> call, @NonNull Response<StatusResponse> response) {
                if (response.isSuccessful() && response.body() != null && "success".equals(response.body().getStatus())) {
                    SharedPreferences sharedPreferences = getSharedPreferences("StallSpotPrefs", MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.clear();
                    editor.apply();

                    Toast.makeText(AsettingsActivity.this, "Account deleted successfully.", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(AsettingsActivity.this, LoginActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();
                } else {
                    String errorMsg = "Failed to delete account.";
                    if(response.body() != null && response.body().getMessage() != null){
                        errorMsg += " " + response.body().getMessage();
                    } else if (!response.isSuccessful()){
                        errorMsg += " Server error code: " + response.code();
                    }
                    Toast.makeText(AsettingsActivity.this, errorMsg, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<StatusResponse> call, @NonNull Throwable t) {
                Log.e(TAG, "Delete account network failure", t);
                Toast.makeText(AsettingsActivity.this, "Failed to delete account. Network error.", Toast.LENGTH_SHORT).show();
            }
        });
    }
}