/*
 * Copyright (c) 2013 Sony Mobile Communications AB.
 * All rights, including trade secret rights, reserved.
 */

package com.sonymobile.userlogginglib.test;

import com.sonymobile.userlogginglib.util.SharedPreferencesUtil;

import android.test.AndroidTestCase;

public class USharedPreferencesUtilTest extends AndroidTestCase {

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        SharedPreferencesUtil.clearPreferences(getContext());
    }

    public void testAddSharedPreferencesSuccess() {
        SharedPreferencesUtil.setAPIKey(getContext(), "apiKey");
        SharedPreferencesUtil.setRootUrl(getContext(), "rootUrl");

        assertEquals("apiKey", SharedPreferencesUtil.getAPIKey(getContext()));
        assertEquals("rootUrl", SharedPreferencesUtil.getRootUrl(getContext()));
    }

    public void testAddSharedPreferencesNullFail() {
        SharedPreferencesUtil.setAPIKey(getContext(), null);
        SharedPreferencesUtil.setRootUrl(getContext(), null);

        assertEquals(null, SharedPreferencesUtil.getAPIKey(getContext()));
        assertEquals(null, SharedPreferencesUtil.getRootUrl(getContext()));
    }

    public void testAddSharedPreferencesEmptyFail() {
        SharedPreferencesUtil.setAPIKey(getContext(), "");
        SharedPreferencesUtil.setRootUrl(getContext(), "");

        assertEquals(null, SharedPreferencesUtil.getAPIKey(getContext()));
        assertEquals(null, SharedPreferencesUtil.getRootUrl(getContext()));
    }

    public void testAddSharedPreferencesMissingFail() {
        assertEquals(null, SharedPreferencesUtil.getAPIKey(getContext()));
        assertEquals(null, SharedPreferencesUtil.getRootUrl(getContext()));
    }

    public void testGetLastSyncTime() {
        long now = System.currentTimeMillis();
        SharedPreferencesUtil.setLastSyncTime(mContext, now);
        long ts = SharedPreferencesUtil.getLastSyncTime(mContext);
        assertTrue(now < ts + 10000 && now > ts - 10000);
    }

    public void testHasLastSyncTime() {
        assertFalse(SharedPreferencesUtil.hasLastSyncTime(mContext));
        SharedPreferencesUtil.setLastSyncTime(mContext, 0l);
        assertTrue(SharedPreferencesUtil.hasLastSyncTime(mContext));
    }

}
