package com.dzenm.lib.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatEditText;

import com.dzenm.lib.R;

import java.util.ArrayList;
import java.util.List;

/**
 * @author dzenm
 * @date 2020/5/4 下午2:12
 * <pre>
 *                  <com.dzenm.lib.view.EditTextLayout
 *                     android:id="@+id/edit_layout"
 *                     android:layout_width="match_parent"
 *                     android:layout_height="wrap_content"
 *                     android:layout_marginTop="@dimen/margin_normal"
 *                     android:layout_marginBottom="@dimen/margin_normal"
 *                     android:orientation="vertical">
 *
 *                     <com.dzenm.lib.view.EditText
 *                         android:id="@+id/edit_text1"
 *                         android:layout_width="match_parent"
 *                         android:layout_height="wrap_content" />
 *
 *                     <com.dzenm.lib.view.EditText
 *                         android:id="@+id/edit_text2"
 *                         android:layout_width="match_parent"
 *                         android:layout_height="wrap_content"
 *                         android:layout_marginTop="@dimen/margin_normal"
 *                         android:layout_marginBottom="@dimen/margin_normal" />
 *
 *                     <com.dzenm.lib.view.EditText
 *                         android:id="@+id/edit_text3"
 *                         android:layout_width="match_parent"
 *                         android:layout_height="wrap_content" />
 *
 *                     <Button
 *                         android:id="@+id/btn_login"
 *                         android:layout_width="match_parent"
 *                         android:layout_height="wrap_content"
 *                         android:layout_marginTop="@dimen/margin_normal"
 *                         android:layout_marginBottom="@dimen/margin_normal"
 *                         android:backgroundTint="@android:color/holo_purple"
 *                         android:text="登录"
 *                         android:textColor="@android:color/white" />
 *                 </com.dzenm.lib.view.EditTextLayout>
 * </pre>
 */
public class EditTextLayout extends LinearLayout {

    private EditTextVerifyHelper mEditTextVerifyHelper;
    private int mBackgroundNormal, mBackgroundError;
    private boolean isEnabledEmptyVerify;
    private View mActionView;

    public EditTextLayout(Context context) {
        this(context, null);
    }

    public EditTextLayout(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public EditTextLayout(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        TypedArray a = context.obtainStyledAttributes(R.styleable.EditTextLayout);
        mBackgroundNormal = a.getResourceId(R.styleable.EditTextLayout_backgroundNormal, 0);
        mBackgroundError = a.getInt(R.styleable.EditTextLayout_backgroundError, 0);
        isEnabledEmptyVerify = a.getBoolean(R.styleable.EditTextLayout_isEnabledEmptyVerity, true);
        a.recycle();
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        if (isEnabledEmptyVerify) {
            mEditTextVerifyHelper = EditTextVerifyHelper.newInstance();
            addEditTextHelper();
        }
    }

    private void addEditTextHelper() {
        if (mBackgroundNormal != 0) {
            mEditTextVerifyHelper.setBackgroundNormal(getResources().getDrawable(mBackgroundNormal));
        }
        if (mBackgroundError != 0) {
            mEditTextVerifyHelper.setBackgroundError(getResources().getDrawable(mBackgroundError));
        }

        int childCount = getChildCount();
        List<View> views = new ArrayList<>();
        for (int i = 0; i < childCount; i++) {
            View view = getChildAt(i);
            if (view instanceof EditText || view instanceof AppCompatEditText ||
                    view instanceof android.widget.EditText) {
                views.add(view);
            } else {
                mActionView = view;
            }
        }

        mEditTextVerifyHelper.addActionView(mActionView);
        mEditTextVerifyHelper.addView(views.toArray(new EditText[views.size()]));
        mEditTextVerifyHelper.verify(true);
    }

    public void addActionView(View managerView) {
        mActionView = managerView;
    }

    public boolean verify(boolean reset) {
        return mEditTextVerifyHelper.verify(reset);
    }

}
