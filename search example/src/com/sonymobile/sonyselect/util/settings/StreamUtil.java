package com.sonymobile.sonyselect.util.settings;

import java.io.Closeable;
import java.io.IOException;
import java.net.HttpURLConnection;

/**
 * {@link StreamUtil} helps with handling streams
 * 
 * @author albintheander
 * 
 */
public class StreamUtil {

    /**
     * Closes the specified stream and catches any exceptions. Handles
     * <code>null</code> gracefully.
     * 
     * @param stream
     *            the stream to close, may be <code>null</code>
     */
    public static void closeSilently(Closeable stream) {
        if (stream == null) {
            return;
        }
        try {
            stream.close();
        } catch (IOException e) {
            // Be silent...
        }
    }

    /**
     * Disconnects the specified HttpUrlConnection. Handles <code>null</code>
     * gracefully.
     * 
     * @param connection
     *            The connection to close, may be <code>null</code>
     */
    public static void closeSilently(HttpURLConnection connection) {
        if (connection != null) {
            connection.disconnect();
        }
    }

}
