package com.dzenm.helper.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import androidx.appcompat.widget.AppCompatImageView;
import android.util.AttributeSet;
import android.view.MotionEvent;

import com.dzenm.helper.R;

/**
 * 根据宽高比例自动计算高度ImageView
 */
public class RatioImageView extends AppCompatImageView {

    /**
     * 点击反馈时的前景颜色
     */
    private int mForegroundColor = 0xFFBDBDBD;

    /**
     * ImageView的宽高比例
     */
    private float mRatio = 0f;

    public RatioImageView(Context context) {
        this(context, null);
    }

    public RatioImageView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RatioImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.RatioImageView);

        mRatio = typedArray.getFloat(R.styleable.RatioImageView_ratio, 0f);
        mForegroundColor = typedArray.getColor(R.styleable.RatioImageView_foregroundColor, 0xFFBDBDBD);
        typedArray.recycle();
    }

    /**
     * @param ratio ImageView的宽高比例
     */
    public void setRatio(float ratio) {
        mRatio = ratio;
    }

    /**
     * @param foregroundColor 点击反馈时的前景颜色
     */
    public void setForegroundColor(int foregroundColor) {
        mForegroundColor = foregroundColor;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int width = MeasureSpec.getSize(widthMeasureSpec);
        if (mRatio != 0) {
            float height = width / mRatio;
            heightMeasureSpec = MeasureSpec.makeMeasureSpec((int) height, MeasureSpec.EXACTLY);
        }
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                Drawable drawable = getDrawable();
                if (drawable != null) {
                    drawable.mutate().setColorFilter(mForegroundColor, PorterDuff.Mode.MULTIPLY);
                }
                break;
            case MotionEvent.ACTION_MOVE:
                break;
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP:
                Drawable drawableUp = getDrawable();
                if (drawableUp != null) {
                    drawableUp.mutate().clearColorFilter();
                }
                break;
        }
        return super.onTouchEvent(event);
    }

}
