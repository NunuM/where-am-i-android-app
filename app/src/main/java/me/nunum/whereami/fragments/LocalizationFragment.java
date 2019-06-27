package me.nunum.whereami.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
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
import me.nunum.whereami.adapters.LocalizationRecyclerViewAdapter;
import me.nunum.whereami.framework.EndlessRecyclerOnScrollListener;
import me.nunum.whereami.framework.OnResponse;
import me.nunum.whereami.model.Localization;
import me.nunum.whereami.service.HttpService;
import me.nunum.whereami.service.Services;
import me.nunum.whereami.utils.AppConfig;

/**
 * A fragment representing a list of Items.
 * <p/>
 * Activities containing this fragment MUST implement the {@link OnListFragmentInteractionListener}
 * interface.
 */
public class LocalizationFragment extends Fragment {

    private static final String TAG = LocalizationFragment.class.getSimpleName();

    private OnListFragmentInteractionListener mListener;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public LocalizationFragment() {
    }

    @SuppressWarnings("unused")
    public static LocalizationFragment newInstance() {
        return new LocalizationFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i(TAG, "onCreate: Open fragment");
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        final HttpService service = (HttpService) mListener.getService(Services.HTTP);

        final View hostView = inflater.inflate(R.layout.fragment_localization_list, container, false);

        final SwipeRefreshLayout swipeRefreshLayout = hostView.findViewById(R.id.fll_localization_swipe);

        final View view = hostView.findViewById(R.id.fll_localization_list);

        // Set the adapter
        if (view instanceof RecyclerView) {

            final Context context = view.getContext();
            final RecyclerView recyclerView = (RecyclerView) view;

            final LinearLayoutManager layoutManager = new LinearLayoutManager(context);
            recyclerView.setLayoutManager(layoutManager);

            final LocalizationRecyclerViewAdapter adapter = new LocalizationRecyclerViewAdapter(mListener);
            recyclerView.setAdapter(adapter);

            final DividerItemDecoration dividerItemDecorator = new DividerItemDecoration(
                    recyclerView.getContext(),
                    DividerItemDecoration.VERTICAL
            );

            recyclerView.addItemDecoration(dividerItemDecorator);

            final EndlessRecyclerOnScrollListener scrollListener = new EndlessRecyclerOnScrollListener(layoutManager) {
                @Override
                public void onLoadMore(int currentPage) {

                    service.paginateLocalizations(currentPage, new OnResponse<List<Localization>>() {
                        @Override
                        public void onSuccess(List<Localization> o) {

                            if (o.size() < visibleThreshold()) {
                                setEnabled(false);
                            } else {
                                setEnabled(true);
                            }

                            if (swipeRefreshLayout.isRefreshing()) {
                                swipeRefreshLayout.setRefreshing(false);
                            }

                            adapter.addAll(o);
                        }

                        @Override
                        public void onFailure(Throwable throwable) {

                            Log.e(TAG, "onFailure: Could not retrieve positions from the server", throwable);

                            Toast.makeText(mListener.context(), R.string.fll_localization_list_request_failure, Toast.LENGTH_SHORT).show();

                            if (swipeRefreshLayout.isRefreshing()) {
                                swipeRefreshLayout.setRefreshing(false);
                            }

                        }
                    });
                }
            };

            recyclerView.addOnScrollListener(scrollListener);

            scrollListener.onLoadMore(AppConfig.FIRST_PAGE);

            swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    scrollListener.onLoadMore(AppConfig.FIRST_PAGE);
                }
            });
        }

        swipeRefreshLayout.setRefreshing(true);

        final FloatingActionButton floatingActionButton = hostView.findViewById(R.id.fll_localization_add_position_bottom);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.openNewLocalizationFragment();
            }
        });

        mListener.setActionBarTitle(getString(R.string.fll_localization_actionbar_title));

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
        Log.i(TAG, "onDetach: Closed fragment");
    }


    public interface OnListFragmentInteractionListener {

        void onLocalizationSelected(Localization item);

        Context context();

        Object getService(Services service);

        void setActionBarTitle(final String barTitle);

        void openNewLocalizationFragment();

        void openTrainingStatus(Localization mItem);
    }
}
