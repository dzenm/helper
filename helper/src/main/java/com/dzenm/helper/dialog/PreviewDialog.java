package com.dzenm.helper.dialog;

import android.annotation.SuppressLint;
import android.graphics.PointF;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.RelativeLayout;

import androidx.appcompat.app.AppCompatActivity;

import com.dzenm.helper.draw.DrawableHelper;
import com.dzenm.helper.os.ScreenHelper;
import com.dzenm.helper.view.ImageLoader;
import com.dzenm.helper.view.RatioImageView;

/**
 * @author dzenm
 * @date 2019-10-07 11:52
 * <pre>
 * PreviewDialog.newInstance(mActivity)
 *       .loader(new MyImageLoader())
 *       .load(binding.ivHeader.getDrawable())
 *       .show();
 * </pre>
 */
public class PreviewDialog extends AbsDialogFragment implements View.OnTouchListener {

    private static final int EVENT_NONE = -1;
    private static final int EVENT_DOWN = 0;
    private static final int EVENT_POINTER_DOWN = 1;
    private static final int EVENT_MOVE = 2;
    private static final int EVENT_POINTER_UP = 3;

    private static final int MODE_NONE = 11;
    private static final int MODE_LONG_CLICK = 12;
    private static final int MODE_ZOOM = 13;
    private static final int MODE_MOVE = 14;

    private ImageLoader mImageLoader;
    private Object mImage;
    private RatioImageView mImageView;

    private int mTouchDownX, mTouchDownY, mOffsetY, mOffsetX;
    private float mPointDistance = 0f;
    private PointF mMidPointF;
    private long mLastTimeMillis = 0;
    private OnLongClickListener mOnLongClickListener;

    private int mMode;
    private int mEvent = EVENT_NONE;
    private float mLastScale = 1f;
    private boolean isZooming = false;

    public static PreviewDialog newInstance(AppCompatActivity activity) {
        return new PreviewDialog(activity);
    }

    public PreviewDialog(AppCompatActivity activity) {
        super(activity);
    }

    public PreviewDialog setImageLoader(ImageLoader imageLoader) {
        mImageLoader = imageLoader;
        return this;
    }

    public PreviewDialog load(Object image) {
        mImage = image;
        return this;
    }

    public PreviewDialog setOnLongClickListener(OnLongClickListener onLongClickListener) {
        mOnLongClickListener = onLongClickListener;
        return this;
    }

    @Override
    protected boolean isFullScreen() {
        return true;
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void initView() {
        getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);
        RelativeLayout relativeLayout = new RelativeLayout(mActivity);
        RelativeLayout.LayoutParams imageParams = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        imageParams.addRule(RelativeLayout.CENTER_IN_PARENT);
        mImageView = new RatioImageView(mActivity);
        mImageView.setLayoutParams(imageParams);

        relativeLayout.setOnTouchListener(this);
        relativeLayout.addView(mImageView);
        mView = relativeLayout;
        mBackground = DrawableHelper.solid(android.R.color.transparent).build();
        mDimAccount = 1f;
    }

    @Override
    public void onStart() {
        super.onStart();
        mImageLoader.onLoader(mImageView, mImage);
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        int actionMasked = event.getActionMasked(); // 获得多点触控检测点
        switch (actionMasked) {
            case MotionEvent.ACTION_DOWN:           // 主控点放下
                // 记录主触摸点坐标
                mEvent = EVENT_DOWN;
                mLastTimeMillis = System.currentTimeMillis();
                mTouchDownX = (int) event.getX(0);
                mTouchDownY = (int) event.getY(0);
                break;
            case MotionEvent.ACTION_POINTER_DOWN:   // 辅控点放下
                // 当两指间距大于40时，计算两指中心点
                if (getPointDistance(event) > 10f) {
                    mEvent = EVENT_POINTER_DOWN;
                    mMidPointF = getPointMid(event);
                    setImageScaleAnimator(mMidPointF.x, mMidPointF.y, 1);
                }
                break;
            case MotionEvent.ACTION_MOVE:           // 主(辅)控点移动
                if (mEvent == EVENT_DOWN && mMode != MODE_ZOOM) {
                    // 计算主控点偏移量
                    mOffsetX = (int) event.getX(0) - mTouchDownX;
                    mOffsetY = (int) event.getY(0) - mTouchDownY;
                    if (Math.abs(mOffsetX) > 10 || Math.abs(mOffsetY) > 10) {
                        mMode = MODE_MOVE;
                        mLastTimeMillis = 0;
                        float offset = (float) mOffsetY / (float) ScreenHelper.getDisplayHeight();
                        setImageDismissAnimator(mOffsetX, mOffsetY, mOffsetY > 0 ? mDimAccount - offset : mDimAccount);
                    } else {
                        if (mMode != MODE_LONG_CLICK) {
                            long currentTimeMillis = System.currentTimeMillis();
                            if (currentTimeMillis - mLastTimeMillis > 500) {
                                mMode = MODE_LONG_CLICK;
                                mLastTimeMillis = 0;
                                if (mOnLongClickListener != null) {
                                    mView.setBackgroundColor(getColor(android.R.color.black));
                                    mOnLongClickListener.onLongClick();
                                }
                            }
                        }
                    }
                } else if (mEvent == MODE_ZOOM && isZooming) {

                } else if (mEvent == EVENT_POINTER_DOWN) {
//                    float newDistance = getPointDistance(event);
//                    if (newDistance > 0f) {
//                        mMode = MODE_ZOOM;
//                        float distance = newDistance / mPointDistance;
//                        float scale = mLastScale + distance;
//                        mImageView.setScaleX(scale);
//                        mImageView.setScaleY(scale);
//                    }
                }
                break;
            case MotionEvent.ACTION_POINTER_UP:     // 非最后一个触控点抬起
//                if (mMode != MODE_ZOOM) {
//                    setImageDismissAnimator(0, 0, mDimAccount);
//                    mMode = MODE_DOWN;
//                }
                mEvent = EVENT_POINTER_UP;
                break;
            case MotionEvent.ACTION_UP:             // 最后一个点抬起
                if (mEvent == EVENT_DOWN
                        && mMode == MODE_MOVE
                        && !isZooming) {
                    if (mOffsetY > ScreenHelper.getDisplayHeight() * 0.1) {
                        dismiss();
                    } else {
                        setImageDismissAnimator(0, 0, mDimAccount);
                    }
                } else if (mEvent == EVENT_DOWN
                        && mMode != MODE_MOVE
                        && mMode != MODE_LONG_CLICK) {
                    dismiss();
                }
                mLastTimeMillis = 0;
                mMode = MODE_NONE;
                mEvent = EVENT_NONE;
                break;
        }
        // 注明消费此事件，不然无效果
        return true;
    }

    /**
     * 设置图片滑动缩放的动画
     *
     * @param pivotX 横轴方向的中心点位置
     * @param pivotY 纵轴方向的中心点位置
     * @param scale  缩放的倍数
     */
    private void setImageScaleAnimator(float pivotX, float pivotY, float scale) {
        mImageView.setPivotX(pivotX);
        mImageView.setPivotY(pivotY);
        mImageView.setScaleX(scale);
        mImageView.setScaleY(scale);
    }

    /**
     * 设置图片滑动消失的动画
     *
     * @param translateX 横轴平移的距离
     * @param translateY 纵轴平移的距离
     * @param scale      缩放的倍数
     */
    private void setImageDismissAnimator(int translateX, int translateY, float scale) {
        mImageView.setTranslationX(translateX);
        mImageView.setTranslationY(translateY);
        mImageView.setScaleX(scale);
        mImageView.setScaleY(scale);
        mImageView.setScaleY(scale);
        getWindow().setDimAmount(scale);
    }

    /**
     * 获取两指之间的距离
     *
     * @param event 触摸事件
     * @return 两指之间的距离
     */
    private float getPointDistance(MotionEvent event) {
        float x = event.getX(0) - event.getX(1);
        float y = event.getY(0) - event.getY(1);
        return (float) Math.sqrt(x * x + y * y);
    }

    private PointF getPointMid(MotionEvent event) {
        float midX = (event.getX(0) + event.getX(1)) / 2;
        float midY = (event.getY(0) + event.getY(1)) / 2;
        return new PointF(midX, midY);
    }

    public interface OnLongClickListener {
        void onLongClick();
    }
}
