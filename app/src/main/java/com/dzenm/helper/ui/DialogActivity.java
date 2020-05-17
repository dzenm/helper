package com.dzenm.helper.ui;

import android.os.Bundle;
import android.view.Gravity;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;

import com.bumptech.glide.Glide;
import com.dzenm.helper.R;
import com.dzenm.helper.databinding.ActivityDialogBinding;
import com.dzenm.helper.util.Urls;
import com.dzenm.lib.base.AbsBaseActivity;
import com.dzenm.lib.dialog.DatePickerDialog;
import com.dzenm.lib.dialog.LoginDialog;
import com.dzenm.lib.download.DownloadHelper;
import com.dzenm.lib.drawable.DrawableHelper;
import com.dzenm.lib.material.MaterialDialog;
import com.dzenm.lib.material.PromptDialog;
import com.dzenm.lib.popupwindow.PopupDialog;
import com.dzenm.lib.toast.ToastHelper;
import com.dzenm.lib.view.ViewHolder;

public class DialogActivity extends AbsBaseActivity implements View.OnClickListener {

    private ActivityDialogBinding binding;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = DataBindingUtil.setContentView(this, R.layout.activity_dialog);
        setToolbarWithImmersiveStatusBar(binding.toolbar, android.R.color.transparent);

        setPressedBackground(binding.tv100, android.R.color.holo_blue_dark);
        setRippleBackground(binding.tv101, android.R.color.holo_red_dark);
        setPressedBackground(binding.tv102, android.R.color.holo_green_dark);
        setRippleBackground(binding.tv103, android.R.color.holo_orange_dark);
        setPressedBackground(binding.tv104, android.R.color.holo_blue_light);
        setRippleBackground(binding.tv105, android.R.color.holo_red_light);
        setPressedBackground(binding.tv106, android.R.color.holo_green_light);
        setRippleBackground(binding.tv107, android.R.color.holo_orange_light);
        setRippleBackground(binding.tv1071, android.R.color.holo_orange_dark);
        setPressedBackground(binding.tv108, android.R.color.darker_gray);
        setRippleBackground(binding.tv109, android.R.color.holo_blue_bright);
        setPressedBackground(binding.tv110, android.R.color.holo_purple);
        setPressedBackground(binding.tv1101, android.R.color.holo_blue_dark);
        setPressedBackground(binding.tv111, android.R.color.holo_green_light);
        setRippleBackground(binding.tv112, android.R.color.holo_orange_light);
        setPressedBackground(binding.tv113, android.R.color.darker_gray);
        setRippleBackground(binding.tv114, android.R.color.holo_blue_bright);

        setPressedBackground(binding.tv120, android.R.color.holo_blue_dark);
        setRippleBackground(binding.tv121, android.R.color.holo_red_dark);
        setPressedBackground(binding.tv122, android.R.color.holo_green_dark);
        setRippleBackground(binding.tv123, android.R.color.holo_orange_dark);
        setPressedBackground(binding.tv124, android.R.color.holo_blue_light);
        setRippleBackground(binding.tv125, android.R.color.holo_red_light);
        setPressedBackground(binding.tv126, android.R.color.holo_green_light);
        setRippleBackground(binding.tv127, android.R.color.holo_orange_light);
        setPressedBackground(binding.tv128, android.R.color.darker_gray);
        setRippleBackground(binding.tv129, android.R.color.holo_blue_bright);

        Glide.with(this).load(Urls.URL_3).into(binding.image);
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.tv_100) {
            LoginDialog.newInstance(this)
                    .setLoginByVerifyCode()
                    .setOnClickListener(new LoginDialog.OnClickListener() {
                        @Override
                        public void onLoginClick(LoginDialog dialog) {
                            ToastHelper.show("登录成功", R.drawable.ic_prompt_success);
                        }

                        @Override
                        public void onRegisterClick(LoginDialog dialog) {
                            ToastHelper.show("注册成功", R.drawable.ic_prompt_success);
                        }

                        @Override
                        public void onVerifyClick() {
                            ToastHelper.show("请求验证码", R.drawable.ic_prompt_success);
                        }
                    }).show();
        } else if (view.getId() == R.id.tv_101) {

        } else if (view.getId() == R.id.tv_1101) {
            String url = "https://downpack.baidu.com/appsearch_AndroidPhone_v8.0.3(1.0.65.172)_1012271b.apk";
            ToastHelper.show("开始下载...");
            DownloadHelper.newInstance(this)
                    .setUrl(url)
                    .download();
        } else if (view.getId() == R.id.tv_111) {

        } else if (view.getId() == R.id.tv_112) {

        } else if (view.getId() == R.id.tv_113) {
            new PopupDialog.Builder(DialogActivity.this)
                    .setView(R.layout.dialog_login)
                    .setOnBindViewHolder(new PopupDialog.OnBindViewHolder() {
                        @Override
                        public void onBinding(ViewHolder holder,
                                              final PopupDialog popupWindow) {
                            holder.getView(R.id.tv_confirm).setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    ToastHelper.show("登录成功");
                                    popupWindow.dismiss();
                                }
                            });
                            holder.getView(R.id.tv_cancel).setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    popupWindow.dismiss();
                                }
                            });
                        }
                    }).create()
                    .showAsDropDown(findViewById(R.id.toolbar), 100, 0);
        } else if (view.getId() == R.id.tv_114) {
            new MaterialDialog.Builder(this)
                    .setTitle("提示")
                    .setMessage("日期选择器")
                    .setButtonText("循环查看", "指定初始日期")
                    .setOnClickListener(new MaterialDialog.OnClickListener() {
                        @Override
                        public void onClick(MaterialDialog dialog, int which) {
                            if (which == MaterialDialog.BUTTON_POSITIVE) {
                                DatePickerDialog.newInstance(DialogActivity.this)
                                        .setRange("1970/1/1", "2019/8/1")
                                        .setLoop(true)
                                        .setTitle("")
                                        .setOnSelectedCallback(new DatePickerDialog.OnSelectedCallback() {
                                            @Override
                                            public void onSelect(String date) {

                                            }
                                        })
                                        .setMargin(0)
                                        .show();
                            } else {
                                DatePickerDialog.newInstance(DialogActivity.this)
                                        .setSeparator("-")
                                        .setSelected("2019-02-01")
                                        .setOnSelectedCallback(new DatePickerDialog.OnSelectedCallback() {
                                            @Override
                                            public void onSelect(String date) {
                                                ToastHelper.show(date);
                                            }
                                        }).setPrimaryColor(android.R.color.holo_green_light)
                                        .show();
                            }
                        }
                    })
                    .create()
                    .show();
        } else if (view.getId() == R.id.tv_120) {
            PromptDialog.newInstance(this)
                    .setTranslucent(true)
                    .showSuccess("登录成功");
        } else if (view.getId() == R.id.tv_121) {
            PromptDialog.newInstance(this)
                    .setBackground(DrawableHelper.solid(android.R.color.holo_purple).radius(8).build())
                    .showError("禁止访问");
        } else if (view.getId() == R.id.tv_122) {
            PromptDialog.newInstance(this)
                    .setGravity(Gravity.BOTTOM)
                    .showWarming("您的身份信息可能被泄露");
        } else if (view.getId() == R.id.tv_123) {
            PromptDialog.newInstance(this)
                    .show("正在加载中, 请稍后...", R.drawable.updata, true);
        } else if (view.getId() == R.id.tv_124) {
            PromptDialog.newInstance(this)
                    .setTranslucent(true)
                    .setOnCountListener(new PromptDialog.OnCountDownListener() {
                        @Override
                        public void onFinish() {
                            logD("回掉完成");
                            ToastHelper.show("回掉完成");
                        }
                    }).showSuccess("完成");
        } else if (view.getId() == R.id.tv_125) {
            PromptDialog.newInstance(this)
                    .showLoading(PromptDialog.LOADING_POINT_ALPHA);
        } else if (view.getId() == R.id.tv_126) {
            PromptDialog.newInstance(this)
                    .showLoading(PromptDialog.LOADING_POINT_TRANS);
        } else if (view.getId() == R.id.tv_127) {
            PromptDialog.newInstance(this)
                    .showLoading(PromptDialog.LOADING_POINT_SCALE);
        } else if (view.getId() == R.id.tv_128) {
            PromptDialog.newInstance(this)
                    .showLoading(PromptDialog.LOADING_RECT_SCALE);
        } else if (view.getId() == R.id.tv_129) {
            PromptDialog.newInstance(this)
                    .showLoading(PromptDialog.LOADING_RECT_ALPHA);
        }
    }

    private void setPressedBackground(View viewBackground, int color) {
        DrawableHelper.radius(20).pressed(color, R.attr.colorSecondary).into(viewBackground);
    }

    private void setRippleBackground(View viewBackground, int color) {
        DrawableHelper.radius(20).ripple(color).into(viewBackground);
    }
}
