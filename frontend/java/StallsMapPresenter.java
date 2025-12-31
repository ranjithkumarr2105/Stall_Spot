package com.simats.foodstall;

import com.simats.foodstall.model.StallLocation;
import org.osmdroid.util.GeoPoint;
import java.util.ArrayList;
import java.util.List;

public class StallsMapPresenter implements StallsMapContract.Presenter, StallsMapContract.Model {

    private StallsMapContract.View view;

    public StallsMapPresenter(StallsMapContract.View view) {
        this.view = view;
    }

    @Override
    public void loadStalls() {
        if (view != null) {
            view.showLoading(true);
            List<StallLocation> stalls = getStallData();
            view.showLoading(false);
            view.displayStallMarkers(stalls);
        }
    }

    @Override
    public void onMarkerClicked(StallLocation stall) {
        // Logic for when a marker is clicked
    }

    @Override
    public List<StallLocation> getStallData() {
        List<StallLocation> stalls = new ArrayList<>();
        // --- UPDATED PART START ---
        // Updated to use the new constructor with a dummy Stall ID
        stalls.add(new StallLocation("SSE01", "Main Canteen", new GeoPoint(13.0415, 80.0925)));
        stalls.add(new StallLocation("SSE02", "Juice Corner", new GeoPoint(13.0410, 80.0930)));
        stalls.add(new StallLocation("SSE03", "Snacks Point", new GeoPoint(13.0420, 80.0920)));
        // --- UPDATED PART END ---
        return stalls;
    }
}