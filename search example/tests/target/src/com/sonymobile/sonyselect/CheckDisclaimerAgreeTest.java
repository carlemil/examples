package com.sonymobile.sonyselect;

import com.robotium.solo.*;

import android.test.ActivityInstrumentationTestCase2;
import android.widget.ImageView;


@SuppressWarnings("rawtypes")
public class CheckDisclaimerAgreeTest extends ActivityInstrumentationTestCase2 {
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
    public CheckDisclaimerAgreeTest() throws ClassNotFoundException {
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

        solo.waitForView(ImageView.class, 2, 120000);

        // Click on action bar item
        solo.clickOnActionBarItem(R.id.disclaimer);
        // Wait for dialog
        solo.waitForDialogToOpen();
        // Click on Agree
        solo.clickOnView(solo.getView(android.R.id.button1));
    }
}
