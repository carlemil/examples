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

package com.sonymobile.sonyselect.application;

import android.app.ActivityManager;
import android.app.Application;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Resources;
import android.os.StrictMode;
import android.text.format.DateUtils;

import com.google.analytics.tracking.android.EasyTracker;
import com.sonymobile.sonyselect.R;
import com.sonymobile.sonyselect.activities.LaunchActivity;
import com.sonymobile.sonyselect.bi.AnalyticsDispatcher;
import com.sonymobile.sonyselect.bi.Tracker;
import com.sonymobile.sonyselect.internal.util.Log;
import com.sonymobile.sonyselect.net.VolleySingelton;

public class SonySelectApplication extends Application {

    private static final String LOG_TAG = SonySelectApplication.class.getName();

    public static final String AUTHORITY = "com.sonymobile.sonyselect.provider";

    public static final String CONTAINER_ID = "GTM-WWJMRL";

    private static SonySelectApplication sInstance;

    private boolean isTablet;

    private String mVersionName;

    public static SonySelectApplication get() {
        return sInstance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        setSonySelectApplication(this);
        StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder().detectAll().penaltyLog().build());
        StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder().detectAll().setClassInstanceLimit(LaunchActivity.class, 2).penaltyLog().build());

        Resources resources = getResources();
        isTablet = resources.getBoolean(R.bool.is_tablet);

        try {
            mVersionName = getPackageManager().getPackageInfo(getPackageName(), 0).versionName;
        } catch (NameNotFoundException e) {
            mVersionName = "NameNotFoundException";
        }
        Log.d(LOG_TAG, "VersionName: " + mVersionName);

        setupViewTracker();

        int cacheSize = (((ActivityManager) getSystemService(Context.ACTIVITY_SERVICE))
                .getMemoryClass() * 1024) / 4;
        Log.d(LOG_TAG, "Volley in-mem-cache-size: " + cacheSize + " Kb.");
        VolleySingelton.initialize(getApplicationContext(), cacheSize);
    }

    public static void setSonySelectApplication(SonySelectApplication instance) {
        sInstance = instance;
    }

    public static boolean isTablet() {
        return get().isTablet;
    }

    private void setupViewTracker() {
        com.google.analytics.tracking.android.Tracker tracker = EasyTracker
                .getInstance(getApplicationContext());
        new Tracker(getApplicationContext(), (EasyTracker) tracker, tracker);
        long dispatchIntervalMillis = getResources().getInteger(
                R.integer.google_analytics_dispatch_period)
                * DateUtils.SECOND_IN_MILLIS;

        // Check if we're in debug mode and if so set the dispatch period to
        // once each ten seconds.
        try {
            PackageInfo packageInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
            int flags = packageInfo.applicationInfo.flags;
            if ((flags & ApplicationInfo.FLAG_DEBUGGABLE) != 0) {
                dispatchIntervalMillis = 10 * DateUtils.SECOND_IN_MILLIS;
                Log.d(LOG_TAG,
                        "Since we're in debug mode the analytics dispatch is performed more often (once every '"
                                + dispatchIntervalMillis + "' millisec).");
            }
        } catch (PackageManager.NameNotFoundException e) {
            Log.d(LOG_TAG,
                    "PackageManager.NameNotFoundException caught during setup of analytics-tracker.",
                    e);
        }

        AnalyticsDispatcher.startDispatcher(this, dispatchIntervalMillis);
    }

    public static String getVersionName() {
        return get().mVersionName;
    }
}
