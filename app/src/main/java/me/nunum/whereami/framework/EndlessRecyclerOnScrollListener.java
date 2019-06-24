package me.nunum.whereami.framework;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

/**
 * Created by nuno on 03-03-2018.
 */

@SuppressWarnings("FieldCanBeLocal")
public abstract class EndlessRecyclerOnScrollListener extends RecyclerView.OnScrollListener {

    public static final String TAG = EndlessRecyclerOnScrollListener.class.getSimpleName();

    private boolean isEnabled;
    private int previousTotal = 0; // The total number of items in the dataset after the last load
    private boolean loading = true; // True if we are still waiting for the last set of data to load.
    private final int visibleThreshold = 5; // The minimum amount of items to have below your current scroll position before loading more.
    private int firstVisibleItem;
    private int visibleItemCount;
    private int totalItemCount;


    private int current_page = 1;

    private final LinearLayoutManager mLinearLayoutManager;

    public EndlessRecyclerOnScrollListener(LinearLayoutManager linearLayoutManager) {
        this.mLinearLayoutManager = linearLayoutManager;
        this.isEnabled = false;
    }

    public synchronized void setEnabled(boolean enabled) {
        isEnabled = enabled;
    }

    public int visibleThreshold() {
        return visibleThreshold;
    }

    @Override
    public void onScrolled(RecyclerView recyclerView, int dx, int dy) {

        if(!isEnabled){
            return;
        }

        super.onScrolled(recyclerView, dx, dy);

        visibleItemCount = recyclerView.getChildCount();
        totalItemCount = mLinearLayoutManager.getItemCount();
        firstVisibleItem = mLinearLayoutManager.findFirstVisibleItemPosition();

        if (loading) {
            if (totalItemCount > previousTotal) {
                loading = false;
                previousTotal = totalItemCount;
            }
        }
        if (!loading && (totalItemCount - visibleItemCount)
                <= (firstVisibleItem + visibleThreshold)) {

            current_page++;

            onLoadMore(current_page);

            loading = true;
        }
    }

    @SuppressWarnings("WeakerAccess")
    public abstract void onLoadMore(int currentPage);
}
