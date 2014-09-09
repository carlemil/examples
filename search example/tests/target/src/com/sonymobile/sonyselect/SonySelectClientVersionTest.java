package com.sonymobile.sonyselect;

import android.test.AndroidTestCase;

import com.sonymobile.sonyselect.application.SonySelectApplication;

/**
 * Tests that version number was filtered into {@link SonySelectClientVersion}.
 *
 * @author hugo.josefson@jayway.com
 */
public class SonySelectClientVersionTest extends AndroidTestCase {

    public void testVersionIsNotNull() {
        assertNotNull(SonySelectApplication.getVersionName());
    }

    public void testVersionContainsPeriod() {
        assertTrue(SonySelectApplication.getVersionName().contains("."));
    }
}
