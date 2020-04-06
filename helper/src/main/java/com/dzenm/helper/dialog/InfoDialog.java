package com.dzenm.helper.dialog;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.dzenm.helper.R;
import com.dzenm.helper.databinding.DialogInfoBinding;
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

    protected DialogInfoBinding binding;

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

    public String getTitle() {
        return mTitle;
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
     * @return 获取输入的文本
     */
    public String getMessage() {
        return binding.tvMessage.getText().toString();
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
     * @param buttonTextColor 按钮文本颜色
     * @return this
     */
    public InfoDialog setButtonTextColor(int buttonTextColor) {
        mPositiveButtonTextColor = getColor(buttonTextColor);
        mNegativeButtonTextColor = getColor(buttonTextColor);
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

    /************************************* 以下为实现过程 *********************************/
    public InfoDialog(AppCompatActivity activity) {
        super(activity);
    }

    @Override
    public int layoutId() {
        return R.layout.dialog_info;
    }

    @Override
    protected View inflater(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, layoutId(), container, false);
        return binding.getRoot();
    }

    @Override
    public void initView() {
        // 默认设置
        binding.tvPositive.setOnClickListener(this);
        binding.tvNegative.setOnClickListener(this);
        binding.tvTitle.setTextColor(mPrimaryTextColor);
        binding.tvMessage.setTextColor(mSecondaryTextColor);
        binding.tvPositive.setTextColor(mButtonTextColor);
        binding.tvNegative.setTextColor(mButtonTextColor);

        if (isMaterialDesign) {
            binding.tvMessage.setMaxLines(10);
            setMaterialDesignStyle();
        } else {
            setUnMaterialDesignStyle();
        }

        setSingleButtonAndTitleStyle(mTitle);

        setDivideStyle(binding.line1, binding.line2);
        setParamsStyle();
    }

    /**
     * 设置Material Design样式
     */
    private void setMaterialDesignStyle() {
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT, OsHelper.dp2px(36));
        layoutParams.gravity = Gravity.END;
        int marginVertical = OsHelper.dp2px(12);
        int marginHorizontal = OsHelper.dp2px(16);
        layoutParams.setMargins(marginHorizontal, marginVertical, marginHorizontal, marginVertical);
        binding.llButton.setLayoutParams(layoutParams);

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.MATCH_PARENT);
        binding.tvPositive.setLayoutParams(params);
        binding.tvNegative.setLayoutParams(params);

        int paddingHorizontal = OsHelper.dp2px(16);
        binding.tvPositive.setPadding(paddingHorizontal, 0, paddingHorizontal, 0);
        binding.tvNegative.setPadding(paddingHorizontal, 0, paddingHorizontal, 0);

        DrawableHelper.radius(2f).pressed(mPressedColor).into(binding.tvPositive);
        DrawableHelper.radius(2f).pressed(mPressedColor).into(binding.tvNegative);

        binding.tvTitle.setGravity(Gravity.START);
        binding.tvMessage.setGravity(Gravity.START);
        setTitleMargin(16);
        setMessageMargin(8, 16);

        setWidthInCenter((int) (ScreenHelper.getDisplayWidth() * 0.9));
    }

    /**
     * 设置非Material Design样式
     */
    protected void setUnMaterialDesignStyle() {
        binding.tvMessage.post(mRunnable);
        DrawableHelper.radiusBR(mBackgroundRadius)
                .pressed(mPressedColor)
                .into(binding.tvPositive);
        DrawableHelper.radiusBL(mBackgroundRadius)
                .pressed(mPressedColor)
                .into(binding.tvNegative);
        setTitleMargin(24);
        setMessageMargin(20, 32);
    }

    private Runnable mRunnable = new Runnable() {
        @Override
        public void run() {
            // 根据文本内容的长度设置显示的Gravity
            // 因为getLineCount()要返回正确的行数，必须是TextView已经成功绘画到屏幕上。
            // 否则我们可以试验不用post直接在onCreate调用，getLineCount就会出现空指针
            TextView textView = binding.tvMessage;
            if (textView == null || textView.getLayout() == null) return;
            int lineCount = textView.getLayout().getLineCount();
//            textView.setGravity(lineCount > 1 ? Gravity.START : Gravity.CENTER_HORIZONTAL);
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
        int visible = (isMaterialDesign) ? View.GONE : View.VISIBLE;
        if (isSingleButton) {
            visible = View.GONE;
        }
        line_1.setVisibility(visible);
        line_2.setVisibility(visible);
        line_1.setBackgroundColor(mDivideColor);
        line_2.setBackgroundColor(mDivideColor);
    }

    /**
     * 根据风格的不一致, 重新设置标题的margin
     *
     * @param topMargin 顶部的margin
     */
    protected void setTitleMargin(int topMargin) {
        LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) binding.tvTitle.getLayoutParams();
        layoutParams.leftMargin = OsHelper.dp2px(20);
        layoutParams.topMargin = OsHelper.dp2px(topMargin);
        layoutParams.rightMargin = OsHelper.dp2px(20);
        binding.tvTitle.setLayoutParams(layoutParams);
    }

    /**
     * 根据风格的不一致, 重新设置Message的margin
     *
     * @param topMargin    顶部的margin
     * @param bottomMargin 底部的margin
     */
    protected void setMessageMargin(int topMargin, int bottomMargin) {
        LinearLayout.LayoutParams layoutParams =
                (LinearLayout.LayoutParams) binding.tvMessage.getLayoutParams();
        layoutParams.leftMargin = OsHelper.dp2px(20);
        layoutParams.topMargin = OsHelper.dp2px(topMargin);
        layoutParams.rightMargin = OsHelper.dp2px(20);
        layoutParams.bottomMargin = OsHelper.dp2px(bottomMargin);
        binding.tvMessage.setLayoutParams(layoutParams);
    }

    /**
     * 设置只显示一个按钮和标题的风格
     */
    protected void setSingleButtonAndTitleStyle(String title) {
        // 单选按钮时需要修改按钮的圆角背景
        if (isSingleButton) {
            binding.tvNegative.setVisibility(View.GONE);
            binding.line2.setVisibility(View.GONE);
            if (!isMaterialDesign) {
                binding.tvNegative.setVisibility(View.GONE);
                DrawableHelper.radiusBL(mBackgroundRadius)
                        .radiusBR(mBackgroundRadius)
                        .pressed(mPrimaryColor, mSecondaryColor)
                        .textColor(android.R.color.white, android.R.color.white)
                        .into(binding.tvPositive);
                setTitleMargin(32);
                setMessageMargin(20, 32);
            }
        }

        // 设置标题的风格, 标题为空时, 隐藏标题, 重新设置margin
        // 设置标题图片时, 没有标题文本时, 直接设置图片, 有文本时, 设置在文本的前面
        if (null == title) {
            if (isMaterialDesign) {
                binding.tvTitle.setText(getString(R.string.dialog_info));
            } else {
                setMessageMargin(40, 36);
                binding.tvTitle.setVisibility(View.GONE);
                isDivide = true;
            }
        } else {
            binding.tvTitle.setText(title);
        }
        if (mTitleIcon != -1 && !isMaterialDesign) {
            binding.tvTitle.setVisibility(View.GONE);
            ImageView ivTitleIcon = findViewById(R.id.iv_title_icon);
            ivTitleIcon.setVisibility(View.VISIBLE);
            ivTitleIcon.setImageResource(mTitleIcon);
            setMessageMargin(16, 24);
        }
    }

    protected void setParamsStyle() {
        // 内容、按钮文本
        if (!TextUtils.isEmpty(mMessage)) {
            binding.tvMessage.setText(mMessage);
        }
        if (!TextUtils.isEmpty(mPositiveButtonText)) {
            binding.tvPositive.setText(mPositiveButtonText);
        }
        if (!TextUtils.isEmpty(mNegativeButtonText)) {
            binding.tvNegative.setText(mNegativeButtonText);
        }

        if (mPositiveButtonTextColor != -1) {
            binding.tvPositive.setTextColor(mPositiveButtonTextColor);
        }
        if (mNegativeButtonTextColor != -1) {
            binding.tvNegative.setTextColor(mNegativeButtonTextColor);
        }
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
