package com.simats.foodstall.model;

import com.google.gson.annotations.SerializedName;
import java.util.List;

/**
 * This class represents the full JSON response from the Uget_favorite_stalls.php script.
 */
public class FavoriteStallsResponse {

    @SerializedName("status")
    private String status;

    // This will hold the list of favorite stall objects from the JSON
    @SerializedName("favorites")
    private List<FavoriteStall> favorites;

    // Getters
    public String getStatus() {
        return status;
    }

    public List<FavoriteStall> getFavorites() {
        return favorites;
    }
}