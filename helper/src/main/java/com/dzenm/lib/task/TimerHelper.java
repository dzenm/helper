package com.dzenm.lib.task;

import java.util.Timer;
import java.util.TimerTask;

/**
 * @author dinzhenyan
 * @date 2019-05-25 20:06
 * 定时任务
 */
public class TimerHelper {

    private Timer mTimer;
    private TimerTask mTimerTask;
    private long mPerid;
    private long mDelay;
    private boolean reapt;

    /**
     * 只执行一次
     *
     * @param perid
     * @param timerTask
     */
    public TimerHelper(long perid, TimerTask timerTask) {
        mPerid = perid;
        mTimerTask = timerTask;
        reapt = false;
        if (mTimer == null) {
            mTimer = new Timer();
        }
    }

    /**
     * 重复执行
     *
     * @param perid
     * @param delay
     * @param timerTask
     */
    public TimerHelper(long perid, long delay, TimerTask timerTask) {
        mPerid = perid;
        mDelay = delay;
        reapt = true;
        mTimerTask = timerTask;
        if (mTimer == null) {
            mTimer = new Timer();
        }
    }

    /*
     * 启动定时任务
     */
    public TimerHelper start() {
        if (reapt) {    // 执行一次所需的时间
            mTimer.schedule(mTimerTask, mDelay, mPerid);
        } else {
            mTimer.schedule(mTimerTask, mPerid);
        }
        return this;
    }

    /*
     * 取消定时任务
     */
    public TimerHelper stop() {
        if (mTimer != null) {
            mTimer.cancel();
            if (mTimerTask != null) {
                mTimerTask.cancel();                     // 将原任务从队列中移除
            }
        }
        return this;
    }
}
