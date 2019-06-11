package me.nunum.whereami.adapters;

import android.support.annotation.NonNull;
import android.support.v7.util.SortedList;
import android.support.v7.widget.RecyclerView;
import android.util.LongSparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import me.nunum.whereami.R;
import me.nunum.whereami.fragments.PredictionDashboardFragment;
import me.nunum.whereami.framework.SortedListCallbackImpl;
import me.nunum.whereami.model.Prediction;

public class PredictionDashboardRecyclerViewAdapter
        extends RecyclerView.Adapter<PredictionDashboardRecyclerViewAdapter.ViewHolder> {


    private final LongSparseArray<SortedList<Prediction>> predictionMap;
    private final PredictionDashboardFragment.OnFragmentInteractionListener mListener;

    public PredictionDashboardRecyclerViewAdapter(PredictionDashboardFragment.OnFragmentInteractionListener mListener) {
        this.mListener = mListener;
        this.predictionMap = new LongSparseArray<>();
    }

    public void addAll(List<Prediction> predictions) {

        Collections.sort(predictions, new Comparator<Prediction>() {
            @Override
            public int compare(Prediction o1, Prediction o2) {
                return o1.getRequestId().compareTo(o2.getRequestId());
            }
        });

        for (int i = 0; i < predictions.size(); i++) {

            Prediction prediction = predictions.get(i);

            SortedList<Prediction> sortedList = this.predictionMap.get(prediction.getRequestId());

            if (sortedList == null) {
                sortedList = new SortedList<>(Prediction.class, new SortedListCallbackImpl<Prediction>(this));
                this.predictionMap.put(prediction.getRequestId(), sortedList);
            }

            sortedList.add(prediction);
        }

    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_prediction_dashboard_item, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        SortedList<Prediction> list = this.predictionMap.valueAt(position);

        if (list == null) {
            this.predictionMap.setValueAt(position, new SortedList<>(Prediction.class, new SortedListCallbackImpl<Prediction>(this)));
            return;
        }

        Prediction prediction = list.get(0);

        if (prediction == null) {
            return;
        }

        holder.mRequestId.setText("Batch " + prediction.getRequestId());

        final ArrayAdapter<CharSequence> adapter = new ArrayAdapter<CharSequence>(mListener.context(), android.R.layout.simple_list_item_1);

        for (int i = 0; i < list.size(); i++) {
            final Prediction p = list.get(i);
            String formattedPredictionString = mListener.context().getString(R.string.fpdi_prediction_label_item, p.getPredictionLabel(), p.getAccuracy());
            adapter.add(formattedPredictionString);
        }

        holder.mPredictionListView.setAdapter(adapter);
    }

    @Override
    public int getItemCount() {
        return this.predictionMap.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public final View mView;
        public final TextView mRequestId;
        public final ListView mPredictionListView;

        public ViewHolder(View itemView) {
            super(itemView);
            this.mView = itemView;
            this.mRequestId = itemView.findViewById(R.id.fpdi_prediction_request_id_label);
            this.mPredictionListView = itemView.findViewById(R.id.fpdi_prediction_dashboard_list);
        }
    }
}
