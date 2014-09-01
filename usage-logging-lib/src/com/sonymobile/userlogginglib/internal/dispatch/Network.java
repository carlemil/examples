/*
 * Copyright (C) 2013 Sony Mobile Communications AB.
 * All rights, including trade secret rights, reserved.
 */

package com.sonymobile.userlogginglib.internal.dispatch;

import com.sonymobile.userlogginglib.internal.Config;

import android.util.Log;

import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class Network implements LoggDestination {

    private final String mRootUrl;

    private final String mApiKey;

    private HttpURLConnection connection;

    public Network(String rootUrl, String apiKey) {
        mRootUrl = rootUrl;
        mApiKey = apiKey;
    }

    public OutputStream open() throws IOException {
        if (Config.DEBUG) {
            Log.d(Config.LOG_TAG, "Open a connection to: " + mRootUrl);
        }
        connection = (HttpURLConnection)new URL(mRootUrl).openConnection();
        connection.setRequestProperty("Api-Key", mApiKey);
        connection.setDoOutput(true);
        return connection.getOutputStream();
    }

    public void close() throws IOException {
        if (Config.DEBUG) {
            Log.d(Config.LOG_TAG, "Close the connection to: " + mRootUrl);
        }
        int responseCode = connection.getResponseCode();
        connection.disconnect();
        if (responseCode != HttpURLConnection.HTTP_OK) {
            throw new IOException("Wrong response code from server: " + responseCode);
        }
    }

}
