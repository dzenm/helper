package com.dzenm.helper.os;

import android.content.Context;

import com.dzenm.helper.R;
import com.dzenm.helper.file.SPHelper;

/**
 * @author dzenm
 * @date 2020-01-16 22:28
 */
public class ThemeHelper {

    private static final String THEME_PREF = "theme_pref";
    private static final String THEME_TYPE = "theme_type";

    public static class ThemeColors {
        public static final int THEME_LIGHT = 1;
        public static final int THEME_DARK = 2;
    }

    public static void setTheme(Context context) {
        int themeType = (int) SPHelper.getInstance().get(THEME_PREF, THEME_TYPE, ThemeColors.THEME_LIGHT);
        int themeId;
        switch (themeType) {
            case ThemeColors.THEME_LIGHT:
                themeId = R.style.AppTheme_Light;
                break;
            case ThemeColors.THEME_DARK:
                themeId = R.style.AppTheme_Dark;
                break;
            default:
                themeId = R.style.AppTheme_Default;
                break;
        }
        context.setTheme(themeId);
    }

    public static boolean setNewTheme(Context context, int theme) {
        return SPHelper.getInstance().put(THEME_PREF, THEME_TYPE, theme);
    }
}
