package com.example.tours.Model;

import java.util.List;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ListTourInvitation {

    @SerializedName("total")
    @Expose
    private Integer total;
    @SerializedName("tours")
    @Expose
    private List<TourInvitation> tours = null;

    public Integer getTotal() {
        return total;
    }

    public void setTotal(Integer total) {
        this.total = total;
    }

    public List<TourInvitation> getTours() {
        return tours;
    }

    public void setTours(List<TourInvitation> tours) {
        this.tours = tours;
    }

}
