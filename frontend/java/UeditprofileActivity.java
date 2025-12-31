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
import android.content.SharedPreferences;
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
import com.simats.foodstall.model.StatusResponse;
import com.simats.foodstall.model.UserProfileResponse;

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

public class UeditprofileActivity extends AppCompatActivity {

    private static final int CAMERA_PERMISSION_CODE = 101;
    private static final int MEDIA_IMAGES_PERMISSION_CODE = 102;
    private static final String TAG = "UeditprofileActivity";

    private CircleImageView profileImageView;
    private FrameLayout loadingOverlay;
    private ImageView loadingIcon;
    private TextView loadingText;
    private TextInputEditText nameEditText, emailEditText, phoneEditText, studentIdEditText;
    private TextInputLayout nameLayout, emailLayout, phoneLayout;
    private Button saveButton;
    private ApiService apiService;
    private String studentId;
    private Uri selectedImageUri = null; // To hold the chosen image Uri

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
        setContentView(R.layout.ueditprofile);

        profileImageView = findViewById(R.id.profileImageView);
        saveButton = findViewById(R.id.saveButton);
        loadingOverlay = findViewById(R.id.loadingOverlay);
        loadingIcon = findViewById(R.id.loadingIcon);
        loadingText = findViewById(R.id.loadingText);
        nameEditText = findViewById(R.id.nameEditText);
        emailEditText = findViewById(R.id.emailEditText);
        phoneEditText = findViewById(R.id.phoneEditText);
        studentIdEditText = findViewById(R.id.studentIdEditText);
        nameLayout = findViewById(R.id.nameLayout);
        emailLayout = findViewById(R.id.emailLayout);
        phoneLayout = findViewById(R.id.phoneLayout);

        apiService = ApiClient.getClient().create(ApiService.class);
        SharedPreferences sharedPreferences = getSharedPreferences("StallSpotPrefs", MODE_PRIVATE);
        studentId = sharedPreferences.getString("STUDENT_ID", null);

        findViewById(R.id.editProfileImageButton).setOnClickListener(v -> showImagePickerDialog());

        saveButton.setOnClickListener(v -> {
            if (validateAllInputs()) {
                updateProfile();
            }
        });

        setupBottomNavigation();

        if (studentId != null && !studentId.isEmpty()) {
            fetchUserProfile(studentId);
        } else {
            Toast.makeText(this, "Error: Not logged in.", Toast.LENGTH_LONG).show();
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        }
    }

    private void fetchUserProfile(String studentId) {
        showLoadingOverlay("Loading profile...", null);
        apiService.getUserProfile(studentId).enqueue(new Callback<UserProfileResponse>() {
            @Override
            public void onResponse(Call<UserProfileResponse> call, Response<UserProfileResponse> response) {
                loadingIcon.clearAnimation();
                loadingOverlay.setVisibility(View.GONE);
                if (response.isSuccessful() && response.body() != null && "success".equals(response.body().getStatus())) {
                    UserProfileResponse.UserData data = response.body().getData();
                    nameEditText.setText(data.getFullname());
                    emailEditText.setText(data.getEmail());
                    phoneEditText.setText(data.getPhonenumber());
                    studentIdEditText.setText(data.getStudentId());

                    String photoUrl = ApiClient.BASE_URL + "uploads/" + data.getProfilePhoto();
                    Glide.with(UeditprofileActivity.this)
                            .load(photoUrl)
                            .placeholder(R.drawable.editprofile)
                            .error(R.drawable.editprofile)
                            .into(profileImageView);
                } else {
                    showCustomToast("Failed to load profile. Please try again.");
                }
            }
            @Override
            public void onFailure(Call<UserProfileResponse> call, Throwable t) {
                loadingIcon.clearAnimation();
                loadingOverlay.setVisibility(View.GONE);
                Log.e(TAG, "onFailure: " + t.getMessage());
                showCustomToast("Network error. Please try again.");
            }
        });
    }

    private void updateProfile() {
        showLoadingOverlay("Saving changes...", null);

        Map<String, RequestBody> fields = new HashMap<>();
        fields.put("role", createPartFromString("user"));
        fields.put("id", createPartFromString(studentId));
        fields.put("fullname", createPartFromString(nameEditText.getText().toString().trim()));
        fields.put("email", createPartFromString(emailEditText.getText().toString().trim()));
        fields.put("phonenumber", createPartFromString(phoneEditText.getText().toString().trim()));

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
                    if ("success".equals(response.body().getStatus())) {
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
        String name = nameEditText.getText().toString().trim();
        if (name.isEmpty()) {
            nameLayout.setError("Full name is required");
            return false;
        } else if (!name.matches("^[a-zA-Z\\s]+$")) {
            nameLayout.setError("Only alphabets and spaces are allowed");
            return false;
        } else {
            nameLayout.setError(null);
            return true;
        }
    }

    private boolean validateEmail() {
        String email = emailEditText.getText().toString().trim();
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

    private boolean validatePhone() {
        String phone = phoneEditText.getText().toString().trim();
        if (phone.isEmpty()) {
            phoneLayout.setError("Phone number is required");
            return false;
        } else if (!phone.matches("^[0-9]{10}$")) {
            phoneLayout.setError("Must be exactly 10 digits");
            return false;
        } else {
            phoneLayout.setError(null);
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
        bottomNavigationView.setSelectedItemId(R.id.nav_profile);
        bottomNavigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.nav_home) {
                startActivity(new Intent(getApplicationContext(), UhomeActivity.class));
                overridePendingTransition(0, 0);
                finish();
                return true;
            } else if (itemId == R.id.nav_orders) {
                startActivity(new Intent(getApplicationContext(), UordersActivity.class));
                overridePendingTransition(0, 0);
                finish();
                return true;
            } else if (itemId == R.id.nav_wallet) {
                startActivity(new Intent(getApplicationContext(), UwalletActivity.class));
                overridePendingTransition(0, 0);
                finish();
                return true;
            } else if (itemId == R.id.nav_profile) {
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