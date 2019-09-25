package com.dzenm.helper.base;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.dzenm.helper.log.Logger;

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
    private View inflaterView(@Nullable LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (isDataBinding()) {
            ViewDataBinding v = DataBindingUtil.inflate(inflater, layoutId(), container, false);
            mRootView = v.getRoot();
            initializeView(v);
        } else {
            mRootView = inflater.inflate(layoutId(), container, false);
            initializeView();
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
    public void initializeView(ViewDataBinding viewDataBinding) {
    }

    /**
     * 初始化控件, 不使用dataBinding, 重写该方法初始化控件
     */
    public void initializeView() {
    }

    /**
     * 设置toolbar及左上角的返回按钮
     *
     * @param toolbar 设置toolbar
     */
    protected void setSupportToolbar(Toolbar toolbar) {
        if (toolbar == null) return;
        AppCompatActivity activity = (AppCompatActivity) mActivity;
        activity.setSupportActionBar(toolbar);
        if (activity.getSupportActionBar() != null) {
            activity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
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
