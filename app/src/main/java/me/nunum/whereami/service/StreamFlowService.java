package me.nunum.whereami.service;

import android.content.Context;
import android.util.Log;

import java.util.List;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

import me.nunum.whereami.framework.Cache;
import me.nunum.whereami.framework.OnCircuitTest;
import me.nunum.whereami.framework.OnSample;
import me.nunum.whereami.framework.OnSync;
import me.nunum.whereami.framework.Receiver;
import me.nunum.whereami.framework.StreamFlow;
import me.nunum.whereami.model.Localization;
import me.nunum.whereami.model.Position;
import me.nunum.whereami.model.WifiDataSample;
import me.nunum.whereami.service.application.ApplicationPreferences;
import me.nunum.whereami.utils.AppConfig;

import static me.nunum.whereami.service.application.ApplicationPreferences.KEYS;

public class StreamFlowService implements
        StreamFlow,
        Receiver<List<WifiDataSample>> {

    private static final String TAG = StreamFlowService.class.getSimpleName();
    private final Context context;
    private final ApplicationPreferences preferences;
    private final ScheduledExecutorService executorService;
    private final AtomicLong batch = new AtomicLong(1);
    private final ConcurrentSkipListMap<Long, List<WifiDataSample>> holder;
    private FLUSH_MODE flushMode;
    private STREAM_STATE streamState;
    private Receiver<List<WifiDataSample>> sinker;
    private ScheduledFuture<?> worker;

    public StreamFlowService(Context context) {
        this.context = context;
        this.flushMode = FLUSH_MODE.NONE;
        this.streamState = STREAM_STATE.STOP;
        this.preferences = ApplicationPreferences.instance(context);
        this.holder = new ConcurrentSkipListMap<>();

        this.executorService = Executors.newSingleThreadScheduledExecutor();
    }

    @Override
    public synchronized boolean start(final Localization localization,
                                      final Position position,
                                      final OnSample onSampleCallback,
                                      Cache cache) {

        if (this.streamState == STREAM_STATE.RUNNING) {
            return false;
        }

        this.streamState = STREAM_STATE.RUNNING;

        final int period = preferences.getIntegerKey(KEYS.SINKER_PERIOD);
        final int delay = preferences.getIntegerKey(KEYS.SINKER_DELAY);

        final String protocol = preferences.getStringKey(KEYS.ONLINE_PROTOCOL);

        switch (protocol.toLowerCase()) {
            case AppConfig.TCP_PROTOCOL:
                break;
            case AppConfig.HTTP_PROTOCOL:

                final HttpService service = (HttpService) cache.get(Services.HTTP);

                service.testConnectivity(new OnCircuitTest() {

                    @Override
                    public void onConnectionSucceeded() {

                        flushMode = FLUSH_MODE.TCP;

                        sinker = service.httpSinker(new OnSync() {
                            @Override
                            public void batchNumber(Long batch, Position p) {
                                final boolean containsKey = holder.containsKey(batch);
                                final int numberOfSamples = holder.remove(batch).size();

                                if (containsKey) {
                                    onSampleCallback.emitted(true, numberOfSamples, p);
                                }

                                Log.i(TAG, "batchNumber: " + batch + ". Send: " + numberOfSamples + " . Size of holder:" + holder.size());
                            }

                            @Override
                            public void failed(Long batch, Throwable throwable) {
                                Log.e(TAG, "failed: Batch number " + batch + " was not transmitted for the serving, using fallback mechanism", throwable);

                                final boolean containsKey = holder.containsKey(batch);
                                final List<WifiDataSample> samples = holder.remove(batch);

                                if (containsKey) {
                                    final PersistenceSinker persistenceSinker = new PersistenceSinker(context, new OnSync() {
                                        @Override
                                        public void batchNumber(Long batch, Position p) {
                                            onSampleCallback.emitted(false, samples.size(), position);
                                        }

                                        @Override
                                        public void failed(Long batch, Throwable throwable) {
                                            Log.e(TAG, "failed: Could not persist samples into database", throwable);
                                        }
                                    });
                                    persistenceSinker.receive(samples, batch);
                                }
                            }
                        });

                        Log.i(TAG, String.format("onConnectionSucceeded: Starting sampling. Delay %d. Period %d", delay, period));
                        worker = executorService
                                .scheduleAtFixedRate(new WifiService(context, position, localization, StreamFlowService.this), delay, period, TimeUnit.SECONDS);

                        onSampleCallback.started();
                    }

                    @Override
                    public void onConnectionFailed() {

                        flushMode = FLUSH_MODE.DATABASE;
                        sinker = new PersistenceSinker(context, new OnSync() {
                            @Override
                            public void batchNumber(Long batch, Position p) {
                                final int numberOfSamples = holder.remove(batch).size();

                                if (holder.containsKey(batch)) {
                                    onSampleCallback.emitted(true, numberOfSamples, p);
                                }

                                Log.i(TAG, "batchNumber: " + batch + ". Send " + numberOfSamples + " . Size of holder:" + holder.size());
                            }

                            @Override
                            public void failed(Long batch, Throwable throwable) {
                                Log.e(TAG, "failed: ");
                            }
                        }, StreamFlowService.this);

                        worker = executorService
                                .scheduleAtFixedRate(new WifiService(context, position, localization, StreamFlowService.this), delay, 5, TimeUnit.SECONDS);

                        onSampleCallback.started();
                    }
                });
                break;

            default:
                break;
        }

        return true;
    }

    /**
     * Request to stop the flow
     */
    @Override
    public boolean stop() {

        if (worker == null || worker.isCancelled() || worker.isDone()) {
            this.streamState = STREAM_STATE.STOP;
            return true;
        }

        final boolean wasCanceled = worker.cancel(true);

        if (wasCanceled) {
            this.streamState = STREAM_STATE.STOP;
        }

        Log.i(TAG, "stop: Stop worker: " + wasCanceled);

        return wasCanceled;
    }


    @Override
    public STREAM_STATE currentState() {
        return this.streamState;
    }

    @Override
    public FLUSH_MODE currentFlushMode() {
        return this.flushMode;
    }

    @Override
    public void receive(List<WifiDataSample> scanResults, Long batch) {

        final long current = this.batch.getAndIncrement();

        this.holder.put(current, scanResults);

        sinker.receive(scanResults, current);

    }
}
