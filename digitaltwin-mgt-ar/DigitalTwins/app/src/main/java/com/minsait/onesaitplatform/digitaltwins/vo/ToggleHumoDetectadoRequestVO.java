package com.minsait.onesaitplatform.digitaltwins.vo;

import java.io.Serializable;

public class ToggleHumoDetectadoRequestVO implements Serializable {
    private String name;
    private String data;

    public ToggleHumoDetectadoRequestVO() {
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