package com.example.tours.Model;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class UserListTour {

    @SerializedName("total")
    @Expose
    private Integer total;
    @SerializedName("tours")
    @Expose
    private List<UserTour> tours = null;

    public Integer getTotal() {
        return total;
    }

    public void setTotal(Integer total) {
        this.total = total;
    }

    public List<UserTour> getTours() {
        return tours;
    }

    public void setTours(List<UserTour> tours) {
        this.tours = tours;
    }

}