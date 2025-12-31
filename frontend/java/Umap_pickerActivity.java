package com.simats.foodstall;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.osmdroid.api.IMapController;
import org.osmdroid.bonuspack.location.GeocoderNominatim;
import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.compass.CompassOverlay;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class Umap_pickerActivity extends AppCompatActivity {

    private static final int REQUEST_PERMISSIONS_REQUEST_CODE = 1;
    private MapView map = null;
    private IMapController mapController;
    private FusedLocationProviderClient fusedLocationClient;
    private EditText searchEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // This is a crucial step for osmdroid to work
        Context ctx = getApplicationContext();
        Configuration.getInstance().load(ctx, PreferenceManager.getDefaultSharedPreferences(ctx));

        setContentView(R.layout.umap_picker);

        // --- Initialize Views from the Canvas layout ---
        map = findViewById(R.id.map);
        searchEditText = findViewById(R.id.searchEditText);
        ImageButton searchButton = findViewById(R.id.searchButton);
        FloatingActionButton myLocationButton = findViewById(R.id.myLocationButton);
        ExtendedFloatingActionButton confirmButton = findViewById(R.id.confirmButton);

        // --- Setup Map Controller and Standard View ---
        mapController = map.getController();
        mapController.setZoom(18.0);
        map.setTileSource(TileSourceFactory.MAPNIK); // Use the standard road map
        map.setMultiTouchControls(true);

        // Add Compass Overlay (the direction/compass button)
        CompassOverlay compassOverlay = new CompassOverlay(this, map);
        compassOverlay.enableCompass();
        map.getOverlays().add(compassOverlay);

        // Set initial center to Chennai, India
        GeoPoint startPoint = new GeoPoint(13.0827, 80.2707);
        mapController.setCenter(startPoint);

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        // --- Setup Listeners for all buttons ---
        myLocationButton.setOnClickListener(v -> fetchCurrentLocation());
        confirmButton.setOnClickListener(v -> confirmLocationSelection());
        searchButton.setOnClickListener(v -> performSearch());

        // Handle the search action from the keyboard
        searchEditText.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                performSearch();
                return true;
            }
            return false;
        });

        requestPermissionsIfNecessary(new String[]{Manifest.permission.ACCESS_FINE_LOCATION});
    }

    private void performSearch() {
        String locationName = searchEditText.getText().toString().trim();
        if (!locationName.isEmpty()) {
            new GeocodeTask().execute(locationName);
        } else {
            Toast.makeText(this, "Please enter a location to search", Toast.LENGTH_SHORT).show();
        }
    }

    private void fetchCurrentLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            fusedLocationClient.getLastLocation().addOnSuccessListener(this, location -> {
                if (location != null) {
                    GeoPoint currentLocation = new GeoPoint(location.getLatitude(), location.getLongitude());
                    mapController.animateTo(currentLocation);
                    Toast.makeText(this, "Centered on your location", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "Could not get current location. Please enable GPS.", Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            requestPermissionsIfNecessary(new String[]{Manifest.permission.ACCESS_FINE_LOCATION});
        }
    }

    private void confirmLocationSelection() {
        GeoPoint selectedPoint = (GeoPoint) map.getMapCenter();
        new ReverseGeocodeTask().execute(selectedPoint);
    }

    @Override
    public void onResume() {
        super.onResume();
        map.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        map.onPause();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_PERMISSIONS_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                fetchCurrentLocation();
            } else {
                Toast.makeText(this, "Location permission is required for this feature.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void requestPermissionsIfNecessary(String[] permissions) {
        for (String permission : permissions) {
            if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, permissions, REQUEST_PERMISSIONS_REQUEST_CODE);
                return;
            }
        }
    }

    // AsyncTask to handle searching for a location by name
    private class GeocodeTask extends AsyncTask<String, Void, Address> {
        @Override
        protected Address doInBackground(String... params) {
            GeocoderNominatim geocoder = new GeocoderNominatim(Locale.getDefault(), "StallSpotAndroidApp/1.0");
            try {
                List<Address> addresses = geocoder.getFromLocationName(params[0], 1);
                if (addresses != null && !addresses.isEmpty()) {
                    return addresses.get(0);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Address address) {
            if (address != null) {
                GeoPoint location = new GeoPoint(address.getLatitude(), address.getLongitude());
                mapController.animateTo(location);
            } else {
                Toast.makeText(Umap_pickerActivity.this, "Location not found.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    // AsyncTask to get the address from the map's center coordinates
    private class ReverseGeocodeTask extends AsyncTask<GeoPoint, Void, String> {
        private GeoPoint point;
        @Override
        protected String doInBackground(GeoPoint... params) {
            point = params[0];
            GeocoderNominatim geocoder = new GeocoderNominatim(Locale.getDefault(), "StallSpotAndroidApp/1.0");
            try {
                List<Address> addresses = geocoder.getFromLocation(point.getLatitude(), point.getLongitude(), 1);
                if (addresses != null && !addresses.isEmpty()) {
                    return addresses.get(0).getAddressLine(0);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            return "Address not found";
        }

        @Override
        protected void onPostExecute(String address) {
            Intent resultIntent = new Intent();
            resultIntent.putExtra("latitude", point.getLatitude());
            resultIntent.putExtra("longitude", point.getLongitude());
            resultIntent.putExtra("address", address);
            setResult(RESULT_OK, resultIntent);
            finish();
        }
    }
}

