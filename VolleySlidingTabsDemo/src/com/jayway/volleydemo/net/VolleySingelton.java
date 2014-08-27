
package com.jayway.volleydemo.net;

import com.android.volley.Network;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.BasicNetwork;
import com.android.volley.toolbox.DiskBasedCache;
import com.android.volley.toolbox.HttpClientStack;
import com.android.volley.toolbox.HttpStack;
import com.android.volley.toolbox.HurlStack;
import com.android.volley.toolbox.ImageLoader;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Bitmap;
import android.net.http.AndroidHttpClient;
import android.os.Build;
import java.io.File;

public class VolleySingelton {

    /** Default on-disk cache directory. */
    private static final String DEFAULT_CACHE_DIR = "volley";

    /** Make the disk cache default size 50 Mb */
    private static final int CACHE_SIZE = 50 * 1024 * 1024;

    private static VolleySingelton mInstance = null;

    private RequestQueue mRequestQueue;

    private ImageLoader mImageLoader;

    private BitmapMemCache mCache;

    private DemoApi api;

    private VolleySingelton(Context context, final int cacheSize) {
        mRequestQueue = newRequestQueue(context);
        mCache = new BitmapMemCache(cacheSize);
        mImageLoader = new ImageLoader(this.mRequestQueue, new ImageLoader.ImageCache() {

            public void putBitmap(String url, Bitmap bitmap) {
                mCache.put(url, bitmap);
            }

            public Bitmap getBitmap(String url) {
                return mCache.get(url);
            }
        });
        api = new DemoApi(mRequestQueue);
    }

    public DemoApi getApi(){
        return api;
    }

    public static void initialize(Context context, int cacheSize) {
        if (mInstance == null) {
            mInstance = new VolleySingelton(context, cacheSize);
        }
    }

    public static VolleySingelton getInstance() {
        if (mInstance == null) {
            throw new IllegalStateException(
                    "VolleySingelton was not initialized befor getInstance was called.");
        }
        return mInstance;
    }

    public RequestQueue getRequestQueue() {
        return this.mRequestQueue;
    }

    public ImageLoader getImageLoader() {
        return this.mImageLoader;
    }

    /**
     * Creates a default instance of the Volley worker pool and calls
     * {@link RequestQueue#start()} on it.
     *
     * @param context A {@link Context} to use for creating the cache dir.
     * @return A started {@link RequestQueue} instance.
     */
    public static RequestQueue newRequestQueue(Context context) {
        File cacheDir = new File(context.getCacheDir(), DEFAULT_CACHE_DIR);
        String userAgent = "volley/0";
        try {
            String packageName = context.getPackageName();
            PackageInfo info = context.getPackageManager().getPackageInfo(packageName, 0);
            userAgent = packageName + "/" + info.versionCode;
        } catch (NameNotFoundException e) {
        }

        HttpStack stack;
        if (Build.VERSION.SDK_INT >= 9) {
            stack = new HurlStack();
        } else {
            // Prior to Gingerbread, HttpUrlConnection was unreliable. See:
            // http://android-developers.blogspot.com/2011/09/androids-http-clients.html
            stack = new HttpClientStack(AndroidHttpClient.newInstance(userAgent));
        }

        Network network = new BasicNetwork(stack);

        RequestQueue queue = new RequestQueue(new DiskBasedCache(cacheDir, CACHE_SIZE), network);
        queue.start();

        return queue;
    }

}
