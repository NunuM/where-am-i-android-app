package me.nunum.whereami.service.httpimpl;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;
import android.widget.ImageView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.net.URL;
import java.util.Map;

import me.nunum.whereami.framework.AsyncHttp;
import me.nunum.whereami.framework.OnCircuitTest;
import me.nunum.whereami.framework.OnResponse;

public class AsyncHttpImpl<T, O> implements AsyncHttp<T, O> {

    private static final String TAG = AsyncHttp.class.getSimpleName();

    private static final String DEFAULT_CONTENT_TYPE = "application/json";

    private final Type tClass;

    private final Gson marshaller;
    private static RequestQueue requestQueue;

    @SuppressWarnings("unchecked")
    public AsyncHttpImpl(Context context, Gson marshaller, Type aClass) {

        this.marshaller = marshaller;
        this.tClass = aClass;
        if (requestQueue == null) {
            requestQueue = Volley.newRequestQueue(context);
        }
    }

    /**
     * Makes an asynchronous GET HTTP request
     *
     * @param url
     * @param headers
     */
    @Override
    public void get(URL url,
                    Map<String, String> headers,
                    OnResponse<O> onResponse) {

        ResponseHandler<O> handler = new ResponseHandler<>(tClass, marshaller, onResponse);

        Log.d(TAG, "get  " + url.toString());
        requestQueue.add(new GetRequest(url.toString(), headers, handler, handler));
    }


    /**
     * Makes an asynchronous GET HTTP request
     *
     * @param url
     * @param headers
     * @param useCache
     */
    public static void downloadImage(URL url,
                                     Map<String, String> headers,
                                     final OnResponse<Bitmap> onResponse, boolean useCache) {

        Request<?> request = new BitmapRequest(url.toString(), headers, new Response.Listener<Bitmap>() {
            @Override
            public void onResponse(Bitmap response) {
                onResponse.onSuccess(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                onResponse.onFailure(error);
            }
        }).setShouldCache(useCache);

        requestQueue.add(request);
    }


    @Override
    public void get(URL url,
                    Type type,
                    Map<String, String> headers,
                    OnResponse<O> onResponse) {

        ResponseHandler<O> handler = new ResponseHandler<>(type, marshaller, onResponse);

        Log.d(TAG, "get  " + url.toString());
        requestQueue.add(new GetRequest(url.toString(), headers, handler, handler));
    }


    public void get(URL url,
                    Type type,
                    Map<String, String> headers,
                    OnResponse<O> onResponse, boolean useCache) {

        ResponseHandler<O> handler = new ResponseHandler<>(type, marshaller, onResponse);

        Log.d(TAG, "get  " + url.toString());
        requestQueue.add(new GetRequest(url.toString(), headers, handler, handler).setShouldCache(useCache));
    }

    /**
     * Makes an asynchronous PUT HTTP request
     *
     * @param url
     * @param headers
     * @param t       Entity
     */
    @Override
    public void put(URL url,
                    Map<String, String> headers,
                    T t,
                    OnResponse<O> onResponse) {

        ResponseHandler<O> handler = new ResponseHandler<>(tClass, marshaller, onResponse);

        Log.d(TAG, "put  " + url.toString());
        requestQueue.add(new PostRequest(Request.Method.PUT, url.toString(), this.marshaller.toJson(t), headers, handler, handler));
    }

    /**
     * Makes an asynchronous POST HTTP request
     *
     * @param url
     * @param headers
     * @param t       Entity
     */
    @Override
    public void post(URL url,
                     Map<String, String> headers,
                     T t,
                     OnResponse<O> onResponse) {

        ResponseHandler<O> handler = new ResponseHandler<>(tClass, marshaller, onResponse);

        Log.d(TAG, "post  " + url.toString());
        requestQueue.add(new PostRequest(url.toString(), this.marshaller.toJson(t), headers, handler, handler));
    }

    /**
     * Makes an asynchronous DELETE HTTP request
     *
     * @param url
     * @param headers
     */
    @Override
    public void delete(URL url,
                       Map<String, String> headers,
                       OnResponse<O> onResponse) {

        ResponseHandler<O> handler = new ResponseHandler<>(tClass, marshaller, onResponse);

        Log.d(TAG, "delete " + url.toString());
        requestQueue.add(new DeleteRequest(url.toString(), headers, handler, handler));
    }

    /**
     * Test current server connectivity
     *
     * @param url
     * @param headers
     * @param circuitTestCallback
     */
    @Override
    public void circuitTester(URL url, Map<String, String> headers, OnCircuitTest circuitTestCallback) {
        Type type = new TypeToken<Void>() {
        }.getType();

        TestHandler handler = new TestHandler(circuitTestCallback);
        requestQueue.add(new GetRequest(url.toString(), headers, handler, handler));
    }


    protected class GetRequest extends StringRequest {

        private final Map<String, String> headers;

        /**
         * Creates a new GET request.
         *
         * @param url           URL to fetch the string at
         * @param listener      Listener to receive the String response
         * @param errorListener Error listener, or null to ignore errors
         */
        public GetRequest(String url,
                          Map<String, String> headers,
                          Response.Listener<String> listener,
                          Response.ErrorListener errorListener) {
            super(Method.GET, url, listener, errorListener);
            this.headers = headers;
        }

        /**
         * Returns a list of extra HTTP headers to go along with this request. Can
         * throw {@link AuthFailureError} as authentication may be required to
         * provide these values.
         *
         * @throws AuthFailureError In the event of auth failure
         */
        @Override
        public Map<String, String> getHeaders() throws AuthFailureError {
            return this.headers;
        }
    }

    protected class PostRequest extends StringRequest {

        private final String marshaled;
        private final Map<String, String> headers;


        /**
         * Create a POST request.
         *
         * @param url
         * @param marshaled
         * @param headers
         * @param listener
         * @param errorListener
         */
        public PostRequest(String url,
                           String marshaled,
                           Map<String, String> headers,
                           Response.Listener<String> listener,
                           Response.ErrorListener errorListener) {

            super(Method.POST, url, listener, errorListener);
            this.headers = headers;
            this.marshaled = marshaled;
        }


        /**
         * Create a POST request.
         *
         * @param url
         * @param marshaled
         * @param headers
         * @param listener
         * @param errorListener
         */
        public PostRequest(int method,
                           String url,
                           String marshaled,
                           Map<String, String> headers,
                           Response.Listener<String> listener,
                           Response.ErrorListener errorListener) {

            super(method, url, listener, errorListener);
            this.headers = headers;
            this.marshaled = marshaled;
        }

        /**
         * Returns the content type of the POST or PUT body.
         */
        @Override
        public String getBodyContentType() {
            return DEFAULT_CONTENT_TYPE;
        }

        /**
         * Returns a list of extra HTTP headers to go along with this request. Can
         * throw {@link AuthFailureError} as authentication may be required to
         * provide these values.
         *
         * @throws AuthFailureError In the event of auth failure
         */
        @Override
        public Map<String, String> getHeaders() throws AuthFailureError {
            return this.headers;
        }

        /**
         * Returns the raw POST or PUT body to be sent.
         * <p>
         * <p>By default, the body consists of the request parameters in
         * application/x-www-form-urlencoded format. When overriding this method, consider overriding
         * {@link #getBodyContentType()} as well to match the new body format.
         *
         * @throws AuthFailureError in the event of auth failure
         */
        @Override
        public byte[] getBody() throws AuthFailureError {
            return this.marshaled.getBytes();
        }
    }

    public static class BitmapRequest extends ImageRequest {

        private final Map<String, String> headers;

        public BitmapRequest(String url,
                             Map<String, String> headers,
                             Response.Listener<Bitmap> listener,
                             Response.ErrorListener errorListener) {
            super(url,
                    listener,
                    200,
                    200,
                    ImageView.ScaleType.CENTER_CROP,
                    Bitmap.Config.ALPHA_8,
                    errorListener);
            this.headers = headers;
        }

        @Override
        public Map<String, String> getHeaders() throws AuthFailureError {
            return this.headers;
        }
    }


    protected class DeleteRequest extends StringRequest {

        private final Map<String, String> headers;

        /**
         * Creates a new GET request.
         *
         * @param url           URL to fetch the string at
         * @param listener      Listener to receive the String response
         * @param errorListener Error listener, or null to ignore errors
         */
        public DeleteRequest(String url,
                             Map<String, String> headers,
                             Response.Listener<String> listener,
                             Response.ErrorListener errorListener) {
            super(Method.DELETE, url, listener, errorListener);
            this.headers = headers;
        }

        /**
         * Returns a list of extra HTTP headers to go along with this request. Can
         * throw {@link AuthFailureError} as authentication may be required to
         * provide these values.
         *
         * @throws AuthFailureError In the event of auth failure
         */
        @Override
        public Map<String, String> getHeaders() throws AuthFailureError {
            return this.headers;
        }
    }

    protected class TestHandler
            implements Response.Listener<String>, Response.ErrorListener {

        private final OnCircuitTest onCircuitTest;

        public TestHandler(OnCircuitTest onCircuitTest) {
            this.onCircuitTest = onCircuitTest;
        }

        /**
         * Callback method that an error has been occurred with the
         * provided error code and optional user-readable message.
         *
         * @param error
         */
        @Override
        public void onErrorResponse(VolleyError error) {
            onCircuitTest.onConnectionFailed();
        }

        /**
         * Called when a response is received.
         *
         * @param response
         */
        @Override
        public void onResponse(String response) {
            onCircuitTest.onConnectionSucceeded();
        }
    }


    protected class ResponseHandler<O>
            implements Response.Listener<String>, Response.ErrorListener {


        private final Type type;
        private final Gson marshaller;
        private final OnResponse<O> onHttpResponse;


        public ResponseHandler(Type type,
                               Gson marshaller,
                               OnResponse<O> onHttpResponse) {
            this.type = type;
            this.marshaller = marshaller;
            this.onHttpResponse = onHttpResponse;
        }

        /**
         * Called when a response is received.
         *
         * @param response
         */
        @Override
        public void onResponse(String response) {

            try {

                this.onHttpResponse.onSuccess((O) this.marshaller.fromJson(response, this.type));

            } catch (JsonSyntaxException exception) {
                this.onHttpResponse.onFailure(exception);
            } catch (JsonParseException exception) {
                this.onHttpResponse.onFailure(exception);
            }
        }


        /**
         * Callback method that an error has been occurred with the
         * provided error code and optional user-readable message.
         *
         * @param error
         */
        @Override
        public void onErrorResponse(VolleyError error) {
            this.onHttpResponse.onFailure(error);
        }

    }
}
