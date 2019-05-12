package me.nunum.whereami.framework;

import java.lang.reflect.Type;
import java.net.URL;
import java.util.Map;


public interface AsyncHttp<T, O> {

    /**
     * Makes an asynchronous GET HTTP request
     *
     * @param headers
     */
    void get(URL url,
             Map<String, String> headers,
             OnResponse<O> onResponse);


    void get(URL url,
             Type type,
             Map<String, String> headers,
             OnResponse<O> onResponse);


    /**
     * Makes an asynchronous PUT HTTP request
     *
     * @param headers
     * @param t       Entity
     */
    void put(URL url,
             Map<String, String> headers,
             T t,
             OnResponse<O> onResponse);

    /**
     * Makes an asynchronous POST HTTP request
     *
     * @param headers
     * @param t       Entity
     */
    void post(URL url,
              Map<String, String> headers,
              T t,
              OnResponse<O> onResponse);

    /**
     * Makes an asynchronous DELETE HTTP request
     *
     * @param headers
     */
    void delete(URL url,
                Map<String, String> headers,
                OnResponse<O> onResponse);

    /**
     * Test current server connectivity
     *
     * @param headers
     * @param circuitTestCallback
     */
    void circuitTester(URL url, Map<String, String> headers, OnCircuitTest circuitTestCallback);
}
