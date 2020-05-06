package com.dzenm.lib.material;

import android.app.Activity;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckedTextView;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.core.widget.NestedScrollView;

import com.dzenm.lib.drawable.DrawableHelper;
import com.dzenm.lib.os.OsHelper;
import com.dzenm.lib.os.ScreenHelper;
import com.dzenm.lib.os.ThemeHelper;
import com.dzenm.lib.photo.PhotoSelector;

/**
 * @author dzenm
 * @date 2020/4/9 15:13
 * @IDE Android Studio
 */
public class MaterialView {

    /**
     * add dialog in click listener, @see {@link MaterialDialog.OnClickListener} or
     * {@link MaterialDialog.OnItemClickListener} or {@link MaterialDialog.OnSingleClickListener}
     * or {@link MaterialDialog.OnMultipleClickListener}
     */
    private MaterialDialog mMaterialDialog;

    /**
     * get some properity by delegate, @see {@link DialogDelegate}
     */
    private DialogDelegate mD;

    /**
     * dialog fragment' s activity
     */
    private Activity mActivity;

    /**
     * @see {@link MaterialDialog.Builder#mOnItemClickListener}
     */
    private MaterialDialog.OnItemClickListener mOnItemClickListener;

    /**
     * @see {@link MaterialDialog.Builder}
     */
    private MaterialDialog.OnSingleClickListener mOnSingleClickListener;

    /**
     * @see {@link MaterialDialog.Builder}
     */
    private MaterialDialog.OnMultipleClickListener mOnMultipleClickListener;

    public MaterialView(MaterialDialog dialog, DialogDelegate delegate, Activity activity) {
        mMaterialDialog = dialog;
        mD = delegate;
        mActivity = activity;
    }

    /******************************** 添加事件 ****************************************/

    public void setOnItemClickListener(MaterialDialog.OnItemClickListener listener) {
        mOnItemClickListener = listener;
    }

    public void setOnSingleClickListener(MaterialDialog.OnSingleClickListener listener) {
        mOnSingleClickListener = listener;
    }

    public void setOnMultipleClickListener(MaterialDialog.OnMultipleClickListener listener) {
        mOnMultipleClickListener = listener;
    }

    public MaterialDialog.OnItemClickListener getPhotoSelectorListener() {
        return new MaterialDialog.OnItemClickListener() {
            @Override
            public void onClick(MaterialDialog dialog, int which) {
                if (which == 0) {
                    PhotoSelector.getInstance().camera();
                } else if (which == 1) {
                    PhotoSelector.getInstance().gallery();
                } else if (which == 2) {
                    dialog.dismiss();
                }
            }
        };
    }

    public PhotoSelector.OnFinishListener getPhotoSelectorFinishListener() {
        return new PhotoSelector.OnFinishListener() {
            @Override
            public void onFinish(int type) {
                mD.mDialogFragment.dismiss();
            }
        };
    }

    /******************************** 创建Title **************************************/

    /**
     * @param title title text
     * @return title layout
     */
    public ViewGroup createTitleLayout(CharSequence title, int icon, boolean isShowContent) {
        boolean isMaterial = mD.isMaterialDesign;

        LinearLayout titleLayout = new LinearLayout(mActivity);
        titleLayout.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT
        ));
        boolean isShowIcon = TextUtils.isEmpty(title) && icon != 0;
        titleLayout.setPadding(OsHelper.dp2px(24), OsHelper.dp2px(isShowIcon ? 24 : 20),
                OsHelper.dp2px(24), OsHelper.dp2px(isMaterial ? 8 : isShowIcon ? 0 : 16));

        // set android and ios style
        titleLayout.setGravity(isMaterial ? Gravity.CENTER_VERTICAL : Gravity.CENTER);
        titleLayout.setOrientation(isMaterial ? LinearLayout.HORIZONTAL : LinearLayout.VERTICAL);

        // create title view
        TextView titleView = new TextView(mActivity);
        titleView.setTextSize(16);
        titleView.setTextColor(mD.mPrimaryTextColor);
        if (!mD.isMaterialDesign) titleView.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
        titleView.setText(title);

        if (icon != 0) {
            ImageView iconImage = new ImageButton(mActivity);
            int iconSize = OsHelper.dp2px(isMaterial ? 32 : 48);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(iconSize, iconSize);
            if (isMaterial) params.rightMargin = OsHelper.dp2px(8);
            if (!isMaterial && !TextUtils.isEmpty(title)) params.bottomMargin = OsHelper.dp2px(8);
            iconImage.setLayoutParams(params);
            iconImage.setPadding(0, 0, 0, 0);

            iconImage.setScaleType(ImageView.ScaleType.FIT_CENTER);
            iconImage.setImageResource(icon);
            iconImage.setBackground(null);
            titleLayout.addView(iconImage);
        }
        titleView.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT
        ));
        titleLayout.addView(titleView);
        return titleLayout;
    }

    /******************************** 创建Content ViewGroup **************************/

    /**
     * @return scroll layout
     */
    public ViewGroup createScrollLayout() {
        NestedScrollView nestedScrollView = new NestedScrollView(mActivity);
        int height = (int) (ScreenHelper.getDisplayHeight() * 0.6);
        int measureHeight = nestedScrollView.getMeasuredHeight();
        if (measureHeight < height) {
            height = FrameLayout.LayoutParams.WRAP_CONTENT;
        }
        nestedScrollView.setLayoutParams(new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        ));
        return nestedScrollView;
    }

    /**
     * @return content layout
     */
    public ViewGroup createContentLayout() {
        // 设置content view, 最大高度
        FrameLayout contentLayout = new FrameLayout(mActivity);
        int height = (int) (ScreenHelper.getDisplayHeight() * 0.7);
        int measureHeight = contentLayout.getMeasuredHeight();
        if (measureHeight < height) {
            height = ViewGroup.LayoutParams.WRAP_CONTENT;
        }
        contentLayout.setLayoutParams(new NestedScrollView.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                height
        ));
        return contentLayout;
    }

    /******************************** 创建Content *************************************/

    /**
     * @param message message text
     * @return message view
     */
    public View createMessageView(CharSequence message, boolean isShowTitle) {
        TextView messageView = new TextView(mActivity);
        messageView.setLayoutParams(new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        ));
        int top = OsHelper.dp2px(isShowTitle ? 0 : mD.isMaterialDesign ? 20 : 24);
        int h = OsHelper.dp2px(24);
        messageView.setPadding(h, top, h, h);
        messageView.setText(message);
        messageView.setGravity(mD.isMaterialDesign ? Gravity.START : Gravity.CENTER_HORIZONTAL);
        messageView.setTextColor(mD.mSecondaryTextColor);
        return messageView;
    }

    public ViewGroup createMenuLayout(
            final int which, boolean isShowTitle, boolean isShowButton, final CharSequence[] items
    ) {
        LinearLayout menuLayout;
        if (which == MaterialDialog.TYPE_SINGLE) {
            menuLayout = new RadioGroup(mActivity);
        } else {
            menuLayout = new LinearLayout(mActivity);
        }

        menuLayout.setLayoutParams(new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT
        ));
        menuLayout.setOrientation(LinearLayout.VERTICAL);
        // if dialog show in center, add padding in top and botton
        menuLayout.setPadding(0, OsHelper.dp2px(isShowTitle ? 0 : 4),
                0, OsHelper.dp2px(isShowButton ? 0 : 4));

        // add item TextView in layout
        for (int i = 0; i < items.length; i++) {
            TextView itemView = createTextViewType(which);
            createItemView(itemView, which, isShowTitle, i, items);
            itemView.setOnClickListener(getOnClickListener(which, i));
            menuLayout.addView(itemView);
        }
        return menuLayout;
    }

    public ViewGroup createIOSMenuLayout(final int which, boolean isShowTitle,
                                         final CharSequence[] items) {
        LinearLayout menuLayout = new LinearLayout(mActivity);
        menuLayout.setLayoutParams(new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT
        ));
        menuLayout.setOrientation(LinearLayout.VERTICAL);

        // add item TextView in layout
        for (int i = 0; i < items.length; i++) {
            // add divide
            if (i == 0 && isShowTitle) {
                menuLayout.addView(addDivideView());
            } else if (i <= items.length - 1) {
                menuLayout.addView(addDivideView());
            }
            final TextView itemView = new TextView(mActivity);
            createItemView(itemView, which, isShowTitle, i, items);
            itemView.setOnClickListener(getOnClickListener(which, i));

            menuLayout.addView(itemView);
        }
        return menuLayout;
    }

    /******************************** 创建Button ************************************/

    /**
     * @param positiveText     positive button text
     * @param negativeText     negative button text
     * @param neutralText      neutral button text
     * @param positiveListener positive button click listener
     * @param negativeListener negative button click listener
     * @param neutralListener  neutral button click listener
     * @return button layout
     */
    public LinearLayout createButtonLayout(
            CharSequence positiveText, CharSequence negativeText, CharSequence neutralText,
            MaterialDialog.OnClickListener positiveListener,
            MaterialDialog.OnClickListener negativeListener,
            MaterialDialog.OnClickListener neutralListener
    ) {
        LinearLayout buttonLayout = new LinearLayout(mActivity);
        buttonLayout.setOrientation(LinearLayout.HORIZONTAL);
        buttonLayout.setLayoutParams(new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                OsHelper.dp2px(50)
        ));
        if (mD.isMaterialDesign) {
            buttonLayout.setGravity(Gravity.END | Gravity.CENTER_VERTICAL);
            int padding = OsHelper.dp2px(8);
            buttonLayout.setPadding(padding, padding, padding, padding);
        }

        boolean isSingleButton = !TextUtils.isEmpty(positiveText)
                && TextUtils.isEmpty(negativeText)
                && TextUtils.isEmpty(neutralText);
        if (!TextUtils.isEmpty(neutralText) && mD.isMaterialDesign) {
            buttonLayout.addView(createButton(isSingleButton, neutralText, neutralListener,
                    MaterialDialog.OnClickListener.BUTTON_NEUTRAL));
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    0, ViewGroup.LayoutParams.MATCH_PARENT, 1
            );
            View empty = new View(mActivity);
            empty.setLayoutParams(params);
            buttonLayout.addView(empty);
        }

        // 根据positive button和negative button文本不为空时添加按钮
        if (!TextUtils.isEmpty(negativeText)) {
            buttonLayout.addView(createButton(isSingleButton, negativeText, negativeListener,
                    MaterialDialog.OnClickListener.BUTTON_NEGATIVE
            ));
            if (!mD.isMaterialDesign) {
                buttonLayout.addView(createLine(1, ViewGroup.LayoutParams.MATCH_PARENT));
            }
        }
        if (!TextUtils.isEmpty(positiveText)) {
            buttonLayout.addView(createButton(isSingleButton, positiveText, positiveListener,
                    MaterialDialog.OnClickListener.BUTTON_POSITIVE
            ));
        }
        return buttonLayout;
    }

    /**
     * @param isSingleButton 是否显示一个Button
     * @param which          {@link MaterialDialog.OnClickListener#BUTTON_NEGATIVE} or
     *                       {@link MaterialDialog.OnClickListener#BUTTON_POSITIVE}
     * @param buttonText     current button text
     * @param listener       button click listener
     * @return a button
     */
    private TextView createButton(
            boolean isSingleButton, CharSequence buttonText,
            final MaterialDialog.OnClickListener listener, final int which
    ) {
        TextView button = new TextView(mActivity);
        LinearLayout.LayoutParams params;
        int textColor;
        if (mD.isMaterialDesign) {
            // 设置Button大小
            params = new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT
            );
            if (which == MaterialDialog.OnClickListener.BUTTON_POSITIVE) {
                params.leftMargin = OsHelper.dp2px(8);
            }
            // 设置button内边距, 设置点击背景颜色
            button.setMinWidth(OsHelper.dp2px(56));
            int paddingH = OsHelper.dp2px(8);
            button.setPadding(paddingH, 0, paddingH, 0);
            DrawableHelper.radius(mD.mBackgroundRadius)
                    .pressed(mD.mBackgroundColor, mD.mPressedColor)
                    .into(button);
            textColor = mD.mButtonTextColor;
        } else {
            // 设置Button大小
            params = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.MATCH_PARENT, 1f);
            // 设置点击背景颜色
            float[] radius;
            if (which == MaterialDialog.OnClickListener.BUTTON_POSITIVE) {
                if (isSingleButton) {
                    radius = new float[]{0, 0, mD.mBackgroundRadiusII[2], mD.mBackgroundRadiusII[3]};
                } else {
                    radius = new float[]{0, 0, mD.mBackgroundRadiusII[2], 0};
                }
            } else {
                radius = new float[]{0, 0, 0, mD.mBackgroundRadiusII[3]};
            }
            DrawableHelper.radius(radius)
                    .pressed(mD.mBackgroundColor, mD.mPressedColor)
                    .into(button);
            textColor = mD.mButtonTextColor;
        }
        button.setLayoutParams(params);
        button.setGravity(Gravity.CENTER);
        button.setText(buttonText);
        button.setTextColor(textColor);
        if (!mD.isMaterialDesign) button.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onClick(mMaterialDialog, which);
            }
        });
        return button;
    }

    /******************************** 创建Line **************************************/

    /**
     * @param width  line width
     * @param height line height
     * @return a line view
     */
    public View createLine(int width, int height) {
        ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(width, height);
        View line = new View(mActivity);
        line.setBackgroundColor(mD.mDivideColor);
        line.setLayoutParams(params);
        return line;
    }

    /******************************** 辅助方法 **************************************/

    /**
     * create multiple choices type item view
     *
     * @param which item type
     * @return multiple view
     */
    private TextView createTextViewType(int which) {
        TextView itemView;
        if (which == MaterialDialog.TYPE_SINGLE) {
            itemView = new CheckedTextView(mActivity);
            itemView.setCompoundDrawablesWithIntrinsicBounds(ThemeHelper.resolveDrawable(mActivity,
                    android.R.attr.listChoiceIndicatorSingle), null, null, null
            );
            itemView.setCompoundDrawablePadding(OsHelper.dp2px(24));
        } else if (which == MaterialDialog.TYPE_MULTIPLE) {
            itemView = new CheckedTextView(mActivity);
            itemView.setCompoundDrawablesWithIntrinsicBounds(ThemeHelper.resolveDrawable(mActivity,
                    android.R.attr.listChoiceIndicatorMultiple), null, null, null
            );
            itemView.setCompoundDrawablePadding(OsHelper.dp2px(24));
        } else {
            itemView = new TextView(mActivity);
        }
        return itemView;
    }

    /**
     * create a cancel view for ios style list item
     *
     * @return a cancel view
     */
    public ViewGroup createIOSCancelView() {
        FrameLayout cancelLayout = new FrameLayout(mActivity);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT
        );
        params.topMargin = OsHelper.dp2px(8);
        cancelLayout.setLayoutParams(params);
        cancelLayout.setBackground(DrawableHelper.solid(mD.mBackgroundColor)
                .radius(mD.mBackgroundRadius)
                .build());

        TextView cancelView = new TextView(mActivity);
        createItemView(cancelView, 0, false, 0, "取消");
        cancelView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mMaterialDialog.dismiss();
            }
        });

        cancelLayout.addView(cancelView);
        return cancelLayout;
    }

    /**
     * create a multiple choices item view
     *
     * @param itemView    current item view
     * @param which       item type
     * @param isShowTitle title visible
     * @param position    item position
     * @param items       item string
     * @return a item view
     */
    public TextView createItemView(
            TextView itemView, int which, boolean isShowTitle, int position, CharSequence... items
    ) {
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT
        );
        int v = OsHelper.dp2px(mD.isMaterialDesign && which != MaterialDialog.TYPE_ITEM
                ? 8 : mD.isMaterialDesign ? 14 : 16);
        int h = OsHelper.dp2px(24);
        itemView.setPadding(h, v, h, v);
        itemView.setLayoutParams(params);

        int textColor;
        int gravity;
        if (mD.isMaterialDesign) {
            textColor = mD.mPrimaryTextColor;
            gravity = Gravity.START | Gravity.CENTER_VERTICAL;
        } else {
            textColor = mD.mButtonTextColor;
            gravity = Gravity.CENTER;
        }
        itemView.setGravity(gravity);

        itemView.setText(items[position]);
        itemView.setTextColor(textColor);
        itemView.setBackground(getDrawable(isShowTitle, items, position));
        return itemView;
    }

    private View.OnClickListener getOnClickListener(final int which, final int position) {
        return new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (which == MaterialDialog.TYPE_ITEM) {
                            mOnItemClickListener.onClick(mMaterialDialog, position);
                        } else if (which == MaterialDialog.TYPE_SINGLE) {
                            CheckedTextView radio = (CheckedTextView) view;
                            radio.toggle();
                            mOnSingleClickListener.onClick(mMaterialDialog, position, radio.isChecked());
                        } else if (which == MaterialDialog.TYPE_MULTIPLE) {
                            CheckedTextView check = (CheckedTextView) view;
                            check.toggle();
                            mOnMultipleClickListener.onClick(mMaterialDialog, position, check.isChecked());
                        }
                    }
                }, 100);
            }
        };
    }

    /**
     * 设置点击的背景圆角效果
     *
     * @param items    item
     * @param position 位置
     * @return 圆角
     */
    private float[] getItemRadius(boolean isShowTitle, CharSequence[] items, int position) {
        float radius = mD.mBackgroundRadius;
        if (mD.isMaterialDesign) {
            return new float[]{0, 0, 0, 0};
        } else if (isShowTitle) {
            if (position == items.length - 1) {
                // Item length is one
                return new float[]{0, 0, radius, radius};
            } else {
                // if item length is not one, and current item is not first or last
                return new float[]{0, 0, 0, 0};
            }
        } else {
            if (items.length == 1) {
                // Item length is one
                return new float[]{radius, radius,
                        radius, radius};
            } else if (position == 0 && items.length > 1) {
                // if item length is not one, and current item is first
                return new float[]{radius, radius, 0, 0};
            } else if (position == items.length - 1 && items.length > 1) {
                // if item length is not one, and current item is last
                return new float[]{0, 0, radius, radius};
            } else {
                // if item length is not one, and current item is not first or last
                return new float[]{0, 0, 0, 0};
            }
        }
    }

    private Drawable getDrawable(boolean isShowTitle, CharSequence[] items, int position) {
        return DrawableHelper.radius(getItemRadius(isShowTitle, items, position))
                .pressed(mD.mBackgroundColor, mD.mPressedColor)
                .build();
    }

    /**
     * @return divide view
     */
    private View addDivideView() {
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, 1);
        View view = new View(mActivity);
        view.setLayoutParams(params);
        view.setBackgroundColor(mD.mDivideColor);
        return view;
    }
}
