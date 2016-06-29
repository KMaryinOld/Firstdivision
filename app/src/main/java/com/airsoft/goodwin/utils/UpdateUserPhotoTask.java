package com.airsoft.goodwin.utils;

import android.graphics.Bitmap;
import android.os.AsyncTask;

import com.airsoft.goodwin.repository.FirstdivisionImagesCache;

/**
 * Created by cherry on 23.01.2016.
 */
public class UpdateUserPhotoTask extends AsyncTask<Void, Void, Bitmap> {
    private String mURL;
    private AfterPhotoUpdateHandler mHandler;

    public UpdateUserPhotoTask(String url, AfterPhotoUpdateHandler handler) {
        mURL = url;
        mHandler = handler;
    }

    @Override
    protected Bitmap doInBackground(Void... params) {
        return Utils.getBitmapFromURL(mURL);
    }

    @Override
    protected void onPostExecute(Bitmap result) {
        super.onPostExecute(result);

        if (result != null) {
            FirstdivisionImagesCache.getInstance().addBitmapToCache(mURL, result);

            mHandler.onAfterPhotoUpdateAction(result);
        }
    }
}
