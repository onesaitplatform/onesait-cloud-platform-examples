package com.minsait.onesaitplatform.digitaltwins.comun.helpers.volley;

import androidx.annotation.GuardedBy;
import androidx.annotation.Nullable;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Response;
import com.android.volley.Response.Listener;
import com.android.volley.Response.ErrorListener;

import com.android.volley.Request;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.HttpHeaderParser;
import com.minsait.onesaitplatform.digitaltwins.comun.helpers.Constantes;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

public class OAuthRequest extends Request<String> {
    /**
     * Default charset for JSON request.
     */
    protected static final String PROTOCOL_CHARSET = "utf-8";

    /**
     * Content type for request.
     */
    private static final String PROTOCOL_CONTENT_TYPE =
            String.format(Constantes.HEADER_APPLICATION_X_WWW_FORM_URLENCODED + "; charset=%s", PROTOCOL_CHARSET);

    /**
     * Lock to guard mListener as it is cleared on cancel() and read on delivery.
     */
    private final Object mLock = new Object();

    @Nullable
    @GuardedBy("mLock")
    private Listener<String> mListener;

    @Nullable
    private final String mRequestBody;

    public OAuthRequest(Listener<String> listener,
                        @Nullable ErrorListener errorListener) {
        super(Request.Method.POST, Constantes.URL_OAUTH, errorListener);
        mListener = listener;
        mRequestBody = Constantes.BODY_OAUTH_WWW_FORM_PARAMETERS;
    }

    @Override
    public void cancel() {
        super.cancel();
        synchronized (mLock) {
            mListener = null;
        }
    }

    @Override
    protected void deliverResponse(String response) {
        Listener<String> listener;
        synchronized (mLock) {
            listener = mListener;
        }
        if (listener != null) {
            listener.onResponse(response);
        }
    }

    @Override
    protected Response<String> parseNetworkResponse(NetworkResponse response) {
        try {
            String jsonString =
                    new String(
                            response.data,
                            HttpHeaderParser.parseCharset(response.headers, PROTOCOL_CHARSET));
            return Response.success(
                    jsonString, HttpHeaderParser.parseCacheHeaders(response));
        } catch (UnsupportedEncodingException e) {
            return Response.error(new ParseError(e));
        }
    }

    @Override
    public String getBodyContentType() {
        return PROTOCOL_CONTENT_TYPE;
    }

    /**
     * Returns a list of extra HTTP headers to go along with this request. Can throw {@link
     * AuthFailureError} as authentication may be required to provide these values.
     *
     * @throws AuthFailureError In the event of auth failure
     */
    @Override
    public Map<String, String> getHeaders() throws AuthFailureError {
        // Añadimos la autorización para obtener el token OAuth.
        Map<String, String> res = new HashMap<String, String>();
        res.put("Authorization", Constantes.HEADER_AUTHORIZATION);

        return res;
    }

    @Override
    public byte[] getBody() {
        try {
            return mRequestBody == null ? null : mRequestBody.getBytes(PROTOCOL_CHARSET);
        } catch (UnsupportedEncodingException uee) {
            VolleyLog.wtf(
                    "Unsupported Encoding while trying to get the bytes of %s using %s",
                    mRequestBody, PROTOCOL_CHARSET);
            return null;
        }
    }
}