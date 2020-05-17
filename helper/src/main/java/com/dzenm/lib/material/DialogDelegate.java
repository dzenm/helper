package com.dzenm.lib.material;

import android.app.Activity;
import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDialogFragment;

import com.dzenm.lib.R;
import com.dzenm.lib.animator.AnimatorHelper;
import com.dzenm.lib.drawable.DrawableHelper;
import com.dzenm.lib.os.ScreenHelper;
import com.dzenm.lib.os.ThemeHelper;

/**
 * @author dzenm
 * @date 2020-02-14 14:10
 */
public class DialogDelegate {

    public static final float IOS_WIDTH = 0.7f;
    public static final float MATERIAL_WIDTH = 0.8f;

    AbsDialogFragment mDialogFragment;
    AppCompatActivity mActivity;
    int mThemeId;

    /**
     * 根布局, 用于设置dialog背景和圆角大小, 长宽, 获取View的ID
     */
    LinearLayout mDecorView;

    /**
     * 当前Fragment的dialog
     */
    Dialog mDialog;

    /**
     * dialog上下左右margin, 在{@link Gravity#TOP}, {@link Gravity#BOTTOM}显示默认值为10,
     * 如果显示在{@link Gravity#CENTER}, {@link #isMaterialDesign} 为true时, 宽度为屏幕宽的的
     * {@link #MATERIAL_WIDTH}, 否则宽度为屏幕宽的的{@link #IOS_WIDTH}
     * 通过 {@link MaterialDialog.Builder#setMargin(int)} 重新设置
     */
    int mMargin;

    /**
     * dialog显示的位置，默认显示在中间, @see {@link Gravity}, 通过
     * {@link MaterialDialog.Builder#setGravity(int)} 重新设置
     */
    int mGravity;

    /**
     * dialog动画, 默认根据 {@link #mGravity} 的位置显示动画
     * 当 {@link #mGravity} 的值为 {@link Gravity#TOP } 从顶部往下弹出
     * 当 {@link #mGravity} 的值为 {@link Gravity#BOTTOM } 从底部往上弹出
     * 当 {@link #mGravity} 的值为 {@link Gravity#CENTER } 从中间缩放显示
     * 通过 {@link MaterialDialog.Builder#setAnimator(int)} 重新设置
     */
    int mAnimator;

    /**
     * dialog显示的背景颜色, 在白色主题时默认为白色, 在暗色主题时默认为灰色, 通过
     * {@link MaterialDialog.Builder#setBackgroundColor(int)} 重新设置
     */
    int mBackgroundColor;

    /**
     * 圆角大小, @see {@link #mBackgroundRadius}
     */
    float mBackgroundRadius;

    /**
     * 圆角大小, 分别为tl, tr, bt, bl, 如果 {@link #isMaterialDesign} 为true, {@link #mBackgroundRadiusII}值为
     * {@link MaterialDialog#MATERIAL_RADIUS}, 如果 {@link #isMaterialDesign} 为 false,
     * {@link #mBackgroundRadiusII}值为 {@link MaterialDialog#DEFAULT_RADIUS}, 通过
     * {@link MaterialDialog.Builder#setBackgroundRadius(float)} 或
     * {@link MaterialDialog.Builder#setBackgroundRadius(float[])}} 重新设置
     */
    float[] mBackgroundRadiusII;

    /**
     * dialog居中时的宽度, 默认宽度为(屏幕宽度 - 10 * {@link #mMargin})
     */
    int mWidthInCenter;

    /**
     * dialog的遮罩透明度, 0f为全透明, 1f为不透明, 通过 {@link MaterialDialog.Builder#setDimAccount(float)} 重新设置
     */
    float mDimAccount;

    /**
     * 触摸dialog外部关闭dialog, 通过 {@link MaterialDialog.Builder#setTouchInOutSideCancel(boolean)} 重新设置
     */
    boolean isTouchInOutSideCancel;

    /**
     * 是否可以取消Dialog
     */
    boolean isCancelable;

    /**
     * 主要颜色, 除了灰色和白色之外的颜色, 默认为蓝色为主色
     * 次要颜色, 除了灰色和白色之外的颜色, 默认为添加一定透明度的蓝色为次色
     */
    int mPrimaryColor, mSecondaryColor;

    /**
     * 显示的主要文字颜色, 显示的次要文字颜色
     */
    int mPrimaryTextColor, mSecondaryTextColor;

    /**
     * 按钮文本颜色
     */
    int mButtonTextColor;

    /**
     * 提示文本颜色, 分割线颜色, 点击按钮颜色, 未点击按钮颜色
     */
    int mHintColor, mDivideColor, mActiveColor, mInactiveColor;

    /**
     * 是否设置为Material样式, 默认为true
     */
    boolean isMaterialDesign;

    /**
     * 是否全屏显示
     */
    boolean isFullScreen = false;

    /************************************* 以下为实现过程 *********************************/

    DialogDelegate(AbsDialogFragment dialogFragment, AppCompatActivity activity) {
        mDialogFragment = dialogFragment;
        mActivity = activity;

        mPrimaryColor = getColor(R.attr.dialogPrimaryColor);
        mSecondaryColor = getColor(R.attr.dialogSecondaryColor);
    }

    /**
     * 设置Dialog主题
     */
    void onCreate() {
        // 必须在onCreate方法设置才有效
        mDialogFragment.setStyle(AppCompatDialogFragment.STYLE_NO_TITLE, mThemeId == 0
                ? isFullScreen ? R.style.FullScreenTheme : R.style.AbsFragmentTheme
                : mThemeId
        );
    }

    @Nullable
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mDialog = getDialog();
        if (mDialog == null) {
            return null;
        }

        mDecorView = createDecorView(mActivity);

        // 初始化默认的颜色
        mPrimaryTextColor = getColor(R.attr.dialogPrimaryTextColor);
        mSecondaryTextColor = getColor(R.attr.dialogSecondaryColorText);
        mButtonTextColor = getColor(R.attr.dialogButtonTextColor);
        mHintColor = getColor(R.attr.dialogHintTextColor);
        mDivideColor = getColor(R.attr.dialogDivideColor);

        mActiveColor = getColor(R.attr.dialogActiveColor);
        mInactiveColor = getColor(R.attr.dialogInactiveColor);

        mDialogFragment.initView();
        return mDecorView;
    }

    /**
     * 创建Decorview
     *
     * @param activity 上下文
     * @return 创建之后的DecorView
     */
    private LinearLayout createDecorView(Activity activity) {
        LinearLayout decorView = new LinearLayout(activity);
        decorView.setLayoutParams(new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.WRAP_CONTENT
        ));
        decorView.setOrientation(LinearLayout.VERTICAL);
        return decorView;
    }

    Dialog getDialog() {
        return mDialogFragment.getDialog();
    }

    Window getWindow() {
        Window window = getDialog().getWindow();
        if (window == null) {
            return mActivity.getWindow();
        }
        return window;
    }

    /**
     * 解决Dialog内存泄漏
     *
     * @param view
     * @param savedInstanceState
     */
    void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        if (mDialog != null) {
            try {
                mDialog.setOnShowListener(null);
                mDialog.setOnDismissListener(null);
                mDialog.setOnCancelListener(null);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 初始化dialog的大小
     *
     * @param decorView 设置dialog的layout params
     */
    void setDialogMargin(View decorView) {
        ViewGroup.MarginLayoutParams layoutParams = (ViewGroup.MarginLayoutParams) decorView.getLayoutParams();
        mDialogFragment.onDialogLayoutParams(layoutParams, mMargin);
        decorView.setLayoutParams(layoutParams);
    }

    void onDialogAnimator() {
        if (mAnimator == AnimatorHelper.expand()) {
            if (mGravity == Gravity.TOP) {
                mAnimator = AnimatorHelper.top();
            } else if (mGravity == Gravity.BOTTOM) {
                mAnimator = AnimatorHelper.bottom();
            }
        }
    }

    /**
     * @param layoutParams 通过decorView的LayoutParams设定layout params
     * @param margin       当前设定的margin值
     */
    void onDialogLayoutParams(
            ViewGroup.MarginLayoutParams layoutParams,
            int margin
    ) {
        if (isFullScreen) {
            layoutParams.width = ScreenHelper.getDisplayWidth();
        } else {
            if (isShowDialogInCenter()) {
                layoutParams.width = mWidthInCenter;
            } else {
                layoutParams.topMargin = 2 * margin;
                layoutParams.bottomMargin = margin;
                layoutParams.width = ScreenHelper.getDisplayWidth() - 2 * margin;
            }
        }
    }

    /**
     * 设置Windows的属性
     */
    void apply(Window window) {
        // dialog背景
        mDecorView.setBackground(DrawableHelper.solid(mBackgroundColor)
                .radius(mBackgroundRadiusII)
                .build()
        );

        // 设置是否可以通过点击dialog之外的区域取消显示dialog
        mDialog.setCanceledOnTouchOutside(isTouchInOutSideCancel);
        mDialog.setCancelable(isCancelable);

        // 设置dialog显示的位置
        WindowManager.LayoutParams attributes = window.getAttributes();
        mDialogFragment.onDialogGravity(attributes);
        window.setAttributes(attributes);

        // 将背景设为透明
        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        // dialog动画
        window.setWindowAnimations(mAnimator);

        // 解决AlertDialog无法弹出软键盘,且必须放在AlertDialog的show方法之后
        window.clearFlags(
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE |
                        WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM
        );
        // 收起键盘
        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        if (isFullScreen) {
            window.setLayout(
                    WindowManager.LayoutParams.MATCH_PARENT,
                    ScreenHelper.getDisplayHeight(mActivity)
            );
        }
        // 消除Dialog内容区域外围的灰色
        if (mDimAccount != -1f) window.setDimAmount(mDimAccount);
    }

    /**
     * @param layoutParams 通过WindowManager的layoutParams设置显示位置
     */
    void onDialogGravity(WindowManager.LayoutParams layoutParams) {
        layoutParams.gravity = mGravity;
    }

    /**
     * @return 是否显示在中间
     */
    boolean isShowDialogInCenter() {
        return mGravity == Gravity.CENTER;
    }

    /**
     * @param attrId 颜色id(位于 res/values/colors.xml)
     * @return 颜色值
     */
    int getColor(int attrId) {
        return ThemeHelper.getColor(mActivity, attrId);
    }

}
