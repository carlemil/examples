package com.sonymobile.sonyselect.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.android.volley.toolbox.ImageLoader.ImageListener;
import com.sonymobile.sonyselect.net.VolleySingelton;

public class BackgroundColorUpdateReceiver extends BroadcastReceiver {
    private static final String LOG_TAG = BackgroundColorUpdateReceiver.class.getCanonicalName();

    public static final String BACKGROUND_COLOR_UPDATE_URL = "BACKGROUND_COLOR_UPDATE_URL";
    private ImageListener mImageLoadedListener;

    public void setImageLoadedListener(ImageListener imageLoadedListener) {
        this.mImageLoadedListener = imageLoadedListener;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        // Get extra data included in the Intent
        String url = intent.getStringExtra(BACKGROUND_COLOR_UPDATE_URL);
        Log.d(LOG_TAG, "Received intent for background color changed. requestUrl:" + url);
        VolleySingelton.getInstance().getImageLoader().get(url, mImageLoadedListener);
    }

}
