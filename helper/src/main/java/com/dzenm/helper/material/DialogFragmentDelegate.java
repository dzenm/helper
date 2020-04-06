package com.dzenm.helper.material;

import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

import androidx.annotation.IdRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDialogFragment;

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

    public static final float DEFAULT_RADIUS = 16f;
    public static final float MATERIAL_RADIUS = 2f;

    private AppCompatActivity mActivity;
    private BaseDialogFragment mDialogFragment;

    /**
     * 根布局, 用于设置dialog颜色和圆角大小, 长宽, 获取View的ID
     */
    private View mView, mContentView;

    /**
     * dialog背景, 默认白色背景和圆角, 通过 {@link #setBackground(Drawable)} 设置背景
     */
    private Drawable mBackground;

    /**
     * dialog四周的margin, 默认值为10, 通过 {@link #setMargin(int)} 设置margin
     */
    private int mMargin;

    /**
     * dialog居中时的宽度, 默认宽度为(屏幕宽度 - 10 * {@link #mMargin})
     */
    private int mCenterWidth;

    /**
     * dialog显示的位置，默认显示在中间, 通过  {@link #setGravity(int)} 设置显示的位置
     */
    private int mGravity;

    /**
     * dialog动画, 默认根据 {@link #mGravity} 的位置显示动画
     * 当 {@link #mGravity} 的值为 TOP 从顶部往下弹出
     * 当 {@link #mGravity} 的值为 BOTTOM 从底部往上弹出
     * 当 {@link #mGravity} 的值为 CENTER 从中间缩放显示
     * 通过 {@link #setAnimator(int)} 设置动画
     */
    private int mAnimator;

    /**
     * 主要颜色, 除了灰色和白色之外的颜色, 默认为蓝色为主色
     */
    private int mPrimaryColor;

    /**
     * 次要颜色, 除了灰色和白色之外的颜色, 默认为添加一定透明度的蓝色为次色
     */
    private int mSecondaryColor;

    /**
     * dialog的遮罩透明度, 通过 {@link #setDimAccount(float)} 设置遮罩透明度
     * 0f 为全透明, 1f为不透明
     */
    private float mDimAccount;

    /**
     * 触摸dialog外部关闭dialog, 通过 {@link #setTouchInOutSideCancel(boolean)} 设置是否关闭
     */
    private boolean isTouchInOutSideCancel;

    /**
     * 显示的主要文字颜色, 显示的次要文字颜色
     */
    private int mPrimaryTextColor, mSecondaryTextColor;

    /**
     * 按钮文本颜色
     */
    private int mButtonTextColor;

    /**
     * 提示文本颜色, 分割线颜色, 按压文本颜色
     */
    private int mHintColor, mDivideColor, mPressedColor;

    /**
     * 圆角大小, 默认值 {@link #DEFAULT_RADIUS}
     */
    private float mBackgroundRadius;

    private Dialog mDialog;

    /**
     * 是否在View之间添加分割线 {@link #setDivide(boolean)}
     */
    private boolean isDivide;

    private boolean isMaterialDesign;

    private boolean isDefaultBackground, isDefaultGravity, isDefaultMargin, isDefaultAnimator;

    private boolean fullScreen;

    private OnClickListener mOnClickListener;

    private int mLayoutResId;

    /************************************* 以下为自定义方法 *********************************/

    /**
     * @param margin {@link #mMargin}
     * @return this
     */
    public <T extends BaseDialogFragment> T setMargin(int margin) {
        mMargin = OsHelper.dp2px(margin);
        isDefaultMargin = false;
        return (T) mDialogFragment;
    }

    /**
     * @param gravity {@link #mGravity}
     * @return this
     */
    public <T extends BaseDialogFragment> T setGravity(int gravity) {
        mGravity = gravity;
        isDefaultGravity = false;
        return (T) mDialogFragment;
    }

    /**
     * @param animator {@link #mAnimator}
     * @return this
     */
    public <T extends BaseDialogFragment> T setAnimator(int animator) {
        mAnimator = animator;
        isDefaultAnimator = false;
        return (T) mDialogFragment;
    }

    /**
     * Dialog背景，默认的background，为白色圆角背景, 使用color文件下的颜色
     *
     * @param background {@link #mBackground}
     * @return mDialogFragment
     */
    public <T extends BaseDialogFragment> T setBackground(Drawable background) {
        mBackground = background;
        isDefaultBackground = false;
        return (T) mDialogFragment;
    }

    /**
     * Dialog矩形背景
     *
     * @return mDialogFragment
     */
    public <T extends BaseDialogFragment> T setBackgroundRectangle() {
        setBackground(DrawableHelper.solid(android.R.color.white).build());
        return (T) mDialogFragment;
    }

    /**
     * @param width dialog居中时的宽度 {@link #mCenterWidth}
     * @return mDialogFragment
     */
    public <T extends BaseDialogFragment> T setCenterWidth(int width) {
        mCenterWidth = width;
        return (T) mDialogFragment;
    }

    /**
     * @param primaryColor 主要显示颜色
     * @return mDialogFragment
     */
    public <T extends BaseDialogFragment> T setPrimaryColor(int primaryColor) {
        mPrimaryColor = primaryColor;
        return (T) mDialogFragment;
    }

    /**
     * @param secondaryColor 次要显示颜色
     * @return mDialogFragment
     */
    public <T extends BaseDialogFragment> T setSecondaryColor(int secondaryColor) {
        mSecondaryColor = secondaryColor;
        return (T) mDialogFragment;
    }

    /**
     * @param dimAccount 设置dialog灰色遮罩的昏暗程度 {@link #mDimAccount}
     * @return mDialogFragment
     */
    public <T extends BaseDialogFragment> T setDimAccount(float dimAccount) {
        mDimAccount = dimAccount;
        return (T) mDialogFragment;
    }

    /**
     * @param onClickListener dialog的点击事件
     * @return mDialogFragment
     */
    public <T extends BaseDialogFragment> T setOnClickListener(OnClickListener onClickListener) {
        mOnClickListener = onClickListener;
        return (T) mDialogFragment;
    }

    /**
     * @param cancel 是否可以通过点击返回关闭dialog
     * @return mDialogFragment
     */
    public <T extends BaseDialogFragment> T setCancel(boolean cancel) {
        mDialogFragment.setCancel(cancel);
        return (T) mDialogFragment;
    }

    /**
     * @param cancel 是否可以通过点击dialog外部关闭dialog
     * @return mDialogFragment
     */
    public <T extends BaseDialogFragment> T setTouchInOutSideCancel(boolean cancel) {
        isTouchInOutSideCancel = cancel;
        return (T) mDialogFragment;
    }

    /**
     * @param divide 是否添加线条, 不添加分割线时为MaterialDesign样式 {@link #isDivide}
     * @return mDialogFragment
     */
    public <T extends BaseDialogFragment> T setDivide(boolean divide) {
        isDivide = divide;
        return (T) mDialogFragment;
    }

    /**
     * @param materialDesign 设置样式
     * @return mDialogFragment
     */
    public <T extends BaseDialogFragment> T setMaterialDesign(boolean materialDesign) {
        isMaterialDesign = materialDesign;
        return (T) mDialogFragment;
    }

    /**
     * @param radiusCard 圆角大小 {@link #mBackgroundRadius}
     * @return mDialogFragment
     */
    public <T extends BaseDialogFragment> T setRadiusCard(float radiusCard) {
        mBackgroundRadius = radiusCard;
        return (T) mDialogFragment;
    }

    /**
     * 设置一些属性完成之后，调用该方法创建fragment并显示。
     *
     * @return mDialogFragment
     */
    public <T extends BaseDialogFragment> T show() {
        show(BaseDialogFragment.class.getSimpleName());
        return (T) mDialogFragment;
    }

    /**
     * @param tag 给fragment添加tag
     * @return mDialogFragment
     */
    public <T extends BaseDialogFragment> T show(String tag) {
        mDialogFragment.show(mActivity.getSupportFragmentManager(), tag);
        return (T) mDialogFragment;
    }

    public void onCancel(@NonNull DialogInterface dialog) {
        mDialogFragment.onCancel(dialog);
    }

    /************************************* 以下为实现过程 *********************************/

    public DialogFragmentDelegate(BaseDialogFragment dialogFragment, AppCompatActivity activity) {
        mDialogFragment = dialogFragment;
        mActivity = activity;
        mBackground = DrawableHelper
                .solid(android.R.color.white)
                .radius(DEFAULT_RADIUS)
                .build();
        mMargin = OsHelper.dp2px(10);
        mCenterWidth = ScreenHelper.getDisplayWidth() - 10 * mMargin;
        mGravity = Gravity.CENTER;
        mAnimator = AnimatorHelper.expand();
        mPrimaryColor = R.color.colorMaterialLightBlue;
        mSecondaryColor = R.color.colorMaterialSecondLightBlue;
        mDimAccount = -1f;
        mBackgroundRadius = DEFAULT_RADIUS;
        isTouchInOutSideCancel = false;
        isDivide = false;
        isMaterialDesign = false;
        isDefaultBackground = isDefaultGravity = isDefaultMargin = isDefaultAnimator = true;
        fullScreen = false;
        mLayoutResId = -1;
    }

    public boolean isFullScreen() {
        return fullScreen;
    }

    void onCreate() {
        // 必须在onCreate方法设置才有效
        mDialogFragment.setStyle(AppCompatDialogFragment.STYLE_NO_TITLE,
                fullScreen ? R.style.FullScreenTheme : R.style.AbsFragmentTheme);
    }

    View getView() {
        return mView;
    }

    void setView(View view) {
        mView = view;
    }

    @Nullable
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState
    ) {
        mDialog = getDialog();
        if (mDialog == null) {
            return null;
        }

        mContentView = getWindow().getDecorView().findViewById(android.R.id.content);
        if (mLayoutResId != -1) mView = inflater.inflate(mLayoutResId, container, false);
        setTextColor();
        setDialogBackground();
        mDialogFragment.initView(inflater, container, savedInstanceState);
        return mView;
    }

    public void setLayoutResId(int layoutResId) {
        mLayoutResId = layoutResId;
    }

    private void setDialogBackground() {
        if (isMaterialDesign) {
            mBackgroundRadius = MATERIAL_RADIUS;
        }
        if (isDefaultBackground) {
            mBackground = DrawableHelper
                    .solid(android.R.color.white)
                    .radius(mBackgroundRadius)
                    .build();
        }
    }

    /**
     * 初始化默认的文本颜色
     */
    private void setTextColor() {
        // 设置文本颜色
        mPrimaryTextColor = getColor(isDefaultBackground ? R.color.colorPrimaryTextDark :
                R.color.colorPrimaryTextLight);
        mSecondaryTextColor = getColor(isDefaultBackground ? R.color.colorSecondaryTextDark :
                R.color.colorSecondaryTextLight);
        mButtonTextColor = getColor(isDefaultBackground ? R.color.colorMaterialLightBlue :
                android.R.color.white);
        mHintColor = getColor(isDefaultBackground ? R.color.colorHintTextDark :
                R.color.colorHintTextLight);
        mDivideColor = getColor(isDefaultBackground ? R.color.colorDivideDark :
                R.color.colorLightGray);

        // 按钮点击时的文本颜色
        mPressedColor = isDefaultBackground ? R.color.colorLightGray : R.color.colorTranslucentLightGray;
    }

    Dialog getDialog() {
        return mDialogFragment.getDialog();
    }

    <T extends View> T findViewById(@IdRes int id) {
        return mView.findViewById(id);
    }

    void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        if (mDialog != null) {
            // 解决Dialog内存泄漏
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
     * 初始化dialog参数
     */
    protected void initializeDialogParams() {
//        Animation startAnimator = OptAnimationLoader.loadAnimation(mActivity,
//                R.anim.dialog_scale_shrink_in);
//        mContentView.setAnimation(startAnimator);
    }

    /**
     * 初始化dialog的大小
     */
    void setDialogMargin(View decorView) {
        ViewGroup.MarginLayoutParams layoutParams = (ViewGroup.MarginLayoutParams) decorView.getLayoutParams();
        mDialogFragment.onDialogLayoutParams(layoutParams, mCenterWidth, mMargin);
        decorView.setLayoutParams(layoutParams);
    }

    /**
     * @param layoutParams 通过rootView的LayoutParams设定margin
     */
    protected void onDialogLayoutParams(
            ViewGroup.MarginLayoutParams layoutParams,
            int centerWidth,
            int margin
    ) {
        if (isFullScreen()) {
            layoutParams.width = ScreenHelper.getDisplayWidth();
        } else {
            if (isDialogInCenter()) {
                layoutParams.width = centerWidth;
            } else {
                layoutParams.topMargin = 2 * margin;
                layoutParams.bottomMargin = margin;
                layoutParams.width = ScreenHelper.getDisplayWidth() - 2 * margin;
            }
        }
    }

    /**
     * 设置默认的动画效果
     */
    void onDialogAnimator() {
        if (isDefaultAnimator) {
            if (mGravity == Gravity.TOP) {
                mAnimator = AnimatorHelper.top();
            } else if (mGravity == Gravity.BOTTOM) {
                mAnimator = AnimatorHelper.bottom();
            }
        }
    }

    /**
     * 设置Windows的属性
     */
    void apply(Window window) {
        // dialog背景
        mView.setBackground(mBackground);

        // 设置是否可以通过点击dialog之外的区域取消显示dialog
        mDialog.setCanceledOnTouchOutside(isTouchInOutSideCancel);

        // 设置dialog显示的位置
        WindowManager.LayoutParams attributes = window.getAttributes();
        mDialogFragment.onDialogGravity(attributes);
        window.setAttributes(attributes);

        // 将背景设为透明
        window.setBackgroundDrawable(new ColorDrawable(getColor(android.R.color.transparent)));

        // dialog动画
        window.setWindowAnimations(mAnimator);

        // 解决AlertDialog无法弹出软键盘,且必须放在AlertDialog的show方法之后
        window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE |
                WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);
        // 收起键盘
        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        if (isFullScreen()) {
            window.setLayout(WindowManager.LayoutParams.MATCH_PARENT,
                    ScreenHelper.getDisplayHeight(mActivity));
        }
        // 消除Dialog内容区域外围的灰色
        if (mDimAccount != -1f) window.setDimAmount(mDimAccount);
    }

    Window getWindow() {
        Window window = getDialog().getWindow();
        if (window == null) {
            return mActivity.getWindow();
        }
        return window;
    }

    /**
     * @param layoutParams 通过WindowManager的layoutParams设置显示位置
     */
    protected void onDialogGravity(WindowManager.LayoutParams layoutParams) {
        layoutParams.gravity = mGravity;
    }

    /**
     * @return 是否显示在中间
     */
    boolean isDialogInCenter() {
        return mGravity == Gravity.CENTER;
    }

    /**
     * @param id 字符串id(位于 res/values/strings.xml)
     * @return 字符串
     */
    protected String getStrings(int id) {
        return mActivity.getResources().getString(id);
    }

    /**
     * @param id 颜色id(位于 res/values/colors.xml)
     * @return 颜色值
     */
    protected int getColor(int id) {
        return OsHelper.getColor(mActivity, id);
    }

    public abstract static class OnClickListener<T extends BaseDialogFragment> {

        /**
         * @param dialog  当前显示的Dialog
         * @param confirm 是否是确定按钮，通过这个判断点击的是哪个按钮
         * @return 返回true表示，点击之后会dismiss dialog， 返回false不dismiss dialog
         */
        public boolean onClick(T dialog, boolean confirm) {
            return true;
        }
    }

}
