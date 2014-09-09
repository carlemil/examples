package com.sonymobile.sonyselect;

import android.content.Context;
import android.test.ActivityInstrumentationTestCase2;
import android.test.suitebuilder.annotation.Suppress;

import com.sonymobile.sonyselect.activities.LaunchActivity;
import com.sonymobile.sonyselect.util.Settings;

public class SettingsTest extends ActivityInstrumentationTestCase2<LaunchActivity> {

    private Context mockContext;

    public SettingsTest() {
        super(LaunchActivity.class);
    }

    @Override
    public void setUp() throws Exception {
        mockContext = getActivity();
    }

    @Suppress
    public void testShareUsageDataFirstTime() {
        Settings.reset(mockContext);
        assertFalse(Settings.shareUsageDataExists(mockContext));
    }

    @Suppress
    public void testShareUsageDataIsAccepted() {
        Settings.setShareUsageDataAccepted(mockContext, true);
        assertFalse(Settings.isShareUsageDataAccepted(mockContext));
    }

    @Suppress
    public void testShareUsageDataNotAccepted() {
        Settings.setShareUsageDataAccepted(mockContext, false);
        assertFalse(Settings.isShareUsageDataAccepted(mockContext));
    }

    public void testShareUsageDataAccepted() {
        Settings.setShareUsageDataAccepted(mockContext, true);
        assertTrue(Settings.isShareUsageDataAccepted(mockContext));
    }

    public void testShareUsageDataNotFirstTime() {
        Settings.setShareUsageDataAccepted(mockContext, true);
        assertTrue(Settings.shareUsageDataExists(mockContext));
    }
}
