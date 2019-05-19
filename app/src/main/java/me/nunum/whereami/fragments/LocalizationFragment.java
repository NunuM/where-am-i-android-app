package me.nunum.whereami.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.GridLayoutManager;
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


    private static final String ARG_COLUMN_COUNT = "column-count";

    private int mColumnCount = 1;
    private OnListFragmentInteractionListener mListener;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public LocalizationFragment() {
    }

    @SuppressWarnings("unused")
    public static LocalizationFragment newInstance(int columnCount) {
        LocalizationFragment fragment = new LocalizationFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_COLUMN_COUNT, columnCount);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mColumnCount = getArguments().getInt(ARG_COLUMN_COUNT);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        final HttpService service = (HttpService) mListener.getService(Services.HTTP);

        LinearLayoutManager layoutManager = null;

        View hostView = inflater.inflate(R.layout.fragment_localization_list, container, false);

        final SwipeRefreshLayout swipeRefreshLayout = (SwipeRefreshLayout) hostView.findViewById(R.id.fll_localization_swipe);

        View view = hostView.findViewById(R.id.fll_localization_list);

        // Set the adapter
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

            final LocalizationRecyclerViewAdapter adapter = new LocalizationRecyclerViewAdapter(mListener);
            recyclerView.setAdapter(adapter);

            DividerItemDecoration dividerItemDecorator = new DividerItemDecoration(
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
                            Toast.makeText(getContext(), R.string.fll_localization_list_request_failure, Toast.LENGTH_SHORT).show();

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

        FloatingActionButton floatingActionButton = (FloatingActionButton) hostView.findViewById(R.id.fll_localization_add_position_bottom);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.openNewLocalizationFragment();
                Log.i("s", "onClick: one more");
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
        mListener = null;
    }

    public static Fragment newInstance() {
        return new LocalizationFragment();
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnListFragmentInteractionListener {

        void onLocalizationSelected(Localization item);

        Context context();

        Object getService(Services service);

        void setActionBarTitle(final String barTitle);

        void openNewLocalizationFragment();

        void openTrainingStatus(Localization mItem);
    }
}
