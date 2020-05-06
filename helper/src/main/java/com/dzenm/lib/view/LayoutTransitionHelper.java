package com.dzenm.lib.view;

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

        // APPEARING                元素在容器中显现时需要动画显示。
        // CHANGE_APPEARING         由于容器中要显现一个新的元素，其它元素的变化需要动画显示。
        // DISAPPEARING             元素在容器中消失时需要动画显示。
        // CHANGE_DISAPPEARING      由于容器中某个元素要消失，其它元素的变化需要动画显示。
        transition.setStagger(LayoutTransition.CHANGE_APPEARING, 200);
        transition.setStagger(LayoutTransition.CHANGE_DISAPPEARING, 200);
        transition.setDuration(LayoutTransition.APPEARING, 200);
        transition.setDuration(LayoutTransition.DISAPPEARING, 200);

        transition.setAnimator(LayoutTransition.APPEARING, getScaleAnimator(view, 0f, 1f));
        // 源码中带有默认300毫秒的延时，需要移除，不然view添加效果不好！！
        transition.setStartDelay(LayoutTransition.APPEARING, 0);

        transition.setAnimator(LayoutTransition.DISAPPEARING, getScaleAnimator(view, 1f, 0f));

        transition.setAnimator(LayoutTransition.CHANGE_APPEARING, getAlphaAnimator(null, 0f, 1f));
        transition.setAnimator(LayoutTransition.CHANGE_DISAPPEARING, getAlphaAnimator(null, 1f, 0f));
        transition.enableTransitionType(LayoutTransition.CHANGE_DISAPPEARING);
        // 源码中带有默认300毫秒的延时，需要移除，不然view添加效果不好！！
        transition.setStartDelay(LayoutTransition.CHANGE_DISAPPEARING, 0);
        return transition;
    }

    public static ObjectAnimator getScaleAnimator(View view, float start, float end) {
        PropertyValuesHolder appearingScaleX = PropertyValuesHolder.ofFloat("scaleX", start, end);
        PropertyValuesHolder appearingScaleY = PropertyValuesHolder.ofFloat("scaleY", start, end);
        return ObjectAnimator.ofPropertyValuesHolder(view, appearingScaleX, appearingScaleY);
    }

    public static ObjectAnimator getAlphaAnimator(View view, float start, float end) {
        return ObjectAnimator.ofFloat(view, "alpha", start, end);
    }
}
