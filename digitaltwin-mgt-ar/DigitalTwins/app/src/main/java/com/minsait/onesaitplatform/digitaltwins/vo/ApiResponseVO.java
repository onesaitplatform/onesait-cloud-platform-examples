package com.minsait.onesaitplatform.digitaltwins.vo;

import java.io.Serializable;
import java.util.ArrayList;

// Clase para mapear la respuesta JSON en un objeto Java en la consulta del token de Api.
public class ApiResponseVO implements Serializable {
    private ArrayList<String> userTokens;

    public ArrayList<String> getUserTokens() {
        return userTokens;
    }

    public void setUserTokens(ArrayList<String> userTokens) {
        this.userTokens = userTokens;
    }
}
