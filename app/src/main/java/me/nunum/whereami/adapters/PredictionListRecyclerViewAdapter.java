package me.nunum.whereami.adapters;

import android.support.v7.util.SortedList;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import me.nunum.whereami.R;
import me.nunum.whereami.fragments.PredictionFragment.OnListFragmentInteractionListener;
import me.nunum.whereami.framework.SortedListCallbackImpl;
import me.nunum.whereami.model.Localization;


public class PredictionListRecyclerViewAdapter
        extends RecyclerView.Adapter<PredictionListRecyclerViewAdapter.ViewHolder> {

    private final SortedList<Localization> mValues;
    private final OnListFragmentInteractionListener mListener;

    public PredictionListRecyclerViewAdapter(OnListFragmentInteractionListener listener) {
        mValues = new SortedList<>(Localization.class, new SortedListCallbackImpl<Localization>(this));
        mListener = listener;
    }

    public void addAll(List<Localization> localizations) {
        this.mValues.beginBatchedUpdates();
        for (int i = 0; i < localizations.size(); i++) {
            this.mValues.add(localizations.get(i));
        }
        this.mValues.endBatchedUpdates();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_prediction_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.mItem = mValues.get(position);
        holder.mIdLocalizationLabel.setText(mValues.get(position).getLabel());
        holder.mIdLocalizationUsername.setText(mValues.get(position).getUser());


        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.openPredictionDashboardFor(holder.mItem);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final TextView mIdLocalizationLabel;
        public final TextView mIdLocalizationUsername;
        public Localization mItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mIdLocalizationLabel = (TextView) view.findViewById(R.id.fpi_localization_label);
            mIdLocalizationUsername = (TextView) view.findViewById(R.id.fpi_localization_username);
        }
    }
}
