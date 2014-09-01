/*
 * Copyright (C) 2013 Sony Mobile Communications AB.
 * All rights, including trade secret rights, reserved.
 */

package com.sonymobile.userlogginglib.internal.dispatch;

import com.sonymobile.userlogginglib.internal.Config;

import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public class LogCatDestination implements LoggDestination {

    private ByteArrayOutputStream out;

    public OutputStream open() {
        out = new ByteArrayOutputStream();
        return out;
    }

    public void close() {
        try {
            out.close();
            Log.d(Config.LOG_TAG, "Sent log data:");
            Log.d(Config.LOG_TAG, new String(out.toByteArray()));
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

}
