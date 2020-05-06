package com.dzenm.lib.base;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.Window;

import androidx.annotation.ColorRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.dzenm.lib.dialog.PromptDialog;
import com.dzenm.lib.log.Logger;
import com.dzenm.lib.os.ActivityHelper;
import com.dzenm.lib.os.StatusBarHelper;
import com.dzenm.lib.os.ThemeHelper;

/**
 * @author dzenm
 * @date 2020-01-05 17:24
 */
public class ActivityDelegate {

    private AppCompatActivity mActivity;
    private PromptDialog mPromptDialog;
    private AbsBaseActivity.OnActivityResult mOnActivityResult;
    private AbsBaseActivity.OnRequestPermissionsResult mOnRequestPermissionsResult;
    private String mTag;

    ActivityDelegate(AppCompatActivity activity, String tag) {
        mActivity = activity;
        mTag = tag;

        ActivityHelper.getInstance().add(mActivity); // 添加Activity到Stack管理
        Logger.d(mTag + "set default theme");
        ThemeHelper.setTheme(mActivity, ThemeHelper.getTheme());
        mActivity.getWindow().requestFeature(Window.FEATURE_CONTENT_TRANSITIONS);    // 设置切换页面动画开关
        mPromptDialog = PromptDialog.newInstance(mActivity);
    }

    void toggleTheme(int theme) {
        ThemeHelper.setTheme(mActivity, theme);
        // 重启Activity
        Intent intent = new Intent(mActivity, mActivity.getClass());
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        mActivity.startActivity(intent);
    }

    /**
     * @param toolbar 设置的toolbar
     */
    void setToolbar(Toolbar toolbar) {
        if (toolbar == null) return;
        mActivity.setSupportActionBar(toolbar);
        if (mActivity.getSupportActionBar() == null) return;
        mActivity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);      // 设置返回按钮
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onHomeClick();
            }
        });
    }

    /**
     * Toolbar的Home键点击事件
     */
    void onHomeClick() {
        mActivity.finish();
    }

    /**
     * 设置toolbar, 并设置沉浸式状态栏
     *
     * @param toolbar 需要设置的Toolbar
     * @param color   设置的颜色
     */
    void setToolbarWithImmersiveStatusBar(Toolbar toolbar, @ColorRes int color) {
        setToolbar(toolbar);
        StatusBarHelper.setStatusBarWithToolbarStyle(mActivity, toolbar, color);
    }

    /**
     * 设置toolbar, 并设置渐变式状态栏
     *
     * @param toolbar  需要设置的Toolbar
     * @param drawable 需要设置的drawable
     */
    void setToolbarWithGradientStatusBar(Toolbar toolbar, Drawable drawable) {
        setToolbar(toolbar);
        toolbar.setBackground(drawable);
        StatusBarHelper.setDrawable(mActivity, drawable);
    }

    /**
     * @param isShow 是否显示提示框
     */
    void show(boolean isShow) {
        if (isShow) {
            if (!mPromptDialog.isShowing()) {
                mPromptDialog.showLoading(PromptDialog.LOADING_POINT_SCALE);
            }
        } else {
            if (mPromptDialog.isShowing()) {
                mPromptDialog.dismiss();
            }
        }
    }

    PromptDialog getPromptDialog() {
        return mPromptDialog;
    }

    void finished() {
        ActivityHelper.getInstance().finish(mActivity);
    }

    void logV(String msg) {
        Logger.v(mTag + msg);
    }

    void logD(String msg) {
        Logger.d(mTag + msg);
    }

    void logI(String msg) {
        Logger.i(mTag + msg);
    }

    void logW(String msg) {
        Logger.w(mTag + msg);
    }

    void logE(String msg) {
        Logger.e(mTag + msg);
    }

    String getTag() {
        return mTag;
    }

    void setTag(String tag) {
        mTag = tag;
    }

    void onDestroy() {
        ActivityHelper.getInstance().remove(mActivity);
    }

    void setOnActivityResult(AbsBaseActivity.OnActivityResult onActivityResult) {
        mOnActivityResult = onActivityResult;
    }

    void setOnRequestPermissionsResult(AbsBaseActivity.OnRequestPermissionsResult onRequestPermissionsResult) {
        mOnRequestPermissionsResult = onRequestPermissionsResult;
    }

    void onRequestActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (mOnActivityResult != null) {
            mOnActivityResult.onResult(requestCode, resultCode, data);
        }
    }

    void onRequestSelfPermissionsResult(
            int requestCode,
            @NonNull String[] permissions,
            @NonNull int[] grantResults
    ) {
        if (mOnRequestPermissionsResult != null) {
            mOnRequestPermissionsResult.onResult(requestCode, permissions, grantResults);
        }
    }
}