package com.sonymobile.sonyselect.components;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;

import com.android.volley.toolbox.NetworkImageView;
import com.sonymobile.sonyselect.R;

public final class RelativeHeightImageView extends NetworkImageView {
    private static final String LOG_TAG = RelativeHeightImageView.class.getName();
    private int relativeWidth;
    private int relativeHeight;
    private float scaleFactor;
    private UpdateBackgroundColorListener mUpdateBackgroundColorListener;

    public void setUpdateBackgroundColorListener(UpdateBackgroundColorListener mUpdateBackgroundColorListener) {
        this.mUpdateBackgroundColorListener = mUpdateBackgroundColorListener;
    }

    public RelativeHeightImageView(Context context) {
        super(context);
        setRelativeSize(1, 1);
        setDefaultImageResId(R.drawable.image_placeholder);
    }

    public RelativeHeightImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        TypedArray typedArray = getContext().obtainStyledAttributes(attrs, R.styleable.RelativeHeightImageView);
        int w = typedArray.getInt(R.styleable.RelativeHeightImageView_layout_weightWidth, 1);
        int h = typedArray.getInt(R.styleable.RelativeHeightImageView_layout_weightHeight, 1);
        setRelativeSize(w, h);
        typedArray.recycle();
        setDefaultImageResId(R.drawable.image_placeholder);
    }

    public RelativeHeightImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        TypedArray typedArray = getContext().obtainStyledAttributes(attrs, R.styleable.RelativeHeightImageView);
        int w = typedArray.getInt(R.styleable.RelativeHeightImageView_layout_weightWidth, 1);
        int h = typedArray.getInt(R.styleable.RelativeHeightImageView_layout_weightHeight, 1);
        setRelativeSize(w, h);
        typedArray.recycle();
        setDefaultImageResId(R.drawable.image_placeholder);
    }

    public void setRelativeSize(int weightWidth, int weightHeight) {
        relativeWidth = weightWidth;
        relativeHeight = weightHeight;
        scaleFactor = ((float) relativeHeight / (float) relativeWidth);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int newHeightSize = (int) (scaleFactor * widthSize);
        int newHeightMeasureSpec = MeasureSpec.makeMeasureSpec(newHeightSize, heightMode);
        super.onMeasure(widthMeasureSpec, newHeightMeasureSpec);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        int relativeHeight = (int) (scaleFactor * w);
        super.onSizeChanged(w, relativeHeight, oldw, oldh);
    }

    @Override
    public void setImageDrawable(Drawable drawable) {
        super.setImageDrawable(drawable);

        if(mUpdateBackgroundColorListener!=null){
            mUpdateBackgroundColorListener.updateBackgroundColor();
        }
    }

}
