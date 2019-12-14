package com.ygaps.travelapp.Model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class NotificationOnRoadList  implements Serializable {
    @SerializedName("notiList")
    @Expose
    private List<NotificationOnRoad> notiList = null;

    public List<NotificationOnRoad> getNotiList() {
        return notiList;
    }

    public void setNotiList(List<NotificationOnRoad> notiList) {
        this.notiList = notiList;
    }
}
