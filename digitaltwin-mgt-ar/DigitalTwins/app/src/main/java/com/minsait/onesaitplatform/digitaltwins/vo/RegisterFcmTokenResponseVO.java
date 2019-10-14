package com.minsait.onesaitplatform.digitaltwins.vo;

import java.io.Serializable;
import java.util.ArrayList;

public class RegisterFcmTokenResponseVO implements Serializable {
    private int count;
    private ArrayList<String> ids;

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public ArrayList<String> getIds() {
        return ids;
    }

    public void setIds(ArrayList<String> ids) {
        this.ids = ids;
    }
}