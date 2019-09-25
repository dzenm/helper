package com.dzenm.helper.os;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import androidx.annotation.ColorInt;
import androidx.annotation.ColorRes;
import androidx.annotation.DrawableRes;
import androidx.annotation.IntRange;
import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.drawerlayout.widget.DrawerLayout;

import com.dzenm.helper.R;

/**
 * Created by Jaeger on 16/2/14.
 * <p>
 * Email: chjie.jaeger@gmail.com
 * GitHub: https://github.com/laobie
 */
public class StatusBarHelper {

    private static final int DEFAULT_STATUS_BAR_ALPHA = 112;
    private static final int FAKE_STATUS_BAR_VIEW_ID = R.id.fake_status_bar_view_id;

    /**
     * 设置状态栏纯色 不加半透明效果
     *
     * @param activity 需要设置的 activity
     * @param color    状态栏颜色值
     */
    public static void setColor(Activity activity, @ColorRes int color) {
        setTranslucentColor(activity, false, color, 0);
    }

    /**
     * 设置状态栏纯色 不加半透明效果
     *
     * @param activity 需要设置的 activity
     * @param color    状态栏颜色值
     */
    public static void setColor(Activity activity, boolean hideStatusBar, @ColorRes int color) {
        setTranslucentColor(activity, hideStatusBar, color, 0);
    }

    /**
     * 设置状态栏颜色
     *
     * @param activity 需要设置的 activity
     * @param color    状态栏颜色值
     */
    public static void setTranslucentColor(Activity activity, @ColorRes int color) {
        setTranslucentColor(activity, false, color, DEFAULT_STATUS_BAR_ALPHA);
    }

    /**
     * 设置状态栏颜色
     *
     * @param activity 需要设置的 activity
     * @param color    状态栏颜色值
     */
    public static void setTranslucentColor(Activity activity, boolean hideStatusBar, @ColorRes int color) {
        setTranslucentColor(activity, hideStatusBar, color, DEFAULT_STATUS_BAR_ALPHA);
    }

    /**
     * 设置状态栏颜色
     *
     * @param activity       需要设置的activity
     * @param color          状态栏颜色值
     * @param statusBarAlpha 状态栏透明度
     */
    public static void setTranslucentColor(@NonNull Activity activity, boolean hideStatusBar,
                                           @ColorRes int color, @IntRange(from = 0, to = 255) int statusBarAlpha) {
        int calculateColor = calculateColorByAlpha(activity.getResources().getColor(color), statusBarAlpha);
        if (hideStatusBar) {
            setStatusBarColor(activity, true, calculateColor);
        } else {
            setStatusBarColor(activity, false,
                    activity.getResources().getColor(android.R.color.transparent));
            ViewGroup decorView = (ViewGroup) activity.getWindow().getDecorView();
            getStatusBarView(activity, decorView).setBackgroundColor(calculateColor);
        }
    }

    /**
     * 设置状态栏 Drawable,比如渐变色
     *
     * @param activity 需要设置的activity
     * @param resId    资源Id
     */
    public static void setDrawable(Activity activity, @DrawableRes int resId) {
        setDrawable(activity, activity.getResources().getDrawable(resId));
    }

    /**
     * 设置状态栏 Drawable,比如渐变色
     *
     * @param activity 需要设置的activity
     * @param drawable 需要设置的drawable
     */
    public static void setDrawable(Activity activity, Drawable drawable) {
        setStatusBarColor(activity, false, activity.getResources().getColor(android.R.color.transparent));
        ViewGroup decorView = (ViewGroup) activity.getWindow().getDecorView();
        getStatusBarView(activity, decorView).setBackground(drawable);
    }

    /**
     * 设置DrawLayout为根布局时的状态栏颜色
     *
     * @param activity 需要设置的activity
     * @param drawer   需要设置的DrawLayout
     * @param color    需要设置的颜色
     */
    public static void setDrawLayoutColor(Activity activity, DrawerLayout drawer, int color) {
        setStatusBarColor(activity, true,
                activity.getResources().getColor(android.R.color.transparent));
        // 创建一个和StatusBar高度相同的View, 然后设置背景颜色
        ViewGroup decorViewLayout = (ViewGroup) drawer.getChildAt(0);
        View contentView = getStatusBarView(activity, decorViewLayout);
        contentView.setBackgroundColor(color);
        // 为DrawerLayout设置高度和StatusBar相同的PaddingTop
        if (!(decorViewLayout instanceof LinearLayout) && decorViewLayout.getChildAt(1) != null) {
            decorViewLayout.getChildAt(1).setPadding(decorViewLayout.getPaddingLeft(),
                    decorViewLayout.getPaddingTop() + getStatusBarHeight(activity),
                    decorViewLayout.getPaddingRight(), decorViewLayout.getPaddingBottom());
        }
        drawer.setFitsSystemWindows(false);
    }

    /**
     * 获取和StatusBar高度相同的View
     *
     * @param activity  需要设置的activity
     * @param decorView 这个View的ViewGroup
     * @return 状态栏View
     */
    private static View getStatusBarView(Activity activity, ViewGroup decorView) {
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
        if (rootView instanceof CoordinatorLayout) {
            CoordinatorLayout coordinatorLayout = (CoordinatorLayout) rootView;
            coordinatorLayout.setStatusBarBackgroundColor(calculateColorByAlpha(color, statusBarAlpha));
        } else {
            contentView.setPadding(0, statusBarHeight, 0, 0);
            contentView.setBackgroundColor(calculateColorByAlpha(color, statusBarAlpha));
        }
        setColor(activity, false, android.R.color.transparent);
    }

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
    public static void setStatusBarColor(@NonNull Activity activity, boolean hideStatusBar, int color) {
        Window window = activity.getWindow();
        if (OsHelper.isLollipop()) {
            // 添加状态栏背景可绘制模式
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            // 清除原有的状态栏半透明状态
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            if (hideStatusBar) {
                // 状态栏不可见, 清除状态栏高度
                window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
            }
            window.setStatusBarColor(color);
            window.setNavigationBarColor(activity.getResources().getColor(android.R.color.transparent));
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
     * 设置Toolbar高度
     *
     * @param toolbar 需要设置的Toolbar
     * @param height  需要设置的高度
     */
    public static void setToolbarHeight(@NonNull Toolbar toolbar, int height) {
        ViewGroup.LayoutParams params = toolbar.getLayoutParams();
        params.height = height;
        toolbar.setLayoutParams(params);
    }

    public static void adjustToolbarForHideStatusBar(Context context, @NonNull Toolbar toolbar) {
        ViewGroup.LayoutParams params = toolbar.getLayoutParams();
        int height = getStatusBarHeight(context);
        if (params instanceof LinearLayout.LayoutParams) {
            LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) params;
            layoutParams.topMargin = height;
            toolbar.setLayoutParams(layoutParams);
        } else if (params instanceof RelativeLayout.LayoutParams) {
            RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) params;
            layoutParams.topMargin = height;
            toolbar.setLayoutParams(layoutParams);
        } else if (params instanceof CoordinatorLayout.LayoutParams) {
            CoordinatorLayout.LayoutParams layoutParams = (CoordinatorLayout.LayoutParams) params;
            layoutParams.topMargin = height;
            toolbar.setLayoutParams(layoutParams);
        } else if (params instanceof FrameLayout.LayoutParams) {
            FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) params;
            layoutParams.topMargin = height;
            toolbar.setLayoutParams(layoutParams);
        } else if (params instanceof DrawerLayout.LayoutParams) {
            DrawerLayout.LayoutParams layoutParams = (DrawerLayout.LayoutParams) params;
            layoutParams.topMargin = height;
            toolbar.setLayoutParams(layoutParams);
        }
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
}
