package com.dzenm.helper.os;

import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Rect;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;

import com.dzenm.helper.R;

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
     * 设置toolbar,并在左上角添加动画横线按钮
     *
     * @param activity     {@link Activity}
     * @param drawerLayout {@link DrawerLayout} 布局
     * @param toolbar      设置的Toolbar
     */
    public void addDrawerLayoutToggle(Activity activity, DrawerLayout drawerLayout, Toolbar toolbar) {
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(activity, drawerLayout, toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
    }

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
     * @return 屏幕宽度, 不包含NavigatorBar
     */
    public static int getDisplayWidth() {
        return Resources.getSystem().getDisplayMetrics().widthPixels;
    }

    /**
     * @return 屏幕宽度, 不包含NavigatorBar
     */
    public static int getDisplayHeight() {
        return Resources.getSystem().getDisplayMetrics().heightPixels;
    }

    /**
     * @return 屏幕宽度, 包含NavigatorBar
     */
    public static int getDisplayWidth(Activity activity) {
        return getDisplayMetrics(activity).widthPixels;
    }

    /**
     * @return 屏幕高度, 包含NavigatorBar
     */
    public static int getDisplayHeight(Activity activity) {
        return getDisplayMetrics(activity).heightPixels;
    }

    /**
     * 获取屏幕指标工具
     *
     * @param activity 获取WindowManager
     * @return 屏幕指标
     */
    public static DisplayMetrics getDisplayMetrics(Activity activity) {
        DisplayMetrics metric = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getRealMetrics(metric);
        return metric;
    }

    /**
     * @return 对角线长度
     */
    public static int getDiagonal() {
        return (int) Math.sqrt(Math.pow(getWidth(), 2) + Math.pow(getHeight(), 2));
    }

    /**
     * @param view 需要截图的view
     * @return 任意View屏幕截图
     */
    public static Bitmap snapShotWithStatusBar(View view) {
        setDrawingCacheView(view);
        Bitmap bitmap = Bitmap.createBitmap(view.getDrawingCache(), 0, 0,
                view.getWidth(), view.getHeight());
        view.destroyDrawingCache();
        return bitmap;
    }

    /**
     * @param activity 需要截图的Activity
     * @return 当前屏幕截图，包含状态栏
     */
    public static Bitmap snapShotWithStatusBar(Activity activity) {
        View view = activity.getWindow().getDecorView();
        setDrawingCacheView(view);
        Bitmap bitmap = Bitmap.createBitmap(view.getDrawingCache(), 0, 0, getWidth(), getHeight());
        view.destroyDrawingCache();
        return bitmap;
    }

    /**
     * @param activity 需要截图的Activity
     * @return 当前屏幕截图，不包含状态栏
     */
    public static Bitmap snapShotWithoutStatusBar(Activity activity) {
        View view = activity.getWindow().getDecorView();
        setDrawingCacheView(view);
        Rect frame = new Rect();
        view.getWindowVisibleDisplayFrame(frame);
        int statusBarHeight = frame.top;
        Bitmap bitmap = Bitmap.createBitmap(view.getDrawingCache(), 0, statusBarHeight,
                getWidth(), getHeight() - statusBarHeight);
        view.destroyDrawingCache();
        return bitmap;
    }

    private static void setDrawingCacheView(View view) {
        view.setDrawingCacheEnabled(true);
        view.buildDrawingCache();
    }

    /**
     * 复制纯文本
     *
     * @param context 获取系统服务的上下文
     * @param text    复制的文本
     */
    public static void copy(Context context, CharSequence text) {
        // 获取剪切板管理器
        ClipboardManager clipboardManager = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
        // 创建普通字符clipData
        ClipData clipData = ClipData.newPlainText("text/plain", text);
        if (clipboardManager != null) {
            clipboardManager.setPrimaryClip(clipData);
        }
    }

    /**
     * 粘贴纯文本
     *
     * @param context 获取系统服务的上下文
     * @return 文本内容
     */
    public static CharSequence paste(Context context) {
        ClipboardManager clipboardManager = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
        if (clipboardManager == null) return "";
        ClipData clipData = clipboardManager.getPrimaryClip();
        // 获取 text
        return clipData == null ? "" : clipData.getItemAt(0).coerceToText(context);

    }

    /**
     * 隐藏Activity的焦点
     *
     * @param activity 获取系统服务的上下文
     * @return 是否隐藏成功
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
     * @param editText 需要设置的EditText
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
            } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 显示输入框的软键盘
     *
     * @param editText 需要设置的EditText
     * @return 是否显示成功
     */
    public static boolean showSoftInput(EditText editText) {
        return ((InputMethodManager) editText.getContext().getSystemService(
                Context.INPUT_METHOD_SERVICE)).showSoftInput(editText, 0);
    }

    /**
     * 隐藏输入框的软键盘
     *
     * @param editText 需要设置的EditText
     * @return 是否隐藏成功
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
        int color = OsHelper.getColor(context, id);
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
     * @param context
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
     * @param text      textView的文本内容
     * @param textPaint 截断字符
     * @param maxWidth  最大的宽度
     * @return 一行最大能显示几个字
     */
    public static int getLineMaxNumber(String text, TextPaint textPaint, int maxWidth) {
        if (null == text || "".equals(text)) return 0;
        StaticLayout sl = new StaticLayout(text, textPaint, maxWidth, Layout.Alignment.ALIGN_NORMAL,
                1.0f, 0, false);
        // 获取第一行最后显示的字符下标
        return sl.getLineEnd(0);
    }

}