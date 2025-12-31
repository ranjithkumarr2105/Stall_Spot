package com.simats.foodstall;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;

import com.bumptech.glide.Glide;
import com.simats.foodstall.model.StatusResponse;
import com.simats.foodstall.model.UserProfileResponse;

import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

public class UsettingsActivity extends AppCompatActivity {

    private CircleImageView profileImage;
    private TextView userNameTextView;
    private static final String TAG = "UsettingsActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.usettings);

        profileImage = findViewById(R.id.profileImage);
        userNameTextView = findViewById(R.id.userName);

        findViewById(R.id.backButton).setOnClickListener(v -> onBackPressed());

        findViewById(R.id.editProfileLayout).setOnClickListener(v -> {
            startActivity(new Intent(UsettingsActivity.this, UeditprofileActivity.class));
        });

        findViewById(R.id.changePasswordLayout).setOnClickListener(v -> {
            Intent intent = new Intent(UsettingsActivity.this, UchangepasswordActivity.class);
            intent.putExtra("USER_ROLE", "USER"); // Role for backend logic

            // Get the student ID from SharedPreferences to pass as identifier
            SharedPreferences sharedPreferences = getSharedPreferences("StallSpotPrefs", MODE_PRIVATE);
            String studentId = sharedPreferences.getString("STUDENT_ID", null);
            if (studentId != null) {
                intent.putExtra("USER_IDENTIFIER", studentId); // Pass the actual ID
                startActivity(intent);
            } else {
                Toast.makeText(this, "Error: User ID not found.", Toast.LENGTH_SHORT).show();
            }
        });

        findViewById(R.id.privacyLayout).setOnClickListener(v -> {
            startActivity(new Intent(UsettingsActivity.this, Uprivacy_policyActivity.class));
        });

        findViewById(R.id.helpLayout).setOnClickListener(v -> {
            startActivity(new Intent(UsettingsActivity.this, UhelpActivity.class));
        });

        findViewById(R.id.logoutButton).setOnClickListener(v -> {
            showLogoutConfirmationDialog();
        });

        findViewById(R.id.deleteAccountButton).setOnClickListener(v -> {
            showDeleteConfirmationDialog();
        });

        // Load profile info when activity starts
        loadUserProfile();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Reload profile info in case it was changed in UeditprofileActivity
        loadUserProfile();
    }

    private void loadUserProfile() {
        SharedPreferences sharedPreferences = getSharedPreferences("StallSpotPrefs", MODE_PRIVATE);
        String studentId = sharedPreferences.getString("STUDENT_ID", null);

        if (studentId == null || studentId.isEmpty()) {
            Log.e(TAG, "Student ID not found in SharedPreferences. Cannot load profile.");
            userNameTextView.setText("User Name"); // Show default
            profileImage.setImageResource(R.drawable.editprofile); // Show default
            return;
        }

        // Show placeholder while loading (optional, but good UX)
        userNameTextView.setText("Loading...");
        profileImage.setImageResource(R.drawable.editprofile);

        ApiService apiService = ApiClient.getClient().create(ApiService.class);
        apiService.getUserProfile(studentId).enqueue(new Callback<UserProfileResponse>() {
            @Override
            public void onResponse(@NonNull Call<UserProfileResponse> call, @NonNull Response<UserProfileResponse> response) {
                if (response.isSuccessful() && response.body() != null && "success".equals(response.body().getStatus())) {
                    UserProfileResponse.UserData data = response.body().getData();
                    if (data != null) {
                        userNameTextView.setText(data.getFullname());

                        String photoUrl = null;
                        if (data.getProfilePhoto() != null && !data.getProfilePhoto().isEmpty()) {
                            // Construct full URL assuming profilePhoto is just the filename
                            photoUrl = ApiClient.BASE_URL + "uploads/" + data.getProfilePhoto();
                            Log.d(TAG, "Loading user profile image from URL: " + photoUrl);
                        } else {
                            Log.d(TAG, "User profile photo is null or empty in API response.");
                        }

                        Glide.with(UsettingsActivity.this)
                                .load(photoUrl) // Load the constructed URL
                                .placeholder(R.drawable.editprofile)
                                .error(R.drawable.editprofile)
                                .into(profileImage);

                        // Save updated info back to SharedPreferences
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putString("USER_NAME", data.getFullname());
                        // Save the relative path, not the full URL, consistent with edit profile
                        editor.putString("USER_PROFILE_URL", data.getProfilePhoto());
                        editor.apply();

                    } else {
                        Log.w(TAG, "API response successful, but user data is null.");
                        userNameTextView.setText(sharedPreferences.getString("USER_NAME", "User Name")); // Fallback to saved name
                        profileImage.setImageResource(R.drawable.editprofile); // Keep placeholder
                    }
                } else {
                    Log.e(TAG, "Failed to load profile. Code: " + response.code() + ", Message: " + response.message());
                    userNameTextView.setText(sharedPreferences.getString("USER_NAME", "User Name")); // Fallback to saved name
                    profileImage.setImageResource(R.drawable.editprofile); // Keep placeholder
                    Toast.makeText(UsettingsActivity.this, "Failed to load profile.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<UserProfileResponse> call, @NonNull Throwable t) {
                Log.e(TAG, "Network error loading profile", t);
                userNameTextView.setText(sharedPreferences.getString("USER_NAME", "User Name")); // Fallback to saved name
                profileImage.setImageResource(R.drawable.editprofile); // Keep placeholder
                Toast.makeText(UsettingsActivity.this, "Network error. Could not load profile.", Toast.LENGTH_SHORT).show();
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
                    Intent intent = new Intent(UsettingsActivity.this, LoginActivity.class);
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
                .setMessage("Are you absolutely sure you want to delete your account? This action is permanent and cannot be undone.")
                .setPositiveButton("Delete", (dialog, which) -> {
                    deleteAccount();
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void deleteAccount() {
        SharedPreferences sharedPreferences = getSharedPreferences("StallSpotPrefs", MODE_PRIVATE);
        String studentId = sharedPreferences.getString("STUDENT_ID", null);

        if (studentId == null) {
            Toast.makeText(this, "Error: Cannot delete account. User ID not found.", Toast.LENGTH_SHORT).show();
            return;
        }

        Toast.makeText(this, "Deleting account...", Toast.LENGTH_SHORT).show();

        ApiClient.getClient().create(ApiService.class).deleteAccount(studentId, "user").enqueue(new Callback<StatusResponse>() {
            @Override
            public void onResponse(@NonNull Call<StatusResponse> call, @NonNull Response<StatusResponse> response) {
                if (response.isSuccessful() && response.body() != null && "success".equals(response.body().getStatus())) {
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.clear();
                    editor.apply();

                    Toast.makeText(UsettingsActivity.this, "Account deleted successfully.", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(UsettingsActivity.this, LoginActivity.class);
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
                    Toast.makeText(UsettingsActivity.this, errorMsg, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<StatusResponse> call, @NonNull Throwable t) {
                Log.e(TAG, "Delete account network failure", t);
                Toast.makeText(UsettingsActivity.this, "Failed to delete account. Network error.", Toast.LENGTH_SHORT).show();
            }
        });
    }
}