package com.dzenm.helper.dialog;

import android.annotation.SuppressLint;
import android.graphics.PorterDuff;
import android.graphics.drawable.GradientDrawable;
import androidx.appcompat.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.dzenm.helper.R;
import com.dzenm.helper.draw.BackGHelper;
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
 *      .setBackground(BackGHelper.solid(android.R.color.holo_green_light).radius(8).build())
 *      .setOnDialogClickListener(new EditDialog.OnDialogClickListener<EditDialog>() {
 *          @Override
 *          public boolean onClick(EditDialog dialog, boolean confirm) {
 *              if (confirm) {
 *                   Toa.show("点击了确定", R.drawable.prompt_warming);
 *              } else {
 *                   Toa.show("点击了取消", R.drawable.prompt_warming);
 *              }
 *              return true;
 *          }
 *      }).setTranslucent(true)
 *      .show();
 * </pre>
 */
@SuppressLint("ValidFragment")
public class EditDialog extends InfoDialog implements TextWatcher {

    private EditText editText;

    /************************************* 以下为自定义方法 *********************************/

    public static EditDialog newInstance(AppCompatActivity activity) {
        return new EditDialog(activity);
    }

    public EditDialog(AppCompatActivity activity) {
        super(activity);
    }

    @Override
    public String getMessage() {
        return editText.getText().toString();
    }

    /************************************* 以下为实现过程 *********************************/

    @Override
    protected void initView() {
        super.initView();

        editText = findViewById(R.id.et_message);
        tvMessage.setVisibility(View.GONE);
        editText.setVisibility(View.VISIBLE);

        if (isShowCenter()) {
            setEditTextMessageLayoutParams(isDivide ? 16 : 8, isDivide ? 24 : 16);
        } else {
            setEditTextMessageLayoutParams(isDivide ? 20 : 16, 32);
        }

        int color = isDefaultBackground ? R.color.colorDivideDark : android.R.color.white;
        if (isMaterialDesign || !isDivide) {
            setEditUnderLineStyle(editText, getColor(color));
        } else {
            BackGHelper.solid(android.R.color.transparent).radius(2f).stroke(0.5f, color).into(editText);
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
        LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) editText.getLayoutParams();
        layoutParams.leftMargin = OsHelper.dp2px(20);
        layoutParams.topMargin = OsHelper.dp2px(topMargin);
        layoutParams.rightMargin = OsHelper.dp2px(20);
        layoutParams.bottomMargin = OsHelper.dp2px(bottomMargin);
        editText.setLayoutParams(layoutParams);
    }

    @Override
    protected void setSingleButtonAndTitleStyle(TextView tvTitle, TextView tvNegative, TextView tvPositive, String title) {
        super.setSingleButtonAndTitleStyle(tvTitle, tvNegative, tvPositive, title);
        if (title == null) {
            tvTitle.setText(getStrings(R.string.dialog_info));
            tvTitle.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        if (editText.getText().toString().trim().length() == 0) { // 监听输入框，没有内容时设置提示文字
            editText.setHint(R.string.dialog_edit_hint);
            tvPositive.setEnabled(false);
        } else {
            tvPositive.setEnabled(true);
        }
    }

    @Override
    public void afterTextChanged(Editable s) {

    }

    public interface OnEditClickListener extends OnDialogClickListener<EditDialog> {
    }
}
