package me.nunum.whereami.framework;

import java.lang.reflect.Type;
import java.net.URL;
import java.util.Map;


public interface AsyncHttp<T, O> {

    void get(URL url,
             Map<String, String> headers,
             OnResponse<O> onResponse);


    void get(URL url,
             Type type,
             Map<String, String> headers,
             OnResponse<O> onResponse);


    void put(URL url,
             Map<String, String> headers,
             T t,
             OnResponse<O> onResponse);


    void post(URL url,
              Map<String, String> headers,
              T t,
              OnResponse<O> onResponse);


    void delete(URL url,
                Map<String, String> headers,
                OnResponse<O> onResponse);


    void circuitTester(URL url, Map<String, String> headers, OnCircuitTest circuitTestCallback);
}
