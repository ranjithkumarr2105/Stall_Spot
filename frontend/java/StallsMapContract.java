package com.simats.foodstall;

import com.simats.foodstall.model.StallLocation; // UPDATED
import java.util.List;

public interface StallsMapContract {

    // Methods the Presenter can call on the View
    interface View {
        void displayStallMarkers(List<StallLocation> stalls); // UPDATED
        void showLoading(boolean isLoading);
        void showErrorMessage(String message);
    }

    // Methods the View can call on the Presenter
    interface Presenter {
        void loadStalls();
        void onMarkerClicked(StallLocation stall); // UPDATED
    }

    // Methods the Presenter can use to get data from the Model
    interface Model {
        List<StallLocation> getStallData(); // UPDATED
    }
}
