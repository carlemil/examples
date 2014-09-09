package com.sonymobile.sonyselect.util;

import android.graphics.Bitmap;
import android.util.Log;

import com.sonymobile.sonyselect.util.colorextraction.ColorExtractor;
import com.sonymobile.sonyselect.util.colorextraction.ColorInfo;
import com.sonymobile.sonyselect.util.colorextraction.evaluator.MainColorEvaluator;

/**
 * Collects bitmap creation to this single class. Makes it easier to reuse
 * options, monitor usage and perform logging.
 */
public class BitmapUtil {

    public static final int COLOR_UNKNOWN = 0;
    
    private static final String LOG_TAG = BitmapUtil.class.getCanonicalName();

    // private static final MainColorEvaluator mEvaluator = new
    // MainColorEvaluator();
    // private static final ColorExtractor mExtractor = new
    // ColorExtractor(mEvaluator);

    /**
     * Extracts a color from a bitmap.
     * 
     * @param bitmap the image to extract a color from.
     * @return the the actual color that was extracted.
     */
    public static int extractColor(final Bitmap bitmap) {
        int color = COLOR_UNKNOWN;
        if (bitmap != null) {
            try {
                Bitmap newBitmap = Bitmap.createScaledBitmap(bitmap, 64, 64, true);
                MainColorEvaluator mEvaluator = new MainColorEvaluator();
                ColorExtractor mExtractor = new ColorExtractor(mEvaluator);
                final ColorInfo colorInfo = mExtractor.extract(newBitmap);
                newBitmap.recycle();
                color = colorInfo.getColor(colorInfo.mLuminance);
            } catch (Exception e) {
                Log.e(LOG_TAG, "Failed to extract color from bitmap: " + e.getLocalizedMessage());
            }
        }
        return color;
    }

}
