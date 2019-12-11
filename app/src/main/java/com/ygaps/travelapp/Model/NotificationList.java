package com.ygaps.travelapp.Model;

import java.util.List;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class NotificationList {

    @SerializedName("notiList")
    @Expose
    private List<Notification> notiList = null;

    public List<Notification> getNotiList() {
        return notiList;
    }

    public void setNotiList(List<Notification> notiList) {
        this.notiList = notiList;
    }

}