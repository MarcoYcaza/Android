package com.example.my_library;

public class btsItem {
    private String btsName;
    private String uniqueCode;
    private String description;

    public btsItem(String btsName, String uniqueCode, String description) {
        this.btsName = btsName;
        this.uniqueCode = uniqueCode;
        this.description = description;
    }

    public String getBtsName() {
        return btsName;
    }

    public void setBtsName(String btsName) {
        this.btsName = btsName;
    }

    public String getUniqueCode() {
        return uniqueCode;
    }

    public void setUniqueCode(String uniqueCode) {
        this.uniqueCode = uniqueCode;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
