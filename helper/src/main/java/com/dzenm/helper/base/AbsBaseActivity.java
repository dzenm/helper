package com.dzenm.helper.base;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.Window;

import androidx.annotation.ColorRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;

import com.dzenm.helper.R;
import com.dzenm.helper.dialog.PromptDialog;
import com.dzenm.helper.net.NetHelper;
import com.dzenm.helper.os.ActivityHelper;
import com.dzenm.helper.os.ScreenHelper;
import com.dzenm.helper.os.StatusBarHelper;
import com.dzenm.helper.permission.PermissionManager;
import com.dzenm.helper.photo.PhotoHelper;

/**
 * @author dinzhenyan
 * @date 2019-04-30 20:03
 */
public abstract class AbsBaseActivity extends AppCompatActivity implements NetHelper.OnNetworkChangeListener {

    protected final String TAG = this.getClass().getSimpleName() + "|";
    protected PromptDialog mPromptDialog;
    private boolean mNetworkAvailable = false;                          // 判断网络是否可用

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityHelper.getInstance().add(this);                         // 添加Activity到Stack管理
        getWindow().requestFeature(Window.FEATURE_CONTENT_TRANSITIONS); // 设置切换页面动画开关
        mPromptDialog = PromptDialog.newInstance(this);
        initializeView();                                               // 初始化View
    }

    protected void initializeView() {
    }

    /**
     * 设置toolbar及返回按钮
     *
     * @param toolbar 设置的toolbar
     */
    public void setToolbar(Toolbar toolbar) {
        if (toolbar == null) return;
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);      // 设置返回按钮
        }
    }

    /**
     * 设置Toolbar, 并移除StatusBar
     *
     * @param toolbar 需要设置的Toolbar
     */
    public void setToolbarWithoutStatusBar(Toolbar toolbar) {
        setToolbar(toolbar);
        StatusBarHelper.adjustToolbarForHideStatusBar(this, toolbar);
        StatusBarHelper.setColor(this, true, android.R.color.transparent);
    }

    /**
     * 设置toolbar, 并设置沉浸式状态栏
     *
     * @param toolbar 需要设置的Toolbar
     * @param color   设置的颜色
     */
    public void setToolbarWithImmersiveStatusBar(Toolbar toolbar, @ColorRes int color) {
        setToolbar(toolbar);
        toolbar.setBackgroundColor(getResources().getColor(color));
        StatusBarHelper.setColor(this, color);
    }

    /**
     * 设置toolbar, 并设置半透明状态栏
     *
     * @param toolbar 需要设置的Toolbar
     * @param color   设置的颜色
     */
    public void setToolbarWithTranslucentStatusBar(Toolbar toolbar, @ColorRes int color) {
        setToolbar(toolbar);
        toolbar.setBackgroundColor(getResources().getColor(color));
        StatusBarHelper.setTranslucentColor(this, color);
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
     * 设置toolbar,并在左上角添加动画横线按钮
     *
     * @param drawerLayout {@link DrawerLayout} 布局
     * @param toolbar      设置的Toolbar
     * @param title        显示的标题
     */
    public void addDrawerLayoutToggle(DrawerLayout drawerLayout, Toolbar toolbar, String title) {
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(title);
        }
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        // 设置返回按钮的点击事件
        onHomeOptionSelected(item.getItemId());
        return super.onOptionsItemSelected(item);
    }

    /**
     * Toolbar的Home键点击事件
     *
     * @param itemId 设置的item
     */
    protected void onHomeOptionSelected(int itemId) {
        if (itemId == android.R.id.home) finish();
    }

    /**
     * 显示提示框
     */
    public void showPromptDialog() {
        if (mPromptDialog.isShowing()) return;
        mPromptDialog.showLoading(PromptDialog.LOADING_POINT_SCALE);
    }

    /**
     * 隐藏提示框
     */
    public void dismissPromptDialog() {
        if (!mPromptDialog.isShowing()) return;
        mPromptDialog.dismiss();
    }

    public boolean isNetworkAvailable() {
        return mNetworkAvailable;
    }

    /**
     * 检测网络是否可用
     */
    public void findNetworkAvailable() {
        NetHelper.getInstance().setOnNetworkChangeListener(this);
    }

    /**
     * 网络广播监听回调时连接到网络
     */
    protected void onConnectNetwork() {

    }

    /**
     * 网络广播监听回调时未连接到网络
     */
    protected void onUnConnectNetWork() {
        NetHelper.setNetworkSetting(this);
    }

    protected void moveTaskToBack() {
        if (!moveTaskToBack(false)) {
            super.onBackPressed();
        }
    }

    @Override
    public void onNetwork(boolean connect) {
        mNetworkAvailable = connect;
        if (connect) {
            onConnectNetwork();
        } else {
            onUnConnectNetWork();
        }
    }

    @Override
    public void finish() {
        ScreenHelper.hideSoftInput(this);
        super.finish();
        ActivityHelper.getInstance().finish(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ActivityHelper.getInstance().remove(this);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        PermissionManager.getInstance().onPermissionResult(requestCode, permissions, grantResults);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        PermissionManager.getInstance().onSettingResult(requestCode, resultCode, data);
        PhotoHelper.getInstance().onPhotoResult(requestCode, resultCode, data);
    }
}
