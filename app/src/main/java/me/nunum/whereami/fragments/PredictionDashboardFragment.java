package me.nunum.whereami.fragments;

import android.animation.ValueAnimator;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ProgressBar;
import android.widget.ToggleButton;

import java.util.Date;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import me.nunum.whereami.R;
import me.nunum.whereami.adapters.PredictionDashboardRecyclerViewAdapter;
import me.nunum.whereami.framework.OnResponse;
import me.nunum.whereami.framework.Receiver;
import me.nunum.whereami.model.Localization;
import me.nunum.whereami.model.Position;
import me.nunum.whereami.model.Prediction;
import me.nunum.whereami.model.WifiDataSample;
import me.nunum.whereami.model.request.NewPredictionRequest;
import me.nunum.whereami.service.HttpService;
import me.nunum.whereami.service.Services;
import me.nunum.whereami.service.WifiService;
import me.nunum.whereami.service.application.ApplicationPreferences;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link PredictionDashboardFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link PredictionDashboardFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PredictionDashboardFragment extends Fragment {

    private static final String TAG = PredictionDashboardFragment.class.getSimpleName();

    private static final String LOCALIZATION_PARAM = "localization";
    private final ValueAnimator valueAnimator = new ValueAnimator();
    private Localization localization;
    private OnFragmentInteractionListener mListener;
    private Date lastUpdate;
    private ProgressBar progressBar;
    private ScheduledFuture<?> future;

    public PredictionDashboardFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment PredictionDashboardFragment.
     */
    public static PredictionDashboardFragment newInstance(Localization localization) {
        PredictionDashboardFragment fragment = new PredictionDashboardFragment();
        Bundle args = new Bundle();
        args.putSerializable(LOCALIZATION_PARAM, localization);
        fragment.setArguments(args);
        fragment.localization = localization;
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            localization = (Localization) getArguments().getSerializable(LOCALIZATION_PARAM);
        }
        Log.i(TAG, "onCreate: Open fragment");
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {

        final HttpService httpService = (HttpService) mListener.getService(Services.HTTP);

        // Inflate the layout for this fragment
        final View hostView = inflater.inflate(R.layout.fragment_prediction_dashboard, container, false);

        final ToggleButton toggle = hostView.findViewById(R.id.fpda_toggle_btn);

        final Button requestAllPredictions = hostView.findViewById(R.id.fpda_all_predictions);

        progressBar = hostView.findViewById(R.id.fpda_prediction_progress_bar);
        progressBar.setVisibility(View.INVISIBLE);

        final PredictionDashboardRecyclerViewAdapter adapter = new PredictionDashboardRecyclerViewAdapter(mListener);

        toggle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                toggle.setClickable(false);
                if (isChecked) {
                    future = startRequestPredictions(adapter);
                    if (future != null) {
                        mListener.setScreenAlwaysOnFlag();
                        startProgressBar();
                    } else {
                        toggle.setChecked(false);
                    }

                    toggle.setClickable(true);
                } else {
                    if (future != null) {
                        future.cancel(true);
                    }
                    toggle.setClickable(true);
                    mListener.clearScreenAlwaysOnFlag();
                    stopProgressBar();
                }
            }
        });

        final View view = hostView.findViewById(R.id.fpda_predictions);

        if (view instanceof RecyclerView) {
            final Context context = view.getContext();
            final RecyclerView recyclerView = (RecyclerView) view;

            final LinearLayoutManager layoutManager = new LinearLayoutManager(context);
            recyclerView.setLayoutManager(layoutManager);


            recyclerView.setAdapter(adapter);

            final DividerItemDecoration dividerItemDecorator = new DividerItemDecoration(
                    recyclerView.getContext(),
                    DividerItemDecoration.VERTICAL
            );
            recyclerView.addItemDecoration(dividerItemDecorator);
        }


        requestAllPredictions.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                httpService.requestNewPrediction(localization,
                        new NewPredictionRequest(new Date(1081157732L * 1000), true),
                        new OnResponse<List<Prediction>>() {
                            @Override
                            public void onSuccess(List<Prediction> o) {
                                adapter.addAll(o);
                            }

                            @Override
                            public void onFailure(Throwable throwable) {
                                Log.e(TAG, "onFailure: Error retrieving all predictions from the server", throwable);
                            }
                        });
            }
        });

        return hostView;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    private synchronized ScheduledFuture<?> startRequestPredictions(final PredictionDashboardRecyclerViewAdapter adapter) {

        if (mListener.requestWifiPermissions()) {
            return null;
        }

        final ApplicationPreferences preferences = ApplicationPreferences.instance(mListener.context());

        final int delay = preferences.getIntegerKey(ApplicationPreferences.KEYS.PRODUCER_DELAY);
        final int period = preferences.getIntegerKey(ApplicationPreferences.KEYS.PRODUCER_PERIOD);

        if (progressBar != null) {
            progressBar.setMax(period);
        }

        final ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();

        Log.i(TAG, "startRequestPredictions: Starting request predictions with delay of " + delay + " and one period of" + period + " seconds");

        return executorService.scheduleAtFixedRate(new WifiService(mListener.context(),
                        new Position(),
                        localization,
                        new Receiver<List<WifiDataSample>>() {
                            @Override
                            public void receive(List<WifiDataSample> wifiDataSamples, Long batchNumber) {
                                final HttpService httpService = (HttpService) mListener.getService(Services.HTTP);

                                httpService.requestNewPrediction(localization,
                                        new NewPredictionRequest(getLastUpdate(), false, wifiDataSamples),
                                        new OnResponse<List<Prediction>>() {
                                            @Override
                                            public void onSuccess(List<Prediction> o) {
                                                adapter.addAll(o);
                                                resetProgressBar();
                                            }

                                            @Override
                                            public void onFailure(Throwable throwable) {
                                                Log.e(TAG, "onFailure: Error retrieving a new prediction from the server", throwable);
                                            }
                                        });
                            }
                        })
                , delay, period, TimeUnit.SECONDS);
    }

    @Override
    public void onDetach() {
        super.onDetach();

        if (future != null) {
            mListener.clearScreenAlwaysOnFlag();
            future.cancel(true);
            future = null;
        }

        Log.i(TAG, "onDetach: Close fragment");
    }


    private void startProgressBar() {

        if (progressBar != null) {
            progressBar.setVisibility(View.VISIBLE);
            valueAnimator.setObjectValues(0, progressBar.getMax());
            valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    int integer = Integer.valueOf(animation.getAnimatedValue().toString());
                    progressBar.setProgress(integer);
                }
            });

            valueAnimator.setDuration(1000 * progressBar.getMax());
            valueAnimator.start();
        }
    }


    private void resetProgressBar() {
        if (progressBar != null) {
            progressBar.setProgress(0);
        }
        valueAnimator.start();
    }

    private void stopProgressBar() {
        valueAnimator.end();
        progressBar.setVisibility(View.INVISIBLE);
    }


    private Date getLastUpdate() {

        if (lastUpdate == null) {
            lastUpdate = new Date();
        }

        Date clone = new Date(lastUpdate.getTime());

        lastUpdate = new Date();

        return clone;
    }


    public interface OnFragmentInteractionListener {
        Object getService(Services service);

        Context context();

        void setScreenAlwaysOnFlag();

        void clearScreenAlwaysOnFlag();

        boolean requestWifiPermissions();
    }
}
