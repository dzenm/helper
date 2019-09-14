package com.dzenm.ui;

import android.Manifest;
import android.content.Intent;
import androidx.databinding.DataBindingUtil;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.view.View;

import com.dzenm.R;
import com.dzenm.databinding.ActivityPermissionBinding;
import com.dzenm.helper.base.AbsBaseActivity;
import com.dzenm.helper.dialog.InfoDialog;
import com.dzenm.helper.draw.BackGHelper;
import com.dzenm.helper.os.OsHelper;
import com.dzenm.helper.permission.PermissionManager;
import com.dzenm.helper.toast.Toa;

public class PermissionActivity extends AbsBaseActivity implements View.OnClickListener, PermissionManager.OnPermissionListener {

    private String[] permissions = new String[]{Manifest.permission.CALL_PHONE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.CAMERA,
            Manifest.permission.WRITE_CALENDAR};

    @Override
    protected void initializeView() {
        ActivityPermissionBinding binding = DataBindingUtil.setContentView(this, R.layout.activity_permission);
        setToolbar(binding.toolbar);
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
                Toa.show("已打开通知权限");
            } else {
                InfoDialog.newInstance(this)
                        .setTitle("提示")
                        .setMessage("是否前往设置页面打开通知权限")
                        .setOnDialogClickListener(new InfoDialog.OnInfoClickListener() {
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
                Toa.show("已打开安装权限");
            } else {
                Toa.show("未打开安装权限");
            }
        } else if (view.getId() == R.id.tv_105) {
            if (OsHelper.isOverlaysPermission(this)) {
                Toa.show("已打开悬浮窗权限");
            } else {
                Toa.show("未打开悬浮窗权限");
            }
        }
    }

    private void setPressedBackground(View viewBackground) {
        BackGHelper.radius(10).pressed(R.color.colorDarkBlue, R.color.colorTranslucentDarkBlue).into(viewBackground);
    }

    private void setRippleBackground(View viewBackground) {
        BackGHelper.radius(10).ripple(R.color.colorDarkBlue).into(viewBackground);
    }

    @Override
    public void onPermit(boolean isGrant) {
        if (isGrant) {
            Toa.show("请求成功", com.dzenm.helper.R.drawable.prompt_success);
        } else {
            Toa.show("未请求成功权限", com.dzenm.helper.R.drawable.prompt_error);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        PermissionManager.getInstance().onSettingResult(requestCode, resultCode, data);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        PermissionManager.getInstance().onPermissionResult(requestCode, permissions, grantResults);
    }

}
