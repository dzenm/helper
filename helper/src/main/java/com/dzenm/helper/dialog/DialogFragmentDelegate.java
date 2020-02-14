package com.dzenm.helper.dialog;

import android.app.Dialog;
import android.graphics.drawable.Drawable;
import android.view.Gravity;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDialogFragment;
import androidx.fragment.app.DialogFragment;

import com.dzenm.helper.R;
import com.dzenm.helper.animator.AnimatorHelper;
import com.dzenm.helper.draw.DrawableHelper;
import com.dzenm.helper.os.OsHelper;
import com.dzenm.helper.os.ScreenHelper;

/**
 * @author dzenm
 * @date 2020-02-14 14:10
 */
public class DialogFragmentDelegate {

    protected static final float DEFAULT_RADIUS = 16f;
    protected static final float MATERIAL_RADIUS = 2f;

    protected AppCompatActivity mActivity;
    private DialogFragment mDialogFragment;

    /**
     * 根布局, 用于设置dialog颜色和圆角大小, 长宽, 获取View的ID
     */
    protected View mView, mContentView;

    /**
     * dialog背景, 默认白色背景和圆角, 通过 {@link #setBackground(Drawable)} 设置背景
     */
    protected Drawable mBackground = DrawableHelper
            .solid(android.R.color.white)
            .radius(DEFAULT_RADIUS)
            .build();

    /**
     * dialog四周的margin, 默认值为10, 通过 {@link #setMargin(int)} 设置margin
     */
    protected int mMargin = OsHelper.dp2px(10);

    /**
     * dialog居中时的宽度, 默认宽度为(屏幕宽度 - 10 * {@link #mMargin})
     */
    protected int mCenterWidth = ScreenHelper.getDisplayWidth() - 10 * mMargin;

    /**
     * dialog显示的位置，默认显示在中间, 通过  {@link #setGravity(int)} 设置显示的位置
     */
    protected int mGravity = Gravity.CENTER;

    /**
     * dialog动画, 默认根据 {@link #mGravity} 的位置显示动画
     * 当 {@link #mGravity} 的值为 {@link Gravity.TOP} 从顶部往下弹出
     * 当 {@link #mGravity} 的值为 {@link Gravity.BOTTOM} 从底部往上弹出
     * 当 {@link #mGravity} 的值为 {@link Gravity.CENTER} 从中间缩放显示
     * 通过 {@link #setAnimator(int)} 设置动画
     */
    protected int mAnimator = AnimatorHelper.expand();

    /**
     * 主要颜色, 除了灰色和白色之外的颜色, 默认为蓝色为主色
     */
    protected int mPrimaryColor = R.color.colorDarkBlue;

    /**
     * 次要颜色, 除了灰色和白色之外的颜色, 默认为添加一定透明度的蓝色为次色
     */
    protected int mSecondaryColor = R.color.colorTranslucentDarkBlue;

    /**
     * dialog的遮罩透明度, 通过 {@link #setDimAccount(float)} 设置遮罩透明度
     * 0f 为全透明, 1f为不透明
     */
    protected float mDimAccount = -1f;

    /**
     * 触摸dialog外部关闭dialog, 通过 {@link #setTouchInOutSideCancel(boolean)} 设置是否关闭
     */
    private boolean isTouchInOutSideCancel = false;

    /**
     * 显示的主要文字颜色 {@link #setDefaultTextColor()}, 显示的次要文字颜色 {@link #setDefaultTextColor()}
     */
    protected int mPrimaryTextColor, mSecondaryTextColor;

    /**
     * 按钮文本颜色
     */
    protected int mButtonTextColor;

    /**
     * 提示文本颜色 {@link #setDefaultTextColor()}
     */
    protected int mHintColor;

    /**
     * 分割线颜色 {@link #setDefaultTextColor()}
     */
    protected int mDivideColor;

    /**
     * 按压文本颜色 {@link #setDefaultTextColor()}
     */
    protected int mPressedColor;

    /**
     * 圆角大小, 默认值 {@link #DEFAULT_RADIUS}
     */
    protected float mBackgroundRadius = DEFAULT_RADIUS;

    protected Dialog mDialog;

    /**
     * 是否在View之间添加分割线 {@link #setDivide(boolean)}
     */
    protected boolean isDivide = false;

    protected boolean isMaterialDesign = false;

    protected boolean isDefaultBackground = true, isDefaultGravity = true,
            isDefaultMargin = true, isDefaultAnimator = true;

    protected OnClickListener mOnClickListener;

    public DialogFragmentDelegate(DialogFragment dialogFragment, AppCompatActivity activity) {
        mDialogFragment = dialogFragment;
        mActivity = activity;
    }

    void onCreate() {
        // 必须在onCreate方法设置才有效
        mDialogFragment.setStyle(AppCompatDialogFragment.STYLE_NO_TITLE,
                isFullScreen() ? R.style.FullScreenTheme : R.style.AbsFragmentTheme);
    }

    boolean isFullScreen() {
        return false;
    }

    private interface OnDialogClickListener<T extends BaseDialogFragment> {

        /**
         * @param dialog  当前显示的Dialog
         * @param confirm 是否是确定按钮，通过这个判断点击的是哪个按钮
         * @return 返回true表示，点击之后会dismiss dialog， 返回false不dismiss dialog
         */
        boolean onClick(T dialog, boolean confirm);
    }

    public abstract static class OnClickListener<T extends BaseDialogFragment> implements OnDialogClickListener<T> {

        @Override
        public boolean onClick(T dialog, boolean confirm) {
            return true;
        }
    }

}
