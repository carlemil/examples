/*
 * Copyright (c) 2013 Sony Mobile Communications AB.
 * All rights, including trade secret rights, reserved.
 */

package com.sonymobile.userlogginglib.test;

import com.sonymobile.userlogginglib.internal.LogEntry;
import com.sonymobile.userlogginglib.internal.db.DatabaseHelper;
import com.sonymobile.userlogginglib.internal.db.LogEntryHelper;
import com.sonymobile.userlogginglib.internal.db.SqliteLogDb;
import org.mockito.MockitoAnnotations;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.os.StrictMode;
import android.test.AndroidTestCase;
import java.lang.reflect.Method;

public class USqliteLogDBTest extends AndroidTestCase {

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

    public void testOpenCloseSuccess() {
        SqliteLogDb logdb = new SqliteLogDb(getContext());
        logdb.open();
        logdb.close();
    }

    public void testIsEmptySuccess() {
        SqliteLogDb logdb = new SqliteLogDb(getContext());
        logdb.open();
        assertTrue(logdb.isEmpty());
        logdb.close();
    }

    public void testDropAll() {

        LogEntry logEntry = new LogEntry();
        logEntry.addKeyValuePair("key", "value");
        logEntry.addKeyValuePair("type", "type");
        LogEntryHelper.addLogEntry(mDB, logEntry);

        SqliteLogDb logdb = new SqliteLogDb(getContext());

        logdb.open();
        assertFalse(logdb.isEmpty());
        logdb.dropAll();
        assertTrue(logdb.isEmpty());
        logdb.close();
    }

}
