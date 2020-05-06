package com.dzenm.lib.view;

import android.graphics.Matrix;
import android.graphics.PointF;
import android.view.MotionEvent;
import android.widget.ImageView;

/**
 * @author dzenm
 * @date 2019-10-10 16:42
 */
public class ImageZoomHelper {

    private static final int NONE = 0;
    private static final int DRAG = 1;
    private static final int ZOOM = 2;

    private static final float MAX_SCALE = 3;

    /**
     * 设定第一个手指触摸屏幕为移动图片的参考点
     */
    private PointF mMovePoint = new PointF();

    /**
     * 初始的两个手指按下的触摸点的距离
     */
    private float mPointDistance = 1f, mMinScale;

    private int mMode = NONE;

    private boolean isDefaultMatrix = true;

    private Matrix mMatrix = new Matrix();
    private Matrix mSavedMatrix = new Matrix();

    private ImageView mImageView;

    void bindImageView(ImageView imageView, MotionEvent event) {
        if (isDefaultMatrix) {
            mImageView = imageView;
            mMinScale = getScale(mImageView.getImageMatrix());
            isDefaultMatrix = false;
        }
        setTouchEvent(event);
    }

    private void setTouchEvent(MotionEvent event) {
        // 进行与操作是为了判断多点触摸
        switch (event.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN:
                // 第一个手指按下事件, 将ImageView设置为缩放模式, 获取原始的缩放系数
                mImageView.setScaleType(ImageView.ScaleType.MATRIX);
                mMatrix.set(mImageView.getImageMatrix());

                // 保存缩放前的Matrix和X轴, y轴坐标点
                mSavedMatrix.set(mMatrix);
                mMovePoint.set(event.getX(), event.getY());

                // 设置为开始缩放模式
                mMode = DRAG;
                break;
            case MotionEvent.ACTION_POINTER_DOWN:
                // 第二个手指按下, 两指之间的距离超过10时设置为缩放事件
                mPointDistance = getPointDistance(event);
                if (mPointDistance > 10f) {
                    mMode = ZOOM;
                }
                break;
            case MotionEvent.ACTION_POINTER_UP:
            case MotionEvent.ACTION_UP:
                float upMatrix = getScale(mMatrix);
                if (upMatrix == mMinScale) {
                    mMatrix.postTranslate(0, 0);
                }
                // 手指放开事件, 重置为初始事件
                mMode = NONE;
                break;
            case MotionEvent.ACTION_MOVE:
                // 手指滑动事件
                if (mMode == DRAG) {
                    // 一个手指拖动时, 并且当前缩放系数不等于初始缩放系数时进行移动
                    mMatrix.set(mSavedMatrix);
                    float scale = getScale(mMatrix);
                    if (scale != mMinScale) {
                        mMatrix.postTranslate(event.getX() - mMovePoint.x, event.getY() - mMovePoint.y);
                    }
                } else if (mMode == ZOOM) {
                    // 两个手指滑动之后的新距离
                    float newDistance = getPointDistance(event);
                    if (newDistance > 10f) {
                        mMatrix.set(mSavedMatrix);
                        float scale = newDistance / mPointDistance;
                        float matrixScale = getScale(mMatrix);

                        if (scale * matrixScale > MAX_SCALE) {
                            scale = MAX_SCALE / matrixScale;
                        } else if (scale * matrixScale < mMinScale) {
                            scale = mMinScale / matrixScale;
                        }
                        mMatrix.postScale(scale, scale, mImageView.getWidth() / 2, mImageView.getHeight() / 2);
                    }
                }
                break;
        }
        // 重新设置ImageView的缩放系数
        mImageView.setImageMatrix(mMatrix);
    }

    private float getScale(Matrix matrix) {
        float[] values = new float[9];
        matrix.getValues(values);
        return values[Matrix.MSCALE_X];
    }

    /**
     * 获取两个触摸点之间的距离
     *
     * @param event 触摸事件
     * @return 两个触摸点之间的距离
     */
    private float getPointDistance(MotionEvent event) {
        float x = event.getX(0) - event.getX(1);
        float y = event.getY(0) - event.getY(1);
        return (float) Math.sqrt(x * x + y * y);
    }
}
