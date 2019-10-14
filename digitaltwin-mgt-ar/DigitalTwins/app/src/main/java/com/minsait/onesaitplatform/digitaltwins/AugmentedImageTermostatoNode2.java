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

import android.graphics.Bitmap;
import android.net.Uri;

import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;
import com.google.ar.core.AugmentedImage;
import com.google.ar.sceneform.AnchorNode;
import com.google.ar.sceneform.Node;
import com.google.ar.sceneform.Scene;
import com.google.ar.sceneform.math.Vector3;
import com.google.ar.sceneform.rendering.ModelRenderable;
import com.google.ar.sceneform.rendering.ViewRenderable;
import com.google.ar.sceneform.ux.TransformableNode;
import com.google.ar.sceneform.ux.TransformationSystem;
import com.minsait.onesaitplatform.digitaltwins.comun.helpers.Autorizaciones;
import com.minsait.onesaitplatform.digitaltwins.comun.helpers.Constantes;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

/**
 * Nodo para el renderizado de la imagen aumentada correspondiente al modelo del aire
 * acondicionado.
 */
@SuppressWarnings({"AndroidApiChecker"})
public class AugmentedImageTermostatoNode2 extends TransformableNode {

    // Tag de este tipo de objetos.
    private static final String TAG = "AugmentedImageTermostatoNode2";

    // La imagen aumentada representada por este nodo.
    private AugmentedImage image;
    // Nodo del objeto que se renderizará.
    private AnchorNode nodo;
    private Scene sceneParent;
    private AppCompatActivity actividad;
    // Contiene el modelo a renderizar.
    private CompletableFuture<ModelRenderable> modeloRenderizable;
    private boolean botonPulsado = false;

    public AugmentedImageTermostatoNode2(AppCompatActivity actividad, TransformationSystem transformationSystem, Scene parent) {
        super(transformationSystem);
        sceneParent = parent;
        this.actividad = actividad;
        // Cargamos el modelo renderizable a partir del fichero sfb.
        modeloRenderizable =
                ModelRenderable.builder()
                        .setSource(actividad, Uri.parse("termostato.sfb"))
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

        // Creamos finalmente el nodo y añadimos el modelo a renderizar.
        nodo = new AnchorNode(image.createAnchor(image.getCenterPose()));
        nodo.setParent(sceneParent);

        this.setParent(nodo);
        this.setRenderable(modeloRenderizable.getNow(null));
        this.select();

        // Cambiamos la dirección a la que mira el modelo para que salga correctamente según el marcador.
        setLookDirection(new Vector3(90.00f, 0.00f, 45.00f));

        ViewRenderable.builder()
                .setView(sceneParent.getView().getContext(), R.layout.btn_termostato_mostrar_tabla)
                .build()
                .thenAccept(renderable -> {

                    ImageView ib = (ImageView) renderable.getView();

                    AnchorNode nodoBoton = new AnchorNode();
                    nodoBoton.setParent(this);
                    Vector3 posicion = new Vector3(getLocalPosition());
                    posicion.z -= 0.055f;
                    posicion.y += 0.055f;
                    nodoBoton.setLocalPosition(posicion);
                    nodoBoton.setLookDirection(new Vector3(-90.00f, 0.00f, 180.00f));
                    nodoBoton.setRenderable(renderable);

                    ib.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            // Si tenemos visible la tabla, la eliminamos.
                            if (botonPulsado) {
                                for (int i = 0; i < getChildren().size(); i++) {
                                    Node nodo = getChildren().get(i);
                                    if ("Nodo_Dashboard".equals(nodo.getName())) {
                                        nodo.getParent().removeChild(nodo);
                                        nodo.setParent(null);
                                        sceneParent.removeChild(nodo);

                                        botonPulsado = false;
                                    }
                                }
                            } else {
                                botonPulsado = true;
                                // Creamos la imagen para mostrarla.
                                ViewRenderable.builder()
                                        .setView(sceneParent.getView().getContext(), R.layout.img_termostato_dashboard)
                                        .build()
                                        .thenAccept(renderable -> {
                                            ImageView im = (ImageView) renderable.getView();
                                            Log.d(TAG, "ObtenerImagenDashBoardTemperatura: 1");
                                            ImageRequest request = new ImageRequest(String.format("%s%s",
                                                    Constantes.URL_TEMPERATURA_DASH_BOARD,
                                                    Constantes.PARAMS_TEMPERATURA_DASH_BOARD),
                                                    new Response.Listener<Bitmap>() {
                                                        @Override
                                                        public void onResponse(Bitmap bitmap) {
                                                            Log.d(TAG, "ObtenerImagenDashBoardTemperatura: " + bitmap.getDensity());
                                                            im.setImageBitmap(bitmap);
                                                            Log.d(TAG, "ObtenerImagenDashBoardTemperatura:2.");
                                                        }
                                                    }, 0, 0,
                                                    ImageView.ScaleType.CENTER_INSIDE,
                                                    null,
                                                    new Response.ErrorListener() {
                                                        public void onErrorResponse(VolleyError error) {
                                                            im.setImageResource(R.drawable.img_error_carga_dashboard);
                                                            Log.e(TAG, "ObtenerImagenDashBoardTemperatura: " + error.getMessage());
                                                        }
                                                    }) {
                                                @Override
                                                public Map<String, String> getHeaders() throws AuthFailureError {
                                                    // Añadimos la autorización con el token OAuth
                                                    Map<String, String> res = new HashMap<String, String>();
                                                    res.put(Constantes.HEADER_NAME_AUTHORIZATION,
                                                            String.format("%s %s",
                                                                    Autorizaciones.getInstance().getMap().get(Autorizaciones.OAUTH_TOKEN_TYPE),
                                                                    Autorizaciones.getInstance().getMap().get(Autorizaciones.OAUTH_TOKEN)));

                                                    return res;
                                                }
                                            };

                                            // Configuramos los reintentos por problemas de red y el timeout.
                                            request.setRetryPolicy(new DefaultRetryPolicy(
                                                    Constantes.CONNECTION_TIMEOUT,
                                                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                                                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

                                            // Añadimos la solicitud a la cola.
                                            AppController.getInstance().addToRequestQueue(request);

                                            /*
                                            // Configuramos Glide para enviar el token de OAuth y poder descargar la imagen
                                            // del dashboard.
                                            GlideUrl glideUrl = new GlideUrl(
                                                    String.format("%s%s",
                                                            Constantes.URL_TEMPERATURA_DASH_BOARD,
                                                            Constantes.PARAMS_TEMPERATURA_DASH_BOARD),
                                                    new LazyHeaders.Builder()
                                                    .addHeader(Constantes.HEADER_NAME_AUTHORIZATION,
                                                            String.format("%s %s",
                                                            Autorizaciones.getInstance().getMap().get(Autorizaciones.OAUTH_TOKEN_TYPE),
                                                            Autorizaciones.getInstance().getMap().get(Autorizaciones.OAUTH_TOKEN)))
                                                    .build());
                                            // Realizamos la carga de la imagen con Glide.
                                            Glide.with(actividad)
                                                    .load(glideUrl)
                                                    .into(im);*/

                                            // Configuramos el nodo.
                                            AnchorNode nodoDashboard = new AnchorNode();
                                            nodoDashboard.setName("Nodo_Dashboard");
                                            Vector3 posicion = new Vector3(getLocalPosition());
                                            posicion.y -= 0.800;
                                            nodoDashboard.setLocalPosition(posicion);
                                            nodoDashboard.setLookDirection(new Vector3(45.00f, 0.00f, 0.00f));

                                            nodoDashboard.setParent(AugmentedImageTermostatoNode2.this);
                                            nodoDashboard.setRenderable(renderable);
                                        });
                                /*
                                ViewRenderable.builder()
                                        .setView(sceneParent.getView().getContext(), R.layout.fragment_temperatura_webview)
                                        .build()
                                        .thenAccept(renderable -> {
                                            WebView wv = (WebView) renderable.getView();
                                            configurarWebView(wv);
                                            wv.loadUrl("https://lab.onesaitplatform.com/controlpanel/dashboards/view/1e8d7094-0a5b-45a5-a21c-dea20f79c541");

                                            AnchorNode nodoRV = new AnchorNode();
                                            Vector3 posicion = new Vector3(getLocalPosition());
                                            posicion.y -= 0.136f * 8;
                                            nodoRV.setLocalPosition(posicion);
                                            nodoRV.setLookDirection(new Vector3(45.00f, 0.00f, 0.00f));

                                            nodoRV.setParent(AugmentedImageTermostatoNode2.this);
                                            nodoRV.setRenderable(renderable);
                                        });

                                ViewRenderable.builder()
                                        .setView(sceneParent.getView().getContext(), R.layout.fragment_temperatura_list)
                                        .build()
                                        .thenAccept(renderable -> {
                                            List<Temperatura> lista = new ArrayList<Temperatura>();
                                            lista.add(new Temperatura("28/09/2019", 26.40f));
                                            lista.add(new Temperatura("29/09/2019", 25.40f));
                                            lista.add(new Temperatura("30/09/2019", 25.30f));
                                            lista.add(new Temperatura("01/10/2019", 24.40f));
                                            lista.add(new Temperatura("02/10/2019", 25.40f));

                                            RecyclerView rv = (RecyclerView) renderable.getView();
                                            rv.setAdapter(new TemperaturaRecyclerViewAdapter(sceneParent.getView().getContext(), lista));

                                            AnchorNode nodoRV = new AnchorNode();
                                            Vector3 posicion = new Vector3(getLocalPosition());
                                            posicion.y -= 0.136f * lista.size();
                                            nodoRV.setLocalPosition(posicion);
                                            nodoRV.setLookDirection(new Vector3(45.00f, 0.00f, 0.00f));

                                            nodoRV.setParent(AugmentedImageTermostatoNode2.this);
                                            nodoRV.setRenderable(renderable);
                                        });*/
                            }
                        }
                    });
                });
    }

    /**
     * Método que se encarga de configurar el WebView para que muestre el DashBoard
     */
    /*
    private void configurarWebView(WebView wv) {
        final WebSettings settings = wv.getSettings();

        settings.setLoadsImagesAutomatically(true);
        settings.setJavaScriptEnabled(true);
        settings.setDomStorageEnabled(true);
        settings.setAppCacheEnabled(true);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            settings.setSafeBrowsingEnabled(false);
            settings.setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
        }
        wv.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            CookieManager.getInstance().setAcceptThirdPartyCookies(wv, true);
        }
        // Extras tried for Android 9.0, can be removed if want.
        settings.setAllowContentAccess(true);
        settings.setAllowFileAccess(true);
        settings.setBlockNetworkImage(false);
    }
    */

    public AugmentedImage getImage() {
        return image;
    }
}