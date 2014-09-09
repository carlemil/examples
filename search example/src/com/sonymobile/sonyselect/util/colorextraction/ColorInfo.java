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

package com.sonymobile.sonyselect.util.colorextraction;

import android.graphics.Color;

/**
 * Class representing information about a color relevant for color extraction.
 * Speed is important so fields are public.
 */
public class ColorInfo {
    /*
     * Luminance factors
     */
    private static final float RED_LUMINANCE_FACTOR = 0.299f;
    private static final float GREEN_LUMINANCE_FACTOR = 0.587f;
    private static final float BLUE_LUMINANCE_FACTOR = 0.114f;

    /** Hue value */
    public final float mHue;

    /** Saturation value */
    public final float mSaturation;

    /** Brightness value */
    public final float mBrightness;

    /**
     * Normalized number of instances of color according to histogram.
     * Normalized so that 1 means it is the most common color, 0.5 means it
     * occurs half the number of times of the most common color, etc.
     */
    public final float mNormalized;

    /** RGB value */
    public final int mRgb;

    /** Luminance represents how bright this color appears */
    public float mLuminance;

    /*
     * Creates a new color info from a hsv value
     */
    public ColorInfo(float hsv[], float n) {
        this.mHue = hsv[0];
        this.mSaturation = hsv[1];
        this.mBrightness = hsv[2];
        this.mNormalized = n;

        mRgb = Color.HSVToColor(hsv);

        this.mLuminance = calculateLuminance(Color.red(mRgb), Color.green(mRgb), Color.blue(mRgb));
    }

    /*
     * Creates a new color info from an rgb value
     */
    public ColorInfo(int rgb, int n) {
        float[] hsv = new float[3];
        Color.colorToHSV(rgb, hsv);
        this.mHue = hsv[0];
        this.mSaturation = hsv[1];
        this.mBrightness = hsv[2];
        this.mNormalized = n;
        this.mRgb = rgb;
        this.mLuminance = calculateLuminance(Color.red(rgb), Color.green(rgb), Color.blue(rgb));
    }

    /*
     * Creates a new color info that is an exact copy of the input parameter
     *
     * @param info Color info to copy
     */
    public ColorInfo(ColorInfo info) {
        this.mHue = info.mHue;
        this.mSaturation = info.mSaturation;
        this.mBrightness = info.mBrightness;
        this.mNormalized = info.mNormalized;
        this.mRgb = info.mRgb;
        this.mLuminance = info.mLuminance;
    }

    /**
     * Gets the color represented by this info but with the luminance set to the
     * luminance of the input value
     *
     * @param newLuminance Luminance to set to color [0, 1]
     * @return RGB value
     */
    public int getColor(float newLuminance) {
        if (newLuminance <= 0f) {
            return Color.rgb(0, 0, 0);
        } else if (newLuminance >= 1f) {
            return Color.rgb(255, 255, 255);
        }

        // Set attributes of current color
        int r = Color.red(mRgb);
        int g = Color.green(mRgb);
        int b = Color.blue(mRgb);
        float currentLuminance = this.mLuminance;

        float multiplier = newLuminance / currentLuminance;
        if (multiplier < 1f || (r == g && g == b)) {
            // If multiplier is less than 1 or we have a non-saturated color we
            // only need to multiply and we're done
            return Color.rgb(Math.round(r * multiplier), Math.round(g * multiplier),
                    Math.round(b * multiplier));
        } else {
            // If multiplier is larger than one we need to calculate new color
            // in iterations since multiplying source color might overflow
            // the individual color channels
            float maxFactor;
            float requiredVectorFactor;
            do {
                // Vector channel value is 0 if channel is maxed out, otherwise
                // we set it to the current channel value + a small value to
                // make sure that the vector isn't all zero
                final float vr = (r == 255) ? 0 : (r + 0.01f);
                final float vg = (g == 255) ? 0 : (g + 0.01f);
                final float vb = (b == 255) ? 0 : (b + 0.01f);

                // Amount of vector we would like to add
                final float luminanceOfVector = calculateLuminance(vr, vg, vb);
                requiredVectorFactor = ((newLuminance - currentLuminance) / luminanceOfVector);

                // Amount of vector we can add before any color hits max value
                maxFactor = Float.MAX_VALUE;
                if (vr != 0) {
                    maxFactor = Math.min((255f - r) / vr, maxFactor);
                }
                if (vg != 0) {
                    maxFactor = Math.min((255f - g) / vg, maxFactor);
                }
                if (vb != 0) {
                    maxFactor = Math.min((255f - b) / vb, maxFactor);
                }

                final float factor = Math.min(requiredVectorFactor, maxFactor);
                r += Math.round(vr * factor);
                g += Math.round(vg * factor);
                b += Math.round(vb * factor);
                currentLuminance = calculateLuminance(r, g, b);

            } while (requiredVectorFactor >= maxFactor);

            return Color.rgb(r, g, b);
        }
    }

    // Help method for calculating luminance
    private float calculateLuminance(float r, float g, float b) {
        return (RED_LUMINANCE_FACTOR * r + GREEN_LUMINANCE_FACTOR * g
                + BLUE_LUMINANCE_FACTOR * b) / 255f;
    }
}
