package com.simats.foodstall;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;

import com.simats.foodstall.model.StallLocation; // UPDATED

import org.osmdroid.api.IMapController;
import org.osmdroid.config.Configuration;
import org.osmdroid.util.BoundingBox;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;

import java.util.List;

public class UStallsMapActivity extends AppCompatActivity implements StallsMapContract.View {

    private StallsMapContract.Presenter presenter;
    private MapView map = null;
    private IMapController mapController;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Context ctx = getApplicationContext();
        Configuration.getInstance().load(ctx, PreferenceManager.getDefaultSharedPreferences(ctx));

        setContentView(R.layout.ustalls_map);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(v -> onBackPressed());

        map = findViewById(R.id.map);
        progressBar = findViewById(R.id.progressBar);

        presenter = new StallsMapPresenter(this);

        setupMapView();

        presenter.loadStalls();
    }

    private void setupMapView() {
        mapController = map.getController();
        GeoPoint campusCenter = new GeoPoint(13.0415, 80.0925);
        mapController.setZoom(17.5);
        mapController.setCenter(campusCenter);
        map.setMultiTouchControls(true);
        final BoundingBox campusBounds = new BoundingBox(13.0450, 80.0960, 13.0380, 80.0890);
        map.setScrollableAreaLimitDouble(campusBounds);
    }

    @Override
    public void displayStallMarkers(List<StallLocation> stalls) { // UPDATED
        for (StallLocation stall : stalls) { // UPDATED
            Marker stallMarker = new Marker(map);
            stallMarker.setPosition(stall.getLocation());
            stallMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
            stallMarker.setTitle(stall.getName());

                Drawable icon = ContextCompat.getDrawable(this, R.drawable.ic_stall_marker);
            stallMarker.setIcon(icon);

            stallMarker.setOnMarkerClickListener((marker, mapView) -> {
                presenter.onMarkerClicked(stall);
                Toast.makeText(UStallsMapActivity.this, "You tapped on " + stall.getName(), Toast.LENGTH_SHORT).show();
                return true;
            });

            map.getOverlays().add(stallMarker);
        }
        map.invalidate();
    }

    @Override
    public void showLoading(boolean isLoading) {
        progressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
    }

    @Override
    public void showErrorMessage(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (map != null) {
            map.onResume();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (map != null) {
            map.onPause();
        }
    }
}
