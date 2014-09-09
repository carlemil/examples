package com.sonymobile.sonyselect;

import com.robotium.solo.*;

import android.test.ActivityInstrumentationTestCase2;


@SuppressWarnings("rawtypes")
public class CheckClickShareTest extends ActivityInstrumentationTestCase2 {
    private Solo solo;

    private static final String LAUNCHER_ACTIVITY_FULL_CLASSNAME = "com.sonymobile.sonyselect.activities.LaunchActivity";

    private static Class<?> launcherActivityClass;

    static {
        try {
            launcherActivityClass = Class.forName(LAUNCHER_ACTIVITY_FULL_CLASSNAME);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    @SuppressWarnings("unchecked")
    public CheckClickShareTest() throws ClassNotFoundException {
        super(launcherActivityClass);
    }

    public void setUp() throws Exception {
        solo = new Solo(getInstrumentation());
        getActivity();
    }

    @Override
    public void tearDown() throws Exception {
        solo.finishOpenedActivities();
    }

    public void testRun() {
        // Wait for activity: 'com.sonymobile.sonyselect.activities.LaunchActivity'
        assertTrue("LaunchActivity is not found!", solo.waitForActivity("LaunchActivity"));
        // Click on Respawnables
        solo.clickInList(1, 1);
        // Click on ImageView
        solo.clickOnView(solo.getView("share"));
        // Wait for activity: 'com.android.internal.app.ChooserActivity'
        assertTrue("ChooserActivity is not found!", solo.waitForActivity("ChooserActivity"));
        // Press menu back key
        solo.goBack();
    }
}
