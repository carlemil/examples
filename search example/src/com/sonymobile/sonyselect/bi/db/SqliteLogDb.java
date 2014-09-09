/*
 * Copyright (C) 2013 Sony Mobile Communications AB.
 * All rights, including trade secret rights, reserved.
 */

package com.sonymobile.sonyselect.bi.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

/**
 * Proxy for the database to enable dependency injection/testing.
 */
public class SqliteLogDb implements GetAppsDb {

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
        return GetAppsHelper.isEmpty(mDb);
    }

    @Override
    public void insert(String packageName, long timestamp) {
        GetAppsHelper.insert(mDb, packageName, timestamp);
    }

    @Override
    public Long getTimestampForPackageName(String packageName) {
        Long c = GetAppsHelper.getTimestampForPackageName(mDb, packageName);
        return c;
    }

    @Override
    public int deleteByPackageName(String packageName) {
        int ret = GetAppsHelper.deleteByPackageName(mDb, packageName);
        return ret;
    }

    @Override
    public int deleteAllOldRows(long oldTimestamp) {
        int ret = GetAppsHelper.deleteAllOldRows(mDb, oldTimestamp);
        return ret;
    }

}
