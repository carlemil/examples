/*
 * Copyright (c) 2013 Sony Mobile Communications AB.
 * All rights, including trade secret rights, reserved.
 */

package com.sonymobile.userlogginglib.test;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.sonymobile.userlogginglib.Logger;
import com.sonymobile.userlogginglib.internal.Queue;
import com.sonymobile.userlogginglib.internal.db.DatabaseHelper;
import com.sonymobile.userlogginglib.internal.dispatch.Dispatcher;
import com.sonymobile.userlogginglib.internal.dispatch.LoggDestination;
import com.sonymobile.userlogginglib.internal.dispatch.Network;

import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.os.StrictMode;
import android.test.AndroidTestCase;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.lang.reflect.Method;

public class UDispatcherTest extends AndroidTestCase {

    private SQLiteDatabase mDB;

    @Mock
    private LoggDestination destination;

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

    public void testDispatchSuccess() throws IOException {

        Logger logger = new Logger();

        logger.log("test-entry").with("animal", "anteater").submit();
        logger.log("test-entry").with("fruit", "banana").submit();

        logger.init(getContext(), "https://log.entrance.sonymobile.com/log",
                "0c9CrB9r28sws8tZWxsSU3rce0bmIWX9", "testclient", "1", null);

        Queue.shutdown();

        ByteArrayOutputStream sentData = new ByteArrayOutputStream();
        Mockito.when(destination.open()).thenReturn(sentData);

        Dispatcher dispatcher = new Dispatcher(destination);
        dispatcher.dispatch(getContext());

        JsonElement sentLog = new JsonParser().parse(new String(sentData.toByteArray()));

        JsonArray data = sentLog.getAsJsonObject().getAsJsonArray("data");
        assertEquals(2, data.size());
        assertEquals("anteater", data.get(0).getAsJsonObject().getAsJsonPrimitive("animal")
                .getAsString());

    }

    // This test case can be un-ignored to test sending data to the
    // real server.
    public void ignored_testDispatchRealServer() throws IOException {

        Logger logger = new Logger();

        logger.log("test-event").with("animal", "anteater").submit();
        logger.log("test-event").with("fruit", "banana").submit();

        logger.init(getContext(), "https://log.entrance.sonymobile.com/log",
                "0c9CrB9r28sws8tZWxsSU3rce0bmIWX9", "testclient", "1", null);

        Queue.shutdown();

        LoggDestination network = new Network("https://log.entrance.sonymobile.com/log",
                "0c9CrB9r28sws8tZWxsSU3rce0bmIWX9");

        // network = new LogCatDestination();

        Dispatcher dispatcher = new Dispatcher(network);
        dispatcher.dispatch(getContext());

    }

}
