package me.nunum.whereami.fragments;

import android.animation.TypeEvaluator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.ToggleButton;

import me.nunum.whereami.R;
import me.nunum.whereami.framework.OnSample;
import me.nunum.whereami.model.Localization;
import me.nunum.whereami.model.Position;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link PositionDetailsFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link PositionDetailsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PositionDetailsFragment extends Fragment {

    private static final String TAG = PositionDetailsFragment.class.getSimpleName();

    private OnFragmentInteractionListener mListener;

    private ToggleButton toggleButton;

    public PositionDetailsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment PositionDetailsFragment.
     */
    public static PositionDetailsFragment newInstance() {
        return new PositionDetailsFragment();
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

        final View view = inflater.inflate(R.layout.fragment_position_details, container, false);

        final Position position = this.mListener.associatedPosition();

        final TextView headerLabel = (TextView) view.findViewById(R.id.fpd_position_header_label);
        final TextView onlineSamples = view.findViewById(R.id.fpd_position_number_of_online_samples);
        final TextView offlineSamples = view.findViewById(R.id.fpd_position_number_of_offline_samples);
        final TextView numberOfRouters = view.findViewById(R.id.fpd_position_number_of_routers);
        final TextView numberOfNetworks = view.findViewById(R.id.fpd_position_number_of_network_names);
        final TextView topRouter = view.findViewById(R.id.fpd_position_top_router);

        headerLabel.setText(position.getLabel());
        onlineSamples.setText(position.getStats().getSamples().toString());
        offlineSamples.setText(mListener.numberOfOfflineSamples().toString());

        numberOfRouters.setText(position.getStats().getRouters().toString());
        numberOfNetworks.setText(position.getStats().getNetworks().toString());
        topRouter.setText(position.getStats().getStrongestSignal());

        toggleButton = (ToggleButton) view.findViewById(R.id.fpd_position_samples_toggle);
        toggleButton.setSaveEnabled(false);

        toggleButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if (isChecked) {
                    Log.i(TAG, "onCheckedChanged: User requested to start sampling");

                    final boolean startSampling = mListener.startSampling(new OnSample() {
                        @Override
                        public void emitted(boolean wasToOnline, int samples, Position p) {
                            Log.i(TAG, "emitted: Completed one sample cycle");
                            try {
                                if (wasToOnline) {

                                    final int start = Integer.valueOf(onlineSamples.getText().toString());
                                    final int end = samples + start;

                                    animateNumber(onlineSamples, start, end);

                                    if (p != null) {
                                        numberOfRouters.setText(p.getStats().getRouters().toString());
                                        numberOfNetworks.setText(p.getStats().getNetworks().toString());
                                        topRouter.setText(p.getStats().getStrongestSignal());

                                        if (mListener != null && mListener.associatedLocalization() != null)
                                            mListener.associatedLocalization().getStats().incrementSamples();
                                    }

                                } else {

                                    final int start = Integer.valueOf(offlineSamples.getText().toString());
                                    final int end = samples + start;

                                    animateNumber(offlineSamples, start, end);
                                }
                            } catch (NumberFormatException exception) {
                                Log.e(TAG, "emitted: Could not update counters due the following exception", exception);
                            }
                        }
                    });

                    if (!startSampling) {
                        buttonView.setChecked(false);
                    }

                } else {
                    mListener.stopSampling();
                    Log.i(TAG, "onCheckedChanged: User requested to stop sampling");
                }
            }
        });

        return view;
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
        if (toggleButton != null) {
            toggleButton.setChecked(false);
        }
        mListener = null;
        toggleButton = null;
        Log.i(TAG, "onDetach: Close fragment");
    }

    private void animateNumber(final TextView view, int start, int last) {

        final ValueAnimator animator = new ValueAnimator();
        animator.setObjectValues(start, last);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                view.setText(String.valueOf(animation.getAnimatedValue()));
            }
        });

        animator.setEvaluator(new TypeEvaluator<Integer>() {
            public Integer evaluate(float fraction, Integer startValue, Integer endValue) {
                return Math.round(startValue + (endValue - startValue) * fraction);
            }
        });

        animator.setDuration(1000);
        animator.start();
    }


    public interface OnFragmentInteractionListener {

        Long numberOfOfflineSamples();

        Position associatedPosition();

        Localization associatedLocalization();

        boolean startSampling(OnSample onSampleCallback);

        boolean stopSampling();

    }
}
