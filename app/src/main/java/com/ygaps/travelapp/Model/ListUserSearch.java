package com.ygaps.travelapp.Model;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ListUserSearch {

    @SerializedName("total")
    @Expose
    private Integer total;
    @SerializedName("users")
    @Expose
    private List<User> users = null;

    public Integer getTotal() {
        return total;
    }

    public void setTotal(Integer total) {
        this.total = total;
    }

    public List<User> getUsers() {
        return users;
    }

    public void setUsers(List<User> users) {
        this.users = users;
    }

}
