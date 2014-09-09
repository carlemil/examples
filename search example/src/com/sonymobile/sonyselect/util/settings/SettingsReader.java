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

import org.xmlpull.v1.XmlPullParserException;

import android.content.Context;

public class SettingsReader {

    private static InputStreamFactory factory = new InputStreamFactory();

    public static SettingsDataStructure loadSettings(Context context) throws IOException, XmlPullParserException {

        InputStream is = null;
        try {
            is = tryOpenCustomSettingsInputStream(context);
            return SettingsParser.parseSettings(is);
        } finally {
            StreamUtil.closeSilently(is);
        }
    }

    private static InputStream tryOpenCustomSettingsInputStream(Context context) throws IOException {
        return factory.openInputStream(context, new CustomSettingsResource());
    }
}
