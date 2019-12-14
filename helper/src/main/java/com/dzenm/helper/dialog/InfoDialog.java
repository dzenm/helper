package com.dzenm.helper.dialog;

import android.annotation.SuppressLint;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.dzenm.helper.R;
import com.dzenm.helper.draw.DrawableHelper;
import com.dzenm.helper.os.OsHelper;
import com.dzenm.helper.os.ScreenHelper;

/**
 * @author dinzhenyan
 * @date 2019-05-19 18:02
 * 提示对话框，仅用于弹出对话框进行提示
 * <pre>
 * InfoDialog.newInstance(this)
 *      .setTitle("标题")
 *      .setMessage("红色背景提示框")
 *      .setBackground(DrawableHelper.solid(android.R.color.holo_red_light).radius(8).build())
 *      .setOnDialogClickListener(new InfoDialog.OnDialogClickListener<InfoDialog>() {
 *          @Override
 *          public boolean onClick(InfoDialog dialog, boolean confirm) {
 *              if (confirm) {
 *                  ToastHelper.show("点击了确定", R.drawable.prompt_warming);
 *              } else {
 *                  ToastHelper.show("点击了取消", R.drawable.prompt_warming);
 *              }
 *              return true;
 *          }
 *      }).setTouchInOutSideCancel(true)
 *      .setTranslucent(true)
 *      .setMargin(20)
 *      .setGravity(Gravity.BOTTOM)
 *      .show();
 * </pre>
 */
@SuppressLint("ValidFragment")
public class InfoDialog extends AbsDialogFragment implements View.OnClickListener {

    /**
     * 提示框的标题 {@link #setTitle(int)}  or {@link #setTitle(String)})}
     */
    private String mTitle;

    /**
     * 提示框的标题图标 {@link #setTitleIcon(int)}
     */
    private int mTitleIcon = -1;

    /**
     * 提示框的内容 {@link #setMessage(String)}
     */
    private String mMessage;

    /**
     * 按钮的文本 {@link #setButtonText(String)}
     */
    private String mPositiveButtonText, mNegativeButtonText;

    /**
     * 是否只显示一个按钮 {@link #setButtonTextColor(int, int)}
     */
    private boolean isSingleButton = false;

    /**
     * 按钮的文本颜色
     */
    private int mPositiveButtonTextColor = -1, mNegativeButtonTextColor = -1;

    protected TextView tvMessage;

    protected TextView tvPositive;

    /************************************* 以下为自定义方法 *********************************/

    public static InfoDialog newInstance(AppCompatActivity activity) {
        return new InfoDialog(activity);
    }

    /**
     * @param title 提示的标题文本
     * @return this
     */
    public InfoDialog setTitle(String title) {
        mTitle = title;
        return this;
    }

    /**
     * @param resId 提示的标题文本
     * @return this
     */
    public InfoDialog setTitle(int resId) {
        mTitle = getString(resId);
        return this;
    }

    /**
     * @param titleIcon 提示标题的图标
     * @return this
     */
    public InfoDialog setTitleIcon(int titleIcon) {
        mTitleIcon = titleIcon;
        return this;
    }

    /**
     * @param message 提示的内容文本
     * @return this
     */
    public InfoDialog setMessage(String message) {
        mMessage = message;
        return this;
    }

    /**
     * @param resId 提示的内容文本
     * @return this
     */
    public InfoDialog setMessage(int resId) {
        mMessage = getStrings(resId);
        return this;
    }

    /**
     * @param positiveButtonText 按钮文本(只设置一个点击按钮)
     * @return this
     */
    public InfoDialog setButtonText(String positiveButtonText) {
        mPositiveButtonText = positiveButtonText;
        isSingleButton = true;
        return this;
    }

    /**
     * @param positiveButtonText 右边的确定按钮文本
     * @param negativeButtonText 左边的取消按钮文本
     * @return this
     */
    public InfoDialog setButtonText(String positiveButtonText, String negativeButtonText) {
        mPositiveButtonText = positiveButtonText;
        mNegativeButtonText = negativeButtonText;
        isSingleButton = false;
        return this;
    }

    /**
     * @param positiveButtonTextColor 右边的确定按钮文本颜色
     * @param negativeButtonTextColor 左边的取消按钮文本颜色
     * @return this
     */
    public InfoDialog setButtonTextColor(int positiveButtonTextColor, int negativeButtonTextColor) {
        mPositiveButtonTextColor = getColor(positiveButtonTextColor);
        mNegativeButtonTextColor = getColor(negativeButtonTextColor);
        return this;
    }

    /**
     * @param buttonTextColor 按钮文本颜色
     * @return this
     */
    public InfoDialog setButtonTextColor(int buttonTextColor) {
        mPositiveButtonTextColor = getColor(buttonTextColor);
        mNegativeButtonTextColor = getColor(buttonTextColor);
        return this;
    }

    /**
     * @return 获取输入的文本
     */
    public String getMessage() {
        return tvMessage.getText().toString();
    }

    @Override
    public InfoDialog setMargin(int margin) {
        return super.setMargin(margin);
    }

    @Override
    public InfoDialog setGravity(int gravity) {
        return super.setGravity(gravity);
    }

    @Override
    public InfoDialog setAnimator(int animator) {
        return super.setAnimator(animator);
    }

    @Override
    public InfoDialog setBackground(Drawable background) {
        return super.setBackground(background);
    }

    @Override
    public InfoDialog setCenterWidth(int width) {
        return super.setCenterWidth(width);
    }

    @Override
    public InfoDialog setPrimaryColor(int primaryColor) {
        return super.setPrimaryColor(primaryColor);
    }

    @Override
    public InfoDialog setSecondaryColor(int secondaryColor) {
        return super.setSecondaryColor(secondaryColor);
    }

    @Override
    public InfoDialog setTranslucent(boolean translucent) {
        return super.setTranslucent(translucent);
    }

    @Override
    public InfoDialog setDimAccount(float dimAccount) {
        return super.setDimAccount(dimAccount);
    }

    @Override
    public InfoDialog setCancel(boolean cancel) {
        return super.setCancel(cancel);
    }

    @Override
    public InfoDialog setTouchInOutSideCancel(boolean cancel) {
        return super.setTouchInOutSideCancel(cancel);
    }

    @Override
    public InfoDialog setDivide(boolean divide) {
        return super.setDivide(divide);
    }

    @Override
    public InfoDialog setMaterialDesign(boolean materialDesign) {
        return super.setMaterialDesign(materialDesign);
    }

    @Override
    public InfoDialog setRadiusCard(float radiusCard) {
        return super.setRadiusCard(radiusCard);
    }

    @Override
    public InfoDialog setOnClickListener(OnClickListener onClickListener) {
        return super.setOnClickListener(onClickListener);
    }

    /************************************* 以下为实现过程 *********************************/
    public InfoDialog(AppCompatActivity activity) {
        super(activity);
    }

    @Override
    protected int layoutId() {
        return R.layout.dialog_info;
    }

    @Override
    protected void initView() {
        tvMessage = findViewById(R.id.tv_message);
        tvPositive = findViewById(R.id.tv_positive);
        TextView tvNegative = findViewById(R.id.tv_negative);
        TextView tvTitle = findViewById(R.id.tv_title);
        View line_1 = findViewById(R.id.line_1);
        View line_2 = findViewById(R.id.line_2);

        // 默认设置
        tvPositive.setOnClickListener(this);
        tvNegative.setOnClickListener(this);
        tvTitle.setTextColor(mPrimaryTextColor);
        tvMessage.setTextColor(mPrimaryTextColor);
        tvPositive.setTextColor(mSecondaryTextColor);
        tvNegative.setTextColor(mSecondaryTextColor);

        if (isMaterialDesign) {
            setMaterialDesignStyle(tvTitle, tvPositive, tvNegative);
            tvMessage.setMaxLines(10);
        } else {
            setUnMaterialDesignStyle(tvTitle, tvPositive, tvNegative);
        }

        setSingleButtonAndTitleStyle(tvTitle, tvPositive, tvNegative, mTitle);
        setDivideStyle(line_1, line_2);

        setDiyParamsStyle(tvNegative);
    }

    /**
     * 设置Material Design样式
     *
     * @param tvTitle    标题
     * @param tvPositive 右边的按钮
     * @param tvNegative 左边的按钮
     */
    protected void setMaterialDesignStyle(TextView tvTitle, TextView tvPositive, TextView tvNegative) {
        LinearLayout linearLayout = findViewById(R.id.ll_button);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT, OsHelper.dp2px(36));
        layoutParams.gravity = Gravity.END;
        int marginVertical = OsHelper.dp2px(8);
        int marginHorizontal = OsHelper.dp2px(8);
        layoutParams.setMargins(marginHorizontal, marginVertical, marginHorizontal, marginVertical);
        linearLayout.setLayoutParams(layoutParams);
        linearLayout.setOrientation(LinearLayout.HORIZONTAL);

        LinearLayout.LayoutParams positiveParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.MATCH_PARENT);
        tvPositive.setLayoutParams(positiveParams);
        int paddingHorizontal = OsHelper.dp2px(16);

        tvPositive.setPadding(paddingHorizontal, 0, paddingHorizontal, 0);
        tvPositive.setGravity(Gravity.CENTER);

        LinearLayout.LayoutParams negativeParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.MATCH_PARENT);
        tvNegative.setLayoutParams(negativeParams);

        tvNegative.setPadding(paddingHorizontal, 0, paddingHorizontal, 0);
        tvNegative.setGravity(Gravity.CENTER);

        DrawableHelper.radius(2f).pressed(mPressedColor).into(tvPositive);
        DrawableHelper.radius(2f).pressed(mPressedColor).into(tvNegative);

        tvTitle.setGravity(Gravity.START);
        tvMessage.setGravity(Gravity.START);
        setTitleMargin(tvTitle, 20);
        setMessageMargin(8, 24);
        mCenterWidth = (int) (ScreenHelper.getDisplayWidth() * 0.9);
    }

    /**
     * 设置非Material Design样式
     *
     * @param tvTitle    标题
     * @param tvPositive 右边的按钮
     * @param tvNegative 左边的按钮
     */
    protected void setUnMaterialDesignStyle(TextView tvTitle, TextView tvPositive, TextView tvNegative) {
        tvTitle.setGravity(Gravity.CENTER);
        tvMessage.post(mRunnable);
        DrawableHelper.radiusBR(mRadiusCard)
                .pressed(mPressedColor)
                .into(tvPositive);
        DrawableHelper.radiusBL(mRadiusCard)
                .pressed(mPressedColor)
                .into(tvNegative);
        setTitleMargin(tvTitle, 24);
        setMessageMargin(16, 24);
    }

    /**
     * 根据文本内容的长度设置显示的Gravity
     */
    private Runnable mRunnable = new Runnable() {
        @Override
        public void run() {
            // 因为getLineCount()要返回正确的行数，必须是TextView已经成功绘画到屏幕上。
            // 否则我们可以试验不用post直接在onCreate调用，getLineCount就会出现空指针
            if (tvMessage == null || tvMessage.getLayout() == null) return;
            int lineCount = tvMessage.getLayout().getLineCount();
            tvMessage.setGravity(lineCount > 1 ? Gravity.START : Gravity.CENTER_HORIZONTAL);
        }
    };

    /**
     * 根据是否有分割线设置分割线的颜色
     *
     * @param line_1 分割线
     * @param line_2 分割线
     */
    protected void setDivideStyle(View line_1, View line_2) {
        // 设置是否带分割线的风格
        int divideColor = isDefaultBackground ? R.color.colorDivideDark : android.R.color.white;
        line_1.setVisibility(View.GONE);
        line_2.setVisibility(View.GONE);
        if (!isMaterialDesign && isDivide) {
            line_1.setVisibility(View.VISIBLE);
            line_2.setVisibility(View.VISIBLE);
            line_1.setBackgroundColor(getColor(divideColor));
            line_2.setBackgroundColor(getColor(divideColor));
        }
    }

    /**
     * 根据风格的不一致, 重新设置标题的margin
     *
     * @param tvTitle   标题TextView
     * @param topMargin 顶部的margin
     */
    protected void setTitleMargin(TextView tvTitle, int topMargin) {
        LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) tvTitle.getLayoutParams();
        layoutParams.leftMargin = OsHelper.dp2px(24);
        layoutParams.topMargin = OsHelper.dp2px(topMargin);
        layoutParams.rightMargin = OsHelper.dp2px(24);
        tvTitle.setLayoutParams(layoutParams);
    }

    /**
     * 根据风格的不一致, 重新设置Message的margin
     *
     * @param topMargin    顶部的margin
     * @param bottomMargin 底部的margin
     */
    protected void setMessageMargin(int topMargin, int bottomMargin) {
        LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) tvMessage.getLayoutParams();
        layoutParams.leftMargin = OsHelper.dp2px(24);
        layoutParams.topMargin = OsHelper.dp2px(topMargin);
        layoutParams.rightMargin = OsHelper.dp2px(24);
        layoutParams.bottomMargin = OsHelper.dp2px(bottomMargin);
        tvMessage.setLayoutParams(layoutParams);
    }

    /**
     * 设置只显示一个按钮和标题的风格
     *
     * @param tvTitle    标题
     * @param tvNegative 右边的按钮
     */
    protected void setSingleButtonAndTitleStyle(TextView tvTitle, TextView tvPositive,
                                                TextView tvNegative, String title) {
        // 单选按钮时需要修改按钮的圆角背景
        if (isSingleButton) {
            tvNegative.setVisibility(View.GONE);
            if (!isMaterialDesign) {
                tvNegative.setVisibility(View.GONE);
                DrawableHelper.radiusBL(mRadiusCard)
                        .radiusBR(mRadiusCard)
                        .pressed(mPrimaryColor, mSecondaryColor)
                        .textColor(android.R.color.white, android.R.color.white)
                        .into(tvPositive);
                setTitleMargin(tvTitle, 32);
                setMessageMargin(20, 32);
            }
        }

        // 设置标题的风格, 标题为空时, 隐藏标题, 重新设置margin
        // 设置标题图片时, 没有标题文本时, 直接设置图片, 有文本时, 设置在文本的前面
        if (null == title) {
            if (isMaterialDesign) {
                tvTitle.setText(getString(R.string.dialog_info));
            } else {
                setMessageMargin(40, 36);
                tvTitle.setVisibility(View.GONE);
                isDivide = true;
            }
        } else {
            tvTitle.setText(title);
        }
        if (mTitleIcon != -1 && !isMaterialDesign) {
            tvTitle.setVisibility(View.GONE);
            ImageView ivTitleIcon = findViewById(R.id.iv_title_icon);
            ivTitleIcon.setVisibility(View.VISIBLE);
            ivTitleIcon.setImageResource(mTitleIcon);
            setMessageMargin(16, 24);
        }
    }

    protected void setDiyParamsStyle(TextView tvNegative) {
        // 内容、按钮文本
        if (!TextUtils.isEmpty(mMessage)) tvMessage.setText(mMessage);
        if (!TextUtils.isEmpty(mPositiveButtonText)) tvPositive.setText(mPositiveButtonText);
        if (!TextUtils.isEmpty(mNegativeButtonText)) tvNegative.setText(mNegativeButtonText);

        if (mPositiveButtonTextColor != -1) tvPositive.setTextColor(mPositiveButtonTextColor);
        if (mNegativeButtonTextColor != -1) tvNegative.setTextColor(mNegativeButtonTextColor);
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.tv_positive) {
            if (mOnClickListener == null) {
                dismiss();
            } else {
                if (mOnClickListener.onClick(this, true)) dismiss();
            }
        } else if (view.getId() == R.id.tv_negative) {
            if (mOnClickListener == null) {
                dismiss();
            } else {
                if (mOnClickListener.onClick(this, false)) dismiss();
            }
        }
    }

    public abstract static class OnInfoClickListener extends OnClickListener<InfoDialog> {
    }
}
