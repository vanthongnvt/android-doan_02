package com.ygaps.travelapp.Model;


import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class MemberLocation implements Serializable {
    @SerializedName("lat")
    @Expose
    private double lat;

    @SerializedName("long")
    @Expose
    private double mlong;

    @SerializedName("id")
    @Expose
    private Integer id;

    public double getLat() {
        return lat;
    }

    public double getLong() {
        return mlong;
    }

    public Integer getId() {
        return id;
    }
}
