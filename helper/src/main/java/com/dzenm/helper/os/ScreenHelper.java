package com.dzenm.helper.os;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Rect;
import android.os.Build;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * @author dinzhenyan
 * @date 2019-04-30 20:03
 * <p>
 * 屏幕获取工具类
 */
public class ScreenHelper {

    /**
     * @return 屏幕宽度
     */
    public static int getWidth() {
        return Resources.getSystem().getDisplayMetrics().widthPixels;
    }

    /**
     * @return 屏幕高度
     */
    public static int getHeight() {
        return Resources.getSystem().getDisplayMetrics().heightPixels;
    }

    /**
     * @return 对角线长度
     */
    public static int getDiagonal() {
        return (int) Math.sqrt(Math.pow(getWidth(), 2) + Math.pow(getHeight(), 2));
    }

    /**
     * @param view
     * @return 任意View屏幕截图
     */
    public static Bitmap snapShotWithStatusBar(View view) {
        view.setDrawingCacheEnabled(true);
        view.buildDrawingCache();
        Bitmap bitmap = Bitmap.createBitmap(view.getDrawingCache(), 0, 0, getWidth(), getHeight());
        view.destroyDrawingCache();
        return bitmap;
    }

    /**
     * @param activity
     * @return 当前屏幕截图，包含状态栏
     */
    public static Bitmap snapShotWithStatusBar(Activity activity) {
        View view = activity.getWindow().getDecorView();
        view.setDrawingCacheEnabled(true);
        view.buildDrawingCache();
        Bitmap bitmap = Bitmap.createBitmap(view.getDrawingCache(), 0, 0, getWidth(), getHeight());
        view.destroyDrawingCache();
        return bitmap;
    }

    /**
     * @param activity
     * @return 当前屏幕截图，不包含状态栏
     */
    public static Bitmap snapShotWithoutStatusBar(Activity activity) {
        View view = activity.getWindow().getDecorView();
        view.setDrawingCacheEnabled(true);
        view.buildDrawingCache();
        Rect frame = new Rect();
        view.getWindowVisibleDisplayFrame(frame);
        int statusBarHeight = frame.top;
        Bitmap bitmap = Bitmap.createBitmap(view.getDrawingCache(), 0, statusBarHeight,
                getWidth(), getHeight() - statusBarHeight);
        view.destroyDrawingCache();
        return bitmap;
    }

    /**
     * 修改状态栏颜色，支持4.4以上版本
     *
     * @param colorId 颜色
     */
    public static void setStatusBarColor(Activity activity, int colorId) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = activity.getWindow();
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(activity.getResources().getColor(colorId));
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            //使用SystemBarTintManager,需要先将状态栏设置为透明
            setTranslucentStatus(activity);
//            SystemBarTintManager systemBarTintManager = new SystemBarTintManager(activity);
//            systemBarTintManager.setStatusBarTintEnabled(true);//显示状态栏
//            systemBarTintManager.setStatusBarTintColor(colorId);//设置状态栏颜色
        }
    }

    /**
     * 设置状态栏透明
     */
    @TargetApi(19)
    public static void setTranslucentStatus(Activity activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            //5.x开始需要把颜色设置透明，否则导航栏会呈现系统默认的浅灰色
            Window window = activity.getWindow();
            View decorView = window.getDecorView();
            //两个 flag 要结合使用，表示让应用的主体内容占用系统状态栏的空间
            int option = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE;
            decorView.setSystemUiVisibility(option);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.TRANSPARENT);
            //导航栏颜色也可以正常设置
            window.setNavigationBarColor(Color.TRANSPARENT);
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Window window = activity.getWindow();
            WindowManager.LayoutParams attributes = window.getAttributes();
            int flagTranslucentStatus = WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS;
            attributes.flags |= flagTranslucentStatus;
            int flagTranslucentNavigation = WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION;
            attributes.flags |= flagTranslucentNavigation;
            window.setAttributes(attributes);
        }
    }


    /**
     * 代码实现android:fitsSystemWindows
     *
     * @param activity
     */
    public static void setRootViewFitsSystemWindows(Activity activity, boolean fitSystemWindows) {
        if (!OsHelper.isLollipop()) return;
        ViewGroup winContent = activity.findViewById(android.R.id.content);
        if (winContent.getChildCount() <= 0) return;
        ViewGroup rootView = (ViewGroup) winContent.getChildAt(0);
        if (rootView != null) rootView.setFitsSystemWindows(fitSystemWindows);
    }

    /**
     * 设置状态栏深色浅色切换
     */
    public static boolean setStatusBarDarkTheme(Activity activity, boolean dark) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            if (OsHelper.isLollipop()) {
                setStatusBarFontIconDark(activity, Rom.ANDROID, dark);
            } else if (OsHelper.isMiui()) {
                setStatusBarFontIconDark(activity, Rom.MIUI, dark);
            } else if (OsHelper.isFlyme()) {
                setStatusBarFontIconDark(activity, Rom.FLYME, dark);
            } else {//其他情况
                return false;
            }
            return true;
        }
        return false;
    }

    /**
     * 设置 状态栏深色浅色切换
     */
    public static boolean setStatusBarFontIconDark(Activity activity, @Rom String type, boolean dark) {
        switch (type) {
            case Rom.MIUI:
                return setMiuiUI(activity, dark);
            case Rom.FLYME:
                return setFlymeUI(activity, dark);
            case Rom.ANDROID:
                return setCommonUI(activity, dark);
        }
        return false;
    }

    //设置6.0 状态栏深色浅色切换
    public static boolean setCommonUI(Activity activity, boolean dark) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            View decorView = activity.getWindow().getDecorView();
            if (decorView != null) {
                int vis = decorView.getSystemUiVisibility();
                if (dark) {
                    vis |= View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR;
                } else {
                    vis &= ~View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR;
                }
                if (decorView.getSystemUiVisibility() != vis) {
                    decorView.setSystemUiVisibility(vis);
                }
                return true;
            }
        }
        return false;
    }

    //设置Flyme 状态栏深色浅色切换
    public static boolean setFlymeUI(Activity activity, boolean dark) {
        try {
            Window window = activity.getWindow();
            WindowManager.LayoutParams lp = window.getAttributes();
            Field darkFlag = WindowManager.LayoutParams.class.getDeclaredField("MEIZU_FLAG_DARK_STATUS_BAR_ICON");
            Field meizuFlags = WindowManager.LayoutParams.class.getDeclaredField("meizuFlags");
            darkFlag.setAccessible(true);
            meizuFlags.setAccessible(true);
            int bit = darkFlag.getInt(null);
            int value = meizuFlags.getInt(lp);
            if (dark) {
                value |= bit;
            } else {
                value &= ~bit;
            }
            meizuFlags.setInt(lp, value);
            window.setAttributes(lp);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    //设置MIUI 状态栏深色浅色切换
    public static boolean setMiuiUI(Activity activity, boolean dark) {
        try {
            Window window = activity.getWindow();
            Class<?> clazz = activity.getWindow().getClass();
            @SuppressLint("PrivateApi") Class<?> layoutParams = Class.forName("android.view.MiuiWindowManager$LayoutParams");
            Field field = layoutParams.getField("EXTRA_FLAG_STATUS_BAR_DARK_MODE");
            int darkModeFlag = field.getInt(layoutParams);
            Method extraFlagField = clazz.getDeclaredMethod("setExtraFlags", int.class, int.class);
            extraFlagField.setAccessible(true);
            if (dark) {    //状态栏亮色且黑色字体
                extraFlagField.invoke(window, darkModeFlag, darkModeFlag);
            } else {
                extraFlagField.invoke(window, 0, darkModeFlag);
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 获取状态栏高度
     *
     * @return
     */
    public static int getStatusBarHeight() {
        int statusHeight = -1;
        int resourceId = Resources.getSystem().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            statusHeight = Resources.getSystem().getDimensionPixelSize(resourceId);
        }
        return statusHeight;
    }

    /**
     * 复制纯文本
     *
     * @param context
     * @param text
     */
    public static void copy(Context context, CharSequence text) {
        // 获取剪切板管理器
        ClipboardManager clipboardManager = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
        // 创建普通字符clipData
        ClipData clipData = ClipData.newPlainText("text/plain", text);
        clipboardManager.setPrimaryClip(clipData);
    }

    /**
     * 粘贴纯文本
     *
     * @param context
     * @return
     */
    public static CharSequence paste(Context context) {
        ClipboardManager clipboardManager = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clipData = clipboardManager.getPrimaryClip();
        // 获取 text
        return clipData == null ? "" : clipData.getItemAt(0).coerceToText(context);

    }

    /**
     * 隐藏Activity的焦点
     *
     * @param activity
     * @return
     */
    public static boolean hideSoftInput(Activity activity) {
        View view = activity.getCurrentFocus();
        if (view == null) return false;
        return ((InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE))
                .hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    /**
     * EditText只能获取焦点，不弹出软键盘
     *
     * @param editText
     */
    public static void getFocusAndHideSoftInput(EditText editText) {
        if (OsHelper.isLollipop()) {
            editText.setShowSoftInputOnFocus(false);
        } else {
            Class<EditText> editClass = EditText.class;
            try {
                Method method = editClass.getMethod("setShowSoftInputOnFocus", boolean.class);
                method.setAccessible(true);
                method.invoke(editText, editClass);
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 显示输入框的软键盘
     *
     * @param editText
     * @return
     */
    public static boolean showSoftInput(EditText editText) {
        return ((InputMethodManager) editText.getContext().getSystemService(
                Context.INPUT_METHOD_SERVICE)).showSoftInput(editText, 0);
    }

    /**
     * 隐藏输入框的软键盘
     *
     * @param editText
     * @return
     */
    public static boolean hideSoftInput(EditText editText) {
        return ((InputMethodManager) editText.getContext().getSystemService(
                Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(editText.getWindowToken(), 0);
    }

    /**
     * 利用反射修改EditText光标样式
     *
     * @param editText 需要设置的EditText
     * @param drawable 不能直接用new出来的对象, 要使用/res/drawable/的资源id
     */
    public static void setCursorDrawable(EditText editText, int drawable) {
        try {
            Field f = TextView.class.getDeclaredField("mCursorDrawableRes");
            f.setAccessible(true);
            f.set(editText, drawable);
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    /**
     * 将color resId转化为16进制的color string
     *
     * @param id color资源Id
     * @return 16进制color string
     */
    public static String getHexColor(Context context, int id) {
        StringBuilder sb = new StringBuilder();
        int color = context.getResources().getColor(id);
        sb.append("#");
        sb.append(Integer.toHexString(Color.alpha(color)));
        sb.append(Integer.toHexString(Color.red(color)));
        sb.append(Integer.toHexString(Color.green(color)));
        sb.append(Integer.toHexString(Color.blue(color)));
        return sb.toString();
    }

    /**
     * 获取资源Id的带透明度的color
     *
     * @param id
     * @param value
     * @return
     */
    public static int getAlphaHexColor(Context context, int id, int value) {
        String color = getHexColor(context, id);
        int index = 1;
        if (color.length() == 9) index = 3;
        int r = Integer.parseInt(color.substring(index, index + 2), 16);
        int g = Integer.parseInt(color.substring(index + 2, index + 4), 16);
        int b = Integer.parseInt(color.substring(index + 4, index + 6), 16);
        return Color.argb(value, r, g, b);
    }

    /**
     * 获取textView一行最大能显示几个字
     *
     * @param text
     * @param textPaint
     * @param maxWidth
     * @return
     */
    public static int getLineMaxNumber(String text, TextPaint textPaint, int maxWidth) {
        if (null == text || "".equals(text)) return 0;
        StaticLayout sl = new StaticLayout(text, textPaint, maxWidth, Layout.Alignment.ALIGN_NORMAL,
                1.0f, 0, false);
        // 获取第一行最后显示的字符下标
        return sl.getLineEnd(0);
    }

    /**
     * dp转px
     *
     * @param value
     * @return
     */
    public static int dp2px(float value) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, value, Resources.getSystem().getDisplayMetrics());
    }

}