package com.minsait.onesaitplatform.digitaltwins.fcm;

import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.minsait.onesaitplatform.digitaltwins.AppController;
import com.minsait.onesaitplatform.digitaltwins.comun.helpers.Autorizaciones;
import com.minsait.onesaitplatform.digitaltwins.comun.helpers.Constantes;
import com.minsait.onesaitplatform.digitaltwins.vo.RegisterFcmTokenRequestVO;
import com.minsait.onesaitplatform.digitaltwins.vo.RegisterFcmTokenResponseVO;

import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

import io.reactivex.Notification;
import io.reactivex.subjects.PublishSubject;

/**
 * NOTE: There can only be one service in each app that receives FCM messages. If multiple
 * are declared in the Manifest then the first one will be chosen.
 */
public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private static final String TAG = "MyFirebaseMsgService";

    private PublishSubject<Notification<Map<String, String>>> notificationPublisher;

    @Override
    public void onCreate() {
        notificationPublisher = NotificationsManager.getPublisher();
        super.onCreate();
    }

    public void dataReceived(Map<String, String> data) {
        notificationPublisher.onNext(Notification.createOnNext(data));
    }

    /**
     * Called when message is received.
     *
     * @param remoteMessage Object representing the message received from Firebase Cloud Messaging.
     */
    // [START receive_message]
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        // [START_EXCLUDE]
        // There are two types of messages data messages and notification messages. Data messages
        // are handled
        // here in onMessageReceived whether the app is in the foreground or background. Data
        // messages are the type
        // traditionally used with GCM. Notification messages are only received here in
        // onMessageReceived when the app
        // is in the foreground. When the app is in the background an automatically generated
        // notification is displayed.
        // When the user taps on the notification they are returned to the app. Messages
        // containing both notification
        // and data payloads are treated as notification messages. The Firebase console always
        // sends notification
        // messages. For more see: https://firebase.google.com/docs/cloud-messaging/concept-options
        // [END_EXCLUDE]

        // TODO(developer): Handle FCM messages here.
        // Not getting messages here? See why this may be: https://goo.gl/39bRNJ
        Log.d(TAG, "From: " + remoteMessage.getFrom());

        // Check if message contains a data payload.
        if (remoteMessage.getData().size() > 0) {
            Log.d(TAG, "Message data payload: " + remoteMessage.getData());
            dataReceived(remoteMessage.getData());

            if (/* Check if data needs to be processed by long running job */ true) {
                // For long-running tasks (10 seconds or more) use WorkManager.
                scheduleJob();
            } else {
                // Handle message within 10 seconds
                handleNow();
            }

        }

        // Check if message contains a notification payload.
        if (remoteMessage.getNotification() != null) {
            Log.d(TAG, "Message Notification Body: " + remoteMessage.getNotification().getBody());
        }

        // Also if you intend on generating your own notifications as a result of a received FCM
        // message, here is where that should be initiated. See sendNotification method below.
    }
    // [END receive_message]


    // [START on_new_token]

    /**
     * Called if InstanceID token is updated. This may occur if the security of
     * the previous token had been compromised. Note that this is called when the InstanceID token
     * is initially generated so this is where you would retrieve the token.
     */
    @Override
    public void onNewToken(String token) {
        Log.d(TAG, "FCM token: " + token);

        // If you want to send messages to this application instance or
        // manage this apps subscriptions on the server side, send the
        // Instance ID token to your app server.
        sendRegistrationToServer(token);
    }
    // [END on_new_token]

    /**
     * Schedule async work using WorkManager.
     */
    private void scheduleJob() {
        // [START dispatch_job]
        OneTimeWorkRequest work = new OneTimeWorkRequest.Builder(MyWorker.class)
                .build();
        WorkManager.getInstance().beginWith(work).enqueue();
        // [END dispatch_job]
    }

    /**
     * Handle time allotted to BroadcastReceivers.
     */
    private void handleNow() {
        Log.d(TAG, "Short lived task is done.");
    }

    /**
     * Persist token to third-party servers.
     * <p>
     * Modify this method to associate the user's FCM InstanceID token with any server-side account
     * maintained by your application.
     *
     * @param token The new token.
     */
    private void sendRegistrationToServer(String token) {
        // Inicializamos GSon
        Gson gson = new Gson();
        Type tipo = new TypeToken<RegisterFcmTokenRequestVO>() {
        }.getType();

        // Preparamos la solicitud.
        RegisterFcmTokenRequestVO solicitudJSON = new RegisterFcmTokenRequestVO();
        solicitudJSON.getST_NativeNotifKeys().setApp(Constantes.URL_ONESAIT_PLATFORM_API_REGISTER_FCM_APP_PARAMETER);
        solicitudJSON.getST_NativeNotifKeys().setUser(Constantes.URL_ONESAIT_PLATFORM_API_REGISTER_FCM_USER_PARAMETER);
        solicitudJSON.getST_NativeNotifKeys().setDeviceToken(token);

        try {
            JsonObjectRequest jsonRequest = new JsonObjectRequest(Request.Method.POST,
                    Constantes.URL_ONESAIT_PLATFORM_API_REGISTER_FCM_TOKEN,
                    new JSONObject(gson.toJson(solicitudJSON, tipo)),
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            // Procesamos la respuesta.
                            Type tipo = new TypeToken<RegisterFcmTokenResponseVO>() {
                            }.getType();
                            Gson gson = new Gson();
                            // Creamos el objeto con los datos.
                            RegisterFcmTokenResponseVO respuestaVO = gson.fromJson(response.toString(), tipo);

                            Log.d(this.getClass().getName(), "REGISTRO_ONESAIT_FCM_TOKEN: " + respuestaVO.getCount());
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Log.e(this.getClass().getName(), "REGISTRO_ONESAIT_FCM_TOKEN: " + error.getMessage());
                        }
                    }) {
                // Añadimos en la cabecera el token API.
                @Override
                public Map getHeaders() throws AuthFailureError {
                    HashMap headers = new HashMap();
                    headers.put("X-OP-APIKey",
                            Autorizaciones.getInstance().getMap().get(Autorizaciones.API_TOKEN));
                    return headers;
                }
            };
            // Configuramos los reintentos por problemas de red y el timeout.
            jsonRequest.setRetryPolicy(new DefaultRetryPolicy(
                    Constantes.CONNECTION_TIMEOUT,
                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

            // Añadimos la solicitud a la cola.
            AppController.getInstance().addToRequestQueue(jsonRequest);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
