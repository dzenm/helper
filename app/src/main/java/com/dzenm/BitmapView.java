package com.dzenm;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;

/**
 * @author dzenm
 * @date 2019-09-23 08:58
 */
public class BitmapView extends View {

    public BitmapView(Context context) {
        this(context, null);
    }

    public BitmapView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public BitmapView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }
}
