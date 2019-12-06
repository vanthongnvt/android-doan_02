package com.ygaps.travelapp.Model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class GetStatusTours {
    @SerializedName("totalToursGroupedByStatus")
    @Expose
    private List<TotalToursGroupedBystatus> totalToursGroupedByStatus = null;

    public List<TotalToursGroupedBystatus> getTotalToursGroupedByStatus() {
        return totalToursGroupedByStatus;
    }

    public void setTotalToursGroupedByStatus(List<TotalToursGroupedBystatus> totalToursGroupedByStatus) {
        this.totalToursGroupedByStatus = totalToursGroupedByStatus;
    }
}
