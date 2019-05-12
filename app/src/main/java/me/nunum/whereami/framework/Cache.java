package me.nunum.whereami.framework;

import android.content.Context;

import com.google.gson.Gson;

import java.util.WeakHashMap;

import me.nunum.whereami.service.HttpService;
import me.nunum.whereami.service.Services;

public class Cache {


    private WeakHashMap<Integer, Object> cache;
    private final Context context;


    public Cache(Context context) {
        this.cache = new WeakHashMap<>();
        this.context = context;
    }


    public Object get(Services service) {

        if (this.cache.containsKey(service.ordinal())) {
            return this.cache.get(service.ordinal());
        }

        Object serviceInstance = this.createService(service);

        this.cache.put(service.ordinal(), serviceInstance);

        return serviceInstance;
    }


    private Object createService(Services service) {

        switch (service) {
            case HTTP:
                return HttpService.create(this.context, new Gson());
            default:
                throw new UnsupportedOperationException("Invalid service");
        }

    }

}
