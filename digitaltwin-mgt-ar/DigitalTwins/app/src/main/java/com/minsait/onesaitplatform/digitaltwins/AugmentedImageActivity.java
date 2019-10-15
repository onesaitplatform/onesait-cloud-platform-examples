/*
 * Copyright 2018 Google LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.minsait.onesaitplatform.digitaltwins;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.ar.core.AugmentedImage;
import com.google.ar.core.Frame;
import com.google.ar.sceneform.FrameTime;
import com.google.ar.sceneform.Node;
import com.google.ar.sceneform.ux.ArFragment;
import com.google.ar.sceneform.ux.TransformableNode;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.minsait.onesaitplatform.digitaltwins.comun.helpers.Autorizaciones;
import com.minsait.onesaitplatform.digitaltwins.comun.helpers.Constantes;
import com.minsait.onesaitplatform.digitaltwins.comun.helpers.SnackbarHelper;
import com.minsait.onesaitplatform.digitaltwins.comun.helpers.volley.OAuthRequest;
import com.minsait.onesaitplatform.digitaltwins.vo.ApiResponseVO;
import com.minsait.onesaitplatform.digitaltwins.vo.OAuthResponseVO;

import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * This application demonstrates using augmented images to place anchor nodes. app to include image
 * tracking functionality.
 *
 * <p>In this example, we assume all images are static or moving slowly with a large occupation of
 * the screen. If the target is actively moving, we recommend to check
 * ArAugmentedImage_getTrackingMethod() and render only when the tracking method equals to
 * AR_AUGMENTED_IMAGE_TRACKING_METHOD_FULL_TRACKING. See details in <a
 * href="https://developers.google.com/ar/develop/c/augmented-images/">Recognize and Augment
 * Images</a>.
 */
public class AugmentedImageActivity extends AppCompatActivity {

    private ArFragment arFragment;
    //private ImageView fitToScanView;

    // Augmented image and its associated center pose anchor, keyed by the augmented image in
    // the database.
    private final Map<AugmentedImage, Node> augmentedImageMap = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        arFragment = (ArFragment) getSupportFragmentManager().findFragmentById(R.id.ux_fragment);
        //  fitToScanView = findViewById(R.id.image_view_fit_to_scan);

        arFragment.getArSceneView().getScene().addOnUpdateListener(this::onUpdateFrame);

        // Obtenemos el token de OAuth y si va todo bien, también el token de API.
        obtenerOAuthToken();
    }

    // Se encarga de realizar la obtención del Token de OAuth.
    private void obtenerOAuthToken() {
        OAuthRequest oAuthRequest = new OAuthRequest(
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // Procesamos la respuesta.
                        Type tipo = new TypeToken<OAuthResponseVO>() {
                        }.getType();
                        Gson gson = new Gson();
                        // Creamos el objeto con los datos.
                        OAuthResponseVO oAuthResponseVO = gson.fromJson(response, tipo);
                        // Añadimos el token de OAuth a las autorizaciones.
                        Autorizaciones.getInstance().getMap().put(Autorizaciones.OAUTH_TOKEN, oAuthResponseVO.getAccess_token());
                        // Añadimos el tipo de token de OAuth a las autorizaciones.
                        Autorizaciones.getInstance().getMap().put(Autorizaciones.OAUTH_TOKEN_TYPE, oAuthResponseVO.getToken_type());

                        Log.d(this.getClass().getName(), "OAUTH_TOKEN: " + oAuthResponseVO.getAccess_token());

                        // Avisamos al usuario de que tenemos el token.
                        SnackbarHelper.getInstance().showMessage(AugmentedImageActivity.this, "Token OAuth obtenido correctamente.");

                        // Ahora que ya tenemos el token OAuth vamos a obtener el token de las APIs.
                        obtenerApiToken();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e(this.getClass().getName(), "obtenerOAuthToken: " + error.getMessage());
                    }
                });
        // Configuramos los reintentos por problemas de red y el timeout.
        oAuthRequest.setRetryPolicy(new DefaultRetryPolicy(
                Constantes.CONNECTION_TIMEOUT,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        // Añadimos la solicitud a la cola.
        AppController.getInstance().addToRequestQueue(oAuthRequest);
    }

    // Se encarga de realizar la obtención del Token de las APIs.
    private void obtenerApiToken() {
        JsonObjectRequest jsonRequest = new JsonObjectRequest(Request.Method.GET,
                Constantes.URL_USER_API_TOKEN,
                null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        // Procesamos la respuesta.
                        Type tipo = new TypeToken<ApiResponseVO>() {
                        }.getType();
                        Gson gson = new Gson();
                        // Creamos el objeto con los datos.
                        ApiResponseVO apiResponseVO = gson.fromJson(response.toString(), tipo);
                        // Añadimos el token de Api a las autorizaciones.
                        Autorizaciones.getInstance().getMap().put(Autorizaciones.API_TOKEN, apiResponseVO.getUserTokens().get(0));

                        Log.d(this.getClass().getName(), "API_TOKEN: " + apiResponseVO.getUserTokens().get(0));

                        // Avisamos al usuario de que tenemos el token.
                        SnackbarHelper.getInstance().showMessage(AugmentedImageActivity.this, "Token Api obtenido correctamente.");
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e(this.getClass().getName(), "obtenerApiToken: " + error.getMessage());
                    }
                }) {
            // Añadimos en la cabecera el token OAuth para que nos devuelva el token del API.
            @Override
            public Map getHeaders() throws AuthFailureError {
                HashMap headers = new HashMap();
                headers.put(Constantes.HEADER_NAME_AUTHORIZATION,
                        String.format("%s %s",
                                Autorizaciones.getInstance().getMap().get(Autorizaciones.OAUTH_TOKEN_TYPE),
                                Autorizaciones.getInstance().getMap().get(Autorizaciones.OAUTH_TOKEN)));
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
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (augmentedImageMap.isEmpty()) {
            //fitToScanView.setVisibility(View.VISIBLE);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        // Si se cierra la actividad, nos aseguramos de cortar
        if (AppController.getInstance().getRequestQueue() != null) {
            AppController.getInstance().getRequestQueue().cancelAll(Constantes.TAG_OAUTH);
            AppController.getInstance().getRequestQueue().cancelAll(Constantes.TAG_USER_API);
        }
    }

    /**
     * Registered with the Sceneform Scene object, this method is called at the start of each frame.
     *
     * @param frameTime - time since last frame.
     */
    private void onUpdateFrame(FrameTime frameTime) {
        Frame frame = arFragment.getArSceneView().getArFrame();

        // If there is no frame, just return.
        if (frame == null) {
            return;
        }

        Collection<AugmentedImage> updatedAugmentedImages =
                frame.getUpdatedTrackables(AugmentedImage.class);
        for (AugmentedImage augmentedImage : updatedAugmentedImages) {
            switch (augmentedImage.getTrackingState()) {
                case PAUSED:
                    // When an image is in PAUSED state, but the camera is not PAUSED, it has been detected,
                    // but not yet tracked.
          /*String text = "Detected Image " + augmentedImage.getIndex();
          SnackbarHelper.getInstance().showMessage(this, text);*/
                    break;

                case TRACKING:
                    // Have to switch to UI Thread to update View.
                    //fitToScanView.setVisibility(View.GONE);

                    // Create a new anchor for newly found images.
                    if (!augmentedImageMap.containsKey(augmentedImage)) {
                        Log.d("Imagen detectada: ", augmentedImage.getName());

                        TransformableNode nodo = null;

                        switch (augmentedImage.getName()) {
                            case "Alarma 01":
                                nodo = new AugmentedImageAlarmaIncendioNode2(this,
                                        arFragment.getTransformationSystem(),
                                        arFragment.getArSceneView().getScene());
                                ((AugmentedImageAlarmaIncendioNode2) nodo).setImage(augmentedImage);
                                break;
                            case "Humo 01":
                                nodo = new AugmentedImageDetectorHumoNode2(this,
                                        arFragment.getTransformationSystem(),
                                        arFragment.getArSceneView().getScene());
                                ((AugmentedImageDetectorHumoNode2) nodo).setImage(augmentedImage);
                                break;
                            case "Salida de Aire 01":
                            case "Salida de Aire 02":
                                nodo = new AugmentedImageAireAcondicionadoNode2(this,
                                        arFragment.getTransformationSystem(),
                                        arFragment.getArSceneView().getScene());
                                ((AugmentedImageAireAcondicionadoNode2) nodo).setImage(augmentedImage);
                                break;
                            case "Termostato 01":
                                nodo = new AugmentedImageTermostatoNode2(this,
                                        arFragment.getTransformationSystem(),
                                        arFragment.getArSceneView().getScene());
                                ((AugmentedImageTermostatoNode2) nodo).setImage(augmentedImage);
                                break;
                        }

/*            node.setLight(Light.builder(Light.Type.DIRECTIONAL).build());

            final int durationInMilliseconds = 1500;
            final float minimumIntensity = 500.0f;
            final float maximumIntensity = 5000.0f;
            ValueAnimator intensityAnimator =
                    ObjectAnimator.ofFloat(
                            node.getLight(), "intensity", minimumIntensity, maximumIntensity);
            intensityAnimator.setDuration(durationInMilliseconds);
            intensityAnimator.setRepeatCount(ValueAnimator.INFINITE);
            intensityAnimator.setRepeatMode(ValueAnimator.REVERSE);
            intensityAnimator.start();*/

                        augmentedImageMap.put(augmentedImage, nodo);
                        //arFragment.getArSceneView().getScene().addChild(nodo);
                    }
                    break;

                case STOPPED:
                    augmentedImageMap.remove(augmentedImage);
                    break;
            }
        }
    }
}
