package me.nunum.whereami.fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.util.List;

import me.nunum.whereami.R;
import me.nunum.whereami.adapters.PostRecyclerViewAdapter;
import me.nunum.whereami.framework.EndlessRecyclerOnScrollListener;
import me.nunum.whereami.framework.OnResponse;
import me.nunum.whereami.model.Post;
import me.nunum.whereami.service.HttpService;
import me.nunum.whereami.service.Services;
import me.nunum.whereami.utils.AppConfig;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link HomeFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link HomeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HomeFragment extends Fragment {

    private static final String TAG = HomeFragment.class.getSimpleName();

    private OnFragmentInteractionListener mListener;

    public HomeFragment() {
        // Required empty public constructor
    }


    public static HomeFragment newInstance() {
        return new HomeFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i(TAG, "onCreate: Open fragment");
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        final View hostView = inflater.inflate(R.layout.fragment_home, container, false);

        final HttpService service = (HttpService) mListener.getService(Services.HTTP);

        final SwipeRefreshLayout swipeRefreshLayout = hostView.findViewById(R.id.fh_post_swipe);

        final View view = hostView.findViewById(R.id.fh_post_list);

        // Set the adapter
        if (view instanceof RecyclerView) {

            final Context context = view.getContext();
            final RecyclerView recyclerView = (RecyclerView) view;

            final LinearLayoutManager layoutManager = new LinearLayoutManager(context);
            recyclerView.setLayoutManager(layoutManager);

            final PostRecyclerViewAdapter adapter = new PostRecyclerViewAdapter(this.mListener);
            recyclerView.setAdapter(adapter);

            final EndlessRecyclerOnScrollListener scrollListener = new EndlessRecyclerOnScrollListener(layoutManager) {

                @Override
                public void onLoadMore(int currentPage) {

                    service.paginatePosts(currentPage, new OnResponse<List<Post>>() {
                        @Override
                        public void onSuccess(List<Post> o) {

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

                            Log.e(TAG, "onFailure: Could not retrieve posts from the server", throwable);

                            swipeRefreshLayout.setRefreshing(false);
                            Toast.makeText(mListener.context(), R.string.fh_posts_request_failure, Toast.LENGTH_LONG).show();
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

        mListener.setActionBarTitle(getString(R.string.fh_bar_title));

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
        Log.i(TAG, "onDetach: Closed fragment");
    }

    public interface OnFragmentInteractionListener {
        Object getService(Services service);

        void setActionBarTitle(String barTitle);

        Context context();

        void launchBrowserIntent(Intent intent);
    }
}
