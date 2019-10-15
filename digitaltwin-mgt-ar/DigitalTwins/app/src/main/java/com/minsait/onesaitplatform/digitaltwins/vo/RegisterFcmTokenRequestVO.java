package com.minsait.onesaitplatform.digitaltwins.vo;

import java.io.Serializable;

public class RegisterFcmTokenRequestVO implements Serializable {
    private RegisterFcmTokenRequestDataVO ST_NativeNotifKeys;

    public RegisterFcmTokenRequestVO() {
        ST_NativeNotifKeys = new RegisterFcmTokenRequestDataVO();
    }

    public RegisterFcmTokenRequestDataVO getST_NativeNotifKeys() {
        return ST_NativeNotifKeys;
    }

    public void setST_NativeNotifKeys(RegisterFcmTokenRequestDataVO ST_NativeNotifKeys) {
        this.ST_NativeNotifKeys = ST_NativeNotifKeys;
    }
}