package me.nunum.whereami.service;

import android.content.Context;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;

import java.util.ArrayList;
import java.util.List;

import me.nunum.whereami.framework.Receiver;
import me.nunum.whereami.model.Localization;
import me.nunum.whereami.model.Position;
import me.nunum.whereami.model.WifiDataSample;

public class WifiService implements Runnable {

    private final Position position;
    private final Localization localization;
    private final WifiManager wifiManager;
    private final Receiver<List<WifiDataSample>> receiver;

    public WifiService(Context context,
                       Position position,
                       Localization localization,
                       Receiver<List<WifiDataSample>> receiver) {
        this.position = position;
        this.receiver = receiver;
        this.localization = localization;
        this.wifiManager = (WifiManager) context
                .getApplicationContext()
                .getSystemService(Context.WIFI_SERVICE);
    }


    /**
     * When an object implementing interface <code>Runnable</code> is used
     * to create a thread, starting the thread causes the object's
     * <code>run</code> method to be called in that separately executing
     * thread.
     * <p>
     * The general contract of the method <code>run</code> is that it may
     * take any action whatsoever.
     *
     * @see Thread#run()
     */
    @Override
    public void run() {

        if (this.wifiManager.isWifiEnabled()) {
            if (this.wifiManager.startScan()) {

                final List<ScanResult> scanResults = this.wifiManager.getScanResults();
                final List<WifiDataSample> dataSamples = new ArrayList<>(scanResults.size());

                for (ScanResult scan : scanResults) {
                    dataSamples.add(WifiDataSample.mapFromScanResult(scan, this.localization, this.position));
                }

                this.receiver.receive(dataSamples, 0L);
            }
        }


    }
}
