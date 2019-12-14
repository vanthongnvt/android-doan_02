package com.ygaps.travelapp.Model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;


public class RequestCoordinateList {
    @SerializedName("hasOneCoordinate")
    @Expose
    private boolean hasOneCoordinate;
    @SerializedName("coordList")
    @Expose
    private Coordinate coordList = null;

    public RequestCoordinateList(boolean hasOneCoordinate, Coordinate coordList) {
        this.hasOneCoordinate = hasOneCoordinate;
        this.coordList = coordList;
    }
}
