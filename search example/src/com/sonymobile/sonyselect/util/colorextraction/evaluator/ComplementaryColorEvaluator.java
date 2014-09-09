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
 * Evaluator implementation for extracting a complementary color to a supplied
 * color from an image.
 */
public class ComplementaryColorEvaluator implements ColorEvaluator {

    /** Hue theshold value */
    private static final float HUE_THRESHOLD = 180f;

    /*
     * Color to extract complementary color to
     */
    private ColorInfo mCC;

    /**
     * Creates instance of this Evaluator
     *
     * @param color Color to find complementary color for
     */
    public ComplementaryColorEvaluator(ColorInfo color) {
        mCC = color;
    }

    @Override
    public int evaluate(ColorInfo c) {
        float hueDifference;
        if (c.mSaturation == 0 || mCC.mSaturation == 0) {
            hueDifference = HUE_THRESHOLD;
        } else {
            hueDifference = Math.abs(c.mHue - mCC.mHue);
            if (hueDifference > HUE_THRESHOLD) {
                hueDifference = 360 - hueDifference;
            }
        }

        return (int)Math.round(1000 * Math.pow(c.mNormalized, .3) * c.mBrightness
                * Math.min(1, c.mBrightness + c.mSaturation) * Math.pow(hueDifference, 4));
    }
}
