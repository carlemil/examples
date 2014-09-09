/*********************************************************************
 *       ____                      __  __       _     _ _            *
 *      / ___|  ___  _ __  _   _  |  \/  | ___ | |__ (_) | ___       *
 *      \___ \ / _ \| '_ \| | | | | \  / |/ _ \| '_ \| | |/ _ \      *
 *       ___) | (_) | | | | |_| | | |\/| | (_) | |_) | | |  __/      *
 *      |____/ \___/|_| |_|\__, | |_|  |_|\___/|_.__/|_|_|\___|      *
 *                         |___/                                     *
 *                                                                   *
 *********************************************************************
 *      Copyright 2014 Sony Mobile Communications AB.                *
 *      All rights, including trade secret rights, reserved.         *
 *********************************************************************/

package com.sonymobile.sonyselect.util;

import android.graphics.Bitmap;
import android.util.Log;
import android.util.LruCache;

import com.android.volley.toolbox.ImageLoader.ImageCache;

public class BitmapMemCache extends LruCache<String, Bitmap> implements ImageCache {

    private static final String LOG_TAG = BitmapMemCache.class.getCanonicalName();

    public BitmapMemCache(int sizeInKiloBytes) {
        super(sizeInKiloBytes);
        Log.d(LOG_TAG, "BitmapMemCache size: "+sizeInKiloBytes+"kb");
    }

    @Override
    protected int sizeOf(String key, Bitmap bitmap) {
        int size = bitmap.getRowBytes() * bitmap.getHeight() / 1024;
        return size;
    }

    public boolean contains(String key) {
        boolean contains = get(key) != null;
        return contains;
    }

    public Bitmap getBitmap(String key) {
        Bitmap bitmap = get(key);
        return bitmap;
    }

    public void putBitmap(String url, Bitmap bitmap) {
        put(url, bitmap);
    }
}
