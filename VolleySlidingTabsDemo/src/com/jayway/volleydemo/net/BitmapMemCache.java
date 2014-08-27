
package com.jayway.volleydemo.net;

import android.graphics.Bitmap;
import android.util.Log;
import android.util.LruCache;

import com.android.volley.toolbox.ImageLoader.ImageCache;

public class BitmapMemCache extends LruCache<String, Bitmap> implements ImageCache {

    private static final String LOG_TAG = BitmapMemCache.class.getCanonicalName();

    public BitmapMemCache(int sizeInKiloBytes) {
        super(sizeInKiloBytes);
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
        Log.d(LOG_TAG, "Size of entrys in cache: " + size() + " Kb.");
        put(url, bitmap);
    }
}
