/*
 * Copyright (C) 2013 Sony Mobile Communications AB.
 * All rights, including trade secret rights, reserved.
 */

package com.sonymobile.userlogginglib.test;

import static org.mockito.Mockito.when;

import com.sonymobile.userlogginglib.internal.db.LogDb;
import com.sonymobile.userlogginglib.service.LoggerScheduler;
import com.sonymobile.userlogginglib.service.LoggerScheduler.Action;
import com.sonymobile.userlogginglib.util.PhoneInfo;
import com.sonymobile.userlogginglib.util.SharedPreferencesUtil;

import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import android.os.StrictMode;
import android.test.AndroidTestCase;

public class ULoggerSchedulerTest extends AndroidTestCase {

    @Mock
    private LogDb logDb;

    @Mock
    private PhoneInfo phoneInfo;

    private LoggerScheduler scheduler;

    @Override
    protected void setUp() throws Exception {
        super.setUp();

        // This is needed for Mockito to work properly
        Thread.currentThread().setContextClassLoader(getClass().getClassLoader());

        MockitoAnnotations.initMocks(this);

        StrictMode.ThreadPolicy threadPolicy = new StrictMode.ThreadPolicy.Builder().detectAll()
                .penaltyLog().penaltyDeath().build();
        StrictMode.setThreadPolicy(threadPolicy);

        StrictMode.VmPolicy vmPolicy = new StrictMode.VmPolicy.Builder().detectAll().penaltyLog()
                .penaltyDeath().build();
        StrictMode.setVmPolicy(vmPolicy);

        scheduler = new LoggerScheduler(getContext(), logDb, phoneInfo, null);
    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
    }

    public void testScheduleCallWithEmptyDbDoesNothing() {
        when(logDb.isEmpty()).thenReturn(true);

        Action result = scheduler.getAction(true);

        assertEquals(Action.NOOP, result);
    }

    public void testTimeStampFromTheFuture() {
        SharedPreferencesUtil.setLastSyncTime(mContext, System.currentTimeMillis() + 1000);

        Action result = scheduler.getAction(true);

        assertEquals(Action.CLEAR_DB, result);
    }

    public void testTooEarlyToUploadOnWifi() {
        long tenHoursAgo = System.currentTimeMillis() - 10 * 60 * 60 * 1000;

        SharedPreferencesUtil.setLastSyncTime(mContext, tenHoursAgo);

        Action result = scheduler.getAction(true);

        assertEquals(Action.SCHEDULE, result);
    }

    public void testTimeToUploadWhenOnWifi() {
        long thirtyHoursAgo = System.currentTimeMillis() - 30 * 60 * 60 * 1000;

        SharedPreferencesUtil.setLastSyncTime(mContext, thirtyHoursAgo);

        when(phoneInfo.isOnWifi()).thenReturn(true);

        Action result = scheduler.getAction(true);

        assertEquals(Action.UPLOAD, result);
    }

    public void testTimeToUploadWhenNotOnWifi() {
        long thirtyHoursAgo = System.currentTimeMillis() - 30 * 60 * 60 * 1000;

        SharedPreferencesUtil.setLastSyncTime(mContext, thirtyHoursAgo);

        when(phoneInfo.isOnWifi()).thenReturn(false);

        Action result = scheduler.getAction(true);

        assertEquals(Action.SCHEDULE, result);
    }

    public void testTimeToUploadOnMobileNetworkAndNotRoaming() {
        long tenDaysAgo = System.currentTimeMillis() - 10 * 24 * 60 * 60 * 1000;

        SharedPreferencesUtil.setLastSyncTime(mContext, tenDaysAgo);

        when(phoneInfo.isOnWifi()).thenReturn(false);
        when(phoneInfo.isRoaming()).thenReturn(false);

        Action result = scheduler.getAction(true);

        assertEquals(Action.UPLOAD, result);
    }

    public void testTimeToUploadOnMobileNetworkWhenRoaming() {
        long tenDaysAgo = System.currentTimeMillis() - 10 * 24 * 60 * 60 * 1000;

        SharedPreferencesUtil.setLastSyncTime(mContext, tenDaysAgo);

        when(phoneInfo.isOnWifi()).thenReturn(false);
        when(phoneInfo.isRoaming()).thenReturn(true);

        Action result = scheduler.getAction(true);

        assertEquals(Action.CLEAR_DB, result);
    }

    public void testIsLastSyncTimeIsInTheFuture() {
        long now = System.currentTimeMillis();
        SharedPreferencesUtil.setLastSyncTime(mContext, now + 1000);
        assertTrue(scheduler.isLastSyncTimeInTheFuture(mContext));
    }

}
