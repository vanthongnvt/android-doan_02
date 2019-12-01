package com.example.tours.Model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class UpdateUserTour {

    @SerializedName("hostId")
    @Expose
    private String hostId;
    @SerializedName("status")
    @Expose
    private Integer status;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("minCost")
    @Expose
    private Number minCost;
    @SerializedName("maxCost")
    @Expose
    private Number maxCost;
    @SerializedName("startDate")
    @Expose
    private Number startDate;
    @SerializedName("endDate")
    @Expose
    private Number endDate;
    @SerializedName("adults")
    @Expose
    private Number adults;
    @SerializedName("childs")
    @Expose
    private Number childs;
    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("isPrivate")
    @Expose
    private Boolean isPrivate;
    @SerializedName("avatar")
    @Expose
    private String avatar;

    public String getHostId() {
        return hostId;
    }

    public void setHostId(String hostId) {
        this.hostId = hostId;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Number getMinCost() {
        return minCost;
    }

    public void setMinCost(Number minCost) {
        this.minCost = minCost;
    }

    public Number getMaxCost() {
        return maxCost;
    }

    public void setMaxCost(Number maxCost) {
        this.maxCost = maxCost;
    }

    public Number getStartDate() {
        return startDate;
    }

    public void setStartDate(Number startDate) {
        this.startDate = startDate;
    }

    public Number getEndDate() {
        return endDate;
    }

    public void setEndDate(Number endDate) {
        this.endDate = endDate;
    }

    public Number getAdults() {
        return adults;
    }

    public void setAdults(Number adults) {
        this.adults = adults;
    }

    public Number getChilds() {
        return childs;
    }

    public void setChilds(Number childs) {
        this.childs = childs;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Boolean getIsPrivate() {
        return isPrivate;
    }

    public void setIsPrivate(Boolean isPrivate) {
        this.isPrivate = isPrivate;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

}
