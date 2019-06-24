package me.nunum.whereami.service.httpimpl;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;
import android.widget.ImageView;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.StringRequest;
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

    private static final long ONE_DAY_IN_MS = 86400000L;

    private static final String TAG = AsyncHttp.class.getSimpleName();

    private static final String DEFAULT_CONTENT_TYPE = "application/json";
    private final RequestQueue requestQueue;
    private final Type tClass;
    private final Gson marshaller;

    public AsyncHttpImpl(Context context, Gson marshaller, Type aClass) {

        this.marshaller = marshaller;
        this.tClass = aClass;
        this.requestQueue = RequestQueueSingleton.getInstance(context).getRequestQueue();
    }


    public static void downloadImage(URL url,
                                     Map<String, String> headers,
                                     final OnResponse<Bitmap> onResponse, boolean useCache, Context context) {

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

        RequestQueueSingleton.getInstance(context).getRequestQueue().add(request);
    }


    @Override
    public void get(URL url,
                    Map<String, String> headers,
                    OnResponse<O> onResponse) {

        ResponseHandler<O> handler = new ResponseHandler<>(tClass, marshaller, onResponse);

        Log.d(TAG, "get  " + url.toString());
        requestQueue.add(new GetRequest(url.toString(), headers, handler, handler).setShouldCache(false));
    }

    @Override
    public void get(URL url,
                    Type type,
                    Map<String, String> headers,
                    OnResponse<O> onResponse) {

        ResponseHandler<O> handler = new ResponseHandler<>(type, marshaller, onResponse);

        Log.d(TAG, "get  " + url.toString());
        requestQueue.add(new GetRequest(url.toString(), headers, handler, handler).setShouldCache(false));
    }


    public void get(URL url,
                    Type type,
                    Map<String, String> headers,
                    OnResponse<O> onResponse, boolean useCache) {

        ResponseHandler<O> handler = new ResponseHandler<>(type, marshaller, onResponse);

        Log.d(TAG, "get  " + url.toString());
        requestQueue.add(new GetRequest(url.toString(), headers, handler, handler).setShouldCache(useCache));
    }


    @Override
    public void put(URL url,
                    Map<String, String> headers,
                    T t,
                    OnResponse<O> onResponse) {

        ResponseHandler<O> handler = new ResponseHandler<>(tClass, marshaller, onResponse);

        Log.d(TAG, "put  " + url.toString());
        requestQueue.add(new PostRequest(Request.Method.PUT, url.toString(), this.marshaller.toJson(t), headers, handler, handler).setShouldCache(false));
    }


    @Override
    public void post(URL url,
                     Map<String, String> headers,
                     T t,
                     OnResponse<O> onResponse) {

        ResponseHandler<O> handler = new ResponseHandler<>(tClass, marshaller, onResponse);

        Log.d(TAG, "post  " + url.toString());
        requestQueue.add(new PostRequest(url.toString(), this.marshaller.toJson(t), headers, handler, handler).setShouldCache(false));
    }


    @Override
    public void delete(URL url,
                       Map<String, String> headers,
                       OnResponse<O> onResponse) {

        ResponseHandler<O> handler = new ResponseHandler<>(tClass, marshaller, onResponse);

        Log.d(TAG, "delete " + url.toString());
        requestQueue.add(new DeleteRequest(url.toString(), headers, handler, handler).setShouldCache(false));
    }


    @Override
    public void circuitTester(URL url, Map<String, String> headers, OnCircuitTest circuitTestCallback) {
        Type type = new TypeToken<Void>() {
        }.getType();

        TestHandler handler = new TestHandler(circuitTestCallback);
        requestQueue.add(new GetRequest(url.toString(), headers, handler, handler).setShouldCache(false));
    }

    static class BitmapRequest extends ImageRequest {


        private final Map<String, String> headers;

        BitmapRequest(String url,
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

        @SuppressWarnings("RedundantThrows")
        @Override
        public Map<String, String> getHeaders() throws AuthFailureError {
            return this.headers;
        }


        @Override
        protected Response<Bitmap> parseNetworkResponse(NetworkResponse response) {
            Response<Bitmap> bitmapResponse = super.parseNetworkResponse(response);
            long diff = System.currentTimeMillis() - bitmapResponse.cacheEntry.serverDate;
            if (diff < ONE_DAY_IN_MS) {
                bitmapResponse.cacheEntry.ttl = System.currentTimeMillis() + ONE_DAY_IN_MS;
            }
            return bitmapResponse;
        }
    }

    class GetRequest extends StringRequest {

        private final Map<String, String> headers;

        /**
         * Creates a new GET request.
         *
         * @param url           URL to fetch the string at
         * @param listener      Listener to receive the String response
         * @param errorListener Error listener, or null to ignore errors
         */
        GetRequest(String url,
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
        @SuppressWarnings("RedundantThrows")
        @Override
        public Map<String, String> getHeaders() throws AuthFailureError {
            return this.headers;
        }

        @Override
        protected Response<String> parseNetworkResponse(NetworkResponse response) {
            return super.parseNetworkResponse(response);
        }
    }

    class PostRequest extends StringRequest {

        private final String marshaled;
        private final Map<String, String> headers;


        private PostRequest(String url,
                            String marshaled,
                            Map<String, String> headers,
                            Response.Listener<String> listener,
                            Response.ErrorListener errorListener) {

            super(Method.POST, url, listener, errorListener);
            this.headers = headers;
            this.marshaled = marshaled;
        }


        private PostRequest(int method,
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
        @SuppressWarnings("RedundantThrows")
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
        @SuppressWarnings("RedundantThrows")
        @Override
        public byte[] getBody() throws AuthFailureError {
            return this.marshaled.getBytes();
        }
    }

    class DeleteRequest extends StringRequest {

        private final Map<String, String> headers;

        /**
         * Creates a new GET request.
         *
         * @param url           URL to fetch the string at
         * @param listener      Listener to receive the String response
         * @param errorListener Error listener, or null to ignore errors
         */
        DeleteRequest(String url,
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
        @SuppressWarnings("RedundantThrows")
        @Override
        public Map<String, String> getHeaders() throws AuthFailureError {
            return this.headers;
        }
    }

    class TestHandler
            implements Response.Listener<String>, Response.ErrorListener {

        private final OnCircuitTest onCircuitTest;

        private TestHandler(OnCircuitTest onCircuitTest) {
            this.onCircuitTest = onCircuitTest;
        }


        @Override
        public void onErrorResponse(VolleyError error) {
            onCircuitTest.onConnectionFailed();
        }

        @Override
        public void onResponse(String response) {
            onCircuitTest.onConnectionSucceeded();
        }
    }


    @SuppressWarnings("TypeParameterHidesVisibleType")
    class ResponseHandler<O>
            implements Response.Listener<String>, Response.ErrorListener {


        private final Type type;
        private final Gson marshaller;
        private final OnResponse<O> onHttpResponse;


        private ResponseHandler(Type type,
                                Gson marshaller,
                                OnResponse<O> onHttpResponse) {
            this.type = type;
            this.marshaller = marshaller;
            this.onHttpResponse = onHttpResponse;
        }


        @Override
        @SuppressWarnings("unchecked")
        public void onResponse(String response) {

            try {

                this.onHttpResponse.onSuccess((O) this.marshaller.fromJson(response, this.type));

            } catch (JsonSyntaxException exception) {
                this.onHttpResponse.onFailure(exception);
            } catch (JsonParseException exception) {
                this.onHttpResponse.onFailure(exception);
            }
        }


        @Override
        public void onErrorResponse(VolleyError error) {
            this.onHttpResponse.onFailure(error);
        }

    }
}
