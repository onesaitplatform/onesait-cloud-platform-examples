package com.minsait.onesaitplatform.digitaltwins.vo;

import java.io.Serializable;

public class Temperatura implements Serializable {
    private String fecha;
    private float grados;

    public Temperatura() {
    }

    public Temperatura(String fecha, float grados) {
        this.fecha = fecha;
        this.grados = grados;
    }

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

    public float getGrados() {
        return grados;
    }

    public void setGrados(float grados) {
        this.grados = grados;
    }
}
