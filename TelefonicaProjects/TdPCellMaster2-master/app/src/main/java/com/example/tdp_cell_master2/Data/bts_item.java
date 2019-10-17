package com.example.tdp_cell_master2.Data;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class bts_item {
    @PrimaryKey
    @NonNull
    private String BtsId;
    private String BtsName;

    public bts_item(@NonNull String btsId, String btsName) {
        BtsId = btsId;
        BtsName = btsName;
    }

    public bts_item() {  /* Constructor vacío*/
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
