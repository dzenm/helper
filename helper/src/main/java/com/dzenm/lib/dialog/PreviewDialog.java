package com.dzenm.lib.dialog;

import android.annotation.SuppressLint;
import android.graphics.PointF;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.dzenm.lib.drawable.DrawableHelper;
import com.dzenm.lib.log.Logger;
import com.dzenm.lib.os.ScreenHelper;
import com.dzenm.lib.view.ImageLoader;
import com.dzenm.lib.view.RatioImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

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
@SuppressLint("ValidFragment")
public class PreviewDialog extends AbsDialogFragment implements View.OnTouchListener {

    private static final int EVENT_NONE = -1;
    private static final int EVENT_DOWN = 0;
    private static final int EVENT_POINTER_DOWN = 1;

    private static final int MODE_NONE = 11;
    private static final int MODE_LONG_CLICK = 12;
    private static final int MODE_ZOOM = 13;
    private static final int MODE_ZOOMING = 14;
    private static final int MODE_MOVE = 15;

    private static final int ZOOM_NONE = 20;
    private static final int ZOOM_START = 21;
    private static final int ZOOM_EXPAND = 22;
    private static final int ZOOM_MOVE = 23;
    private static final int ZOOM_SHRINK = 24;
    private static final int ZOOM_END = 25;

    private ImageLoader mImageLoader;
    private Object mImage;
    private RatioImageView mImageView;

    private int mMode = MODE_NONE, mEvent = EVENT_NONE, mZoom = ZOOM_NONE;
    private int mTouchDownX, mTouchDownY, mOffsetY, mOffsetX;
    private float mPointDistance = 0f, mCurrentScale = 0f, mLastScale = 0f;
    private long mLastTimeMillis = 0;
    private boolean isZooming = false;
    private PointF mMidPointF, mLastZoomMovePointF;
    private OnLongClickListener mOnLongClickListener;


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

    @Override
    protected View inflater(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        RelativeLayout relativeLayout = new RelativeLayout(mActivity);
        RelativeLayout.LayoutParams imageParams = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        imageParams.addRule(RelativeLayout.CENTER_IN_PARENT);
        mImageView = new RatioImageView(mActivity);
        mImageView.setLayoutParams(imageParams);

        relativeLayout.setOnTouchListener(this);
        relativeLayout.setLayoutParams(imageParams);
        relativeLayout.addView(mImageView);
        mBackground = DrawableHelper.solid(android.R.color.transparent).build();
        mDimAccount = 1f;
        return relativeLayout;
    }

    @Override
    public void onStart() {
        super.onStart();
        mImageLoader.onLoader(mImageView, mImage);
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        // 获得多点触控检测点
        int actionMasked = event.getActionMasked();
        switch (actionMasked) {
            case MotionEvent.ACTION_DOWN:
                actionDown(event);
                break;
            case MotionEvent.ACTION_POINTER_DOWN:
                actionDownPointer(event);
                break;
            case MotionEvent.ACTION_MOVE:
                actionMove(event);
                break;
            case MotionEvent.ACTION_POINTER_UP:
                actionUpPointer(event);
                break;
            case MotionEvent.ACTION_UP:
                actionUp(event);
                break;
        }
        // 注明消费此事件，不然无效果
        return true;
    }

    private void actionDown(MotionEvent event) {
        // 主控点放下, 记录主触摸点触摸的状态, 时间, 坐标
        mEvent = EVENT_DOWN;
        mLastTimeMillis = System.currentTimeMillis();
        mTouchDownX = (int) event.getX(0);
        mTouchDownY = (int) event.getY(0);
    }

    private void actionDownPointer(MotionEvent event) {
        // 辅控点放下, 当两指间距大于40时，设置两指之间的距离, 触摸的状态, 计算两指的中心点, 并重新设置缩放中心点
        float pointDistance = getPointDistance(event);
        if (pointDistance > 10f) {
            mPointDistance = pointDistance;
            mEvent = EVENT_POINTER_DOWN;
            mMidPointF = getPointMid(event);
        }
    }

    private void actionMove(MotionEvent event) {
        // 主(辅)控点移动
        // 1. 单指移动时, 移动事件或者长按事件
        // 2. 双指移动时, 缩放事件
        // 3. 放大之后的单指移动, 移动事件
        // 计算主控点偏移量
        int offsetX = (int) event.getX(0) - mTouchDownX;
        mOffsetY = (int) event.getY(0) - mTouchDownY;
        if (mEvent == EVENT_DOWN && mMode != MODE_ZOOM) {    // 单指移动时, 移动事件或者长按事件
            // 距离小于一定值时, 时间大于500毫秒, 为长按事件, 否则为移动事件
            if (mMode != MODE_LONG_CLICK && (Math.abs(offsetX) > 40 || Math.abs(mOffsetY) > 40)) {
                // 移动事件, 重新设置背景透明, 根据移动距离设置ImageView的偏移
                mMode = MODE_MOVE;
                mLastTimeMillis = 0;
                getDecorView().setBackgroundColor(getColor(android.R.color.transparent));
                float offset = (float) mOffsetY / (float) ScreenHelper.getDisplayHeight();
                setImageDismissAnimator(offsetX, mOffsetY,
                        mOffsetY > 0 ? mDimAccount - offset : mDimAccount);
            } else if (mMode != MODE_MOVE && mMode != MODE_LONG_CLICK
                    && Math.abs(offsetX) < 40 && Math.abs(mOffsetY) < 40) {
                // 长按事件, 设置黑色背景, dialog重叠时不能覆盖上一层dialog的遮罩层
                long currentTimeMillis = System.currentTimeMillis();
                if (currentTimeMillis - mLastTimeMillis > 500) {
                    mMode = MODE_LONG_CLICK;
                    mLastTimeMillis = 0;
                    getDecorView().setBackgroundColor(getColor(android.R.color.black));
                    if (mOnLongClickListener != null) {
                        mOnLongClickListener.onLongClick();
                    }
                }
            }
        } else if (mEvent == EVENT_POINTER_DOWN) {  // 双指移动时, 缩放事件
            float newDistance = getPointDistance(event);
//            int offsetPointX = (int) event.getX(1) - mTouchDownX;
//            int offsetPointY = (int) event.getY(1) - mTouchDownY;
            if (newDistance > 50f) {
                if (mMode == MODE_ZOOM) {
                    mCurrentScale = mLastScale + newDistance / mPointDistance;
                } else {
                    mMode = MODE_ZOOM;
                    setImageScaleAnimator(mMidPointF.x, mMidPointF.y, 1f);
                    mCurrentScale = newDistance / mPointDistance;
                }
                mImageView.setScaleX(mCurrentScale);
                mImageView.setScaleY(mCurrentScale);
            }
//        } else if (mEvent == EVENT_DOWN) {          // 放大之后的单指移动, 移动事件
//            setImageDismissAnimator(offsetX, offsetY, mLastScale);
        }
        Logger.d("MODE_ZOOM: " + mMode);
    }

    private void actionUpPointer(MotionEvent event) {
        // 双指缩放时, 非最后一个触控点抬起
        if (mMode == MODE_ZOOM) {
            if (mCurrentScale <= 1f) {
                mImageView.setScaleX(1f);
                mImageView.setScaleY(1f);
                mCurrentScale = mLastScale = 0f;
            } else {
                mLastScale = mCurrentScale;
            }
        }
    }

    private void actionUp(MotionEvent event) {
        // 最后一个手指抬起
        if (mMode == MODE_ZOOM) {          // 缩放状态
            if (mEvent == EVENT_DOWN) {
                mLastZoomMovePointF = new PointF(mTouchDownX, mTouchDownY);
            }
        } else if (mEvent == EVENT_DOWN) {  // 单指状态
            if (mMode == MODE_MOVE) {
                // 移动过后离开屏幕, 往下滑动超过一定距离时, 关闭dialog, 否则恢复至初始状态
                if (mOffsetY > ScreenHelper.getDisplayHeight() * 0.1) {
                    dismiss();
                } else {
                    setImageDismissAnimator(0, 0, mDimAccount);
                }
            } else if (mMode != MODE_LONG_CLICK) {  // 单击关闭dialog
                dismiss();
            }
            mLastTimeMillis = 0;
            mMode = MODE_NONE;
            mEvent = EVENT_NONE;
        }
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
        if (event.getPointerCount() != 2) return 0f;
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
