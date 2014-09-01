/*
 * Copyright (C) 2013 Sony Mobile Communications AB.
 * All rights, including trade secret rights, reserved.
 */

package com.sonymobile.userlogginglib.internal.db;

import com.sonymobile.userlogginglib.internal.Config;
import com.sonymobile.userlogginglib.util.Utils;

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

    // History of the database:
    // VERSION 1: Initial version
    // VERSION 2: Added session_id to LogEntryTable.
    // VERSION 3: Added session_id to SessionTable.
    // VERSION 4: Removed everything session related.
    // VERSION 5: Added timestamp as a field in LogEntryTable.
    private static final int VERSION = 5;

    // Default max size of database.
    private static final int DB_MAX_SIZE_IN_BYTES = 2 * 1024 * 1024;

    // Max size of database in bytes.
    private static int mDBMaxSizeInBytes = DB_MAX_SIZE_IN_BYTES;

    // The name of the database file stored in the file system.
    private static final String NAME = "logger.db";

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
        if (Config.DEBUG) {
            Log.d(Config.LOG_TAG, "Upgrading database from v." + oldVersion + " to v." + newVersion);
        }
        reset(database);
    }

    @Override
    public void onDowngrade(SQLiteDatabase database, int oldVersion, int newVersion) {
        if (Config.DEBUG) {
            Log.d(Config.LOG_TAG, "Downgrading database from v." + oldVersion + " to v."
                    + newVersion);
        }
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
            if (Config.DEBUG) {
                Log.d(Config.LOG_TAG, "SQLiteFullException, dropping all LogEntries.");
            }
            LogEntryHelper.deleteAllLogEntries(db);
            return -1;
        } catch (SQLiteException sQLiteException) {
            if (Config.DEBUG) {
                Log.e(Config.LOG_TAG, "SQLiteException: " + sQLiteException.getMessage(),
                        sQLiteException);
            }
            return -1;
        }
    }

    private void reset(SQLiteDatabase database) {
        try {
            database.beginTransaction();
            clearDatabase(database);
            setupDatabase(database);
            database.setTransactionSuccessful();
            if (Config.DEBUG) {
                Log.d(Config.LOG_TAG, "Setting maximum database size to: " + mDBMaxSizeInBytes);
            }
            database.setMaximumSize(mDBMaxSizeInBytes);
        } catch (SQLException e) {
            if (Config.DEBUG) {
                Log.d(Config.LOG_TAG, "IGNORING SQL EXCEPTION:", e);
            }
        } finally {
            database.endTransaction();
        }
    }

    private void setupDatabase(SQLiteDatabase database) throws SQLException {

        if (Config.DEBUG) {
            Log.d(Config.LOG_TAG, "CREATE TABLE " + LogEntryTable.NAME);
        }
        database.execSQL("CREATE TABLE " + LogEntryTable.NAME + " (" + //
                LogEntryTable.Columns.ID + " INTEGER PRIMARY KEY, " + //
                LogEntryTable.Columns.DATA + " TEXT)");
    }

    private void clearDatabase(SQLiteDatabase database) throws SQLException {
        String query = "SELECT type, name FROM sqlite_master WHERE type IN (?, ?, ?)";
        String[] types = {
                "table", "view", "trigger"
        };
        Cursor cursor = null;
        try {
            cursor = database.rawQuery(query, types);
            if (Config.DEBUG) {
                Log.d(Config.LOG_TAG, "CURSOR: Opening for clearing database.");
            }
            if (cursor != null && cursor.moveToFirst()) {
                do {
                    String type = cursor.getString(cursor.getColumnIndex("type"));
                    String name = cursor.getString(cursor.getColumnIndex("name"));
                    database.execSQL("DROP " + type + " IF EXISTS " + name);
                } while (cursor.moveToNext());
            }
        } catch (SQLException e) {
            if (Config.DEBUG) {
                Log.e(Config.LOG_TAG, "Failed clearing database. ", e);
            }
        } finally {
            if (Config.DEBUG) {
                Log.d(Config.LOG_TAG, "CURSOR: Closing after clearing database.");
            }
            Utils.closeSilently(cursor);
        }
    }
}
