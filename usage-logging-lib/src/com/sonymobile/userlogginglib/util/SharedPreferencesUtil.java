/*
 * Copyright (c) 2013 Sony Mobile Communications AB.
 * All rights, including trade secret rights, reserved.
 */

package com.sonymobile.userlogginglib.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.preference.PreferenceManager;

/**
 * Util for storing stuff in the SharedPreferences class.
 */
public class SharedPreferencesUtil {

    /**
     * Default max time (in milliseconds) to wait for wifi before starting a
     * upload over a non-wifi connection.
     */
    private static final long DEFAULT_MAX_WAIT_FOR_WIFI_IN_MS = 7L * 24 * 60 * 60 * 1000;

    /**
     * Default min time (in milliseconds) to wait when on wifi before starting a
     * upload. Defaults to 21 hours.
     */
    private static final long DEFAULT_MIN_WAIT_WHEN_ON_WIFI_IN_MS = 21 * 60 * 60 * 1000;

    private static final String API_KEY = "api_key";

    private static final String ROOT_URL = "root_url";

    private static final String CLIENT_NAME = "client_name";

    private static final String CLIENT_VERSION = "client_version";

    private static final String MAX_WAIT_FOR_WIFI = "max_wait_for_WIFI";

    private static final String MIN_WAIT_WHEN_ON_WIFI = "min_wait_when_on_WIFI";

    private static final String RANDOM_ID = "random_id";

    private static final String DEVICE_ID = "device_id";

    private static final String LAST_SYNC_TIME = "last_sync_time";

    /**
     * Clears the SharedPreferences.
     *
     * @param context {@link Context} for reading/writing the
     *            {@link SharedPreferences}'s.
     */
    public static void clearPreferences(Context context) {
        SharedPreferences sharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(context);
        Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.apply();
    }

    /**
     * Stores the API-key.
     *
     * @param context {@link Context} for reading/writing the
     *            {@link SharedPreferences}'s.
     * @param apiKey The API-key to store.
     */
    public static void setAPIKey(Context context, String apiKey) {
        if (!Utils.isEmpty(apiKey)) {
            savePreferences(context, API_KEY, apiKey);
        }
    }

    /**
     * Stores the root URL.
     *
     * @param context {@link Context} for reading/writing the
     *            {@link SharedPreferences}'s.
     * @param rootUrl The rootURL to store.
     */
    public static void setRootUrl(Context context, String rootUrl) {
        if (!Utils.isEmpty(rootUrl)) {
            savePreferences(context, ROOT_URL, rootUrl);
        }
    }

    /**
     * Stores the client name.
     *
     * @param context {@link Context} for reading/writing the
     *            {@link SharedPreferences}'s.
     * @param clientName The client name to store.
     */
    public static void setClientName(Context context, String clientName) {
        if (!Utils.isEmpty(clientName)) {
            savePreferences(context, CLIENT_NAME, clientName);
        }
    }

    /**
     * Stores the client version.
     *
     * @param context {@link Context} for reading/writing the
     *            {@link SharedPreferences}'s.
     * @param clientVersion The client version to store.
     */
    public static void setClientVersion(Context context, String clientVersion) {
        if (!Utils.isEmpty(clientVersion)) {
            savePreferences(context, CLIENT_VERSION, clientVersion);
        }
    }

    /**
     * Sets the max time (in milliseconds) to wait for WIFI before starting a
     * upload over a non-WIFI connection.
     *
     * @param context {@link Context} for reading/writing the
     *            {@link SharedPreferences}'s.
     * @param maxWaitForWIFI The max time (in milliseconds) to wait for WIFI
     *            before starting a upload over a non-WIFI connection to store.
     */
    public static void setMaxWaitForWIFI(Context context, long maxWaitForWIFI) {
        savePreferences(context, MAX_WAIT_FOR_WIFI, maxWaitForWIFI);
    }

    /**
     * Sets the min time (in milliseconds) to wait when on WIFI before starting
     * a upload.
     *
     * @param context {@link Context} for reading/writing the
     *            {@link SharedPreferences}'s.
     * @param maxWaitWhenOnWIFI The the min time (in milliseconds) to wait when
     *            on WIFI before starting a upload to store.
     */
    public static void setMinWaitWhenOnWIFI(Context context, long minWaitWhenOnWIFI) {
        savePreferences(context, MIN_WAIT_WHEN_ON_WIFI, minWaitWhenOnWIFI);
    }

    /**
     * Sets the user id.
     *
     * @param context {@link Context} for reading/writing the
     *            {@link SharedPreferences}'s.
     * @param deviceId the id identifying the device.
     */
    public static void setDeviceId(Context context, String deviceId) {
        savePreferences(context, DEVICE_ID, deviceId);
    }

    /**
     * Sets the time-stamp of the last sync..
     *
     * @param context {@link Context} for reading/writing the
     *            {@link SharedPreferences}'s.
     * @param lastSyncTime the time-stamp of the last sync.
     */
    public static void setLastSyncTime(Context context, long lastSyncTime) {
        savePreferences(context, LAST_SYNC_TIME, lastSyncTime);
    }

    /**
     * Reads and returns the API-key.
     *
     * @param context {@link Context} for reading/writing the
     *            {@link SharedPreferences}'s.
     * @return apiKey The API-key.
     */
    public static String getAPIKey(Context context) {
        return loadSavedPreferencesString(context, API_KEY);
    }

    /**
     * Reads and returns the root URL.
     *
     * @param context {@link Context} for reading/writing the
     *            {@link SharedPreferences}'s.
     * @return the root URL.
     */
    public static String getRootUrl(Context context) {
        return loadSavedPreferencesString(context, ROOT_URL);
    }

    /**
     * Reads and returns the client name.
     *
     * @param context {@link Context} for reading/writing the
     *            {@link SharedPreferences}'s.
     * @return the client name.
     */
    public static String getClientName(Context context) {
        return loadSavedPreferencesString(context, CLIENT_NAME);
    }

    /**
     * Reads and returns the client version.
     *
     * @param context {@link Context} for reading/writing the
     *            {@link SharedPreferences}'s.
     * @return the client version.
     */
    public static String getClientVersion(Context context) {
        return loadSavedPreferencesString(context, CLIENT_VERSION);
    }

    /**
     * Reads and returns the max wait for WIFI.
     *
     * @param context {@link Context} for reading/writing the
     *            {@link SharedPreferences}'s.
     * @return the the max wait for WIFI.
     */
    public static long getMaxWaitForWIFI(Context context) {
        return loadSavedPreferencesLong(context, MAX_WAIT_FOR_WIFI, DEFAULT_MAX_WAIT_FOR_WIFI_IN_MS);
    }

    /**
     * Reads and returns the max wait when on WIFI.
     *
     * @param context {@link Context} for reading/writing the
     *            {@link SharedPreferences}'s.
     * @return the max wait when on WIFI.
     */
    public static long getMinWaitWhenOnWIFI(Context context) {
        return loadSavedPreferencesLong(context, MIN_WAIT_WHEN_ON_WIFI,
                DEFAULT_MIN_WAIT_WHEN_ON_WIFI_IN_MS);
    }

    /**
     * Reads and returns the time-stamp of the last sync.
     *
     * @param context {@link Context} for reading/writing the
     *            {@link SharedPreferences}'s.
     * @return the time-stamp of the last sync.
     */
    public static Long getLastSyncTime(Context context) {
        return loadSavedPreferencesLong(context, LAST_SYNC_TIME, Long.MIN_VALUE);
    }

    /**
     * Gets the randomly generated id. If this method has never been called
     * before, it will generate an id and save it for future use.
     *
     * @param context a {@link Context} used to access the
     *            {@link SharedPreferences}
     * @return the user id.
     */
    public static String getRandomId(Context context) {
        String randomId = loadSavedPreferencesString(context, RANDOM_ID);
        if (randomId == null) {
            randomId = Long.toHexString((long) (Math.random() * Long.MAX_VALUE));
            savePreferences(context, RANDOM_ID, randomId);
        }
        return randomId;
    }

    /**
     * Returns true if any last sync time stamp(s) have been written to the
     * SharedPreferences, else false.
     *
     * @param context {@link Context} for reading/writing the
     *            {@link SharedPreferences}'s.
     * @return true if any last sync time stamp(s) have been written to the
     *         SharedPreferences, else false.
     */
    public static boolean hasLastSyncTime(Context context) {
        SharedPreferences sharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(context);
        return sharedPreferences.contains(LAST_SYNC_TIME);
    }

    /**
     * Gets the device id.
     *
     * @param context a {@link Context} used to access the
     *            {@link SharedPreferences}
     * @return the device id.
     */
    public static String getDeviceId(Context context) {
        return loadSavedPreferencesString(context, DEVICE_ID);
    }

    private static void savePreferences(Context context, String key, String value) {
        SharedPreferences sharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(context);
        Editor editor = sharedPreferences.edit();
        editor.putString(key, value);
        editor.apply();
    }

    private static void savePreferences(Context context, String key, long value) {
        SharedPreferences sharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(context);
        Editor editor = sharedPreferences.edit();
        editor.putLong(key, value);
        editor.apply();
    }

    private static String loadSavedPreferencesString(Context context, String key) {
        SharedPreferences sharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(context);
        String value = sharedPreferences.getString(key, null);
        return value;
    }

    private static long loadSavedPreferencesLong(Context context, String key, long defaultValue) {
        SharedPreferences sharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(context);
        long value = sharedPreferences.getLong(key, defaultValue);
        return value;
    }

}
