
package com.sonymobile.sonyselect;

import com.robotium.solo.*;

import android.test.ActivityInstrumentationTestCase2;
import android.widget.ImageView;

@SuppressWarnings("rawtypes")
public class AGoToDetailedViewTest extends ActivityInstrumentationTestCase2 {
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
    public AGoToDetailedViewTest() throws ClassNotFoundException {
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
        // Wait for activity:
        // 'com.sonymobile.sonyselect.activities.LaunchActivity'
        assertTrue("LaunchActivity is not found!", solo.waitForActivity("LaunchActivity"));
        // Wait for dialog
        if (solo.waitForDialogToOpen(30000)) {
            // Click on Agree
            // solo.clickOnText("Continue");
            solo.clickOnView(solo.getView(android.R.id.button1));
        }
        // Assert that: 'ProgressBar' is shown
        assertTrue("'ProgressBar' is not shown!", solo.waitForView(solo.getView("progressbar")));
        // Set default small timeout to 15000 milliseconds
        Timeout.setSmallTimeout(15000);
        // Wait until first image is shown
        assertTrue("Images were not fetched from Server",
                solo.waitForView(ImageView.class, 2, 120000));
        // Click on Elf Punt FULL FREE
        solo.clickInList(1, 1);
        // Assert that: 'View' is shown
        assertTrue("Images were not fetched from Server",
                solo.waitForView(ImageView.class, 1, 10000));
        // Assert that: 'ImageView' is shown

        // Assert that: 'Elf Punt FULL FREE' is shown

        // Click on Get
        solo.clickOnView(solo.getView("get"));
    }
}
