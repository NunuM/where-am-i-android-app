package me.nunum.whereami.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.util.List;

import me.nunum.whereami.R;
import me.nunum.whereami.adapters.TrainingRecyclerViewAdapter;
import me.nunum.whereami.framework.OnResponse;
import me.nunum.whereami.model.Localization;
import me.nunum.whereami.model.TrainingProgress;
import me.nunum.whereami.service.HttpService;
import me.nunum.whereami.service.Services;
import me.nunum.whereami.utils.StringUtils;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link TrainingStatusFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link TrainingStatusFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class TrainingStatusFragment extends Fragment {

    private static final String TAG = TrainingStatusFragment.class.getSimpleName();

    private OnFragmentInteractionListener mListener;

    public TrainingStatusFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment TrainingStatusFragment.
     */
    public static TrainingStatusFragment newInstance() {
        return new TrainingStatusFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i(TAG, "onCreate: Open fragment");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        final View hostView = inflater.inflate(R.layout.fragment_training_status, container, false);

        final HttpService service = (HttpService) mListener.getService(Services.HTTP);

        final SwipeRefreshLayout swipeRefreshLayout = (SwipeRefreshLayout) hostView.findViewById(R.id.fts_training_swipe);

        final Localization localization = mListener.associatedLocalization();

        final View view = hostView.findViewById(R.id.fts_training_list);

        if (view instanceof RecyclerView) {
            final Context context = view.getContext();
            final RecyclerView recyclerView = (RecyclerView) view;

            final LinearLayoutManager layoutManager = new LinearLayoutManager(context);
            recyclerView.setLayoutManager(layoutManager);


            final TrainingRecyclerViewAdapter viewAdapter = new TrainingRecyclerViewAdapter(mListener, localization.isOwner());

            final OnResponse<List<TrainingProgress>> onResponse = new OnResponse<List<TrainingProgress>>() {
                @Override
                public void onSuccess(List<TrainingProgress> o) {
                    viewAdapter.addAll(o);

                    if (swipeRefreshLayout.isRefreshing()) {
                        swipeRefreshLayout.setRefreshing(false);
                    }
                }

                @Override
                public void onFailure(Throwable throwable) {

                    Log.e(TAG, "onFailure: Error retrieving training status", throwable);

                    if (swipeRefreshLayout.isRefreshing()) {
                        swipeRefreshLayout.setRefreshing(false);
                    }

                    Toast.makeText(mListener.context(), R.string.ftli_training_request_failure, Toast.LENGTH_SHORT).show();
                }
            };

            service.getLocalizationTrainingInformation(localization, onResponse);

            recyclerView.setAdapter(viewAdapter);

            final DividerItemDecoration dividerItemDecorator = new DividerItemDecoration(
                    recyclerView.getContext(),
                    DividerItemDecoration.VERTICAL
            );

            recyclerView.addItemDecoration(dividerItemDecorator);

            swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    service.getLocalizationTrainingInformation(localization, onResponse);
                }
            });
        }

        swipeRefreshLayout.setRefreshing(true);

        final FloatingActionButton floatingActionButton = (FloatingActionButton) hostView.findViewById(R.id.fts_training_add_position_bottom);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mListener.openNewTrainingFragment();
            }
        });

        mListener.setActionBarTitle(mListener.context().getString(R.string.title_training_status, StringUtils.capitalize(localization.getLabel())));

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

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
        Log.i(TAG, "onDetach: Close fragment");
    }

    public interface OnFragmentInteractionListener {

        Context context();

        Object getService(Services service);

        Localization associatedLocalization();

        void setActionBarTitle(final String barTitle);

        void openNewTrainingFragment();
    }
}
