
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
import android.widget.EditText;
import android.widget.ImageView;

import com.robotium.solo.Solo;
import com.sonymobile.sonyselect.fragment.PrefsFragment;

public class SonySelectSearchProdServerTest extends ActivityInstrumentationTestCase2 {

    private static String LOG_TAG = SonySelectSearchProdServerTest.class.getCanonicalName();

    public static String URL_TO_PING = "http://sonyselect-api.sonymobile.com/sonyselect/v3";

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

    public SonySelectSearchProdServerTest() {
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
            if (solo.waitForDialogToOpen()) {
                // Click on Agree
                solo.clickOnView(solo.getView(android.R.id.button1));
            }
            // Assert that: 'ProgressBar' is shown
            solo.waitForView(solo.getView("progressbar"));

            solo.waitForView(solo.getView("action_search"));
            solo.clickOnView(solo.getView("action_search"));

            solo.waitForView(solo.getView("search_src_text"));

            solo.enterText((EditText) solo.getView("search_src_text"), "the");

            assertTrue("No suggestions received?", solo.waitForDialogToOpen());

            solo.pressSoftKeyboardSearchButton();

            assertTrue("Search failed, no list vissible.",
                    solo.waitForView(solo.getView("searchResultGridView")));

            assertTrue("Search failes, no results.", solo.waitForView(ImageView.class, 2, 10000));

            assertTrue("Search failed, no list vissible.",
                    solo.waitForView(solo.getView("searchResultGridView")));

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
