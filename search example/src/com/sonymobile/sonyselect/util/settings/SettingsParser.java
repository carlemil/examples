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

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import android.util.Log;

public class SettingsParser {
    
    private static final String LOG_TAG = SettingsParser.class.getName();

    private static final String SETTING_TAG = "setting";

    private static final String KEY_ATTRIBUTE = "key";

    public static SettingsDataStructure parseSettings(InputStream is) throws XmlPullParserException, IOException {
        return parseSettingsFile(is);
    }

    private static SettingsDataStructure parseSettingsFile(InputStream stream) throws XmlPullParserException, IOException {
        SettingsDataStructure settingsData = new SettingsDataStructure();
        XmlPullParserFactory f = XmlPullParserFactory.newInstance();
        XmlPullParser parser = f.newPullParser();
        parser.setInput(stream, "UTF-8");
        int eventType = parser.getEventType();
        do {
            if (eventType == XmlPullParser.START_TAG) {
                if (parser.getName().equals(SETTING_TAG)) {
                    parseRootSetting(parser, settingsData);
                }
            }
            eventType = parser.next();
        } while (eventType != XmlPullParser.END_DOCUMENT);
        return settingsData;
    }

    private static void parseRootSetting(XmlPullParser parser, SettingsDataStructure settingsData) throws XmlPullParserException, IOException {
        String settingName = parser.getAttributeValue(null, KEY_ATTRIBUTE);
        
        if (settingName.equals("disableDisclaimer")) {
            settingsData.disableDisclaimer = Boolean.parseBoolean(parser.nextText());
        } else {
            Log.w(LOG_TAG, "Unrecognised setting in settings file: " + settingName);
        }
    }
}
