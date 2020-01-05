package com.dzenm.helper.dialog;

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
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.app.AppCompatDialogFragment;

import com.dzenm.helper.R;
import com.dzenm.helper.animator.AnimatorHelper;
import com.dzenm.helper.draw.DrawableHelper;
import com.dzenm.helper.os.OsHelper;
import com.dzenm.helper.os.ScreenHelper;

/**
 * @author dinzhenyan
 * @date 2019-05-18 15:23
 */
public abstract class AbsDialogFragment extends AppCompatDialogFragment {

    protected static final float DEFAULT_RADIUS = 16f;
    protected static final float MATERIAL_RADIUS = 2f;

    protected AppCompatActivity mActivity;

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
     * 通过 {@link #setTranslucent(boolean)} 设置遮罩是否透明
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

    static {
        // 开启在TextView的drawableTop或者其他额外方式使用矢量图渲染
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
    }

    /************************************* 以下为自定义方法 *********************************/

    /**
     * @param margin {@link #mMargin}
     * @return this
     */
    public <T extends AbsDialogFragment> T setMargin(int margin) {
        mMargin = OsHelper.dp2px(margin);
        isDefaultMargin = false;
        return (T) this;
    }

    /**
     * @param gravity {@link #mGravity}
     * @return this
     */
    public <T extends AbsDialogFragment> T setGravity(int gravity) {
        mGravity = gravity;
        isDefaultGravity = false;
        return (T) this;
    }

    /**
     * @param animator {@link #mAnimator}
     * @return this
     */
    public <T extends AbsDialogFragment> T setAnimator(int animator) {
        mAnimator = animator;
        isDefaultAnimator = false;
        return (T) this;
    }

    /**
     * Dialog背景，默认的background，为白色圆角背景, 使用color文件下的颜色
     *
     * @param background {@link #mBackground}
     * @return this
     */
    public <T extends AbsDialogFragment> T setBackground(Drawable background) {
        mBackground = background;
        isDefaultBackground = false;
        return (T) this;
    }

    /**
     * Dialog矩形背景
     *
     * @return this
     */
    public <T extends AbsDialogFragment> T setBackgroundRectangle() {
        setBackground(DrawableHelper.solid(android.R.color.white).build());
        return (T) this;
    }

    /**
     * @param width dialog居中时的宽度 {@link #mCenterWidth}
     * @return this
     */
    public <T extends AbsDialogFragment> T setCenterWidth(int width) {
        mCenterWidth = width;
        return (T) this;
    }

    /**
     * @param primaryColor 主要显示颜色
     * @return this
     */
    public <T extends AbsDialogFragment> T setPrimaryColor(int primaryColor) {
        mPrimaryColor = primaryColor;
        return (T) this;
    }

    /**
     * @param secondaryColor 次要显示颜色
     * @return this
     */
    public <T extends AbsDialogFragment> T setSecondaryColor(int secondaryColor) {
        mSecondaryColor = secondaryColor;
        return (T) this;
    }

    /**
     * @param translucent 去除dialog灰色遮罩
     * @return this
     */
    public <T extends AbsDialogFragment> T setTranslucent(boolean translucent) {
        mDimAccount = 0f;
        return (T) this;
    }

    /**
     * @param dimAccount 设置dialog灰色遮罩的昏暗程度 {@link #mDimAccount}
     * @return this
     */
    public <T extends AbsDialogFragment> T setDimAccount(float dimAccount) {
        mDimAccount = dimAccount;
        return (T) this;
    }

    /**
     * @param onClickListener dialog的点击事件
     * @return this
     */
    public <T extends AbsDialogFragment> T setOnClickListener(OnClickListener onClickListener) {
        mOnClickListener = onClickListener;
        return (T) this;
    }

    /**
     * @param cancel 是否可以通过点击返回关闭dialog
     * @return this
     */
    public <T extends AbsDialogFragment> T setCancel(boolean cancel) {
        setCancelable(cancel);
        return (T) this;
    }

    /**
     * @param cancel 是否可以通过点击dialog外部关闭dialog
     * @return this
     */
    public <T extends AbsDialogFragment> T setTouchInOutSideCancel(boolean cancel) {
        isTouchInOutSideCancel = cancel;
        return (T) this;
    }

    /**
     * @param divide 是否添加线条, 不添加分割线时为MaterialDesign样式 {@link #isDivide}
     * @return this
     */
    public <T extends AbsDialogFragment> T setDivide(boolean divide) {
        isDivide = divide;
        return (T) this;
    }

    /**
     * @param materialDesign 设置样式
     * @return this
     */
    public <T extends AbsDialogFragment> T setMaterialDesign(boolean materialDesign) {
        isMaterialDesign = materialDesign;
        return (T) this;
    }

    /**
     * @param radiusCard 圆角大小 {@link #mBackgroundRadius}
     * @return this
     */
    public <T extends AbsDialogFragment> T setRadiusCard(float radiusCard) {
        mBackgroundRadius = radiusCard;
        return (T) this;
    }

    /**
     * 设置一些属性完成之后，调用该方法创建fragment并显示。
     *
     * @return this
     */
    public <T extends AbsDialogFragment> T show() {
        show(AbsDialogFragment.class.getSimpleName());
        return (T) this;
    }

    /**
     * @param tag 给fragment添加tag
     * @return this
     */
    public <T extends AbsDialogFragment> T show(String tag) {
        show(mActivity.getSupportFragmentManager(), tag);
        return (T) this;
    }

    @Override
    public void onCancel(@NonNull DialogInterface dialog) {
        super.onCancel(dialog);
    }

    /************************************* 以下为实现过程 *********************************/

    public AbsDialogFragment(AppCompatActivity activity) {
        mActivity = activity;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 必须在onCreate方法设置才有效
        setStyle(AppCompatDialogFragment.STYLE_NO_TITLE,
                isFullScreen() ? R.style.FullScreenTheme : R.style.AbsFragmentTheme);
    }

    protected boolean isFullScreen() {
        return false;
    }

    @Nullable
    @Override
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
        if (layoutId() != -1) mView = inflater.inflate(layoutId(), null);
        setDefaultTextColor();
        setDefaultBackground();
        initView(inflater, container, savedInstanceState);
        return mView;
    }

    protected int layoutId() {
        return -1;
    }

    protected void initView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState
    ) {
    }


    protected void setDefaultBackground() {
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
    private void setDefaultTextColor() {
        // 设置文本颜色
        mPrimaryTextColor = getColor(isDefaultBackground ? R.color.colorPrimaryTextDark :
                R.color.colorPrimaryTextLight);
        mSecondaryTextColor = getColor(isDefaultBackground ? R.color.colorSecondaryTextDark :
                R.color.colorSecondaryTextLight);
        mButtonTextColor = getColor(isDefaultBackground ? R.color.colorDarkBlue :
                android.R.color.white);
        mHintColor = getColor(isDefaultBackground ? R.color.colorHintTextDark :
                R.color.colorHintTextLight);
        mDivideColor = getColor(isDefaultBackground ? R.color.colorDivideDark :
                R.color.colorLightGray);

        // 按钮点击时的文本颜色
        mPressedColor = isDefaultBackground ? R.color.colorLightGray : R.color.colorTranslucentLightGray;
    }


    public <T extends View> T findViewById(@IdRes int id) {
        return mView.findViewById(id);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
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

    @Override
    public void onStart() {
        super.onStart();
        initializeDialogParams(getWindow());
    }

    /**
     * 初始化dialog参数
     */
    protected void initializeDialogParams(Window window) {
//        Animation startAnimator = OptAnimationLoader.loadAnimation(mActivity,
//                R.anim.dialog_scale_shrink_in);
//        mContentView.setAnimation(startAnimator);
        setDefaultMargin(mView);
        setDefaultAnimator();
        setWindowProperty(window);
    }

    /**
     * 初始化dialog的大小
     */
    private void setDefaultMargin(View decorView) {
        ViewGroup.MarginLayoutParams layoutParams = (ViewGroup.MarginLayoutParams) decorView.getLayoutParams();
        setLayoutParams(layoutParams, mCenterWidth, mMargin);
        decorView.setLayoutParams(layoutParams);
    }

    /**
     * @param layoutParams 通过rootView的LayoutParams设定margin
     */
    protected void setLayoutParams(
            ViewGroup.MarginLayoutParams layoutParams,
            int centerWidth,
            int margin
    ) {
        if (isFullScreen()) {
            layoutParams.width = ScreenHelper.getDisplayWidth();
        } else {
            if (isShowCenter()) {
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
    protected void setDefaultAnimator() {
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
    protected void setWindowProperty(Window window) {
        // dialog背景
        mView.setBackground(mBackground);

        // 设置是否可以通过点击dialog之外的区域取消显示dialog
        mDialog.setCanceledOnTouchOutside(isTouchInOutSideCancel);

        // 设置dialog显示的位置
        WindowManager.LayoutParams attributes = window.getAttributes();
        setDialogGravity(attributes);
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

    protected Window getWindow() {
        Window window = mDialog.getWindow();
        if (window == null) {
            return mActivity.getWindow();
        }
        return window;
    }

    /**
     * @param layoutParams 通过WindowManager的layoutParams设置显示位置
     */
    protected void setDialogGravity(WindowManager.LayoutParams layoutParams) {
        layoutParams.gravity = mGravity;
    }

    /**
     * @return 是否显示在中间
     */
    protected boolean isShowCenter() {
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
        return mActivity.getResources().getColor(id);
    }

    private interface OnDialogClickListener<T extends AbsDialogFragment> {

        /**
         * @param dialog  当前显示的Dialog
         * @param confirm 是否是确定按钮，通过这个判断点击的是哪个按钮
         * @return 返回true表示，点击之后会dismiss dialog， 返回false不dismiss dialog
         */
        boolean onClick(T dialog, boolean confirm);
    }

    public abstract static class OnClickListener<T extends AbsDialogFragment> implements OnDialogClickListener<T> {

        @Override
        public boolean onClick(T dialog, boolean confirm) {
            return true;
        }
    }

}
