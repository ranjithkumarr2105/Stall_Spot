package com.simats.foodstall;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;

import com.bumptech.glide.Glide;
import com.simats.foodstall.model.OwnerProfileResponse;
import com.simats.foodstall.model.StatusResponse;

import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import android.widget.TextView;
import android.widget.Toast;

public class OsettingsActivity extends AppCompatActivity {

    private CircleImageView profileImage;
    private TextView userNameTextView;
    private static final String TAG = "OsettingsActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.osettings);

        profileImage = findViewById(R.id.profileImage);
        userNameTextView = findViewById(R.id.userName);

        findViewById(R.id.backButton).setOnClickListener(v -> onBackPressed());

        findViewById(R.id.editProfileLayout).setOnClickListener(v -> {
            startActivity(new Intent(this, OprofileActivity.class));
        });

        findViewById(R.id.helpSupportLayout).setOnClickListener(v -> {
            startActivity(new Intent(this, OhelpActivity.class));
        });

        findViewById(R.id.privacyLayout).setOnClickListener(v -> {
            startActivity(new Intent(this, OpolicyActivity.class));
        });

        findViewById(R.id.changePasswordLayout).setOnClickListener(v -> {
            Intent intent = new Intent(this, UchangepasswordActivity.class);
            intent.putExtra("USER_ROLE", "OWNER"); // Role for backend logic

            // Get the stall ID from SharedPreferences to pass as identifier
            SharedPreferences sharedPreferences = getSharedPreferences("owner_prefs", MODE_PRIVATE);
            String stallId = sharedPreferences.getString("stall_id", null);
            if (stallId != null) {
                intent.putExtra("USER_IDENTIFIER", stallId); // Pass the actual ID
                startActivity(intent);
            } else {
                Toast.makeText(this, "Error: Stall ID not found.", Toast.LENGTH_SHORT).show();
            }
        });

        findViewById(R.id.logoutButton).setOnClickListener(v -> {
            showLogoutConfirmationDialog();
        });

        findViewById(R.id.deleteAccountButton).setOnClickListener(v -> {
            showDeleteConfirmationDialog();
        });

        loadOwnerProfile();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Reload profile info in case it was changed in OprofileActivity
        loadOwnerProfile();
    }


    private void loadOwnerProfile() {
        SharedPreferences sharedPreferences = getSharedPreferences("owner_prefs", MODE_PRIVATE);
        String stallId = sharedPreferences.getString("stall_id", null);

        if (stallId == null || stallId.isEmpty()) {
            Log.e(TAG, "Stall ID not found in SharedPreferences. Cannot load profile.");
            userNameTextView.setText("Owner Name");
            profileImage.setImageResource(R.drawable.editprofile);
            return;
        }

        userNameTextView.setText("Loading...");
        profileImage.setImageResource(R.drawable.editprofile);

        ApiService apiService = ApiClient.getClient().create(ApiService.class);
        apiService.getOwnerProfile(stallId).enqueue(new Callback<OwnerProfileResponse>() {
            @Override
            public void onResponse(@NonNull Call<OwnerProfileResponse> call, @NonNull Response<OwnerProfileResponse> response) {
                if (response.isSuccessful() && response.body() != null && "success".equals(response.body().getStatus())) {
                    OwnerProfileResponse.OwnerData data = response.body().getData();
                    if (data != null) {
                        // Use Owner Name if available, otherwise fallback to Stall Name
                        String nameToDisplay = (data.getOwnerName() != null && !data.getOwnerName().isEmpty()) ? data.getOwnerName() : data.getStallName();
                        userNameTextView.setText(nameToDisplay);

                        String photoUrl = null;
                        if (data.getProfilePhoto() != null && !data.getProfilePhoto().isEmpty()) {
                            photoUrl = ApiClient.BASE_URL + "uploads/" + data.getProfilePhoto();
                            Log.d(TAG, "Loading owner profile image from URL: " + photoUrl);
                        } else {
                            Log.d(TAG, "Owner profile photo is null or empty in API response.");
                        }

                        Glide.with(OsettingsActivity.this)
                                .load(photoUrl)
                                .placeholder(R.drawable.editprofile)
                                .error(R.drawable.editprofile)
                                .into(profileImage);

                        // Save updated info back to SharedPreferences
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putString("stall_name", data.getStallName());
                        editor.putString("owner_name", data.getOwnerName());
                        editor.putString("OWNER_PROFILE_URL", data.getProfilePhoto());
                        editor.apply();

                    } else {
                        Log.w(TAG, "API response successful, but owner data is null.");
                        userNameTextView.setText(sharedPreferences.getString("owner_name", "Owner Name")); // Fallback
                        profileImage.setImageResource(R.drawable.editprofile);
                    }
                } else {
                    Log.e(TAG, "Failed to load owner profile. Code: " + response.code() + ", Message: " + response.message());
                    userNameTextView.setText(sharedPreferences.getString("owner_name", "Owner Name")); // Fallback
                    profileImage.setImageResource(R.drawable.editprofile);
                    Toast.makeText(OsettingsActivity.this, "Failed to load profile.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<OwnerProfileResponse> call, @NonNull Throwable t) {
                Log.e(TAG, "Network error loading owner profile", t);
                userNameTextView.setText(sharedPreferences.getString("owner_name", "Owner Name")); // Fallback
                profileImage.setImageResource(R.drawable.editprofile);
                Toast.makeText(OsettingsActivity.this, "Network error. Could not load profile.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showLogoutConfirmationDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Logout")
                .setMessage("Are you sure you want to logout?")
                .setPositiveButton("Yes", (dialog, which) -> {
                    SharedPreferences sharedPreferences = getSharedPreferences("owner_prefs", MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.clear();
                    editor.apply();

                    Toast.makeText(this, "Logged out successfully", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(OsettingsActivity.this, LoginActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();
                })
                .setNegativeButton("No", null)
                .show();
    }

    private void showDeleteConfirmationDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Delete Account")
                .setMessage("Are you absolutely sure you want to delete your stall account? This action is permanent and cannot be undone.")
                .setPositiveButton("Delete", (dialog, which) -> {
                    deleteAccount();
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void deleteAccount() {
        SharedPreferences sharedPreferences = getSharedPreferences("owner_prefs", MODE_PRIVATE);
        String stallId = sharedPreferences.getString("stall_id", null);

        if (stallId == null) {
            Toast.makeText(this, "Error: Cannot delete account. Stall ID not found.", Toast.LENGTH_SHORT).show();
            return;
        }

        Toast.makeText(this, "Deleting account...", Toast.LENGTH_SHORT).show();

        ApiClient.getClient().create(ApiService.class).deleteAccount(stallId, "owner").enqueue(new Callback<StatusResponse>() {
            @Override
            public void onResponse(@NonNull Call<StatusResponse> call, @NonNull Response<StatusResponse> response) {
                if (response.isSuccessful() && response.body() != null && "success".equals(response.body().getStatus())) {
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.clear();
                    editor.apply();

                    Toast.makeText(OsettingsActivity.this, "Account deleted successfully.", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(OsettingsActivity.this, LoginActivity.class);
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
                    Toast.makeText(OsettingsActivity.this, errorMsg, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<StatusResponse> call, @NonNull Throwable t) {
                Log.e(TAG, "Delete account network failure", t);
                Toast.makeText(OsettingsActivity.this, "Failed to delete account. Network error.", Toast.LENGTH_SHORT).show();
            }
        });
    }
}