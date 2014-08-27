package com.jayway.volleydemo.net;

import android.graphics.Bitmap;
import android.util.LruCache;

import com.android.volley.toolbox.ImageLoader.ImageCache;

public class BitmapLruCache extends LruCache<String, Bitmap> implements ImageCache {

    public BitmapLruCache(int sizeInBytes) {
        super(sizeInBytes);
    }

    @Override
    protected int sizeOf(String key, Bitmap bitmap) {
        int size = bitmap.getRowBytes() * bitmap.getHeight();
        return size;
    }

    public boolean contains(String key) {
        return get(key) != null;
    }

    public Bitmap getBitmap(String key) {
        Bitmap bitmap = get(key);
        return bitmap;
    }

    public void putBitmap(String url, Bitmap bitmap) {
        put(url, bitmap);
    }
}