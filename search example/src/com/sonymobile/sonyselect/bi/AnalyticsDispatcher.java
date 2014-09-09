package com.sonymobile.sonyselect.bi;

import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.os.PowerManager;
import android.util.Log;

import com.google.analytics.tracking.android.EasyTracker;
import com.sonymobile.sonyselect.bi.gahelper.GaHelper;

/**
 * Handles dispatching to the server.
 *
 * Also checks that the user has agreed to the terms of conditions and whether
 * the screen is on or off, and pauses dispatching if the screen is off.
 *
 * The reason for this is that there's a requirement that no network data is
 * allowed to be sent while the screen is turned off.
 *
 * @author Erik Ogenvik
 *
 */
public class AnalyticsDispatcher {

    private static final String LOG_TAG = AnalyticsDispatcher.class.getName();

    private static AnalyticsDispatcher dispatcher;

    private final Context context;

    /**
     * Handles the periodic dispatching to GA.
     */
    private final Handler handler = new Handler();

    /**
     * Milliseconds between each dispatch.
     */
    private final long millisecondsBetweenDispatch;

    /**
     * Whether the dispatcher is active or not (determined by the screen being
     * on currently).
     */
    private boolean active = false;

    /**
     * Makes sure that no GA data is sent when the screen is turned off.
     *
     * Note that the expected lifetime of this instance is for the whole
     * application's lifecycle; thus we won't bother with deregistering it from
     * the context.
     */
    private final BroadcastReceiver screenReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            if (Intent.ACTION_SCREEN_OFF.equals(intent.getAction())) {
                active = false;
            } else if (Intent.ACTION_SCREEN_ON.equals(intent.getAction())) {
                active = true;
            }
        }
    };

    private Runnable dispatchTask = new Runnable() {

        @Override
        public void run() {
            // We should only dispatch if the screen is on and the user is no monkey user(test automation)
            if (active && (!ActivityManager.isUserAMonkey() && !ActivityManager.isRunningInTestHarness())) {
                // First check if the user has opted out of tracking. This will
                // result in GA not sending any data.
                GaHelper.readAndSetGaEnabled(context);

                Log.v(LOG_TAG, "Dispatching to GA.");
                EasyTracker.getInstance(context).dispatchLocalHits();
            }
            handler.postDelayed(this, millisecondsBetweenDispatch);
        }
    };

    public static void startDispatcher(Context context, long millisecondsBetweenDispatch) {
        dispatcher = new AnalyticsDispatcher(context, millisecondsBetweenDispatch);
    }

    public static AnalyticsDispatcher getDispatcher() {
        return dispatcher;
    }

    private AnalyticsDispatcher(final Context context, long millisecondsBetweenDispatch) {
        this.context = context;
        this.millisecondsBetweenDispatch = millisecondsBetweenDispatch;

        PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
        if (pm != null) {
            active = pm.isScreenOn();
        }

        IntentFilter intentFilter = new IntentFilter(Intent.ACTION_SCREEN_OFF);
        intentFilter.addAction(Intent.ACTION_SCREEN_ON);
        // Note that we won't deregister the receiver, as it's intended
        // lifecycle is the length of the application.
        context.registerReceiver(screenReceiver, intentFilter);

        handler.postDelayed(dispatchTask, millisecondsBetweenDispatch);
    }

}
