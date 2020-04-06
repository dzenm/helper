package com.dzenm.helper.os;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;

import androidx.annotation.ColorInt;
import androidx.annotation.ColorRes;
import androidx.annotation.IntRange;
import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;

import com.dzenm.helper.R;

/**
 * @author dinzhenyan
 * @date 2019-04-30 20:03
 * <p>
 * 状态栏工具类
 */
public class StatusBarHelper {

    private static final int DEFAULT_STATUS_BAR_ALPHA = 112;
    private static final int FAKE_STATUS_BAR_VIEW_ID = R.id.fake_status_bar_view_id;

    /**
     * 设置状态栏和Toolbar颜色
     *
     * @param activity 需要设置的activity
     * @param view     需要设置的Toolbar
     * @param color    状态栏和Toolbar颜色值
     */
    public static void setStatusBarWithToolbarStyle(Activity activity, @NonNull View view, @ColorRes int color) {
        setColor(activity, color);
        adjustViewHeightForHideStatusBar(activity, view);
        view.setBackgroundColor(getColor(activity, color));
    }

    /**
     * 设置Fragment中的Toolbar状态栏和Toolbar的颜色
     *
     * @param activity 需要设置的activity
     * @param toolbar  需要设置的Toolbar
     * @param color    需要设置的颜色
     */
    public static void setFragmentToolbarColor(Activity activity, @NonNull Toolbar toolbar, int color) {
        adjustToolbarHeightForHideStatusBar(activity, toolbar);
        toolbar.setBackgroundColor(getColor(activity, color));
        setColor(activity, color);
    }

    /**
     * 设置状态栏纯色, 不加半透明效果
     *
     * @param activity 需要设置的activity
     * @param color    状态栏颜色值
     */
    public static void setColor(Activity activity, @ColorRes int color) {
        setTranslucentColor(activity, color, 0);
    }

    /**
     * 设置状态栏颜色
     *
     * @param activity       需要设置的activity
     * @param color          状态栏颜色值
     * @param statusBarAlpha 状态栏透明度
     */
    public static void setTranslucentColor(@NonNull Activity activity, @ColorRes int color,
                                           @IntRange(from = 0, to = 255) int statusBarAlpha) {
        int calculateColor = calculateColorByAlpha(getColor(activity, color), statusBarAlpha);
        setStatusBarColor(activity, getColor(activity, android.R.color.transparent));

        setRootViewFitsSystemWindows(activity);

        ViewGroup decorView = (ViewGroup) activity.getWindow().getDecorView();
        getStatusBarView(activity, decorView).setBackgroundColor(calculateColor);
    }

    /**
     * 设置Root View延伸到状态栏
     *
     * @param activity 需要设置的activity
     */
    private static void setRootViewFitsSystemWindows(@NonNull Activity activity) {
        View content = activity.findViewById(android.R.id.content);
        content.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
    }

    /**
     * 设置状态栏 Drawable,比如渐变色
     *
     * @param activity 需要设置的activity
     * @param drawable 需要设置的drawable
     */
    public static void setDrawable(Activity activity, Drawable drawable) {
        setStatusBarColor(activity, getColor(activity, android.R.color.transparent));
        ViewGroup decorView = (ViewGroup) activity.getWindow().getDecorView();
        getStatusBarView(activity, decorView).setBackground(drawable);
    }

    /**
     * 设置DrawLayout为根布局时的状态栏颜色, 去除状态栏半透明阴影
     *
     * @param activity 需要设置的activity
     * @param drawer   需要设置的DrawLayout
     * @param color    需要设置的颜色
     */
    public static void setDrawLayoutColor(Activity activity, @NonNull DrawerLayout drawer, int color) {
        setDrawLayoutColor(activity, drawer, color, false);
    }

    /**
     * 设置DrawLayout为根布局时的状态栏颜色
     *
     * @param activity      需要设置的activity
     * @param drawer        需要设置的DrawLayout
     * @param color         需要设置的颜色
     * @param isTranslucent 是否设置状态栏半透明的灰色阴影
     */
    public static void setDrawLayoutColor(Activity activity, @NonNull DrawerLayout drawer,
                                          int color, boolean isTranslucent) {
        setStatusBarColor(activity, getColor(activity, android.R.color.transparent));
        // 创建一个和StatusBar高度相同的View, 然后设置背景颜色
        ViewGroup decorViewLayout = (ViewGroup) drawer.getChildAt(0);
        View contentView = getStatusBarView(activity, decorViewLayout);
        contentView.setBackgroundColor(getColor(activity, color));
        if (!isTranslucent) contentView.setFitsSystemWindows(true);

        setRootViewFitsSystemWindows(activity);

        // 为DrawerLayout设置高度和StatusBar相同的PaddingTop
        if (!(decorViewLayout instanceof LinearLayout) && decorViewLayout.getChildAt(1) != null) {
            decorViewLayout.getChildAt(1).setPadding(decorViewLayout.getPaddingLeft(),
                    decorViewLayout.getPaddingTop() + getStatusBarHeight(activity),
                    decorViewLayout.getPaddingRight(), decorViewLayout.getPaddingBottom());
        }
    }

    /**
     * 获取和StatusBar高度相同的View
     *
     * @param activity  需要设置的activity
     * @param decorView 这个View的ViewGroup
     * @return 状态栏View
     */
    private static View getStatusBarView(Activity activity, @NonNull ViewGroup decorView) {
        View fakeStatusBarView = decorView.findViewById(FAKE_STATUS_BAR_VIEW_ID);
        if (fakeStatusBarView == null) {
            // decorView 中添加一个与状态栏大小的 view
            fakeStatusBarView = createStatusBarView(activity);
            decorView.addView(fakeStatusBarView, 0);
        }
        return fakeStatusBarView;
    }

    /**
     * 创建一个和状态栏大小相同的矩形条, 并设置背景为 drawable
     *
     * @param activity 需要设置的activity
     * @return 状态栏View
     */
    private static View createStatusBarView(Activity activity) {
        View statusBarView = new View(activity);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, getStatusBarHeight(activity));
        statusBarView.setLayoutParams(params);
        statusBarView.setTag(FAKE_STATUS_BAR_VIEW_ID);
        statusBarView.setId(FAKE_STATUS_BAR_VIEW_ID);
        return statusBarView;
    }

    /**
     * 为滑动返回界面设置状态栏颜色
     *
     * @param activity 需要设置的activity
     * @param color    状态栏颜色值
     */
    public static void setColorForSwipeBack(Activity activity, int color) {
        setColorForSwipeBack(activity, color, DEFAULT_STATUS_BAR_ALPHA);
    }

    /**
     * 为滑动返回界面设置状态栏颜色
     *
     * @param activity       需要设置的activity
     * @param color          状态栏颜色值
     * @param statusBarAlpha 状态栏透明度
     */
    public static void setColorForSwipeBack(Activity activity, @ColorInt int color,
                                            @IntRange(from = 0, to = 255) int statusBarAlpha) {
        if (!OsHelper.isLollipop()) return;
        ViewGroup contentView = activity.findViewById(android.R.id.content);
        View rootView = contentView.getChildAt(0);
        int statusBarHeight = getStatusBarHeight(activity);
//        if (rootView instanceof CoordinatorLayout) {
//            CoordinatorLayout coordinatorLayout = (CoordinatorLayout) rootView;
//            coordinatorLayout.setStatusBarBackgroundColor(calculateColorByAlpha(color, statusBarAlpha));
//        } else {
//        }
        contentView.setPadding(0, statusBarHeight, 0, 0);
        contentView.setBackgroundColor(calculateColorByAlpha(color, statusBarAlpha));
        setColor(activity, android.R.color.transparent);
    }

    /**
     * 设置StatusBar文本样式
     *
     * @param activity 上下文
     * @param dark     是否设置为暗色, false为白色, true为黑色
     */
    public static void setStatusBarTextStyle(Activity activity, boolean dark) {
        if (OsHelper.isMarshmallow()) {
            activity.getWindow().getDecorView().setSystemUiVisibility(dark ?
                    View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR :
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
        }
    }

    /**
     * 设置status bar颜色
     *
     * @param activity 需要设置的Activity
     * @param color    设置的颜色
     */
    public static void setStatusBarColor(@NonNull Activity activity, int color) {
        Window window = activity.getWindow();
        if (OsHelper.isLollipop()) {
            // 添加状态栏背景可绘制模式
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            // 清除原有的状态栏半透明状态
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(color);
        } else {
            window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }
    }

    /**
     * 获取状态栏高度
     *
     * @param context context
     * @return 状态栏高度
     */
    public static int getStatusBarHeight(@NonNull Context context) {
        // 获得状态栏高度
        return context.getResources()
                .getDimensionPixelSize(
                        context.getResources()
                                .getIdentifier("status_bar_height", "dimen", "android"));
    }

    /**
     * 获取ActionBar高度
     *
     * @param context 上下文
     * @return ActionBar高度
     */
    public static int getActionBarHeight(@NonNull Context context) {
        TypedValue t = new TypedValue();
        if (context.getTheme().resolveAttribute(android.R.attr.actionBarSize, t, true)) {
            return TypedValue.complexToDimensionPixelSize(t.data, context.getResources().getDisplayMetrics());
        }
        return -1;
    }

    /**
     * 设置View的margin高度
     *
     * @param activity 上下文
     * @param view     需要设置的View
     */
    public static void adjustViewHeightForHideStatusBar(Activity activity, @NonNull View view) {
        ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) view.getLayoutParams();
        params.topMargin += getStatusBarHeight(activity);
        view.setLayoutParams(params);
    }

    /**
     * 设置Toolbar的高度, 添加一个高度和StatusBar高度一样的padding top
     *
     * @param activity 上下文
     * @param view     需要设置的View
     */
    public static void adjustToolbarHeightForHideStatusBar(Activity activity, @NonNull View view) {
        ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) view.getLayoutParams();
        params.height = getStatusBarHeight(activity) + getActionBarHeight(activity);
        view.setLayoutParams(params);
        view.setPadding(view.getPaddingLeft(), view.getPaddingTop() + getStatusBarHeight(activity),
                view.getPaddingRight(), view.getPaddingBottom());
    }

    /**
     * 计算状态栏颜色
     *
     * @param color color值
     * @param alpha alpha值
     * @return 最终的状态栏颜色
     */
    public static int calculateColorByAlpha(@ColorInt int color, int alpha) {
        if (alpha == 0) return color;
        float a = 1 - alpha / 255f;
        int red = color >> 16 & 0xff;
        int green = color >> 8 & 0xff;
        int blue = color & 0xff;
        red = (int) (red * a + 0.5);
        green = (int) (green * a + 0.5);
        blue = (int) (blue * a + 0.5);
        return 0xff << 24 | red << 16 | green << 8 | blue;
    }

    private static int getColor(@NonNull Activity activity, @ColorRes int color) {
        return OsHelper.getColor(activity, color);
    }
}
