package com.simats.foodstall;

import com.simats.foodstall.model.StallLocation;
import org.osmdroid.util.GeoPoint;
import java.util.ArrayList;
import java.util.List;

// This class is a temporary manager for stall locations until a backend is connected.
public class StallLocationManager {

    private static final List<StallLocation> stallLocations = new ArrayList<>();

    // Initialize with some default locations
    static {
        // --- UPDATED PART START ---
        // The constructor calls have been updated to include a Stall ID as the first argument.
        stallLocations.add(new StallLocation("SSE01", "Main Canteen", new GeoPoint(13.0415, 80.0925)));
        stallLocations.add(new StallLocation("SSE02", "Juice Corner", new GeoPoint(13.0410, 80.0930)));
        stallLocations.add(new StallLocation("SSE03", "Snacks Point", new GeoPoint(13.0420, 80.0920)));
        // --- UPDATED PART END ---
    }

    public static List<StallLocation> getStallLocations() {
        return stallLocations;
    }

    public static void addOrUpdateStallLocation(StallLocation newLocation) {
        boolean found = false;
        for (int i = 0; i < stallLocations.size(); i++) {
            if (stallLocations.get(i).getName().equalsIgnoreCase(newLocation.getName())) {
                // Update the existing location
                stallLocations.set(i, newLocation);
                found = true;
                break;
            }
        }
        if (!found) {
            // Add as a new location
            stallLocations.add(newLocation);
        }
    }
}