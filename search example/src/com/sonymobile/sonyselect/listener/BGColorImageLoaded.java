package com.sonymobile.sonyselect.listener;

import android.content.res.Resources;
import android.util.Log;

import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.ImageLoader.ImageContainer;
import com.sonymobile.sonyselect.R;
import com.sonymobile.sonyselect.components.BackgroundColorHelper;
import com.sonymobile.sonyselect.util.BitmapUtil;

public class BGColorImageLoaded implements ImageLoader.ImageListener {
    private static final String LOG_TAG = BGColorImageLoaded.class.getCanonicalName();
    Resources mResources;
    BackgroundColorHelper mBackgroundColorHelper;

    public BGColorImageLoaded(Resources resources, BackgroundColorHelper backgroundColorHelper) {
        mResources = resources;
        mBackgroundColorHelper = backgroundColorHelper;
    }

    @Override
    public void onErrorResponse(VolleyError error) {
        Log.e(LOG_TAG, "Failed loading image: " + error.getMessage());
    }

    @Override
    public void onResponse(ImageContainer imageContainer, boolean isImmediate) {
        int color = mResources.getColor(R.color.default_background_color);
        if (imageContainer != null && imageContainer.getBitmap() != null) {
            Log.d(LOG_TAG, "Got imageContainer, url: " + imageContainer.getRequestUrl());
            color = BitmapUtil.extractColor(imageContainer.getBitmap());
        }
        Log.d(LOG_TAG, "Set background to color: " + Integer.toHexString(color));
        mBackgroundColorHelper.onColorExtracted(color);
    }
}
