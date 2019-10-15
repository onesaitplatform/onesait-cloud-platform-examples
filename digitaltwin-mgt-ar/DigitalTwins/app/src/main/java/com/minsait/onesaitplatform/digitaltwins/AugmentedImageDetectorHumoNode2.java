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

import android.animation.ObjectAnimator;
import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.view.animation.LinearInterpolator;

import androidx.annotation.Nullable;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.ar.core.AugmentedImage;
import com.google.ar.sceneform.AnchorNode;
import com.google.ar.sceneform.FrameTime;
import com.google.ar.sceneform.Scene;
import com.google.ar.sceneform.math.Quaternion;
import com.google.ar.sceneform.math.QuaternionEvaluator;
import com.google.ar.sceneform.math.Vector3;
import com.google.ar.sceneform.rendering.ModelRenderable;
import com.google.ar.sceneform.ux.TransformableNode;
import com.google.ar.sceneform.ux.TransformationSystem;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.minsait.onesaitplatform.digitaltwins.comun.helpers.Constantes;
import com.minsait.onesaitplatform.digitaltwins.vo.ToggleHumoDetectadoRequestVO;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

/**
 * Nodo para el renderizado de la imagen aumentada correspondiente al modelo del aire
 * acondicionado.
 */
@SuppressWarnings({"AndroidApiChecker"})
public class AugmentedImageDetectorHumoNode2 extends TransformableNode {

    // Tag de este tipo de objetos.
    private static final String TAG = "AugmentedImageDetectorHumoNode2";

    // Animación para la rotación.
    @Nullable
    private ObjectAnimator animation = null;
    // Grados por segundo de la rotación.
    private float degreesPerSecond = 20.0f;

    // La imagen aumentada representada por este nodo.
    private AugmentedImage image;
    // Nodo del objeto que se renderizará.
    private AnchorNode nodo;
    private Scene sceneParent;
    // Contiene el modelo a renderizar.
    private CompletableFuture<ModelRenderable> modeloRenderizable;

    public AugmentedImageDetectorHumoNode2(Context context, TransformationSystem transformationSystem, Scene parent) {
        super(transformationSystem);
        sceneParent = parent;
        // Cargamos el modelo renderizable a partir del fichero sfb.
        modeloRenderizable =
                ModelRenderable.builder()
                        .setSource(context, Uri.parse("detector_humo.sfb"))
                        .build();
    }

    /**
     * Se llama a este método cuando se detecta una imagen aumentada y debemos renderizarla. En ese
     * momento se crea un nodo para añadirlo al árbol de Sceneform a partir de la imagen detectada.
     */
    @SuppressWarnings({"AndroidApiChecker", "FutureReturnValueIgnored"})
    public void setImage(AugmentedImage image) {
        this.image = image;

        // Initialize mazeNode and set its parents and the Renderable.
        // If any of the models are not loaded, process this function
        // until they all are loaded.
        if (!modeloRenderizable.isDone()) {
            CompletableFuture.allOf(modeloRenderizable)
                    .thenAccept((Void aVoid) -> setImage(image))
                    .exceptionally(
                            throwable -> {
                                Log.e(TAG, "Exception loading", throwable);
                                return null;
                            });
            return;
        }

/*
    setLocalPosition(new Vector3(image.getCenterPose().tx(),
            image.getCenterPose().ty(),
            image.getCenterPose().tz()));*/

        // Creamos finalmente el nodo y añadimos el modelo a renderizar.
        nodo = new AnchorNode(image.createAnchor(image.getCenterPose()));
        nodo.setParent(sceneParent);
        // Asignamos el listener para saber si se ha pulsado.
        setOnTapListener((hitTestResult, motionEvent) -> {
            // Inicializamos GSon
            Gson gson = new Gson();
            Type tipo = new TypeToken<ToggleHumoDetectadoRequestVO>() {
            }.getType();

            // Preparamos la solicitud.
            ToggleHumoDetectadoRequestVO solicitudJSON = new ToggleHumoDetectadoRequestVO();

            try {
                JsonObjectRequest jsonRequest = new JsonObjectRequest(Request.Method.POST,
                        Constantes.BASE_URL_DETECTOR_HUMO + Constantes.RECURSO_DETECTOR_HUMO,
                        new JSONObject(gson.toJson(solicitudJSON, tipo)),
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                Log.d(this.getClass().getName(), "ToggleHumoDetectadoRequest enviado.");
                            }
                        },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                Log.e(this.getClass().getName(), "ToggleHumoDetectadoRequest: " + error.getMessage());
                            }
                        }) {
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
        });

        this.setParent(nodo);
        this.setRenderable(modeloRenderizable.getNow(null));
        this.select();

        // Cambiamos la dirección a la que mira el modelo para que salga correctamente según el marcador.
        setLookDirection(new Vector3(0.00f, 0.00f, 180.00f));
    }

    @Override
    public void onUpdate(FrameTime frameTime) {
        super.onUpdate(frameTime);

        // Animation hasn't been set up.
        if (animation == null) {
            return;
        }

        animation.resume();

        float animatedFraction = animation.getAnimatedFraction();
        animation.setDuration(getAnimationDuration());
        animation.setCurrentFraction(animatedFraction);
    }

    @Override
    public void onActivate() {
        //startAnimation();
    }

    @Override
    public void onDeactivate() {
        //stopAnimation();
    }

    private long getAnimationDuration() {
        return (long) (1000 * 360 / (degreesPerSecond * 100.00f));
    }

    private void startAnimation() {
        if (animation != null) {
            return;
        }

        animation = createAnimator(true, 0.00f);
        animation.setTarget(this);
        animation.setDuration(getAnimationDuration());
        animation.start();
    }

    private void stopAnimation() {
        if (animation == null) {
            return;
        }
        animation.cancel();
        animation = null;
    }

    /**
     * Returns an ObjectAnimator that makes this node rotate.
     */
    private static ObjectAnimator createAnimator(boolean clockwise, float axisTiltDeg) {
        // Node's setLocalRotation method accepts Quaternions as parameters.
        // First, set up orientations that will animate a circle.
        Quaternion[] orientations = new Quaternion[4];
        // Rotation to apply first, to tilt its axis.
        Quaternion baseOrientation = Quaternion.axisAngle(new Vector3(1.0f, 0f, 0.0f), axisTiltDeg);
        for (int i = 0; i < orientations.length; i++) {
            float angle = i * 360 / (orientations.length - 1);
            if (clockwise) {
                angle = 360 - angle;
            }
            Quaternion orientation = Quaternion.axisAngle(new Vector3(0.0f, 1.0f, 0.0f), angle);
            orientations[i] = Quaternion.multiply(baseOrientation, orientation);
        }

        ObjectAnimator animacion = new ObjectAnimator();
        // Cast to Object[] to make sure the varargs overload is called.
        animacion.setObjectValues((Object[]) orientations);

        // Next, give it the localRotation property.
        animacion.setPropertyName("localRotation");

        // Use Sceneform's QuaternionEvaluator.
        animacion.setEvaluator(new QuaternionEvaluator());

        //  Allow animacion to repeat forever
        animacion.setRepeatCount(ObjectAnimator.INFINITE);
        animacion.setRepeatMode(ObjectAnimator.RESTART);
        animacion.setInterpolator(new LinearInterpolator());
        animacion.setAutoCancel(true);

        return animacion;
    }

    public AugmentedImage getImage() {
        return image;
    }
}
