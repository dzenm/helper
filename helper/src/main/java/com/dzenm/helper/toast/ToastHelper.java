package com.dzenm.helper.toast;

import android.graphics.drawable.Drawable;

/**
 * @author dinzhenyan
 * @date 2019-06-07 10:46
 * <pre>
 * 在Application里初始化
 * ToastHelper.getInstance(this);
 *
 * 弹出一个文本Toast
 * ToastHelper.show("自定义Toast");
 *
 * 弹一个带图标和背景颜色的Toast
 * ToastHelper.setBackground(GradientHelper.get(getResources()
 *                     .getColor(android.R.color.holo_blue_bright), 12))
 *                     .show("带图标的toast", R.drawable.prompt_success);
 * </pre>
 */
public class ToastHelper {

    public static ToastPrompt getInstance() {
        return ToastPrompt.getInstance();
    }

    /**
     * 为单个Toast显示设置属性时, 需要先调用该方法
     */
    public static ToastPrompt customize() {
        return getInstance().customize();
    }

    /**
     * @param background Toast背景drawable资源文件
     */
    public ToastPrompt setBackground(Drawable background) {
        return getInstance().setBackground(background);
    }

    /**
     * @param gravity 显示的位置
     * @param offset  垂直方向偏移量
     */
    public ToastPrompt setGravity(int gravity, int offset) {
        return getInstance().setGravity(gravity, offset);
    }

    /**
     * @param repeat true显示多次， false显示一次
     */
    public static ToastPrompt isRepeat(boolean repeat) {
        return getInstance().isRepeat(repeat);
    }

    /**
     * @param resId 显示的文本
     */
    public static void show(int resId) {
        getInstance().show(resId);
    }

    /**
     * @param text 显示的文本
     */
    public static void show(CharSequence text) {
        getInstance().show(text);
    }

    /**
     * @param text  显示的文本
     * @param resId 显示的图片
     */
    public static void show(CharSequence text, int resId) {
        getInstance().show(text, resId);
    }

    /**
     * @param text     显示的文本
     * @param resId    显示的图片
     * @param duration 显示的时间
     */
    public static void show(CharSequence text, int resId, int duration) {
        getInstance().show(text, resId, duration);
    }

    /**
     * @param text      显示的文本内容
     * @param resId     显示的图片资源
     * @param duration  显示的时间
     * @param showImage 是否显示图片
     */
    public static void show(CharSequence text, int resId, int duration, @ToastPrompt.Type int showImage) {
        getInstance().show(text, resId, duration, showImage);
    }
}
