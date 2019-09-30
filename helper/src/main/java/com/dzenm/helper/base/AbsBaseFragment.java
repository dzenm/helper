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

    protected final String TAG = this.getClass().getSimpleName() + "|";
    protected boolean isViewCreated = false;

    protected A mActivity;

    /**
     * 根布局, 用于获取布局的id
     */
    protected View mRootView;

    /**
     * 设定布局
     *
     * @return layout id
     */
    protected int layoutId() {
        return 0;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        Logger.d(TAG + "onAttach");
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
    private View inflaterView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                              @Nullable Bundle savedInstanceState) {
        if (isDataBinding()) {
            ViewDataBinding v = DataBindingUtil.inflate(inflater, layoutId(), container, false);
            mRootView = v.getRoot();
            initializeView(savedInstanceState, v);
        } else {
            mRootView = inflater.inflate(layoutId(), container, false);
            initializeView(savedInstanceState);
        }
        return mRootView;
    }

    /**
     * @return 是否使用DataBinding
     */
    protected boolean isDataBinding() {
        return false;
    }

    /**
     * 初始化控件, 使用dataBinding, 重写该方法初始化控件
     *
     * @param viewDataBinding 提供给子类使用
     */
    public void initializeView(Bundle savedInstanceState, ViewDataBinding viewDataBinding) {
    }

    /**
     * 初始化控件, 不使用dataBinding, 重写该方法初始化控件
     */
    public void initializeView(Bundle savedInstanceState) {
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
        Logger.d(TAG + "onCreateView");
        return inflaterView(inflater, container, savedInstanceState);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        isViewCreated = true;
        Logger.d(TAG + "onViewCreated");
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Logger.d(TAG + "onActivityCreated");
    }

    protected <T extends View> T findViewById(int id) {
        return mRootView.findViewById(id);
    }

    protected void startActivity(Class clazz) {
        Intent intent = new Intent(mActivity, clazz);
        startActivity(intent);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        isViewCreated = false;
        mRootView = null;
    }
}
