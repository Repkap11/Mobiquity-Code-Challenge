package com.repkap11.mobiquity;

import android.app.Service;
import android.content.Intent;
import android.net.Uri;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import com.dropbox.client2.DropboxAPI;
import com.dropbox.client2.ProgressListener;
import com.dropbox.client2.exception.DropboxException;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by paul on 3/14/15.
 */
public class DropboxGetMetadataService extends Service {

    private static final String TAG = DropboxGetMetadataService.class.getSimpleName();
    public static final String ACTION_GET_METADATA = "ACTION_GET_METADATA";
    public static final String RESULT_EXTRA_IMAGE_PATHS = "RESULT_EXTRA_IMAGE_PATHS";

    public static final String ACTION_UPLOAD_FILE = "ACTION_UPLOAD_FILE";

    private Thread mDownloadThread = new Thread();

    public DropboxGetMetadataService() {
        super();
    }


    public void startUploadFile(final DropboxAPI dbAPI, final String imagePath) {
        Log.i(TAG,"Starting file upload");
        try {
            mDownloadThread.interrupt();
            //Log.i(TAG,"About to join thread");
            mDownloadThread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        mDownloadThread = new Thread(new Runnable() {
            public void run() {
                ArrayList<String> paths = new ArrayList<String>();
                try {
                    File file = new File(imagePath);
                    FileInputStream inputStream = new FileInputStream(file);
                    Log.i(TAG,"Starting file upload started");
                    dbAPI.putFile("/" + file.getName(), inputStream,
                            file.length(), null, new ProgressListener() {
                                @Override
                                public void onProgress(long l, long l2) {
                                    Log.i(TAG, "Upload Progress:" + l + " out of " + l2);
                                }
                            });
                } catch (DropboxException e) {
                    e.printStackTrace();
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
                Intent result = new Intent(ACTION_UPLOAD_FILE);
                sendBroadcast(result);
            }
        });
        mDownloadThread.start();
    }

    public void startDownloadMetadata(final DropboxAPI dbAPI, final String path) {
        try {
            mDownloadThread.interrupt();
            //Log.i(TAG,"About to join thread");
            mDownloadThread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        //Log.i(TAG,"About to start thread");
        mDownloadThread = new Thread(new Runnable() {
            public void run() {
                ArrayList<String> paths = new ArrayList<String>();
                try {
                    DropboxAPI.Entry existingEntry = dbAPI.metadata(path, 0 /* unlimited number of results*/, null, true, null);
                    Log.i(TAG, "Entry:" + existingEntry.path);
                    //boolean isDir = existingEntry.isDir();
                    List<DropboxAPI.Entry> files = existingEntry.contents;
                    if (files != null) {
                        paths = new ArrayList<String>(files.size());
                        Log.i(TAG, "Num elements:" + files.size());
                        for (int i = 0; i < files.size(); i++) {
                            DropboxAPI.Entry file = files.get(i);
                            if (!file.isDeleted) {
                                //DropboxAPI.DropboxLink link = dbAPI.media(file.path, true);
                                //String url = link.url;
                                String path = file.path;
                                paths.add(i, path);
                            }
                        }
                    }
                } catch (DropboxException e) {
                    e.printStackTrace();
                }
                Intent result = new Intent(ACTION_GET_METADATA);
                result.putExtra(RESULT_EXTRA_IMAGE_PATHS, paths);
                sendBroadcast(result);
            }
        });
        mDownloadThread.start();
    }

    private final IBinder mBinder = new MyBinder();

    @Override
    public IBinder onBind(Intent arg0) {
        Log.d(TAG, "onBind");
        return mBinder;
    }

    public class MyBinder extends Binder {
        DropboxGetMetadataService getService() {
            return DropboxGetMetadataService.this;
        }
    }
}
