/*********************************************************************
 *  ____                      _____      _                           *
 * / ___|  ___  _ __  _   _  | ____|_ __(_) ___ ___ ___  ___  _ __   *
 * \___ \ / _ \| '_ \| | | | |  _| | '__| |/ __/ __/ __|/ _ \| '_ \  *
 *  ___) | (_) | | | | |_| | | |___| |  | | (__\__ \__ \ (_) | | | | *
 * |____/ \___/|_| |_|\__, | |_____|_|  |_|\___|___/___/\___/|_| |_| *
 *                    |___/                                          *
 *                                                                   *
 *********************************************************************
 * Copyright 2011 Sony Ericsson Mobile Communications AB.            *
 * All rights, including trade secret rights, reserved.              *
 *********************************************************************/

package com.sonymobile.sonyselect.util.colorextraction.evaluator;

import com.sonymobile.sonyselect.util.colorextraction.ColorInfo;
import com.sonymobile.sonyselect.util.colorextraction.ColorEvaluator;

/**
 * Simple evaluator implementation for extracting a bright and saturated color
 * from an image.
 */
public class MainColorEvaluator implements ColorEvaluator {

    @Override
    public int evaluate(ColorInfo c) {
        return 1 + (int)Math.round(1000 * (Math.pow(c.mNormalized, .3) + 0.01f) * c.mBrightness
                * (c.mSaturation + 0.1f));
    }
}
