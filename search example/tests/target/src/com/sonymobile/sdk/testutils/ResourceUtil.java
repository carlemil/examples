/*
 * Copyright (c) 2013 Sony Mobile Communications AB.
 * All rights, including trade secret rights, reserved.
 */

package com.sonymobile.sdk.testutils;

import java.io.IOException;
import java.io.InputStream;
import java.util.Scanner;

public class ResourceUtil {

    public static String toString(String path) {
        InputStream input = ResourceUtil.class.getResourceAsStream(path);
        Scanner s = new Scanner(input, "utf-8").useDelimiter("\\A");
        String text = s.hasNext() ? s.next() : "";
        if (input != null) {
            try {
                input.close();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        return text;
    }
}
