package com.ygaps.travelapp.Model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class FirebaseNotificationOnRoad implements Serializable {

    @SerializedName("type")
    @Expose
    private Integer type;
    @SerializedName("long")
    @Expose
    private double _long;
    @SerializedName("speed")
    @Expose
    private Integer speed;
    @SerializedName("note")
    @Expose
    private String note;
    @SerializedName("lat")
    @Expose
    private double lat;
    @SerializedName("tourId")
    @Expose
    private String tourId;
    @SerializedName("userId")
    @Expose
    private String userId;

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public double getLong() {
        return  _long;
    }

    public void setLong(double _long) {
        this._long = _long;
    }

    public Integer getSpeed() {
        if(speed!=null) {
            return speed;
        }
        return 0;
    }

    public void setSpeed(Integer speed) {
        this.speed = speed;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public String getTourId() {
        return tourId;
    }

    public void setTourId(String tourId) {
        this.tourId = tourId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

}
