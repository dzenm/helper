package com.dzenm.helper.base;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import androidx.annotation.ColorRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.dzenm.helper.dialog.PromptDialog;
import com.dzenm.helper.log.Logger;
import com.dzenm.helper.os.ScreenHelper;
import com.dzenm.helper.os.ThemeHelper;

/**
 * @author dinzhenyan
 * @date 2019-04-30 20:03
 */
public abstract class AbsBaseActivity extends AppCompatActivity {

    private ActivityDelegate mActivityDelegate;
    private String mTag = this.getClass().getSimpleName() + "| ";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Logger.d(mTag + "set default theme");
        ThemeHelper.setTheme(this, ThemeHelper.getTheme());
        mActivityDelegate = new ActivityDelegate(this, mTag);
        initializeCreate(savedInstanceState);
        initializeView(savedInstanceState);
    }

    public void toggleTheme(int theme) {
        ThemeHelper.setTheme(this, theme);
        // 重启Activity
        Intent intent = new Intent(this, this.getClass());
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    protected void initializeCreate(Bundle savedInstanceState) {
        mActivityDelegate.initializeCreate();
    }

    protected void initializeView(@Nullable Bundle savedInstanceState) {
    }

    public void setToolbar(Toolbar toolbar) {
        mActivityDelegate.setToolbar(toolbar);
    }

    public void setToolbarWithImmersiveStatusBar(Toolbar toolbar, @ColorRes int color) {
        mActivityDelegate.setToolbarWithImmersiveStatusBar(toolbar, color);
    }

    public void setToolbarWithGradientStatusBar(Toolbar toolbar, Drawable drawable) {
        mActivityDelegate.setToolbarWithGradientStatusBar(toolbar, drawable);
    }

    protected void onHomeClick() {
        mActivityDelegate.onHomeClick();
    }

    public void show(boolean isShow) {
        mActivityDelegate.show(isShow);
    }

    public PromptDialog getPromptDialog() {
        return mActivityDelegate.getPromptDialog();
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
        mActivityDelegate.finished();
    }

    public void logV(String msg) {
        mActivityDelegate.logV(msg);
    }

    public void logD(String msg) {
        mActivityDelegate.logD(msg);
    }

    public void logI(String msg) {
        mActivityDelegate.logI(msg);
    }

    public void logW(String msg) {
        mActivityDelegate.logW(msg);
    }

    public void logE(String msg) {
        mActivityDelegate.logE(msg);
    }

    public String getTag() {
        return mActivityDelegate.getTag();
    }

    public void setTag(String tag) {
        mActivityDelegate.setTag(tag);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mActivityDelegate.onDestroy();
    }

    public void setOnActivityResult(OnActivityResult onActivityResult) {
        mActivityDelegate.setOnActivityResult(onActivityResult);
    }

    public void setOnRequestPermissionsResult(OnRequestPermissionsResult onRequestPermissionsResult) {
        mActivityDelegate.setOnRequestPermissionsResult(onRequestPermissionsResult);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        onRequestActivityResult(requestCode, resultCode, data);
    }

    protected void onRequestActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        mActivityDelegate.onRequestActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onRequestPermissionsResult(
            int requestCode,
            @NonNull String[] permissions,
            @NonNull int[] grantResults
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        onRequestSelfPermissionsResult(requestCode, permissions, grantResults);
    }

    public void onRequestSelfPermissionsResult(
            int requestCode,
            @NonNull String[] permissions,
            @NonNull int[] grantResults
    ) {
        mActivityDelegate.onRequestSelfPermissionsResult(requestCode, permissions, grantResults);
    }

    public interface OnActivityResult {

        void onResult(int requestCode, int resultCode, @Nullable Intent data);
    }

    public interface OnRequestPermissionsResult {

        void onResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults);
    }
}
