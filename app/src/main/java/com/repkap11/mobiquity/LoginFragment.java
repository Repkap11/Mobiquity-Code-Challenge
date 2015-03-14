package com.repkap11.mobiquity;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link LoginFragment.FragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link LoginFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class LoginFragment extends Fragment implements Button.OnClickListener{
    private static final String TAG = LoginFragment.class.getSimpleName();


    public static final String DROPBOX_LOGIN_CLICKED = "DROPBOX_LOGIN_CLICKED";

    private FragmentInteractionListener mListener;

    public LoginFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
         View baseView = inflater.inflate(R.layout.fragment_login, container, false);
        ((Button)baseView.findViewById(R.id.fragment_login_login_button)).setOnClickListener(this);

        TextView mTVIsLoggedIn = (TextView)baseView.findViewById(R.id.fragment_login_is_logged_in);
        return baseView;
    }

    public void loginOnClick(View view) {

        Log.i(TAG, "Dropbox login button clicked");
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (FragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onClick(View v) {
        Log.i(TAG,"Dropbox login clicked");
        mListener.onLoginFragmentInteraction(DROPBOX_LOGIN_CLICKED);
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface FragmentInteractionListener {
        public void onLoginFragmentInteraction(String event);
    }
}
