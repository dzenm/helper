package com.dzenm.helper.draw;

import androidx.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * @author dzenm
 * @date 2019-08-19 22:00
 */
@IntDef({Shape.RECTANGLE, Shape.OVAL})
@Retention(RetentionPolicy.SOURCE)
@interface Shape {

    // 样式-矩形
    int RECTANGLE = 0;

    // 样式-圆形
    int OVAL = 1;
}
