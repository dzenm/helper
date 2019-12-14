package com.dzenm.helper.animator;

import android.graphics.drawable.Animatable;
import android.graphics.drawable.Drawable;

import com.dzenm.helper.R;

/**
 * @author dinzhenyan
 * @date 2019-04-30 20:03
 * <p>
 * 不同方向上的进入与退出的动画
 */
public final class AnimatorHelper {

    /**
     * 放大
     *
     * @return res/values/style
     */
    public static int expand() {
        return R.style.BaseDialog_Scale_Expand_Animator;
    }

    /**
     * 缩小
     *
     * @return res/values/style
     */
    public static int shrink() {
        return R.style.BaseDialog_Scale_Shrink_Animator;
    }

    /**
     * 下进下出
     *
     * @return res/values/style
     */
    public static int bottom() {
        return R.style.BaseDialog_Bottom_Animator;
    }

    /**
     * 下进上出
     *
     * @return res/values/style
     */
    public static int bottom2Top() {
        return R.style.BaseDialog_Bottom_Top_Animator;
    }

    /**
     * 上进上出
     *
     * @return res/values/style
     */
    public static int top() {
        return R.style.BaseDialog_Top_Animator;
    }

    /**
     * 上进下出
     *
     * @return res/values/style
     */
    public static int top2Bottom() {
        return R.style.BaseDialog_Top_Bottom_Animator;
    }

    /**
     * 左进左出
     *
     * @return res/values/style
     */
    public static int left() {
        return R.style.BaseDialog_Left_Animator;
    }

    /**
     * 右进右出
     *
     * @return res/values/style
     */
    public static int right() {
        return R.style.BaseDialog_Right_Animator;
    }

    /**
     * 左进右出
     *
     * @return res/values/style
     */
    public static int left2Right() {
        return R.style.BaseDialog_Left_Right_Animator;
    }

    /**
     * 右进左出
     *
     * @return res/values/style
     */
    public static int right2Left() {
        return R.style.BaseDialog_Right_Left_Animator;
    }

    /**
     * 下弹出上弹出
     *
     * @return res/values/style
     */
    public static int overshoot() {
        return R.style.BaseDialog_Overshoot_Animator;
    }

    /**
     * 透明度变化
     *
     * @return res/values/style
     */
    public static int alpha() {
        return R.style.BaseDialog_Alpha_Animator;
    }

    /**
     * 回弹效果
     *
     * @return res/values/style
     */
    public static int rebound() {
        return R.style.BaseDialog_Rebound_Animator;
    }

    public static void play(Drawable drawable) {
        if ((drawable instanceof Animatable)) ((Animatable) drawable).start();
    }

}
