package com.minsait.onesaitplatform.digitaltwins.vo;

import java.io.Serializable;

public class RegisterFcmTokenRequestDataVO implements Serializable {
    private String app;
    private String deviceToken;
    private String user;

    public String getApp() {
        return app;
    }

    public void setApp(String app) {
        this.app = app;
    }

    public String getDeviceToken() {
        return deviceToken;
    }

    public void setDeviceToken(String deviceToken) {
        this.deviceToken = deviceToken;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }
}