package com.dzenm.helper.draw;

import android.graphics.drawable.Drawable;
import android.view.View;

/**
 * @author dzenm
 * @date 2019-07-20 11:18
 */
interface IDrawable<T extends Drawable, V extends View> {

    /**
     * @param view 需要设置背景的View
     */
    void into(V view);

    /**
     * @return 创建一个新的Drawable
     */
    T build();
}
