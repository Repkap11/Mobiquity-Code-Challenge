package com.repkap11.mobiquity;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.app.Fragment;
import android.os.Bundle;
import android.os.IBinder;
import android.os.ResultReceiver;
import android.util.Log;

import com.dropbox.client2.DropboxAPI;

import java.lang.reflect.Array;
import java.util.ArrayList;


public abstract class DropboxAwareFragment extends Fragment {

    private static final String TAG = DropboxAwareFragment.class.getSimpleName();
    private DropboxGetMetadataService myServiceBinder;
    private ResponseReceiver mResponseReceiver = new ResponseReceiver();
    private boolean mResponseReceiverRegistered = false;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (!mResponseReceiverRegistered) {
            getActivity().getApplicationContext().registerReceiver(mResponseReceiver, new IntentFilter(DropboxGetMetadataService.ACTION_GET_METADATA));
            mResponseReceiverRegistered = true;
        }
    }

    @Override
    public void onDestroy() {
        if (mResponseReceiverRegistered) {
            getActivity().getApplicationContext().unregisterReceiver(mResponseReceiver);
            mResponseReceiverRegistered = false;
        }
        super.onDestroy();
    }

    private ServiceConnection myConnection = new ServiceConnection() {

        public void onServiceConnected(ComponentName className, IBinder binder) {
            myServiceBinder = ((DropboxGetMetadataService.MyBinder) binder).getService();
            Log.i(TAG, "connected");
            getDropboxMetadata("/");
        }

        public void onServiceDisconnected(ComponentName className) {
            Log.i(TAG, "disconnected");
            myServiceBinder = null;
        }
    };

    public void doBindService() {
        Intent intent = new Intent(getActivity().getApplicationContext(), DropboxGetMetadataService.class);
        getActivity().getApplicationContext().bindService(intent, myConnection, Context.BIND_AUTO_CREATE);
    }

    @Override
    public void onResume() {
        if (myServiceBinder == null) {
            doBindService();
        }
        super.onResume();
    }

    @Override
    public void onPause() {
        doUnBindService();
        super.onPause();
    }

    private void doUnBindService() {
        if (myServiceBinder != null) {
            getActivity().getApplicationContext().unbindService(myConnection);
            myServiceBinder = null;
        }
    }

    protected void getDropboxMetadata(String path) {
        DropboxAPI api = ((GreetingsActivity) getActivity()).mDBApi;
        if (api != null) {
            myServiceBinder.startDownloadMetadata(api, path);
        } else
        {
            Log.e(TAG,"Unable to get Dropbox metadata while db api is null");
        }
    }

    protected abstract void onMetadataReceived(ArrayList<String> data);

    private class ResponseReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            ArrayList<String> data = intent.getStringArrayListExtra(DropboxGetMetadataService.RESULT_EXTRA_IMAGE_URLS);
            onMetadataReceived(data);
        }
    }
}
