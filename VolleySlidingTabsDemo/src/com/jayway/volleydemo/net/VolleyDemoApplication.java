package com.jayway.volleydemo.net;

import com.newrelic.agent.android.NewRelic;

import android.app.ActivityManager;
import android.app.Application;
import android.content.Context;
import android.util.Log;

/**
 * Created by erbsman on 7/25/13.
 */
public class VolleyDemoApplication extends Application {

    private static final String TAG = VolleyDemoApplication.class.getCanonicalName();

    private static VolleyDemoApplication sInstance;

    public static VolleyDemoApplication get() {
        return sInstance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        sInstance = this;

        int memoryClass = ((ActivityManager) getSystemService(Context.ACTIVITY_SERVICE))
                .getMemoryClass();
        // use about 1/4th of the available memory for caching images.
        int cacheSize = memoryClass * 1024 * 1024 / 4;
        Log.d(TAG, "Setting cache size to: " + cacheSize + " bytes.");
        VolleySingelton.initialize(getApplicationContext(), cacheSize);

        NewRelic.withApplicationToken(
                "AA89a88d67013dfd1640fa27b5a4068eab5dfeaf8d"
                ).start(this.getApplicationContext());
    }

}
