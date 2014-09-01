/*
 * Copyright (C) 2013 Sony Mobile Communications AB.
 * All rights, including trade secret rights, reserved.
 */

package com.sonymobile.userlogginglib.internal.dispatch;

import com.sonymobile.userlogginglib.internal.Config;
import com.sonymobile.userlogginglib.internal.db.DatabaseHelper;
import com.sonymobile.userlogginglib.internal.db.LogEntryHelper;
import com.sonymobile.userlogginglib.util.CursorUtil;
import com.sonymobile.userlogginglib.util.Utils;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.io.IOException;

/**
 * Reads data from the {@link SQLiteDatabase} and dispatches to the server.
 * After dispatching the data it will empty the database. If the server fails in
 * receiving the data the data will be lost.
 */
public class Dispatcher {

    private final DispatcherHelper mDispatcherHelper;

    public Dispatcher(LoggDestination destination) {
        mDispatcherHelper = new DispatcherHelper(destination);
    }

    /**
     * Reads data from the {@link SQLiteDatabase} and sends it to the server,
     * then deletes the local data.
     *
     * @param context {@link Context} for getting hold of the
     *            {@link SQLiteDatabase}.
     * @return true if the dispatch was successful.
     */
    public boolean dispatch(Context context) {
        SQLiteDatabase db = new DatabaseHelper(context).getWritableDatabase();

        int sentEntries = sendEntries(context, db);
        if (sentEntries > 0) {
            deleteAllEntries(db);
        }
        db.close();

        return sentEntries > 0;
    }

    private void deleteAllEntries(SQLiteDatabase db) {
        int res = LogEntryHelper.deleteAllLogEntries(db);
        if (Config.DEBUG) {
            Log.d(Config.LOG_TAG, "Deleted a total of " + res + " LogEntrys from database.");
        }
    }

    public int sendEntries(Context context, SQLiteDatabase db) {
        int count = 0;
        Cursor cursor = null;
        try {
            cursor = LogEntryHelper.getAllLogEntries(db);
            if (Utils.isEmpty(cursor)) {
                return 0;
            }
            mDispatcherHelper.prepareForSendingLogEntries(context);
            do {
                String data = LogEntryHelper.getDataFromCursor(cursor);
                if (data != null) {
                    mDispatcherHelper.sendLogEntry(data, cursor.isLast());
                }
                count++;
            } while (cursor.moveToNext());
            mDispatcherHelper.doneSendingLogEntries();
        } catch (IOException e) {
            if (Config.DEBUG) {
                Log.w(Config.LOG_TAG, "Problems sending entries/connecting to server.", e);
            }
            return 0;
        } catch (SQLException e) {
            if (Config.DEBUG) {
                Log.w(Config.LOG_TAG, "Problems sending entries.", e);
            }
            return 0;
        } finally {
            CursorUtil.closeSilently(cursor);
        }
        return count;
    }
}
