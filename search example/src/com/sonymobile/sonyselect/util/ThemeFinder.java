package com.sonymobile.sonyselect.util;

import android.content.Context;
import android.content.res.Resources;

public class ThemeFinder {

    public static int getThemeId(Resources res) {
        return android.R.style.Theme;
    }

    public static int getDialogThemeId(Resources resources) {
        return android.R.style.Theme_DeviceDefault_Panel;
    }

    public static int getResourceId(String name, String type, Resources res, Context context) {
        return res.getIdentifier(name, type, context.getPackageName());
    }

}
