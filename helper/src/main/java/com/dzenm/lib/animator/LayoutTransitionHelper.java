package com.dzenm.lib.animator;

import android.animation.LayoutTransition;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.view.View;

/**
 * @author dzenm
 * @date 2019-09-21 10:02
 */
public class LayoutTransitionHelper {

    public static LayoutTransition scaleViewAnimator(View view) {
        LayoutTransition transition = new LayoutTransition();

        // CHANGE_APPEARING         addView时, 其它View在ViewGroup中先执行开始动画, 为需要添加的View腾出空间
        transition.setDuration(LayoutTransition.CHANGE_APPEARING, 300);
        transition.setAnimator(LayoutTransition.CHANGE_APPEARING, getAlphaAnimator(null, 0f, 1f));

        // APPEARING                addView时, 在执行View添加的动画
        transition.setDuration(LayoutTransition.APPEARING, 300);
        transition.setAnimator(LayoutTransition.APPEARING, getScaleAnimator(view, 0f, 1f));
        // 源码中带有默认300毫秒的延时，需要移除，不然view添加效果不好
        transition.setStartDelay(LayoutTransition.APPEARING, 0);

        // DISAPPEARING             removeView时, 先执行View移除的动画, 为需要移除的View收回空间
        transition.setDuration(LayoutTransition.DISAPPEARING, 300);
        transition.setAnimator(LayoutTransition.DISAPPEARING, getScaleAnimator(view, 1f, 0f));

        // CHANGE_APPEARING         removeView时, 其它View在ViewGroup中继续执行移除动画。
        transition.setDuration(LayoutTransition.CHANGE_DISAPPEARING, 300);
        transition.setAnimator(LayoutTransition.CHANGE_DISAPPEARING, getAlphaAnimator(null, 1f, 0f));
        // 源码中带有默认300毫秒的延时，需要移除，不然view添加效果不好！！
        transition.setStartDelay(LayoutTransition.CHANGE_DISAPPEARING, 0);

        transition.enableTransitionType(LayoutTransition.APPEARING);
        return transition;
    }

    public static ObjectAnimator getScaleAnimator(View view, float start, float end) {
        PropertyValuesHolder appearingScaleX = PropertyValuesHolder.ofFloat("scaleX", start, end);
        PropertyValuesHolder appearingScaleY = PropertyValuesHolder.ofFloat("scaleY", start, end);
        ObjectAnimator animator = ObjectAnimator.ofPropertyValuesHolder(view, appearingScaleX, appearingScaleY);
        animator.setDuration(400);
        return animator;
    }

    public static ObjectAnimator getAlphaAnimator(View view, float start, float end) {
        ObjectAnimator animator = ObjectAnimator.ofFloat(view, "alpha", start, end);
        animator.setDuration(200);
        return animator;
    }
}
