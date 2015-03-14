package com.repkap11.mobiquity;

import android.content.Context;
import android.graphics.Bitmap;
import android.media.Image;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageSize;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

import java.util.Random;

/**
 * Created by paul on 3/13/15.
 */
public class ImageLoaderAdapter extends BaseAdapter {
    private static final String TAG = ImageLoaderAdapter.class.getSimpleName();
    private Context mContext;
    private int mNumImages;
    public ImageLoaderAdapter(Context context){
        Log.e(TAG,"Image loader recreated");

        DisplayImageOptions defaultOptions = new DisplayImageOptions.Builder()
        .cacheInMemory(true)
        .cacheOnDisk(true)
        .build();
        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(context)
        .defaultDisplayImageOptions(defaultOptions)
        .build();
        ImageLoader.getInstance().init(config);
        this.mContext = context;
        this.mNumImages =  new Random().nextInt((50 - 2) + 1) + 2;

    }

    @Override
    public int getCount() {
        return mNumImages;
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
            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            returnView = inflater.inflate(R.layout.fragment_item_grid_element,parent, false);
            returnView.setTag(new ItemHolder(returnView));
        } else
        {
            returnView = convertView;
        }
        ImageView imageView = ((ItemHolder)returnView.getTag()).mImageView;
        loader.displayImage("http://www.online-image-editor.com//styles/2014/images/example_image.png", imageView);
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
