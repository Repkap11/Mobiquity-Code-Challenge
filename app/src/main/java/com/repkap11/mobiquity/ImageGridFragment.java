package com.repkap11.mobiquity;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListAdapter;

import java.util.ArrayList;


/**
 * A fragment representing a list of Items.
 * Activities containing this fragment MUST implement the {@link FragmentInteractionListener}
 * interface.
 */
public class ImageGridFragment extends PictureTakingFragment implements AbsListView.OnItemClickListener {

    private static final String TAG = ImageGridFragment.class.getSimpleName();
    protected static final int REQUEST_IMAGE_CAPTURE = 1;

    private FragmentInteractionListener mListener;
    private AbsListView mListView;
    private ImageLoaderAdapter mAdapter;

    public ImageGridFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        mAdapter = new ImageLoaderAdapter((GreetingsActivity)getActivity(), new ArrayList<String>());
    }
    @Override
    public void onMetadataReceived(ArrayList<String> data){
        Log.i(TAG,"Received data with:"+data.size()+" elements");
        mAdapter.setData(data);
        mAdapter.notifyDataSetChanged();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_item_grid, container, false);
        // Set the adapter
        mListView = (AbsListView) view.findViewById(android.R.id.list);
        ((AdapterView<ListAdapter>) mListView).setAdapter(mAdapter);

        // Set OnItemClickListener so we can be notified on item clicks
        mListView.setOnItemClickListener(this);
        return view;
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
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (null != mListener) {
            // Notify the active callbacks interface (the activity, if the
            // fragment is attached to one) that an item has been selected.
            mListener.onImageGridFragmentInteraction(position);
        }
    }
    @Override
    public void onResume(){
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
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
        // TODO: Update argument type and name
        public void onImageGridFragmentInteraction(int position);
    }

    @Override
    public void onDestroy() {
        Log.e(TAG,"Grid Fragment Destroyed");
        super.onDestroy();
    }
}
