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
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;
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

    /**
     * 设定布局
     *
     * @return layout id
     */
    protected int layoutId() {
        return -1;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        Logger.d(mTag + "onAttach");
        mActivity = (A) requireActivity();
    }

    public A getAttachActivity() {
        return mActivity;
    }

    /**
     * 引入View, 作为根布局, 可选用dataBinding, 或者一般方法, 默认使用dataBinding
     *
     * @param inflater           inflater view
     * @param container          root viewGroup
     * @param savedInstanceState 重建View时获取保存的数据
     * @return root view
     */
    private View inflaterView(@Nullable LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (inflater == null) return null;
        if (layoutId() != -1) {
            if (isDataBinding()) {
                ViewDataBinding v = DataBindingUtil.inflate(inflater, layoutId(), container, false);
                if (v != null) {
                    mDecorView = v.getRoot();
                }
                initializeView(savedInstanceState, v);
            } else {
                mDecorView = inflater.inflate(layoutId(), container, false);
                initializeView(savedInstanceState, null);
            }
        } else {
            mDecorView = inflater.inflate(layoutId(), container, false);
            initializeView(savedInstanceState, null);
        }
        return mDecorView;
    }

    /**
     * @return 是否使用DataBinding
     */
    protected boolean isDataBinding() {
        return true;
    }

    /**
     * 初始化控件
     *
     * @param savedInstanceState 保存数据
     * @param viewDataBinding    提供给子类使用
     */
    public void initializeView(@Nullable Bundle savedInstanceState, @Nullable ViewDataBinding viewDataBinding) {
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
    public View onCreateView(@Nullable LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        logD("onCreateView");
        return inflaterView(inflater, container, savedInstanceState);
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
        logD("onDestroyView");
        mDecorView = null;
    }
}
