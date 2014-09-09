/*
 * Copyright (c) 2013 Sony Mobile Communications AB.
 * All rights, including trade secret rights, reserved.
 */

package com.sonymobile.sonyselect;

import com.sonymobile.sonyselect.activities.LaunchActivity;
import com.sonymobile.sonyselect.bi.db.DatabaseHelper;
import com.sonymobile.sonyselect.bi.db.GetAppsHelper;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.StrictMode;
import android.test.AndroidTestCase;

import java.lang.reflect.Method;

public class ZGetAppsHelperTest extends AndroidTestCase {

    SQLiteDatabase mDB;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        Context context = getContext();
        DatabaseHelper databaseHelper = new DatabaseHelper(context);

        mDB = databaseHelper.getWritableDatabase();

        Method method = databaseHelper.getClass().getDeclaredMethod("reset", mDB.getClass());
        method.setAccessible(true);
        method.invoke(databaseHelper, mDB);

        StrictMode.ThreadPolicy threadPolicy = new StrictMode.ThreadPolicy.Builder()
                .detectAll()
                .penaltyLog()
                .build();
        StrictMode.setThreadPolicy(threadPolicy);

        StrictMode.VmPolicy vmPolicy = new StrictMode.VmPolicy.Builder()
                .detectAll()
                .detectActivityLeaks()
//                .detectFileUriExposure() // requires api level 18
                .detectLeakedClosableObjects()
                .detectLeakedRegistrationObjects()
                .detectLeakedSqlLiteObjects()
                .setClassInstanceLimit(LaunchActivity.class, 9) // Several Activities will be instantiated because jvm won't have had time to garbage collect .
                .penaltyLog()
                .build();
        StrictMode.setVmPolicy(vmPolicy);

    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
        // Set back the StrictMode to standard
        StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder().detectAll().penaltyLog().build());
        StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder().detectAll().setClassInstanceLimit(LaunchActivity.class, 3).penaltyLog().build());
    }

    public void testInsert() {
        GetAppsHelper.insert(mDB, "org.package.testing1", 123);
        Cursor cursor = GetAppsHelper.getAllRows(mDB);
        assertEquals(1, cursor.getCount());
        cursor.close();
        GetAppsHelper.insert(mDB, "org.package.testing2", 123);
        cursor = GetAppsHelper.getAllRows(mDB);
        assertEquals(2, cursor.getCount());
        cursor.close();
    }

    public void testInsertDuplicatePackageName() {
        String packageName = "org.package.testing";
        GetAppsHelper.insert(mDB, packageName, 123);
        Cursor cursor = GetAppsHelper.getAllRows(mDB);
        assertEquals(1, cursor.getCount());
        cursor.close();
        GetAppsHelper.insert(mDB, packageName, 321);
        cursor = GetAppsHelper.getAllRows(mDB);
        assertEquals(1, cursor.getCount());
        cursor.close();
        long t = GetAppsHelper.getTimestampForPackageName(mDB, packageName);
        assertEquals(t, 321);
    }

    public void testGetAllRows() {
        GetAppsHelper.insert(mDB, "org.package.testing1", 123);
        Cursor c = GetAppsHelper.getAllRows(mDB);
        assertTrue(c.getCount() == 1);
        c.close();

        GetAppsHelper.insert(mDB, "org.package.testing2", 123);
        c = GetAppsHelper.getAllRows(mDB);
        assertTrue(c.getCount() == 2);
        c.close();
    }

    public void testGetByPackageName() {
        String packageName1 = "org.package.testing1";
        String packageName2 = "org.package.testing2";
        GetAppsHelper.insert(mDB, packageName1, 1234);
        GetAppsHelper.insert(mDB, packageName2, 12345);

        long c = GetAppsHelper.getTimestampForPackageName(mDB, packageName1);
        assertEquals(c, 1234);
    }

    public void testDeleteByPackageName() {
        GetAppsHelper.insert(mDB, "org.package.testing", 123);
        int r = GetAppsHelper.deleteByPackageName(mDB, "org.package.testing");
        assertTrue(r == 1);

    }

    public void testDeleteAllRowsInGetAppsData() {
        GetAppsHelper.insert(mDB, "org.package.testing1", 1);
        GetAppsHelper.insert(mDB, "org.package.testing2", 12);
        GetAppsHelper.insert(mDB, "org.package.testing3", 123);
        GetAppsHelper.insert(mDB, "org.package.testing4", 1234);
        GetAppsHelper.insert(mDB, "org.package.testing5", 12345);

        int r = GetAppsHelper.deleteAllRows(mDB);
        assertTrue(r == 5);
    }

    public void testDeleteAllOldRowsInGetAppsData() {
        int r;
        GetAppsHelper.insert(mDB, "org.package.testing1", -100);
        GetAppsHelper.insert(mDB, "org.package.testing2", 302);
        GetAppsHelper.insert(mDB, "org.package.testing3", 423);
        GetAppsHelper.insert(mDB, "org.package.testing4", 534);
        GetAppsHelper.insert(mDB, "org.package.testing5", 1545);
        r = GetAppsHelper.deleteAllOldRows(mDB, 400);
        assertEquals(2, r);
    }

    public void testIsEmpty() {
        assertTrue(GetAppsHelper.isEmpty(mDB));
        GetAppsHelper.insert(mDB, "org.package.testing1", 1);
        assertTrue(!GetAppsHelper.isEmpty(mDB));
        GetAppsHelper.insert(mDB, "org.package.testing2", 12);
        assertTrue(!GetAppsHelper.isEmpty(mDB));
        GetAppsHelper.deleteByPackageName(mDB, "org.package.testing1");
        assertTrue(!GetAppsHelper.isEmpty(mDB));
        GetAppsHelper.deleteByPackageName(mDB, "org.package.testing2");
        assertTrue(GetAppsHelper.isEmpty(mDB));
    }

}
