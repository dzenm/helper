package com.dzenm.helper.base;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.view.Window;

import androidx.annotation.ColorRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;

import com.dzenm.helper.dialog.PromptDialog;
import com.dzenm.helper.log.Logger;
import com.dzenm.helper.os.ActivityHelper;
import com.dzenm.helper.os.ScreenHelper;
import com.dzenm.helper.os.StatusBarHelper;
import com.dzenm.helper.permission.PermissionManager;
import com.dzenm.helper.photo.PhotoSelector;

/**
 * @author dinzhenyan
 * @date 2019-04-30 20:03
 */
public abstract class AbsBaseActivity extends AppCompatActivity {

    private String mTag = this.getClass().getSimpleName() + "| ";

    private PromptDialog mPromptDialog;
    private OnRequestPermissionsResult mOnRequestPermissionsResult;
    private OnActivityResult mOnActivityResult;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initializeCreate();
        if (layoutId() != -1) {
            if (isDataBinding()) {
                ViewDataBinding v = DataBindingUtil.setContentView(this, layoutId());
                initializeView(savedInstanceState, v);                  // 使用DataBinding, 初始化View
            } else {
                setContentView(layoutId());
                initializeView(savedInstanceState, null);// 不使用DataBinding, 初始化View
            }
        } else {
            initializeView(savedInstanceState, null);
        }
    }

    protected void initializeCreate() {
        ActivityHelper.getInstance().add(this);                         // 添加Activity到Stack管理
        getWindow().requestFeature(Window.FEATURE_CONTENT_TRANSITIONS); // 设置切换页面动画开关
        mPromptDialog = PromptDialog.newInstance(this);
    }

    protected int layoutId() {
        return -1;
    }

    /**
     * @return 是否使用DataBinding
     */
    protected boolean isDataBinding() {
        return true;
    }

    protected void initializeView(@Nullable Bundle savedInstanceState, @Nullable ViewDataBinding viewDataBinding) {
    }

    /**
     * 设置toolbar
     *
     * @param toolbar 设置的toolbar
     */
    public void setToolbar(Toolbar toolbar) {
        if (toolbar == null) return;
        setSupportActionBar(toolbar);
        if (getSupportActionBar() == null) return;
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);      // 设置返回按钮
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onHomeClick();
            }
        });
    }

    /**
     * 设置toolbar, 并设置沉浸式状态栏
     *
     * @param toolbar 需要设置的Toolbar
     * @param color   设置的颜色
     */
    public void setToolbarWithImmersiveStatusBar(Toolbar toolbar, @ColorRes int color) {
        setToolbar(toolbar);
        StatusBarHelper.setStatusBarWithToolbarStyle(this, toolbar, color);
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
        StatusBarHelper.setDrawable(this, drawable);
    }

    /**
     * Toolbar的Home键点击事件
     */
    protected void onHomeClick() {
        finish();
    }

    /**
     * 显示提示框
     *
     * @param isShow 是否显示提示框
     */
    public void show(boolean isShow) {
        if (isShow) {
            if (mPromptDialog.isShowing()) return;
            mPromptDialog.showLoading(PromptDialog.LOADING_POINT_SCALE);
        } else {
            if (!mPromptDialog.isShowing()) return;
            mPromptDialog.dismiss();
        }
    }

    public PromptDialog getPromptDialog() {
        return mPromptDialog;
    }

    public void moveTaskToBack() {
        if (!moveTaskToBack(false)) {
            super.onBackPressed();
        }
    }

    @Override
    public void finish() {
        ScreenHelper.hideSoftInput(this);
        super.finish();
        finished();
    }

    public void finished() {
        ActivityHelper.getInstance().finish(this);
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

    public String getTag() {
        return mTag;
    }

    public void setTag(String tag) {
        mTag = tag;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ActivityHelper.getInstance().remove(this);
    }

    public void setOnActivityResult(OnActivityResult onActivityResult) {
        mOnActivityResult = onActivityResult;
    }

    public void setOnRequestPermissionsResult(OnRequestPermissionsResult onRequestPermissionsResult) {
        mOnRequestPermissionsResult = onRequestPermissionsResult;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        onRequestActivityResult(requestCode, resultCode, data);
    }

    protected void onRequestActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        PermissionManager.getInstance().onActivityResult(requestCode, resultCode, data);
        PhotoSelector.getInstance().onPhotoResult(requestCode, resultCode, data);
        if (mOnActivityResult != null) {
            mOnActivityResult.onResult(requestCode, resultCode, data);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        onRequestSelfPermissionsResult(requestCode, permissions, grantResults);
    }

    public void onRequestSelfPermissionsResult(int requestCode,
                                               @NonNull String[] permissions,
                                               @NonNull int[] grantResults) {
        PermissionManager.getInstance().onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (mOnRequestPermissionsResult != null) {
            mOnRequestPermissionsResult.onResult(requestCode, permissions, grantResults);
        }
    }
}
