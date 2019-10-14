package com.minsait.onesaitplatform.digitaltwins;

import android.annotation.SuppressLint;
import android.net.http.SslError;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.CookieManager;
import android.webkit.SslErrorHandler;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.appcompat.app.AppCompatActivity;

import java.io.BufferedInputStream;
import java.io.IOException;

public class Main2Activity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        WebView wv = findViewById(R.id.webview);
        wv.setWebViewClient(new WebViewClient() {

            /*
            @Override
            public WebResourceResponse shouldInterceptRequest(WebView view, String url) {

                if(url.toLowerCase().contains("/favicon.ico")) {
                    try {
                        return new WebResourceResponse("image/png", null, new BufferedInputStream(view.getContext().getResources().getAssets().open("dummyfavicon.ico")));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                return null;
            }

            @Override
            @SuppressLint("NewApi")
            public WebResourceResponse shouldInterceptRequest(WebView view, WebResourceRequest request) {

                if(!request.isForMainFrame() && request.getUrl().getPath().endsWith("/favicon.ico")) {
                    try {
                        return new WebResourceResponse("image/png", null, new BufferedInputStream(view.getContext().getResources().getAssets().open("dummyfavicon.ico")));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                return null;
            }*/

            @Override
            public void onLoadResource(WebView view, String url) {
                Log.e("onLoadResource:", url);
            }

            @Override
            public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
                Log.e("Web", error.getDescription().toString());
                super.onReceivedError(view, request, error);
            }

            public void onReceivedHttpError(
                    WebView view, WebResourceRequest request, WebResourceResponse errorResponse) {
                Log.e("Web2", "" + errorResponse.getStatusCode() + request.getUrl());
            }

            @Override
            public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
                handler.proceed();
            }
        });

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


        //wv.loadUrl("https://www.google.es");
        //wv.loadUrl("https://lab.onesaitplatform.com/controlpanel/dashboards/view/ab3d2faa-2adc-40e0-8b2f-feca099ee9f2");
        wv.loadUrl("https://lab.onesaitplatform.com/controlpanel/dashboards/view/8f6fd6d0-d18e-43fe-b5ce-f63aa19f45e2");
    }
}
