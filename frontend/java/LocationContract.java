package com.simats.foodstall;

public interface LocationContract {
    interface View {
        void showLocation(double latitude, double longitude);
        void showLocationError();
        void showLoading(boolean isLoading);
    }

    interface Presenter {
        void requestLocation();
        void detachView();
    }
}