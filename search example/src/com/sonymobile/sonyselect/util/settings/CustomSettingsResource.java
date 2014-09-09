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

import android.net.Uri;

public class CustomSettingsResource {

    private static final String URI = "content://com.sonyericsson.provider.customization/settings/com.sonymobile.sonyselect/custom_settings.xml";

    public String getUri() {
        return URI;
    }

    public String getProtocol() {
        return Uri.parse(URI).getScheme();
    }
}
