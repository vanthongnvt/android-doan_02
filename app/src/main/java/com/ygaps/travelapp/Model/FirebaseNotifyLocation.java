package com.ygaps.travelapp.Model;

import java.io.Serializable;
import java.util.List;

public class FirebaseNotifyLocation implements Serializable {

    Integer type;

    List<MemberLocation> memPos=null;

    public Integer getType() {
        return type;
    }

    public List<MemberLocation> getMemPos() {
        return memPos;
    }

    public void setMemPos(List<MemberLocation> memPos) {
        this.memPos = memPos;
    }
}
