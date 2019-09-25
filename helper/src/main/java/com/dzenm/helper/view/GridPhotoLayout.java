package com.dzenm.helper.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

import com.dzenm.helper.R;

/**
 * @author dzenm
 * @date 2019-09-21 08:37
 */
public class GridPhotoLayout extends ViewGroup {

    private int mChildVerticalSpace, mChildHorizontalSpace;
    private int mColumnCount = 3;
    private int mChildWidth, mChildHeight;

    public GridPhotoLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        TypedArray attributes = context.obtainStyledAttributes(attrs, R.styleable.GridPhotoLayout);
        mChildVerticalSpace = attributes.getDimensionPixelSize(R.styleable.GridPhotoLayout_margin_child, 0);
        mChildHorizontalSpace = attributes.getDimensionPixelSize(R.styleable.GridPhotoLayout_margin_child, 0);
        mColumnCount = attributes.getInt(R.styleable.GridPhotoLayout_columnCount, 3);
        attributes.recycle();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int w = MeasureSpec.getSize(widthMeasureSpec);
        int childCount = getChildCount();
        if (childCount > 0) {
            mChildHeight = mChildWidth = (w - (mColumnCount - 1) * mChildHorizontalSpace) / mColumnCount;
            int vw = w;
            if (childCount < mColumnCount) vw = childCount * (mChildHeight + mChildVerticalSpace);
            int rowCount = childCount / mColumnCount + (childCount % mColumnCount == 0 ? 0 :1);
            int vh = rowCount * mChildHeight + (rowCount > 0 ? rowCount - 1 : 0) * mChildVerticalSpace;
            setMeasuredDimension(vw, vh);
        }
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        int left, top;
        int count = getChildCount();
        for (int i = 0; i < count; i++) {
            View child = getChildAt(i);
            left = (i % mColumnCount) * (mChildWidth + mChildHorizontalSpace);
            top = (i / mColumnCount) * (mChildHeight + mChildVerticalSpace);
            child.layout(left, top, left + mChildWidth, top + mChildHeight);
        }
    }
}