/*
 * Copyright (c) 2013 Sony Mobile Communications AB.
 * All rights, including trade secret rights, reserved.
 */

/**
 * @file GaHelperSubscriber.java
 *
 * @author Erling Mårtensson (erling.martensson@sonymobile.com)
 */

package com.sonymobile.sonyselect.bi.gahelper;

import android.content.Context;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.Handler;
import android.provider.Settings;

public class GaHelperSubscriber extends ContentObserver {

    private static final String SOMC_GA_ENABLED_SETTING = "somc.google_analytics_enabled";

    private static final Uri GA_URI = Settings.System.getUriFor(SOMC_GA_ENABLED_SETTING);

    private static final String LOG_TAG = "GaHelper";

    private final Context mContext;

    private boolean mSubscribing = false;

    /**
     * Constructor
     *
     * @param context the context to use
     * @throws IllegalArgumentException if context is null
     */
    public GaHelperSubscriber(Context context) throws IllegalArgumentException {
        super(null);

        if (context == null) {
            throw new IllegalArgumentException("context is not allowed to be null");
        }

        mContext = context;
    }

    /**
     * Constructor
     *
     * @param context the context to use
     * @param handler the handler to use
     * @throws IllegalArgumentException if context is null
     */
    public GaHelperSubscriber(Context context, Handler handler) throws IllegalArgumentException {
        super(handler);

        if (context == null) {
            throw new IllegalArgumentException("context is not allowed to be null");
        }

        mContext = context;
    }

    /**
     * Subscribe to SOMC GA setting changes and act upon them. This method will
     * also set the AppOptOut according to the current SOMC GA setting value as
     * the initial state. This method and the associated
     * unsubscribeGaSettingChanges is not thread safe.
     *
     */
    public void subscribeGaSettingChanges() {
        GaHelperLog.d(LOG_TAG, "subscribeGaSettingChanges");

        // Read the current values as a starting point before subscribing
        GaHelper.readAndSetGaEnabled(mContext);

        if (!mSubscribing) {
            mContext.getContentResolver().registerContentObserver(GA_URI, false, this);
            mSubscribing = true;
        }
    }

    /**
     * Unsubscribe to SOMC GA setting changes. This method and the associated
     * subscribeGaSettingChanges is not thread safe.
     *
     */
    public void unsubscribeGaSettingChanges() {
        GaHelperLog.d(LOG_TAG, "unsubscribeGaSettingChanges");
        if (mSubscribing) {
            mContext.getContentResolver().unregisterContentObserver(this);
            mSubscribing = false;
        }
    }

    @Override
    public void onChange(boolean selfChange) {
        GaHelperLog.d(LOG_TAG, "onChange");

        GaHelper.readAndSetGaEnabled(mContext);
    }
}
