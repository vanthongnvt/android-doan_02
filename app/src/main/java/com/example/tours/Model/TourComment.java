package com.example.tours.Model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class TourComment implements Serializable {

    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("comment")
    @Expose
    private String comment;
    @SerializedName("avatar")
    @Expose
    private String avatar;
    @SerializedName("createdOn")
    @Expose
    private String createdOn;

    public TourComment(Integer id, String name, String comment, String avatar, String createdOn) {
        this.id = id;
        this.name = name;
        this.comment = comment;
        this.avatar = avatar;
        this.createdOn = createdOn;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        if(name==null){
            return "<ID: "+ id+" >";
        }
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getCreatedOn(){
        Date d = new Date(Long.parseLong(createdOn));
        DateFormat f = new SimpleDateFormat("dd/MM/yy HH:mm", Locale.getDefault());
        return f.format(d);
    }

}