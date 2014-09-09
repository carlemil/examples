/*
 * Copyright (C) 2013 Sony Mobile Communications AB.
 * All rights, including trade secret rights, reserved.
 */

package com.sonymobile.sonyselect.bi.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.util.Log;

/**
 * This class acts as a glue layer between the {@link SQLiteDatabase} and the
 * rest of the app when manipulating GetApps data.
 */
public class GetAppsHelper extends DatabaseHelper {

    private GetAppsHelper(Context context) {
        super(context);
    }

    /**
     * Returns true if the {@link LogEntryTable} is empty.
     *
     * @param db the {@link SQLiteDatabase} to insert data in.
     * @return true if the {@link LogEntryTable} is empty
     */
    public static boolean isEmpty(SQLiteDatabase db) {
        boolean isEmpty = false;
        Cursor cursor = null;
        try {
            cursor = db.rawQuery("SELECT count(*) FROM " + GetAppsTable.NAME, null);
            if (cursor.moveToFirst()) {
                isEmpty = cursor.getInt(0) == 0;
            }
        } catch (SQLiteException sQLiteException) {
            Log.w(LOG_TAG, "SQLiteException: " + sQLiteException.getMessage(), sQLiteException);

        }
        com.sonymobile.sonyselect.internal.util.Utils.closeSilently(cursor);
        return isEmpty;
    }

    /**
     * Adds a package name and timestamp to the {@link LogEntryTable}.
     *
     * @param db the database to insert data into.
     * @param packageName the name of the package.
     * @param timestamp the time of the insert operation.
     * @return the row ID of the newly inserted row, or -1 if an error occurred.
     */
    public static int insert(SQLiteDatabase db, String packageName, long timeStamp) {
        int result = -1;

        ContentValues values = new ContentValues();
        values.put(GetAppsTable.Columns.PACKAGE_NAME, packageName);
        values.put(GetAppsTable.Columns.TIMESTAMP, timeStamp);

        try {
            result = (int) insert(db, GetAppsTable.NAME, values);
        } catch (SQLiteException sQLiteException) {
            Log.w(LOG_TAG, "SQLiteException: " + sQLiteException.getMessage(), sQLiteException);

        }
        return result;
    }

    /**
     * Return a timestamp if any package name is found.
     *
     * @param db the database to search for package name.
     * @param packageName the package name to search for.
     * @return the timestamp.
     */
    public static Long getTimestampForPackageName(SQLiteDatabase db, String packageName) {
        Cursor c = null;
        try {
            c = db.query(GetAppsTable.NAME, null, GetAppsTable.Columns.PACKAGE_NAME + "= ?",
                    new String[] {
                        packageName
                    }, null, null, null);
        } catch (SQLiteException sQLiteException) {
            Log.w(LOG_TAG, "SQLiteException: " + sQLiteException.getMessage(), sQLiteException);
        }
        Long timestamp = null;
        if (c != null && c.moveToFirst()) {
            int columnIndex = c.getColumnIndex(GetAppsTable.Columns.TIMESTAMP);
            timestamp = Long.valueOf(c.getLong(columnIndex));
        }
        c.close();
        return timestamp;
    }

    /**
     * Return all rows from the {@link LogEntryTable}.
     *
     * @param db the database containing the {@link LogEntryTable}.
     * @return a {@link Cursor}
     */
    public static Cursor getAllRows(SQLiteDatabase db) {
        Cursor cursor = null;
        try {
            cursor = db.query(GetAppsTable.NAME, null, null, null, null, null, null);
        } catch (SQLiteException sQLiteException) {
            Log.w(LOG_TAG, "SQLiteException: " + sQLiteException.getMessage(), sQLiteException);
        }
        return cursor;
    }

    /**
     * Deletes a row (with _ID==id) in the {@link GetAppsTable}.
     *
     * @param db the {@link SQLiteDatabase} to delete a row from.
     * @param id the id of the row to delete.
     * @return 1 if the delete was successful, 0 if not.
     */
    public static int deleteByID(SQLiteDatabase db, long id) {
        int res = 0;
        try {
            res = db.delete(GetAppsTable.NAME, GetAppsTable.Columns.ID + " = " + id, null);
        } catch (SQLiteException sQLiteException) {
            Log.w(LOG_TAG, "SQLiteException: " + sQLiteException.getMessage(), sQLiteException);
        }
        return res;
    }

    /**
     * Deletes ALL rows from the {@link GetAppsTable} table in the
     * {@link SQLiteDatabase}.
     *
     * @param db the {@link SQLiteDatabase} to delete a row from.
     * @return 0 if successful.
     */
    public static int deleteAllRows(SQLiteDatabase db) {
        int res = 0;
        try {
            res = db.delete(GetAppsTable.NAME, null, null);
        } catch (SQLiteException sQLiteException) {
            Log.w(LOG_TAG, "SQLiteException: " + sQLiteException.getMessage(), sQLiteException);
        }
        return res;
    }

    /**
     * Deletes ALL OLD rows from the {@link GetAppsTable} table in the
     * {@link SQLiteDatabase}. A row is OLD if its timestamp is older than
     * oldTimestamp.
     *
     * @param db the {@link SQLiteDatabase} to delete a row from.
     * @return the number of rows deleted.
     */
    public static int deleteAllOldRows(SQLiteDatabase db, long oldTimestamp) {
        int res = 0;

        try {
            res = db.delete(GetAppsTable.NAME, GetAppsTable.Columns.TIMESTAMP + "<?", new String[] {
                Long.toString(oldTimestamp)
            });
        } catch (SQLiteException sQLiteException) {
            Log.w(LOG_TAG, "SQLiteException: " + sQLiteException.getMessage(), sQLiteException);
        }
        return res;
    }

    /**
     * Delete all rows matching packageName.
     *
     * @param db the {@link SQLiteDatabase} to delete a row from.
     * @param packageName the string to match rows against, if match, delete the
     *            row.
     * @return the number of rows deleted.
     */
    public static int deleteByPackageName(SQLiteDatabase db, String packageName) {
        int res = 0;

        try {
            res = db.delete(GetAppsTable.NAME, GetAppsTable.Columns.PACKAGE_NAME + "=?",
                    new String[] {
                        packageName
                    });
        } catch (SQLiteException sQLiteException) {
            Log.w(LOG_TAG, "SQLiteException: " + sQLiteException.getMessage(), sQLiteException);
        }
        return res;
    }

}
