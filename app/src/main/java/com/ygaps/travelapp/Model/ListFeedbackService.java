package com.ygaps.travelapp.Model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ListFeedbackService {
    @SerializedName("feedbackList")
    @Expose
    private List<FeedbackService> feedbackList = null;

    public List<FeedbackService> getFeedbackList() {
        return feedbackList;
    }

    public void setFeedbackList(List<FeedbackService> feedbackList) {
        this.feedbackList = feedbackList;
    }
}
