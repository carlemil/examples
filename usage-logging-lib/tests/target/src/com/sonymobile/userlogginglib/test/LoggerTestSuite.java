/*
 * Copyright (C) 2013 Sony Mobile Communications AB.
 * All rights, including trade secret rights, reserved.
 */

package com.sonymobile.userlogginglib.test;

import android.app.Instrumentation;
import android.test.InstrumentationTestSuite;

public class LoggerTestSuite extends InstrumentationTestSuite {

    public LoggerTestSuite(Instrumentation instrumentation) {
        super(instrumentation);

        // This is needed for dexmaker to work properly on Android 4.3+
        System.setProperty("dexmaker.dexcache", instrumentation.getTargetContext().getCacheDir()
                .getPath());

        // This is needed for Mockito to work properly
        Thread.currentThread().setContextClassLoader(getClass().getClassLoader());

        addTestSuite(UDispatcherTest.class);
        addTestSuite(ULogEntryHelperTest.class);
        addTestSuite(ULoggerIntentServiceHelperTest.class);
        addTestSuite(ULoggerSchedulerTest.class);
        addTestSuite(ULoggerTest.class);
        addTestSuite(UMaxDBSizeTest.class);
        addTestSuite(USharedPreferencesUtilTest.class);
        addTestSuite(USqliteLogDBTest.class);
    }

}
