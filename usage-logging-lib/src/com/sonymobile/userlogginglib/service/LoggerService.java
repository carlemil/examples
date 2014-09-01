/*
 * Copyright (C) 2013 Sony Mobile Communications AB.
 * All rights, including trade secret rights, reserved.
 */

package com.sonymobile.userlogginglib.service;

import com.sonymobile.userlogginglib.internal.Config;
import com.sonymobile.userlogginglib.internal.db.LogDb;
import com.sonymobile.userlogginglib.internal.db.SqliteLogDb;
import com.sonymobile.userlogginglib.internal.dispatch.Dispatcher;
import com.sonymobile.userlogginglib.internal.dispatch.LoggDestination;
import com.sonymobile.userlogginglib.internal.dispatch.Network;
import com.sonymobile.userlogginglib.service.LoggerScheduler.Action;
import com.sonymobile.userlogginglib.util.PhoneInfo;
import com.sonymobile.userlogginglib.util.SharedPreferencesUtil;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

/**
 * --- General Requirements --- </br> No real-time logging </br> Max wait for
 * wifi before uploading on mobile network is one week. </br> No uploading when
 * roaming. Should we discard data that is older than </br> one week? </br> Log
 * when offline </br> API Key is needed to access service </br> SSL to access
 * service </br>
 */
public class LoggerService extends IntentService {

    public static final String SCHEDULED_SERVICE_CALL = "scheduled_service_call";

    public LoggerService() {
        super("LoggerService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (Config.DEBUG) {
            Log.d(Config.LOG_TAG, "LoggerService is now running.");
        }
        boolean automaticServiceCall = false;
        if (intent != null) {
            automaticServiceCall = intent.getBooleanExtra(SCHEDULED_SERVICE_CALL, false);
        }

        LogDb db = new SqliteLogDb(this);
        String apiKey = SharedPreferencesUtil.getAPIKey(this);
        String rootUrl = SharedPreferencesUtil.getRootUrl(this);

        LoggDestination destination = new Network(rootUrl, apiKey);
        Dispatcher dispatcher = new Dispatcher(destination);
        LoggerScheduler scheduler = new LoggerScheduler(this, db, new PhoneInfo(this), dispatcher);
        Action action = scheduler.getAction(automaticServiceCall);
        scheduler.perform(action);

        if (Config.DEBUG) {
            Log.d(Config.LOG_TAG, "LoggerService is done and will shut down now.");
        }
    }

}
