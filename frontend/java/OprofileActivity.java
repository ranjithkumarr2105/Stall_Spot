package com.simats.foodstall;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.MediaStore;
import android.util.Log;
import android.util.Patterns;
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
import com.simats.foodstall.model.OwnerProfileResponse;
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

public class OprofileActivity extends AppCompatActivity {

    private static final String TAG = "OprofileActivity";
    private static final int CAMERA_PERMISSION_CODE = 101;

    private TextInputEditText stallNameEditText, ownerNameEditText, addressEditText, phoneNumberEditText, emailEditText, fssaiNumberEditText, ownerIdEditText;
    private TextInputLayout ownerNameLayout, addressLayout, phoneLayout, emailLayout;
    private FrameLayout loadingOverlay;
    private ImageView loadingIcon;
    private TextView loadingText;
    private CircleImageView profileImage;
    private Button saveButton;
    private ApiService apiService;
    private String stallId;
    private Uri selectedImageUri = null;

    private ActivityResultLauncher<Intent> cameraLauncher;
    private ActivityResultLauncher<Intent> galleryLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.oprofile);

        stallNameEditText = findViewById(R.id.stallNameEditText);
        ownerNameEditText = findViewById(R.id.ownerNameEditText);
        addressEditText = findViewById(R.id.addressEditText);
        phoneNumberEditText = findViewById(R.id.phoneNumberEditText);
        emailEditText = findViewById(R.id.emailEditText);
        fssaiNumberEditText = findViewById(R.id.fssaiNumberEditText);
        ownerIdEditText = findViewById(R.id.ownerIdEditText);
        loadingOverlay = findViewById(R.id.loadingOverlay);
        loadingIcon = findViewById(R.id.loadingIcon);
        loadingText = findViewById(R.id.loadingText);
        profileImage = findViewById(R.id.profileImage);
        saveButton = findViewById(R.id.saveButton);
        ownerNameLayout = findViewById(R.id.ownerNameLayout);
        addressLayout = findViewById(R.id.addressLayout);
        phoneLayout = findViewById(R.id.phoneLayout);
        emailLayout = findViewById(R.id.emailLayout);

        apiService = ApiClient.getClient().create(ApiService.class);
        SharedPreferences ownerPrefs = getSharedPreferences("owner_prefs", MODE_PRIVATE);
        stallId = ownerPrefs.getString("stall_id", null);

        registerCameraLauncher();
        registerGalleryLauncher();

        saveButton.setOnClickListener(v -> saveChanges());
        findViewById(R.id.editProfileImageButton).setOnClickListener(v -> showImagePickerDialog());

        setupBottomNavigation();

        if (stallId != null && !stallId.isEmpty()) {
            fetchOwnerProfile(stallId);
        } else {
            Toast.makeText(this, "Error: Not logged in.", Toast.LENGTH_LONG).show();
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        }
    }

    private void fetchOwnerProfile(String stallId) {
        showLoadingOverlay("Loading profile...", null);
        apiService.getOwnerProfile(stallId).enqueue(new Callback<OwnerProfileResponse>() {
            @Override
            public void onResponse(Call<OwnerProfileResponse> call, Response<OwnerProfileResponse> response) {
                loadingIcon.clearAnimation();
                loadingOverlay.setVisibility(View.GONE);
                if (response.isSuccessful() && response.body() != null && "success".equals(response.body().getStatus())) {
                    OwnerProfileResponse.OwnerData data = response.body().getData();
                    populateForm(data);

                    String photoUrl = ApiClient.BASE_URL + "uploads/" + data.getProfilePhoto();
                    Glide.with(OprofileActivity.this)
                            .load(photoUrl)
                            .placeholder(R.drawable.editprofile)
                            .error(R.drawable.editprofile)
                            .into(profileImage);
                } else {
                    Toast.makeText(OprofileActivity.this, "Failed to load profile", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<OwnerProfileResponse> call, Throwable t) {
                loadingIcon.clearAnimation();
                loadingOverlay.setVisibility(View.GONE);
                Log.e(TAG, "onFailure: " + t.getMessage());
                Toast.makeText(OprofileActivity.this, "Network error", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void populateForm(OwnerProfileResponse.OwnerData data) {
        stallNameEditText.setText(data.getStallName());
        ownerNameEditText.setText(data.getOwnerName());
        addressEditText.setText(data.getFullAddress());
        phoneNumberEditText.setText(data.getPhoneNumber());
        emailEditText.setText(data.getEmail());
        fssaiNumberEditText.setText(data.getFssaiNumber());
        ownerIdEditText.setText(data.getStallId());
    }

    private void saveChanges() {
        if (!validateAllInputs()) {
            Toast.makeText(this, "Please fix the errors in the form", Toast.LENGTH_SHORT).show();
            return;
        }
        showLoadingOverlay("Saving changes...", null);

        Map<String, RequestBody> fields = new HashMap<>();
        fields.put("role", createPartFromString("owner"));
        fields.put("id", createPartFromString(stallId));
        fields.put("ownername", createPartFromString(ownerNameEditText.getText().toString().trim()));
        fields.put("phonenumber", createPartFromString(phoneNumberEditText.getText().toString().trim()));
        fields.put("email", createPartFromString(emailEditText.getText().toString().trim()));
        fields.put("fulladdress", createPartFromString(addressEditText.getText().toString().trim()));

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
                    Toast.makeText(OprofileActivity.this, response.body().getMessage(), Toast.LENGTH_SHORT).show();
                    if("success".equals(response.body().getStatus())) {
                        selectedImageUri = null;
                    }
                } else {
                    Toast.makeText(OprofileActivity.this, "Failed to save changes", Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onFailure(Call<StatusResponse> call, Throwable t) {
                loadingIcon.clearAnimation();
                loadingOverlay.setVisibility(View.GONE);
                Log.e(TAG, "onFailure: " + t.getMessage());
                Toast.makeText(OprofileActivity.this, "Network error", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private boolean validateOwnerName() {
        String name = ownerNameEditText.getText().toString().trim();
        if (name.isEmpty()) {
            ownerNameLayout.setError("Owner name is required");
            return false;
        } else if (!name.matches("^[a-zA-Z\\s]+$")) {
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

    private boolean validatePhone() {
        String phone = phoneNumberEditText.getText().toString().trim();
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

    private boolean validateAllInputs() {
        boolean isNameValid = validateOwnerName();
        boolean isAddressValid = validateAddress();
        boolean isPhoneValid = validatePhone();
        boolean isEmailValid = validateEmail();
        return isNameValid && isAddressValid && isPhoneValid && isEmailValid;
    }

    private void showImagePickerDialog() {
        final CharSequence[] options = {"Take Photo", "Choose from Gallery", "Cancel"};
        AlertDialog.Builder builder = new AlertDialog.Builder(OprofileActivity.this);
        builder.setTitle("Change Profile Photo");
        builder.setItems(options, (dialog, item) -> {
            if (options[item].equals("Take Photo")) {
                checkCameraPermissionAndLaunch();
            } else if (options[item].equals("Choose from Gallery")) {
                Intent galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                galleryLauncher.launch(galleryIntent);
            } else if (options[item].equals("Cancel")) {
                dialog.dismiss();
            }
        });
        builder.show();
    }

    private void checkCameraPermissionAndLaunch() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            cameraLauncher.launch(cameraIntent);
        } else {
            requestPermissions(new String[]{Manifest.permission.CAMERA}, CAMERA_PERMISSION_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == CAMERA_PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                cameraLauncher.launch(cameraIntent);
            } else {
                Toast.makeText(this, "Camera Permission is required to use the camera.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void registerCameraLauncher() {
        cameraLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        Bundle extras = result.getData().getExtras();
                        Bitmap imageBitmap = (Bitmap) extras.get("data");
                        selectedImageUri = getImageUri(imageBitmap);
                        profileImage.setImageBitmap(imageBitmap);
                    }
                });
    }

    private void registerGalleryLauncher() {
        galleryLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        selectedImageUri = result.getData().getData();
                        profileImage.setImageURI(selectedImageUri);
                    }
                });
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
        bottomNavigationView.setSelectedItemId(R.id.nav_owner_profile);
        bottomNavigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.nav_owner_profile) {
                return true;
            } else if (itemId == R.id.nav_owner_home) {
                startActivity(new Intent(getApplicationContext(), OhomeActivity.class));
                overridePendingTransition(0, 0);
                finish();
                return true;
            } else if (itemId == R.id.nav_owner_orders) {
                startActivity(new Intent(getApplicationContext(), OordersActivity.class));
                overridePendingTransition(0, 0);
                finish();
                return true;
            } else if (itemId == R.id.nav_owner_menu) {
                startActivity(new Intent(getApplicationContext(), OmenuActivity.class));
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