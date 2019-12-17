package com.ygaps.travelapp.Model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class TourNotificationText implements Serializable {

    @SerializedName("type_name")
    @Expose
    private String typeName;
    @SerializedName("notification")
    @Expose
    private String notification;
    @SerializedName("type")
    @Expose
    private String type;
    @SerializedName("tourId")
    @Expose
    private String tourId;
    @SerializedName("userId")
    @Expose
    private Integer userId;

    public String getTypeName() {
        return typeName;
    }

    public void setTypeName(String typeName) {
        this.typeName = typeName;
    }

    public String getNotification() {
        return notification;
    }

    public void setNotification(String notification) {
        this.notification = notification;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getTourId() {
        return tourId;
    }

    public void setTourId(String tourId) {
        this.tourId = tourId;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

}