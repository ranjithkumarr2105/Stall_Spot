package com.simats.foodstall;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.MediaStore;
import android.util.Log;
import android.util.Patterns;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.simats.foodstall.model.AdminProfileResponse;
import com.simats.foodstall.model.StatusResponse;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AprofileActivity extends AppCompatActivity {

    private static final String TAG = "AprofileActivity";
    private static final int CAMERA_PERMISSION_CODE = 101;
    private static final int MEDIA_IMAGES_PERMISSION_CODE = 102;
    private static final String ADMIN_ID = "admin";

    private CircleImageView profileImageView;
    private FrameLayout loadingOverlay;
    private ImageView loadingIcon;
    private TextView loadingText;
    private Button saveChangesButton;
    private TextInputEditText adminNameEditText, adminEmailEditText, adminContactEditText;
    private TextInputLayout adminNameLayout, adminEmailLayout, adminContactLayout;
    private ApiService apiService;
    private Uri selectedImageUri = null;

    private final ActivityResultLauncher<Intent> galleryLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    selectedImageUri = result.getData().getData();
                    profileImageView.setImageURI(selectedImageUri);
                }
            });

    private final ActivityResultLauncher<Intent> cameraLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    Bitmap imageBitmap = (Bitmap) result.getData().getExtras().get("data");
                    selectedImageUri = getImageUri(imageBitmap);
                    profileImageView.setImageBitmap(imageBitmap);
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.aprofile);

        profileImageView = findViewById(R.id.profileImageView);
        saveChangesButton = findViewById(R.id.saveChangesButton);
        loadingOverlay = findViewById(R.id.loadingOverlay);
        loadingIcon = findViewById(R.id.loadingIcon);
        loadingText = findViewById(R.id.loadingText);
        ImageView editProfileImageButton = findViewById(R.id.editProfileImageButton);
        adminNameEditText = findViewById(R.id.adminNameEditText);
        adminEmailEditText = findViewById(R.id.adminEmailEditText);
        adminContactEditText = findViewById(R.id.adminContactEditText);
        adminNameLayout = findViewById(R.id.adminNameLayout);
        adminEmailLayout = findViewById(R.id.adminEmailLayout);
        adminContactLayout = findViewById(R.id.adminContactLayout);

        apiService = ApiClient.getClient().create(ApiService.class);

        editProfileImageButton.setOnClickListener(v -> showImagePickerDialog());
        saveChangesButton.setOnClickListener(v -> {
            if (validateAllInputs()) {
                updateAdminProfile();
            }
        });

        setupBottomNavigation();
        fetchAdminProfile();
    }

    private void fetchAdminProfile() {
        showLoadingOverlay("Loading profile...", null);
        apiService.getAdminProfile(ADMIN_ID).enqueue(new Callback<AdminProfileResponse>() {
            @Override
            public void onResponse(Call<AdminProfileResponse> call, Response<AdminProfileResponse> response) {
                loadingIcon.clearAnimation();
                loadingOverlay.setVisibility(View.GONE);
                if (response.isSuccessful() && response.body() != null && "success".equals(response.body().getStatus())) {
                    AdminProfileResponse.AdminData data = response.body().getData();
                    adminNameEditText.setText(data.getFullname());
                    adminEmailEditText.setText(data.getEmail());
                    adminContactEditText.setText(data.getPhonenumber());

                    String photoUrl = ApiClient.BASE_URL + "uploads/" + data.getProfilePhoto();
                    Glide.with(AprofileActivity.this)
                            .load(photoUrl)
                            .placeholder(R.drawable.editprofile)
                            .error(R.drawable.editprofile)
                            .into(profileImageView);
                } else {
                    showCustomToast("Failed to load admin profile.");
                }
            }
            @Override
            public void onFailure(Call<AdminProfileResponse> call, Throwable t) {
                loadingIcon.clearAnimation();
                loadingOverlay.setVisibility(View.GONE);
                Log.e(TAG, "onFailure: " + t.getMessage());
                showCustomToast("Network error. Please try again.");
            }
        });
    }

    private void updateAdminProfile() {
        showLoadingOverlay("Saving changes...", null);

        Map<String, RequestBody> fields = new HashMap<>();
        fields.put("role", createPartFromString("admin"));
        fields.put("id", createPartFromString(ADMIN_ID));
        fields.put("fullname", createPartFromString(adminNameEditText.getText().toString().trim()));
        fields.put("email", createPartFromString(adminEmailEditText.getText().toString().trim()));
        fields.put("phonenumber", createPartFromString(adminContactEditText.getText().toString().trim()));

        MultipartBody.Part photoPart = null;
        if (selectedImageUri != null) {
            photoPart = prepareFilePart("profile_photo", selectedImageUri);
        }

        apiService.updateProfile(fields, photoPart).enqueue(new Callback<StatusResponse>() {
            @Override
            public void onResponse(Call<StatusResponse> call, Response<StatusResponse> response) {
                loadingIcon.clearAnimation();
                loadingOverlay.setVisibility(View.GONE);
                if (response.isSuccessful() && response.body() != null) {
                    showCustomToast(response.body().getMessage());
                    if("success".equals(response.body().getStatus())) {
                        selectedImageUri = null;
                    }
                } else {
                    showCustomToast("Failed to update profile.");
                }
            }
            @Override
            public void onFailure(Call<StatusResponse> call, Throwable t) {
                loadingIcon.clearAnimation();
                loadingOverlay.setVisibility(View.GONE);
                Log.e(TAG, "onFailure: " + t.getMessage());
                showCustomToast("Network error. Please try again.");
            }
        });
    }

    private boolean validateName() {
        String name = adminNameEditText.getText().toString().trim();
        if (name.isEmpty()) {
            adminNameLayout.setError("Name is required");
            return false;
        } else if (!name.matches("^[a-zA-Z\\s]+$")) {
            adminNameLayout.setError("Only alphabets and spaces are allowed");
            return false;
        } else {
            adminNameLayout.setError(null);
            return true;
        }
    }

    private boolean validateEmail() {
        String email = adminEmailEditText.getText().toString().trim();
        if (email.isEmpty()) {
            adminEmailLayout.setError("Email is required");
            return false;
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            adminEmailLayout.setError("Please enter a valid email address");
            return false;
        } else if (!email.endsWith("@gmail.com")) {
            adminEmailLayout.setError("Only @gmail.com emails are accepted");
            return false;
        } else {
            adminEmailLayout.setError(null);
            return true;
        }
    }

    private boolean validatePhone() {
        String phone = adminContactEditText.getText().toString().trim();
        if (phone.isEmpty()) {
            adminContactLayout.setError("Contact number is required");
            return false;
        } else if (!phone.matches("^[0-9]{10}$")) {
            adminContactLayout.setError("Must be exactly 10 digits");
            return false;
        } else {
            adminContactLayout.setError(null);
            return true;
        }
    }

    private boolean validateAllInputs() {
        boolean isNameValid = validateName();
        boolean isEmailValid = validateEmail();
        boolean isPhoneValid = validatePhone();
        return isNameValid && isEmailValid && isPhoneValid;
    }

    private void showCustomToast(String message) {
        LayoutInflater inflater = getLayoutInflater();
        View layout = inflater.inflate(R.layout.custom_toast, findViewById(R.id.custom_toast_container));
        TextView text = layout.findViewById(R.id.toast_text);
        text.setText(message);
        Toast toast = new Toast(getApplicationContext());
        toast.setGravity(Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 200);
        toast.setDuration(Toast.LENGTH_SHORT);
        toast.setView(layout);
        toast.show();
    }

    private void showImagePickerDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Choose Image Source");
        builder.setItems(new CharSequence[]{"Gallery", "Camera"}, (dialog, which) -> {
            if (which == 0) {
                checkStoragePermissionAndOpenGallery();
            } else {
                checkCameraPermissionAndOpenCamera();
            }
        });
        builder.show();
    }

    private void checkStoragePermissionAndOpenGallery() {
        String permission;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            permission = Manifest.permission.READ_MEDIA_IMAGES;
        } else {
            permission = Manifest.permission.READ_EXTERNAL_STORAGE;
        }
        if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{permission}, MEDIA_IMAGES_PERMISSION_CODE);
        } else {
            openGallery();
        }
    }

    private void openGallery() {
        Intent galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        galleryLauncher.launch(galleryIntent);
    }

    private void checkCameraPermissionAndOpenCamera() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, CAMERA_PERMISSION_CODE);
        } else {
            openCamera();
        }
    }

    private void openCamera() {
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        cameraLauncher.launch(cameraIntent);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == MEDIA_IMAGES_PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openGallery();
            } else {
                Toast.makeText(this, "Storage permission is required to select an image.", Toast.LENGTH_SHORT).show();
            }
        }
        if (requestCode == CAMERA_PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openCamera();
            } else {
                Toast.makeText(this, "Camera permission is required to take a picture.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void showLoadingOverlay(String message, Runnable onComplete) {
        loadingText.setText(message);
        loadingOverlay.setVisibility(View.VISIBLE);
        Animation rotation = AnimationUtils.loadAnimation(this, R.anim.hourglass_rotation);
        loadingIcon.startAnimation(rotation);
        if (onComplete != null) {
            new Handler(Looper.getMainLooper()).postDelayed(() -> {
                loadingIcon.clearAnimation();
                loadingOverlay.setVisibility(View.GONE);
                onComplete.run();
            }, 1500);
        }
    }

    private void setupBottomNavigation() {
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation_bar);
        bottomNavigationView.setSelectedItemId(R.id.nav_admin_profile);
        bottomNavigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.nav_admin_profile) {
                return true;
            } else if (itemId == R.id.nav_admin_home) {
                startActivity(new Intent(getApplicationContext(), AhomeActivity.class));
                overridePendingTransition(0, 0);
                finish();
                return true;
            } else if (itemId == R.id.nav_admin_new_stall) {
                startActivity(new Intent(getApplicationContext(), Anew_stallActivity.class));
                overridePendingTransition(0, 0);
                finish();
                return true;
            } else if (itemId == R.id.nav_admin_view_reports) {
                startActivity(new Intent(getApplicationContext(), Aview_reportsActivity.class));
                overridePendingTransition(0, 0);
                finish();
                return true;
            }
            return false;
        });
    }

    private RequestBody createPartFromString(String descriptionString) {
        return RequestBody.create(MultipartBody.FORM, descriptionString);
    }

    private MultipartBody.Part prepareFilePart(String partName, Uri fileUri) {
        String filePath = getRealPathFromURI(fileUri);
        if (filePath == null) {
            Toast.makeText(this, "Error finding file path", Toast.LENGTH_SHORT).show();
            return null;
        }
        File file = new File(filePath);
        RequestBody requestFile = RequestBody.create(MediaType.parse(getContentResolver().getType(fileUri)), file);
        return MultipartBody.Part.createFormData(partName, file.getName(), requestFile);
    }

    private String getRealPathFromURI(Uri contentUri) {
        String[] proj = { MediaStore.Images.Media.DATA };
        Cursor cursor = getContentResolver().query(contentUri, proj, null, null, null);
        if (cursor == null) return null;
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        String path = cursor.getString(column_index);
        cursor.close();
        return path;
    }

    private Uri getImageUri(Bitmap inImage) {
        String path = MediaStore.Images.Media.insertImage(getContentResolver(), inImage, "Title_" + System.currentTimeMillis(), null);
        return Uri.parse(path);
    }
}