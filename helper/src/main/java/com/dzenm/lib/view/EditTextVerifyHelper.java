package com.dzenm.lib.view;

import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.dzenm.lib.os.OsHelper;

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
public class EditTextVerifyHelper implements OnTextChangeListener, View.OnFocusChangeListener {

    private static final float TRANSLUCENT = 0.3f;  // 透明度
    private static final float OPAQUE = 1;          // 不透明

    /**
     * 根据输入的内容作出相应的动作的View
     */
    private View mObserverView;

    /**
     * 需要监听的EditText
     */
    private EditText[] mObservableEditTexts;

    /**
     * 出现错误的EditText的背景
     */
    private Drawable mBackgroundError = buildDrawable(Color.RED);

    /**
     * 正常的EditText的背景
     */
    private Drawable mBackgroundNormal = buildDrawable(Color.GRAY);

    public static EditTextVerifyHelper newInstance() {
        return new EditTextVerifyHelper();
    }

    /**
     * 设定需要监听的EditText
     *
     * @param editTexts 所有联动的EditText
     * @return this
     */
    public EditTextVerifyHelper addView(@NonNull EditText... editTexts) {
        // 设置EditText的监听事件
        for (EditText edit : editTexts) {
            edit.addTextChangedListener(new CustomEditText(edit, this));
            edit.setOnFocusChangeListener(this);
        }
        mObservableEditTexts = editTexts;
        return this;
    }

    public EditTextVerifyHelper addActionView(@Nullable View view) {
        if (view == null) throw new NullPointerException("the action view is null");
        mObserverView = view;
        setActionViewEnabled(false);
        return this;
    }

    public void setBackgroundError(Drawable backgroundError) {
        mBackgroundError = backgroundError;
    }

    public void setBackgroundNormal(Drawable backgroundNormal) {
        mBackgroundNormal = backgroundNormal;
    }

    /**
     * 验证需要监听的EditText是否全部输入了文本
     *
     * @param reset 是否重置EditText为初始内容
     * @return this
     */
    public boolean verify(boolean reset) {
        if (mObservableEditTexts != null) {
            for (EditText observable : mObservableEditTexts) {
                if (reset) {
                    verifyEditTextInput(false, observable);
                } else {
                    // 验证EditText输入是否为空
                    if (TextUtils.isEmpty(observable.getText().toString())) {
                        verifyEditTextInput(true, observable);
                        return false;
                    } else {
                        verifyEditTextInput(false, observable);
                    }
                }
            }
        }
        return true;
    }

    @Override
    public void onTextChanged(EditText editText, CharSequence s) {
        // 对输入的文本是否改变进行监听
        setEditTextBackgroundState(editText, s.length() == 0);
    }

    @Override
    public void afterTextChanged(Editable s) {
        if (mObservableEditTexts != null) {
            for (EditText observable : mObservableEditTexts) {
                if (TextUtils.isEmpty(observable.getText().toString())) {
                    setActionViewEnabled(false);      // 如果TextView为空，设置不可点击和透明度
                    break;
                }
                setActionViewEnabled(true); // 如果不为空，设置可点击和不透明
            }
        }
    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        if (mObservableEditTexts != null) {
            for (EditText observable : mObservableEditTexts) {
                // 获取焦点时，对获取焦点的EditText是否输入为空进行错误提示，未获取焦点的EditText恢复原样
                setEditTextBackgroundState(observable, observable == v
                        && (hasFocus && ((TextView) v).getText().length() == 0));

            }
        }
    }

    private void setActionViewEnabled(boolean enabled) {
        if (mObserverView != null) {
            mObserverView.setEnabled(enabled);                        // 设置点击
            mObserverView.setAlpha(enabled ? OPAQUE : TRANSLUCENT);   // 设置透明度
        }
    }

    /**
     * 验证输入的内容，并作出相应的反馈
     *
     * @param error    是否出现错误
     * @param editText 校验的EditText
     */
    private void verifyEditTextInput(boolean error, EditText editText) {
        // 设置背景
        setEditTextBackgroundState(editText, error);

        // 设置文本及是否需要获取焦点
        if (error) editText.requestFocus();
    }

    /**
     * @param editText 设置的EditText
     * @param state    设置背景的状态
     */
    private void setEditTextBackgroundState(EditText editText, boolean state) {
        editText.setBackground(state ? mBackgroundError : mBackgroundNormal);
    }

    private Drawable buildDrawable(int strokeColor) {
        GradientDrawable drawable = new GradientDrawable();
        drawable.setShape(GradientDrawable.RECTANGLE);
        drawable.setColor(Color.TRANSPARENT);
        drawable.setCornerRadius(OsHelper.dp2px(2));
        drawable.setStroke(1, strokeColor);
        return drawable;
    }

    private static class CustomEditText implements TextWatcher {

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
            mOnTextChangeListener.afterTextChanged(s);
        }

    }
}