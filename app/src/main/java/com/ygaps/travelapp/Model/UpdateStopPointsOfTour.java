package com.ygaps.travelapp.Model;
import java.util.List;
        import com.google.gson.annotations.Expose;
        import com.google.gson.annotations.SerializedName;

public class UpdateStopPointsOfTour {

    @SerializedName("tourId")
    @Expose
    private Integer tourId;
    @SerializedName("stopPoints")
    @Expose
    private List<StopPoint> stopPoints = null;
    @SerializedName("deleteIds")
    @Expose
    private List<Integer> deleteIds = null;

    public UpdateStopPointsOfTour(Integer tourId, List<StopPoint> stopPoints, List<Integer> deleteIds) {
        this.tourId = tourId;
        this.stopPoints = stopPoints;
        this.deleteIds = deleteIds;
    }

    public Integer getTourId() {
        return tourId;
    }

    public void setTourId(Integer tourId) {
        this.tourId = tourId;
    }

    public List<StopPoint> getStopPoints() {
        return stopPoints;
    }

    public void setStopPoints(List<StopPoint> stopPoints) {
        this.stopPoints = stopPoints;
    }

    public List<Integer> getDeleteIds() {
        return deleteIds;
    }

    public void setDeleteIds(List<Integer> deleteIds) {
        this.deleteIds = deleteIds;
    }

}