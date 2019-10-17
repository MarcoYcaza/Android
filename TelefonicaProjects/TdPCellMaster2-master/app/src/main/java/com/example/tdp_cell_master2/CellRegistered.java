package com.example.tdp_cell_master2;

public class CellRegistered {

    private Integer dbm;
    private String type;
    private Integer cid;
    private Integer lac;
    private Integer pci;
    private Integer psc;

    public CellRegistered() {
        dbm = 0;
        type = "null";
        cid = 0;
        lac = 0;
        pci = 0;
        psc = 0;
    }

    public Integer getDbm() {
        return dbm;
    }

    public void setDbm(Integer dbm) {
        this.dbm = dbm;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Integer getCid() {
        return cid;
    }

    public void setCid(Integer cid) {
        this.cid = cid;
    }

    public Integer getLac() {
        return lac;
    }

    public void setLac(Integer lac) {
        this.lac = lac;
    }

    public Integer getPci() {
        return pci;
    }

    public void setPci(Integer pci) {
        this.pci = pci;
    }

    public Integer getPsc() {
        return psc;
    }

    public void setPsc(Integer psc) {
        this.psc = psc;
    }
}
