package com.dzenm.helper.draw;

import androidx.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * @author dzenm
 * @date 2019-08-19 22:10
 */
@IntDef({DrawableStyle.GRADIENT, DrawableStyle.STATE_LIST, DrawableStyle.RIPPLE,})
@Retention(RetentionPolicy.SOURCE)
@interface DrawableStyle {

    /**
     * null
     */
    int NONE = 0;

    /**
     * {@link android.graphics.drawable.GradientDrawable}
     */
    int GRADIENT = 1;

    /**
     * {@link android.graphics.drawable.StateListDrawable}
     */
    int STATE_LIST = 2;

    /**
     * {@link android.graphics.drawable.RippleDrawable}
     */
    int RIPPLE = 3;
}
