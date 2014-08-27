
package com.jayway.volleydemo.customviews;

import android.content.Context;
import android.graphics.Point;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.GridLayout;

import com.jayway.volleydemo.adapters.GridLayoutAdapter;

public class ExpandableGridLayout extends GridLayout {

    private static final String LOG_TAG = ExpandableGridLayout.class.getCanonicalName();

    private GridLayoutAdapter adapter;

    private int screenWidth;

    public ExpandableGridLayout(Context context, AttributeSet attrs) {
        super(context, attrs);

        Point size = new Point();
        WindowManager windowManager = ((WindowManager) context
                .getSystemService(Context.WINDOW_SERVICE));
        windowManager.getDefaultDisplay().getSize(size);
        screenWidth = size.x;

        GridLayout.LayoutParams params = new LayoutParams();
        params.setGravity(Gravity.CENTER);
        setLayoutParams(params);
    }

    public void forceLayout() {
        if (adapter != null) {

            View tmpView = adapter.getSmallView(0);
            tmpView.measure(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
            int itemViewWidth = tmpView.getMeasuredWidth();

            removeAllViews();

            int layoutPadding = dpToPx(2 * 8);
            int columnSpan = (screenWidth - layoutPadding) / itemViewWidth;
            itemViewWidth = (screenWidth - layoutPadding) / columnSpan;
            setColumnCount(columnSpan);

            for (int i = 0; i < adapter.getCount(); i++) {
                View smallView = adapter.getSmallView(i);
                android.view.ViewGroup.LayoutParams smallItemParams = new LayoutParams();
                smallItemParams.width = itemViewWidth;
                smallView.setLayoutParams(smallItemParams);
                addView(smallView);
            }
        }
    }

    public int dpToPx(int dp) {
        DisplayMetrics displayMetrics = getContext().getResources().getDisplayMetrics();
        int px = Math.round(dp * (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT));
        return px;
    }

    public void setAdapter(GridLayoutAdapter adapter) {
        this.adapter = adapter;
    }

}
