package com.repkap11.mobiquity;

import android.app.DownloadManager;
import android.content.Context;
import android.util.Log;

import com.dropbox.client2.DropboxAPI;
import com.dropbox.client2.exception.DropboxException;
import com.dropbox.client2.exception.DropboxUnlinkedException;
import com.nostra13.universalimageloader.core.download.BaseImageDownloader;
import com.nostra13.universalimageloader.core.download.ImageDownloader;

import java.io.IOException;
import java.io.InputStream;

public class DropboxImageDownloader extends BaseImageDownloader {
    private static final String TAG = DropboxImageDownloader.class.getSimpleName();

    private DropboxAPI mAPI;
    public DropboxImageDownloader(GreetingsActivity activity) {
        super(activity);
        this.mAPI = activity.mDBApi;
    }

    @Override
    protected InputStream getStreamFromOtherSource(String imageUri, Object extra) throws IOException {
        Log.i(TAG, "Calling dropbox image downloader:"+imageUri);
        try {
            return mAPI.getThumbnailStream(imageUri, DropboxAPI.ThumbSize.ICON_256x256, DropboxAPI.ThumbFormat.JPEG);
            //return mAPI.getFileStream(imageUri, null);
        } catch (DropboxUnlinkedException e) {
            throw new IOException(e);
        }catch (DropboxException e){
            //TODO, check the exception for its type
            throw new IOException(e);
        }
    }
}