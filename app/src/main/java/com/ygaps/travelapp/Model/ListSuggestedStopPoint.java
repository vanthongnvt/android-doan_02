package com.ygaps.travelapp.Model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ListSuggestedStopPoint {
    @SerializedName("total")
    @Expose
    private Integer total;

    @SerializedName("stopPoints")
    @Expose
    private List<StopPoint> stopPoints = null;

    public Integer getTotal() {
        return total;
    }

    public List<StopPoint> getStopPoints() {
        return stopPoints;
    }
}
