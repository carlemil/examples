/*
 * Copyright (C) 2013 Sony Mobile Communications AB.
 * All rights, including trade secret rights, reserved.
 */

package com.sonymobile.userlogginglib.internal;

import com.sonymobile.userlogginglib.internal.db.DatabaseHelper;
import com.sonymobile.userlogginglib.internal.db.LogEntryHelper;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

/**
 * Wrapper of the {@link ExecutorService} and {@link LinkedBlockingDeque} used
 * for writing {@link LogEntry}'s to the database.
 */
public class Queue {

    private static ExecutorService executor;

    private static LinkedBlockingQueue<LogEntry> queue = new LinkedBlockingQueue<LogEntry>();

    private static Context sContext;

    /**
     * Creates a instance of the {@link Executors} if there is none, and
     * executes all {@link LogEntry}'s found in the {@link LinkedBlockingQueue}.
     *
     * @param context used by the {@link DequeRunnable} to gain access to the
     *            {@link SQLiteDatabase}.
     */
    public synchronized static void init(Context context) {
        Queue.sContext = context;
        if (executor == null) {
            executor = Executors.newSingleThreadExecutor();
            for (LogEntry entry : queue) {
                executor.execute(new EntryWriterTask(context, entry));
            }
            queue.clear();
        }
    }

    /**
     * Stops the {@link ExecutorService}. Waits for the {@link ExecutorService}
     * to terminate before returning (or timeouts after 10 seconds).
     */
    public synchronized static void shutdown() {
        if (executor != null) {
            executor.shutdown();
            try {
                executor.awaitTermination(10, TimeUnit.SECONDS);
            } catch (InterruptedException e) {
            }
            executor = null;
        }
    }

    /**
     * Add a {@link LogEntry} to the {@link ExecutorService} or the
     * {@link LinkedBlockingQueue} if the {@link ExecutorService} is not
     * running.
     *
     * @param entry the {@link LogEntry} to submit to the server.
     */
    public static synchronized void submit(LogEntry entry) {
        if (executor == null) {
            queue.add(entry);
        } else {
            EntryWriterTask task = new EntryWriterTask(sContext, entry);
            executor.execute(task);
        }
    }

    private static class EntryWriterTask implements Runnable {

        private final LogEntry mEntry;

        private final Context mContext;

        public EntryWriterTask(Context context, LogEntry entry) {
            this.mContext = context;
            this.mEntry = entry;
        }

        @Override
        public void run() {
            SQLiteDatabase db = new DatabaseHelper(mContext).getWritableDatabase();
            LogEntryHelper.addLogEntry(db, mEntry);
            db.close();
        }
    }

}
