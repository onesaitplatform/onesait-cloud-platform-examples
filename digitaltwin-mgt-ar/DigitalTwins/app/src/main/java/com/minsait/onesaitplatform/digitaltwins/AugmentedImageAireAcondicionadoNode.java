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

import android.content.Context;
import android.net.Uri;
import android.util.Log;

import com.google.ar.core.AugmentedImage;
import com.google.ar.sceneform.AnchorNode;
import com.google.ar.sceneform.Node;
import com.google.ar.sceneform.math.Vector3;
import com.google.ar.sceneform.rendering.ModelRenderable;

import java.util.concurrent.CompletableFuture;

/**
 * Nodo para el renderizado de la imagen aumentada correspondiente al modelo del aire
 * acondicionado.
 */
@SuppressWarnings({"AndroidApiChecker"})
public class AugmentedImageAireAcondicionadoNode extends AnchorNode {

    // Tag de este tipo de objetos.
    private static final String TAG = "AugmentedImageAireAcondicionadoNode";

    // La imagen aumentada representada por este nodo.
    private AugmentedImage image;
    // Nodo del objeto que se renderizará.
    private Node nodo;
    // Contiene el modelo a renderizar.
    private CompletableFuture<ModelRenderable> modeloRenderizable;

    public AugmentedImageAireAcondicionadoNode(Context context) {
        // Cargamos el modelo renderizable a partir del fichero sfb.
        modeloRenderizable =
                ModelRenderable.builder()
                        .setSource(context, Uri.parse("aire_acondicionado.sfb"))
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

        // Asignamos el marcador (anchor) basado en centro de la imagen. Esto es para que ARCore sea
        // capaz de realizar el seguimiento al movernos por la escena.
        setAnchor(image.createAnchor(image.getCenterPose()));

        // Creamos finalmente el nodo y añadimos el modelo a renderizar.
        nodo = new Node();
        nodo.setParent(this);
        nodo.setRenderable(modeloRenderizable.getNow(null));
    }

    public AugmentedImage getImage() {
        return image;
    }
}
