/*
 * Copyright (C) 2013 Sony Mobile Communications AB.
 * All rights, including trade secret rights, reserved.
 */

package com.sonymobile.userlogginglib.internal;

import com.sonymobile.userlogginglib.util.Utils;

import android.util.Log;

import java.util.AbstractQueue;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

/**
 * Definition of a entry in the Captain's log.
 */
public class LogEntry {

    private final Map<String, String> mMap = new HashMap<String, String>();

    private boolean mPendingSubmision = true;

    /**
     * Gets the map that holds this {@link LogEntry}'s data.
     *
     * @return the Map.
     */
    public Map<String, String> getMap() {
        return mMap;
    }

    /**
     * Add a new key/value pair to the {@link LogEntry}
     *
     * @param key The key of the pair.
     * @param value The Value of the pair.
     */
    public void addKeyValuePair(String key, String value) {
        mMap.put(key, value);
    }

    /**
     * Returns false if submit() have been called on this LogEntry.
     *
     * @return false if submitted, true if not.
     */
    private boolean isPendingSubmission() {
        return mPendingSubmision;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        Set<String> keySet = mMap.keySet();
        for (String key : keySet) {
            sb.append(key);
            sb.append(": ");
            sb.append(mMap.get(key));
            sb.append("\n");
        }
        return sb.toString();
    }

    /**
     * Builder of LogEntrys that will be sent to some server when submit() is
     * called.
     */
    public static class LogToServerBuilder extends Builder {

        /**
         * Adds a key/value pair to the {@link LogEntry}.
         *
         * @param key the Key.
         * @param value the Value.
         * @return The builder, for further building using this method.
         */
        public LogToServerBuilder with(String key, String value) {
            if (mLogEntry.isPendingSubmission()) {
                if (!Utils.isEmpty(key) && !Utils.isEmpty(value)) {
                    mLogEntry.addKeyValuePair(key, value);
                } else if (Config.DEBUG) {
                    Log.w(Config.LOG_TAG, "Trying to log with empty key or value.");
                }
            } else if (Config.DEBUG) {
                Log.w(Config.LOG_TAG, "Can not change an already submitted LogEntry.");
            }
            return this;
        }

        /**
         * Submit the {@link LogEntry} to the server.
         */
        public void submit() {
            if (mLogEntry.isPendingSubmission()) {
                mLogEntry.mPendingSubmision = false;
                Queue.submit(mLogEntry);
            } else if (Config.DEBUG) {
                Log.w(Config.LOG_TAG, "Can not submit a LogEntry twice.");
            }
        }
    }

    /**
     * Builder of LogEntrys that will be printed in LogCat when submit() is
     * called.
     */
    public static class LogToLogCatBuilder extends Builder {

        /**
         * Adds a key/value pair to the {@link LogEntry}.
         *
         * @param key the Key.
         * @param value the Value.
         * @return The builder, for further building using this method.
         */
        public LogToLogCatBuilder with(String key, String value) {
            if (!Utils.isEmpty(key) && !Utils.isEmpty(value)) {
                mLogEntry.addKeyValuePair(key, value);
            } else if (Config.DEBUG) {
                Log.w(Config.LOG_TAG, "Trying to log with empty key or value.");
            }
            return this;
        }

        /**
         * Submit the {@link LogEntry} to LogCat.
         */
        public void submit() {
            if (Config.DEBUG) {
                Log.d(Config.LOG_TAG, "Dumping LogEntrys to LogCat since 'type'"
                        + " is missing, this LogEntry will NOT be sent to the server.");
            }
            StringBuffer sb = new StringBuffer();
            Map<String, String> map = mLogEntry.getMap();
            sb.append("LogEntry:\n");
            for (Entry<String, String> entry : map.entrySet()) {
                sb.append("key: ").append(entry.getKey()).append(", value: ")
                        .append(entry.getValue()).append("\n");
            }
            if (Config.DEBUG) {
                Log.d(Config.LOG_TAG, sb.toString());
            }

        }
    }

    /**
     * Builds new {@link LogEntry}'s.
     */
    public abstract static class Builder {

        protected final LogEntry mLogEntry;

        /**
         * Constructs a new {@link LogToServerBuilder} to use for creating a
         * {@link LogEntry}.
         *
         * @param queue The {@link AbstractQueue} that the {@link LogEntry}
         *            should be put on when send() is called.
         */
        public Builder() {
            mLogEntry = new LogEntry();
        }

        /**
         * Adds a key/value pair to the {@link LogEntry}.
         *
         * @param key the Key.
         * @param value the Value.
         * @return The builder, for further building using this method.
         */
        public abstract Builder with(String key, String value);

        /**
         * Submit the {@link LogEntry}.
         */
        public abstract void submit();

    }

}
