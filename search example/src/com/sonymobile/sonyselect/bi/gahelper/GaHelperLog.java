/*
 * Copyright (c) 2014 Sony Mobile Communications AB.
 * All rights, including trade secret rights, reserved.
 */

/**
 * @file GaLog.java
 *
 * @author Erling Mårtensson (erling.martensson@sonymobile.com)
 */

package com.sonymobile.sonyselect.bi.gahelper;

public class GaHelperLog {

    private static volatile boolean mEnabled = false;
    private static volatile Object mLock = new Object();

    private GaHelperLog() {
    }

    public static void enable(boolean enable) {
        synchronized (mLock) {
            mEnabled = enable;
        }
    }

    public static int d(String tag, String msg) {

        synchronized (mLock) {
            if (mEnabled) {
                return android.util.Log.d(tag, msg);
            } else {
                return 0;
            }
        }
    }

    public static int e(String tag, String msg) {

        synchronized (mLock) {
            if (mEnabled) {
                return android.util.Log.e(tag, msg);
            } else {
                return 0;
            }
        }
    }
};
