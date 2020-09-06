package com.justm.ssip;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

public class MapUrlSingleton {

    private static MapUrlSingleton instance;
    private Context ctx ;
    private RequestQueue requestQueue ;


    MapUrlSingleton(Context context)
    {
        ctx = context ;
        requestQueue = getRequest();
    }
    public RequestQueue getRequest() {
        if (requestQueue == null) {
            // getApplicationContext() is key, it keeps you from leaking the
            // Activity or BroadcastReceiver if someone passes one in.
            requestQueue = Volley.newRequestQueue(ctx.getApplicationContext());
        }
        return requestQueue;
    }

    public static synchronized MapUrlSingleton getInstance(Context context) {
        if (instance == null) {
            instance = new MapUrlSingleton(context);
        }
        return instance;
    }

    public <T> void addToRequestQueue(Request<T> req) {
        getRequest().add(req);
    }
}
