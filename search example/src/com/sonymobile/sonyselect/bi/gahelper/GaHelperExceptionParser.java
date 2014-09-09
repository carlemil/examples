/*
 * Copyright (c) 2013-2014 Sony Mobile Communications AB.
 * All rights, including trade secret rights, reserved.
 */

/**
 * @file GaHelperExceptionParser.java
 *
 * @author Erling Mårtensson (erling.martensson@sonymobile.com)
 */

package com.sonymobile.sonyselect.bi.gahelper;

import android.content.Context;

import com.google.analytics.tracking.android.EasyTracker;
import com.google.analytics.tracking.android.ExceptionParser;
import com.google.analytics.tracking.android.ExceptionReporter;
import com.google.analytics.tracking.android.GAServiceManager;
import com.google.analytics.tracking.android.Tracker;

import java.util.Arrays;
import java.util.List;

public class GaHelperExceptionParser {

    private static final String LOG_TAG = "GaHelper";

    private static boolean sEnabledExceptionTracking = false;

    // Constants used when doing exception reporting
    private static final int MAX_MESSAGE_LENGTH = 40;

    private static final int MAX_STACK_DEPTH = 10;

    private static final int MAX_CAUSE_DEPTH = 2;

    // "android." is the most commonly used package name, but other package
    // names are also used in frameworks/base. In frameworks only
    // com.google.android is used and not any other variant of com.google.
    private static final List<String> RESERVED_NAMESPACE_PREFIXES = Arrays.asList("android.",
            "com.android", "com.google.android", "java.", "javax.", "sun.");

    /**
     * This method replaces the default uncaught exception parser as to enable a
     * more detailed and consistent Google Analytics reporting. The default GA
     * exception handler only sends the name of the Throwable, no stack trace
     * etc. This method will enable exception tracking regardless of what is
     * stated in the analytics.xml file. If you have the
     * ga_reportUncaughtExceptions set to true this will generate two
     * SendExceptions and thus this should be left in the default value, which
     * is false.
     */
    static public void enableExceptionParsingEasyTracker(Context context) {
        // Set the context first, otherwise it will crash when calling
        // getTracker
        enableExceptionParsing(EasyTracker.getInstance(context), context);
    }

    /**
     * This method replaces the default uncaught exception parser as to enable a
     * more detailed and consistent Google Analytics reporting. This method may
     * be used by apps not using EasyTracker.
     *
     * @param tracker the tracker
     * @context the context
     * @throws IllegalArgumentException if tracker is null
     */
    public static synchronized void enableExceptionParsing(final Tracker tracker, Context context) {

        // Make sure this method is called only once
        if (!sEnabledExceptionTracking) {
            if (tracker == null) {
                throw new IllegalArgumentException("tracker is not allowed to be null");
            }

            sEnabledExceptionTracking = true;

            final ExceptionReporter exceptionReporter = new ExceptionReporter(tracker,
                    GAServiceManager.getInstance(), Thread.getDefaultUncaughtExceptionHandler(),
                    context);

            exceptionReporter.setExceptionParser(new ExceptionParser() {

                @Override
                public String getDescription(String threadName, Throwable t) {
                    return gaStringFromThrowable(threadName, t);
                }
            });

            Thread.setDefaultUncaughtExceptionHandler(exceptionReporter);
        }
    }

    /**
     * This method returns a String in a short format that is suitable for
     * reporting exceptions to Google Analytics.
     * <P>
     * Use this method when you cannot rely on the Uncaught Exception handler
     * reporting.
     *
     * @param threadName The name of the Thread throwing the exception
     * @param t The Throwable being thrown
     * @return A String with the exception call stack in a short format suitable
     *         for google analytics reporting.
     */
    public static String gaStringFromThrowable(String threadName, Throwable t) {
        StringBuilder description = new StringBuilder();

        // Use try, catch as to avoid exceptions being thrown
        try {
            // Create the throwable description
            // Start by adding the thread name, but remove trailing
            // numbers as to merge automatically enumerated threads
            description = description.append("T:").append(removeTrailingNumbers(threadName))
                    .append(" ")
                    // and then add info about the exception possible causes
                    // and then file, method and line.
                    // Initially cause depth is 0.
                    .append(createThrowableDescription(t, 0));

            GaHelperLog.d(LOG_TAG, "uncaughtException: " + description);
        } catch (Exception e) {
            // Any unexpected exceptions will be taken care of here
            GaHelperLog.e(LOG_TAG, "internal exception : " + e.getMessage());
        }

        return description.toString();
    }

    /**
     * This method creates a description string for the throwable
     *
     * @param t The throwable
     * @param depth The max depth in the cause depth to use
     */
    private static StringBuilder createThrowableDescription(Throwable t, int depth) {
        StringBuilder retval = new StringBuilder();

        if (depth <= MAX_CAUSE_DEPTH) {
            // Add the short description
            retval = createThrowableDescriptionShort(t);

            // Was there a cause?
            Throwable cause = t.getCause();
            if (cause != null) {
                // Yes, there was a cause!
                retval = retval.append(" Cause: ");
                retval = retval.append(createThrowableDescription(cause, depth + 1));
            } else {
                // No cause, then add the stack trace
                retval = retval.append(createThrowableDescriptionStackTrace(t));
            }
        } else {
            // Depth is too high, don't go further, just add dots and the stack
            // trace
            retval = retval.append("... ").append(createThrowableDescriptionStackTrace(t));
        }

        return retval;
    }

    /**
     * This method creates a short substring of the class name and the optional
     * message string. *
     *
     * @param t The throwable
     */
    private static StringBuilder createThrowableDescriptionShort(Throwable t) {
        String className = t.getClass().getSimpleName();
        // get the detailed string that may have been provided when
        // this exception was thrown
        StringBuilder retval = new StringBuilder(className);

        String message = t.getMessage();
        if (message != null) {
            // if we have a message append that.
            // Truncate to the allowed max as not to waste space, this
            // information is not the most important information
            message = message.substring(0, Math.min(message.length(), MAX_MESSAGE_LENGTH));
            retval = retval.append("(").append(message).append(")");
        }

        return retval;
    }

    /**
     * This method creates a minimal one line stack trace. It tries to skip
     * items related to information in the framework
     *
     * @param t The throwable
     */
    private static String createThrowableDescriptionStackTrace(Throwable t) {
        return gaStringFromStackTrace(t.getStackTrace());
    }

    /**
     * This method returns a String in a short format that is suitable for
     * reporting exceptions to Google Analytics.
     * <P>
     * Use this method when having an Uncaught Exception Handler can not be
     * applied.
     *
     * @param stackTrace The StackTrace to be converted to a String
     * @return A String of the exception stack in short format
     */
    public static String gaStringFromStackTrace(final StackTraceElement[] stackTrace) {
        StringBuilder builder = new StringBuilder();

        // Try to find the most useful line in the stack trace.
        // Avoid rows starting with package name "android."
        // Go to max depth but not more, then something is too complex anyway
        int stackTraceIndex = 0;
        boolean done = false;
        final int stackTraceLength = stackTrace.length;

        while (!done && stackTraceIndex < stackTraceLength && stackTraceIndex < MAX_STACK_DEPTH) {
            StackTraceElement stackElement = stackTrace[stackTraceIndex];

            if (stackElement != null) {
                // We have a valid entry
                // Check the package name
                String classname = stackElement.getClassName();
                if (packageNameIsReserved(classname)) {
                    // This is using of the reserved package names
                    // e.g. "android." , skip it, try the next
                    stackTraceIndex++;
                } else {
                    // So this was not android name space. Use this index.
                    done = true;
                }
            } else {
                // No point in doing more
                done = true;
            }
        }

        // If nothing was found use row 0. It's probably not perfect but
        // a reasonable starting point
        if (stackTraceIndex == MAX_STACK_DEPTH || stackTraceIndex >= stackTraceLength) {
            stackTraceIndex = 0;
        }

        StackTraceElement stackElement = stackTrace[stackTraceIndex];

        // Create a description in this format:
        // F: file name
        // M: method name
        // L: line
        // R: row used in stack trace
        if (stackElement != null) {
            builder = builder.append(" F:").append(stackElement.getFileName()).append(" M:")
                    .append(stackElement.getMethodName()).append(" L:")
                    .append(stackElement.getLineNumber()).append(" R:")
                    .append(String.valueOf(stackTraceIndex));
        } else {
            // No stackTrace, odd, just add ? to indicate this
            builder = builder.append(" ?");
        }

        return builder.toString();
    }

    /**
     * This method detects if the class name provided is one of the reserved
     * package names or not.
     *
     */
    private static boolean packageNameIsReserved(String className) {
        boolean found = false;
        int index = 0;
        while (!found && index < RESERVED_NAMESPACE_PREFIXES.size()) {
            if (className.startsWith(RESERVED_NAMESPACE_PREFIXES.get(index))) {
                found = true;
            } else {
                index++;
            }
        }
        return found;
    }

    /**
     * Remove trailing numbers from a string.
     *
     * @param input string
     */
    private static String removeTrailingNumbers(String input) {
        boolean done = false;
        int length;
        char lastCharacter;
        String retval = input;

        // The documentation doesn't clearly say if thread name
        // may be null.Assume the worst.
        if (retval == null) {
            retval = new String("<unknown thread>");
        }

        while (!done && retval.length() > 0) {
            length = retval.length();
            lastCharacter = retval.charAt(length - 1);

            // Is the last character a number?
            if (Character.isDigit(lastCharacter)) {
                // Yes, it is number, remove it
                retval = retval.substring(0, length - 1);
            } else {
                // No, we're done
                done = true;
            }
        }

        // If the thread name would be all numbers use the original input
        if (retval.length() == 0) {
            retval = input;
        }

        return retval;
    }
}
