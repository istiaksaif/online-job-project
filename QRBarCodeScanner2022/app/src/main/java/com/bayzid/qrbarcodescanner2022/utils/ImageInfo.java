package com.bayzid.qrbarcodescanner2022.utils;

import android.net.Uri;

public class ImageInfo {
    private Uri mImageUri;
    private boolean mTakenByCamera;

    public ImageInfo() {
    }

    public ImageInfo(Uri imageUri, boolean takenByCamera) {
        mImageUri = imageUri;
        mTakenByCamera = takenByCamera;
    }

    public Uri getImageUri() {
        return mImageUri;
    }

    public void setImageUri(Uri imageUri) {
        mImageUri = imageUri;
    }

    public boolean isTakenByCamera() {
        return mTakenByCamera;
    }

    public void setTakenByCamera(boolean takenByCamera) {
        mTakenByCamera = takenByCamera;
    }
}
