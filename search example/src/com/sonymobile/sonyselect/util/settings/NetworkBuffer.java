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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class NetworkBuffer {

    public static ByteArrayInputStream bufferInputStream(InputStream input) throws IOException {
        ByteArrayOutputStream os = new ByteArrayOutputStream();

        byte[] buf = new byte[4096];

        int bytesRead = input.read(buf);
        while (bytesRead > -1) {
            os.write(buf, 0, bytesRead);
            bytesRead = input.read(buf);
        }

        os.close();
        input.close();

        return new ByteArrayInputStream(os.toByteArray());
    }
}
