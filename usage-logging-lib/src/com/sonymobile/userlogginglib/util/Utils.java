/*
 * Copyright (C) 2013 Sony Mobile Communications AB.
 * All rights, including trade secret rights, reserved.
 */
package com.sonymobile.userlogginglib.util;

import com.sonymobile.userlogginglib.internal.Config;

import android.database.Cursor;
import android.net.Uri;
import android.util.Log;

import java.io.Closeable;
import java.io.IOException;
import java.math.BigInteger;
import java.net.HttpURLConnection;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

/**
 * <p>
 * <strong>DO NOT USE THIS CLASS. It is not part of the API and will change, or
 * be removed, at any time without warning.</strong>
 * </p>
 * <h1>Utilities</h1>
 * <p>
 * This is a tool box for common, non-application-logic set of tasks used by the
 * Sony Logger. Typically containing methods for validating strings, lists
 * or maps before using them. Validation would include simple null checks and
 * length integrity tests. There are also methods for gracefully closing streams
 * (checking for null and catching those rarely thrown exceptions).
 * </p>
 * <h2>Example usage</h2>
 * <p>
 * The below code snippets could be used from everywhere as all functional
 * dependencies are injected directly to the static methods.
 * </p>
 *
 * <pre>
 * <code>
 * String serverUri = null;
 * String geoIpUri = "something";
 *
 * // This call will return boolean true, because one of the given arguments is
 * // is empty (in this case actually null.)
 * Utils.isAnyEmpty(serverUri, geoIpUri);
 * </code>
 * </pre>
 *
 */
public final class Utils {

    private static final String LOG_TAG = Utils.class.getName();

    private Utils() {
        // Prevent instantiation of this class.
    }

    /**
     * <p>
     * <strong>DO NOT USE THIS CLASS. It is not part of the API and will change,
     * or be removed, at any time without warning.</strong>
     * </p>
     * <p>
     * Implementation of {@link Iterator} which always responds as being empty.
     * </p>
     *
     * @param <T>
     *            the type of object <em>supposedly</em> returned by the
     *            iterator.
     */
    public static final class EmptyIterator<T> implements Iterator<T> {

        /**
         * <p>
         * <strong>DO NOT USE THIS METHOD. It is not part of the API and will
         * change, or be removed, at any time without warning.</strong>
         * </p>
         * <p>
         * Returns {@code false} because there are no elements.
         * </p>
         *
         * @return {@code false}
         */
        @Override
        public boolean hasNext() {
            return false;
        }

        /**
         * <p>
         * <strong>DO NOT USE THIS METHOD. It is not part of the API and will
         * change, or be removed, at any time without warning.</strong>
         * </p>
         * <p>
         * Throws {@link java.util.NoSuchElementException} because there are no
         * elements.
         * </p>
         *
         * @throws java.util.NoSuchElementException
         */
        @Override
        public T next() throws NoSuchElementException {
            throw new NoSuchElementException(EmptyIterator.class.getSimpleName() + " never has any elements.");
        }

        /**
         * <p>
         * <strong>DO NOT USE THIS METHOD. It is not part of the API and will
         * change, or be removed, at any time without warning.</strong>
         * </p>
         * <p>
         * Does nothing, because there are no elements.
         * </p>
         */
        @Override
        public void remove() {
        }
    }

    /**
     * Calculates a MD5-hash on the given input string.
     *
     * @param content
     *            The string to calculate the the hash on.
     * @return The MD5 check sum.
     */
    public static String calculateHash(String content) {
        String result = null;
        if (!isEmpty(content)) {
            try {
                byte[] rawBytes = content.getBytes();
                MessageDigest digest = MessageDigest.getInstance("SHA1");
                digest.update(rawBytes, 0, rawBytes.length);
                byte[] digestedBytes = digest.digest();
                BigInteger bigInt = new BigInteger(1, digestedBytes);
                result = bigInt.toString(16);
            } catch (NoSuchAlgorithmException e) {
                Log.e(Config.LOG_TAG, "Failed to calculate hash", e);
                result = content;
            } catch (IllegalArgumentException e) {
                Log.e(Config.LOG_TAG, "Failed to calculate hash", e);
                result = content;
            }
        }
        return result;
    }

    /**
     * <p>
     * <strong>DO NOT USE THIS METHOD. It is not part of the API and will
     * change, or be removed, at any time without warning.</strong>
     * </p>
     * <p>
     * Closes the specified stream and swallows any exceptions silently. Handles
     * {@code null} gracefully.
     * </p>
     *
     * @param stream
     *            The stream to close. May be {@code null}
     */
    public static void closeSilently(Closeable stream) {
        if (stream == null) {
            return;
        }
        try {
            stream.close();
        } catch (IOException e) {
            // Be silent...
        }
    }

    /**
     * <p>
     * <strong>DO NOT USE THIS METHOD. It is not part of the API and will
     * change, or be removed, at any time without warning.</strong>
     * </p>
     * <p>
     * Disconnects the specified {@link HttpURLConnection} and swallows any
     * exceptions silently. Handles {@code null} gracefully.
     * </p>
     *
     * @param connection
     *            The connection to close. May be {@code null}
     */
    public static void closeSilently(HttpURLConnection connection) {
        if (connection != null) {
            connection.disconnect();
        }
    }

    /**
     * <p>
     * <strong>DO NOT USE THIS METHOD. It is not part of the API and will
     * change, or be removed, at any time without warning.</strong>
     * </p>
     * <p>
     * Checks if the given URI is {@code null} or is equal to the {@code EMPTY}
     * URI.
     * </p>
     *
     * @param string
     *            The string to check
     * @return Boolean {@code true} if the string is {@code null} or a no-length
     *         string, boolean {@code false} if it has some content.
     */
    public static boolean isEmpty(Uri uri) {
        return uri == null || uri.equals(Uri.EMPTY);
    }

    /**
     * <p>
     * <strong>DO NOT USE THIS METHOD. It is not part of the API and will
     * change, or be removed, at any time without warning.</strong>
     * </p>
     * <p>
     * Checks if the given string is {@code null} or doesn't contain any
     * characters.
     * </p>
     *
     * @param string
     *            The string to check
     * @return Boolean {@code true} if the string is {@code null} or a no-length
     *         string, boolean {@code false} if it has some content.
     */
    public static boolean isEmpty(String string) {
        return string == null || string.length() == 0;
    }

    /**
     * <p>
     * <strong>DO NOT USE THIS METHOD. It is not part of the API and will
     * change, or be removed, at any time without warning.</strong>
     * </p>
     * <p>
     * Checks if <em>any</em> of the given strings is {@code null} or doesn't
     * contain any characters. This method stops checking at the first
     * {@code null} or no-length string it finds.
     * </p>
     *
     * @param strings
     *            The strings to check
     * @return Boolean {@code true} if a {@code null} or a no-length string is
     *         found, boolean {@code false} if all strings have some content.
     */
    public static boolean isAnyEmpty(String... strings) {
        for (String s : strings) {
            if (isEmpty(s)) {
                return true;
            }
        }
        return false;
    }

    /**
     * <p>
     * <strong>DO NOT USE THIS METHOD. It is not part of the API and will
     * change, or be removed, at any time without warning.</strong>
     * </p>
     * <p>
     * Checks if the given array is {@code null} or doesn't contain any items.
     * </p>
     *
     * @param array
     *            An array of objects to check
     * @return Boolean {@code true} if the array is {@code null} or the size of
     *         it is zero, boolean {@code false} otherwise.
     */
    public static boolean isEmpty(Object[] array) {
        return array == null || array.length == 0;
    }

    /**
     * <p>
     * <strong>DO NOT USE THIS METHOD. It is not part of the API and will
     * change, or be removed, at any time without warning.</strong>
     * </p>
     * <p>
     * Checks if the given list is {@code null} or doesn't contain any items.
     * </p>
     *
     * @param list
     *            A list of any kind of items to check
     * @return Boolean {@code true} if the list is {@code null} or the size of
     *         it is zero, boolean {@code false} otherwise.
     */
    public static boolean isEmpty(List<?> list) {
        return list == null || list.size() == 0;
    }

    /**
     * <p>
     * <strong>DO NOT USE THIS METHOD. It is not part of the API and will
     * change, or be removed, at any time without warning.</strong>
     * </p>
     * <p>
     * Checks if the given map is {@code null} or doesn't contain any items.
     * </p>
     *
     * @param map
     *            A map of any kind of items to check
     * @return Boolean {@code true} if the map is {@code null} or the size of it
     *         is zero, boolean {@code false} otherwise.
     */
    public static boolean isEmpty(Map<?, ?> map) {
        return map == null || map.size() == 0;
    }

    /**
     * <p>
     * <strong>DO NOT USE THIS METHOD. It is not part of the API and will
     * change, or be removed, at any time without warning.</strong>
     * </p>
     * <p>
     * Check if the given {@link android.database.Cursor} is {@code null} or
     * doesn't contain any items.
     * </p>
     *
     * @param cursor
     *            A {@code Cursor} to check
     * @return Boolean {@code true} if the cursor is {@code null} or the number
     *         of rows of it is zero, boolean {@code false} otherwise.
     */
    public static boolean isEmpty(Cursor cursor) {
        return cursor == null || cursor.isClosed() || cursor.getCount() == 0;
    }

    /**
     * <p>
     * <strong>DO NOT USE THIS METHOD. It is not part of the API and will
     * change, or be removed, at any time without warning.</strong>
     * </p>
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
        if (s == null) {
            return fallback;
        }
        try {
            return Integer.parseInt(s);
        } catch (NumberFormatException e) {
            Log.e(LOG_TAG, "Failed to parse int", e);
            return fallback;
        }
    }
}
