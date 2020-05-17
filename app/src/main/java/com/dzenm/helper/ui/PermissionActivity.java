package com.dzenm.helper.ui;

import android.Manifest;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;

import com.dzenm.helper.R;
import com.dzenm.helper.databinding.ActivityPermissionBinding;
import com.dzenm.lib.base.AbsBaseActivity;
import com.dzenm.lib.drawable.DrawableHelper;
import com.dzenm.lib.material.MaterialDialog;
import com.dzenm.lib.os.OsHelper;
import com.dzenm.lib.permission.PermissionManager;
import com.dzenm.lib.toast.ToastHelper;

public class PermissionActivity extends AbsBaseActivity implements View.OnClickListener,
        PermissionManager.OnPermissionListener {

    private ActivityPermissionBinding binding;

    private String[] permissions = new String[]{Manifest.permission.CALL_PHONE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.CAMERA,
            Manifest.permission.WRITE_CALENDAR};

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_permission);
        setToolbarWithImmersiveStatusBar(binding.toolbar, R.color.colorMaterialLightBlue);

        setPressedBackground(binding.tv100);
        setRippleBackground(binding.tv101);
        setPressedBackground(binding.tv102);
        setRippleBackground(binding.tv103);
        setPressedBackground(binding.tv104);
        setRippleBackground(binding.tv105);
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
                    .mode(PermissionManager.MODE_DEFAULT)
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
                new MaterialDialog.Builder(this)
                        .setTitle("提示")
                        .setMessage("是否前往设置页面打开通知权限")
                        .setOnClickListener(new MaterialDialog.OnClickListener() {
                            @Override
                            public void onClick(MaterialDialog dialog, int which) {
                                if (which == MaterialDialog.BUTTON_POSITIVE) {
                                    OsHelper.openNotificationSetting(PermissionActivity.this);
                                }
                                dialog.dismiss();
                            }
                        }).create()
                        .show();
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
        DrawableHelper.radius(10).pressed(R.color.colorMaterialLightBlue, R.color.colorMaterialSecondLightBlue).into(viewBackground);
    }

    private void setRippleBackground(View viewBackground) {
        DrawableHelper.radius(10).ripple(R.color.colorMaterialLightBlue).into(viewBackground);
    }

    @Override
    public void onPermit(boolean isGrant) {
        if (isGrant) {
            ToastHelper.show("请求成功", R.drawable.prompt_success);
        } else {
            ToastHelper.show("未请求成功权限", R.drawable.prompt_error);
        }
    }
}
