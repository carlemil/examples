/*
 * Copyright (c) 2013 Sony Mobile Communications AB.
 * All rights, including trade secret rights, reserved.
 */

package com.sonymobile.userlogginglib.test;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.sonymobile.userlogginglib.Logger;
import com.sonymobile.userlogginglib.internal.LogEntry.Builder;
import com.sonymobile.userlogginglib.internal.Queue;
import com.sonymobile.userlogginglib.internal.db.DatabaseHelper;
import com.sonymobile.userlogginglib.internal.db.LogEntryHelper;

import org.mockito.MockitoAnnotations;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.StrictMode;
import android.test.AndroidTestCase;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

public class ULoggerTest extends AndroidTestCase {

    SQLiteDatabase mDB;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        MockitoAnnotations.initMocks(this);

        Context context = getContext();
        DatabaseHelper databaseHelper = new DatabaseHelper(context);
        mDB = databaseHelper.getWritableDatabase();

        Method method = databaseHelper.getClass().getDeclaredMethod("reset", mDB.getClass());
        method.setAccessible(true);
        method.invoke(databaseHelper, mDB);

        StrictMode.ThreadPolicy threadPolicy = new StrictMode.ThreadPolicy.Builder().detectAll()
                .penaltyLog().penaltyDeath().build();
        StrictMode.setThreadPolicy(threadPolicy);

        StrictMode.VmPolicy vmPolicy = new StrictMode.VmPolicy.Builder().detectAll().penaltyLog()
                .penaltyDeath().build();
        StrictMode.setVmPolicy(vmPolicy);

    }

    public void testAddLogSuccess() {

        Map<String, String> additionalParams = new HashMap<String, String>();
        additionalParams.put("test_param", "test_value");
        Logger logger = new Logger();
        logger.init(getContext(), "rootUrl", "APIKey", "testclient", "1", null);

        logger.log("test-entry").with("key", "value").submit();

        Queue.shutdown();

        assertKeyValuePairsInFirstLogEntryInDb("type", "test-entry", "key", "value");
    }

    public void testAddLogBeforeInitSuccess() {

        Map<String, String> additionalParams = new HashMap<String, String>();
        additionalParams.put("test_param", "test_value");
        Logger logger = new Logger();

        logger.log("test-entry").with("key", "value").submit();

        logger.init(getContext(), "rootUrl", "APIKey", "testclient", "1", null);

        Queue.shutdown();

        assertKeyValuePairsInFirstLogEntryInDb("type", "test-entry", "key", "value");
    }

    public void testAddLogAdditionalNullParamsSuccess() {

        Map<String, String> additionalParams = new HashMap<String, String>();
        additionalParams.put("test_param", "test_value");
        Logger logger = new Logger();
        logger.init(getContext(), "rootUrl", "APIKey", "testclient", "1", null);
        logger.log("test-entry").with("notnull", null).with(null, "notnull").with("key", "value")
                .submit();

        Queue.shutdown();

        assertKeyValuePairsInFirstLogEntryInDb("type", "test-entry", "key", "value");
    }

    public void testLogEntriesWithNullTypeAreDiscarded() {

        Map<String, String> additionalParams = new HashMap<String, String>();
        additionalParams.put("test_param", "test_value");
        Logger logger = new Logger();
        logger.init(getContext(), "rootUrl", "APIKey", "testclient", "1", null);
        logger.log(null).submit();
        logger.log(null).with("key", "value").submit();

        Queue.shutdown();

        Cursor cursor = LogEntryHelper.getAllLogEntries(mDB);
        assertFalse(cursor.moveToFirst());
        cursor.close();
    }

    public void testAddLogAfterSubmitFail() {

        Map<String, String> additionalParams = new HashMap<String, String>();
        additionalParams.put("test_param", "test_value");
        Logger logger = new Logger();
        logger.init(getContext(), "rootUrl", "APIKey", "testclient", "1", null);

        Builder logEntry = logger.log("test-entry");
        logEntry.submit();
        logEntry.with("key", "value").submit();

        Queue.shutdown();

        Cursor cursor = LogEntryHelper.getAllLogEntries(mDB);
        assertTrue(cursor.moveToFirst());
        String data = LogEntryHelper.getDataFromCursor(cursor);
        cursor.close();
        Type mapType = new TypeToken<Map<String, String>>() {
        }.getType();
        Map<String, String> logEntryMap = new Gson().fromJson(data, mapType);

        assertFalse(logEntryMap.containsKey("key"));
        assertFalse(logEntryMap.containsValue("value"));
    }

    public void testAddLogWithLogCatLoggerDoesNotAddLogToDB() {

        Map<String, String> additionalParams = new HashMap<String, String>();
        additionalParams.put("test_param", "test_value");
        Logger logger = new Logger();
        logger.init(getContext(), "rootUrl", "APIKey", "testclient", "1", null);

        // Set a empty 'type' to force the usage of the LogToLogCatBuilder
        Builder logEntry = logger.log("");
        logEntry.with("key", "value").submit();

        Queue.shutdown();

        Cursor cursor = LogEntryHelper.getAllLogEntries(mDB);
        assertFalse(cursor.moveToFirst());
        cursor.close();
    }

    /**
     * Asserts that the provided key-value pairs are available in the first log
     * entry in the data base. The parameters are keys and values interleaved:
     *
     * <pre>
     * assertKeyValuePairs(&quot;key1&quot;, &quot;value1&quot;, &quot;key2&quot;, &quot;value2&quot;);
     * </pre>
     *
     * @param keyValue the pairs of keys and values.
     */
    private void assertKeyValuePairsInFirstLogEntryInDb(String... keyValue) {
        Cursor cursor = LogEntryHelper.getAllLogEntries(mDB);
        assertTrue(cursor.moveToFirst());
        String data = LogEntryHelper.getDataFromCursor(cursor);
        cursor.close();
        Type mapType = new TypeToken<Map<String, String>>() {
        }.getType();
        Map<String, String> logEntryMap = new Gson().fromJson(data, mapType);
        int i = 0;
        while (i < keyValue.length) {
            String key = keyValue[i++];
            String expectedValue = keyValue[i++];
            assertTrue("Key " + key + "not found in log entry", logEntryMap.containsKey(key));
            String actualValue = logEntryMap.get(key);
            assertEquals("Expected " + expectedValue + " but got " + actualValue + " for key "
                    + key, expectedValue, actualValue);
        }
    }

}
