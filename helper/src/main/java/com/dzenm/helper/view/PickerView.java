package com.dzenm.helper.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * @author dzenm
 * @date 2019-07-22 17:24
 */
public class PickerView extends View {

    private int mViewHeight;

    private int mViewWidth;

    /**
     * text之间间距和minTextSize之比
     */
    public static final float MARGIN_ALPHA = 3.0f;

    /**
     * 自动回滚到中间的速度
     */
    public static final float SPEED = 10;

    /**
     * 是否可以循环滚动
     */
    private boolean isLoop = true;

    /**
     * 数据列表
     */
    private List<String> mDataList;

    /**
     * 选中的位置，这个位置是mDataList的中心位置，一直不变
     */
    private int mCurrentSelectedIndex;

    /**
     * 选中的文字大小
     */
    private float mSelectedTextSize = 40;

    /**
     * 未选中的文字大小
     */
    private float mUnSelectedTextSize = 20;

    /**
     * 文本显示的最大透明度
     */
    private float mMaxTextAlpha = 255;

    /**
     * 文本显示的最小透明度
     */
    private float mMinTextAlpha = 96;

    /**
     * 选中文本颜色
     */
    private int mPrimaryTextColor = 0x212121;

    /**
     * 未选中文本颜色
     */
    private int mSecondTextColor = 0x757575;

    /**
     * 最后一次手指放下的Y坐标值
     */
    private float mLastDownY;

    /**
     * 滑动的距离
     */
    private float mMoveLen = 0;

    /**
     * 是否初始化
     */
    private boolean isInit = false;

    /**
     * 选中监听事件
     */
    private onSelectListener mSelectListener;

    /**
     * 是否可以滚动
     */
    private boolean isScroll = true;

    private Timer timer;
    private MyTimerTask mTask;

    private Paint mSelectedPaint, mUnSelectedPaint;

    @SuppressLint("HandlerLeak")
    Handler mUpdateHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (Math.abs(mMoveLen) < SPEED) {
                mMoveLen = 0;
                if (mTask != null) {
                    mTask.cancel();
                    mTask = null;
                    performSelect();
                }
            } else {
                // 这里mMoveLen / Math.abs(mMoveLen)是为了保有mMoveLen的正负号，以实现上滚或下滚
                mMoveLen = mMoveLen - mMoveLen / Math.abs(mMoveLen) * SPEED;
            }
            invalidate();
        }

    };

    public PickerView(Context context) {
        this(context, null);
    }

    public PickerView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PickerView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    {
        timer = new Timer();
        mDataList = new ArrayList<>();

        // 第一个paint(当前选中)
        mSelectedPaint = newPaint();

        // 第二个paint(未选中)
        mUnSelectedPaint = newPaint();
    }

    private Paint newPaint() {
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setStyle(Paint.Style.FILL);
        paint.setTextAlign(Paint.Align.CENTER);
        return paint;
    }

    public void setOnSelectListener(onSelectListener listener) {
        mSelectListener = listener;
    }

    /**
     * @param data 显示的List数据
     */
    public void setData(List<String> data) {
        mDataList = data;
        mCurrentSelectedIndex = data.size() / 4;
        invalidate();
    }

    /**
     * @param loop 循环显示(控制内容是否首尾相连)
     */
    public void setIsLoop(boolean loop) {
        isLoop = loop;
    }

    /**
     * @param scroll 控制内容是否可以滚动
     */
    public void setScroll(boolean scroll) {
        isScroll = scroll;
    }

    /**
     * @return 获取选中的内容
     */
    public String getSelected() {
        return mDataList.get(mCurrentSelectedIndex);
    }

    /**
     * @param primaryTextColor 主文本颜色
     */
    public void setPrimaryTextColor(int primaryTextColor) {
        mPrimaryTextColor = primaryTextColor;
        invalidate();
    }

    /**
     * @param secondTextColor 副文本颜色
     */
    public void setSecondTextColor(int secondTextColor) {
        mSecondTextColor = secondTextColor;
        invalidate();
    }

    /**
     * @param selected 选中的item的index
     */
    public void setSelected(int selected) {
        mCurrentSelectedIndex = selected;
        if (isLoop) {
            int distance = mDataList.size() / 2 - mCurrentSelectedIndex;
            if (distance < 0) {
                for (int i = 0; i < -distance; i++) {
                    moveHeadToTail();
                    mCurrentSelectedIndex--;
                }
            } else if (distance > 0) {
                for (int i = 0; i < distance; i++) {
                    moveTailToHead();
                    mCurrentSelectedIndex++;
                }
            }
        }
        invalidate();
    }

    /**
     * @param selected 选中的item的内容
     */
    public void setSelected(String selected) {
        for (int i = 0; i < mDataList.size(); i++) {
            if (mDataList.get(i).equals(selected)) {
                setSelected(i);
                break;
            }
        }
    }

    private void moveHeadToTail() {
        if (isLoop) {
            String head = mDataList.get(0);
            mDataList.remove(0);
            mDataList.add(head);
        }
    }

    private void moveTailToHead() {
        if (isLoop) {
            String tail = mDataList.get(mDataList.size() - 1);
            mDataList.remove(mDataList.size() - 1);
            mDataList.add(0, tail);
        }

    }

    private void performSelect() {
        if (mSelectListener != null) mSelectListener.onSelect(this);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        mViewHeight = getMeasuredHeight();
        mViewWidth = getMeasuredWidth();

        // 按照View的高度计算字体大小
        mSelectedTextSize = mViewHeight / 8.8f;
        mUnSelectedTextSize = mSelectedTextSize / 1.8f;
        isInit = true;
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        // 根据index绘制view
        if (isInit)
            drawData(canvas);
    }

    private void drawData(Canvas canvas) {
        // 先绘制选中的text再往上往下绘制其余的text
        float scale = parabola(mViewHeight / 4.0f, mMoveLen);
        float size = (mSelectedTextSize - mUnSelectedTextSize) * scale + mUnSelectedTextSize;

        mSelectedPaint.setColor(mPrimaryTextColor);
        mSelectedPaint.setTextSize(size);
        mSelectedPaint.setAlpha((int) ((mMaxTextAlpha - mMinTextAlpha) * scale + mMinTextAlpha));

        // text居中绘制，注意baseline的计算才能达到居中，y值是text中心坐标
        float x = (float) (mViewWidth / 2.0);
        float y = (float) (mViewHeight / 2.0 + mMoveLen);
        Paint.FontMetricsInt fmi = mSelectedPaint.getFontMetricsInt();
        float baseline = (float) (y - (fmi.bottom / 2.0 + fmi.top / 2.0));

        canvas.drawText(mDataList.get(mCurrentSelectedIndex), x, baseline, mSelectedPaint);
        // 绘制上方data
        for (int i = 1; (mCurrentSelectedIndex - i) >= 0; i++) {
            drawOtherText(canvas, i, -1);
        }
        // 绘制下方data
        for (int i = 1; (mCurrentSelectedIndex + i) < mDataList.size(); i++) {
            drawOtherText(canvas, i, 1);
        }
    }

    /**
     * @param canvas
     * @param position 距离mCurrentSelected的差值
     * @param type     1表示向下绘制，-1表示向上绘制
     */
    private void drawOtherText(Canvas canvas, int position, int type) {
        float d = MARGIN_ALPHA * mUnSelectedTextSize * position + type * mMoveLen;
        float scale = parabola(mViewHeight / 4.0f, d);
        float size = (mSelectedTextSize - mUnSelectedTextSize) * scale + mUnSelectedTextSize;

        mUnSelectedPaint.setColor(mSecondTextColor);
        mUnSelectedPaint.setTextSize(size);
        mUnSelectedPaint.setAlpha((int) ((mMaxTextAlpha - mMinTextAlpha) * scale + mMinTextAlpha));
        float y = (float) (mViewHeight / 2.0 + type * d);
        Paint.FontMetricsInt fmi = mUnSelectedPaint.getFontMetricsInt();
        float baseline = (float) (y - (fmi.bottom / 2.0 + fmi.top / 2.0));
        canvas.drawText(mDataList.get(mCurrentSelectedIndex + type * position),
                (float) (mViewWidth / 2.0), baseline, mUnSelectedPaint);
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getActionMasked()) {
            case MotionEvent.ACTION_DOWN:
                doDown(event);
                break;
            case MotionEvent.ACTION_MOVE:
                mMoveLen += (event.getY() - mLastDownY);
                if (mMoveLen > MARGIN_ALPHA * mUnSelectedTextSize / 2) {
                    if (!isLoop && mCurrentSelectedIndex == 0) {
                        mLastDownY = event.getY();
                        invalidate();
                        return true;
                    }
                    if (!isLoop) mCurrentSelectedIndex--;
                    // 往下滑超过离开距离
                    moveTailToHead();
                    mMoveLen = mMoveLen - MARGIN_ALPHA * mUnSelectedTextSize;
                } else if (mMoveLen < -MARGIN_ALPHA * mUnSelectedTextSize / 2) {
                    if (mCurrentSelectedIndex == mDataList.size() - 1) {
                        mLastDownY = event.getY();
                        invalidate();
                        return true;
                    }
                    if (!isLoop) mCurrentSelectedIndex++;
                    // 往上滑超过离开距离
                    moveHeadToTail();
                    mMoveLen = mMoveLen + MARGIN_ALPHA * mUnSelectedTextSize;
                }

                mLastDownY = event.getY();
                invalidate();
                break;
            case MotionEvent.ACTION_UP:
                doUp(event);
                break;
        }
        return true;
    }

    private void doDown(MotionEvent event) {
        if (mTask != null) {
            mTask.cancel();
            mTask = null;
        }
        mLastDownY = event.getY();
    }

    private void doMove(MotionEvent event) {
        mMoveLen += (event.getY() - mLastDownY);
        if (mMoveLen > MARGIN_ALPHA * mUnSelectedTextSize / 2) {
            // 往下滑超过离开距离
            moveTailToHead();
            mMoveLen = mMoveLen - MARGIN_ALPHA * mUnSelectedTextSize;
        } else if (mMoveLen < -MARGIN_ALPHA * mUnSelectedTextSize / 2) {
            // 往上滑超过离开距离
            moveHeadToTail();
            mMoveLen = mMoveLen + MARGIN_ALPHA * mUnSelectedTextSize;
        }

        mLastDownY = event.getY();
        invalidate();
    }

    private void doUp(MotionEvent event) {
        // 抬起手后mCurrentSelected的位置由当前位置move到中间选中位置
        if (Math.abs(mMoveLen) < 0.0001) {
            mMoveLen = 0;
            return;
        }
        if (mTask != null) {
            mTask.cancel();
            mTask = null;
        }
        mTask = new MyTimerTask(mUpdateHandler);
        timer.schedule(mTask, 0, 10);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        return isScroll && super.dispatchTouchEvent(event);
    }

    /**
     * 抛物线
     *
     * @param zero 零点坐标
     * @param x    偏移量
     * @return scale
     */
    private float parabola(float zero, float x) {
        float f = (float) (1 - Math.pow(x / zero, 2));
        return f < 0 ? 0 : f;
    }

    public interface onSelectListener {
        void onSelect(PickerView pickerView);
    }

    static class MyTimerTask extends TimerTask {
        Handler mHandler;

        MyTimerTask(Handler handler) {
            mHandler = handler;
        }

        @Override
        public void run() {
            mHandler.sendMessage(mHandler.obtainMessage());
        }
    }
}