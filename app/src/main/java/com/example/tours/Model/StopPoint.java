package com.example.tours.Model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class StopPoint implements Serializable {

    @SerializedName("tourId")
    @Expose
    private Integer tourId;
    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("address")
    @Expose
    private String address;
    @SerializedName("long")
    @Expose
    private Double longitude;
    @SerializedName("lat")
    @Expose
    private Double latitude;
    @SerializedName("contact")
    @Expose
    private String contact;
    @SerializedName("minCost")
    @Expose
    private Integer minCost;
    @SerializedName("maxCost")
    @Expose
    private Integer maxCost;
    @SerializedName("arrivalAt")
    @Expose
    private Long arrivalAt;
    @SerializedName("leaveAt")
    @Expose
    private Long leaveAt;
    @SerializedName("serviceTypeId")
    @Expose
    private Integer serviceTypeId;
    @SerializedName("provinceId")
    @Expose
    private Integer provinceId;
    @SerializedName("avatar")
    @Expose
    private String avatar;

    public StopPoint(Integer id, String name, String address,Integer provinceId, Double longitude, Double latitude, String contact, Integer minCost, Integer maxCost, Long arrivalAt,Long leaveAt, Integer serviceTypeId) {
        this.id = id;
        this.name = name;
        this.address = address;
        this.provinceId=provinceId;
        this.longitude = longitude;
        this.latitude = latitude;
        this.contact = contact;
        this.minCost = minCost;
        this.maxCost = maxCost;
        this.arrivalAt=arrivalAt;
        this.leaveAt=leaveAt;
        this.serviceTypeId = serviceTypeId;
    }

    public Integer getTourId() {
        return tourId;
    }

    public void setTourId(Integer tourId) {
        this.tourId = tourId;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public String getContact() {
        return contact;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }

    public Integer getMinCost() {
        return minCost;
    }

    public void setMinCost(Integer minCost) {
        this.minCost = minCost;
    }

    public Integer getMaxCost() {
        return maxCost;
    }

    public void setMaxCost(Integer maxCost) {
        this.maxCost = maxCost;
    }

    public Long getArrivalAt() {
        return arrivalAt;
    }

    public void setArrivalAt(Long arrivalAt) {
        this.arrivalAt = arrivalAt;
    }

    public Long getLeaveAt() {
        return leaveAt;
    }

    public void setLeaveAt(Long leaveAt) {
        this.leaveAt = leaveAt;
    }

    public Integer getServiceTypeId() {
        return serviceTypeId;
    }

    public void setServiceTypeId(Integer serviceTypeId) {
        this.serviceTypeId = serviceTypeId;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public Integer getProvinceId() {
        return provinceId;
    }

    public void setProvinceId(Integer provinceId) {
        this.provinceId = provinceId;
    }

}