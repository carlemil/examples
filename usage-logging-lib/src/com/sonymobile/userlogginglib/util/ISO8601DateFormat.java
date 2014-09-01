/*
 * Copyright (C) 2013 Sony Mobile Communications AB.
 * All rights, including trade secret rights, reserved.
 */

package com.sonymobile.userlogginglib.util;

import android.text.format.DateUtils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

/**
 * Used to format a date according to ISO 8601.
 */
public class ISO8601DateFormat {

    /**
     * ISO8601 date-time format without time zone
     */
    private static final String ISO_8601_DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ss";

    /**
     * Current timezone for the sytem.
     */
    private static TimeZone timeZone = Calendar.getInstance().getTimeZone();

    /**
     * <p>
     * Formats the given date according to a ISO8601 date on the form
     * YYYY-MM-DDThh:mm:ssZTD, where T is a literal and ZTD is the time zone
     * that is either 'Z', meaning UTC, or +/-hh:mm for other time zones.
     * </p>
     * <p>
     * Since {@link Date} doesn't contain time zone information, the time zone
     * is the system's default time zone.
     * </p>
     *
     * @param date the date to format
     * @return a string representation of the time in ISO-8601 compliant format.
     */
    public static String format(Date date) {
        StringBuilder sb = new StringBuilder(24);
        sb.append(new SimpleDateFormat(ISO_8601_DATE_FORMAT, Locale.US).format(date));
        int offsetInMs = timeZone.getRawOffset();
        if (offsetInMs == 0) {
            sb.append("Z");
        } else {
            // Append offset as time: +HH:MM
            sb.append(offsetInMs > 0 ? '+' : '-');
            long hours = Math.abs(offsetInMs / DateUtils.HOUR_IN_MILLIS);
            long minutes = Math.abs(offsetInMs / DateUtils.MINUTE_IN_MILLIS) % 60;
            sb.append(hours < 10 ? "0" : "");
            sb.append(hours);
            sb.append(":");
            sb.append(minutes < 10 ? "0" : "");
            sb.append(minutes);
        }
        return sb.toString();
    }

}
