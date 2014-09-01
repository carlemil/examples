/*
 * Copyright (c) 2013 Sony Mobile Communications AB.
 * All rights, including trade secret rights, reserved.
 */

package com.sonymobile.userlogginglib.test;

import com.sonymobile.userlogginglib.internal.LogEntry;
import com.sonymobile.userlogginglib.internal.db.DatabaseHelper;
import com.sonymobile.userlogginglib.internal.db.LogEntryHelper;
import org.mockito.MockitoAnnotations;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.os.StrictMode;
import android.test.AndroidTestCase;

import java.lang.reflect.Method;

public class ULogEntryHelperTest extends AndroidTestCase {

    SQLiteDatabase mDB;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        MockitoAnnotations.initMocks(this);

        Context context = getContext();
        DatabaseHelper databaseHelper = new DatabaseHelper(context);
        mDB = databaseHelper.getWritableDatabase();

        Method method = databaseHelper.getClass().getDeclaredMethod("reset", mDB.getClass());
        method.setAccessible(true);
        method.invoke(databaseHelper, mDB);

        StrictMode.ThreadPolicy threadPolicy = new StrictMode.ThreadPolicy.Builder().detectAll()
                .penaltyLog().penaltyDeath().build();
        StrictMode.setThreadPolicy(threadPolicy);

        StrictMode.VmPolicy vmPolicy = new StrictMode.VmPolicy.Builder().detectAll().penaltyLog()
                .penaltyDeath().build();
        StrictMode.setVmPolicy(vmPolicy);

    }

    public void testAddLogEntrySuccess() {

        LogEntry logEntry = new LogEntry();
        logEntry.addKeyValuePair("type", "test-entry");
        logEntry.addKeyValuePair("test_key", "test_value");
        int row = LogEntryHelper.addLogEntry(mDB, logEntry);

        assertTrue(row >= 0);
    }

    public void testAddMultipleLogEntrySuccess() {

        LogEntry logEntry = new LogEntry();
        logEntry.addKeyValuePair("type", "test-entry");
        logEntry.addKeyValuePair("test_key", "test_value");

        LogEntryHelper.addLogEntry(mDB, logEntry);

        logEntry = new LogEntry();
        logEntry.addKeyValuePair("type", "test-entry");
        logEntry.addKeyValuePair("test_key", "test_value");
        LogEntryHelper.addLogEntry(mDB, logEntry);

        logEntry = new LogEntry();
        logEntry.addKeyValuePair("type", "test-entry");
        logEntry.addKeyValuePair("test_key", "test_value");
        int row = LogEntryHelper.addLogEntry(mDB, logEntry);

        assertTrue(row == 3);
    }

    public void testAddLogEntryFailEmpty() {

        LogEntry logEntry = new LogEntry();
        int row = LogEntryHelper.addLogEntry(mDB, logEntry);

        assertTrue(row == -1);
    }

}
