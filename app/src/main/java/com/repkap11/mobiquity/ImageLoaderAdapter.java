package com.repkap11.mobiquity;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.DropBoxManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.dropbox.client2.DropboxAPI;
import com.dropbox.client2.exception.DropboxException;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by paul on 3/13/15.
 */
public class ImageLoaderAdapter extends BaseAdapter {
    private static final String TAG = ImageLoaderAdapter.class.getSimpleName();
    private final ArrayList<String> mURLs;
    private GreetingsActivity mActivity;
    public ImageLoaderAdapter(GreetingsActivity activity, ArrayList<String> urls){
        Log.e(TAG,"Image loader recreated");
        DisplayImageOptions defaultOptions = new DisplayImageOptions.Builder()
        .cacheInMemory(true)
        .cacheOnDisk(true)
        .considerExifParams(true)
        //.showImageOnLoading(R.drawable.download_failure_icon)
        //.showImageForEmptyUri(R.drawable.ic_empty)
        .showImageOnFail(R.drawable.download_failure_icon)
        .build();
        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(activity)
        .defaultDisplayImageOptions(defaultOptions)
        .build();
        ImageLoader.getInstance().init(config);
        this.mActivity = activity;
        this.mURLs = urls;
    }


    @Override
    public int getCount() {
        return mURLs.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }
private static class ItemHolder {
    public ImageView mImageView;
    public ItemHolder(View baseView){
        ImageView imageView = (ImageView)baseView.findViewById(R.id.fragment_item_grid_element_image);
        this.mImageView = imageView;
    }
}
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ImageLoader loader = ImageLoader.getInstance();
        View returnView;
        if (convertView == null){
            LayoutInflater inflater = (LayoutInflater) mActivity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            returnView = inflater.inflate(R.layout.fragment_item_grid_element,parent, false);
            returnView.setTag(new ItemHolder(returnView));
        } else
        {
            returnView = convertView;
        }
        ImageView imageView = ((ItemHolder)returnView.getTag()).mImageView;
        loader.displayImage(mURLs.get(position), imageView);
        /*
        loader.loadImage(, DisplayImageOptions.createSimple(),new ImageLoadingListener() {
            @Override
            public void onLoadingStarted(String s, View view) {
                Log.i(TAG, "Image Loading Started");
            }

            @Override
            public void onLoadingFailed(String s, View view, FailReason failReason) {
                Log.i(TAG, "Image Loading Failed"+failReason.toString());
            }

            @Override
            public void onLoadingComplete(String s, View view, Bitmap bitmap) {
                Log.i(TAG, "Image Loading Complete");
            }

            @Override
            public void onLoadingCancelled(String s, View view) {
                Log.i(TAG, "Image Loading Cancelled");
            }
        });
        */
        return returnView;
    }
}
