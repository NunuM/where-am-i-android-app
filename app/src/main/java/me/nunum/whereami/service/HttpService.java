package me.nunum.whereami.service;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

import me.nunum.whereami.framework.AsyncHttp;
import me.nunum.whereami.framework.OnCircuitTest;
import me.nunum.whereami.framework.OnResponse;
import me.nunum.whereami.framework.OnSync;
import me.nunum.whereami.framework.Receiver;
import me.nunum.whereami.model.Algorithm;
import me.nunum.whereami.model.Localization;
import me.nunum.whereami.model.Position;
import me.nunum.whereami.model.Post;
import me.nunum.whereami.model.TrainingProgress;
import me.nunum.whereami.model.WifiDataSample;
import me.nunum.whereami.model.request.NewLocalizationRequest;
import me.nunum.whereami.model.request.NewPositionRequest;
import me.nunum.whereami.model.request.NewTrainingRequest;
import me.nunum.whereami.model.request.PostRequest;
import me.nunum.whereami.model.request.SpamRequest;
import me.nunum.whereami.service.application.ApplicationPreferences;
import me.nunum.whereami.service.httpimpl.AsyncHttpImpl;
import me.nunum.whereami.utils.AppConfig;

import static me.nunum.whereami.service.application.ApplicationPreferences.KEYS;

public final class HttpService {

    private final static String TAG = HttpService.class.getSimpleName();

    private static HttpService service;

    private final Context context;
    private final Gson gson;
    private final ApplicationPreferences applicationPreferences;

    private final Map<String, String> headers;

    private HttpService(Context context, Gson gson) {

        this.context = context;
        this.gson = gson;
        this.applicationPreferences = ApplicationPreferences.instance(context);

        this.headers = new HashMap<>(5);

        this.headers.put("accept", AppConfig.JSON_MIME_TYPE_HEADER);
        this.headers.put("x-app", this.applicationPreferences.getStringKey(KEYS.INSTANCE_ID));
        this.headers.put("locale", Locale.getDefault().getCountry());
        this.headers.put("timezone", TimeZone.getDefault().getID());
    }

    public static HttpService create(Context context, Gson marshaller) {

        if (service == null) {
            service = new HttpService(context, marshaller);
        }

        return service;
    }

    /**
     * @param localizationRequest
     * @param onResponse
     * @return boolean
     */
    public boolean newLocalization(NewLocalizationRequest localizationRequest, OnResponse<Localization> onResponse) {

        final String host = this.applicationPreferences.getStringKey(KEYS.HTTP_REMOTE_HOST);

        final String resource = this.applicationPreferences.getStringKey(KEYS.HTTP_LOCALIZATIONS_RESOURCE);

        final AsyncHttp<NewLocalizationRequest, Localization> asyncHttp = new AsyncHttpImpl<>(this.context, this.gson, Localization.class);

        asyncHttp.post(this.makeURL(host, resource), this.headers, localizationRequest, onResponse);

        return true;
    }

    public boolean image(String url, OnResponse<Bitmap> onResponse){

        AsyncHttpImpl.downloadImage(this.makeURL(url,""), this.headers, onResponse, true);

        return true;
    }



    public boolean paginateLocalizations(int currentPage, OnResponse<List<Localization>> onResponse) {

        final String host = this.applicationPreferences.getStringKey(KEYS.HTTP_REMOTE_HOST);

        final String resource = this.applicationPreferences.getStringKey(KEYS.HTTP_LOCALIZATIONS_RESOURCE);

        final Type type = new TypeToken<List<Localization>>() {
        }.getType();

        final AsyncHttp<Void, List<Localization>> asyncHttp = new AsyncHttpImpl<>(this.context, this.gson, type);

        asyncHttp.get(this.makeURL(host, resource, String.format(Locale.getDefault(), "page=%d", currentPage)), type, this.headers, onResponse);

        return true;
    }


    public boolean deleteLocalization(long id, OnResponse<Void> onResponse) {

        final String host = this.applicationPreferences.getStringKey(KEYS.HTTP_REMOTE_HOST);

        final String resource = this.applicationPreferences.getStringKey(KEYS.HTTP_LOCALIZATIONS_RESOURCE);

        final AsyncHttp<Void, Void> asyncHttp = new AsyncHttpImpl<>(this.context, this.gson, Void.class);

        asyncHttp.delete(this.makeURL(host, resource.concat("/" + id)), this.headers, onResponse);

        return true;
    }

    public boolean getLocalizationTrainingInformation(Localization localization,
                                                      OnResponse<List<TrainingProgress>> onResponse) {

        final String host = this.applicationPreferences.getStringKey(KEYS.HTTP_REMOTE_HOST);

        String resource = this.applicationPreferences.getStringKey(KEYS.HTTP_TRAINING_RESOURCE);

        if (resource.contains(":id")) {
            resource = resource.replace(":id", String.valueOf(localization.id()));
        }

        final Type type = new TypeToken<List<TrainingProgress>>() {
        }.getType();

        final AsyncHttp<Void, List<TrainingProgress>> asyncHttp = new AsyncHttpImpl<>(this.context, this.gson, type);

        asyncHttp.get(this.makeURL(host, resource), headers, onResponse);

        return true;
    }


    public boolean newSpam(SpamRequest request, OnResponse<Void> onResponse) {

        final String host = this.applicationPreferences.getStringKey(KEYS.HTTP_REMOTE_HOST);

        String resource = this.applicationPreferences.getStringKey(KEYS.HTTP_LOCALIZATION_SPAM_RESOURCE);

        if (resource.contains(":id")) {
            resource = resource.replace(":id", String.valueOf(request.getId()));
        }

        final AsyncHttp<SpamRequest, Void> asyncHttp = new AsyncHttpImpl<>(this.context, this.gson, Void.class);

        asyncHttp.post(this.makeURL(host, resource), this.headers, request, onResponse);

        return true;
    }


    public boolean newPositionSpam(long localization, SpamRequest request, OnResponse<Void> onResponse) {

        final String host = this.applicationPreferences.getStringKey(KEYS.HTTP_REMOTE_HOST);

        String resource = this.applicationPreferences.getStringKey(KEYS.HTTP_POSITION_SPAM_RESOURCE);

        if (resource.contains(":id")) {
            resource = resource.replace(":id", String.valueOf(localization));
        }

        if (resource.contains(":it")) {
            resource = resource.replace(":it", String.valueOf(request.getId()));
        }

        final AsyncHttp<SpamRequest, Void> asyncHttp = new AsyncHttpImpl<>(this.context, this.gson, Void.class);

        asyncHttp.post(this.makeURL(host, resource), this.headers, request, onResponse);

        return true;
    }

    public boolean newTrain(long id, NewTrainingRequest request, OnResponse<TrainingProgress> onResponse) {

        final String host = this.applicationPreferences.getStringKey(KEYS.HTTP_REMOTE_HOST);

        String resource = this.applicationPreferences.getStringKey(KEYS.HTTP_TRAINING_RESOURCE);

        if (resource.contains(":id")) {
            resource = resource.replace(":id", String.valueOf(id));
        }

        final AsyncHttp<NewTrainingRequest, TrainingProgress> asyncHttp = new AsyncHttpImpl<>(this.context, this.gson, TrainingProgress.class);

        asyncHttp.post(this.makeURL(host, resource), this.headers, request, onResponse);

        return true;
    }

    public boolean deleteTraining(long id, TrainingProgress request, OnResponse<Void> onResponse) {

        final String host = this.applicationPreferences.getStringKey(KEYS.HTTP_REMOTE_HOST);

        String resource = this.applicationPreferences.getStringKey(KEYS.HTTP_TRAINING_RESOURCE);

        if (resource.contains(":id")) {
            resource = resource.replace(":id", String.valueOf(id));
        }

        resource += "/" + request.getId();

        final AsyncHttp<Void, Void> asyncHttp = new AsyncHttpImpl<>(this.context, this.gson, Void.class);

        asyncHttp.delete(this.makeURL(host, resource), this.headers, onResponse);

        return true;
    }

    public boolean newPost(PostRequest request, OnResponse<Post> onResponse) {

        final String host = this.applicationPreferences.getStringKey(KEYS.HTTP_REMOTE_HOST);

        final String resource = this.applicationPreferences.getStringKey(KEYS.HTTP_POSTS_RESOURCE);

        final AsyncHttp<PostRequest, Post> asyncHttp = new AsyncHttpImpl<>(this.context, this.gson, Post.class);

        asyncHttp.post(this.makeURL(host, resource), this.headers, request, onResponse);

        return true;
    }


    public boolean paginatePosts(int currentPage, OnResponse<List<Post>> onResponse) {

        final String host = this.applicationPreferences.getStringKey(KEYS.HTTP_REMOTE_HOST);

        final String resource = this.applicationPreferences.getStringKey(KEYS.HTTP_POSTS_RESOURCE);

        final Type type = new TypeToken<List<Localization>>() {
        }.getType();

        final AsyncHttpImpl<Void, List<Post>> asyncHttp = new AsyncHttpImpl<>(this.context, this.gson, type);


        asyncHttp.get(this.makeURL(host, resource, String.format(Locale.getDefault(), "page=%d", currentPage)), type, this.headers, onResponse, true);

        return true;
    }


    public boolean deletePost(long id, OnResponse<Void> onResponse) {

        final String host = this.applicationPreferences.getStringKey(KEYS.HTTP_REMOTE_HOST);

        final String resource = this.applicationPreferences.getStringKey(KEYS.HTTP_POSTS_RESOURCE);

        final AsyncHttp<Void, Void> asyncHttp = new AsyncHttpImpl<>(this.context, this.gson, Void.class);

        asyncHttp.delete(this.makeURL(host, resource.concat("/" + id)), this.headers, onResponse);

        return true;
    }


    public boolean newPosition(Localization localization, NewPositionRequest request, OnResponse<Position> onResponse) {

        final String host = this.applicationPreferences.getStringKey(KEYS.HTTP_REMOTE_HOST);

        String resource = this.applicationPreferences.getStringKey(KEYS.HTTP_LOCALIZATION_POSITIONS_RESOURCE);

        if (resource.contains(":id")) {
            resource = resource.replace(":id", String.valueOf(localization.id()));
        }

        final AsyncHttpImpl<NewPositionRequest, Position> asyncHttp = new AsyncHttpImpl<>(this.context, this.gson, Position.class);

        asyncHttp.post(this.makeURL(host, resource), this.headers, request, onResponse);

        return true;
    }


    public boolean allPositions(Localization localization, OnResponse<List<Position>> onResponse) {

        final String host = this.applicationPreferences.getStringKey(KEYS.HTTP_REMOTE_HOST);

        String resource = this.applicationPreferences.getStringKey(KEYS.HTTP_LOCALIZATION_POSITIONS_RESOURCE);

        if (resource.contains(":id")) {
            resource = resource.replace(":id", String.valueOf(localization.id()));
        }


        final Type type = new TypeToken<List<Position>>() {
        }.getType();

        final AsyncHttpImpl<Void, List<Position>> asyncHttp = new AsyncHttpImpl<>(this.context, this.gson, type);

        asyncHttp.get(this.makeURL(host, resource), type, this.headers, onResponse);

        return true;
    }

    public boolean allAlgorithms(OnResponse<List<Algorithm>> onResponse) {

        final String host = this.applicationPreferences.getStringKey(KEYS.HTTP_REMOTE_HOST);

        String resource = this.applicationPreferences.getStringKey(KEYS.HTTP_ALGORITHM_RESOURCE);

        final Type type = new TypeToken<List<Algorithm>>() {
        }.getType();

        final AsyncHttpImpl<Void, List<Algorithm>> asyncHttp = new AsyncHttpImpl<>(this.context, this.gson, type);

        asyncHttp.get(this.makeURL(host, resource), type, this.headers, onResponse);

        return true;
    }

    public boolean deletePosition(Localization localization, Position position, OnResponse<Void> onResponse) {

        final String host = this.applicationPreferences.getStringKey(KEYS.HTTP_REMOTE_HOST);

        String resource = this.applicationPreferences.getStringKey(KEYS.HTTP_LOCALIZATION_POSITIONS_RESOURCE);

        if (resource.contains(":id")) {
            resource = resource.replace(":id", String.valueOf(localization.id()));
        }

        final AsyncHttpImpl<Void, Void> asyncHttp = new AsyncHttpImpl<>(this.context, this.gson, Void.class);

        asyncHttp.delete(this.makeURL(host, resource.concat("/" + position.id())), this.headers, onResponse);

        return true;
    }


    public void testConnectivity(OnCircuitTest onCircuitTest) {

        final AsyncHttp<Void, Void> asyncHttp = new AsyncHttpImpl<>(context, gson, Void.class);

        asyncHttp.circuitTester(this.makeURL("https://google.pt", "/"),
                this.headers,
                onCircuitTest);
    }


    private URL makeURL(String host, String resource) {

        String normalizedResource;

        if (resource.startsWith("/")) {
            normalizedResource = host.endsWith("/") ? resource.substring(1) : resource;
        } else {
            normalizedResource = host.endsWith("/") ? resource : "/" + resource;
        }

        try {
            return new URL(host + normalizedResource);
        } catch (MalformedURLException e) {
            Log.e(TAG, "makeURL: Wrong URL format.", e);

            throw new IllegalArgumentException(String.
                    format("Tried to make a http request to %s with resource of %s", host, resource)
                    , e);
        }
    }

    private URL makeURL(String host, String resource, String query) {

        String normalizedResource;

        if (resource.startsWith("/")) {
            normalizedResource = host.endsWith("/") ? resource.substring(1) : resource;
        } else {
            normalizedResource = host.endsWith("/") ? resource : "/" + resource;
        }

        try {
            return new URL(String.format("%s%s?%s", host, normalizedResource, query));
        } catch (MalformedURLException e) {
            Log.e(TAG, "makeURL: Wrong URL format.", e);

            throw new IllegalArgumentException(String.
                    format("Tried to make a http request to %s with resource of %s", host, resource)
                    , e);
        }
    }

    public HttpSinker httpSinker(OnSync onResponse) {
        return new HttpSinker(onResponse);
    }

    private class HttpSinker implements Receiver<List<WifiDataSample>> {

        private final URL url;
        private final OnSync onSync;
        private final AsyncHttp<List<WifiDataSample>, List<Position>> asyncHttp;

        private HttpSinker(OnSync onSync) {

            this.onSync = onSync;

            final Type type = new TypeToken<List<Position>>() {
            }.getType();

            asyncHttp = new AsyncHttpImpl<>(context, gson, type);

            final String host = HttpService.this.applicationPreferences.getStringKey(KEYS.HTTP_REMOTE_HOST);

            String resource = HttpService.this.applicationPreferences.getStringKey(KEYS.HTTP_SAMPLES_RESOURCE);

            this.url = makeURL(host, resource);

        }

        @Override
        public void receive(List<WifiDataSample> wifiDataSamples, final Long batchNumber) {

            asyncHttp.post(this.url, HttpService.this.headers, wifiDataSamples, new OnResponse<List<Position>>() {
                @Override
                public void onSuccess(List<Position> o) {
                    for (int i = 0; i < o.size(); i++) {
                        onSync.batchNumber(batchNumber, o.get(i));
                    }
                }

                @Override
                public void onFailure(Throwable throwable) {
                    onSync.failed(batchNumber, throwable);
                }
            });
        }
    }

}
