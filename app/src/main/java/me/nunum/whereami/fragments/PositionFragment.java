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
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import me.nunum.whereami.R;
import me.nunum.whereami.adapters.PositionRecyclerViewAdapter;
import me.nunum.whereami.framework.EndlessRecyclerOnScrollListener;
import me.nunum.whereami.framework.OnListSizeChange;
import me.nunum.whereami.framework.OnResponse;
import me.nunum.whereami.model.Localization;
import me.nunum.whereami.model.Position;
import me.nunum.whereami.service.HttpService;
import me.nunum.whereami.service.Services;
import me.nunum.whereami.utils.AppConfig;
import me.nunum.whereami.utils.StringUtils;

/**
 * A fragment representing a list of Items.
 * <p/>
 * Activities containing this fragment MUST implement the {@link OnListFragmentInteractionListener}
 * interface.
 */
public class PositionFragment extends Fragment {

    private static final String TAG = PositionFragment.class.getSimpleName();

    private OnListFragmentInteractionListener mListener;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public PositionFragment() {
    }


    public static PositionFragment newInstance() {
        return new PositionFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i(TAG, "onCreate: Open fragment");
    }

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {

        final HttpService service = (HttpService) mListener.getService(Services.HTTP);

        final View hostView = inflater.inflate(R.layout.fragment_position_list, container, false);

        final View view = hostView.findViewById(R.id.fpl_position_list);

        final SwipeRefreshLayout swipeRefreshLayout = (SwipeRefreshLayout) hostView.findViewById(R.id.fpl_position_swipe);

        final Localization localization = mListener.associatedLocalization();

        // Set the adapter
        if (view instanceof RecyclerView) {
            final Context context = view.getContext();
            final RecyclerView recyclerView = (RecyclerView) view;

            final LinearLayoutManager layoutManager = new LinearLayoutManager(context);
            recyclerView.setLayoutManager(layoutManager);

            final PositionRecyclerViewAdapter viewAdapter = new PositionRecyclerViewAdapter(mListener);

            final TextView positions = (TextView) hostView.findViewById(R.id.fpl_localization_stats_positions);
            positions.setText(localization.getStats().getPositions().toString());

            viewAdapter.setSizeListener(new OnListSizeChange() {
                @Override
                public void currentSize(int size) {
                    positions.setText(String.format("%d", size));
                }
            });

            recyclerView.setAdapter(viewAdapter);

            final DividerItemDecoration dividerItemDecorator = new DividerItemDecoration(
                    recyclerView.getContext(),
                    DividerItemDecoration.VERTICAL
            );

            recyclerView.addItemDecoration(dividerItemDecorator);

            final EndlessRecyclerOnScrollListener scrollListener = new EndlessRecyclerOnScrollListener(layoutManager) {
                @Override
                public void onLoadMore(int currentPage) {
                    service.allPositions(localization, new OnResponse<List<Position>>() {
                        @Override
                        public void onSuccess(List<Position> o) {

                            viewAdapter.addAll(o);

                            if (swipeRefreshLayout.isRefreshing()) {
                                swipeRefreshLayout.setRefreshing(false);
                            }
                        }

                        @Override
                        public void onFailure(Throwable throwable) {

                            Log.e(PositionFragment.TAG, "onFailure: Error retrieving positions from the server", throwable);

                            Toast.makeText(mListener.context(), R.string.fpi_position_list_request_failure, Toast.LENGTH_LONG).show();

                            if (swipeRefreshLayout.isRefreshing()) {
                                swipeRefreshLayout.setRefreshing(false);
                            }
                        }
                    });
                }
            };

            recyclerView.addOnScrollListener(scrollListener);

            scrollListener.setEnabled(false);

            scrollListener.onLoadMore(AppConfig.FIRST_PAGE);

            swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    scrollListener.onLoadMore(AppConfig.FIRST_PAGE);
                }
            });
        }

        swipeRefreshLayout.setRefreshing(true);

        final FloatingActionButton floatingActionButton = (FloatingActionButton) hostView.findViewById(R.id.fpl_position_add_position_bottom);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.openNewPositionFragment();
            }
        });

        if (!localization.isOwner()) {
            floatingActionButton.hide();
        }

        final TextView samples = (TextView) hostView.findViewById(R.id.fpl_localization_stats_samples);
        final TextView nModels = (TextView) hostView.findViewById(R.id.fpl_localization_stats_models);

        samples.setText(localization.getStats().getSamples().toString());
        nModels.setText(localization.getStats().getNumberOfTrainedModels().toString());

        mListener.setActionBarTitle(StringUtils.capitalize(localization.getLabel()));

        return hostView;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnListFragmentInteractionListener) {
            mListener = (OnListFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnListFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
        Log.i(TAG, "onDetach: Close fragment");
    }

    public interface OnListFragmentInteractionListener {

        void onPositionSelected(Position item);

        Context context();

        Localization associatedLocalization();

        Object getService(Services service);

        void setActionBarTitle(final String barTitle);

        public void openNewPositionFragment();
    }
}
