package me.nunum.whereami.framework;

import android.content.Context;

import com.google.gson.Gson;

import java.util.WeakHashMap;

import me.nunum.whereami.service.HttpService;
import me.nunum.whereami.service.Services;

public class Cache {


    private final Context context;
    private final WeakHashMap<Integer, Object> cache;


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

        if (service == Services.HTTP) {
            return HttpService.create(this.context, new Gson());
        }

        throw new UnsupportedOperationException("Invalid service");
    }

}
