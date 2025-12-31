package com.simats.foodstall;

import android.app.TimePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log; // Import Log
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.switchmaterial.SwitchMaterial;
import com.simats.foodstall.adapter.OOwnerMenuAdapter;
import com.simats.foodstall.model.OGetMenuResponse;
import com.simats.foodstall.model.OMenuItem;
import com.simats.foodstall.model.StatusResponse;
import org.json.JSONObject;
import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OmenuActivity extends AppCompatActivity {

    private SwitchMaterial workingTodaySwitch;
    private FrameLayout loadingOverlay;
    private ImageView loadingIcon;
    private RecyclerView regularItemsRecyclerView, comboDealsRecyclerView;
    private Button saveStallDetailsButton, addMenuItemButton, addComboButton;
    private OOwnerMenuAdapter regularMenuAdapter, comboMenuAdapter;
    private String stallId;
    private LinearLayout controlsContainer, operatingHoursContainer;
    private TextView statusMessageTextView;
    private EditText openingTimeEditText, closingTimeEditText;
    private ActivityResultLauncher<Intent> galleryLauncher;
    private CircleImageView currentDialogImageView;
    private Uri selectedImageUri = null;
    private static final String TAG = "OmenuActivity"; // Added TAG for logging

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.omenu);

        // --- [ THE FIX ] ---
        // Changed "StallSpotPrefs" to "owner_prefs"
        // Changed key "STALL_ID" to "stall_id" (matching LoginActivity/AgreementActivity)
        Log.d(TAG, "Attempting to read stall_id from owner_prefs...");
        SharedPreferences sp = getSharedPreferences("owner_prefs", MODE_PRIVATE);
        stallId = sp.getString("stall_id", null);
        // --- [ END FIX ] ---

        if (stallId == null || stallId.isEmpty()) {
            // This toast confirms the problem if it still happens
            Log.e(TAG, "Error: Stall ID not found in owner_prefs. Finishing activity.");
            Toast.makeText(this, "Error: Stall ID not found. Please log in again.", Toast.LENGTH_LONG).show();
            // Redirect to Login to ensure prefs are set correctly
            Intent loginIntent = new Intent(this, LoginActivity.class);
            loginIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(loginIntent);
            finish();
            return; // Stop further execution
        }
        Log.d(TAG, "Successfully retrieved stall_id: " + stallId);


        bindViews();
        setupGalleryLauncher();
        setupListeners();
        setupRecyclerViews();
        setupBottomNavigation();
        fetchDataFromServer();
    }

    private void bindViews() {
        workingTodaySwitch = findViewById(R.id.workingTodaySwitch);
        openingTimeEditText = findViewById(R.id.openingTimeEditText);
        closingTimeEditText = findViewById(R.id.closingTimeEditText);
        operatingHoursContainer = findViewById(R.id.operatingHoursContainer);
        saveStallDetailsButton = findViewById(R.id.saveStallDetailsButton);
        addMenuItemButton = findViewById(R.id.addMenuItemButton);
        addComboButton = findViewById(R.id.addComboButton);
        regularItemsRecyclerView = findViewById(R.id.regularItemsRecyclerView);
        comboDealsRecyclerView = findViewById(R.id.comboDealsRecyclerView);
        loadingOverlay = findViewById(R.id.loadingOverlay);
        loadingIcon = findViewById(R.id.loadingIcon);
        controlsContainer = findViewById(R.id.controlsContainer);
        statusMessageTextView = findViewById(R.id.statusMessageTextView);
    }

    private void setupGalleryLauncher() {
        galleryLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        selectedImageUri = result.getData().getData();
                        if (currentDialogImageView != null) {
                            currentDialogImageView.setImageURI(selectedImageUri);
                        }
                    }
                });
    }

    private void setupListeners() {
        workingTodaySwitch.setOnCheckedChangeListener((buttonView, isChecked) -> operatingHoursContainer.setVisibility(isChecked ? View.VISIBLE : View.GONE));
        saveStallDetailsButton.setOnClickListener(v -> updateStallStatus());
        addMenuItemButton.setOnClickListener(v -> showAddEditItemDialog(null, "Regular"));
        addComboButton.setOnClickListener(v -> showAddEditItemDialog(null, "Combo"));
        openingTimeEditText.setOnClickListener(v -> showTimePickerDialog(openingTimeEditText));
        closingTimeEditText.setOnClickListener(v -> showTimePickerDialog(closingTimeEditText));
    }

    private void showTimePickerDialog(final EditText timeEditText) {
        Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);
        TimePickerDialog timePickerDialog = new TimePickerDialog(this, (view, hourOfDay, minuteOfHour) -> {
            String amPm = (hourOfDay < 12) ? "AM" : "PM";
            int displayHour = (hourOfDay > 12) ? hourOfDay - 12 : hourOfDay;
            if (displayHour == 0) displayHour = 12; // Handle midnight/noon correctly
            String time = String.format(Locale.getDefault(), "%d:%02d %s", displayHour, minuteOfHour, amPm);
            timeEditText.setText(time);
        }, hour, minute, false); // false = 12 hour format AM/PM
        timePickerDialog.show();
    }

    private void setupRecyclerViews() {
        OOwnerMenuAdapter.OnMenuItemClickListener listener = new OOwnerMenuAdapter.OnMenuItemClickListener() {
            @Override public void onEditClick(OMenuItem item) { showAddEditItemDialog(item, item.getCategory()); }
            @Override public void onDeleteClick(OMenuItem item) { showDeleteConfirmationDialog(item); }
        };
        regularMenuAdapter = new OOwnerMenuAdapter(listener);
        regularItemsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        regularItemsRecyclerView.setAdapter(regularMenuAdapter);

        comboMenuAdapter = new OOwnerMenuAdapter(listener);
        comboDealsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        comboDealsRecyclerView.setAdapter(comboMenuAdapter);

        // Keep nested scrolling disabled as per your original code
        regularItemsRecyclerView.setNestedScrollingEnabled(false);
        comboDealsRecyclerView.setNestedScrollingEnabled(false);
    }

    private void fetchDataFromServer() {
        startLoadingAnimation();
        Log.d(TAG, "Fetching menu details for stall_id: " + stallId);
        ApiClient.getClient().create(ApiService.class).getOwnerMenuDetails("get_menu_details", stallId)
                .enqueue(new Callback<OGetMenuResponse>() {
                    @Override
                    public void onResponse(@NonNull Call<OGetMenuResponse> call, @NonNull Response<OGetMenuResponse> response) {
                        stopLoadingAnimation();
                        if (response.isSuccessful() && response.body() != null && "success".equals(response.body().getStatus())) {
                            Log.d(TAG, "Successfully fetched menu details.");
                            OGetMenuResponse.MenuData data = response.body().getData();
                            handleStallStatus(data.getStallDetails());
                            // Ensure stall is approved before processing menu items
                            if (data.getStallDetails() != null && data.getStallDetails().getApproval() == 1) {
                                if (data.getMenuItems() != null) {
                                    List<OMenuItem> regularItemsList = new ArrayList<>();
                                    List<OMenuItem> comboItemsList = new ArrayList<>();
                                    for (OMenuItem item : data.getMenuItems()) {
                                        // Added null check for safety
                                        if (item.getCategory() == null) continue;
                                        // Grouping logic (case-insensitive and includes Today's Special)
                                        if ("Combo".equalsIgnoreCase(item.getCategory()) || "Today's Special".equalsIgnoreCase(item.getCategory())) {
                                            comboItemsList.add(item);
                                        } else {
                                            regularItemsList.add(item);
                                        }
                                    }
                                    regularMenuAdapter.submitList(regularItemsList);
                                    comboMenuAdapter.submitList(comboItemsList);
                                    Log.d(TAG, "Updated RecyclerViews. Regular: " + regularItemsList.size() + ", Combo/Special: " + comboItemsList.size());
                                } else {
                                    Log.w(TAG, "Menu items list is null in response data.");
                                    regularMenuAdapter.submitList(new ArrayList<>()); // Clear lists if null
                                    comboMenuAdapter.submitList(new ArrayList<>());
                                }
                            } else {
                                Log.w(TAG, "Stall not approved, menu items not processed.");
                                // UI handled by handleStallStatus
                            }
                        } else {
                            Log.e(TAG, "Failed to load menu data. Response unsuccessful or status not success. Code: " + response.code());
                            statusMessageTextView.setVisibility(View.VISIBLE);
                            statusMessageTextView.setText("Failed to load menu data.");
                            controlsContainer.setVisibility(View.GONE);
                        }
                    }
                    @Override
                    public void onFailure(@NonNull Call<OGetMenuResponse> call, @NonNull Throwable t) {
                        stopLoadingAnimation();
                        Log.e(TAG, "Network Error fetching menu data", t);
                        statusMessageTextView.setVisibility(View.VISIBLE);
                        statusMessageTextView.setText("Network Error: " + t.getMessage());
                        controlsContainer.setVisibility(View.GONE);
                    }
                });
    }

    private void handleStallStatus(OGetMenuResponse.StallDetails details) {
        if (details == null) {
            Log.e(TAG, "StallDetails object is null in handleStallStatus.");
            statusMessageTextView.setText("Could not retrieve stall status.");
            statusMessageTextView.setVisibility(View.VISIBLE);
            controlsContainer.setVisibility(View.GONE);
            return;
        }

        if (details.getApproval() == 1) {
            Log.d(TAG, "Stall is approved. Enabling controls.");
            statusMessageTextView.setVisibility(View.GONE);
            controlsContainer.setVisibility(View.VISIBLE);
            workingTodaySwitch.setChecked(details.isOpenToday());
            operatingHoursContainer.setVisibility(details.isOpenToday() ? View.VISIBLE : View.GONE);
            openingTimeEditText.setText(details.getOpeningHours());
            closingTimeEditText.setText(details.getClosingHours());
        } else {
            controlsContainer.setVisibility(View.GONE);
            statusMessageTextView.setVisibility(View.VISIBLE);
            String message = details.getApproval() == 0 ? "Your stall is pending approval.\nMenu management is disabled." : "Your stall application was rejected.\nMenu management is disabled.";
            Log.w(TAG, "Stall not approved. Status: " + details.getApproval() + ". Message: " + message);
            statusMessageTextView.setText(message);
        }
    }

    // Unchanged methods: showAddEditItemDialog, addItemToServer, updateItemOnServer,
    // showDeleteConfirmationDialog, deleteItemFromServer, updateStallStatus,
    // createPartFromString, prepareFilePart, Cb (Callback class),
    // startLoadingAnimation, stopLoadingAnimation, setupBottomNavigation
    // ... (Keep all these existing methods exactly as they were) ...

    private void showAddEditItemDialog(final OMenuItem itemToEdit, final String defaultCategory) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_add_edit_item, null);
        builder.setView(view);
        selectedImageUri = null; // Reset image URI for each dialog
        final CircleImageView dialogImageView = view.findViewById(R.id.dialogItemImageView);
        currentDialogImageView = dialogImageView; // Store reference for gallery result

        dialogImageView.setOnClickListener(v -> {
            Intent galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            galleryLauncher.launch(galleryIntent);
        });

        final EditText nameEditText = view.findViewById(R.id.dialogItemNameEditText);
        final EditText priceEditText = view.findViewById(R.id.dialogItemPriceEditText);
        final AutoCompleteTextView categoryAutoComplete = view.findViewById(R.id.dialogItemCategoryAutoComplete);

        // Setup category dropdown
        String[] categories = new String[]{"Regular", "Combo", "Today's Special"}; // Make sure these match backend expectations
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, R.layout.dropdown_item, categories);
        categoryAutoComplete.setAdapter(adapter);

        builder.setTitle(itemToEdit == null ? "Add New Item" : "Edit Item");

        // Pre-fill fields if editing
        if (itemToEdit != null) {
            nameEditText.setText(itemToEdit.getName());
            priceEditText.setText(String.valueOf(itemToEdit.getPrice()));
            categoryAutoComplete.setText(itemToEdit.getCategory(), false); // Set text without filtering suggestions
            if (itemToEdit.getItemImage() != null && !itemToEdit.getItemImage().isEmpty()) {
                Glide.with(this)
                        .load(ApiClient.BASE_URL + "uploads/" + itemToEdit.getItemImage())
                        .placeholder(R.drawable.ic_camera_background) // Placeholder
                        .error(R.drawable.ic_camera_background)       // Error image
                        .into(dialogImageView);
            } else {
                dialogImageView.setImageResource(R.drawable.ic_camera_background); // Show placeholder if no image
            }
        } else {
            // Set default category when adding new
            categoryAutoComplete.setText(defaultCategory, false);
            dialogImageView.setImageResource(R.drawable.ic_camera_background); // Show placeholder for new item
        }

        builder.setPositiveButton(itemToEdit == null ? "Add" : "Save", (dialog, which) -> {
            String name = nameEditText.getText().toString().trim();
            String priceStr = priceEditText.getText().toString().trim();
            String category = categoryAutoComplete.getText().toString().trim();

            // Basic Validation
            if(name.isEmpty() || priceStr.isEmpty() || category.isEmpty()){
                Toast.makeText(this,"All fields are required.",Toast.LENGTH_SHORT).show();
                return; // Prevent dialog closing if validation fails (handled implicitly by not calling dismiss)
            }
            try {
                // Ensure price is a valid number
                Double.parseDouble(priceStr);
            } catch (NumberFormatException e) {
                Toast.makeText(this,"Please enter a valid price.",Toast.LENGTH_SHORT).show();
                return;
            }

            // Call appropriate server function
            if (itemToEdit == null) {
                addItemToServer(name, priceStr, category, selectedImageUri);
            } else {
                updateItemOnServer(itemToEdit, name, priceStr, category, selectedImageUri);
            }
        });
        builder.setNegativeButton("Cancel", (dialog, which) -> {
            currentDialogImageView = null; // Clear reference on cancel
            selectedImageUri = null;
        });

        // Ensure dialog doesn't close prematurely if validation fails later
        AlertDialog dialog = builder.create();
        dialog.show();
        // Override positive button click listener AFTER showing to prevent auto-dismissal
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(v -> {
            // Re-validate before calling server
            String name = nameEditText.getText().toString().trim();
            String priceStr = priceEditText.getText().toString().trim();
            String category = categoryAutoComplete.getText().toString().trim();
            boolean valid = true;
            if(name.isEmpty()){ nameEditText.setError("Name required"); valid = false;} else { nameEditText.setError(null);}
            if(priceStr.isEmpty()){ priceEditText.setError("Price required"); valid = false;} else {
                try { Double.parseDouble(priceStr); priceEditText.setError(null); }
                catch (NumberFormatException e) { priceEditText.setError("Invalid price"); valid = false; }
            }
            if(category.isEmpty()){ categoryAutoComplete.setError("Category required"); valid = false;} else { categoryAutoComplete.setError(null);}

            if(valid) {
                if (itemToEdit == null) {
                    addItemToServer(name, priceStr, category, selectedImageUri);
                } else {
                    updateItemOnServer(itemToEdit, name, priceStr, category, selectedImageUri);
                }
                dialog.dismiss(); // Dismiss only if valid and API call initiated
                currentDialogImageView = null; // Clear reference
                selectedImageUri = null;
            }
        });
    }


    private void addItemToServer(String name, String price, String category, @Nullable Uri imageUri) {
        startLoadingAnimation();
        Map<String, RequestBody> fields = new HashMap<>();
        fields.put("action", createPartFromString("add_item"));
        fields.put("stall_id", createPartFromString(stallId));
        fields.put("item_name", createPartFromString(name));
        fields.put("item_price", createPartFromString(price));
        fields.put("item_category", createPartFromString(category));
        MultipartBody.Part imagePart = (imageUri != null) ? prepareFilePart("item_image", imageUri) : null;
        ApiClient.getClient().create(ApiService.class).addMenuItem(fields, imagePart).enqueue(new Cb("Item added!", "Failed to add.", true));
    }

    private void updateItemOnServer(OMenuItem item, String name, String price, String category, @Nullable Uri imageUri) {
        startLoadingAnimation();
        Map<String, RequestBody> fields = new HashMap<>();
        fields.put("action", createPartFromString("update_item"));
        fields.put("stall_id", createPartFromString(stallId));
        fields.put("item_id", createPartFromString(String.valueOf(item.getItemId())));
        fields.put("item_name", createPartFromString(name));
        fields.put("item_price", createPartFromString(price));
        fields.put("item_category", createPartFromString(category));
        fields.put("existing_image_url", createPartFromString(item.getItemImage() != null ? item.getItemImage() : ""));
        MultipartBody.Part imagePart = (imageUri != null) ? prepareFilePart("item_image", imageUri) : null;
        ApiClient.getClient().create(ApiService.class).updateMenuItem(fields, imagePart).enqueue(new Cb("Item updated!", "Failed to update.", true));
    }

    private void showDeleteConfirmationDialog(final OMenuItem item) {
        new AlertDialog.Builder(this)
                .setTitle("Delete Item")
                .setMessage("Are you sure you want to delete '" + item.getName() + "'? This cannot be undone.")
                .setPositiveButton("Delete", (dialog, which) -> deleteItemFromServer(item))
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void deleteItemFromServer(OMenuItem item) {
        startLoadingAnimation();
        ApiClient.getClient().create(ApiService.class).deleteMenuItem("delete_item", item.getItemId()).enqueue(new Cb("Item deleted.", "Failed to delete.", true));
    }

    private void updateStallStatus() {
        startLoadingAnimation();
        String openingTime = openingTimeEditText.getText().toString();
        String closingTime = closingTimeEditText.getText().toString();
        // Perform basic validation if needed
        if (workingTodaySwitch.isChecked() && (openingTime.isEmpty() || closingTime.isEmpty())){
            stopLoadingAnimation();
            Toast.makeText(this, "Please set opening and closing times.", Toast.LENGTH_SHORT).show();
            return;
        }

        ApiClient.getClient().create(ApiService.class).updateOwnerStallStatus("update_stall_status", stallId, workingTodaySwitch.isChecked()?1:0, openingTime, closingTime).enqueue(new Cb("Status updated.","Failed to update status.", false)); // Don't refresh whole list for status update
    }

    // Helper to create RequestBody from String
    private RequestBody createPartFromString(String s) {
        return RequestBody.create(MultipartBody.FORM, s != null ? s : "");
    }

    // Helper to create MultipartBody.Part from Uri
    private MultipartBody.Part prepareFilePart(String partName, Uri fileUri) {
        try {
            // Use FileUtils if needed, otherwise try direct path (might not work with all Uris)
            String realPath = FileUtils.getPath(this, fileUri); // Assuming FileUtils is available
            if (realPath == null) {
                Log.e(TAG, "Could not get real path for URI: " + fileUri);
                Toast.makeText(this, "Could not read file", Toast.LENGTH_SHORT).show();
                return null;
            }
            File file = new File(realPath);
            String mimeType = getContentResolver().getType(fileUri);
            if (mimeType == null) {
                mimeType = "image/*"; // Fallback mime type
            }
            RequestBody requestFile = RequestBody.create(MediaType.parse(mimeType), file);
            return MultipartBody.Part.createFormData(partName, file.getName(), requestFile);
        } catch (Exception e) {
            Log.e(TAG, "Error preparing file part", e);
            Toast.makeText(this, "Error processing image file", Toast.LENGTH_SHORT).show();
            return null;
        }
    }


    // Generic Callback Handler
    private class Cb implements Callback<StatusResponse> {
        private final String successMsg, errorMsg;
        private final boolean refreshOnSuccess;

        Cb(String s, String e, boolean refresh){
            this.successMsg = s;
            this.errorMsg = e;
            this.refreshOnSuccess = refresh;
        }

        @Override
        public void onResponse(@NonNull Call<StatusResponse> call, @NonNull Response<StatusResponse> r) {
            stopLoadingAnimation(); // Ensure animation stops
            if(r.isSuccessful() && r.body() != null && "success".equals(r.body().getStatus())) {
                Toast.makeText(OmenuActivity.this, successMsg, Toast.LENGTH_SHORT).show();
                if (refreshOnSuccess) {
                    fetchDataFromServer(); // Refresh data on success if needed
                }
            } else {
                String errorMessage = errorMsg;
                // Try to get more specific error from response body
                if (r.body() != null && r.body().getMessage() != null) {
                    errorMessage = errorMsg + ": " + r.body().getMessage();
                } else if (r.errorBody() != null) {
                    try {
                        JSONObject errorObj = new JSONObject(r.errorBody().string());
                        errorMessage = errorMsg + ": " + errorObj.optString("message", "Unknown server error.");
                    } catch (Exception ignored) {
                        errorMessage = errorMsg + " (Code: " + r.code() + ")";
                    }
                } else if (!r.isSuccessful()) {
                    errorMessage = errorMsg + " (Code: " + r.code() + ")";
                }
                Log.e(TAG, "API Call Failed: " + errorMessage);
                Toast.makeText(OmenuActivity.this, errorMessage, Toast.LENGTH_LONG).show();
            }
        }
        @Override
        public void onFailure(@NonNull Call<StatusResponse> call, @NonNull Throwable t) {
            stopLoadingAnimation(); // Ensure animation stops
            Log.e(TAG, "API Call Network Failure", t);
            Toast.makeText(OmenuActivity.this, "Network Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    // --- Loading Animation Methods --- (Unchanged)
    private void startLoadingAnimation() {
        if(loadingOverlay != null) {
            ImageView loadingIcon = loadingOverlay.findViewById(R.id.loadingIcon);
            loadingOverlay.setVisibility(View.VISIBLE);
            if (loadingIcon != null) {
                Animation rotation = AnimationUtils.loadAnimation(this, R.anim.hourglass_rotation);
                loadingIcon.startAnimation(rotation);
            } else {
                Log.w(TAG, "loadingIcon inside overlay is null");
            }
        } else {
            Log.w(TAG, "loadingOverlay is null");
        }
    }

    private void stopLoadingAnimation() {
        if (loadingOverlay != null) {
            ImageView loadingIcon = loadingOverlay.findViewById(R.id.loadingIcon);
            if(loadingIcon != null) loadingIcon.clearAnimation();
            loadingOverlay.setVisibility(View.GONE);
        }
    }

    // --- Bottom Navigation (Unchanged) ---
    private void setupBottomNavigation() {
        BottomNavigationView bnv = findViewById(R.id.bottom_navigation_bar);
        bnv.setSelectedItemId(R.id.nav_owner_menu);
        bnv.setOnItemSelectedListener(item -> {
            int i = item.getItemId();
            if(i==R.id.nav_owner_menu) return true;
            else if(i==R.id.nav_owner_home) { startActivity(new Intent(getApplicationContext(), OhomeActivity.class)); overridePendingTransition(0,0); finish(); return true; }
            else if(i==R.id.nav_owner_orders) { startActivity(new Intent(getApplicationContext(), OordersActivity.class)); overridePendingTransition(0,0); finish(); return true; }
            else if(i==R.id.nav_owner_profile) { startActivity(new Intent(getApplicationContext(), OprofileActivity.class)); overridePendingTransition(0,0); finish(); return true; }
            return false;
        });
    }
}
