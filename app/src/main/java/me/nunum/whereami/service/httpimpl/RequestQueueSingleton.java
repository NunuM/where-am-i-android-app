package me.nunum.whereami.service.httpimpl;

import android.content.Context;

import com.android.volley.Network;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.BasicNetwork;
import com.android.volley.toolbox.DiskBasedCache;
import com.android.volley.toolbox.HurlStack;

public class RequestQueueSingleton {

    private static RequestQueueSingleton instance;
    private final RequestQueue requestQueue;

    private RequestQueueSingleton(Context context) {

        final DiskBasedCache cache = new DiskBasedCache(context.getCacheDir(), 1024 * 2048);
        Network network = new BasicNetwork(new HurlStack());

        this.requestQueue = new RequestQueue(cache, network);
        this.requestQueue.start();
    }

    public static synchronized RequestQueueSingleton getInstance(Context context) {
        if (instance == null) {
            instance = new RequestQueueSingleton(context);
        }
        return instance;
    }

    public RequestQueue getRequestQueue() {
        return requestQueue;
    }
}
