/*
 * Copyright (C) 2013 Sony Mobile Communications AB.
 * All rights, including trade secret rights, reserved.
 */

package com.sonymobile.userlogginglib.util;

import com.sonymobile.userlogginglib.internal.Config;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.telephony.TelephonyManager;
import android.util.Log;

import java.util.Locale;

public class PhoneInfo {

    private final Context mContext;

    public PhoneInfo(Context context) {
        mContext = context;
    }

    public String getMcc() {
        TelephonyManager tm = (TelephonyManager)mContext
                .getSystemService(Context.TELEPHONY_SERVICE);
        String mccmnc = tm.getSimOperator();
        if (mccmnc != null && mccmnc.length() > 3) {
            return mccmnc.substring(0, 3);
        }
        return null;
    }

    public String getMnc() {
        TelephonyManager tm = (TelephonyManager)mContext
                .getSystemService(Context.TELEPHONY_SERVICE);
        String mccmnc = tm.getSimOperator();
        if (mccmnc != null && mccmnc.length() > 3) {
            return mccmnc.substring(3);
        }
        return null;
    }

    public String getLocale() {
        return Locale.getDefault().toString();
    }

    public boolean isOnWifi() {
        ConnectivityManager connectivityManager = (ConnectivityManager)mContext
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        return networkInfo.isConnected();
    }

    public boolean isRoaming() {
        TelephonyManager telephonyManager = (TelephonyManager)mContext
                .getSystemService(Context.TELEPHONY_SERVICE);
        boolean isRoaming = telephonyManager.isNetworkRoaming();
        if (isRoaming) {
            if (Config.DEBUG) {
                Log.d(Config.LOG_TAG, "Device is roaming.");
            }
        }
        return isRoaming;
    }

}
