/*********************************************************************
 *       ____                      __  __       _     _ _            *
 *      / ___|  ___  _ __  _   _  |  \/  | ___ | |__ (_) | ___       *
 *      \___ \ / _ \| '_ \| | | | | \  / |/ _ \| '_ \| | |/ _ \      *
 *       ___) | (_) | | | | |_| | | |\/| | (_) | |_) | | |  __/      *
 *      |____/ \___/|_| |_|\__, | |_|  |_|\___/|_.__/|_|_|\___|      *
 *                         |___/                                     *
 *                                                                   *
 *********************************************************************
 *      Copyright 2013 Sony Mobile Communications AB.                *
 *      All rights, including trade secret rights, reserved.         *
 *********************************************************************/

package com.sonymobile.sonyselect.util.settings;

import java.io.IOException;
import java.io.InputStream;

import android.content.ContentResolver;
import android.content.Context;
import android.net.Uri;
import android.util.Log;

public class InputStreamFactory {

    private static final String LOG_TAG = InputStreamFactory.class.getName();

    private static final String CONTENT = "content";

    /**
     * Returns the proper input stream based on the protocol of the url.
     * 
     * @param context
     * @param customSettingsResource
     *            an CustomSettingsResource object
     * @return an open InputStream. Note that the calling client is responsible
     *         for closing the InputStream.
     * @throws IllegalArgumentException
     *             if the url related to the Downloadable is malformed.
     * @throws IOException
     *             if there was a problem opening the InputStream.
     */
    public InputStream openInputStream(Context context, CustomSettingsResource customSettingsResource) throws IllegalArgumentException, IOException {

        InputStream is = null;

        String protocol = customSettingsResource.getProtocol();
        if (protocol.equalsIgnoreCase(CONTENT)) {

            Log.d(LOG_TAG, "InputStreamFactory is opening InputStream for CONTENT connection...");

            Uri uri = Uri.parse(customSettingsResource.getUri());
            ContentResolver cr = context.getContentResolver();

            Log.d(LOG_TAG, "InputStreamFactory is using uri: " + uri);

            is = NetworkBuffer.bufferInputStream(cr.openInputStream(uri));

        } else {
            throw new IllegalArgumentException("InputStreamFactory cannot open input stream. Specified protocol not supported: " + protocol + ", URL: " + customSettingsResource.getUri());
        }

        return is;
    }
}
