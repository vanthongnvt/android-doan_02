package com.ygaps.travelapp.Model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class TotalTourReview {
    @SerializedName("reviewList")
    @Expose
    private List<TourReview> reviewList = null;

    public List<TourReview> getReviewList() {
        return reviewList;
    }

    public void setReviewList(List<TourReview> reviewList) {
        this.reviewList = reviewList;
    }
}
