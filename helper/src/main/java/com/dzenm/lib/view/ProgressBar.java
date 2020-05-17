package com.dzenm.lib.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;

import com.dzenm.lib.R;
import com.dzenm.lib.os.OsHelper;

/**
 * @author dzenm
 * @date 2019-08-14 10:22
 */
public class ProgressBar extends View {

    /*
     * 进度条显示的高度（可通过xml的属性设置）
     */
    private int mProgressHeight;

    /*
     * 已加载的进度条的颜色（可通过xml的属性设置）
     */
    private int mProgressColor;

    /*
     * 当前已完成的进度值
     */
    private int mProgressValue;

    /*
     * 总的进度数量（可通过xml的属性设置）
     */
    private int mMaxValue;

    /*
     * 百分比的文字大小（可通过xml的属性设置）
     */
    private int mTextSize;

    private float mTextPadding;

    /*
     * 显示的默认文字
     */
    private String mText;

    /*
     * 百分比文字是否静止在末尾（可通过xml的属性设置）
     */
    private boolean isTextStatic;

    /*
     * 绘制背景灰色线条画笔
     */
    private Paint mPaintRemainingValue;

    /*
     * 绘制进度条画笔
     */
    private Paint mPaintProgressValue;

    /*
     * 绘制下载进度画笔
     */
    private Paint mPaintText;

    /*
     * 获取百分比数字的长宽
     */
    private Rect mTextBound;

    public ProgressBar(Context context) {
        this(context, null);
    }

    public ProgressBar(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ProgressBar(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        // 获取自定义属性
        TypedArray t = context.obtainStyledAttributes(attrs, R.styleable.ProgressBar);
        mProgressHeight = (int) t.getDimension(R.styleable.ProgressBar_progressHeight, dp2px(4));
        mTextSize = (int) t.getDimension(R.styleable.ProgressBar_textSize, 36);
        mMaxValue = t.getInteger(R.styleable.ProgressBar_maxValue, 100);
        mProgressColor = t.getColor(R.styleable.ProgressBar_progressColor,
                context.getResources().getColor(android.R.color.holo_blue_light));
        isTextStatic = t.getBoolean(R.styleable.ProgressBar_textStatic, true);
        mProgressValue = 0;
        mText = mProgressValue + "%";
        mTextPadding = dp2px(4);
        t.recycle();
    }

    {
        mPaintRemainingValue = new Paint();
        mPaintProgressValue = new Paint();
        mPaintText = new Paint();
        mTextBound = new Rect();
    }

    /**
     * @param currentValue 当前进度值
     */
    public void setCurrentValue(int currentValue) {
        if (currentValue > mMaxValue) return;
        mProgressValue = currentValue;
        invalidate();
    }

    /**
     * @param progressColor 进度条颜色
     */
    public void setProgressColor(int progressColor) {
        mProgressColor = progressColor;
        invalidate();
    }

    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);
        setPaint();

        // 距离顶部偏移量
        int offsetTop = (getHeight() - mProgressHeight) / 2;

        // 进度的百分比
        float percentageValue = (getMeasuredWidth() - getTextWidth() -
                (isTextStatic ? mTextPadding : 2 * mTextPadding)) * mProgressValue / mMaxValue;

        // 绘制进度条已进行的颜色
        setPaint(mPaintProgressValue, mProgressColor);
        canvas.drawLine(0, offsetTop, percentageValue, offsetTop, mPaintProgressValue);

        float startText, startRemaining, endRemaining;
        if (isTextStatic) {
            startText = getMeasuredWidth() - getTextWidth();
            startRemaining = percentageValue;
            endRemaining = startText - mTextPadding;
        } else {
            startText = percentageValue + mTextPadding;
            startRemaining = percentageValue + getTextWidth() + 2 * mTextPadding;
            endRemaining = getMeasuredWidth();
        }

        // 绘制文字
        canvas.drawText(mText, startText, offsetTop + (mTextBound.height() >> 1), mPaintText);

        // 绘制剩余进度条的底色
        setPaint(mPaintRemainingValue, getResources().getColor(R.color.lightGrayColor));
        canvas.drawLine(startRemaining, offsetTop, endRemaining, offsetTop, mPaintRemainingValue);
    }

    private void setPaint() {
        // 根据文字的位置设置文字的Rect大小
        mPaintText.setAntiAlias(true);
        mPaintText.setColor(mProgressColor);                                    // 设置绘制百分比文字属性
        mPaintText.setTextSize(mTextSize);
        mText = mProgressValue + "%";
        mPaintText.getTextBounds(mText, 0, mText.length(), mTextBound);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int width = measureWidth(getSuggestedMinimumWidth(), widthMeasureSpec);
        int height = measureHeight(getSuggestedMinimumHeight(), heightMeasureSpec);
        setMeasuredDimension(width, height);
    }

    /**
     * 计算需要的宽度
     *
     * @param defaultWidth
     * @param measureSpec
     * @return
     */
    private int measureWidth(int defaultWidth, int measureSpec) {
        int width = defaultWidth;
        int specMode = MeasureSpec.getMode(measureSpec);
        int specSize = MeasureSpec.getSize(measureSpec);
        if (specMode == MeasureSpec.AT_MOST) {
            width = specSize;
        } else if (specMode == MeasureSpec.EXACTLY) {
            width = specSize;
        } else if (specMode == MeasureSpec.UNSPECIFIED) {
            width = Math.max(defaultWidth, specSize);
        }
        return width;
    }

    /**
     * 计算需要的高度
     *
     * @param defaultHeight
     * @param measureSpec
     * @return
     */
    private int measureHeight(int defaultHeight, int measureSpec) {
        int height = defaultHeight;
        int specMode = MeasureSpec.getMode(measureSpec);
        int specSize = MeasureSpec.getSize(measureSpec);
        if (specMode == MeasureSpec.AT_MOST) {
            height = specSize;
        } else if (specMode == MeasureSpec.EXACTLY) {
            height = specSize;
        } else if (specMode == MeasureSpec.UNSPECIFIED) {
            height = Math.max(defaultHeight, specSize);
        }
        return height;
    }

    private void setPaint(Paint paint, int color) {
        paint.setAntiAlias(true);
        paint.setColor(color);
        paint.setStyle(Paint.Style.FILL);
        paint.setStrokeWidth(mProgressHeight);
    }

    /**
     * 获取文字的宽度
     */
    private int getTextWidth() {
        Paint paint = new Paint();
        paint.setTextSize(mTextSize);
        paint.setTextAlign(Paint.Align.CENTER);
        paint.getTextBounds(mText, 0, mText.length(), mTextBound);
        return mTextBound.width() + 2;
    }

    /**
     * @param value
     * @return dp值
     */
    private int dp2px(int value) {
        return OsHelper.dp2px(value);
    }
}
