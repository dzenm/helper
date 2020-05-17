package com.dzenm.lib.os;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.TypedValue;

import androidx.annotation.AttrRes;
import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.content.res.AppCompatResources;

import com.dzenm.lib.R;
import com.dzenm.lib.file.SPHelper;

/**
 * @author dzenm
 * @date 2020-01-16 22:28
 */
public class ThemeHelper {

    private static final String THEME_PREF = "theme_pref";
    private static final String THEME_TYPE = "theme_type";
    private static final String THEME_MODE = "theme_mode";

    public static void setLocalTheme(@NonNull Activity activity, int theme) {
        setTheme(activity, theme);
        // 重启Activity
//        activity.recreate();
        Intent intent = new Intent(activity, activity.getClass());
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        activity.startActivity(intent);
    }

    public static void setTheme(@NonNull Activity activity, int theme) {
        saveNewTheme(theme);
        activity.setTheme(theme);
    }

    public static void saveNewTheme(int theme) {
        SPHelper.getInstance().put(THEME_PREF, THEME_TYPE, theme);
    }

    public static void saveLocalNightMode(int theme) {
        SPHelper.getInstance().put(THEME_PREF, THEME_MODE, theme);
    }

    public static int getLocalTheme() {
        return (int) SPHelper.getInstance().get(THEME_PREF, THEME_TYPE, R.style.AppTheme_Light);
    }

    public static int getLocalNightMode() {
        return (int) SPHelper.getInstance().get(THEME_PREF, THEME_MODE,
                AppCompatDelegate.MODE_NIGHT_UNSPECIFIED);
    }

    /**
     * 获取 attr/color 下文件的颜色值
     *
     * @param color 颜色值
     * @return 是否通过Resource文件获取的颜色值
     */
    public static int getColor(@NonNull Context context, int color) {
        try {
            return context.getResources().getColor(color);
        } catch (Exception e) {
            if (color < 0) {
                return color;
            } else {
                return resolveColor(context, color);
            }
        }
    }

    public static int resolveColor(@NonNull Context context, int attrRes) {
        TypedArray a = context.obtainStyledAttributes(new int[]{attrRes});
        try {
            return a.getColor(0, 0);
        } finally {
            a.recycle();
        }
    }

    public static int resolveInt(@NonNull Context context, int attrRes) {
        TypedArray a = context.obtainStyledAttributes(new int[]{attrRes});
        try {
            return a.getInt(0, 0);
        } finally {
            a.recycle();
        }
    }

    public static float resolveFloat(@NonNull Context context, int attrRes) {
        TypedArray a = context.obtainStyledAttributes(new int[]{attrRes});
        try {
            return a.getFloat(0, 0);
        } finally {
            a.recycle();
        }
    }

    public static String resolveString(@NonNull Context context, int attrRes) {
        TypedArray a = context.obtainStyledAttributes(new int[]{attrRes});
        try {
            return a.getString(0);
        } finally {
            a.recycle();
        }
    }

    public static int resolveDimension(@NonNull Context context, int attrRes) {
        TypedArray a = context.obtainStyledAttributes(new int[]{attrRes});
        try {
            return a.getDimensionPixelSize(0, 0);
        } finally {
            a.recycle();
        }
    }

    public static boolean resolveBoolean(@NonNull Context context, int attrRes) {
        TypedArray a = context.obtainStyledAttributes(new int[]{attrRes});
        try {
            return a.getBoolean(0, false);
        } finally {
            a.recycle();
        }
    }

    public static Drawable resolveDrawable(@NonNull Context context, @AttrRes int attrRes) {
        TypedArray a = context.obtainStyledAttributes(new int[]{attrRes});
        try {
            Drawable drawable = null;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                drawable = a.getDrawable(0);
            } else {
                int id = a.getResourceId(0, -1);
                if (id == -1) {
                    drawable = AppCompatResources.getDrawable(context, id);
                }
            }
            return drawable;
        } finally {
            a.recycle();
        }
    }

    public static ColorStateList getColorStateList(@NonNull Context context, int color) {
        TypedArray a = context.obtainStyledAttributes(new int[]{color});
        try {
            final TypedValue value = new TypedValue();
            context.getResources().getValue(color, value, true);
            if (value.type >= TypedValue.TYPE_FIRST_COLOR_INT
                    && value.type <= TypedValue.TYPE_LAST_COLOR_INT) {
                return getActionTextStateList(context, value.data);
            } else {
                if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.LOLLIPOP_MR1) {
                    return context.getResources().getColorStateList(color, null);
                } else {
                    return context.getColorStateList(color);
                }
            }
        } catch (Exception e) {
            int[][] states = new int[][]{
                    new int[]{android.R.attr.state_enabled}, new int[]{}
            };
            int[] colors = new int[]{color, color};
            return new ColorStateList(states, colors);
        } finally {
            a.recycle();
        }
    }

    public static ColorStateList resolveColorStateList(@NonNull Context context, @AttrRes int attrRes) {
        TypedArray a = context.getTheme().obtainStyledAttributes(new int[]{attrRes});
        try {

            final TypedValue value = a.peekValue(0);
            if (value.type >= TypedValue.TYPE_FIRST_COLOR_INT
                    && value.type <= TypedValue.TYPE_LAST_COLOR_INT) {
                return getActionTextStateList(context, value.data);
            } else {
                return a.getColorStateList(0);
            }
        } finally {
            a.recycle();
        }
    }

    public static ColorStateList getActionTextStateList(@NonNull Context context, int newPrimaryColor) {
        final int fallBackButtonColor = resolveColor(context, android.R.attr.textColorPrimary);
        if (newPrimaryColor == 0) {
            newPrimaryColor = fallBackButtonColor;
        }
        int[][] states = new int[][]{
                new int[]{android.R.attr.state_enabled}, new int[]{}
        };

        int[] colors = new int[]{adjustAlpha(newPrimaryColor, 0.4f), newPrimaryColor};
        return new ColorStateList(states, colors);
    }

    public static int adjustAlpha(@ColorInt int color, float factor) {
        int alpha = Math.round(Color.alpha(color) * factor);
        int red = Color.red(color);
        int green = Color.green(color);
        int blue = Color.blue(color);
        return Color.argb(alpha, red, green, blue);
    }
}
