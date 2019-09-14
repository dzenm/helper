package com.dzenm.helper.base;

import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.view.MenuItem;
import android.view.Window;

import com.dzenm.helper.R;
import com.dzenm.helper.dialog.PromptDialog;
import com.dzenm.helper.net.NetHelper;
import com.dzenm.helper.os.ActivityHelper;
import com.dzenm.helper.os.ScreenHelper;

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
        // 设置切换页面动画开关
        getWindow().requestFeature(Window.FEATURE_CONTENT_TRANSITIONS);
        ActivityHelper.getInstance().add(this);                         // 添加Activity到Stack管理
        mPromptDialog = PromptDialog.newInstance(this);
        initializeView();                                               // 初始化View
    }

    protected void initializeView() {
    }

    /**
     * 设置toolbar,并在左上角添加动画横线按钮
     *
     * @param drawerLayout
     * @param toolbar
     * @param title
     */
    public void addDrawerLayoutToggle(DrawerLayout drawerLayout, Toolbar toolbar, String title) {
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(title);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
    }

    /**
     * 设置toolbar及返回按钮
     *
     * @param toolbar 设置的toolbar
     */
    protected void setToolbar(Toolbar toolbar) {
        if (toolbar == null) return;
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);      // 设置返回按钮
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // 设置返回按钮的点击事件
        onHomeOptionSelected(item);
        return super.onOptionsItemSelected(item);
    }

    /**
     * Toolbar的Home键点击事件
     *
     * @param item
     */
    protected void onHomeOptionSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) finish();
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

    @Override
    public void onNetwork(boolean connect) {
        mNetworkAvailable = connect;
        if (connect) {
            onConnectNetwork();
        } else {
            onUnConnectNetWork();
        }
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
}
