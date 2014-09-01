/*
 * Copyright (C) 2013 Sony Mobile Communications AB.
 * All rights, including trade secret rights, reserved.
 */

package com.sonymobile.userlogginglib.internal.db;

/**
 * Facade towards the database.
 */
public interface LogDb {

    /**
     * Opens the database. Don't forget to close it when you're done!
     */
    void open();

    /**
     * Closes the database. All method calls (except for {@link #open()}) will
     * probably throw {@link NullPointerException} after this call.
     */
    void close();

    /**
     * Checks if the database is empty.
     *
     * @return <code>true</code> if the database is empty, <code>false</code>
     *         otherwise.
     */
    boolean isEmpty();

    /**
     * Deletes everything in the database.
     */
    void dropAll();

}
