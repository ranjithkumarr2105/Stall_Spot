package com.simats.foodstall;

import android.content.Context;
import android.content.SharedPreferences;

import org.osmdroid.util.GeoPoint;

// This class will save and retrieve the user's chosen location
public class LocationManager {

    private static final String PREFS_NAME = "LocationPrefs";
    private static final String KEY_LATITUDE = "user_latitude";
    private static final String KEY_LONGITUDE = "user_longitude";

    // Save the user's location
    public static void saveUserLocation(Context context, GeoPoint location) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(KEY_LATITUDE, String.valueOf(location.getLatitude()));
        editor.putString(KEY_LONGITUDE, String.valueOf(location.getLongitude()));
        editor.apply();
    }

    // Retrieve the user's location
    public static GeoPoint getUserLocation(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        String lat = prefs.getString(KEY_LATITUDE, null);
        String lon = prefs.getString(KEY_LONGITUDE, null);

        if (lat != null && lon != null) {
            return new GeoPoint(Double.parseDouble(lat), Double.parseDouble(lon));
        }
        return null; // Return null if no location is saved
    }
}