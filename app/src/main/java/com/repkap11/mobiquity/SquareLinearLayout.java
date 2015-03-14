package com.repkap11.mobiquity;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.LinearLayout;

import org.w3c.dom.Attr;


/**
 * This LinearLayout attempts to be square by making its smaller dimension equal to its larger dimension.
 */
public class SquareLinearLayout extends LinearLayout {
    private static final String TAG = SquareLinearLayout.class.getSimpleName();

    public SquareLinearLayout(Context context) {
        super(context);
    }
    public SquareLinearLayout(Context context,AttributeSet attrs) {
        super(context,attrs);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);

        int size;
        if (widthMode == MeasureSpec.EXACTLY && widthSize > 0) {
            size = widthSize;
        } else if (heightMode == MeasureSpec.EXACTLY && heightSize > 0) {
            size = heightSize;
        } else {
            size = widthSize < heightSize ? widthSize : heightSize;
        }
        int finalMeasureSpec = MeasureSpec.makeMeasureSpec(size, MeasureSpec.EXACTLY);
        super.onMeasure(finalMeasureSpec, finalMeasureSpec);
    }
}
