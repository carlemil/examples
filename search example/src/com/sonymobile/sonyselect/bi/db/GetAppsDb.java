/*
 * Copyright (C) 2013 Sony Mobile Communications AB.
 * All rights, including trade secret rights, reserved.
 */

package com.sonymobile.sonyselect.bi.db;

import android.database.sqlite.SQLiteDatabase;

/**
 * Facade towards the database.
 */
public interface GetAppsDb {

    /**
     * Opens the database. Don't forget to close it when you're done!
     */
    void open();

    /**
     * Closes the database. All method calls (except for {@link #open()}) will
     * probably throw {@link NullPointerException} after this call.
     */
    void close();

    /**
     * Checks if the database is empty.
     *
     * @return <code>true</code> if the database is empty, <code>false</code>
     *         otherwise.
     */
    boolean isEmpty();

    /**
     * Adds a package name and timestamp to the database.
     *
     * @param packageName the name of the package.
     * @param timestamp the time of the insert operation.
     */
    void insert(String packageName, long timestamp);

    /**
     * Returns a cursor pointing to a row matching the packageName.
     *
     * @param packageName the package name.
     * @return a cursor.
     */
    Long getTimestampForPackageName(String packageName);

    /**
     * Deletes ALL OLD rows from the {@link GetAppsTable} table in the
     * {@link SQLiteDatabase}. A row is OLD if its timestamp is more than 24h in
     * the past.
     *
     * @param oldTimestamp the old timestamp.
     * @return the number of rows .
     */
    int deleteAllOldRows(long oldTimestamp);

    /**
     * Deletes all rows matching packageName.
     * @param packageName the String to match for deletion.
     * @return
     */
    int deleteByPackageName(String packageName);

}
