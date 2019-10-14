package com.minsait.onesaitplatform.digitaltwins.vo;

import java.io.Serializable;

// Clase para mapear la respuesta JSON en un objeto Java.
public class OAuthResponseParametersVO implements Serializable {
    private String grant_type;
    private String username;

    public String getGrant_type() {
        return grant_type;
    }

    public void setGrant_type(String grant_type) {
        this.grant_type = grant_type;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}