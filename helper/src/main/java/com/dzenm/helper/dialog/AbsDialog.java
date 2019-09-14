package com.dzenm.helper.dialog;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import androidx.annotation.IdRes;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.app.AppCompatDialog;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

import com.dzenm.helper.R;
import com.dzenm.helper.draw.BackGHelper;
import com.dzenm.helper.os.OsHelper;
import com.dzenm.helper.os.ScreenHelper;

/**
 * @author dinzhenyan
 * @date 2019-04-30 20:03
 * <p>
 * Dialog的抽象类
 */
public abstract class AbsDialog extends AppCompatDialog {

    protected static final float DEFAULT_RADIUS = 8f;

    /**
     * 根布局, 用于设置dialog显示颜色和圆角大小, 以及dialog的长宽, 或者寻找子View的ID
     */
    protected View mView;

    /**
     * dialog显示的背景, 通过设置根布局的背景mView.setBackground(mBackground)设置dialog的背景
     * 默认白色背景和圆角, 自定义背景调用 {@link #setBackground(Drawable)}
     */
    protected Drawable mBackground = BackGHelper.radius(DEFAULT_RADIUS).build();

    /**
     * dialog上下左右的边距值, 默认值为10, 由于不能直接通过WindowManager属性设置, 需要通过
     * ViewGroup.MarginLayoutParams设置topMargin、bottomMargin, 左右的margin通过width设置
     * 自定义边距值调用 {@link #setMargin(int)}
     */
    protected int mMargin = OsHelper.dp2px(10);

    /**
     * dialog居中时的宽度, 宽度为(屏幕宽度-10*mMargin), 由于居中时, width值过大, 因此在居中时
     * 做一些限制, 改变居中时的宽度
     */
    protected int mCenterWidth = OsHelper.getDisplayWidth() - 10 * mMargin;


    /**
     * dialog显示的位置，默认显示在中间, 调用 {@link Gravity} 里的值设置
     * 自定义位置调用 {@link #setGravity(int)}
     */
    protected int mGravity = Gravity.CENTER;

    /**
     * dialog动画, 默认根据 {@link #mGravity} 的位置显示动画
     * 当 {@link #mGravity} 的值为 {@link Gravity} TOP 从顶部往下弹出
     * 当 {@link #mGravity} 的值为 {@link Gravity} BOTTOM 从底部往上弹出
     * 当 {@link #mGravity} 的值为 {@link Gravity} CENTER 从中间缩放显示
     * 自定义动画调用 {@link #setAnimator(int)}
     */
    protected int mAnimator = AnimatorHelper.shrink();

    /**
     * 主要颜色, 除了灰色和白色之外的颜色, 默认为蓝色为主色
     */
    protected int mPrimaryColor = R.color.colorDarkBlue;

    /**
     * 次要颜色, 除了灰色和白色之外的颜色, 默认为添加一定透明度的蓝色为次色
     */
    protected int mSecondaryColor = R.color.colorTranslucentDarkBlue;

    /**
     * dialog之外的灰色遮罩 (去除dialog灰色区域)
     * 自定义调用 {@link #setTranslucent(boolean)}
     */
    protected boolean isTranslucent = false;

    /**
     * 触摸dialog外部关闭dialog
     * 自定义调用 {@link #setTouchInOutSideCancel(boolean)}
     */
    private boolean isTouchInOutSideCancel = false;

    /**
     * 圆角大小 {@link #DEFAULT_RADIUS}
     */
    protected float mRadiusCard = DEFAULT_RADIUS;

    /**
     * 是否在View和View之间添加分割线 {@link #setDivide(boolean)}
     */
    protected boolean isDivide = false;

    protected boolean isDefaultBackground = true;

    protected boolean isDefaultGravity = true;

    protected boolean isDefaultMargin = true;

    protected boolean isDefaultAnimator = true;

    protected OnDialogClickListener mOnDialogClickListener;

    static {
        // 开启在TextView的drawableTop或者其他额外方式使用矢量图渲染
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
    }

    /************************************* 以下为自定义提示内容 *********************************/

    /**
     * @param margin {@link #mMargin}
     * @return this
     */
    public <T extends AbsDialog> T setMargin(int margin) {
        mMargin = margin;
        isDefaultMargin = false;
        return (T) this;
    }

    /**
     * @param gravity {@link #mGravity}
     * @return this
     */
    public <T extends AbsDialog> T setGravity(int gravity) {
        mGravity = gravity;
        isDefaultGravity = false;
        return (T) this;
    }

    /**
     * @param animator {@link #mAnimator}
     * @return this
     */
    public <T extends AbsDialog> T setAnimator(int animator) {
        mAnimator = animator;
        isDefaultAnimator = false;
        return (T) this;
    }

    /**
     * Dialog背景，默认的background，为白色圆角背景
     * 使用color文件下的颜色时，必须使用getResources().getColor()，否则不显示
     *
     * @param background {@link #mBackground}
     * @return this
     */
    public <T extends AbsDialog> T setBackground(Drawable background) {
        mBackground = background;
        isDefaultBackground = false;
        return (T) this;
    }

    /**
     * Dialog矩形背景
     *
     * @return this
     */
    public <T extends AbsDialog> T setBackgroundRectangle() {
        setBackground(BackGHelper.solid(android.R.color.white).build());
        return (T) this;
    }

    /**
     * @param translucent dialog灰色遮罩 {@link #isTranslucent}
     * @return this
     */
    public <T extends AbsDialog> T setTranslucent(boolean translucent) {
        isTranslucent = translucent;
        return (T) this;
    }

    /**
     * @param cancel 是否可以通过点击返回关闭dialog
     * @return this
     */
    public <T extends AbsDialog> T setCancel(boolean cancel) {
        setCancelable(cancel);
        return (T) this;
    }

    /**
     * @param cancel 是否可以通过点击dialog外部关闭dialog
     * @return this
     */
    public <T extends AbsDialog> T setTouchOutsideCancel(boolean cancel) {
        setCanceledOnTouchOutside(cancel);
        return (T) this;
    }

    /**
     * @param divide 是否添加线条, 不添加分割线时为MaterialDesign样式 {@link #isDivide}
     * @return this
     */
    public <T extends AbsDialog> T setDivide(boolean divide) {
        isDivide = divide;
        return (T) this;
    }

    /**
     * @param onDialogClickListener dialog的点击事件
     * @return this
     */
    public <T extends AbsDialog> T setOnDialogClickListener(OnDialogClickListener onDialogClickListener) {
        mOnDialogClickListener = onDialogClickListener;
        return (T) this;
    }


    /**
     * 创建并显示Dialog，放在最后调用
     * 继承时若需要设置gravity，animator， background时
     * 必须重写该方法，并且在 super.show() 之前调用
     * 其他有关View的操作在 super.show() 之后调用
     */
    @Override
    public void show() {
        Window window = getWindow();
        setDialogSize(window);
        setStyle();
        setWindowProperty(window);
        super.show();
        afterShowSetting(window);
    }

    /************************************* 以下为实现细节（不可见方法） *********************************/

    public AbsDialog(@NonNull Context context) {
        this(context, R.style.BaseDialog);
    }

    public AbsDialog(Context context, int theme) {
        super(context, theme);
        setCanceledOnTouchOutside(false);
        create();
    }

    protected int layoutId() {
        return 0;
    }

    /**
     * 初始化View控件
     */
    protected void initView() {

    }

    /**
     * 设置默认的效果
     */
    protected void setStyle() {
        if (isDefaultMargin) {
            if (mGravity == Gravity.TOP) {
                if (isDefaultBackground) {
                    // 底部圆角，白色背景
                    mBackground = BackGHelper.radiusBR(8).radiusBL(8).build();
                }
            } else if (mGravity == Gravity.BOTTOM) {
                if (isDefaultBackground) {
                    // 顶部圆角，白色背景
                    mBackground = BackGHelper.radiusTL(8).radiusTR(8).build();
                }
            }
        }
        if (isDefaultAnimator) {
            if (mGravity == Gravity.TOP) {
                mAnimator = AnimatorHelper.top();
            } else if (mGravity == Gravity.BOTTOM) {
                mAnimator = AnimatorHelper.bottom();
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (layoutId() != 0) {
            mView = LayoutInflater.from(getContext()).inflate(layoutId(), null);
            setContentView(mView);
        }
        initView();
    }

    /**
     * 供子类查找id
     *
     * @param id view id
     * @return view
     */
    public <T extends View> T findViewById(@IdRes int id) {
        return mView.findViewById(id);
    }

    /**
     * 初始化dialog的大小
     */
    private void setDialogSize(Window window) {
        ViewGroup.MarginLayoutParams layoutParams = (ViewGroup.MarginLayoutParams) mView.getLayoutParams();
        window.setLayout(WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT);
        WindowManager.LayoutParams windowAttributes = window.getAttributes();
        if (isShowCenter()) {
            setCenterDialogLayoutParams(windowAttributes);
        } else {
            setDialogLayoutParams(layoutParams);
            mView.setLayoutParams(layoutParams);
        }
        window.setAttributes(windowAttributes);
    }

    /**
     * 设置dialog的LayoutParams
     */
    protected void setDialogLayoutParams(ViewGroup.MarginLayoutParams layoutParams) {
        layoutParams.topMargin = OsHelper.dp2px(mMargin);
        layoutParams.bottomMargin = OsHelper.dp2px(mMargin);
        layoutParams.width = ScreenHelper.getWidth() - 2 * OsHelper.dp2px(mMargin);
    }

    /**
     * 设置dialog居中的LayoutParams
     */
    protected void setCenterDialogLayoutParams(WindowManager.LayoutParams windowAttributes) {
        if (!isPromptDialog()) {
            windowAttributes.width = ScreenHelper.getWidth() - 10 * OsHelper.dp2px(mMargin);
        }
    }

    protected boolean isPromptDialog() {
        return false;
    }

    /**
     * 设置Windows的属性
     */
    protected void setWindowProperty(Window window) {
        window.setGravity(mGravity);                                  // 显示的位置
        window.setWindowAnimations(mAnimator);                        // 窗口动画
        mView.setBackground(mBackground);
    }

    /**
     * Dialog调用show方法之后的一些设置
     */
    protected void afterShowSetting(Window window) {
        window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);  // 解决ALertDialog无法弹出软键盘,且必须放在AlertDialog的show方法之后
        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);  // 收起键盘

        if (isTranslucent) {    // 消除Dialog内容区域外围的灰色
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
                window.setDimAmount(0);
            } else {
                window.clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
            }
        }
    }

    /**
     * @param id 字符串id(位于 res/values/strings.xml)
     * @return 字符串
     */
    protected String getString(int id) {
        return getContext().getResources().getString(id);
    }

    /**
     * @param id 颜色id(位于 res/values/colors.xml)
     * @return 颜色值
     */
    protected int getColor(int id) {
        return getContext().getResources().getColor(id);
    }

    /**
     * @return 是否显示在中间
     */
    protected boolean isShowCenter() {
        return mGravity == Gravity.CENTER;
    }

    public interface OnDialogClickListener<T extends AbsDialog> {
        /**
         * @param dialog  Dialog
         * @param confirm 是否是确定按钮，通过这个判断点击的是哪个按钮
         * @return 返回true表示，点击之后会dismiss dialog， 返回false不dismiss dialog
         */
        boolean onClick(T dialog, boolean confirm);
    }
}