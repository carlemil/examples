package com.sonymobile.sonyselect.util;

import android.util.Log;

public class StringUtil {

    private static final String LOG_TAG = StringUtil.class.getName();

    /**
     * Decide if the string is <code>null</code> contains no characters.
     * 
     * @param s
     *            The string to check
     * @return Boolean <code>true</code> if the string is <code>null</code> or
     *         the length of s is zero, boolean <code>false</code> otherwise.
     */
    public static boolean isEmpty(String s) {
        return s == null || s.length() == 0;
    }

    /**
     * Parse the specified string into an <code>int</code>. If s is
     * <code>null</code> or can't be parsed, the fallback value will be returned
     * instead.
     * 
     * @param s
     *            The string to parse
     * @param fallback
     *            The value to return if s==null or parsing went wrong.
     * @return The integer value parsed from s or the fallback value if
     *         something went wrong.
     */
    public static int parseInt(String s, int fallback) {
        if (s == null)
            return fallback;
        try {
            return Integer.parseInt(s);
        } catch (NumberFormatException e) {
            Log.e(LOG_TAG, "Failed to parse int", e);
            return fallback;
        }
    }

    /**
     * Convert a byte array to a hexadecimal notation string (0-F).
     * 
     * @param data
     *            The byte array to convert.
     * @return A hexadecimal formatted string.
     */
    public static String toHexString(byte[] data) {
        final char[] template = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F' };
        char[] chars = new char[data.length * 2];
        int v;

        for (int i = 0; i < data.length; i++) {
            v = data[i] & 0xFF;
            chars[i * 2] = template[v >>> 4];
            chars[i * 2 + 1] = template[v & 0x0F];
        }

        return new String(chars);
    }

}
