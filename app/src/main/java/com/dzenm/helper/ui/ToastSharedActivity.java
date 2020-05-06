package com.dzenm.helper.ui;

import android.os.Bundle;
import android.view.Gravity;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;

import com.dzenm.helper.R;
import com.dzenm.helper.databinding.ActivityToastSharedBinding;
import com.dzenm.lib.base.AbsBaseActivity;
import com.dzenm.lib.dialog.EditDialog;
import com.dzenm.lib.drawable.DrawableHelper;
import com.dzenm.lib.os.ScreenHelper;
import com.dzenm.lib.share.ShareHelper;
import com.dzenm.lib.toast.ToastHelper;

public class ToastSharedActivity extends AbsBaseActivity implements View.OnClickListener {

    private ActivityToastSharedBinding binding;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_toast_shared);
        setToolbarWithImmersiveStatusBar(binding.toolbar, R.color.colorAccent);

        setPressedBackground(binding.tv100, android.R.color.holo_blue_dark);
        setRippleBackground(binding.tv101, android.R.color.holo_red_dark);
        setPressedBackground(binding.tv102, android.R.color.holo_green_dark);
        setRippleBackground(binding.tv103, android.R.color.holo_orange_dark);
        setPressedBackground(binding.tv104, android.R.color.holo_blue_light);
        setRippleBackground(binding.tv105, android.R.color.holo_red_light);
        setPressedBackground(binding.tv106, android.R.color.holo_green_light);
        setRippleBackground(binding.tv107, android.R.color.holo_orange_light);
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.tv_100) {
            EditDialog.newInstance(this)
                    .setTitle("请输入要分享的内容")
                    .setMessage("新月已生飞鸟外，落霞更在夕阳西")
                    .setButtonText("分享", "取消")
                    .setOnClickListener(new EditDialog.OnEditClickListener() {
                        @Override
                        public boolean onClick(EditDialog dialog, boolean confirm) {
                            if (confirm) {
                                ShareHelper.getInstance()
                                        .with(ToastSharedActivity.this)
                                        .setText("分享文本")
                                        .setOnShareListener(new ShareHelper.OnShareListener() {
                                            @Override
                                            public void onResult(boolean isShare) {
                                                if (isShare) {
                                                    ToastHelper.show("分享成功");
                                                } else {
                                                    ToastHelper.show("分享失败");
                                                }
                                            }
                                        }).share();
                            }
                            return true;
                        }
                    }).show();
        } else if (view.getId() == R.id.tv_101) {
            ShareHelper.getInstance()
                    .with(this)
                    .setFile("1", "did")
                    .setOnShareListener(new ShareHelper.OnShareListener() {
                        @Override
                        public void onResult(boolean isShare) {
                            if (isShare) {
                                ToastHelper.show("分享成功");
                            } else {
                                ToastHelper.show("分享失败");
                            }
                        }
                    }).share();
        } else if (view.getId() == R.id.tv_102) {
            ScreenHelper.copy(this, "这是复制的内容");
            ToastHelper.show("复制成功");
        } else if (view.getId() == R.id.tv_103) {
            String s = ScreenHelper.paste(this).toString();
            ToastHelper.show("获取剪贴板内容： " + s);
        } else if (view.getId() == R.id.tv_104) {
            ToastHelper.show("自定义Toast");
        } else if (view.getId() == R.id.tv_105) {
            ToastHelper.show("带图标的toast", R.drawable.prompt_success);
        } else if (view.getId() == R.id.tv_106) {
            ToastHelper.customize()
                    .setBackground(DrawableHelper.solid(android.R.color.holo_green_light).radius(8).build())
                    .show("自定义背景Toast");
        } else if (view.getId() == R.id.tv_107) {
            ToastHelper.customize()
                    .setGravity(Gravity.BOTTOM, 0)
                    .show("自定义背景Toast");
        }
    }

    private void setPressedBackground(View viewBackground, int color) {
        DrawableHelper.radius(10).pressed(color, R.color.colorGray).into(viewBackground);
    }

    private void setRippleBackground(View viewBackground, int color) {
        DrawableHelper.radius(10).ripple(color).into(viewBackground);
    }
}
