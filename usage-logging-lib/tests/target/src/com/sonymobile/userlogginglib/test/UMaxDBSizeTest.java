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
import android.test.AndroidTestCase;

import java.lang.reflect.Method;

public class UMaxDBSizeTest extends AndroidTestCase {

    SQLiteDatabase mDB;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        MockitoAnnotations.initMocks(this);

        Context context = getContext();
        DatabaseHelper databaseHelper = new DatabaseHelper(context);
        DatabaseHelper.setMaxSizeOfDBInBytes(5000);
        mDB = databaseHelper.getWritableDatabase();

        Method method = databaseHelper.getClass().getDeclaredMethod("reset", mDB.getClass());
        method.setAccessible(true);
        method.invoke(databaseHelper, mDB);
    }

    /**
     * Tests that the max size of the DB is honored.
     *
     */
    public void testThatDbMaxSizeIsHonored() {
        int res = 0;
        int i = 0;
        while (i < 100 && res != -1) {
            LogEntry logEntry = new LogEntry();
            logEntry.addKeyValuePair("test_key" + i, "test_value" + i);
            logEntry.addKeyValuePair("type", "type");
            res = LogEntryHelper.addLogEntry(mDB, logEntry);
        }
        assertEquals(res, -1);
        LogEntry logEntry = new LogEntry();
        logEntry.addKeyValuePair("test_key", "test_value");
        logEntry.addKeyValuePair("type", "type");
        res = LogEntryHelper.addLogEntry(mDB, logEntry);
        assertEquals(res, 1);
    }

}
