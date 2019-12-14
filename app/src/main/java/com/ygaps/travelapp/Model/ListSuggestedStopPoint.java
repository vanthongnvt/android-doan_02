package com.ygaps.travelapp.Model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ListSuggestedStopPoint {
    @SerializedName("stopPoints")
    @Expose
    private List<StopPoint> stopPoints = null;

    public List<StopPoint> getStopPoints() {
        return stopPoints;
    }
}
