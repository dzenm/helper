package com.dzenm.helper.draw;

import android.content.Context;
import android.graphics.drawable.Drawable;
import androidx.annotation.ColorRes;

/**
 * @author dzenm
 * @date 2019-07-19 17:01
 * <pre>
 * 点击产生交互的背景
 * BackGHelper.pressed(R.color.colorLightGray, new float[]{0, 0, 8, 8}).into(tvPositive);
 * 给etMessage设置一个透明圆角带边框的背景
 * BackGHelper.solid(android.R.color.transparent)
 *            .radius(2)
 *            .stroke(0.5f, divideColor)
 *            .into(etMessage);
 * </pre>
 */
public class BackGHelper {

    private BackGHelper() {
    }

    public static ShapeHelper init(Context context) {
        return ShapeHelper.getInstance().init(context);
    }

    /**
     * 设置形状
     *
     * @param shape 可选项为圆形和矩形
     * @return ShapeHelper
     */
    public static ShapeHelper shape(@Shape int shape) {
        return ShapeHelper.getInstance().shape(shape);
    }

    /**
     * 设置宽度和高度
     *
     * @param width  宽度
     * @param height 高度
     * @return ShapeHelper
     */
    public static ShapeHelper size(int width, int height) {
        return ShapeHelper.getInstance().size(width, height);
    }

    /**
     * 设置宽度
     *
     * @param width 宽度
     * @return ShapeHelper
     */
    public static ShapeHelper width(int width) {
        return ShapeHelper.getInstance().width(width);
    }

    /**
     * 设置高度
     *
     * @param height 高度
     * @return ShapeHelper
     */
    public static ShapeHelper height(int height) {
        return ShapeHelper.getInstance().height(height);
    }

    /**
     * 设置颜色
     *
     * @param colorRedId 颜色值, res/color 文件下的资源id
     * @return ShapeHelper
     */
    public static ShapeHelper solid(@ColorRes int colorRedId) {
        return ShapeHelper.getInstance().solid(colorRedId);
    }

    /**
     * 设置形状
     *
     * @return ShapeHelper
     */
    public static ShapeHelper solid(String color) {
        return ShapeHelper.getInstance().solid(color);
    }

    /**
     * 圆角弧度
     *
     * @param radius 圆角值(包含四个角)
     * @return ShapeHelper
     */
    public static ShapeHelper radius(float radius) {
        return ShapeHelper.getInstance().radius(radius);
    }

    /**
     * 圆角弧度
     *
     * @param tl 上左圆角弧度
     * @return ShapeHelper
     */
    public static ShapeHelper radiusTL(float tl) {
        return ShapeHelper.getInstance().radiusTL(tl);
    }

    /**
     * 圆角弧度
     *
     * @param tr 上右圆角弧度
     * @return ShapeHelper
     */
    public static ShapeHelper radiusTR(float tr) {
        return ShapeHelper.getInstance().radiusTR(tr);
    }

    /**
     * 圆角弧度
     *
     * @param br 下右圆角弧度
     * @return ShapeHelper
     */
    public static ShapeHelper radiusBR(float br) {
        return ShapeHelper.getInstance().radiusBR(br);
    }

    /**
     * 圆角弧度
     *
     * @param bl 下左圆角弧度
     * @return ShapeHelper
     */
    public static ShapeHelper radiusBL(float bl) {
        return ShapeHelper.getInstance().radiusBL(bl);
    }

    /**
     * 圆角弧度
     *
     * @param tl 上左圆角弧度
     * @param tr 右上圆角弧度
     * @param br 下右圆角弧度
     * @param bl 下左圆角弧度
     * @return ShapeHelper
     */
    public static ShapeHelper radius(float tl, float tr, float br, float bl) {
        return ShapeHelper.getInstance().radius(tl, tr, br, bl);
    }

    /**
     * 圆角弧度
     *
     * @param radius 圆角弧度值(float数组大小为四个值，上左，上右，下右，下左)
     * @return ShapeHelper
     */
    public static ShapeHelper radius(float[] radius) {
        return ShapeHelper.getInstance().radius(radius);
    }

    /**
     * 边框大小
     *
     * @param stroke 边框值
     * @param color  边框颜色
     * @return ShapeHelper
     */
    public static ShapeHelper stroke(int stroke, int color) {
        return ShapeHelper.getInstance().stroke(stroke, color);
    }

    /**
     * 内边距大小
     *
     * @param padding 内边距值(包含四边)
     * @return ShapeHelper
     */
    public static ShapeHelper bound(int padding) {
        return ShapeHelper.getInstance().bound(padding);
    }

    /**
     * 内边距大小
     *
     * @param l 左边
     * @param t 上边
     * @param r 右边
     * @param b 下边
     * @return ShapeHelper
     */
    public static ShapeHelper bound(int l, int t, int r, int b) {
        return ShapeHelper.getInstance().bound(l, t, r, b);
    }

    /**
     * 内边距大小
     *
     * @param padding 内边距值(int数组大小为四个值，左，上，右，下)
     * @return ShapeHelper
     */
    public static ShapeHelper bound(int[] padding) {
        return ShapeHelper.getInstance().bound(padding);
    }

    /**
     * 线性渐变方向
     *
     * @param orientation 线性渐变方向
     * @return ShapeHelper
     */
    public static ShapeHelper orientation(@Orientation String orientation) {
        return ShapeHelper.getInstance().orientation(orientation);
    }

    /**
     * 辐射渐变半径
     *
     * @param gradientRadius 渐变半径
     * @return ShapeHelper
     */
    public static ShapeHelper radialRadius(int gradientRadius) {
        return ShapeHelper.getInstance().radialRadius(gradientRadius);
    }

    /**
     * 渐变颜色
     * 线性类型: 线性渐变(GradientDrawable.LINEAR_GRADIENT)
     *
     * @param colorsResId 渐变颜色组, 必须大于2个颜色
     * @return ShapeHelper
     */
    public static ShapeHelper gradient(@ColorRes int... colorsResId) {
        return ShapeHelper.getInstance().gradient(colorsResId);
    }

    /**
     * 渐变颜色
     * 线性类型: 线性渐变(GradientDrawable.LINEAR_GRADIENT)
     *
     * @param colors 渐变颜色组, 必须大于2个颜色
     * @return ShapeHelper
     */
    public static ShapeHelper gradient(String... colors) {
        return ShapeHelper.getInstance().gradient(colors);
    }

    /**
     * Selector按压设置
     *
     * @param pressedColor 按压后的背景颜色
     * @return ShapeHelper
     */
    public static ShapeHelper pressed(String pressedColor) {
        return ShapeHelper.getInstance().pressed(pressedColor);
    }

    /**
     * Selector按压设置
     *
     * @param pressedColor 按压后的背景颜色
     * @return ShapeHelper
     */
    public static ShapeHelper pressed(@ColorRes int pressedColor) {
        return ShapeHelper.getInstance().pressed(pressedColor);
    }

    /**
     * Selector按压设置
     *
     * @param normalColor  正常背景颜色
     * @param pressedColor 按压后的背景颜色
     * @return ShapeHelper
     */
    public static ShapeHelper pressed(String normalColor, String pressedColor) {
        return ShapeHelper.getInstance().pressed(normalColor, pressedColor);
    }

    /**
     * Selector按压设置
     *
     * @param normalColor  正常背景颜色
     * @param pressedColor 按压后的背景颜色
     * @return ShapeHelper
     */
    public static ShapeHelper pressed(@ColorRes int normalColor, @ColorRes int pressedColor) {
        return ShapeHelper.getInstance().pressed(normalColor, pressedColor);
    }

    /**
     * Selector按压设置
     *
     * @param normalDrawable  正常的背景
     * @param pressedDrawable 按压后的背景
     * @return ShapeHelper
     */
    public static ShapeHelper pressed(Drawable normalDrawable, Drawable pressedDrawable) {
        return ShapeHelper.getInstance().pressed(normalDrawable, pressedDrawable);
    }

    /**
     * Selector状态设置
     *
     * @param state           Selector状态
     * @param normalDrawable  正常的背景
     * @param pressedDrawable 按压后的背景
     * @return ShapeHelper
     */
    public static ShapeHelper state(@SelectorState int state, Drawable normalDrawable, Drawable pressedDrawable) {
        return ShapeHelper.getInstance().state(state, normalDrawable, pressedDrawable);
    }

    /**
     * 按压后的文本颜色
     *
     * @param normalColorResId   正常的文本颜色
     * @param selectorColorResId 按压后的文本颜色
     * @return ShapeHelper
     */
    public static ShapeHelper textColor(@ColorRes int normalColorResId, @ColorRes int selectorColorResId) {
        return ShapeHelper.getInstance().textColor(normalColorResId, selectorColorResId);
    }

    /**
     * 按压后的文本颜色
     *
     * @param normalColor   正常的文本颜色
     * @param selectorColor 按压后的文本颜色
     * @return ShapeHelper
     */
    public static ShapeHelper textColor(String normalColor, String selectorColor) {
        return ShapeHelper.getInstance().textColor(normalColor, selectorColor);
    }

    /**
     * 波纹点击效果, 白色背景, 按压波纹效果为淡灰色
     *
     * @return ShapeHelper
     */
    public static ShapeHelper ripple() {
        return ShapeHelper.getInstance().ripple();
    }

    /**
     * 波纹点击效果, 按压波纹效果为淡灰色
     *
     * @param normalColor 正常显示View的颜色
     * @return ShapeHelper
     */
    public static ShapeHelper ripple(@ColorRes int normalColor) {
        return ShapeHelper.getInstance().ripple(normalColor);
    }

    /**
     * 波纹点击效果, 按压波纹效果为淡灰色
     *
     * @param normalColor 正常显示View的颜色
     * @return ShapeHelper
     */
    public static ShapeHelper ripple(String normalColor) {
        return ShapeHelper.getInstance().ripple(normalColor);
    }

    /**
     * 波纹点击效果
     *
     * @param normalColor  正常显示View的颜色
     * @param pressedColor 点击View显示的波纹的颜色
     * @return ShapeHelper
     */
    public static ShapeHelper ripple(String normalColor, String pressedColor) {
        return ShapeHelper.getInstance().ripple(normalColor, pressedColor);
    }

    /**
     * 波纹点击效果
     *
     * @param normalColor  正常显示View的颜色
     * @param pressedColor 点击View显示的波纹的颜色
     * @return ShapeHelper
     */
    public static ShapeHelper ripple(@ColorRes int normalColor, @ColorRes int pressedColor) {
        return ShapeHelper.getInstance().ripple(normalColor, pressedColor);
    }
}
