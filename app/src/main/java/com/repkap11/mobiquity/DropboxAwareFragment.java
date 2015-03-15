package com.repkap11.mobiquity;

import android.app.Fragment;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;

import com.dropbox.client2.DropboxAPI;

import java.util.ArrayList;


/**
 * This base fragment class interacts with the Dropbox api via DropboxGetMetataService.
 */
public abstract class DropboxAwareFragment extends Fragment {

    private static final String TAG = DropboxAwareFragment.class.getSimpleName();
    private DropboxGetMetadataService myServiceBinder;
    private ResponseReceiver mResponseReceiver = new ResponseReceiver();
    private boolean mResponseReceiverRegistered = false;
    private boolean mNeedsDownload = true;
    private String mNeedsUploadPath = null;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (!mResponseReceiverRegistered) {
            getActivity().getApplicationContext().registerReceiver(mResponseReceiver,
                    new IntentFilter(DropboxGetMetadataService.ACTION_GET_METADATA));

            getActivity().getApplicationContext().registerReceiver(mResponseReceiver,
                    new IntentFilter(DropboxGetMetadataService.ACTION_UPLOAD_FILE));

            getActivity().getApplicationContext().registerReceiver(mResponseReceiver,
                    new IntentFilter(DropboxGetMetadataService.ACTION_UPLOAD_FILE_PROGRESS));
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

        //onServiceConnected will get called whenever the owning activity gets destroyed.
        public void onServiceConnected(ComponentName className, IBinder binder) {
            myServiceBinder = ((DropboxGetMetadataService.MyBinder) binder).getService();
            Log.i(TAG, "connected");
            if (mNeedsUploadPath != null) {
                uploadFileToDropbox(mNeedsUploadPath);
                mNeedsUploadPath = null;
            }
            if (mNeedsDownload) {
                mNeedsDownload = false;
                getDropboxMetadata("/");
            }
        }

        public void onServiceDisconnected(ComponentName className) {
            Log.i(TAG, "disconnected");
            myServiceBinder = null;
        }
    };

    @Override
    public void onResume() {
        if (myServiceBinder == null) {
            doBindService();
        }
        super.onResume();
    }

    public void doBindService() {
        Intent intent = new Intent(getActivity().getApplicationContext(), DropboxGetMetadataService.class);
        getActivity().getApplicationContext().bindService(intent, myConnection, Context.BIND_AUTO_CREATE);
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
        if (getActivity() != null) {
            DropboxAPI api = ((GreetingsActivity) getActivity()).mDBApi;
            if (api != null) {
                myServiceBinder.startDownloadMetadata(api, path);
            } else {
                Log.e(TAG, "Unable to get Dropbox metadata while db api is null");
            }
        } else {
            Log.e(TAG, "Unable to get Dropbox metadata while activity is null");
        }
    }

    protected void startDropboxFileUpload(String path) {
        mNeedsUploadPath = path;
        if (myServiceBinder != null) {
            Log.e(TAG, "Problem, binds is already set, we need up upload now");
        }
    }

    private void uploadFileToDropbox(String imagePath) {
        if (getActivity() != null) {
            DropboxAPI api = ((GreetingsActivity) getActivity()).mDBApi;
            if (api != null) {
                myServiceBinder.startUploadFile(api, imagePath);
            } else {
                Log.e(TAG, "Unable to get Dropbox metadata while db api is null");
            }
        } else {
            Log.e(TAG, "Unable to get Dropbox metadata while activity is null");
        }
    }

    protected abstract void onMetadataReceived(ArrayList<String> data);

    protected abstract void onFileUploadComplete();
    protected abstract void onFileUploadProgress(float progress);

    /**
     * Listens for results from DropboxGetMatadataService, and forwards them to onMetadataReceived
     */
    private class ResponseReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals(DropboxGetMetadataService.ACTION_GET_METADATA)) {
                ArrayList<String> data = intent.getStringArrayListExtra(DropboxGetMetadataService.RESULT_EXTRA_IMAGE_PATHS);
                onMetadataReceived(data);
            } else if (action.equals(DropboxGetMetadataService.ACTION_UPLOAD_FILE)) {
                onFileUploadComplete();
            } else if (action.equals(DropboxGetMetadataService.ACTION_UPLOAD_FILE_PROGRESS)) {
                Log.e(TAG,"Progress Message Received");
                float progress = intent.getFloatExtra(DropboxGetMetadataService.RESULT_EXTRA_PROGRESS,0);
                onFileUploadProgress(progress);
            }
        }

    }
}