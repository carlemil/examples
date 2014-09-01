/*
 * Copyright (c) 2013 Sony Mobile Communications AB.
 * All rights, including trade secret rights, reserved.
 */

package com.sonymobile.userlogginglib.util;

import android.database.Cursor;

/**
 * Utility class to help with cursors.
 */
public class CursorUtil {

    /**
     * Gets the content of the named column in the current row as a String, or
     * returns the default value if the column doesn't exist.
     *
     * @param cursor the cursor to extract the current row from
     * @param column the name of the column in the current row
     * @param defaultValue the value to return if the column doesn't exist.
     * @return the value of the column, if the column exists, otherwise the
     *         provided default value.
     */
    public static String getString(Cursor cursor, String column, String defaultValue) {
        int columnIndex = cursor.getColumnIndex(column);
        if (columnIndex >= 0) {
            return cursor.getString(columnIndex);
        }
        return defaultValue;
    }

    /**
     * Gets the content of the named column in the current row as a long, or
     * returns the default value if the column doesn't exist.
     *
     * @param cursor the cursor to extract the current row from
     * @param column the name of the column in the current row
     * @param defaultValue the value to return if the column doesn't exist.
     * @return the value of the column, if the column exists, otherwise the
     *         provided default value.
     */
    public static long getLong(Cursor cursor, String column, long defaultValue) {
        int columnIndex = cursor.getColumnIndex(column);
        if (columnIndex >= 0) {
            return cursor.getLong(columnIndex);
        }
        return defaultValue;
    }

    /**
     * Closes the provided cursor if possible. If not possible, it returns
     * without throwing exceptions.
     *
     * @param cursor the cursor to close.
     */
    public static void closeSilently(Cursor cursor) {
        if (cursor != null) {
            cursor.close();
        }
    }

}
