package me.nunum.whereami.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
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

    private static final String LOCALIZATION_PARAM = "localization";

    private Localization localization;

    private OnFragmentInteractionListener mListener;

    private int mColumnCount = 1;

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
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View hostView = inflater.inflate(R.layout.fragment_prediction_dashboard, container, false);

        final ToggleButton toggle = (ToggleButton) hostView.findViewById(R.id.fpda_toggle_btn);

        final PredictionDashboardRecyclerViewAdapter adapter = new PredictionDashboardRecyclerViewAdapter(mListener);

        toggle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                toggle.setClickable(false);
                if (isChecked) {
                    future = startRequestPredictions(adapter);
                    toggle.setClickable(true);
                } else {
                    if (future != null) {
                        future.cancel(true);
                    }
                    toggle.setClickable(true);
                }
            }
        });

        LinearLayoutManager layoutManager = null;

        View view = hostView.findViewById(R.id.fpda_predictions);

        if (view instanceof RecyclerView) {
            Context context = view.getContext();
            RecyclerView recyclerView = (RecyclerView) view;
            if (mColumnCount <= 1) {
                layoutManager = new LinearLayoutManager(context);
                recyclerView.setLayoutManager(layoutManager);
            } else {
                layoutManager = new GridLayoutManager(context, mColumnCount);
                recyclerView.setLayoutManager(layoutManager);
            }

            recyclerView.setAdapter(adapter);

            final DividerItemDecoration dividerItemDecorator = new DividerItemDecoration(
                    recyclerView.getContext(),
                    DividerItemDecoration.VERTICAL
            );
            recyclerView.addItemDecoration(dividerItemDecorator);
        }

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

        ApplicationPreferences preferences = ApplicationPreferences.instance(mListener.context());

        final Integer delay = preferences.getIntegerKey(ApplicationPreferences.KEYS.PRODUCER_DELAY);
        final Integer period = preferences.getIntegerKey(ApplicationPreferences.KEYS.PRODUCER_PERIOD);

        final ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();

        return executorService.scheduleAtFixedRate(new WifiService(mListener.context(),
                        new Position(),
                        localization,
                        new Receiver<List<WifiDataSample>>() {
                            @Override
                            public void receive(List<WifiDataSample> wifiDataSamples, Long batchNumber) {
                                final HttpService httpService = (HttpService) mListener.getService(Services.HTTP);

                                httpService.requestNewPrediction(localization,
                                        new NewPredictionRequest(new Date(), false, wifiDataSamples),
                                        new OnResponse<List<Prediction>>() {
                                            @Override
                                            public void onSuccess(List<Prediction> o) {
                                                adapter.addAll(o);
                                            }

                                            @Override
                                            public void onFailure(Throwable throwable) {
                                                Log.e("RRRR", "onFailure: ___________________", throwable);
                                            }
                                        });
                            }
                        })
                , delay, period, TimeUnit.SECONDS);
    }

    private void stopRequestPredictions() {

    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }


    public interface OnFragmentInteractionListener {
        Object getService(Services service);

        Context context();
    }
}
