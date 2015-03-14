package com.repkap11.mobiquity;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
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
                    Log.i(TAG,"URI toString: "+mImageUri.getPath());
                    startDropboxFileUpload(new File(mImageUri.getPath()).getAbsolutePath());
                }
        }
    }

    @Override
    protected void onFileUploadComplete() {
        getDropboxMetadata("/");
    }

}
