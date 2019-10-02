package com.example.my_library;

public class btsItem {
    private String btsName;
    private String uniqueCode;
    private String coordenadas;
    private String departamento;
    private String direccion;
    private String controlador;


    public btsItem(String btsName, String uniqueCode, String coordenadas, String departamento, String direccion, String controlador) {
        this.btsName = btsName;
        this.uniqueCode = uniqueCode;
        this.coordenadas = coordenadas;
        this.departamento = departamento;
        this.direccion = direccion;
        this.controlador = controlador;
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

    public String getCoordenadas() {
        return coordenadas;
    }

    public void setCoordenadas(String coordenadas) {
        this.coordenadas = coordenadas;
    }

    public String getDepartamento() {
        return departamento;
    }

    public void setDepartamento(String departamento) {
        this.departamento = departamento;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public String getControlador() {
        return controlador;
    }

    public void setControlador(String controlador) {
        this.controlador = controlador;
    }
}
