package com.example.tours.Model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ListReviewPoint {
    @SerializedName("pointStats")
    @Expose
    private List<ReviewPoint> pointStats=null;

    public List<ReviewPoint> getPointStats() {
        return pointStats;
    }
}
