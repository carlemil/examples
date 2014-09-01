/*
 * Copyright (C) 2013 Sony Mobile Communications AB.
 * All rights, including trade secret rights, reserved.
 */

package com.sonymobile.userlogginglib.service;

import com.sonymobile.userlogginglib.internal.Config;
import com.sonymobile.userlogginglib.internal.db.LogDb;
import com.sonymobile.userlogginglib.internal.dispatch.Dispatcher;
import com.sonymobile.userlogginglib.util.PhoneInfo;
import com.sonymobile.userlogginglib.util.SharedPreferencesUtil;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

/**
 * {@link LoggerScheduler} helps with deciding whether it's time to upload data
 * to the server or schedule the service to run at a later time.
 */
public class LoggerScheduler {

    public enum Action {
        NOOP, CLEAR_DB, SCHEDULE, UPLOAD
    };

    private final LogDb mDb;

    private final PhoneInfo mPhoneInfo;

    private final Dispatcher mDispatcher;

    private final Context mContext;

    public LoggerScheduler(Context context, LogDb db, PhoneInfo phoneInfo, Dispatcher dispatcher) {
        mContext = context;
        mDb = db;
        mPhoneInfo = phoneInfo;
        mDispatcher = dispatcher;
    }

    public Action getAction(boolean startedByAlarmManager) {
        Action result;

        mDb.open();

        if (startedByAlarmManager && mDb.isEmpty()) {
            result = Action.NOOP;
        } else if (isLastSyncTimeInTheFuture(mContext)) {
            result = Action.CLEAR_DB;
        } else if (timeToUploadOnWifi() && mPhoneInfo.isOnWifi()) {
            result = Action.UPLOAD;
        } else if (timeToUploadOnMobileNetwork() && mPhoneInfo.isRoaming()) {
            result = Action.CLEAR_DB;
        } else if (timeToUploadOnMobileNetwork() && !mPhoneInfo.isRoaming()) {
            result = Action.UPLOAD;
        } else {
            result = Action.SCHEDULE;
        }

        mDb.close();
        return result;
    }

    private boolean timeToUploadOnMobileNetwork() {
        long now = System.currentTimeMillis();
        long timeSinceFirstLogEntry = now - SharedPreferencesUtil.getLastSyncTime(mContext);

        long maxWaitForWIFI = SharedPreferencesUtil.getMaxWaitForWIFI(mContext);
        return timeSinceFirstLogEntry > maxWaitForWIFI;
    }

    private boolean timeToUploadOnWifi() {
        long now = System.currentTimeMillis();
        long timeSinceFirstLogEntry = now - SharedPreferencesUtil.getLastSyncTime(mContext);

        long minWaitWhenOnWIFI = SharedPreferencesUtil.getMinWaitWhenOnWIFI(mContext);

        return timeSinceFirstLogEntry > minWaitWhenOnWIFI;
    }

    public void perform(Action action) {
        switch (action) {
            case CLEAR_DB:
                clearDb();
                scheduleNextAutomaticCheck();
                break;
            case UPLOAD:
                uploadData();
                scheduleNextAutomaticCheck();
                break;
            case SCHEDULE:
                scheduleNextAutomaticCheck();
                break;
            default: // NOOP
                noop();
                break;
        }
    }

    private void noop() {
        if (Config.DEBUG) {
            Log.d(Config.LOG_TAG, "Nothing to do.");
        }
    }

    private void uploadData() {
        if (Config.DEBUG) {
            Log.d(Config.LOG_TAG, "Uploading data");
        }
        boolean dispatchSuccess = mDispatcher.dispatch(mContext);
        if (dispatchSuccess) {
            SharedPreferencesUtil.setLastSyncTime(mContext, System.currentTimeMillis());
        }
    }

    private void clearDb() {
        if (Config.DEBUG) {
            Log.d(Config.LOG_TAG, "Clearing database");
        }
        mDb.open();
        mDb.dropAll();
        mDb.close();
    }

    private void scheduleNextAutomaticCheck() {
        long wifiPeriod = SharedPreferencesUtil.getMinWaitWhenOnWIFI(mContext);
        long now = System.currentTimeMillis();

        long periodsSinceFirstLogEntry = (now - SharedPreferencesUtil.getLastSyncTime(mContext))
                / wifiPeriod;
        periodsSinceFirstLogEntry++;
        long startTime = SharedPreferencesUtil.getLastSyncTime(mContext)
                + periodsSinceFirstLogEntry * wifiPeriod;

        Intent intent = new Intent(mContext, LoggerService.class);
        intent.putExtra(LoggerService.SCHEDULED_SERVICE_CALL, true);
        PendingIntent pintent = PendingIntent.getService(mContext, 0, intent, 0);

        AlarmManager alarm = (AlarmManager) mContext.getSystemService(Context.ALARM_SERVICE);
        alarm.set(AlarmManager.RTC, startTime, pintent);
        if (Config.DEBUG) {
            Log.d(Config.LOG_TAG, "Scheduled next automatick check at: " + startTime);
        }
    }

    public boolean isLastSyncTimeInTheFuture(Context context) {
        return SharedPreferencesUtil.getLastSyncTime(context) > System.currentTimeMillis();
    }
}
