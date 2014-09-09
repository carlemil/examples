package com.sonymobile.sonyselect.util;

import android.database.Cursor;

public class CursorUtils {

    /**
     * <p>
     * Check if the given {@link android.database.Cursor} is {@code null} or
     * doesn't contain any items.
     * </p>
     * 
     * @param cursor A {@code Cursor} to check
     * @return Boolean {@code true} if the cursor is {@code null} or the number
     *         of rows of it is zero, boolean {@code false} otherwise.
     */
    public static boolean isEmpty(Cursor cursor) {
        return cursor == null || cursor.getCount() == 0 || cursor.isClosed();
    }

    /**
     * checks if the given {@link android.database.Cursor} contains elements
     * 
     * @param cursor A {@code Cursor} to check
     * @return Boolean {@code true} if the cursor is {@code null} or the number
     *         of rows of it is zero, boolean {@code false} otherwise.
     */
    public static boolean notEmpty(Cursor cursor) {
        return !isEmpty(cursor);
    }
}
