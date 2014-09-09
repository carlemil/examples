package com.sonymobile.sonyselect.util;

import android.graphics.Color;

import com.sonymobile.sonyselect.domain.GooglePlayItem;

public class ColorUtil {

    private static final String APP = "app";
    private static final String GAME = "game";
    private static final String MUSIC = "music";
    private static final String VIDEO = "video";
    private static final int BLUE = Color.rgb(40, 180, 220);
    private static final int GREY = Color.parseColor("#c8c8c8");
    private static final int PURPLE = Color.rgb(175, 30, 210);
    private static final int RED = Color.rgb(220, 40, 40);

    public static int getBarColor(GooglePlayItem googlePlayItem) {
        final String type = googlePlayItem.type;
        if (type.equals(GAME)) {
            return BLUE;
        } else if (type.equals(APP)) {
            return GREY;
        } else if (type.equals(MUSIC)) {
            return PURPLE;
        } else if (type.equals(VIDEO)) {
            return RED;
        } else {
            return GREY;
        }
    }

}
