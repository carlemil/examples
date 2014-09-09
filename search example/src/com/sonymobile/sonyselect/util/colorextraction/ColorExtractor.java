/*
 * Copyright (C) 2010-2011 Sony Ericsson Mobile Communications AB.
 * Copyright (C) 2012 Sony Mobile Communications AB.
 * All rights, including trade secret rights, reserved.
 */

package com.sonymobile.sonyselect.util.colorextraction;

import com.sonymobile.sonyselect.util.colorextraction.evaluator.MainColorEvaluator;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.util.Log;

import java.io.FileDescriptor;
import java.io.InputStream;
import java.util.ArrayList;

/**
 * Extracts colors from images using the supplied Evaluation objects
 */
public class ColorExtractor {

    /*
     * Max number of pixels to be analyzed for color extraction. Serves as a
     * ceiling for bitmap decoding scaling factor.
     */
    private static final int MAX_PIXELS = 10000;

    /*
     * Debug flag. Set this to true to see all debug prints.
     */
    private static final boolean DEBUG = false;

    /*
     * Debug tag. Use this tag in filtering the log output.
     */
    private static final String TAG = "ColorExtractor";

    /*
     * Range of H, S, V color values
     */
    private static final float HUE_RANGE = 360f;
    private static final float SAT_RANGE = 1f;
    private static final float VAL_RANGE = 1f;
    private static final float[] RANGE = {
            HUE_RANGE, SAT_RANGE, VAL_RANGE
    };

    /*
     * Histogram resolution in H, S, V dimensions
     */
    private static final int[] RES = {
            36, 10, 10
    };

    /*
     * List of ColorInfo objects initialized from image source
     */
    private final ArrayList<ColorInfo> mColorInfoList = new ArrayList<ColorInfo>();

    /*
     * Color evaluator
     */
    private ColorEvaluator mEvaluator;

    /**
     * Create color extractor using the supplied {@link ColorEvaluator}.
     *
     * @param evaluator Color evaluator used to score colors
     */
    public ColorExtractor(ColorEvaluator evaluator) {
        if (evaluator == null) {
            mEvaluator = new MainColorEvaluator();
        } else {
            mEvaluator = evaluator;
        }
    }

    /**
     * Extracts a color from the image supplied in the byte array using
     * the {@link ColorEvaluator} that this ColorExtractor was initialized with.
     *
     * @param data byte array of compressed image data
     * @param offset offset into image data
     * @param length the number of bytes, beginning at offset, to parse
     *
     * @return Extracted color, or null if failed to extract color info.
     *
     * @throws NullPointerException if the provided data is null.
     *
     * @throws IllegalArgumentException if the parameters are incorrect.
     * Valid parameter values are 0 < length <= (data.length - offset)
     * and 0 <= offset <= (data.length - length).
     */
    public ColorInfo extract(byte[] data, int offset, int length) {
        if (data == null) {
            throw new NullPointerException("Supplied data array is null");
        } else if (length <= 0 || offset < 0 || (offset + length) > data.length) {
            throw new IllegalArgumentException();
        }

        createColorInfoList(data, offset, length, null, null, null, 0, null);
        return extract();
    }

    /**
     * Extracts a color from the image on the supplied file path using
     * the {@link ColorEvaluator} that this ColorExtractor was initialized with.
     *
     * @param pathName complete path name for image file
     *
     * @return Extracted color, or null if failed to extract color info.
     *
     * @throws NullPointerException if the supplied pathname is null.
     */
    public ColorInfo extract(String pathName) {
        if (pathName == null) {
            throw new NullPointerException("Supplied pathname is null");
        }
        createColorInfoList(null, 0, 0, pathName, null, null, 0, null);
        return extract();
    }

    /**
     * Extracts a color from the image pointed to by the supplied file descriptor
     * using the {@link ColorEvaluator} that this ColorExtractor was initialized with.
     *
     * @param fd The file descriptor containing the image data
     *
     * @return Extracted color, or null if failed to extract color info.
     *
     * @throws NullPointerException if the supplied file descriptor is null.
     */
    public ColorInfo extract(FileDescriptor fd) {
        if (fd == null) {
            throw new NullPointerException("Supplied file descriptor is null");
        }
        createColorInfoList(null, 0, 0, null, fd, null, 0, null);
        return extract();
    }

    /**
     * Extracts a color from the image resource supplied in the inparameters
     * using the {@link ColorEvaluator} that this ColorExtractor was initialized with.
     *
     * @param res The resources object containing the image data
     * @param resId The resource id of the image data
     *
     * @return Extracted color, or null if failed to extract color info.
     *
     * @throws NullPointerException if the supplied resource object is null.
     */
    public ColorInfo extract(Resources res, int resId) {
        if (res == null) {
            throw new NullPointerException("Supplied resources object is null");
        }
        createColorInfoList(null, 0, 0, null, null, res, resId, null);
        return extract();
    }

    /**
     * Extracts a color from the image held by the supplied input stream
     * using the {@link ColorEvaluator} that this ColorExtractor was initialized with.
     *
     * @param inputStream The input stream that holds the raw image data
     *
     * @return Extracted color, or null if failed to extract color info.
     *
     * @throws NullPointerException if the supplied inputStream is null.
     */
    public ColorInfo extract(InputStream inputStream) {
        if (inputStream == null) {
            throw new NullPointerException("Supplied inputstream is null");
        }
        createColorInfoList(null, 0, 0, null, null, null, 0, inputStream);
        return extract();
    }

    /**
     * Extracts a color from the supplied bitmap using the
     * {@link ColorEvaluator} that this ColorExtractor was initialized with.
     *
     * @param bitmap The bitmap that holds image data
     *
     * @return Extracted color, or null if failed to extract color info.
     *
     * @throws NullPointerException if the supplied bitmap is null.
     */
    public ColorInfo extract(Bitmap bitmap) {
        if (bitmap == null) {
            throw new NullPointerException("Supplied bitmap is null");
        }

        final int numberOfPixels = bitmap.getWidth() * bitmap.getHeight();
        final float scaling = Math.max(1, ((float)numberOfPixels) / MAX_PIXELS);

        final int dstWidth = Math.max(1,
                Math.min(MAX_PIXELS, (int)Math.floor(bitmap.getWidth() / scaling)));
        final int dstHeight = Math.max(1,
                Math.min(MAX_PIXELS, (int)Math.floor(bitmap.getHeight() / scaling)));

        Bitmap scaledBitmap = Bitmap.createScaledBitmap(bitmap, dstWidth, dstHeight, true);

        if (scaledBitmap != null) {
            initialize(scaledBitmap);
            // Bitmap#createScaledBitmap(..) can return the source Bitmap, don't recycle
            // it in this case!
            if (scaledBitmap != bitmap) {
                scaledBitmap.recycle();
            }
            return extract();
        }
        return null;
    }

    private ColorInfo extract() {
        ColorInfo bestColor = null;
        int bestScore = Integer.MIN_VALUE;
        int worstScore = Integer.MAX_VALUE;

        // Evaluate all colors and store results
        if (DEBUG) {
            Log.d(TAG, "STARTING EVALUATION OF COLORINFO OBJECTS");
            new RuntimeException().printStackTrace();
        }
        for (ColorInfo c : mColorInfoList) {
            int score = mEvaluator.evaluate(c);

            if (DEBUG) {
                Log.d(TAG, "EVAL: Score = " + score + " Color = [" + c.mHue + ", "
                    + c.mSaturation + "," + c.mBrightness + "] norm = " + c.mNormalized);
            }

            if (score >= bestScore) {
                bestColor = c;
                bestScore = score;
            }
            if (score < worstScore) {
                worstScore = score;
            }
        }

        if (DEBUG) {
            Log.d(TAG, "bestScore = " + bestScore + " worstScore = " + worstScore
                    + " bestColor = [Hue = " + bestColor.mHue + ", Saturation = "
                    + bestColor.mSaturation + ", Brightness = " + bestColor.mBrightness
                    + ", Luminance = " + bestColor.mLuminance
                    + "]");
        }

        if ((bestScore == worstScore && mColorInfoList.size() > 1) || bestColor == null) {
            if (DEBUG) {
                Log.d(TAG, "USING default white");
            }

            // If best score and worst score is the same we return white, this
            // is to protect from cases where every color gets the same score
            // leading to a more or less random selection of color
            return new ColorInfo(0xffffffff, 0);
        } else {
            // Return a copy of the best color
            return new ColorInfo(bestColor);
        }
    }

    /*
     * This method is used as a helper method by the extract-methods.
     * It initializes a ColorInfoList object using image data.
     * The image data can be provided in many ways, such as in
     * a byte array or by giving a path to an imagefile.
     */
    private void createColorInfoList(byte[] data, int offset, int length, String pathName,
            FileDescriptor fd, Resources res, int resId, InputStream inputStream) {
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;

        decodeBitmap(data, offset, length, pathName, fd, res, resId, inputStream, options);

        final int numberOfPixels = options.outWidth * options.outHeight;
        final float scaling = ((float)numberOfPixels) / MAX_PIXELS;

        options.inJustDecodeBounds = false;
        options.inSampleSize = (int)Math.ceil(scaling);

        Bitmap scaledBitmap = decodeBitmap(data, offset, length, pathName, fd, res, resId,
                inputStream, options);

        if (scaledBitmap != null) {
            initialize(scaledBitmap);
            scaledBitmap.recycle();
        }
    }

    /*
     * Initialize color extractor using a pre-scaled bitmap containing less or
     * equal to MAX_PIXELS pixels.
     */
    private void initialize(Bitmap scaledBitmap) {
        // Quantify colors in bitmap and create histogram
        int maxHistValue = 0;
        final int[][][] histogram = new int[RES[0]][RES[1]][RES[2]];
        final float[] hsv = new float[3];
        final int[] qHSV = new int[3];

        // Perform a walk through all pixels in the image
        if (DEBUG) {
            Log.d("COLOR", "STARTING WALKTHROUGH OF PIXELS");
        }
        for (int y = 0; y < scaledBitmap.getHeight(); ++y) {
            for (int x = 0; x < scaledBitmap.getWidth(); ++x) {
                Color.colorToHSV(scaledBitmap.getPixel(x, y), hsv);

                // quantify loads the qHSV with values of H, S and V from the image
                // according to the resolution specified in RES.
                quantify(hsv, qHSV);

                // The element in the histogram array corresponding to the color
                // extracted is increased by 1
                final int histValue = histogram[qHSV[0]][qHSV[1]][qHSV[2]] + 1;

                // The maximum count of any element in the histogram array is updated
                maxHistValue = Math.max(histValue, maxHistValue);
                histogram[qHSV[0]][qHSV[1]][qHSV[2]] = histValue;
            }
        }

        // Now histogram has been created.

        // Walk through the histogram and for each hue
        // create a ColorInfo object and
        // pass it the count of that hue.
        if (DEBUG) {
            Log.d(TAG, "STARTING CREATION OF COLORINFO OBJECTS");
        }

        for (int h = 0; h < histogram.length; ++h) {
            for (int s = 0; s < histogram[h].length; ++s) {
                for (int v = 0; v < histogram[h][s].length; ++v) {
                    // Load hsv with hue corresponding to h, s and v
                    dequantify(h, s, v, hsv);

                    // Create colorinfo object from hue and count of that hue (divided by the
                    // maximal hue count). That is, each color info object contains a value
                    // showing how common this color is compared to the most common color.
                    // If this color is half as common it gets a value of 0.5 and so on.
                    if (histogram[h][s][v] > 0) {
                        if (DEBUG) {
                            int count = scaledBitmap.getWidth() * scaledBitmap.getHeight();
                            Log.d(TAG, "ColorInfo object created hsv = [" + hsv[0] + ","
                                + hsv[1] + "," + hsv[2] + "] count = " + histogram[h][s][v]
                                + " total count = " + count);
                        }

                        mColorInfoList.add(new ColorInfo(hsv, ((float)histogram[h][s][v])
                                / maxHistValue));
                    }
                }
            }
        }

        // Now we have created a colorlist with all the hues and for each
        // hue a number based on count of that hue divided by maximal hue count
    }

    /*
     * Quantifies hsv value
     *
     * hsv is an array containing the unquantized h, s and v values
     * for a color.
     *
     * qHSV will be loaded to contain the quantized values.
     *
     * (hsv[n] / RANGE[n]) is a value in the interval [0.0..1.0],
     * we want to linearly map values in this interval to the discrete
     * steps in the RES[n] range. By using floor on a value multiplied
     * by the number of steps we perform the linear mapping
     * [0.0 .. 1.0] --> (0, 1, .. ,RES[n])
     */
    private static void quantify(float[] hsv, int[] qHSV) {
        qHSV[2] = (int)Math.floor((hsv[2] / RANGE[2]) * RES[2]);

        if (qHSV[2] == RES[2]) {
            qHSV[2] = qHSV[2] - 1;
        }

        if (qHSV[2] == 0) {
            // If brightness is quantified to 0 then hue and saturation are
            // forced to 0 as well
            qHSV[0] = 0;
            qHSV[1] = 0;
            qHSV[2] = 0;
        } else {
            qHSV[1] = (int)Math.floor((hsv[1] / RANGE[1]) * RES[1]);

            if (qHSV[1] == RES[1]) {
                qHSV[1] = qHSV[1] - 1;
            }

            if (qHSV[1] == 0) {
                // If saturation is quantified to 0 then the hue is force to 0
                // as well
                qHSV[0] = 0;
                qHSV[1] = 0;
            } else {
                qHSV[0] = (int)Math.floor((hsv[0] / RANGE[0]) * RES[0]);
                if (qHSV[0] == RES[0]){
                    qHSV[0] = qHSV[0] - 1;
                }
            }
        }

        if (DEBUG) {
            Log.d("COLOR", "hsv = [" + hsv[0] + "," + hsv[1] + "," + hsv[2] + "]  RES = ["
                + RES[0] + "," + RES[1] + "," + RES[2] + "]  qHSV = [" + qHSV[0] + "," + qHSV[1]
                + "," + qHSV[2] + "]");
        }
    }

    /*
     * Dequantifies hsv value
     */
    private static void dequantify(int h, int s, int v, float[] hsv) {
        hsv[0] = (((float)h) / (RES[0] - 1)) * RANGE[0];
        hsv[1] = (((float)s) / (RES[1] - 1)) * RANGE[1];
        hsv[2] = (((float)v) / (RES[2] - 1)) * RANGE[2];
    }

    /*
     * Wrapper function for most BitmapFactory.decodeXXX() functions.
     */
    private static Bitmap decodeBitmap(byte[] data, int offset, int length, String pathName,
            FileDescriptor fd, Resources res, int resId, InputStream inputStream,
            BitmapFactory.Options options) {

        if (data != null) {
            return BitmapFactory.decodeByteArray(data, offset, length, options);
        } else if (pathName != null) {
            return BitmapFactory.decodeFile(pathName, options);
        } else if (fd != null) {
            return BitmapFactory.decodeFileDescriptor(fd, null, options);
        } else if (res != null) {
            return BitmapFactory.decodeResource(res, resId, options);
        } else if (inputStream != null) {
            return BitmapFactory.decodeStream(inputStream, null, options);
        }
        return null;
    }

}
