/*
 * Copyright (C) 2013 Sony Mobile Communications AB.
 * All rights, including trade secret rights, reserved.
 */

package com.sonymobile.sonyselect.bi.db;

import com.sonymobile.sonyselect.internal.util.Utils;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteFullException;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * This is helper class to handle the {@link SQLiteDatabase} for the Logger.
 */
public class DatabaseHelper extends SQLiteOpenHelper {

    protected static final String LOG_TAG = DatabaseHelper.class.getCanonicalName();

    // History of the database:
    // VERSION 1: Initial version
    private static final int VERSION = 1;

    // Default max size of database.
    private static final int DB_MAX_SIZE_IN_BYTES = 2 * 1024 * 1024;

    // Max size of database in bytes.
    private static int mDBMaxSizeInBytes = DB_MAX_SIZE_IN_BYTES;

    // The name of the database file stored in the file system.
    private static final String NAME = "AppGetAndInstallLogging.db";

    private SQLiteDatabase mSQLiteDatabase;

    public DatabaseHelper(Context context) {
        super(context, NAME, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase database) {
        reset(database);
    }

    @Override
    public void onUpgrade(SQLiteDatabase database, int oldVersion, int newVersion) {
        Log.d(LOG_TAG, "Upgrading database from v." + oldVersion + " to v." + newVersion);

        reset(database);
    }

    @Override
    public void onDowngrade(SQLiteDatabase database, int oldVersion, int newVersion) {
        Log.d(LOG_TAG, "Downgrading database from v." + oldVersion + " to v." + newVersion);

        reset(database);
    }

    @Override
    public SQLiteDatabase getWritableDatabase() {
        synchronized (DatabaseHelper.class) {
            if (mSQLiteDatabase == null) {
                mSQLiteDatabase = super.getWritableDatabase();
            }
        }
        return mSQLiteDatabase;
    };

    /**
     * Set the maximum size of the database in bytes.
     *
     * @param mDBMaxSizeInBytes the size.
     */
    public static void setMaxSizeOfDBInBytes(int mDBMaxSizeInBytes) {
        DatabaseHelper.mDBMaxSizeInBytes = mDBMaxSizeInBytes;
    }

    /**
     * Inserts something in the database
     *
     * @param db the database to insert data in.
     * @param tableName the name of the table to insert data in.
     * @param values the data to insert in the table.
     * @return the row ID of the newly inserted row, or -1 if an error occurred.
     */
    protected static long insert(SQLiteDatabase db, String tableName, ContentValues values) {
        try {
            // when this fails because it exists it throws
            // android.database.sqlite.SQLiteConstraintException: error code 19:
            // constraint failed;
            return db.insertOrThrow(tableName, null, values);
        } catch (SQLiteFullException sQLiteFullException) {
            Log.d(LOG_TAG, "SQLiteFullException, dropping all Entries.");

            GetAppsHelper.deleteAllRows(db);
            return -1;
        } catch (SQLiteException sQLiteException) {
            Log.e(LOG_TAG, "SQLiteException: " + sQLiteException.getMessage(), sQLiteException);
            return -1;
        }
    }

    private void reset(SQLiteDatabase database) {
        try {
            database.beginTransaction();
            clearDatabase(database);
            setupDatabase(database);
            database.setTransactionSuccessful();
            Log.d(LOG_TAG, "Setting maximum database size to: " + mDBMaxSizeInBytes);

            database.setMaximumSize(mDBMaxSizeInBytes);
        } catch (SQLException e) {
            Log.d(LOG_TAG, "IGNORING SQL EXCEPTION:", e);

        } finally {
            database.endTransaction();
        }
    }

    private void setupDatabase(SQLiteDatabase database) throws SQLException {

        Log.d(LOG_TAG, "CREATE TABLE " + GetAppsTable.NAME);

        database.execSQL("CREATE TABLE " + GetAppsTable.NAME + " (" + //
                GetAppsTable.Columns.ID + " INTEGER PRIMARY KEY, " + //
                GetAppsTable.Columns.TIMESTAMP + " TIMESTAMP DEFAULT CURRENT_TIMESTAMP, " + //
                GetAppsTable.Columns.PACKAGE_NAME + " TEXT, " + //
                "UNIQUE(" + GetAppsTable.Columns.PACKAGE_NAME + //
                ") ON CONFLICT REPLACE)");
    }

    private void clearDatabase(SQLiteDatabase database) throws SQLException {
        String query = "SELECT type, name FROM sqlite_master WHERE type IN (?, ?, ?)";
        String[] types = {
                "table", "view", "trigger"
        };
        Cursor cursor = null;
        try {
            cursor = database.rawQuery(query, types);
            Log.d(LOG_TAG, "CURSOR: Opening for clearing database.");

            if (cursor != null && cursor.moveToFirst()) {
                do {
                    String type = cursor.getString(cursor.getColumnIndex("type"));
                    String name = cursor.getString(cursor.getColumnIndex("name"));
                    database.execSQL("DROP " + type + " IF EXISTS " + name);
                } while (cursor.moveToNext());
            }
        } catch (SQLException e) {
            Log.e(LOG_TAG, "Failed clearing database. ", e);

        } finally {
            Log.d(LOG_TAG, "CURSOR: Closing after clearing database.");

            Utils.closeSilently(cursor);
        }
    }
}
