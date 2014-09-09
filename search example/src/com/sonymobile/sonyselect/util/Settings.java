package com.sonymobile.sonyselect.util;

import java.io.FileNotFoundException;
import java.io.IOException;

import org.xmlpull.v1.XmlPullParserException;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.sonymobile.sonyselect.R;
import com.sonymobile.sonyselect.util.settings.SettingsDataStructure;
import com.sonymobile.sonyselect.util.settings.SettingsReader;

public final class Settings {

    public static final String PREFERENCES_FILE = "RecommendationPreferencesFile";

    private static final String PREFS_SHARE_USAGE_DATA_ACCEPTED = "share_usage_data_accepted";

    private static final String LOG_TAG = Settings.class.getCanonicalName();

    private static SharedPreferences.Editor openSharedPreferencesForEditing(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREFERENCES_FILE, Context.MODE_PRIVATE);
        return prefs.edit();
    }

    private static SharedPreferences loadSharedPreferences(Context context) {
        return context.getSharedPreferences(PREFERENCES_FILE, Context.MODE_PRIVATE);
    }

    public static Boolean isShareUsageDataAccepted(Context context) {
        boolean isShareUsageDataAccepted = false;
        if (context != null) {
            SharedPreferences prefs = loadSharedPreferences(context);
            isShareUsageDataAccepted = prefs.getBoolean(PREFS_SHARE_USAGE_DATA_ACCEPTED, false);
        } else {
            Log.e(LOG_TAG, "isShareUsageDataAccepted, could not read/write from/to SharedPreferences, context == null.");
        }
        return isShareUsageDataAccepted;
    }

    public static void setShareUsageDataAccepted(Context context, boolean acceptShareUsageData) {
        if (context != null) {
            SharedPreferences.Editor editor = openSharedPreferencesForEditing(context);
            editor.putBoolean(PREFS_SHARE_USAGE_DATA_ACCEPTED, acceptShareUsageData);
            editor.apply();
        } else {
            Log.e(LOG_TAG, "isShareUsageDataAccepted, could not read/write from/to SharedPreferences, context == null.");
        }
    }

    public static SettingsDataStructure readSettingsFromSacco(Context context) {
        try {
            SettingsDataStructure saccoSettings = SettingsReader.loadSettings(context);
            Log.d(LOG_TAG, "Settings successfully loaded custom settings from file system.");
            return saccoSettings;
        } catch (FileNotFoundException e) {
            Log.i(LOG_TAG, "Settings could not find custom settings file. Using default settings...");
        } catch (XmlPullParserException e) {
            Log.e(LOG_TAG, "Settings experienced and error when parsing settings file: " + e.toString() + ".\nUsing default settings...");
        } catch (IOException e) {
            Log.e(LOG_TAG, "Settings experienced and error when reading settings file: " + e.toString() + ".\nUsing default settings...");
        }
        return null;
    }

    public static synchronized boolean isDisclaimerDisabled(Context context) {
        SettingsDataStructure saccoSettings = readSettingsFromSacco(context);
        if (saccoSettings != null) {
            return saccoSettings.disableDisclaimer;
        }
        return context.getResources().getBoolean(R.bool.disableDisclaimer);
    }

    public static void reset(Context context) {
        if (context != null) {
            SharedPreferences.Editor editor = openSharedPreferencesForEditing(context);
            editor.clear();
            editor.apply();
        } else {
            Log.e(LOG_TAG, "reset, could not read/write from/to SharedPreferences, context == null.");
        }
    }

    public static boolean shareUsageDataExists(Context context) {
        boolean shareUsageDataExists = false;
        if (context != null) {
            SharedPreferences prefs = loadSharedPreferences(context);
            shareUsageDataExists = prefs.contains(PREFS_SHARE_USAGE_DATA_ACCEPTED);
        } else {
            Log.e(LOG_TAG, "shareUsageDataExists, could not read/write from/to SharedPreferences, context == null.");
        }
        return shareUsageDataExists;
    }
}
