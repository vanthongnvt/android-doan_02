package com.ygaps.travelapp.Model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class CreateTour {
    @SerializedName("hostId")
    @Expose
    private String hostId;
    @SerializedName("status")
    @Expose
    private Number status;
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
    @SerializedName("sourceLat")
    @Expose
    private Number sourceLat;
    @SerializedName("sourceNumber")
    @Expose
    private Number sourceNumber;
    @SerializedName("desLat")
    @Expose
    private Number desLat;
    @SerializedName("desNumber")
    @Expose
    private Number desNumber;
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

    public Number getStatus() {
        return status;
    }

    public void setStatus(Number status) {
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

    public Number getSourceLat() {
        return sourceLat;
    }

    public void setSourceLat(Number sourceLat) {
        this.sourceLat = sourceLat;
    }

    public Number getSourceNumber() {
        return sourceNumber;
    }

    public void setSourceNumber(Number sourceNumber) {
        this.sourceNumber = sourceNumber;
    }

    public Number getDesLat() {
        return desLat;
    }

    public void setDesLat(Number desLat) {
        this.desLat = desLat;
    }

    public Number getDesNumber() {
        return desNumber;
    }

    public void setDesNumber(Number desNumber) {
        this.desNumber = desNumber;
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
