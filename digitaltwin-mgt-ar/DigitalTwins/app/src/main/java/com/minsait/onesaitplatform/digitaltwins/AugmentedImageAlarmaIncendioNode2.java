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
import android.animation.ValueAnimator;
import android.content.Context;
import android.net.Uri;
import android.os.Handler;
import android.util.Log;

import com.google.ar.core.AugmentedImage;
import com.google.ar.sceneform.AnchorNode;
import com.google.ar.sceneform.Node;
import com.google.ar.sceneform.Scene;
import com.google.ar.sceneform.math.Vector3;
import com.google.ar.sceneform.rendering.Color;
import com.google.ar.sceneform.rendering.Light;
import com.google.ar.sceneform.rendering.ModelRenderable;
import com.google.ar.sceneform.ux.TransformableNode;
import com.google.ar.sceneform.ux.TransformationSystem;
import com.minsait.onesaitplatform.digitaltwins.fcm.NotificationsManager;

import java.util.Map;
import java.util.concurrent.CompletableFuture;

import io.reactivex.Notification;
import io.reactivex.Observable;
import io.reactivex.observers.DisposableObserver;

/**
 * Nodo para el renderizado de la imagen aumentada correspondiente al modelo del aire
 * acondicionado.
 */
@SuppressWarnings({"AndroidApiChecker"})
public class AugmentedImageAlarmaIncendioNode2 extends TransformableNode {

    // Tag de este tipo de objetos.
    private static final String TAG = "AugmentedImageAlarmaIncendioNode2";

    // La imagen aumentada representada por este nodo.
    private AugmentedImage image;
    // Nodo del objeto que se renderizará.
    private AnchorNode nodo;
    private Scene sceneParent;
    // Contiene el modelo a renderizar.
    private CompletableFuture<ModelRenderable> modeloRenderizable;

    // Observable para obtener los mensajes del servicio de FCM.
    private Observable<Notification<Map<String, String>>> notificationObservable;
    // Observer que recibirá las notificaciones desde el servicio de FCM.
    private DisposableObserver<Notification<Map<String, String>>> observer;

    // Luz asociada a este nodo.
    private Light luz;
    // Animador de la intensidad
    private ValueAnimator intensityAnimator;
    // Nodo que contiene la luz.
    private AnchorNode nodoLuz;

    public AugmentedImageAlarmaIncendioNode2(Context context, TransformationSystem transformationSystem, Scene parent) {
        super(transformationSystem);
        sceneParent = parent;
        // Cargamos el modelo renderizable a partir del fichero sfb.
        modeloRenderizable =
                ModelRenderable.builder()
                        .setSource(context, Uri.parse("alarma_incendio.sfb"))
                        .build();
        // Obtenemos el notificador de sucesos desde el servicio de FCM.
        notificationObservable = NotificationsManager.getNotificationObservable();
    }

    @Override
    public void onActivate() {
        // Continuamos con la propagación del evento.
        super.onActivate();
        // Creamos el objeto observer que recibirá las notificaciones.
        observer = new DisposableObserver<Notification<Map<String, String>>>() {
            @Override
            public void onNext(Notification<Map<String, String>> notification) {
                Log.d(TAG, String.format("Notificación recibida de MyFirebaseMsgService, datos: %s.", notification.getValue()));

                // Obtenemos el valor de alarmaIsOn para saber si debemos activarla o desactivarla.
                boolean activarAlarma = Boolean.valueOf((notification.getValue().get("alarmaIsOn")));

                if (activarAlarma) intensityAnimator.setFloatValues(1000.0f, 10000.0f);
                else intensityAnimator.setFloatValues(0.0f, 0.0f);
            }

            @Override
            public void onError(Throwable e) {
                Log.e(TAG, "Error recibiendo la notificación de MyFirebaseMsgService: " +
                        e.getMessage());
            }

            @Override
            public void onComplete() {
            }
        };
        // Nos suscribimos al notificador.
        notificationObservable.subscribe(observer);
    }

    public void configurarLuz() {
        // Creamos la luz
        luz = Light.builder(Light.Type.DIRECTIONAL)
                .setColor(new Color(android.graphics.Color.RED))
                .setIntensity(0.0f)
                .setShadowCastingEnabled(true)
                .build();
        // Creamos la animación de la luz.
        final int durationInMilliseconds = 500;
        intensityAnimator = ObjectAnimator.ofFloat(luz,
                "intensity",
                0, 0);
        intensityAnimator.setDuration(durationInMilliseconds);
        intensityAnimator.setRepeatCount(ValueAnimator.INFINITE);
        intensityAnimator.setRepeatMode(ValueAnimator.REVERSE);
        intensityAnimator.start();

        // Creamos el nodo.
        nodoLuz = new AnchorNode();
        // Cambiamos la dirección a la que mira la luz para direccionarla hacia la alarma.
        nodoLuz.setLookDirection(new Vector3(0.00f, 0.00f, 90.00f));
        // Asignamos la luz al nodo.
        nodoLuz.setLight(luz);
        addChild(nodoLuz);
        nodoLuz.setParent(AugmentedImageAlarmaIncendioNode2.this);
    }

    @Override
    public void onDeactivate() {
        // Eliminamos la suscripción.
        observer.dispose();
        // Continuamos con la propagación del evento.
        super.onDeactivate();
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

        // Creamos finalmente el nodo y añadimos el modelo a renderizar.
        nodo = new AnchorNode(image.createAnchor(image.getCenterPose()));
        nodo.setParent(sceneParent);

        setParent(nodo);
        setRenderable(modeloRenderizable.getNow(null));
        select();
        // Cambiamos la dirección a la que mira el modelo para que salga correctamente según el marcador.
        setLookDirection(new Vector3(0.00f, 0.00f, 90.00f));
        // Configuramos la luz del nodo.
        configurarLuz();
    }

    public AugmentedImage getImage() {
        return image;
    }
}
