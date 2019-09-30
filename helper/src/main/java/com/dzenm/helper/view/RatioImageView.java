package com.dzenm.helper.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.MotionEvent;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageView;

import com.dzenm.helper.R;
import com.dzenm.helper.os.OsHelper;

public class RatioImageView extends AppCompatImageView {

    /**
     * 四个角的圆角大小, 分别为左上, 右上, 右下, 左下
     */
    private int[] mCornerRadius = new int[4];

    /**
     * 是否为圆形
     */
    private boolean isCircle;

    /**
     * 绘制使用的画笔
     */
    private Paint mPaint;

    /**
     * 点击反馈时的前景颜色
     */
    private int mForegroundColor;

    /**
     * ImageView的宽高比例
     */
    private float mRatio;

    /**
     * 设置ImageView为圆形图片
     *
     * @param circle 是否设置为圆形
     */
    public void setCircle(boolean circle) {
        isCircle = circle;
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

    public RatioImageView(Context context) {
        this(context, null);
    }

    public RatioImageView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RatioImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray t = context.obtainStyledAttributes(attrs, R.styleable.RatioImageView);

        mRatio = t.getFloat(R.styleable.RatioImageView_ratio, 1f);
        mForegroundColor = t.getColor(R.styleable.RatioImageView_foregroundColor, 0xFFBDBDBD);
        isCircle = t.getBoolean(R.styleable.RatioImageView_isCircle, false);
        mCornerRadius[0] = mCornerRadius[1] = mCornerRadius[2] = mCornerRadius[3] =
                (int) t.getDimension(R.styleable.RatioImageView_cornerRadius, dp2px(8));
        mCornerRadius[0] = (int) t.getDimension(R.styleable.RatioImageView_top_left_cornerRadius, dp2px(8));
        mCornerRadius[1] = (int) t.getDimension(R.styleable.RatioImageView_top_right_cornerRadius, dp2px(8));
        mCornerRadius[2] = (int) t.getDimension(R.styleable.RatioImageView_bottom_left_cornerRadius, dp2px(8));
        mCornerRadius[3] = (int) t.getDimension(R.styleable.RatioImageView_top_left_cornerRadius, dp2px(8));
        t.recycle();
        mPaint = new Paint();
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
                if (drawable != null && isClickable()) {
                    drawable.mutate().setColorFilter(mForegroundColor, PorterDuff.Mode.MULTIPLY);
                }
                break;
            case MotionEvent.ACTION_MOVE:
                break;
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP:
                Drawable drawableUp = getDrawable();
                if (drawableUp != null && isClickable()) {
                    drawableUp.mutate().clearColorFilter();
                }
                break;
        }
        return super.onTouchEvent(event);
    }

    @Override
    public void draw(Canvas canvas) {
        // 保存图片
        int w = getWidth(), h = getHeight();
        canvas.saveLayer(getRectF(), null, Canvas.ALL_SAVE_FLAG);
        super.draw(canvas);

        mPaint.reset();
        mPaint.setAntiAlias(true);
        mPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_OUT));

        // 绘制目标图片的Path
        canvas.drawPath(getTargetPath(w, h), mPaint);

        // 重绘图片
        canvas.restore();
    }

    /**
     * 获取目标图像的Path
     *
     * @param w 宽度
     * @param h 长度
     * @return 目标图像的Path
     */
    private Path getTargetPath(int w, int h) {
        Path path = getOriginImagePath();
        Path targetPath = new Path();
        // 获取目标图片的Path
        if (isCircle) {
            float radius = (Math.min(w, h) / 2.0f);
            targetPath.addCircle(w / 2.0f, h / 2.0f, radius, Path.Direction.CW);
        } else {
            targetPath.addRoundRect(getRectF(), getRadii(0), Path.Direction.CW);
        }
        // 将目标图片的Path和原始图片的Path取目标图片没有的部分, 裁剪原始图片
        path.op(targetPath, Path.Op.DIFFERENCE);
        return path;
    }

    /**
     * 获取和原图片大小的Path
     *
     * @return 原图片大小的Path
     */
    private Path getOriginImagePath() {
        Path imagePath = new Path();
        imagePath.addRect(getRectF(), Path.Direction.CW);
        return imagePath;
    }

    /**
     * 获取圆角矩形的圆角大小
     *
     * @param strokeWidth 边框的大小
     * @return 圆角矩形的圆角大小
     */
    private float[] getRadii(float strokeWidth) {
        float[] radii = new float[8];
        for (int i = 0; i < mCornerRadius.length; i++) {
            radii[2 * i] = radii[2 * i + 1] = mCornerRadius[i] - strokeWidth / 2.0f;
        }
        return radii;
    }

    /**
     * 获取原图片区域的大小
     *
     * @return 和原图片大小相同的区域
     */
    private RectF getRectF() {
        return new RectF(0, 0, getWidth(), getHeight());
    }

    private int dp2px(float dp) {
        return OsHelper.dp2px(dp);
    }
}