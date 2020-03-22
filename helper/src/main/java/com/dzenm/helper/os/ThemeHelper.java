package com.dzenm.helper.os;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;

import androidx.annotation.ColorInt;

import com.dzenm.helper.R;
import com.dzenm.helper.file.SPHelper;

/**
 * @author dzenm
 * @date 2020-01-16 22:28
 */
public class ThemeHelper {

    private static final String THEME_PREF = "theme_pref";
    private static final String THEME_TYPE = "theme_type";

    public static void setTheme(Context context, int theme) {
        SPHelper.getInstance().put(THEME_PREF, THEME_TYPE, theme);
        context.setTheme(theme);
    }

    public static int getTheme() {
        return (int) SPHelper.getInstance().get(THEME_PREF, THEME_TYPE, R.style.AppTheme_Dark);
    }

    public static @ColorInt
    int getColor(Context context, int resId) {
        @SuppressLint("Recycle") TypedArray a =
                context.obtainStyledAttributes(new int[]{resId});
        return a.getColor(0, 0);
    }
}
