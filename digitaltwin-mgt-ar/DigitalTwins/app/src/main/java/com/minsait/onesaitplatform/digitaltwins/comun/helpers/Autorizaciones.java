package com.minsait.onesaitplatform.digitaltwins.comun.helpers;

import java.util.concurrent.ConcurrentHashMap;

// Clase Thread Safe para gestionar las autorizaciones y obtenerlas fácilmente desde cualquier hilo.
// La clase implementa el patrón Singleton y carga perezosa (Lazy Loading).
public class Autorizaciones {

    public static final String OAUTH_TOKEN = "OAUTH_TOKEN";
    public static final String OAUTH_TOKEN_TYPE = "OAUTH_TOKEN_TYPE";
    public static final String API_TOKEN = "API_TOKEN";

    // Variable privada con la instancia.
    private static volatile Autorizaciones instance;

    // HashMap que contiene las autorizaciones.
    private ConcurrentHashMap<String, String> map;

    // Constructor privado para evitar que se instancie desde fuera.
    private Autorizaciones() {
        // Creamos el HashMap y le añadimos los token iniciales vacíos.
        map = new ConcurrentHashMap<String, String>();
        map.put(OAUTH_TOKEN, "");
        map.put(OAUTH_TOKEN_TYPE, "");
        map.put(API_TOKEN, "");
    }

    public ConcurrentHashMap<String, String> getMap() {
        return map;
    }

    /**
     * This inner class is loaded only after getInstance() is called for the first time.
     */
    private static class SingletonHelper {
        private static final Autorizaciones INSTANCE = new Autorizaciones();
    }

    // Método que devuelve la instancia a utilizar.
    public static Autorizaciones getInstance() {
        return SingletonHelper.INSTANCE;
    }
}
