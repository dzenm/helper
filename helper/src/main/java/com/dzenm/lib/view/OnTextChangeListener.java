package com.dzenm.lib.view;

import android.text.Editable;
import android.widget.EditText;

/**
 * @author dinzhenyan
 * @date 2019-05-27 20:20
 */
public interface OnTextChangeListener {

    void onTextChanged(EditText editText, CharSequence s);

    void afterTextChanged(Editable s);
}
