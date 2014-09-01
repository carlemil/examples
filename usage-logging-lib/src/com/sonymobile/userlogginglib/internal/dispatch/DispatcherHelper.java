/*
 * Copyright (C) 2013 Sony Mobile Communications AB.
 * All rights, including trade secret rights, reserved.
 */

package com.sonymobile.userlogginglib.internal.dispatch;

import com.google.gson.Gson;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;
import com.google.gson.stream.JsonWriter;
import com.sonymobile.userlogginglib.internal.LogEntry;
import com.sonymobile.userlogginglib.util.PhoneInfo;
import com.sonymobile.userlogginglib.util.SharedPreferencesUtil;
import com.sonymobile.userlogginglib.util.Utils;

import android.content.Context;
import android.os.Build;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;

/**
 * Handles networking/uploading of Log data to the server.
 */
public class DispatcherHelper {

    private static final Gson mGson = new Gson();

    private static final String CLIENT_NAME = "client_name";

    private static final String CLIENT_VERSION = "client_version";

    private static final String DEVICE_ID = "device_id";

    private static final String MODEL = "model";

    private static final String MCC = "mcc";

    private static final String MNC = "mnc";

    private static final String LOCALE = "locale";

    private static final String RANDOM_ID = "random_id";

    private static final String DATA = "data";

    private LoggDestination mDestination;

    private JsonWriter mJsonWriter;

    DispatcherHelper(LoggDestination destination) {
        mDestination = destination;
    }

    /**
     * Prepare and send the "header" of the payload to the server.
     *
     * @param context Context used for looking up various parameters/metadata.
     * @throws IOException
     */
    public void prepareForSendingLogEntries(Context context) throws IOException {
        OutputStream out = mDestination.open();
        mJsonWriter = new JsonWriter(new OutputStreamWriter(out, "UTF-8"));

        mJsonWriter.beginObject();
        sendSessionValues(context);

        // Send start of logentry array
        mJsonWriter.name(DATA).beginArray();
    }

    private void sendSessionValues(Context context) throws IOException {
        PhoneInfo phoneInfo = new PhoneInfo(context);
        mJsonWriter.name(CLIENT_NAME).value(SharedPreferencesUtil.getClientName(context));

        mJsonWriter.name(CLIENT_VERSION).value(SharedPreferencesUtil.getClientVersion(context));
        String deviceId = SharedPreferencesUtil.getDeviceId(context);
        if (!Utils.isEmpty(deviceId)) {
            mJsonWriter.name(DEVICE_ID).value(deviceId);
        }
        mJsonWriter.name(MODEL).value(Build.MODEL);
        String mcc = phoneInfo.getMcc();
        if (mcc != null) {
            mJsonWriter.name(MCC).value(mcc);
        }
        String mnc = phoneInfo.getMnc();
        if (mcc != null) {
            mJsonWriter.name(MNC).value(mnc);
        }
        mJsonWriter.name(LOCALE).value(phoneInfo.getLocale());
        mJsonWriter.name(RANDOM_ID).value(SharedPreferencesUtil.getRandomId(context));
    }

    /**
     * Send a {@link LogEntry} to the server.
     *
     * @param data the {@link LogEntry} formated as a json string.
     * @param isLastEntry used to determine if there should be a trailing "," on
     *            the data sent. Needed to maintain correct json.
     * @throws IOException
     */
    public void sendLogEntry(String data, boolean isLastEntry) throws IOException {
        try {
            mGson.toJson(new JsonParser().parse(data), mJsonWriter);
        } catch (JsonParseException e) {
            throw new IOException("Problems adding logentry", e);
        }
    }

    /**
     * Finalize the upload of data to the server.
     *
     * @throws IOException
     */
    public void doneSendingLogEntries() throws IOException {
        mJsonWriter.endArray();
        mJsonWriter.endObject();
        mJsonWriter.close();
        mDestination.close();
    }

}
