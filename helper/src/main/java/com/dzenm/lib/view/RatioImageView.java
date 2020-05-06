package com.dzenm.lib.view;

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

import com.dzenm.lib.R;
import com.dzenm.lib.os.OsHelper;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.core.view.ViewCompat;

public class RatioImageView extends AppCompatImageView {

    private int[] mCornerRadius = new int[4];   // 四个角的圆角大小, 分别为左上, 右上, 右下, 左下
    private boolean isCircle;                   // 是否为圆形

    private boolean isMask;                     // 是否按压有灰色阴影

    private float mRatio;                       // ImageView的宽高比例
    private int mForegroundColor;               // 点击反馈时的前景颜色

    private int mNumber;                        // 数量文本显示剩余的数量
    private int mNumberColor;                   // 显示文字的颜色
    private int mNumberMaskColor;               // 默认的遮盖颜色
    private float mNumberSize;                  // 显示文字的大小单位sp

    private Paint mPaint;                       // 绘制使用的画笔

    private ImageZoomHelper mImageZoomHelper;   // 设置图片可以缩放和移动

    /**
     * @param circle 是否设置为圆形
     */
    public void setCircle(boolean circle) {
        isCircle = circle;
    }

    /**
     * @param mask 是否在按压时显示灰色阴影(默认为是), 必须设置点击事件才生效
     */
    public void setMask(boolean mask) {
        isMask = mask;
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

    public void setNumber(int number) {
        mNumber = number;
    }

    public void setNumberSize(float numberSize) {
        mNumberSize = numberSize;
    }

    public void setNumberColor(int numberColor) {
        mNumberColor = numberColor;
    }

    /**
     * 设置ImageView可以缩放和移动
     *
     * @param imageZoomHelper 创建一个新{@link ImageZoomHelper}即可绑定
     */
    public void setImageZoomHelper(ImageZoomHelper imageZoomHelper) {
        mImageZoomHelper = imageZoomHelper;
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

        mRatio = t.getFloat(R.styleable.RatioImageView_ratio, 0f);
        mForegroundColor = t.getColor(
                R.styleable.RatioImageView_foregroundColor, 0xFFBDBDBD);
        isCircle = t.getBoolean(R.styleable.RatioImageView_isCircle, false);
        isMask = t.getBoolean(R.styleable.RatioImageView_isMask, true);
        int radius = OsHelper.dp2px(8);
        mCornerRadius[0] = mCornerRadius[1] = mCornerRadius[2] = mCornerRadius[3] = (int) t.getDimension(
                R.styleable.RatioImageView_cornerRadius, radius);
        mCornerRadius[0] = (int) t.getDimension(R.styleable.RatioImageView_top_left_cornerRadius, radius);
        mCornerRadius[1] = (int) t.getDimension(R.styleable.RatioImageView_top_right_cornerRadius, radius);
        mCornerRadius[2] = (int) t.getDimension(R.styleable.RatioImageView_bottom_left_cornerRadius, radius);
        mCornerRadius[3] = (int) t.getDimension(R.styleable.RatioImageView_top_left_cornerRadius, radius);
        mNumberColor = t.getColor(R.styleable.RatioImageView_numberColor, 0xFFFFFFFF);
        mNumberMaskColor = t.getColor(R.styleable.RatioImageView_numberMaskColor, 0x66000000);
        mNumberSize = (int) t.getDimension(R.styleable.RatioImageView_numberSize, OsHelper.dp2px(30));

        t.recycle();

        mPaint = new Paint();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int width = MeasureSpec.getSize(widthMeasureSpec);
        if (mRatio != 0) {
            int height = (int) (width / mRatio);
            heightMeasureSpec = MeasureSpec.makeMeasureSpec(height, MeasureSpec.EXACTLY);
        }
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    public void draw(Canvas canvas) {
        // 保存图片
        int w = getWidth(), h = getHeight();
        canvas.saveLayer(getOriginRectF(), null, Canvas.ALL_SAVE_FLAG);
        super.draw(canvas);

        mPaint.reset();
        mPaint.setAntiAlias(true);
        mPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_OUT));

        // 绘制目标图片的Path
        canvas.drawPath(getTargetPath(w, h, Path.Direction.CW, Path.Op.DIFFERENCE), mPaint);
        // 重绘图片
        canvas.restore();

        // 添加灰色遮罩数字显示
        if (mNumber > 0) {
            mPaint.reset();
            mPaint.setAntiAlias(true);

            // 绘制灰色遮罩
            mPaint.setColor(mNumberMaskColor);
            canvas.drawPath(getTargetPath(w, h, Path.Direction.CCW, Path.Op.INTERSECT), mPaint);

            // 绘制文字
            float baseY = (getHeight() >> 1) - (mPaint.ascent() + mPaint.descent()) / 2;
            String text = "+" + mNumber;
            mPaint.setTextSize(mNumberSize);
            mPaint.setTextAlign(Paint.Align.CENTER);
            mPaint.setColor(mNumberColor);
            canvas.drawText(text, (getWidth() >> 1), baseY, mPaint);
        }
        mPaint.setXfermode(null);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (mImageZoomHelper != null) {
            mImageZoomHelper.bindImageView(this, event);
            return true;
        } else {
            Drawable drawable = getDrawable();
            if (drawable != null && isClickable()) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        /*
                         * 默认情况下，所有的从同一资源（R.drawable.XXX）加载来的drawable实例都共享一个共用的状态，
                         * 如果你更改一个实例的状态，其他所有的实例都会收到相同的通知。
                         * 使用使 mutate 可以让这个drawable变得状态不定。这个操作不能还原（变为不定后就不能变为原来的状态）。
                         * 一个状态不定的drawable可以保证它不与其他任何一个drawabe共享它的状态。
                         * 此处应该是要使用的 mutate()，但是在部分手机上会出现点击后变白的现象，所以没有使用
                         * 目前这种解决方案没有问题
                         */
                        if (isMask) {
//                            drawable.mutate().setColorFilter(mForegroundColor, PorterDuff.Mode.MULTIPLY);
                            drawable.setColorFilter(mForegroundColor, PorterDuff.Mode.MULTIPLY);
                            ViewCompat.postInvalidateOnAnimation(this);
                        }
                        break;
                    case MotionEvent.ACTION_MOVE:
                        break;
                    case MotionEvent.ACTION_CANCEL:
                    case MotionEvent.ACTION_UP:
                        if (isMask) {
//                            drawable.mutate().clearColorFilter();
                            drawable.clearColorFilter();
                            ViewCompat.postInvalidateOnAnimation(this);
                        }
                        break;
                }
            }
            return super.onTouchEvent(event);
        }
    }

    /**
     * 获取目标图像的Path
     *
     * @param w 宽度
     * @param h 长度
     * @return 目标图像的Path
     */
    private Path getTargetPath(int w, int h, Path.Direction direction, Path.Op op) {
        Path path = getOriginImagePath();
        Path targetPath = new Path();
        // 获取目标图片的Path
        if (isCircle) {
            float radius = (Math.min(w, h) / 2.0f);
            targetPath.addCircle(w / 2.0f, h / 2.0f, radius, direction);
        } else {
            targetPath.addRoundRect(getOriginRectF(), getRadii(0), direction);
        }
        // 将目标图片的Path和原始图片的Path取目标图片没有的部分, 裁剪原始图片
        path.op(targetPath, op);
        return path;
    }

    /**
     * 获取和原图片大小的Path
     *
     * @return 原图片大小的Path
     */
    private Path getOriginImagePath() {
        Path path = new Path();
        path.addRect(getOriginRectF(), Path.Direction.CW);
        return path;
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
    private RectF getOriginRectF() {
        return new RectF(0, 0, getWidth(), getHeight());
    }
}