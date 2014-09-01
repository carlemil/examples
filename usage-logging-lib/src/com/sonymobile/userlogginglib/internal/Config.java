/*
 * Copyright (C) 2013 Sony Mobile Communications AB.
 * All rights, including trade secret rights, reserved.
 */

package com.sonymobile.userlogginglib.internal;

/**
 * Holds config information used by other classes.
 */
public class Config {
    /**
     * MUST be used to wrap ALL Log.x statements within the logger.
     */
    public static final boolean DEBUG = false;

    /**
     * MUST be used in ALL Log.x(LOG_TAG... statements within the logger.
     */
    public static final String LOG_TAG = "Sony Logger";
}
