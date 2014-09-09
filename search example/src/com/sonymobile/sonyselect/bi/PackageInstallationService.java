/*
 * Copyright (C) 2012 Sony Mobile Communications AB.
 * All rights, including trade secret rights, reserved.
 */

package com.sonymobile.sonyselect.bi;

import com.sonymobile.sonyselect.bi.db.SqliteLogDb;

import android.app.ActivityManager;
import android.app.IntentService;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

/**
 * This class monitors application installs.
 */
public class PackageInstallationService extends IntentService {

    public PackageInstallationService() {
        super("PackageInstallationService");
        setIntentRedelivery(true);
    }

    private static final String LOG_TAG = PackageInstallationService.class.getCanonicalName();

    /**
     * The name of the action to perform extra. This will be sent in the start
     * command.
     */
    public static final String ACTION_NAME = "com.sonymobile.sonyselect.bi.action";

    /**
     * The 'package installed' action. This will log an installed app.
     */
    public static final String PACKAGE_INSTALLED_ACTION = "PACKAGE_INSTALLED";

    public static final String PACKAGE_UID_EXTRA = "PACKAGE_UID";

    public static final String PACKAGE_NAME = "PACKAGE_NAME";

    @Override
    public final IBinder onBind(final Intent intent) {
        return null;
    }

    @Override
    public final void onLowMemory() {
        super.onLowMemory();
        Log.d(LOG_TAG, "WARNING! Low on memory");
        stopSelf();
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent == null) {
            Log.d(LOG_TAG, "Service restarted... Should not happen, not logging...");
        } else if (ActivityManager.isUserAMonkey()) {
            Log.d(LOG_TAG, "User is a monkey, not logging...");
        } else {
            String action = intent.getStringExtra(ACTION_NAME);
            if (PACKAGE_INSTALLED_ACTION.equals(action)) {
                final int MISSING = -1;
                final int packageUid = intent.getIntExtra(PACKAGE_UID_EXTRA, MISSING);
                if (packageUid != MISSING) {
                    final String packageName = intent.getStringExtra(PACKAGE_NAME);
                    final Long timeDiff = getTimeDiffForPackage(packageName);
                    if (timeDiff != null) {
                        Tracker.getTracker().trackApplicationInstall(packageName, timeDiff);
                    } else {
                        Log.d(LOG_TAG, "Package name was NOT found in the db: GetAppsTable.");
                    }
                } else {
                    Log.d(LOG_TAG, "WARNING! Package uid is missing.");
                }
            }
        }
    }

    private Long getTimeDiffForPackage(String packageName) {
        SqliteLogDb db = new SqliteLogDb(getApplicationContext());
        db.open();
        Long timestamp = db.getTimestampForPackageName(packageName);
        db.deleteByPackageName(packageName);
        db.close();
        if (timestamp == null) {
            return null;
        }
        Long timediff = System.currentTimeMillis() - timestamp;
        Log.d(LOG_TAG, "get -> install timediff: " + timediff);
        return timediff;
    }

}
