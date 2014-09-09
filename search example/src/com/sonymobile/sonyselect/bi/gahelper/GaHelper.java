/*
 * Copyright (c) 2013 Sony Mobile Communications AB.
 * All rights, including trade secret rights, reserved.
 */

/**
 * @file GaHelper.java
 *
 * @author Erling Mårtensson (erling.martensson@sonymobile.com)
 */

package com.sonymobile.sonyselect.bi.gahelper;

import android.content.Context;
import android.provider.Settings;

import com.google.analytics.tracking.android.GoogleAnalytics;

/* NOTE: This is a shared source. Any changes to this file should be
 * done in the vendor/semc/frameworks/google-analytics git!
 */

public class GaHelper {

    private static final String SOMC_GA_ENABLED_SETTING = "somc.google_analytics_enabled";

    private static final String LOG_TAG = "GaHelper";

    /**
     * Constructor
     *
     * @throws IllegalArgumentException if context is null
     */
    private GaHelper() {
    }

    /**
     * Read SOMC GA setting and enable/disable GA directly
     * @param context the context to use
     */
    static public void readAndSetGaEnabled(Context context) {
        int defaultValueEnabled = 1;
        boolean gaEnabled = Settings.System.getInt(context.getContentResolver(),
                SOMC_GA_ENABLED_SETTING, defaultValueEnabled) == defaultValueEnabled;

        GaHelperLog.d(LOG_TAG, "somc.google_analytics_enabled=" + gaEnabled);

        // If GA is Enabled -> OptOut(false)
        // If GA is Disabled -> OptOut(true)
        GoogleAnalytics.getInstance(context).setAppOptOut(!gaEnabled);
    }
}
