package me.nunum.whereami.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import java.util.List;

import me.nunum.whereami.R;
import me.nunum.whereami.framework.OnResponse;
import me.nunum.whereami.model.Algorithm;
import me.nunum.whereami.model.AlgorithmProvider;
import me.nunum.whereami.service.HttpService;
import me.nunum.whereami.service.Services;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link NewTrainingRequestFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link NewTrainingRequestFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class NewTrainingRequestFragment extends Fragment {

    private static final String TAG = NewTrainingRequestFragment.class.getSimpleName();

    private List<Algorithm> algorithmsList = null;

    private OnFragmentInteractionListener mListener;

    public NewTrainingRequestFragment() {
        // Required empty public constructor
    }

    public static NewTrainingRequestFragment newInstance() {
        return new NewTrainingRequestFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i(TAG, "onCreate: Open fragment");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View hostView = inflater.inflate(R.layout.fragment_new_training_request, container, false);

        final ListView algorithms = (ListView) hostView.findViewById(R.id.fntr_algorithm_list);
        final ListView algorithmsImplementations = (ListView) hostView.findViewById(R.id.fntr_algorithm_implementation_list);

        final Button submit = (Button) hostView.findViewById(R.id.fntr_submit_btn);

        final ArrayAdapter<CharSequence> adapter = new ArrayAdapter<CharSequence>(mListener.context(), android.R.layout.simple_list_item_single_choice);
        final ArrayAdapter<CharSequence> adapterForImplementations = new ArrayAdapter<CharSequence>(mListener.context(), android.R.layout.simple_list_item_single_choice);

        algorithms.setAdapter(adapter);
        algorithmsImplementations.setAdapter(adapterForImplementations);

        algorithms.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Algorithm algorithm = algorithmsList.get(position);

                algorithmsImplementations.clearChoices();
                adapterForImplementations.clear();

                for (AlgorithmProvider p : algorithm.getProviders()) {
                    final String providerStr = getString(R.string.fntr_new_training_provider_name, p.getId(), p.getPredictionRate(), p.getDeployed().toString());
                    adapterForImplementations.add(providerStr);
                }
            }
        });

        final HttpService service = (HttpService) mListener.getService(Services.HTTP);

        service.allAlgorithms(new OnResponse<List<Algorithm>>() {
            @Override
            public void onSuccess(final List<Algorithm> o) {
                for (int i = 0; i < o.size(); i++) {
                    adapter.add(o.get(i).getName());
                }
                algorithmsList = o;
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onFailure(Throwable throwable) {
                Log.e(TAG, "onFailure: Error request available algorithms from the server", throwable);
                Toast.makeText(mListener.context(), R.string.fntr_request_algorithm_error, Toast.LENGTH_SHORT).show();
            }
        });

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!(algorithmsList == null || algorithmsList.isEmpty())) {
                    int itemPosition = algorithms.getCheckedItemPosition();

                    if (AdapterView.INVALID_POSITION != itemPosition
                            && itemPosition < algorithmsList.size()) {

                        final Algorithm algorithm = algorithmsList.get(itemPosition);

                        final int providerSize = algorithm.getProviders().size();

                        final int checkedItemPosition = algorithmsImplementations.getCheckedItemPosition();

                        if (checkedItemPosition != AdapterView.INVALID_POSITION
                                && checkedItemPosition < providerSize) {

                            AlgorithmProvider provider = algorithm.getProviders().get(checkedItemPosition);

                            Object position = algorithmsImplementations.getItemAtPosition(checkedItemPosition);

                            if (position.toString().contains(provider.getId().toString())) {
                                mListener.submitNewTanningRequest(algorithm.getId(), provider.getId());
                                return;
                            }
                        }
                    }

                    Toast.makeText(mListener.context(), R.string.fntr_new_training_algorithm_invalid_request, Toast.LENGTH_LONG).show();
                }
            }
        });

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
        mListener = null;
        algorithmsList = null;
        Log.i(TAG, "onDetach: Close fragment");
    }

    public interface OnFragmentInteractionListener {
        Context context();

        Object getService(Services service);

        void submitNewTanningRequest(Long algorithmId, Long providerId);
    }
}
