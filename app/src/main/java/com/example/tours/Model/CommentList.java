package com.example.tours.Model;

import java.util.List;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class CommentList {

    @SerializedName("commentList")
    @Expose
    private List<TourComment> commentList = null;

    public List<TourComment> getCommentList() {
        return commentList;
    }

    public void setCommentList(List<TourComment> commentList) {
        this.commentList = commentList;
    }

}