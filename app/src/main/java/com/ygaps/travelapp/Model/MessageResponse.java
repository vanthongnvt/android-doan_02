package com.ygaps.travelapp.Model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class MessageResponse {

    @SerializedName("message")
    @Expose
    private String message;
    @SerializedName("resCode")
    @Expose
    private Integer resCode;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Integer getResCode(){return resCode;}

}