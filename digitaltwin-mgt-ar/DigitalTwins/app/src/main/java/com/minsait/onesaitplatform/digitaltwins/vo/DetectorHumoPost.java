package com.minsait.onesaitplatform.digitaltwins.vo;

import java.io.Serializable;

public class DetectorHumoPost implements Serializable {
    private String name;
    private String data;

    public DetectorHumoPost() {
        setName("toggleHumoDetectado");
        setData("");
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }
}