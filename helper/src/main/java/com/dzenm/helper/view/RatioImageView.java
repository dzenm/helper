package com.dzenm.helper.view;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.graphics.Xfermode;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.MotionEvent;

import androidx.annotation.ColorInt;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageView;

import com.dzenm.helper.R;

/**
 * 根据宽高比例自动计算高度ImageView
 */
public class RatioImageView extends AppCompatImageView {

    /**
     * 点击反馈时的前景颜色
     */
    private int mForegroundColor;

    /**
     * ImageView的宽高比例
     */
    private float mRatio;

    /**
     * 是否显示为圆形, 如果为圆形则设置的corner无效
     * 是否覆盖图片(针对border、inner_border)
     */
    private boolean isCircle, isCoverImage;

    /**
     * 遮罩颜色
     */
    private int mMaskColor;

    /**
     * 可显示区域的宽度, 高度
     */
    private int mWidth, mHeight;

    /**
     * 圆形区域的半径
     */
    private float mRadius;

    /**
     * 圆角半径(统一设置)，优先级高于单独设置每个角的半径
     */
    private int mCornerRadius;

    /**
     * 圆角半径数组，上左，上右，下右，下左依次排列
     */
    private int[] mCornerRadiuses;

    /**
     * 所有边框的半径值
     */
    private float[] mBorderRadii;

    /**
     * 图片的半径值
     */
    private float[] mImageRadii;

    /**
     * 边框宽度, 颜色
     * 内层边框宽度, 颜色
     */
    private int mBorderWidth, mBorderColor, mInnerBorderWidth, mInnerBorderColor;

    /**
     * 图片占的矩形区域, 边框的矩形区域
     */
    private RectF mImageRectF, mBorderRectF;

    /**
     * 用来裁剪图片的ptah, 图片区域大小的Path
     */
    private Path mPath, mImagePath;

    /*
     * 绘制边框的画笔
     */
    private Paint mPaint;

    /*
     * 方向的个数
     */
    private int mDirectionSize = 4;

    private int defInnerBorderColor = Color.WHITE,
            defBorderColor = Color.WHITE,
            defMaskColor = Color.TRANSPARENT;

    private Xfermode mXfermode;

    public RatioImageView(Context context) {
        this(context, null);
    }

    public RatioImageView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RatioImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray t = context.obtainStyledAttributes(attrs, R.styleable.RatioImageView);
        mRatio = t.getFloat(R.styleable.RatioImageView_ratio, 1f);
        mForegroundColor = t.getColor(R.styleable.RatioImageView_foregroundColor, 0xFFBDBDBD);
        isCoverImage = t.getBoolean(R.styleable.RatioImageView_isCoverImage, isCoverImage);
        isCircle = t.getBoolean(R.styleable.RatioImageView_isCircle, isCircle);
        mBorderWidth = t.getDimensionPixelSize(R.styleable.RatioImageView_borderWidth, mBorderWidth);
        mBorderColor = t.getColor(R.styleable.RatioImageView_borderColor, defBorderColor);
        mInnerBorderWidth = t.getDimensionPixelSize(R.styleable.RatioImageView_innerBorderWidth, mInnerBorderWidth);
        mInnerBorderColor = t.getColor(R.styleable.RatioImageView_innerBorderColor, defInnerBorderColor);
        mCornerRadius = t.getDimensionPixelSize(R.styleable.RatioImageView_cornerRadius, mCornerRadius);
        mCornerRadiuses[0] = t.getDimensionPixelSize(R.styleable.RatioImageView_topLeftCornerRadius, mCornerRadiuses[0]);
        mCornerRadiuses[1] = t.getDimensionPixelSize(R.styleable.RatioImageView_topRightCornerRadius, mCornerRadiuses[1]);
        mCornerRadiuses[2] = t.getDimensionPixelSize(R.styleable.RatioImageView_bottomRightCornerRadius, mCornerRadiuses[2]);
        mCornerRadiuses[3] = t.getDimensionPixelSize(R.styleable.RatioImageView_bottomLeftCornerRadius, mCornerRadiuses[3]);
        mMaskColor = t.getColor(R.styleable.RatioImageView_maskColor, defMaskColor);
        t.recycle();
    }

    {
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.O_MR1) {
            mXfermode = new PorterDuffXfermode(PorterDuff.Mode.DST_IN);
        } else {
            mXfermode = new PorterDuffXfermode(PorterDuff.Mode.DST_OUT);
            mImagePath = new Path();
        }
        mBorderRadii = new float[2 * mDirectionSize];
        mImageRadii = new float[2 * mDirectionSize];
        mCornerRadiuses = new int[mDirectionSize];

        mBorderRectF = new RectF();
        mImageRectF = new RectF();

        mPaint = new Paint();
        mPath = new Path();

        calculateBorderAndImageRadii();
        clearInnerBorderWidth();
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

    /**
     * 计算外边框的RectF
     */
    private void initBorderRectF() {
        if (!isCircle)
            mBorderRectF.set(mBorderWidth / 2.0f, mBorderWidth / 2.0f, mWidth - mBorderWidth / 2.0f, mHeight - mBorderWidth / 2.0f);
    }

    /**
     * 计算图片原始区域的RectF
     */
    private void initImageRectF() {
        if (isCircle) {
            mRadius = Math.min(mWidth, mHeight) / 2.0f;
            mImageRectF.set(mWidth / 2.0f - mRadius, mHeight / 2.0f - mRadius, mWidth / 2.0f + mRadius, mHeight / 2.0f + mRadius);
        } else {
            mImageRectF.set(0, 0, mWidth, mHeight);
            if (isCoverImage) mImageRectF = mBorderRectF;
        }
    }

    /**
     * 计算RectF的圆角半径
     */
    private void calculateBorderAndImageRadii() {
        if (isCircle) return;
        if (mCornerRadius > 0) {
            for (int i = 0; i < mBorderRadii.length; i++) {
                mBorderRadii[i] = mCornerRadius;
                mImageRadii[i] = mCornerRadius - mBorderWidth / 2.0f;
            }
        } else {
            for (int i = 0; i < mCornerRadiuses.length; i++) {
                mBorderRadii[2 * i] = mBorderRadii[2 * i + 1] = mCornerRadiuses[i];
                mImageRadii[2 * i] = mImageRadii[2 * i + 1] = mCornerRadiuses[i] - mBorderWidth / 2.0f;
            }
        }
    }

    private void calculateBorderRadiiAndRectF(boolean reset) {
        if (reset) mCornerRadius = 0;
        calculateBorderAndImageRadii();
        initBorderRectF();
        invalidate();
    }

    /**
     * 目前圆角矩形情况下不支持inner_border，需要将其置0
     */
    private void clearInnerBorderWidth() {
        if (!isCircle) mInnerBorderWidth = 0;
    }

    public void setCoverImage(boolean isCoverImage) {
        this.isCoverImage = isCoverImage;
        initImageRectF();
        invalidate();
    }

    public void isCircle(boolean isCircle) {
        this.isCircle = isCircle;
        clearInnerBorderWidth();
        initImageRectF();
        invalidate();
    }

    public void setBorderWidth(int borderWidth) {
        this.mBorderWidth = dp2px(borderWidth);
        calculateBorderRadiiAndRectF(false);
    }

    public void setBorderColor(@ColorInt int borderColor) {
        this.mBorderColor = borderColor;
        invalidate();
    }

    public void setInnerBorderWidth(int innerBorderWidth) {
        this.mInnerBorderWidth = dp2px(innerBorderWidth);
        clearInnerBorderWidth();
        invalidate();
    }

    public void setInnerBorderColor(@ColorInt int innerBorderColor) {
        this.mInnerBorderColor = innerBorderColor;
        invalidate();
    }

    public void setCornerRadius(int cornerRadius) {
        this.mCornerRadius = dp2px(cornerRadius);
        calculateBorderRadiiAndRectF(false);
    }

    public void setCornerRadius(int topLeftCornerRadius, int topRightCornerRadius, int bottomRightCornerRadius, int bottomLeftCornerRadius) {
        mCornerRadiuses[0] = topLeftCornerRadius;
        mCornerRadiuses[1] = topRightCornerRadius;
        mCornerRadiuses[2] = bottomRightCornerRadius;
        mCornerRadiuses[3] = bottomLeftCornerRadius;
        calculateBorderRadiiAndRectF(true);
    }

    public void setMaskColor(@ColorInt int mMaskColor) {
        this.mMaskColor = mMaskColor;
        invalidate();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mWidth = w;
        mHeight = h;

        initBorderRectF();
        initImageRectF();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        // 使用图形混合模式来显示指定区域的图片
        canvas.saveLayer(mImageRectF, null, Canvas.ALL_SAVE_FLAG);
        if (!isCoverImage) {
            int strokeWidth = 2 * (mBorderWidth + mInnerBorderWidth);
            // 缩小画布，使图片内容不被borders覆盖
            canvas.scale((mWidth - strokeWidth) / mWidth, (mHeight - strokeWidth) / mHeight,
                    mWidth / 2.0f, mHeight / 2.0f);
        }
        super.onDraw(canvas);

        mPaint.reset();
        mPath.reset();
        if (isCircle) {
            mPath.addCircle(mWidth / 2.0f, mHeight / 2.0f, mRadius, Path.Direction.CCW);
        } else {
            mPath.addRoundRect(mImageRectF, mImageRadii, Path.Direction.CCW);
        }

        mPaint.setAntiAlias(true);
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setXfermode(mXfermode);
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.O_MR1) {
            canvas.drawPath(mPath, mPaint);
        } else {
            mImagePath.addRect(mImageRectF, Path.Direction.CCW);
            // 计算tempPath和mPath的差集
            mImagePath.op(mPath, Path.Op.DIFFERENCE);
            canvas.drawPath(mImagePath, mPaint);
        }
        mPaint.setXfermode(null);

        // 绘制遮罩
        if (mMaskColor != 0) {
            mPaint.setColor(mMaskColor);
            canvas.drawPath(mPath, mPaint);
        }

        // 恢复画布
        canvas.restore();
        // 绘制边框
        if (isCircle) {
            if (mBorderWidth > 0)
                drawCircleBorder(canvas, mBorderWidth, mBorderColor, mRadius - mBorderWidth / 2.0f);
            if (mInnerBorderWidth > 0)
                drawCircleBorder(canvas, mInnerBorderWidth, mInnerBorderColor, mRadius - mBorderWidth - mInnerBorderWidth / 2.0f);
        } else {
            if (mBorderWidth > 0)
                drawRectFBorder(canvas, mBorderWidth, mBorderColor, mBorderRectF, mBorderRadii);
        }
    }

    private void drawCircleBorder(Canvas canvas, int borderWidth, int borderColor, float radius) {
        initBorderPaint(borderWidth, borderColor);
        mPath.addCircle(mWidth / 2.0f, mHeight / 2.0f, radius, Path.Direction.CCW);
        canvas.drawPath(mPath, mPaint);
    }

    private void drawRectFBorder(Canvas canvas, int borderWidth, int borderColor, RectF rectF, float[] radii) {
        initBorderPaint(borderWidth, borderColor);
        mPath.addRoundRect(rectF, radii, Path.Direction.CCW);
        canvas.drawPath(mPath, mPaint);
    }

    private void initBorderPaint(int borderWidth, int borderColor) {
        mPath.reset();
        mPaint.setStrokeWidth(borderWidth);
        mPaint.setColor(borderColor);
        mPaint.setStyle(Paint.Style.STROKE);
    }

    public static int dp2px(int value) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, value, Resources.getSystem().getDisplayMetrics());
    }
}
