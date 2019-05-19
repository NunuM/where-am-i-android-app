package me.nunum.whereami.fragments;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import me.nunum.whereami.R;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link NewPositionFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link NewPositionFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class NewPositionFragment extends Fragment {

    private OnFragmentInteractionListener mListener;

    public NewPositionFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment NewPositionFragment.
     */
    public static NewPositionFragment newInstance() {
        NewPositionFragment fragment = new NewPositionFragment();
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
        View view = inflater.inflate(R.layout.fragment_new_position, container, false);

        final EditText labelName = (EditText) view.findViewById(R.id.fnp_input_label);
        Button submit = (Button) view.findViewById(R.id.fnp_submit_btn);

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String label = labelName.getText().toString();

                if (label.isEmpty()) {
                    Toast.makeText(mListener.context(), R.string.fnp_new_position_label_empty_input, Toast.LENGTH_SHORT).show();
                    return;
                }

                mListener.createPosition(label);
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
        mListener = null;
    }


    public interface OnFragmentInteractionListener {
        Context context();

        void createPosition(final String label);
    }
}
