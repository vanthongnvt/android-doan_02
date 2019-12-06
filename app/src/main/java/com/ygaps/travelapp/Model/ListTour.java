package com.ygaps.travelapp.Model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ListTour {
    @SerializedName("total")
    @Expose
    private Integer total;

    @SerializedName("tours")
    @Expose
    private List<Tour> listtour = null;

    public Integer getTotal() {
        return total;
    }

    public void setTotal(Integer total) {
        this.total = total;
    }

    public List<Tour> getTours() {
        return listtour;
    }

    public void setTours(List<Tour> tours) {
        this.listtour = tours;
    }


}
