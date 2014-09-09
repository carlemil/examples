package com.sonymobile.sonyselect.util;

import android.content.Context;
import android.content.res.Resources;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewConfiguration;

import com.sonymobile.ui.support.SystemUiVisibilityWrapper;

public class UiUtils {

    private static int sStatusBarHeight = 0;
    private static int sNavigationBarHeight = 0;

    private UiUtils() {
    }

    private static void init(Context context) {
        try {
            Resources resources = context.getResources();

            int idNavigationBarHeight = resources.getIdentifier(
                    "navigation_bar_height", "dimen", "android");
            int idStatusBarHeight = resources.getIdentifier(
                    "status_bar_height", "dimen", "android");

            sNavigationBarHeight = resources
                    .getDimensionPixelSize(idNavigationBarHeight);
            sStatusBarHeight = resources
                    .getDimensionPixelSize(idStatusBarHeight);

        } catch (Resources.NotFoundException e) {
        }

        // Set navigation bar height to 0 if device has hardware keys. Ideally
        // the platform should have returned 0 above.
        if (ViewConfiguration.get(context).hasPermanentMenuKey()) {
            sNavigationBarHeight = 0;
        }
    }

    public static void setSystemUiTranslucent(View view, Context ctx) {
        init(ctx);

        SystemUiVisibilityWrapper sysUiWrapper = SystemUiVisibilityWrapper
                .newInstance(view);
        sysUiWrapper.setVisibilityFlag(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN,
                true).setVisibilityFlag(
                View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION, true);

        // Only make system ui translucent if platform supports to set opacity
        // level.
        if (sysUiWrapper.supportsTranslucentBackground()
                && sysUiWrapper
                        .supportsTranslucentBackgroundOpacity(SystemUiVisibilityWrapper.BACKGROUND_OPACITY_TRANSPARENT)) {
            sysUiWrapper.setTranslucentBackground(true);
            sysUiWrapper
                    .setTranslucentBackgroundOpacity(SystemUiVisibilityWrapper.BACKGROUND_OPACITY_DEFAULT);
        }

        sysUiWrapper.apply();
    }

    public static int getStatusBarHeight() {
        return sStatusBarHeight;
    }

    public static int getNavigationBarHeight() {
        return sNavigationBarHeight;
    }

    public static int getActionBarHeight(Context context) {
        int actionBarHeight = 0;
        final TypedValue tv = new TypedValue();
        context.getTheme().resolveAttribute(android.R.attr.actionBarSize, tv, true);
        actionBarHeight = TypedValue.complexToDimensionPixelSize(tv.data, context.getResources().getDisplayMetrics());
        return actionBarHeight;
    }
}
