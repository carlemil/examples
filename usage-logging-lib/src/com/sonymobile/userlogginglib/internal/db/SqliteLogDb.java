/*
 * Copyright (C) 2013 Sony Mobile Communications AB.
 * All rights, including trade secret rights, reserved.
 */

package com.sonymobile.userlogginglib.internal.db;

import com.sonymobile.userlogginglib.internal.Config;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class SqliteLogDb implements LogDb {

    private final Context mContext;

    private SQLiteDatabase mDb;

    public SqliteLogDb(Context context) {
        mContext = context;
    }

    @Override
    public void open() {
        mDb = new DatabaseHelper(mContext).getWritableDatabase();
    }

    @Override
    public void close() {
        mDb.close();
        mDb = null;
    }

    @Override
    public boolean isEmpty() {
        return LogEntryHelper.isEmpty(mDb);
    }

    @Override
    public void dropAll() {
        if (Config.DEBUG) {
            Log.d(Config.LOG_TAG, "Delete all LogEntries.");
        }
        LogEntryHelper.deleteAllLogEntries(mDb);
    }

}
