package me.nunum.whereami.model.request;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import me.nunum.whereami.model.WifiDataSample;

public final class NewPredictionRequest {

    private String lastUpdate;

    private String clientNow;

    private boolean onlyPolling;

    private List<WifiDataSample> samples;

    public NewPredictionRequest() {
        this(new Date(), new Date(), true, new ArrayList<WifiDataSample>());
    }

    public NewPredictionRequest(Date lastUpdate, boolean onlyPolling) {
        this(lastUpdate, new Date(), onlyPolling, new ArrayList<WifiDataSample>());
    }

    public NewPredictionRequest(Date lastUpdate, boolean onlyPolling, List<WifiDataSample> samples) {
        this(lastUpdate, new Date(), onlyPolling, samples);
    }

    public NewPredictionRequest(Date lastUpdate, Date clientNow, boolean onlyPolling, List<WifiDataSample> samples) {
        SimpleDateFormat simpleDateFormat =
                new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SS'Z'");

        this.lastUpdate = simpleDateFormat.format(lastUpdate);
        this.clientNow = simpleDateFormat.format(clientNow);
        this.onlyPolling = onlyPolling;
        this.samples = samples;
    }

    public String getLastUpdate() {
        return lastUpdate;
    }

    public void setLastUpdate(String lastUpdate) {
        this.lastUpdate = lastUpdate;
    }

    public String getClientNow() {
        return clientNow;
    }

    public void setClientNow(String clientNow) {
        this.clientNow = clientNow;
    }

    public boolean isOnlyPolling() {
        return onlyPolling;
    }

    public void setOnlyPolling(boolean onlyPolling) {
        this.onlyPolling = onlyPolling;
    }

    public List<WifiDataSample> getSamples() {
        return samples;
    }

    public void setSamples(List<WifiDataSample> samples) {
        this.samples = samples;
    }
}
