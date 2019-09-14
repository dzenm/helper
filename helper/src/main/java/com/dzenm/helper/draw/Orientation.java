package com.dzenm.helper.draw;

import androidx.annotation.StringDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * @author dzenm
 * @date 2019-08-19 22:06
 */
@StringDef({Orientation.TOP_BOTTOM, Orientation.TR_BL, Orientation.RIGHT_LEFT, Orientation.BR_TL,
        Orientation.BOTTOM_TOP, Orientation.BL_TR, Orientation.LEFT_RIGHT, Orientation.TL_BR})
@Retention(RetentionPolicy.SOURCE)
public @interface Orientation {

    // 从上到下渐变
    String TOP_BOTTOM = "TOP_BOTTOM";

    // 从右上到左下渐变
    String TR_BL = "TR_BL";

    // 从右到左渐变
    String RIGHT_LEFT = "RIGHT_LEFT";

    // 从右下到左上渐变
    String BR_TL = "BR_TL";

    // 从下到上渐变
    String BOTTOM_TOP = "BOTTOM_TOP";

    // 从左下到右上渐变
    String BL_TR = "BL_TR";

    // 从左到右渐变
    String LEFT_RIGHT = "LEFT_RIGHT";

    // 从左上到右下渐变
    String TL_BR = "TL_BR";
}
