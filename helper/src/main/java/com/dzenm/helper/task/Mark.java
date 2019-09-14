package com.dzenm.helper.task;

import com.dzenm.helper.log.Logger;

/**
 * @author dinzhenyan
 * @date 2019-06-27 11:03
 * 在很多个或者异步任务时， 用于判断所有的任务是否执行完毕。在构造方法{@link #Mark(int)}里添加任务
 * 的个数，然后在每一个异步任务结束之后通过{@link #set(int)}。方法对该任务进行标记，当所有任务执行
 * 完成，通过{@link #setOnMarkListener(OnMarkListener)}方法进行回调
 */
public class Mark {

    private boolean[] isMarks;

    private OnMarkListener mOnMarkListener;

    public void setOnMarkListener(OnMarkListener onMarkListener) {
        mOnMarkListener = onMarkListener;
    }

    /**
     * 初始化需要标记的个数
     *
     * @param size
     */
    public Mark(int size) {
        isMarks = new boolean[size];
        for (int i = 0; i < size; i++) {
            isMarks[i] = false;
        }
    }

    /**
     * 获取position位置的是否被标记
     *
     * @param position
     * @return
     */
    public boolean get(int position) {
        return isMarks[position];
    }

    /**
     * 设置position位置的被标记
     *
     * @param position
     */
    public void set(int position) {
        isMarks[position] = true;
        if (isMarkFinished()) {
            mOnMarkListener.onFinished();
        }
    }

    /**
     * 是否所有的被标记
     *
     * @return
     */
    public boolean isMarkFinished() {
        for (int i = 0; i < isMarks.length; i++) {
            if (!isMarks[i]) {
                Logger.e("mark position " + i + " is not init");
                return false;
            }
        }
        Logger.d("mark init finished");
        return true;
    }

    public interface OnMarkListener {
        void onFinished();
    }
}
