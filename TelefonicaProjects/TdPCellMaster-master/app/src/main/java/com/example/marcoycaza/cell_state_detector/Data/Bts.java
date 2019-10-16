package com.example.marcoycaza.cell_state_detector.Data;


import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

import androidx.annotation.NonNull;

@Entity
public class Bts {

    @PrimaryKey
    @NonNull
    private String BtsId;
    private String BtsName;

    public Bts(@NonNull String btsId, String btsName) {
        BtsId = btsId;
        BtsName = btsName;
    }

    public Bts() {
    }

    public String getBtsId() {
        return BtsId;
    }
    public void setBtsId(String btsId) {
        this.BtsId = btsId;
    }
    public String getBtsName() {
        return BtsName;
    }
    public void setBtsName(String btsName) {
        this.BtsName = btsName;
    }
}