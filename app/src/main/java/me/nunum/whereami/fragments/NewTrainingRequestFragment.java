package me.nunum.whereami.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.util.Linkify;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import me.nunum.whereami.R;
import me.nunum.whereami.framework.OnResponse;
import me.nunum.whereami.model.Algorithm;
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
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER

    private List<Algorithm> algorithmsList = null;

    private OnFragmentInteractionListener mListener;

    public NewTrainingRequestFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment NewTrainingRequestFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static NewTrainingRequestFragment newInstance() {
        NewTrainingRequestFragment fragment = new NewTrainingRequestFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View hostView = inflater.inflate(R.layout.fragment_new_training_request, container, false);

        final Spinner algorithms = (Spinner) hostView.findViewById(R.id.fntr_list_algorithms);

        final TextView info = (TextView) hostView.findViewById(R.id.fntr_algorithm_info);

        final Button submit = (Button) hostView.findViewById(R.id.fntr_submit_btn);


        final ArrayAdapter<CharSequence> adapter = new ArrayAdapter<CharSequence>(mListener.context(), android.R.layout.simple_spinner_dropdown_item);

        algorithms.setAdapter(adapter);

        HttpService service = (HttpService) mListener.getService(Services.HTTP);

        service.allAlgorithms(new OnResponse<List<Algorithm>>() {
            @Override
            public void onSuccess(final List<Algorithm> o) {
                for (int i = 0; i < o.size(); i++) {
                    adapter.add(o.get(i).getName());
                }

                algorithmsList = o;
            }

            @Override
            public void onFailure(Throwable throwable) {
                Toast.makeText(mListener.context(), R.string.fntr_request_algorithm_error, Toast.LENGTH_SHORT).show();
            }
        });


        algorithms.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

                if (algorithmsList == null) {
                    return;
                }

                Algorithm algorithm = algorithmsList.get(i);
                Log.d("TAG", "onItemClick: " + algorithm);

                info.setText("Information: " + algorithm.getPaperURL());
                Linkify.addLinks(info, Linkify.WEB_URLS);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                info.setText("");
            }
        });

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!(algorithmsList == null || algorithmsList.isEmpty())) {
                    final Algorithm algorithm = algorithmsList.get(algorithms.getSelectedItemPosition());
                    mListener.submitNewTanningRequest(algorithm);
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
    }

    public interface OnFragmentInteractionListener {
        Context context();

        Object getService(Services service);

        void submitNewTanningRequest(Algorithm algorithm);
    }
}
