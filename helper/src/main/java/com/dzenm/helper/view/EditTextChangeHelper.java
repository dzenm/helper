package com.dzenm.helper.view;

import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.dzenm.helper.R;

/**
 * @author dinzhenyan
 * @date 2019-05-26 17:40
 * 验证输入框的内容
 * <pre>
 * EditTextChangeHelper mEditTextChangeHelper = EditTextChangeHelper.newInstance();
 * // 校验多个EditText是否输入了文本
 * mEditTextChangeHelper.setEditText(etUsername, etPassword, etVerifyCode)
 *       .verify(true);
 * mEditTextChangeHelper.verify(false)
 * </pre>
 */
public class EditTextChangeHelper implements OnTextChangeListener, View.OnFocusChangeListener {

    /*
     * 需要监听的EditText
     */
    private EditText[] mEditTexts;

    /*
     * EditText的hint文本
     */
    private String[] mTexts;

    /*
     * 出现错误的EditText的背景
     */
    private int backgroundError = R.drawable.bg_red_border;
    /*
     * 正常的EditText的背景
     */
    private int backgroundNormal = R.drawable.bg_gray_border;

    public static EditTextChangeHelper newInstance() {
        return new EditTextChangeHelper();
    }

    /**
     * 设定需要监听的EditText
     *
     * @param editTexts 所有联动的EditText
     * @return this
     */
    public EditTextChangeHelper addView(@NonNull EditText... editTexts) {
        // 设置EditText的监听事件
        for (EditText edit : editTexts) {
            edit.addTextChangedListener(new CustomEditText(edit, this));
            edit.setOnFocusChangeListener(this);
        }
        mEditTexts = editTexts;
        return this;
    }

    /**
     * 设置hint文本
     *
     * @param texts 提示文本内容
     * @return this
     */
    public EditTextChangeHelper addText(@NonNull String... texts) {
        mTexts = texts;
        return this;
    }

    /**
     * 验证需要监听的EditText是否全部输入了文本
     *
     * @param reset 是否重置EditText为初始内容
     * @return this
     */
    public boolean verify(boolean reset) {
        for (int i = 0; i < mEditTexts.length; i++) {
            EditText editText = mEditTexts[i];
            String text = null;

            // hint可为空
            if (mTexts != null) {
                text = mTexts[i];
            }

            if (reset) {
                verify(false, editText, text);
            } else {
                // 验证EditText输入是否为空
                if (TextUtils.isEmpty(editText.getText().toString())) {
                    verify(true, editText, text);
                    return false;
                } else {
                    verify(false, editText, text);
                }
            }
        }
        return true;
    }

    /**
     * 验证输入的内容，并作出相应的反馈
     *
     * @param error    是否出现错误
     * @param editText 校验的EditText
     * @param texts    校验后的文本内容
     */
    private void verify(boolean error, EditText editText, String texts) {
        // 设置背景
        setBackgroundState(error, editText);

        // 设置文本及是否需要获取焦点
        if (!TextUtils.isEmpty(texts)) editText.setHint(texts);
        if (error) editText.requestFocus();
    }

    @Override
    public void onTextChanged(EditText editText, CharSequence s) {
        // 对输入的文本是否改变进行监听
        setBackgroundState(s.length() == 0, editText);
    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        for (EditText editText : mEditTexts) {
            // 获取焦点时，对获取焦点的EditText是否输入为空进行错误提示，未获取焦点的EditText恢复原样
            if (editText != v) {
                setBackgroundState(false, editText);
            } else {
                setBackgroundState(hasFocus && ((TextView) v).getText().length() == 0, editText);
            }
        }
    }

    /**
     * @param state    设置背景的状态
     * @param editText 设置的EditText
     */
    private void setBackgroundState(boolean state, EditText editText) {
        editText.setBackgroundResource(state ? backgroundError : backgroundNormal);
    }

    private class CustomEditText implements TextWatcher {

        OnTextChangeListener mOnTextChangeListener;
        EditText mEditText;

        private CustomEditText(EditText editText, OnTextChangeListener onTextChangeListener) {
            mEditText = editText;
            mOnTextChangeListener = onTextChangeListener;
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            mOnTextChangeListener.onTextChanged(mEditText, s);
        }

        @Override
        public void afterTextChanged(Editable s) {

        }

    }
}