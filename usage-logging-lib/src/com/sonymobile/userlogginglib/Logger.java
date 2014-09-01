/*
 * Copyright (C) 2013 Sony Mobile Communications AB.
 * All rights, including trade secret rights, reserved.
 */

package com.sonymobile.userlogginglib;

import com.sonymobile.userlogginglib.internal.LogEntry;
import com.sonymobile.userlogginglib.internal.Queue;
import com.sonymobile.userlogginglib.service.LoggerService;
import com.sonymobile.userlogginglib.util.SharedPreferencesUtil;

import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;

/**
 * Logger is used to log messages from a client to the logging server.
 */
public class Logger {

    /**
     * Creates and returns a {@link LogEntry.Builder} that in turn can be used
     * to create a {@link LogEntry}.
     *
     * @param type the type of the log entry to create. The types are completely
     *            client specific.
     * @return the {@link LogEntry.Builder} that will create a {@link LogEntry}
     *         when asked to.
     */
    public LogEntry.Builder log(String type) {
        if (TextUtils.isEmpty(type)) {
            return new LogEntry.LogToLogCatBuilder().with("type", null);
        } else {
            return new LogEntry.LogToServerBuilder().with("type", type);
        }
    }

    /**
     * Initialize the {@link Logger}.
     *
     * @param context The {@link Context} used by the {@link Queue} for
     *            {@link SQLiteDatabase} access.
     * @param rootUrl the rootUrl.
     * @param apiKey the apiKey.
     * @param clientName the name of the client.
     * @param clientVersion the version of the client.
     * @param deviceId a id identifying the device or null.
     */
    public void init(Context context, String rootUrl, String apiKey, String clientName,
            String clientVersion, String deviceId) {
        SharedPreferencesUtil.setAPIKey(context, apiKey);
        SharedPreferencesUtil.setRootUrl(context, rootUrl);
        SharedPreferencesUtil.setClientName(context, clientName);
        SharedPreferencesUtil.setClientVersion(context, clientVersion);
        SharedPreferencesUtil.setDeviceId(context, deviceId);

        // If no previous sync time have been set (first run) set NOW as
        // previous sync time.
        if (!SharedPreferencesUtil.hasLastSyncTime(context)) {
            SharedPreferencesUtil.setLastSyncTime(context, System.currentTimeMillis());
        }

        Queue.init(context);
        startService(context);
    }

    private void startService(Context context) {
        Intent msgIntent = new Intent(context, LoggerService.class);
        context.startService(msgIntent);
    }

}
