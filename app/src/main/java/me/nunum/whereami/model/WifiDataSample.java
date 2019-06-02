package me.nunum.whereami.model;

import android.net.wifi.ScanResult;
import android.os.Build;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class WifiDataSample {

    private String bssid;
    private String ssid;
    private int levelDBM;
    private int centerFreq0;
    private int centerFreq1;
    private int channelWidth;
    private int frequency;
    private String timeStamp;
    private Long positionId;
    private Long floorId;
    private Long localizationId;

    private WifiDataSample() {

    }

    public WifiDataSample(String bssid,
                    String ssid,
                    int levelDBM,
                    int centerFreq0,
                    int centerFreq1,
                    int channelWidth,
                    int frequency,
                    String timeStamp,
                    long positionId,
                    long floorId,
                    long localizationId) {

        this.bssid = bssid;
        this.ssid = ssid;
        this.levelDBM = levelDBM;
        this.centerFreq0 = centerFreq0;
        this.centerFreq1 = centerFreq1;
        this.channelWidth = channelWidth;
        this.frequency = frequency;
        this.timeStamp = timeStamp;
        this.positionId = positionId;
        this.floorId = floorId;
        this.localizationId = localizationId;
    }

    public String getBssid() {
        return bssid;
    }

    public void setBssid(String bssid) {
        this.bssid = bssid;
    }

    public String getSsid() {
        return ssid;
    }

    public void setSsid(String ssid) {
        this.ssid = ssid;
    }

    public int getLevelDBM() {
        return levelDBM;
    }

    public void setLevelDBM(int levelDBM) {
        this.levelDBM = levelDBM;
    }

    public int getCenterFreq0() {
        return centerFreq0;
    }

    public void setCenterFreq0(int centerFreq0) {
        this.centerFreq0 = centerFreq0;
    }

    public int getCenterFreq1() {
        return centerFreq1;
    }

    public void setCenterFreq1(int centerFreq1) {
        this.centerFreq1 = centerFreq1;
    }

    public int getChannelWidth() {
        return channelWidth;
    }

    public void setChannelWidth(int channelWidth) {
        this.channelWidth = channelWidth;
    }

    public int getFrequency() {
        return frequency;
    }

    public void setFrequency(int frequency) {
        this.frequency = frequency;
    }

    public String getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(String timeStamp) {
        this.timeStamp = timeStamp;
    }


    public void setPositionId(Long positionId) {
        this.positionId = positionId;
    }

    public void setFloorId(Long floorId) {
        this.floorId = floorId;
    }

    public void setLocalizationId(Long localizationId) {
        this.localizationId = localizationId;
    }

    public Long getPositionId() {
        return positionId;
    }

    public Long getFloorId() {
        return floorId;
    }

    public Long getLocalizationId() {
        return localizationId;
    }

    public static WifiDataSample mapFromScanResult(ScanResult result, Localization localization, Position position) {
        WifiDataSample data = new WifiDataSample();
        data.setBssid(result.BSSID);
        data.setSsid(result.SSID);
        data.setLevelDBM(result.level);

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            data.setCenterFreq0(-1);
            data.setCenterFreq1(-1);
            data.setChannelWidth(-1);
        } else {
            data.setCenterFreq0(result.centerFreq0);
            data.setCenterFreq1(result.centerFreq1);
            data.setChannelWidth(result.channelWidth);
        }


        data.setFrequency(result.frequency);
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        data.setTimeStamp(dateFormat.format(new Date()));

        data.setPositionId(position.getId());
        data.setLocalizationId(localization.getId());
        data.setFloorId(0L);

        return data;
    }

}
