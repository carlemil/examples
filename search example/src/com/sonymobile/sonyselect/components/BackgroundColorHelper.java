package com.sonymobile.sonyselect.components;

import android.animation.ArgbEvaluator;
import android.animation.ValueAnimator;
import android.animation.ValueAnimator.AnimatorUpdateListener;
import android.graphics.PorterDuff.Mode;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.animation.DecelerateInterpolator;

import com.sonymobile.sonyselect.R;
import com.sonymobile.sonyselect.util.BitmapUtil;

public class BackgroundColorHelper {

    private static final int BACKGROUND_FADE_DURATION_MS = 500;

    private int currentBgColor = -1;
    private int defaultBgColor;
    private ValueAnimator animator;

    private View view;

    private Drawable drawable;

    public BackgroundColorHelper(View view) {
        this.view = view;
        defaultBgColor = view.getResources().getColor(R.color.default_background_color);
        currentBgColor = defaultBgColor;
    }

    private void setBackgroundColor(int color) {
        if (view != null && color != BitmapUtil.COLOR_UNKNOWN) {
            if (drawable == null) {
                drawable = view.getResources().getDrawable(R.drawable.bg);
                View rootView = view.getRootView();
                rootView.setBackground(drawable);
            }
            drawable.setColorFilter(color, Mode.OVERLAY);
        }
    }

    public void onColorExtracted(int color) {
        if (view != null && color != BitmapUtil.COLOR_UNKNOWN) {
            fadeToBgColor(color);
        }
    }

    private void fadeToBgColor(int color) {
        if (currentBgColor == BitmapUtil.COLOR_UNKNOWN) {
            setBackgroundColor(color);
            currentBgColor = color;
            return;
        }

        animator = ValueAnimator.ofObject(new ArgbEvaluator(), currentBgColor, color);
        animator.setDuration(BACKGROUND_FADE_DURATION_MS);
        animator.setInterpolator(new DecelerateInterpolator());
        animator.addUpdateListener(new AnimatorUpdateListener() {
            public void onAnimationUpdate(ValueAnimator animation) {
                currentBgColor = (Integer) animation.getAnimatedValue();
                setBackgroundColor(currentBgColor);
            }
        });
        animator.start();
    }

    public void setDefaultBackgroundColor() {
        setBackgroundColor(defaultBgColor);
    }

    public void stop() {
        if (animator != null) {
            animator.end();
        }
    }

}
