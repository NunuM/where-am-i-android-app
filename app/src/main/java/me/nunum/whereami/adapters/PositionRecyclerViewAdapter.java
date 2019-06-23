package me.nunum.whereami.adapters;

import android.support.v7.util.SortedList;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import me.nunum.whereami.R;
import me.nunum.whereami.fragments.PositionFragment.OnListFragmentInteractionListener;
import me.nunum.whereami.framework.OnListSizeChange;
import me.nunum.whereami.framework.OnResponse;
import me.nunum.whereami.framework.SortedListCallbackImpl;
import me.nunum.whereami.model.Position;
import me.nunum.whereami.service.HttpService;
import me.nunum.whereami.service.Services;

/**
 * {@link RecyclerView.Adapter} that can display a {@link Position} and makes a call to the
 * specified {@link OnListFragmentInteractionListener}.
 */
public class PositionRecyclerViewAdapter extends RecyclerView.Adapter<PositionRecyclerViewAdapter.ViewHolder> {

    private static final String TAG = PositionRecyclerViewAdapter.class.getSimpleName();

    private final SortedList<Position> mValues;
    private final OnListFragmentInteractionListener mListener;
    private OnListSizeChange mSizeListener;

    public PositionRecyclerViewAdapter(OnListFragmentInteractionListener listener) {
        mValues = new SortedList<>(Position.class, new SortedListCallbackImpl<Position>(this));
        mListener = listener;
    }

    public void setSizeListener(OnListSizeChange mSizeListener) {
        this.mSizeListener = mSizeListener;
    }

    public void add(Position position) {
        this.mValues.add(position);
    }

    public void addAll(List<Position> positions) {
        this.mValues.beginBatchedUpdates();
        for (int i = 0; i < positions.size(); i++) {
            this.mValues.add(positions.get(i));
        }
        this.mValues.endBatchedUpdates();

        if (mSizeListener != null) {
            mSizeListener.currentSize(mValues.size());
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_position_item, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        holder.mItem = mValues.get(position);
        holder.mIdPositionLabel.setText(mValues.get(position).getLabel());

        holder.mIdPositionOptionsMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu popupMenu = new PopupMenu(mListener.context(), holder.mIdPositionOptionsMenu);

                popupMenu.inflate(R.menu.fpi_position_options_menu);

                if (!mListener.associatedLocalization().isOwner()) {
                    popupMenu.getMenu().findItem(R.id.fpi_position_options_delete).setVisible(false);
                }

                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {

                    final HttpService service = (HttpService) mListener.getService(Services.HTTP);

                    @Override
                    public boolean onMenuItemClick(MenuItem item) {

                        switch (item.getItemId()) {
                            case R.id.fpi_position_options_delete:

                                service.deletePosition(mListener.associatedLocalization(), holder.mItem, new OnResponse<Void>() {
                                    @Override
                                    public void onSuccess(Void o) {
                                        mValues.remove(holder.mItem);

                                        if (mSizeListener != null)
                                            mSizeListener.currentSize(mValues.size());
                                    }

                                    @Override
                                    public void onFailure(Throwable throwable) {

                                        Log.e(TAG, "onFailure: Error deleting position " + holder.mItem.getId(), throwable);

                                        Toast.makeText(mListener.context(), R.string.fli_localization_delete_request_failure, Toast.LENGTH_SHORT).show();
                                    }
                                });

                                break;
                            case R.id.fpi_position_options_spam:

                                service.newPositionSpam(mListener.associatedLocalization().getId(), holder.mItem.createSpamRequest(), new OnResponse<Void>() {
                                    @Override
                                    public void onSuccess(Void o) {
                                        Toast.makeText(mListener.context(), R.string.fli_localization_spam_request_success, Toast.LENGTH_SHORT).show();
                                    }

                                    @Override
                                    public void onFailure(Throwable throwable) {

                                        Log.e(TAG, "onFailure: Error submititng spam report for the position " + holder.mItem.getId(), throwable);

                                        Toast.makeText(mListener.context(), R.string.fli_localization_spam_request_failure, Toast.LENGTH_SHORT).show();
                                    }
                                });

                                break;
                        }

                        return false;
                    }
                });

                popupMenu.show();
            }
        });

        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mListener) {
                    mListener.onPositionSelected(holder.mItem);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final TextView mIdPositionLabel;
        public final Button mIdPositionOptionsMenu;
        public Position mItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mIdPositionLabel = (TextView) view.findViewById(R.id.fpi_position_label);
            mIdPositionOptionsMenu = (Button) view.findViewById(R.id.fpi_position_options);
        }
    }
}
