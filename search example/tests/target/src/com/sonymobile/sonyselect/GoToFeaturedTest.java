package com.sonymobile.sonyselect;

import com.robotium.solo.*;

import android.test.ActivityInstrumentationTestCase2;
import android.widget.ImageView;


@SuppressWarnings("rawtypes")
public class GoToFeaturedTest extends ActivityInstrumentationTestCase2 {
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
    public GoToFeaturedTest() throws ClassNotFoundException {
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
        // Set default small timeout to 13235 milliseconds
        Timeout.setSmallTimeout(13235);
        assertTrue("Images were not fetched from Server", solo.waitForView(ImageView.class, 2, 120000));
        // Click on Respawnables
        solo.clickInList(1, 0);

        assertTrue("Images were not fetched from Server", solo.waitForView(ImageView.class, 1, 10000));
    }
}
