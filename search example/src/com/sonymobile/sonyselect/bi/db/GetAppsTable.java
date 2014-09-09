/*
 * Copyright (C) 2013 Sony Mobile Communications AB.
 * All rights, including trade secret rights, reserved.
 */

package com.sonymobile.sonyselect.bi.db;

/**
 * This class defines the GetApps table layout.
 */
public class GetAppsTable {

    /**
     * The name of the table
     */
    public static final String NAME = "GetAppsTable";

    /**
     * The name of the columns
     */
    public static final class Columns {

        /**
         * The name of the ID column
         */
        public static final String ID = "_id";

        /**
         * The name of the packagename column
         */
        public static final String PACKAGE_NAME = "package_name";

        /**
         * The name of the timestamp column
         */
        public static final String TIMESTAMP = "timestamp";
    }
}
