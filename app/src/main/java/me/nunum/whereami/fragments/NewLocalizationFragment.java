package me.nunum.whereami.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.Toast;

import me.nunum.whereami.R;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link NewLocalizationFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link NewLocalizationFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class NewLocalizationFragment extends Fragment {

    private double lat = 0;
    private double lng = 0;
    private String username;

    private OnFragmentInteractionListener mListener;

    public NewLocalizationFragment() {
        // Required empty public constructor
    }

    public static NewLocalizationFragment newInstance(double lat, double lng, String username) {
        NewLocalizationFragment fragment = new NewLocalizationFragment();
        fragment.lat = lat;
        fragment.lng = lng;
        fragment.username = username;
        Bundle args = new Bundle();
        args.putDouble("lat", lat);
        args.putDouble("lng", lng);
        args.putString("username", username);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            this.lat = getArguments().getDouble("lat");
            this.lng = getArguments().getDouble("lng");
            this.username = getArguments().getString("username");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_new_localization, container, false);

        final EditText localizationLabel = (EditText) view.findViewById(R.id.fnl_input_localization_label);
        final EditText username = (EditText) view.findViewById(R.id.fnl_input_user);
        final Switch isPrivate = (Switch) view.findViewById(R.id.fnl_is_private);
        final Button submit = (Button) view.findViewById(R.id.fnl_submit_btn);

        if (!this.username.isEmpty()) {
            username.setText(this.username);
            username.setEnabled(false);
        }

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String localizationStr = localizationLabel.getText().toString();
                final String usernameStr = username.getText().toString();
                final boolean isPrivateBl = isPrivate.isChecked();

                if (localizationStr.isEmpty()) {
                    Toast.makeText(mListener.context(), R.string.fnl_new_localization_empty_name, Toast.LENGTH_SHORT).show();
                    return;
                }

                if (usernameStr.isEmpty()) {
                    Toast.makeText(mListener.context(), R.string.fnl_new_localization_empty_username, Toast.LENGTH_SHORT).show();
                    return;
                }

                mListener.createLocalization(localizationStr.trim(), usernameStr.trim(), isPrivateBl, lat, lng);
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

        void createLocalization(final String localization,
                                final String username,
                                final boolean isPrivate,
                                final double lat,
                                final double lng);

    }
}
