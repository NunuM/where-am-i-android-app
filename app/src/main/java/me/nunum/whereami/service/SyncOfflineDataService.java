package me.nunum.whereami.service;

import android.app.IntentService;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.util.Log;

import com.google.gson.Gson;

import me.nunum.whereami.framework.OnSync;
import me.nunum.whereami.model.Position;

public class SyncOfflineDataService extends IntentService implements OnSync {

    private static final String TAG = SyncOfflineDataService.class.getSimpleName();

    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     */
    public SyncOfflineDataService() {
        super("SyncOfflineDataService");
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {

        try {
            final HttpService service = HttpService.create(getApplicationContext(), new Gson());

            PersistenceSinker sinker = new PersistenceSinker(getApplicationContext(), this, service.httpSinker(this));

            while (!sinker.isEmpty()) {
                sinker.run();
            }
        } catch (Throwable e) {
            Log.e(TAG, "onHandleIntent: Service error", e);
        } finally {
            stopSelf();
        }
    }

    @Override
    public void batchNumber(Long batch, Position position) {
        Log.i(TAG, "batchNumber: Synced new batch of stored data");
    }

    @Override
    public void failed(Long batch, Throwable throwable) {
        Log.e(TAG, "failed: Could not sync data", throwable);
        stopSelf();
    }
}
