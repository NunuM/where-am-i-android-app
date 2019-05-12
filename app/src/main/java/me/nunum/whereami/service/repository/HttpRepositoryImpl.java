package me.nunum.whereami.service.repository;

import android.content.Context;

import com.google.gson.Gson;

import java.lang.reflect.Type;
import java.net.URL;
import java.util.Map;

import me.nunum.whereami.framework.AsyncHttp;
import me.nunum.whereami.framework.OnResponse;
import me.nunum.whereami.framework.persistence.repositories.Repository;
import me.nunum.whereami.service.httpimpl.AsyncHttpImpl;

public abstract class HttpRepositoryImpl<T, PK>
        implements Repository<T, PK, OnResponse<PK>> {

    private final Type tClass;
    private final AsyncHttp<T, PK> http;

    public HttpRepositoryImpl(Type tClass, Context context, Gson marshaller) {
        this.tClass = tClass;
        this.http = new AsyncHttpImpl<>(context, marshaller,tClass);
    }

    public enum Action {
        CREATE,
        DELETE,
        UPDATE,
        FIND,
        PAGINATE
    }

    public abstract URL uRLFor(Action action);

    public abstract Map<String, String> headers();

    @Override
    public void save(T entity, OnResponse<PK> callback) {
        this.http.put(uRLFor(Action.UPDATE), headers(), entity, callback);
    }

    @Override
    public void add(T entity, OnResponse<PK> callback) {
        this.http.post(uRLFor(Action.CREATE), headers(), entity, callback);
    }


    @Override
    public void findById(Long id, OnResponse<PK> callback) {
        this.http.get(this.uRLFor(Action.FIND), headers(), callback);
    }


    @Override
    public void delete(OnResponse<PK> callback) {
        this.http.delete(uRLFor(Action.DELETE), headers(), callback);
    }

}
