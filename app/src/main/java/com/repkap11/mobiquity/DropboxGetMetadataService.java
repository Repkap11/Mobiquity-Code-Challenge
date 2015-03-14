package com.repkap11.mobiquity;

import android.app.IntentService;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import com.dropbox.client2.DropboxAPI;
import com.dropbox.client2.exception.DropboxException;
import com.dropbox.client2.session.Session;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by paul on 3/14/15.
 */
public class DropboxGetMetadataService extends Service {

    private static final String TAG = DropboxGetMetadataService.class.getSimpleName();
    public static final String SESSION_NAME = "SESSION_NAME";
    public static final String ACTION_GET_METADATA = "ACTION_GET_METADATA";
    public static final String RESULT_EXTRA_IMAGE_URLS = "RESULT_EXTRA_IMAGE_URLS";
    private Thread mDownloadThread = new Thread();

    private boolean mIsBoundToActivity;

    public DropboxGetMetadataService() {
        super();
    }

    public void startDownloadMetadata(final DropboxAPI dbAPI, final String path) {
        try {
            mDownloadThread.interrupt();
            Log.i(TAG,"About to join thread");
            mDownloadThread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        Log.i(TAG,"About to start thread");
        mDownloadThread = new Thread(new Runnable() {
            public void run() {
                ArrayList<String> urls = new ArrayList<String>();
                try {
                    DropboxAPI.Entry existingEntry = dbAPI.metadata(path, 0 /* unlimited number of results*/, null, true, null);
                    Log.i(TAG,"Entry:"+existingEntry.path);
                    //boolean isDir = existingEntry.isDir();
                    List<DropboxAPI.Entry> files = existingEntry.contents;
                    if (files != null) {
                        urls = new ArrayList<String>(files.size());
                        Log.i(TAG,"Num elements:"+files.size());
                        for (int i = 0; i < files.size(); i++) {
                            DropboxAPI.Entry file = files.get(i);
                            if (!file.isDeleted) {
                                DropboxAPI.DropboxLink link = dbAPI.media(file.path, true);
                                String url = link.url;
                                Log.i(TAG, "File Name:" + file.fileName() + " URL:" + url);
                                urls.add(i,url);
                            }
                        }
                    }
                } catch (DropboxException e) {
                    e.printStackTrace();
                }
                Intent result = new Intent(ACTION_GET_METADATA);
                result.putExtra(RESULT_EXTRA_IMAGE_URLS,urls);
                sendBroadcast(result);
            }
        });
        mDownloadThread.start();
    }

    private final IBinder mBinder = new MyBinder();

    @Override
    public IBinder onBind(Intent arg0) {
        mIsBoundToActivity = true;
        Log.d(TAG, "onBind");
        return mBinder;
    }

    public class MyBinder extends Binder {
        DropboxGetMetadataService getService() {
            return DropboxGetMetadataService.this;
        }
    }
}
