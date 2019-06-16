package me.nunum.whereami.adapters;

import android.support.annotation.NonNull;
import android.support.v7.util.SortedList;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import me.nunum.whereami.R;
import me.nunum.whereami.fragments.PredictionDashboardFragment;
import me.nunum.whereami.framework.OnResponse;
import me.nunum.whereami.framework.SortedListCallbackImpl;
import me.nunum.whereami.model.Prediction;
import me.nunum.whereami.model.request.PredictionFeedbackRequest;
import me.nunum.whereami.service.HttpService;
import me.nunum.whereami.service.Services;

public class PredictionDashboardRecyclerViewAdapter
        extends RecyclerView.Adapter<PredictionDashboardRecyclerViewAdapter.ViewHolder> {


    private final SortedList<Prediction> mValues;
    private final PredictionDashboardFragment.OnFragmentInteractionListener mListener;

    public PredictionDashboardRecyclerViewAdapter(PredictionDashboardFragment.OnFragmentInteractionListener mListener) {
        this.mListener = mListener;
        this.mValues = new SortedList<>(Prediction.class, new SortedListCallbackImpl<Prediction>(this));
    }

    public void addAll(List<Prediction> predictions) {
        this.mValues.beginBatchedUpdates();
        for (int i = 0; i < predictions.size(); i++) {
            this.mValues.add(predictions.get(i));
        }
        this.mValues.endBatchedUpdates();
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_prediction_dashboard_item, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        final Prediction p = mValues.get(position);

        holder.mPredictedLabel.setText(p.getPredictionLabel());

        holder
                .mRequestId
                .setText(mListener
                        .context()
                        .getString(R.string.fpdi_prediction_label_item, p.getRequestId(), p.getAccuracy()));

        holder.mPositiveFeedback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final HttpService service = (HttpService) mListener.getService(Services.HTTP);

                service.predictionFeedback(p.getLocalizationId(), p.getId(), new PredictionFeedbackRequest(true), new OnResponse<Prediction>() {
                    @Override
                    public void onSuccess(Prediction o) {
                        holder.mPositiveFeedback.setVisibility(View.INVISIBLE);
                        holder.mPositiveFeedback.setClickable(false);
                    }

                    @Override
                    public void onFailure(Throwable throwable) {
                        Toast.makeText(mListener.context(), R.string.fpdi_prediction_feedback_failure, Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

        holder.mNegativeFeedback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final HttpService service = (HttpService) mListener.getService(Services.HTTP);

                service.predictionFeedback(p.getLocalizationId(), p.getId(), new PredictionFeedbackRequest(), new OnResponse<Prediction>() {
                    @Override
                    public void onSuccess(Prediction o) {
                        holder.mNegativeFeedback.setVisibility(View.INVISIBLE);
                        holder.mNegativeFeedback.setClickable(false);
                    }

                    @Override
                    public void onFailure(Throwable throwable) {
                        Toast.makeText(mListener.context(), R.string.fpdi_prediction_feedback_failure, Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });


        switch (p.getPredictionFeedback()) {
            case 1:
                holder.mPositiveFeedback.setClickable(false);
                holder.mNegativeFeedback.setVisibility(View.INVISIBLE);
                break;
            case 2:
                holder.mNegativeFeedback.setClickable(false);
                holder.mPositiveFeedback.setVisibility(View.INVISIBLE);
                break;
        }

    }

    @Override
    public int getItemCount() {
        return this.mValues.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public final View mView;
        public final TextView mRequestId;
        public final TextView mPredictedLabel;
        public final Button mPositiveFeedback;
        public final Button mNegativeFeedback;

        public ViewHolder(View itemView) {
            super(itemView);
            this.mView = itemView;
            this.mRequestId = itemView.findViewById(R.id.fpdi_prediction_request_id_label);
            this.mPredictedLabel = itemView.findViewById(R.id.fpdi_prediction_position_label);
            this.mPositiveFeedback = itemView.findViewById(R.id.fpdi_prediction_positive_feedback);
            this.mNegativeFeedback = itemView.findViewById(R.id.fpdi_prediction_negative_feedback);
        }
    }
}
