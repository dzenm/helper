package com.dzenm.helper.material;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * @author dzenm
 * @date 2020/2/28 下午2:53
 */
public interface DialogFragmentInterface {

    int layoutId();

    void initView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState
    );

    void onDialogAnimator();

    void onDialogGravity(
            WindowManager.LayoutParams layoutParams
    );

    void onDialogLayoutParams(
            ViewGroup.MarginLayoutParams layoutParams,
            int centerWidth,
            int margin
    );

    void apply(Window window);
}
