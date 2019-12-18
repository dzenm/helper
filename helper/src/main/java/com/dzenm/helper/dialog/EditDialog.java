package com.dzenm.helper.dialog;

import android.graphics.PorterDuff;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.dzenm.helper.R;
import com.dzenm.helper.draw.DrawableHelper;
import com.dzenm.helper.os.OsHelper;
import com.dzenm.helper.view.EditText;

/**
 * @author dinzhenyan
 * @date 2019-05-19 21:26
 * 可编辑的对话框
 * <pre>
 * EditDialog.newInstance(this)
 *      .setTitle("测试")
 *      .setMessage("是否打开")
 *      .setDivide(false)
 *      .setBackground(DrawableHelper.solid(android.R.color.holo_green_light).radius(8).build())
 *      .setOnDialogClickListener(new EditDialog.OnDialogClickListener<EditDialog>() {
 *          @Override
 *          public boolean onClick(EditDialog dialog, boolean confirm) {
 *              if (confirm) {
 *                   ToastHelper.show("点击了确定", R.drawable.prompt_warming);
 *              } else {
 *                   ToastHelper.show("点击了取消", R.drawable.prompt_warming);
 *              }
 *              return true;
 *          }
 *      }).show();
 * </pre>
 */
public class EditDialog extends InfoDialog implements TextWatcher {

    /************************************* 以下为自定义方法 *********************************/

    public static EditDialog newInstance(AppCompatActivity activity) {
        return new EditDialog(activity);
    }

    public EditDialog(AppCompatActivity activity) {
        super(activity);
    }

    @Override
    public String getMessage() {
        return binding.etMessage.getText().toString();
    }

    /************************************* 以下为实现过程 *********************************/

    @Override
    protected void initView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState2) {
        super.initView(inflater, container, savedInstanceState2);
        binding.tvMessage.setVisibility(View.GONE);
        EditText editText = binding.etMessage;
        editText.setVisibility(View.VISIBLE);

        if (isShowCenter()) {
            setEditTextMessageLayoutParams(isDivide ? 16 : 8, isDivide ? 24 : 16);
        } else {
            setEditTextMessageLayoutParams(isDivide ? 20 : 16, 32);
        }

        int color = isDefaultBackground ? R.color.colorDivideDark : R.color.colorDivideLight;
        if (isMaterialDesign || !isDivide) {
            setEditUnderLineStyle(editText, getColor(color));
        } else {
            DrawableHelper.solid(android.R.color.transparent).radius(2f).stroke(0.5f, color).into(editText);
        }
        setEditCursorDrawable(editText, getColor(color));

        editText.requestFocus();                             // 获取焦点
        editText.setHint(R.string.dialog_edit_hint);         // 设置提示文字
        editText.setSelection(editText.getText().length());  // 如果有内容将光标移到最后面
        editText.addTextChangedListener(this);
    }

    /**
     * 设置EditText下划线颜色
     */
    private void setEditUnderLineStyle(EditText editText, int color) {
        editText.getBackground()
                .mutate()
                .setColorFilter(color, PorterDuff.Mode.SRC_ATOP);
    }

    /**
     * 设置光标样式
     */
    private void setEditCursorDrawable(EditText editText, int color) {
        // 设置EditText光标颜色
        GradientDrawable drawable = (GradientDrawable) mActivity.getDrawable(R.drawable.edit_cursor_drawable);
        assert drawable != null;
        drawable.setColor(color);
        drawable.setSize(OsHelper.dp2px(2), -1);

        // 设置提示文字颜色
        editText.setHintTextColor(mHintColor);
    }

    private void setEditTextMessageLayoutParams(int topMargin, int bottomMargin) {
        LinearLayout.LayoutParams layoutParams =
                (LinearLayout.LayoutParams) binding.etMessage.getLayoutParams();
        layoutParams.leftMargin = OsHelper.dp2px(20);
        layoutParams.topMargin = OsHelper.dp2px(topMargin);
        layoutParams.rightMargin = OsHelper.dp2px(20);
        layoutParams.bottomMargin = OsHelper.dp2px(bottomMargin);
        binding.etMessage.setLayoutParams(layoutParams);
    }

    @Override
    protected void setSingleButtonAndTitleStyle(String title) {
        super.setSingleButtonAndTitleStyle(title);
        if (title == null) {
            binding.tvTitle.setText(getStrings(R.string.dialog_info));
            binding.tvTitle.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        if (binding.etMessage.getText().toString().trim().length() == 0) { // 监听输入框，没有内容时设置提示文字
            binding.etMessage.setHint(R.string.dialog_edit_hint);
            binding.tvPositive.setEnabled(false);
        } else {
            binding.tvPositive.setEnabled(true);
        }
    }

    @Override
    public void afterTextChanged(Editable s) {

    }

    public abstract static class OnEditClickListener extends OnClickListener<EditDialog> {
    }
}
