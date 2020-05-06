package com.dzenm.lib.os;

import android.app.Activity;
import android.app.Service;

import com.dzenm.lib.log.Logger;

import java.util.Stack;

/**
 * @author dinzhenyan
 * @date 2019-06-11 12:25
 */
public class ActivityHelper {

    private static final String TAG = ActivityHelper.class.getSimpleName() + "| ";
    private static volatile ActivityHelper sActivityHelper;
    private static Stack<Activity> sActivityStack;
    private static Stack<Service> sServiceStack;

    private ActivityHelper() {
    }

    /**
     * 单一实例
     */
    public static ActivityHelper getInstance() {
        if (sActivityHelper == null) synchronized (ActivityHelper.class) {
            if (sActivityHelper == null) {
                sActivityHelper = new ActivityHelper();
                sActivityStack = new Stack<>();
                sServiceStack = new Stack<>();
            }
        }
        return sActivityHelper;
    }

    /**
     * 判断Activity是否存活
     *
     * @param clazz 判断的Activity的class
     * @return 是否存活
     */
    public boolean isAlive(Class<?> clazz) {
        if (isEmpty(sActivityStack)) return false;
        for (Activity activity : sActivityStack) {
            if (activity.getClass().equals(clazz)) {
                Logger.d(TAG + clazz.getSimpleName() + " isAlive");
                return true;
            }
        }
        Logger.d(TAG + clazz.getSimpleName() + " not isAlive");
        return false;
    }

    /**
     * 添加指定的Activity
     */
    public ActivityHelper add(Activity activity) {
        if (sActivityStack == null) throw new NullPointerException("activity stack is null");
        Logger.d(TAG + "add activity: " + activity.getClass().getSimpleName());
        sActivityStack.add(activity);
        Logger.d(TAG + "activity stack's size is " + sActivityStack.size());
        return this;
    }

    /**
     * 移除指定的Activity
     */
    public ActivityHelper remove(Activity activity) {
        if (!isEmpty(sActivityStack)) sActivityStack.remove(activity);
        Logger.d(TAG + "remove activity: " + activity.getClass().getSimpleName());
        Logger.d(TAG + "activity stack's size is " + sActivityStack.size());
        return this;
    }

    /**
     * 获取指定的Activity
     */
    public <T extends Activity> T get(Class<T> clazz) {
        if (isEmpty(sActivityStack)) return null;
        for (Activity activity : sActivityStack) {
            if (activity.getClass().equals(clazz)) {
                Logger.d(TAG + "get activity: " + clazz.getSimpleName());
                return (T) activity;
            }
        }
        Logger.d(TAG + "activity stack's size is " + sActivityStack.size());
        return null;
    }

    /**
     * 获取当前显示Activity（堆栈中最后一个传入的activity）
     */
    public Activity getTop() {
        if (!isEmpty(sActivityStack)) return sActivityStack.lastElement();
        Logger.d(TAG + "get top activity: " + sActivityStack.lastElement().getClass().getSimpleName());
        Logger.d(TAG + "activity stack's size is " + sActivityStack.size());
        return null;
    }

    /**
     * 获取所有Activity
     */
    public Stack<Activity> getAll() {
        Logger.d(TAG + "activity stack's size is " + sActivityStack.size());
        return sActivityStack;
    }

    /**
     * 结束指定的Activity
     */
    public ActivityHelper finish(Activity activity) {
        if (isEmpty(sActivityStack)) return this;
        if (activity.isFinishing()) return this;
        Logger.d(TAG + "finish activity: " + activity.getClass().getSimpleName());
        activity.finish();
        Logger.d(TAG + "activity stack's size is " + sActivityStack.size());
        return this;
    }

    /**
     * 结束指定类名的Activity
     */
    public ActivityHelper finish(Class clazz) {
        if (isEmpty(sActivityStack)) return this;
        for (Activity activity : sActivityStack) {
            if (activity.getClass().equals(clazz)) {
                Logger.d(TAG + "finish activity: " + activity.getClass().getSimpleName());
                finish(activity);
                break;
            }
        }
        Logger.d(TAG + "activity stack's size is " + sActivityStack.size());
        return this;
    }

    /**
     * 结束除当前传入以外所有Activity
     */
    public ActivityHelper finishOthers(Class clazz) {
        if (isEmpty(sActivityStack)) return this;
        Logger.d(TAG + "finish others activity except " + clazz.getSimpleName());
        for (Activity activity : sActivityStack) {
            if (!activity.getClass().equals(clazz)) activity.finish();
        }
        Logger.d(TAG + "activity stack's size is " + sActivityStack.size());
        return this;
    }

    /**
     * 结束所有Activity
     */
    public ActivityHelper finishAll() {
        if (isEmpty(sActivityStack)) return this;
        for (Activity activity : sActivityStack) {
            activity.finish();
        }
        Logger.d(TAG + "finish all activity");
        sActivityStack.clear();
        Logger.d(TAG + "activity stack's size is " + sActivityStack.size());
        return this;
    }

    /**
     * 添加指定的Service
     */
    public ActivityHelper add(Service service) {
        if (sServiceStack == null) throw new NullPointerException("service stack is null");
        Logger.d(TAG + "add service: " + service.getClass().getSimpleName());
        sServiceStack.add(service);
        Logger.d(TAG + "service stack's size is " + sServiceStack.size());
        return this;
    }

    /**
     * 移除指定的Service
     */
    public ActivityHelper remove(Service service) {
        if (isEmpty(sServiceStack)) return this;
        Logger.d(TAG + "remove service: " + service.getClass().getSimpleName());
        sServiceStack.remove(service);
        Logger.d(TAG + "service stack's size is " + sServiceStack.size());
        return this;
    }

    /**
     * 获取指定的Serivce
     */
    public <T> T getService(Class<?> clazz) {
        if (isEmpty(sServiceStack)) return null;
        for (Service service : sServiceStack) {
            if (service.getClass().equals(clazz)) {
                Logger.d(TAG + "get services: " + service.getClass().getSimpleName());
                return (T) service;
            }
        }
        Logger.d(TAG + "service stack's size is " + sServiceStack.size());
        return null;
    }

    /**
     * 获取堆栈中最后一个传入的Service
     */
    public Service getTopService() {
        if (isEmpty(sServiceStack)) return null;
        Logger.d(TAG + "get top service: " + sServiceStack.lastElement().getClass().getSimpleName());
        Logger.d(TAG + "service stack's size is " + sServiceStack.size());
        return sServiceStack.lastElement();
    }

    /**
     * 结束指定的Service
     */
    public ActivityHelper stop(Service service) {
        if (isEmpty(sServiceStack)) return this;
        Logger.d(TAG + "stop services: " + service.getClass().getSimpleName());
        service.stopSelf();
        Logger.d(TAG + "service stack's size is " + sServiceStack.size());
        return this;
    }

    /**
     * 结束指定类名的Service
     */
    public ActivityHelper stop(Class cla) {
        if (isEmpty(sServiceStack)) return this;
        for (Service service : sServiceStack) {
            if (service.getClass().equals(cla)) {
                Logger.d(TAG + "stop services: " + service.getClass().getSimpleName());
                service.stopSelf();
                break;
            }
        }
        Logger.d(TAG + "service stack's size is " + sServiceStack.size());
        return this;
    }

    /**
     * 结束所有Service
     */
    public ActivityHelper stop() {
        if (isEmpty(sServiceStack)) return this;
        for (Service service : sServiceStack) {
            service.stopSelf();
        }
        Logger.d(TAG + "stop all services");
        sServiceStack.clear();
        Logger.d(TAG + "service stack's size is " + sServiceStack.size());
        return this;
    }

    /**
     * 获取所有Service
     */
    public Stack<Service> getService() {
        Logger.d(TAG + "service stack's size is " + sServiceStack.size());
        return sServiceStack;
    }

    /**
     * 退出应用程序
     */
    public void exit() {
        try {
            finishAll();
            stop();
            Logger.d(TAG + "exit and finish all activity with service");
            android.os.Process.killProcess(android.os.Process.myPid());     // 杀死该应用进程
            System.exit(0);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 判断栈是否为空
     *
     * @param stack 任务栈
     * @return 是否为空
     */
    private boolean isEmpty(Stack stack) {
        if (stack == null)
            throw new NullPointerException("stack is null, please initialize before use");
        return stack.isEmpty();
    }
}
