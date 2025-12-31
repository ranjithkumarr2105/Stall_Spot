package com.simats.foodstall;

import android.annotation.SuppressLint;
import android.app.Activity;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;

public class LocationPresenter implements LocationContract.Presenter {

    private LocationContract.View view;
    private FusedLocationProviderClient fusedLocationClient;

    public LocationPresenter(LocationContract.View view, Activity activity) {
        this.view = view;
        this.fusedLocationClient = LocationServices.getFusedLocationProviderClient(activity);
    }

    @SuppressLint("MissingPermission") // Permissions are checked in the Activity
    @Override
    public void requestLocation() {
        if (view != null) {
            view.showLoading(true);
            fusedLocationClient.getLastLocation()
                    .addOnSuccessListener(location -> {
                        if (view != null) {
                            view.showLoading(false);
                            if (location != null) {
                                view.showLocation(location.getLatitude(), location.getLongitude());
                            } else {
                                view.showLocationError();
                            }
                        }
                    })
                    .addOnFailureListener(e -> {
                        if (view != null) {
                            view.showLoading(false);
                            view.showLocationError();
                        }
                    });
        }
    }

    @Override
    public void detachView() {
        this.view = null; // To avoid memory leaks
    }
}