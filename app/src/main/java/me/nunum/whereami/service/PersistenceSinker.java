package me.nunum.whereami.service;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import me.nunum.whereami.framework.Emitter;
import me.nunum.whereami.framework.Marshaller;
import me.nunum.whereami.framework.OnSync;
import me.nunum.whereami.framework.Receiver;
import me.nunum.whereami.model.WifiDataSample;
import me.nunum.whereami.model.marshaller.PersistenceWifiMarshaller;
import me.nunum.whereami.service.database.DatabaseService;
import me.nunum.whereami.utils.AppConfig;


public class PersistenceSinker
        implements Receiver<List<WifiDataSample>>,
        Emitter<List<WifiDataSample>>, Runnable {

    private static final String TAG = PersistenceSinker.class.getSimpleName();

    private final Context context;
    private final DatabaseService databaseService;
    private final Marshaller<WifiDataSample, ContentValues> marshaller;
    private final OnSync onSync;
    private final Receiver<List<WifiDataSample>> receiver;

    public PersistenceSinker(Context context, OnSync onSync) {
        this.context = context;
        this.marshaller = new PersistenceWifiMarshaller();
        this.receiver = this;
        this.databaseService = DatabaseService.getInstance(this.context);
        this.onSync = onSync;
    }

    public PersistenceSinker(Context context,
                             OnSync onSync,
                             Receiver<List<WifiDataSample>> receiver) {
        this.context = context;
        this.marshaller = new PersistenceWifiMarshaller();
        this.receiver = receiver;
        this.onSync = onSync;
        this.databaseService = DatabaseService.getInstance(this.context);
    }

    @SuppressWarnings("TryFinallyCanBeTryWithResources")
    @Override
    public synchronized void receive(List<WifiDataSample> data, Long batchNumber) {

        if (this.databaseService == null) {
            return;
        }

        SQLiteDatabase database = null;

        try {
            database = this.databaseService.getWritableDatabase();

            for (WifiDataSample wifiData : data) {
                final ContentValues value = this.marshaller.marshall(wifiData);
                try {
                    database.insertOrThrow(DatabaseService.FINGERPRINT, null, value);
                } catch (SQLException e) {
                    Log.e(TAG, "receive: Cannot store value into database:" + wifiData, e);
                }
            }

        } catch (SQLException exception) {
            Log.e(TAG, "receive: Not able to open db connection", exception);
        } finally {
            if (database != null) {
                database.close();
            }
        }

        onSync.batchNumber(batchNumber, null);
    }


    @Override
    public void run() {
        emitTo(receiver);
    }

    @SuppressWarnings("TryFinallyCanBeTryWithResources")
    @Override
    public void emitTo(Receiver<List<WifiDataSample>> receiver) {


        final List<Long> longs = new ArrayList<>(10);
        final List<WifiDataSample> list = new ArrayList<>(10);

        SQLiteDatabase writableDatabase = null;
        Cursor cursor = null;

        try {
            writableDatabase = databaseService.getWritableDatabase();

            cursor = writableDatabase.rawQuery("SELECT rowid,* FROM " + DatabaseService.FINGERPRINT + " LIMIT " + AppConfig.DATABASE_BATCH, null);

            while (cursor.moveToNext()) {

                final Long rowId = cursor.getLong(0);
                final String bssid = cursor.getString(1);
                final String timestamp = cursor.getString(2);
                final Long position = cursor.getLong(3);
                final Integer centerfreq0 = cursor.getInt(4);
                final Integer centerfreq1 = cursor.getInt(5);
                final Integer channelWidth = cursor.getInt(6);
                final Integer frequency = cursor.getInt(7);
                final Integer leveldbm = cursor.getInt(8);
                final Long localization = cursor.getLong(9);
                final String ssid = cursor.getString(10);

                final WifiDataSample wifiData = new WifiDataSample(bssid,
                        ssid,
                        leveldbm,
                        centerfreq0,
                        centerfreq1,
                        channelWidth,
                        frequency,
                        timestamp,
                        position,
                        0L,
                        localization);

                list.add(wifiData);
                longs.add(rowId);
            }

            if (longs.isEmpty()) {
                //this.signable.databaseDrained();
                return;
            }

            int i;
            StringBuilder values = new StringBuilder();
            for (i = 0; i < longs.size() - 1; i++) {
                values.append(longs.get(i).toString()).append(",");
            }

            values.append(longs.get(i));
            final String indices = values.toString();

            Object[] objects = new Object[0];
            writableDatabase.execSQL("DELETE FROM " + DatabaseService.FINGERPRINT + " WHERE rowid IN (" + indices + ")", objects);
        } catch (SQLException exception) {

            Log.e(TAG, "emitTo: Some error at db", exception);

        } finally {

            if (cursor != null) {
                cursor.close();
            }

            if (writableDatabase != null) {
                writableDatabase.close();
            }
        }

        Log.i(TAG, "emitTo: sink data");
        receiver.receive(list, 0L);
    }


    @SuppressWarnings("TryFinallyCanBeTryWithResources")
    public synchronized boolean isEmpty() {

        long numEntries = 0L;

        SQLiteDatabase readableDatabase = null;

        try {
            readableDatabase = databaseService.getReadableDatabase();
            numEntries = DatabaseUtils.queryNumEntries(readableDatabase, DatabaseService.FINGERPRINT);
        } catch (SQLException exception) {
            Log.e(TAG, "isEmpty: Not able to open db connection", exception);
        } finally {
            if (readableDatabase != null) {
                readableDatabase.close();
            }
        }

        return numEntries == 0;
    }
}
