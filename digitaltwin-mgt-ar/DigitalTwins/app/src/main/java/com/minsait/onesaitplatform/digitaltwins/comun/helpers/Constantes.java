package com.minsait.onesaitplatform.digitaltwins.comun.helpers;

// Clase que contiene contantes para la aplicación.
public class Constantes {
    /* Urls base para los Digital Twins */

    /*public static final String BASE_URL_ALARMA = "http://onesaitrevolution.ddns.net:21002";
    public static final String BASE_URL_DETECTOR_HUMO = "http://onesaitrevolution.ddns.net:21001";
    public static final String BASE_URL_TERMOSTATO = "http://onesaitrevolution.ddns.net:21000";
    */
    //public static final String BASE_URL_DETECTOR_HUMO = "https://lab.onesaitplatform.com/DetectorHumo";
    public static final String BASE_URL_DETECTOR_HUMO = "http://onesaitrevolution.ddns.net:21001";

    /* FIN: Urls base para los Digital Twins */
    /* Dirección de los recursos para los Digital Twins */
    public static final String RECURSO_ALARMA = "/Alarma/actions";
    public static final String RECURSO_DETECTOR_HUMO = "/DetectorHumo/actions";
    public static final String RECURSO_TERMOSTATO = "/termostato/actions";
    /* FIN: Dirección de los recursos para los Digital Twins */

    // URL para obtener el token OAuth
    public static final String URL_OAUTH = "https://lab.onesaitplatform.com/oauth-server/oauth/token";
    // URL para obtener el token de usuario para las APIs.
    public static final String URL_USER_API_TOKEN = "https://lab.onesaitplatform.com/controlpanel/api/apis/api/tokens";
    // URL para registrar el token de FireBase.
    public static final String URL_ONESAIT_PLATFORM_API_REGISTER_FCM_TOKEN = "https://lab.onesaitplatform.com/api-manager/server/api/v2/ST_RegisterNotificationClientToken/";
    public static final String URL_ONESAIT_PLATFORM_API_REGISTER_FCM_APP_PARAMETER = "Stranger Team messaging";
    public static final String URL_ONESAIT_PLATFORM_API_REGISTER_FCM_USER_PARAMETER = "Stranger Team";
    public static final String URL_TEMPERATURA_DASH_BOARD = "https://lab.onesaitplatform.com/controlpanel/api/dashboards/dashboard/generateDashboardImage/1eba83cd-6784-4e19-81e3-449a8967512b";
    public static final String PARAMS_TEMPERATURA_DASH_BOARD = "?waittime=10000&height=794&width=1123&fullpage=false";

    public static final String TAG_OAUTH = "TAG_OAUTH";
    public static final String TAG_USER_API = "TAG_USER_API";

    public static final String BODY_OAUTH_WWW_FORM_PARAMETERS = "username=strangerTeam&password=Stranger_001&grant_type=password&scope=openid";

    // Content-Types para enviar en ciertas cabeceras HTTP.
    public static final String HEADER_APPLICATION_X_WWW_FORM_URLENCODED = "application/x-www-form-urlencoded";

    public static final String HEADER_AUTHORIZATION = "Basic b25lc2FpdHBsYXRmb3JtOm9uZXNhaXRwbGF0Zm9ybQ==";

    public static final String HEADER_NAME_AUTHORIZATION = "Authorization";

    public static final int CONNECTION_TIMEOUT = 12000;
}
