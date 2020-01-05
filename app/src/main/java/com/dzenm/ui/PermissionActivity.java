package com.dzenm.ui;

import android.Manifest;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;

import com.dzenm.R;
import com.dzenm.databinding.ActivityPermissionBinding;
import com.dzenm.helper.base.AbsActivity;
import com.dzenm.helper.base.AbsBaseActivity;
import com.dzenm.helper.dialog.InfoDialog;
import com.dzenm.helper.draw.DrawableHelper;
import com.dzenm.helper.os.OsHelper;
import com.dzenm.helper.permission.PermissionManager;
import com.dzenm.helper.toast.ToastHelper;

public class PermissionActivity extends AbsActivity<ActivityPermissionBinding> implements View.OnClickListener,
        PermissionManager.OnPermissionListener {

    private String[] permissions = new String[]{Manifest.permission.CALL_PHONE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.CAMERA,
            Manifest.permission.WRITE_CALENDAR};

    @Override
    protected int layoutId() {
        return R.layout.activity_permission;
    }

    @Override
    protected void initializeView(@Nullable Bundle savedInstanceState) {
        super.initializeView(savedInstanceState);
        setToolbarWithImmersiveStatusBar(getBinding().toolbar, R.color.colorDarkBlue);

        setPressedBackground(getBinding().tv100);
        setRippleBackground(getBinding().tv101);
        setPressedBackground(getBinding().tv102);
        setRippleBackground(getBinding().tv103);
        setPressedBackground(getBinding().tv104);
        setRippleBackground(getBinding().tv105);
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.tv_100) {
            PermissionManager.getInstance()
                    .with(this)
                    .load(permissions)
                    .mode(PermissionManager.MODE_ONCE)
                    .into(this)
                    .request();
        } else if (view.getId() == R.id.tv_101) {
            PermissionManager.getInstance()
                    .with(this)
                    .load(permissions)
                    .mode(PermissionManager.MODE_ONCE_INFO)
                    .into(this)
                    .request();
        } else if (view.getId() == R.id.tv_102) {
            PermissionManager.getInstance()
                    .with(this)
                    .load(permissions)
                    .mode(PermissionManager.MODE_REPEAT)
                    .into(this)
                    .request();
        } else if (view.getId() == R.id.tv_103) {
            if (OsHelper.isNotificationEnabled(this)) {
                ToastHelper.show("已打开通知权限");
            } else {
                InfoDialog.newInstance(this)
                        .setTitle("提示")
                        .setMessage("是否前往设置页面打开通知权限")
                        .setOnClickListener(new InfoDialog.OnInfoClickListener() {
                            @Override
                            public boolean onClick(InfoDialog dialog, boolean confirm) {
                                if (confirm) {
                                    OsHelper.openNotificationSetting(PermissionActivity.this);
                                }
                                return true;
                            }
                        }).show();
            }
        } else if (view.getId() == R.id.tv_104) {
            if (OsHelper.isInstallPermission(this)) {
                ToastHelper.show("已打开安装权限");
            } else {
                ToastHelper.show("未打开安装权限");
            }
        } else if (view.getId() == R.id.tv_105) {
            if (OsHelper.isOverlaysPermission(this)) {
                ToastHelper.show("已打开悬浮窗权限");
            } else {
                ToastHelper.show("未打开悬浮窗权限");
            }
        }
    }

    private void setPressedBackground(View viewBackground) {
        DrawableHelper.radius(10).pressed(R.color.colorDarkBlue, R.color.colorTranslucentDarkBlue).into(viewBackground);
    }

    private void setRippleBackground(View viewBackground) {
        DrawableHelper.radius(10).ripple(R.color.colorDarkBlue).into(viewBackground);
    }

    @Override
    public void onPermit(boolean isGrant) {
        if (isGrant) {
            ToastHelper.show("请求成功", com.dzenm.helper.R.drawable.prompt_success);
        } else {
            ToastHelper.show("未请求成功权限", com.dzenm.helper.R.drawable.prompt_error);
        }
    }
}
