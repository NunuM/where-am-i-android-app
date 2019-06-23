package me.nunum.whereami.model.marshaller;


import android.content.ContentValues;

import me.nunum.whereami.framework.Marshaller;
import me.nunum.whereami.model.WifiDataSample;

public class PersistenceWifiMarshaller implements Marshaller<WifiDataSample, ContentValues> {

    @Override
    public ContentValues marshall(WifiDataSample wifiData) {

        ContentValues values = new ContentValues();

        values.put("bssid", wifiData.getBssid());
        values.put("timestamp", wifiData.getTimeStamp());
        values.put("position", wifiData.getPositionId());
        values.put("centerfreq0", wifiData.getCenterFreq0());
        values.put("centerfreq1", wifiData.getCenterFreq1());
        values.put("channel_width", wifiData.getChannelWidth());
        values.put("frequency", wifiData.getFrequency());
        values.put("leveldbm", wifiData.getLevelDBM());
        values.put("localization", wifiData.getLocalizationId());
        values.put("ssid", wifiData.getSsid());

        return values;
    }
}