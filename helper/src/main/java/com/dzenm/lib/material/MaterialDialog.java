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

    public static final int TYPE_ITEM = -1;
    public static final int TYPE_SINGLE = -2;
    public static final int TYPE_MULTIPLE = -3;

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
     * title icon, @see {@link Builder}
     */
    private int mIcon;

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
    public OnItemClickListener mOnItemClickListener;

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
     * create view layout @see {@link MaterialView}
     */
    private MaterialView mMaterialView;

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
        mMaterialView = new MaterialView(this, mD, mActivity);

        LinearLayout decorView = getDecorView();
        boolean isShowTitle = !TextUtils.isEmpty(mTitle) || mIcon != 0;
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
                // Multip Menu Dialog
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
        }

        // 添加 Title Layout
        if (isShowTitle) decorView.addView(mMaterialView.createTitleLayout(
                mTitle, mIcon, isShowContent
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

    public static class Builder {

        private AppCompatActivity mActivity;

        private int mThemeId;

        /**
         * dialog标题文本, 如果文本不为空, 显示标题, 否则不显示,
         * 可以通过 {@link #setTitle(int)} 或 {@link #setTitle(CharSequence)} 设置
         */
        private CharSequence mTitle;

        /**
         * dialog标题图标, 如果图标为空, 不显示图标
         */
        private int mIcon;

        /**
         * dialog内容文本, 如果文本不为空, 显示文本, 否则不显示,
         * 如果同时设置了 {@link #mView} , 则优先显示 {@link #mView}, 可以通过
         * {@link #setMessage(int)} 或 {@link #setMessage(CharSequence)} 设置
         */
        private CharSequence mMessage;

        /**
         * 自定义View, 如果同时设置了Message文本, 则优先显示 {@link #mView}
         * 可以通过 {@link #setView(int)} 或 {@link #setView(View)} 设置
         */
        private View mView;

        /**
         * Item 列表 可以通过 {@link #setItem(CharSequence...)} 或 {@link #setItem(int...)} 设置
         */
        private CharSequence[] mItems;

        /**
         * button文本, 可以通过 {@link #setPositiveClickListener(int, OnClickListener)} or
         * {@link #setPositiveClickListener(CharSequence, OnClickListener)}, 设置右边的按钮
         * 或 {@link #setNegativeClickListener(int, OnClickListener)} or
         * {@link #setNegativeClickListener(CharSequence, OnClickListener) 设置左边的按钮}
         */
        private CharSequence mPositiveButtonText, mNegativeButtonText, mNeutralButtonText;

        /**
         * button点击事件, @see {@link #mPositiveButtonText} or {@link #mNegativeButtonText} or
         * {@link #mNegativeButtonText}
         */
        private OnClickListener mOnClickListener, mPositiveClickListener,
                mNegativeClickListener, mNeutralClickListener;

        private int mWhichType;

        /**
         * item click listener, 可以通过 {@link #setOnMultipleClickListener(OnMultipleClickListener
         *)} 设置
         */
        public OnItemClickListener mOnItemClickListener;

        /**
         * photo selected click listener, 可以通过 {@link #setOnSelectedPhotoListener(
         *PhotoSelector.OnSelectedPhotoListener)} 设置
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
         * item single item click listener，可以通过 {@link #setOnSingleClickListener(OnSingleClickListener)}
         * 设置
         */
        private OnSingleClickListener mOnSingleClickListener;

        /**
         * item single item click listener，可以通过 {@link #setOnMultipleClickListener(OnMultipleClickListener)}
         * 设置
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
            mBackgroundColor = getColor(R.attr.colorDialogBackground);
            mBackgroundRadius = MATERIAL_RADIUS;
        }

        public Builder setTitle(CharSequence title) {
            mTitle = title;
            return this;
        }

        public Builder setTitle(int titleId) {
            mTitle = mActivity.getText(titleId);
            return this;
        }

        public Builder setIcon(int icon) {
            mIcon = icon;
            return this;
        }

        public Builder setMessage(CharSequence message) {
            mMessage = message;
            return this;
        }

        public Builder setMessage(int messageId) {
            mMessage = mActivity.getText(messageId);
            return this;
        }

        public Builder setView(View view) {
            mView = view;
            return this;
        }

        public Builder setView(int resId) {
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

        public Builder setItem(int... item) {
            mItems = new CharSequence[item.length];
            for (int i = 0; i < item.length; i++) {
                mItems[i] = mActivity.getText(item[i]);
            }
            return this;
        }

        public Builder setButtonText(int positiveButtonText) {
            mPositiveButtonText = mActivity.getText(positiveButtonText);
            return this;
        }

        public Builder setButtonText(CharSequence positiveButtonText) {
            mPositiveButtonText = positiveButtonText;
            return this;
        }

        public Builder setButtonText(int positiveButtonText, int negativeButtonText) {
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
                int positiveButtonText, int negativeButtonText, int neutralButtonText
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

        public Builder setPositiveClickListener(int buttonText, OnClickListener listener) {
            mPositiveButtonText = mActivity.getText(buttonText);
            mPositiveClickListener = listener;
            return this;
        }

        public Builder setNegativeClickListener(CharSequence buttonText, OnClickListener listener) {
            mNegativeButtonText = buttonText;
            mNegativeClickListener = listener;
            return this;
        }

        public Builder setNegativeClickListener(int buttonText, OnClickListener listener) {
            mNegativeButtonText = mActivity.getText(buttonText);
            mNegativeClickListener = listener;
            return this;
        }

        public Builder setNeutralClickListener(CharSequence buttonText, OnClickListener listener) {
            mNeutralButtonText = buttonText;
            mNeutralClickListener = listener;
            return this;
        }

        public Builder setNeutralClickListener(int buttonText, OnClickListener listener) {
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
        public Builder setBackgroundColor(int backgroundColor) {
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
         * @param primaryColor {@link DialogDelegate#mPrimaryColor}
         * @return this
         */
        public Builder setPrimaryColor(int primaryColor) {
            mPrimaryColor = primaryColor;
            return this;
        }

        /**
         * @param secondaryColor {@link DialogDelegate#mSecondaryColor}
         * @return this
         */
        public Builder setSecondaryColor(int secondaryColor) {
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
            dialog.mIcon = mIcon;
            dialog.mMessage = mMessage;
            dialog.mContentView = mView;
            dialog.mItems = mItems;
            dialog.mPositiveButtonText = mPositiveButtonText;
            dialog.mNegativeButtonText = mNegativeButtonText;
            dialog.mNeutralButtonText = mNeutralButtonText;

            if (mPositiveClickListener == null && mNegativeClickListener == null
                    && mNeutralClickListener == null) {
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

            int width = ScreenHelper.getDisplayWidth();
            delegate.mMargin = mMargin;
            delegate.mWidthInCenter = isMaterialDesign ? (int) (width * 0.8) : (int) (width * 0.7);
            delegate.mGravity = mGravity;
            delegate.mAnimator = mAnimator;

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
        void onClick(MaterialDialog dialog, int which);
    }

    /**
     * see {@link Builder#mOnSingleClickListener}
     */
    public interface OnSingleClickListener {
        void onClick(MaterialDialog dialog, int which, boolean isChecked);
    }

    /**
     * see {@link Builder#mOnMultipClickListener}
     */
    public interface OnMultipleClickListener {
        void onClick(MaterialDialog dialog, int which, boolean isChecked);
    }
}
