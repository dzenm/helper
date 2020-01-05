package com.dzenm.helper.base;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.ColorRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import com.dzenm.helper.log.Logger;
import com.dzenm.helper.os.StatusBarHelper;

/**
 * @author dinzhenyan
 * @date 2019-04-30 20:03
 */
public abstract class AbsBaseFragment<A extends Activity> extends Fragment {

    private String mTag = this.getClass().getSimpleName() + "| ";

    protected A mActivity;

    /**
     * 根布局
     */
    protected View mDecorView;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        Logger.d(mTag + "onAttach");
        mActivity = (A) requireActivity();
    }

    public A getAttachActivity() {
        return mActivity;
    }

    public void initializeView(LayoutInflater inflater, ViewGroup container, @Nullable Bundle savedInstanceState) {
    }

    /**
     * 设置toolbar
     *
     * @param toolbar 设置的toolbar
     */
    public void setToolbar(Toolbar toolbar) {
        if (toolbar == null) return;
        AppCompatActivity activity = (AppCompatActivity) mActivity;
        activity.setSupportActionBar(toolbar);
    }

    /**
     * 设置toolbar, 并设置沉浸式状态栏
     *
     * @param toolbar 需要设置的Toolbar
     * @param color   设置的颜色
     */
    public void setToolbarWithImmersiveStatusBar(Toolbar toolbar, @ColorRes int color) {
        setToolbar(toolbar);
        StatusBarHelper.setFragmentToolbarColor(mActivity, toolbar, color);
    }

    /**
     * 设置toolbar, 并设置渐变式状态栏
     *
     * @param toolbar  需要设置的Toolbar
     * @param drawable 需要设置的drawable
     */
    public void setToolbarWithGradientStatusBar(Toolbar toolbar, Drawable drawable) {
        setToolbar(toolbar);
        toolbar.setBackground(drawable);
        StatusBarHelper.setDrawable(mActivity, drawable);
    }

    @Nullable
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            ViewGroup container,
            @Nullable Bundle savedInstanceState
    ) {
        logD("onCreateView");
        initializeView(inflater, container, savedInstanceState);
        return mDecorView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        logD("onViewCreated");
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        logD("onActivityCreated");
    }

    protected <T extends View> T findViewById(int id) {
        return mDecorView.findViewById(id);
    }

    protected void startActivity(Class clazz) {
        Intent intent = new Intent(mActivity, clazz);
        startActivity(intent);
    }

    public void logV(String msg) {
        Logger.v(mTag + msg);
    }

    public void logD(String msg) {
        Logger.d(mTag + msg);
    }

    public void logI(String msg) {
        Logger.i(mTag + msg);
    }

    public void logW(String msg) {
        Logger.w(mTag + msg);
    }

    public void logE(String msg) {
        Logger.e(mTag + msg);
    }

    public String getLogTag() {
        return mTag;
    }

    public void setLogTag(String tag) {
        mTag = tag;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mDecorView = null;
        logD("onDestroyView");
    }
}
