
package com.sonymobile.sonyselect;

import java.text.SimpleDateFormat;
import java.util.Date;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.preference.PreferenceManager;
import android.test.ActivityInstrumentationTestCase2;
import android.util.Log;
import android.view.WindowManager;
import android.widget.ImageView;

import com.robotium.solo.Solo;
import com.sonymobile.sonyselect.fragment.PrefsFragment;

public abstract class IntegrationAbstractServerPingTest extends ActivityInstrumentationTestCase2 {

    private static String LOG_TAG = IntegrationAbstractServerPingTest.class.getCanonicalName();

    public static String URL_TO_PING;

    private Solo solo;

    private WakeLock wakeLock;

    private static final String LAUNCHER_ACTIVITY_FULL_CLASSNAME = "com.sonymobile.sonyselect.activities.LaunchActivity";

    private static Class<?> launcherActivityClass;

    static {
        try {
            launcherActivityClass = Class.forName(LAUNCHER_ACTIVITY_FULL_CLASSNAME);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public IntegrationAbstractServerPingTest() {
        super(launcherActivityClass);
    }

    @Override
    public void setUp() throws Exception {
        Context context = getInstrumentation().getTargetContext();
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);

        prefs.edit().putString(PrefsFragment.PREF_SERVER_URI_EDITTEXT, URL_TO_PING).commit();

        String actual = prefs.getString(PrefsFragment.PREF_SERVER_URI_EDITTEXT, null);
        assertEquals(URL_TO_PING, actual);

        solo = new Solo(getInstrumentation());
        getActivity();

        getInstrumentation().runOnMainSync(new Runnable() {
            @Override
            public void run() {
                getActivity().getWindow().addFlags(
                        WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD
                                | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
                PowerManager pm = (PowerManager) getActivity().getSystemService(
                        Context.POWER_SERVICE);
                wakeLock = pm.newWakeLock(PowerManager.FULL_WAKE_LOCK
                        | PowerManager.ACQUIRE_CAUSES_WAKEUP | PowerManager.ON_AFTER_RELEASE,
                        "MyWakeLock");
                wakeLock.acquire();
            }
        });
    }

    @Override
    public void tearDown() throws Exception {
        Context context = getInstrumentation().getTargetContext();
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);

        prefs.edit().remove(PrefsFragment.PREF_SERVER_URI_EDITTEXT).commit();

        String actual = prefs.getString(PrefsFragment.PREF_SERVER_URI_EDITTEXT, "Not found");
        assertEquals("Not found", actual);

        wakeLock.release();

        solo.finishOpenedActivities();
    }

    public void testRun() throws Throwable {
        try {
            // Wait for activity
            assertTrue("LaunchActivity is not found!", solo.waitForActivity("LaunchActivity"));
            // Wait for dialog
            if (solo.waitForDialogToOpen(30000)) {
                // Click on Agree
                solo.clickOnView(solo.getView(android.R.id.button1));
            }
            // Assert that: 'ProgressBar' is shown
            solo.waitForView(solo.getView("progressbar"));
            // Assert that two 'ImageView's are shown
            assertTrue("Lists were not fetched from Server.",
                    solo.waitForView(ImageView.class, 2, 120000));

            // Click on Item 1 in List 1
            solo.clickInList(1, 1);
            // Assert that a 'ImageView' is shown
            assertTrue("Item missing?", solo.waitForView(ImageView.class, 1, 30000));
        } catch (Throwable t) {
            SimpleDateFormat s = new SimpleDateFormat("yyyy_MM_dd_hh_mm_ss");
            String timestamp = s.format(new Date());
            String testCaseName = String.format("%s.%s.%s", getClass().getName(), getName(),
                    timestamp);
            solo.takeScreenshot(testCaseName);
            Log.w(LOG_TAG, String.format("Captured screenshot for failed test: %s", testCaseName));
            throw t;
        }
    }

}
