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
    private Long position;
    private Long floorid;
    private Long localization;

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
                    long position,
                    long floorid,
                    long localization) {

        this.bssid = bssid;
        this.ssid = ssid;
        this.levelDBM = levelDBM;
        this.centerFreq0 = centerFreq0;
        this.centerFreq1 = centerFreq1;
        this.channelWidth = channelWidth;
        this.frequency = frequency;
        this.timeStamp = timeStamp;
        this.position = position;
        this.floorid = floorid;
        this.localization = localization;
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


    public void setPosition(Long position) {
        this.position = position;
    }

    public void setFloorid(Long floorid) {
        this.floorid = floorid;
    }

    public void setLocalization(Long localization) {
        this.localization = localization;
    }

    public Long getPosition() {
        return position;
    }

    public Long getFloorid() {
        return floorid;
    }

    public Long getLocalization() {
        return localization;
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

        data.setPosition(position.getId());
        data.setLocalization(localization.getId());
        data.setFloorid(0L);

        return data;
    }

}
