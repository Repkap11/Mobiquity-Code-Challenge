package com.repkap11.mobiquity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;


public abstract class PictureTakingFragment extends DropboxAwareFragment implements View.OnClickListener {

    private static final String TAG = PictureTakingFragment.class.getSimpleName();
    private ProgressDialog mProgressDialog;
    private boolean mNeedsUpLoadProgressDialog = false;
    private int mProgressDialogProgress = 0;

    Uri mImageUri;

    public PictureTakingFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onStart() {
        Button mTakePictureButton = (Button) getView().findViewById(R.id.button_take_picture);
        mTakePictureButton.setOnClickListener(this);
        super.onStart();
    }

    @Override
    public void onClick(View v) {
        dispatchTakePictureIntent();
    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );
        return image;
    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getActivity().getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException e) {
                // Error occurred while creating the File
                e.printStackTrace();
            }
            mImageUri = Uri.fromFile(photoFile);
            // Continue only if the File was successfully created
            if (photoFile != null) {
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, mImageUri);
                startActivityForResult(takePictureIntent, ImageGridFragment.REQUEST_IMAGE_CAPTURE);
            }
        }
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case ImageGridFragment.REQUEST_IMAGE_CAPTURE:
                if (resultCode == Activity.RESULT_OK) {
                    getActivity().getContentResolver().notifyChange(mImageUri, null);
                    Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                    mediaScanIntent.setData(mImageUri);
                    getActivity().sendBroadcast(mediaScanIntent);
                    Toast.makeText(getActivity(), mImageUri.toString(), Toast.LENGTH_LONG).show();
                    Log.i(TAG, "URI toString: " + mImageUri.getPath());
                    startDropboxFileUpload(new File(mImageUri.getPath()).getAbsolutePath());
                    mNeedsUpLoadProgressDialog = true;
                    showProgressDialog();
                }
        }
    }

    private void showProgressDialog() {
        mProgressDialog.setIndeterminate(false);
        mProgressDialog.setMessage("Uploading Image...");
        mProgressDialog.setCancelable(false);
        mProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        mProgressDialog.setProgress(mProgressDialogProgress);
        Log.e(TAG,"Initted With Progress:"+mProgressDialogProgress);
        mProgressDialog.show();
        mProgressDialog.setProgress(mProgressDialogProgress);
    }

    @Override
    public void onAttach(Activity activity) {
        Log.i(TAG, "On attach called");
        super.onAttach(activity);
        mProgressDialog = new ProgressDialog(activity);
        mProgressDialog.setProgressDrawable(getResources().getDrawable(R.drawable.apptheme_progress_horizontal_holo_light));
        if (mNeedsUpLoadProgressDialog) {
            showProgressDialog();
        }

    }

    @Override
    public void onDetach() {
        mProgressDialog.dismiss();
        mProgressDialog = null;
        super.onDetach();
    }

    @Override
    protected void onFileUploadProgress(float progress) {
        if (mProgressDialog != null) {
            mProgressDialogProgress = (int) (progress * 100);
            mProgressDialog.setProgress(mProgressDialogProgress);
        }
    }

    @Override
    protected void onFileUploadComplete() {
        mNeedsUpLoadProgressDialog = false;
        if (mProgressDialog != null) {
            mProgressDialog.dismiss();
        }
        getDropboxMetadata("/");
    }

}
