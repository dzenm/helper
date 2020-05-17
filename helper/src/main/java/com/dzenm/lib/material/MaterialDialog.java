package com.dzenm.lib.material;

import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.LinearLayout;

import androidx.annotation.ColorInt;
import androidx.annotation.DrawableRes;
import androidx.annotation.LayoutRes;
import androidx.annotation.StringRes;
import androidx.appcompat.app.AppCompatActivity;

import com.dzenm.lib.R;
import com.dzenm.lib.animator.AnimatorHelper;
import com.dzenm.lib.drawable.DrawableHelper;
import com.dzenm.lib.os.OsHelper;
import com.dzenm.lib.os.ScreenHelper;
import com.dzenm.lib.os.ThemeHelper;
import com.dzenm.lib.photo.PhotoSelector;

/**
 * @author dinzhenyan
 * @date 2019-05-18 15:23
 */
public final class MaterialDialog extends AbsDialogFragment {

    public static final float DEFAULT_RADIUS = 16f;
    public static final float MATERIAL_RADIUS = 4f;

    static final int TYPE_ITEM = -1;
    static final int TYPE_SINGLE = -2;
    static final int TYPE_MULTIPLE = -3;

    /**
     * The identifier for the positive button.
     */
    public static final int BUTTON_POSITIVE = -1;

    /**
     * The identifier for the negative button.
     */
    public static final int BUTTON_NEGATIVE = -2;

    /**
     * The identifier for the neutral button.
     */
    public static final int BUTTON_NEUTRAL = -3;

    /**
     * content view, not include title and button, if {@link #mMessage} is null, content view is
     * {@link MaterialView#createMenuLayout(int, boolean, boolean, CharSequence[])}
     */
    private View mContentView;

    /**
     * title text, @see {@link Builder#mTitle}, message text, @see {@link Builder#mMessage}
     */
    private CharSequence mTitle, mMessage;

    /**
     * title text color, {@link Builder#mTitleColor}
     */
    private int mTitleColor;

    /**
     * title icon, @see {@link Builder}
     */
    private Drawable mIcon;

    /**
     * item text, @see {@link Builder#mItems}
     */
    private CharSequence[] mItems;

    /**
     * button text, @see {@link Builder#mPositiveButtonText}, {@link Builder#mNegativeButtonText}
     */
    private CharSequence mPositiveButtonText, mNegativeButtonText, mNeutralButtonText;

    /**
     * button click listener, @see {@link Builder#mPositiveClickListener} or {@link Builder#mNegativeClickListener}
     */
    private OnClickListener mPositiveClickListener, mNegativeClickListener, mNeutralClickListener;

    private int mWhichType;

    /**
     * item click listener, @see {@link Builder#mOnMultipClickListener} and {@link Builder#mItems}
     */
    private OnItemClickListener mOnItemClickListener;

    /**
     * item single click listener, @see {@link Builder#mOnSingleClickListener} and {@link Builder#mItems}
     */
    private OnSingleClickListener mOnSingleClickListener;

    /**
     * item multiple click listener, @see {@link Builder#mOnMultipleClickListener} and
     * {@link Builder#mItems}
     */
    private OnMultipleClickListener mOnMultipleClickListener;

    /**
     * photo selected click listener, @see {@link Builder#mOnSelectedPhotoListener}
     */
    private PhotoSelector.OnSelectedPhotoListener mOnSelectedPhotoListener;

    /**
     * create a content view, @see {@link Builder#mIContentView}
     */
    private IContentView mIContentView;

    /************************************* 以下为实现过程 *********************************/

    private MaterialDialog(AppCompatActivity activity) {
        super(activity);
    }

    @Override
    protected void initView() {
        MaterialView mMaterialView = new MaterialView(this, mD, mActivity);

        // 获取创建的DecorView
        LinearLayout decorView = getDecorView();

        // 判断是否显示标题, 是否显示Message, Content, 是否显示按钮
        boolean isShowTitle = !TextUtils.isEmpty(mTitle) || mIcon != null;
        boolean isShowContent = !TextUtils.isEmpty(mMessage);
        boolean isShowButton = !((TextUtils.isEmpty(mPositiveButtonText)
                && TextUtils.isEmpty(mNegativeButtonText))
                && TextUtils.isEmpty(mNeutralButtonText));

        if (isShowContent) {
            mContentView = mMaterialView.createMessageView(mMessage, isShowTitle);
        }

        if (mItems != null) {
            isShowContent = true;
            if (mOnSelectedPhotoListener != null) {
                // Photo Dialog
                if (!mD.isMaterialDesign) mD.mGravity = Gravity.BOTTOM;
                mOnItemClickListener = mMaterialView.getPhotoSelectorListener();
                PhotoSelector.getInstance().with(this)
                        .setOnSelectedPhotoListener(mOnSelectedPhotoListener)
                        .setOnFinishListener(mMaterialView.getPhotoSelectorFinishListener());
            }

            if (mOnItemClickListener != null) {
                // Menu Dialog
                mMaterialView.setOnItemClickListener(mOnItemClickListener);
            } else if (mOnSingleClickListener != null) {
                // Single Menu Dialog
                mMaterialView.setOnSingleClickListener(mOnSingleClickListener);
            } else if (mOnMultipleClickListener != null) {
                // Multiple Menu Dialog
                mMaterialView.setOnMultipleClickListener(mOnMultipleClickListener);
            }

            if (mD.isMaterialDesign) {
                mContentView = mMaterialView.createMenuLayout(mWhichType, isShowTitle, isShowButton, mItems);
            } else {
                mContentView = mMaterialView.createIOSMenuLayout(mWhichType, isShowTitle, mItems);
            }
        } else if (mIContentView != null) {
            // set a view for Dialog
            isShowContent = true;
            mContentView = mIContentView.onCreateView(mD);
        } else if (mContentView != null) {
            isShowContent = true;
        }

        // 添加 Title Layout
        if (isShowTitle) decorView.addView(mMaterialView.createTitleLayout(
                mTitle, mIcon, mTitleColor
        ));

        // 添加 Content Layout
        if (isShowContent) {
            ViewGroup viewGroup = mMaterialView.createContentLayout();
            ViewGroup scrollLayout = mMaterialView.createScrollLayout();
            LinearLayout titleLayout = (LinearLayout) decorView.getChildAt(0);

            // 添加ContentView
            viewGroup.addView(mContentView);
            scrollLayout.addView(viewGroup);
            decorView.addView(scrollLayout);

            // 设置IOS 样式的Item
            if (!mD.isMaterialDesign && mItems != null) {
                Drawable drawable;
                if (titleLayout != null) {
                    titleLayout.setBackground(DrawableHelper.solid(mD.mBackgroundColor)
                            .radiusTL(mD.mBackgroundRadius).radiusTR(mD.mBackgroundRadius).build());
                    drawable = DrawableHelper.solid(mD.mBackgroundColor)
                            .radiusBL(mD.mBackgroundRadius).radiusBR(mD.mBackgroundRadius).build();
                } else {
                    drawable = DrawableHelper.solid(mD.mBackgroundColor).radius(mD.mBackgroundRadiusII)
                            .build();
                }
                scrollLayout.setBackground(drawable);
                View cancelView = mMaterialView.createIOSCancelView();
                decorView.addView(cancelView);
            }
        }

        // 添加 Button Layout
        if (isShowButton) {
            // 添加分割线
            if (!mD.isMaterialDesign) {
                decorView.addView(mMaterialView.createLine(ViewGroup.LayoutParams.MATCH_PARENT, 1));
            }

            decorView.addView(mMaterialView.createButtonLayout(
                    mPositiveButtonText, mNegativeButtonText, mNeutralButtonText,
                    mPositiveClickListener, mNegativeClickListener, mNeutralClickListener
            ));
        }
    }

    @Override
    protected void apply(Window window) {
        super.apply(window);
        if (!mD.isMaterialDesign && mItems != null) {
            mD.mDecorView.setBackground(DrawableHelper.solid(Color.TRANSPARENT).build());
        }
    }

    /************************************* 使用Builder模式 *********************************/

    public static class Builder {

        private AppCompatActivity mActivity;

        /**
         * dialog主题样式
         */
        private int mThemeId;

        /**
         * dialog标题文本, 如果文本不为空, 显示标题, 否则不显示,
         * 可以通过 {@link #setTitle(int)} 或 {@link #setTitle(CharSequence)} 设置
         */
        private CharSequence mTitle;

        /**
         * dialog标题文本颜色, 默认使用灰黑色
         */
        private int mTitleColor;

        /**
         * dialog标题图标, 如果图标不为空, 显示图标, 否则不显示
         * 可以通过 {@link #setIcon(int)} 或 {@link #setIcon(Drawable)} 设置
         */
        private Drawable mIcon;

        /**
         * dialog内容文本, 如果文本不为空, 显示文本, 否则不显示, 可以通过
         * {@link #setMessage(int)} 或 {@link #setMessage(CharSequence)} 设置
         */
        private CharSequence mMessage;

        /**
         * 自定义View, 可以通过 {@link #setView(int)} 或 {@link #setView(View)} 设置
         */
        private View mView;

        /**
         * Item 列表 可以通过 {@link #setItem(CharSequence...)} 或 {@link #setItem(int...)} 设置
         */
        private CharSequence[] mItems;

        /**
         * button文本, 可以通过 {@link #setPositiveClickListener(int, OnClickListener)} 或
         * {@link #setPositiveClickListener(CharSequence, OnClickListener)} 设置右边的按钮,
         * 通过 {@link #setNegativeClickListener(int, OnClickListener)} 或
         * {@link #setNegativeClickListener(CharSequence, OnClickListener) 设置左边的按钮},
         * 通过 {@link #setNeutralClickListener(int, OnClickListener)} 或
         * {@link #setNeutralClickListener(CharSequence, OnClickListener) 设置最左边的按钮},
         */
        private CharSequence mPositiveButtonText, mNegativeButtonText, mNeutralButtonText;

        /**
         * button点击事件, @see {@link #mPositiveButtonText} 或 {@link #mNegativeButtonText} 或
         * {@link #mNeutralButtonText}
         */
        private OnClickListener mOnClickListener, mPositiveClickListener,
                mNegativeClickListener, mNeutralClickListener;

        /**
         * Item的类型
         */
        private int mWhichType;

        /**
         * item点击事件, 可以通过 {@link #setOnMultipleClickListener(OnMultipleClickListener)} 设置
         */
        public OnItemClickListener mOnItemClickListener;

        /**
         * photo选择器的点击事件, 可以通过
         * {@link #setOnSelectedPhotoListener(PhotoSelector.OnSelectedPhotoListener)} 设置
         * <p>
         * final String[] item = new String[]{"拍照", "图片", "取消"};
         * new MaterialDialog.Builder(this)
         * .setTitle("选择一个图片来源")
         * .setItem(item)
         * .setOnSelectedPhotoListener(new PhotoSelector.OnSelectedPhotoListener() {
         *
         * @Override public boolean onGraph(PhotoSelector selector, String filePath) {
         * return super.onGraph(selector, filePath);
         * }
         * }.create()
         * .show();
         */
        private PhotoSelector.OnSelectedPhotoListener mOnSelectedPhotoListener;

        /**
         * item单选点击事件，可以通过 {@link #setOnSingleClickListener(OnSingleClickListener)} 设置
         */
        private OnSingleClickListener mOnSingleClickListener;

        /**
         * item多选点击事件，可以通过 {@link #setOnMultipleClickListener(OnMultipleClickListener)} 设置
         */
        private OnMultipleClickListener mOnMultipClickListener;

        /**
         * if use {@link DialogDelegate}, please implement {@link IContentView}, then setup by
         * {@link #setView(IContentView)}
         */
        private IContentView mIContentView;

        /**
         * @see {@link DialogDelegate#isMaterialDesign}
         */
        private boolean isMaterialDesign = true;

        /**
         * @see {@link DialogDelegate#mMargin}
         */
        private int mMargin = OsHelper.dp2px(10);

        /**
         * @see {@link DialogDelegate#mGravity}
         */
        private int mGravity = Gravity.CENTER;

        /**
         * @see {@link DialogDelegate#mAnimator}
         */
        private int mAnimator = AnimatorHelper.expand();

        /**
         * @see {@link DialogDelegate#mBackgroundColor}
         */
        private int mBackgroundColor;

        /**
         * @see {@link DialogDelegate#mBackgroundRadiusII}
         */
        private float mBackgroundRadius;

        /**
         * @see {@link DialogDelegate#mBackgroundRadiusII}
         */
        private float[] mBackgroundRadiusII;

        /**
         * @see {@link DialogDelegate#mDimAccount}
         */
        private float mDimAccount = -1f;

        /**
         * @see {@link DialogDelegate#isTouchInOutSideCancel}
         */
        private boolean isTouchInOutSideCancel = true;

        /**
         * @see {@link DialogDelegate#isCancelable}
         */
        private boolean isCancelable = true;

        /**
         * @see {@link DialogDelegate#mPrimaryColor}
         */
        private int mPrimaryColor;

        /**
         * @see {@link DialogDelegate#mSecondaryColor}
         */
        private int mSecondaryColor;

        /**
         * @see {@link DialogDelegate#mButtonTextColor}
         */
        private int mButtonTextColor;

        public Builder(AppCompatActivity activity) {
            this(activity, 0);
        }

        public Builder(AppCompatActivity activity, int themeId) {
            mActivity = activity;
            mThemeId = themeId;
            mBackgroundColor = getColor(R.attr.dialogBackgroundColor);
            mBackgroundRadius = MATERIAL_RADIUS;
        }

        public Builder setTitle(CharSequence title) {
            mTitle = title;
            return this;
        }

        public Builder setTitle(@StringRes int titleId) {
            mTitle = mActivity.getText(titleId);
            return this;
        }

        public Builder setTitleColor(@ColorInt int titleColor) {
            mTitleColor = titleColor;
            return this;
        }

        public Builder setIcon(@DrawableRes int icon) {
            mIcon = mActivity.getDrawable(icon);
            return this;
        }

        public Builder setIcon(Drawable icon) {
            mIcon = icon;
            return this;
        }

        public Builder setMessage(CharSequence message) {
            mMessage = message;
            return this;
        }

        public Builder setMessage(@StringRes int messageId) {
            mMessage = mActivity.getText(messageId);
            return this;
        }

        public Builder setView(View view) {
            mView = view;
            return this;
        }

        public Builder setView(@LayoutRes int resId) {
            mView = LayoutInflater.from(mActivity).inflate(resId, null);
            return this;
        }

        public Builder setView(IContentView view) {
            mIContentView = view;
            return this;
        }

        public Builder setItem(CharSequence... item) {
            mItems = item;
            return this;
        }

        public Builder setItem(@StringRes int... item) {
            mItems = new CharSequence[item.length];
            for (int i = 0; i < item.length; i++) {
                mItems[i] = mActivity.getText(item[i]);
            }
            return this;
        }

        public Builder setButtonText(@StringRes int positiveButtonText) {
            mPositiveButtonText = mActivity.getText(positiveButtonText);
            return this;
        }

        public Builder setButtonText(CharSequence positiveButtonText) {
            mPositiveButtonText = positiveButtonText;
            return this;
        }

        public Builder setButtonText(@StringRes int positiveButtonText, @StringRes int negativeButtonText) {
            mPositiveButtonText = mActivity.getText(positiveButtonText);
            mNegativeButtonText = mActivity.getText(negativeButtonText);
            return this;
        }

        public Builder setButtonText(CharSequence positiveButtonText, CharSequence negativeButtonText) {
            mPositiveButtonText = positiveButtonText;
            mNegativeButtonText = negativeButtonText;
            return this;
        }

        public Builder setButtonText(
                @StringRes int positiveButtonText, @StringRes int negativeButtonText, @StringRes int neutralButtonText
        ) {
            mPositiveButtonText = mActivity.getText(positiveButtonText);
            mNegativeButtonText = mActivity.getText(negativeButtonText);
            mNeutralButtonText = mActivity.getText(neutralButtonText);
            return this;
        }

        public Builder setButtonText(
                CharSequence positiveButtonText, CharSequence negativeButtonText,
                CharSequence neutralButtonText
        ) {
            mPositiveButtonText = positiveButtonText;
            mNegativeButtonText = negativeButtonText;
            mNeutralButtonText = neutralButtonText;
            return this;
        }

        public Builder setOnClickListener(OnClickListener listener) {
            mOnClickListener = listener;
            return this;
        }

        public Builder setPositiveClickListener(CharSequence buttonText, OnClickListener listener) {
            mPositiveButtonText = buttonText;
            mPositiveClickListener = listener;
            return this;
        }

        public Builder setPositiveClickListener(@StringRes int buttonText, OnClickListener listener) {
            mPositiveButtonText = mActivity.getText(buttonText);
            mPositiveClickListener = listener;
            return this;
        }

        public Builder setNegativeClickListener(CharSequence buttonText, OnClickListener listener) {
            mNegativeButtonText = buttonText;
            mNegativeClickListener = listener;
            return this;
        }

        public Builder setNegativeClickListener(@StringRes int buttonText, OnClickListener listener) {
            mNegativeButtonText = mActivity.getText(buttonText);
            mNegativeClickListener = listener;
            return this;
        }

        public Builder setNeutralClickListener(CharSequence buttonText, OnClickListener listener) {
            mNeutralButtonText = buttonText;
            mNeutralClickListener = listener;
            return this;
        }

        public Builder setNeutralClickListener(@StringRes int buttonText, OnClickListener listener) {
            mNeutralButtonText = mActivity.getText(buttonText);
            mNeutralClickListener = listener;
            return this;
        }

        public Builder setOnItemClickListener(OnItemClickListener listener) {
            mOnItemClickListener = listener;
            mWhichType = TYPE_ITEM;
            return this;
        }

        public Builder setOnSelectedPhotoListener(PhotoSelector.OnSelectedPhotoListener listener) {
            mOnSelectedPhotoListener = listener;
            mWhichType = TYPE_ITEM;
            return this;
        }

        public Builder setOnSingleClickListener(OnSingleClickListener listener) {
            mOnSingleClickListener = listener;
            mWhichType = TYPE_SINGLE;
            return this;
        }

        public Builder setOnMultipleClickListener(OnMultipleClickListener listener) {
            mOnMultipClickListener = listener;
            mWhichType = TYPE_MULTIPLE;
            return this;
        }

        public Builder setMargin(int margin) {
            mMargin = OsHelper.dp2px(margin);
            return this;
        }

        public Builder setGravity(int gravity) {
            mGravity = gravity;
            return this;
        }

        public Builder setAnimator(int animator) {
            mAnimator = animator;
            return this;
        }

        /**
         * @param backgroundColor {@link DialogDelegate#mBackgroundColor}
         * @return this
         */
        public Builder setBackgroundColor(@ColorInt int backgroundColor) {
            mBackgroundColor = backgroundColor;
            return this;
        }

        /**
         * @param backgroundRadius {@link DialogDelegate#mBackgroundRadius}
         * @return this
         */
        public Builder setBackgroundRadius(float backgroundRadius) {
            mBackgroundRadius = backgroundRadius;
            return this;
        }

        /**
         * @param backgroundRadiusII {@link DialogDelegate#mBackgroundRadiusII}
         * @return this
         */
        public Builder setBackgroundRadius(float[] backgroundRadiusII) {
            mBackgroundRadiusII = backgroundRadiusII;
            return this;
        }

        /**
         * @param dimAccount {@link DialogDelegate#mDimAccount}
         * @return this
         */
        public Builder setDimAccount(float dimAccount) {
            mDimAccount = dimAccount;
            return this;
        }

        /**
         * @param touchInOutSideCancel {@link DialogDelegate#isTouchInOutSideCancel}
         * @return this
         */
        public Builder setTouchInOutSideCancel(boolean touchInOutSideCancel) {
            isTouchInOutSideCancel = touchInOutSideCancel;
            return this;
        }

        /**
         * @param cancelable {@link DialogDelegate#isCancelable}
         * @return this
         */
        public Builder setCancelable(boolean cancelable) {
            isCancelable = cancelable;
            return this;
        }

        /**
         * @param primaryColor {@link DialogDelegate#mPrimaryColor}
         * @return this
         */
        public Builder setPrimaryColor(@ColorInt int primaryColor) {
            mPrimaryColor = primaryColor;
            return this;
        }

        /**
         * @param secondaryColor {@link DialogDelegate#mSecondaryColor}
         * @return this
         */
        public Builder setSecondaryColor(@ColorInt int secondaryColor) {
            mSecondaryColor = secondaryColor;
            return this;
        }

        /**
         * @param materialDesign {@link DialogDelegate#isMaterialDesign}
         * @return this
         */
        public Builder setMaterialDesign(boolean materialDesign) {
            isMaterialDesign = materialDesign;
            return this;
        }

        public MaterialDialog create() {
            // 创建Material Dialog和dialog的内容
            MaterialDialog dialog = new MaterialDialog(mActivity);
            dialog.mTitle = mTitle;
            dialog.mTitleColor = mTitleColor;
            dialog.mIcon = mIcon;
            dialog.mMessage = mMessage;
            dialog.mContentView = mView;
            dialog.mItems = mItems;
            dialog.mPositiveButtonText = mPositiveButtonText;
            dialog.mNegativeButtonText = mNegativeButtonText;
            dialog.mNeutralButtonText = mNeutralButtonText;

            if (mPositiveClickListener == null && mNegativeClickListener == null
                    && mNeutralClickListener == null && mOnClickListener != null) {
                dialog.mPositiveClickListener = mOnClickListener;
                dialog.mNegativeClickListener = mOnClickListener;
                dialog.mNeutralClickListener = mOnClickListener;
            } else {
                dialog.mPositiveClickListener = mPositiveClickListener;
                dialog.mNegativeClickListener = mNegativeClickListener;
                dialog.mNeutralClickListener = mNeutralClickListener;
            }
            dialog.mWhichType = mWhichType;
            dialog.mOnItemClickListener = mOnItemClickListener;
            dialog.mOnSelectedPhotoListener = mOnSelectedPhotoListener;
            dialog.mOnSingleClickListener = mOnSingleClickListener;
            dialog.mOnMultipleClickListener = mOnMultipClickListener;
            dialog.mIContentView = mIContentView;

            // 创建Dialog的属性设置
            DialogDelegate delegate = dialog.mD;
            delegate.mThemeId = mThemeId;

            delegate.mMargin = mMargin;
            delegate.mGravity = mGravity;
            delegate.mAnimator = mAnimator;
            int width = ScreenHelper.getDisplayWidth();
            delegate.mWidthInCenter = isMaterialDesign
                    ? (int) (width * DialogDelegate.MATERIAL_WIDTH)
                    : (int) (width * DialogDelegate.IOS_WIDTH);

            delegate.mBackgroundColor = mBackgroundColor;

            if (mBackgroundRadiusII == null) {
                float rad = mBackgroundRadius == MATERIAL_RADIUS
                        ? isMaterialDesign ? MATERIAL_RADIUS : DEFAULT_RADIUS
                        : mBackgroundRadius;
                delegate.mBackgroundRadius = rad;
                delegate.mBackgroundRadiusII = new float[]{rad, rad, rad, rad};
            } else {
                delegate.mBackgroundRadius = mBackgroundRadius;
                delegate.mBackgroundRadiusII = mBackgroundRadiusII;
            }

            delegate.mDimAccount = mDimAccount;
            delegate.isTouchInOutSideCancel = isTouchInOutSideCancel;
            delegate.isCancelable = isCancelable;
            delegate.isMaterialDesign = isMaterialDesign;

            return dialog;
        }

        /**
         * @param attrId 颜色id(位于 res/values/colors.xml)
         * @return 颜色值
         */
        private int getColor(int attrId) {
            return ThemeHelper.getColor(mActivity, attrId);
        }
    }

    /**
     * see {@link Builder#mPositiveClickListener} or {@link Builder#mNegativeClickListener}
     */
    public abstract static class OnClickListener {
        /**
         * @param dialog 当前显示的Dialog
         * @param which  通过这个判断点击的是哪个按钮
         */
        public void onClick(MaterialDialog dialog, int which) {
        }
    }

    /**
     * see {@link Builder#mOnItemClickListener}
     */
    public interface OnItemClickListener {
        /**
         * @param dialog 当前dialog
         * @param which  通过这个判断点击的位置
         */
        void onClick(MaterialDialog dialog, int which);
    }

    /**
     * @see {@link Builder#mOnSingleClickListener}
     */
    public interface OnSingleClickListener {
        /**
         * @param dialog    当前dialog
         * @param which     通过这个判断点击的位置
         * @param isChecked 当前点击的选项是否选中
         */
        void onClick(MaterialDialog dialog, int which, boolean isChecked);
    }

    /**
     * @see {@link Builder#mOnMultipClickListener}
     */
    public interface OnMultipleClickListener {
        /**
         * @param dialog    当前dialog
         * @param which     通过这个判断点击的位置
         * @param isChecked 当前点击的选项是否选中
         */
        void onClick(MaterialDialog dialog, int which, boolean isChecked);
    }
}
