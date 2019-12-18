package com.dzenm.ui;

import android.os.Bundle;
import android.view.Gravity;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.databinding.ViewDataBinding;

import com.dzenm.R;
import com.dzenm.databinding.ActivityDialogBinding;
import com.dzenm.helper.base.AbsBaseActivity;
import com.dzenm.helper.databinding.DialogInfoBinding;
import com.dzenm.helper.dialog.AbsDialogFragment;
import com.dzenm.helper.dialog.DatePickerDialog;
import com.dzenm.helper.dialog.DialogHelper;
import com.dzenm.helper.dialog.EditDialog;
import com.dzenm.helper.dialog.InfoDialog;
import com.dzenm.helper.dialog.LoginDialog;
import com.dzenm.helper.dialog.MenuDialog;
import com.dzenm.helper.dialog.PhotoDialog;
import com.dzenm.helper.dialog.PromptDialog;
import com.dzenm.helper.dialog.UpGradeDialog;
import com.dzenm.helper.dialog.ViewHolder;
import com.dzenm.helper.download.DownloadHelper;
import com.dzenm.helper.draw.DrawableHelper;
import com.dzenm.helper.net.NetworkHelper;
import com.dzenm.helper.photo.PhotoSelector;
import com.dzenm.helper.popup.PopupDialog;
import com.dzenm.helper.toast.ToastHelper;

public class DialogActivity extends AbsBaseActivity implements View.OnClickListener {

    @Override
    protected boolean isDataBinding() {
        return true;
    }

    @Override
    protected int layoutId() {
        return R.layout.activity_dialog;
    }

    @Override
    protected void initializeView(@Nullable Bundle savedInstanceState, @Nullable ViewDataBinding viewDataBinding) {
        ActivityDialogBinding binding = (ActivityDialogBinding) viewDataBinding;
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
            DialogHelper.newInstance(this)
                    .setLayout(R.layout.dialog_info)
                    .setOnViewDataBinding(new DialogHelper.OnViewDataBinding() {
                        @Override
                        public void onBinding(ViewDataBinding binding, final AbsDialogFragment dialog) {
                            DialogInfoBinding infoBinding = (DialogInfoBinding) binding;
                            infoBinding.etMessage.setVisibility(View.VISIBLE);
                            infoBinding.etMessage.setText("这是使用dataBinding的结果");
                            infoBinding.tvNegative.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    dialog.dismiss();
                                }
                            });
                            infoBinding.tvPositive.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    dialog.dismiss();
                                }
                            });
                        }
                    }).show();
        } else if (view.getId() == R.id.tv_102) {
            InfoDialog.newInstance(this)
                    .setTitle("设置标题")
                    .setMessage("Material Design样式下的dialog必须有标题的, 如果没有设置, 将有一个默认的标题：\"温馨提示\", " +
                            "其它样式是可以设置无标题的, 千万别忘了设置message, 否则将不会有任务提示出现在dialog, 仅仅有两个按钮存在")
                    .setMaterialDesign(true)
                    .setOnClickListener(new InfoDialog.OnClickListener<InfoDialog>() {
                        @Override
                        public boolean onClick(final InfoDialog dialog, boolean confirm) {
                            if (confirm) {
                                InfoDialog.newInstance(DialogActivity.this)
                                        .setMessage("这是一个Material Design样式的dialog, 在代码里未设置标题, " +
                                                "但是居然还出现了一个默认的标题。")
                                        .setMaterialDesign(true)
                                        .setTouchInOutSideCancel(true)
                                        .show();
                            } else {
                                InfoDialog.newInstance(DialogActivity.this)
                                        .setMessage("这是一个非Material Design样式的dialog, 没有设置标题时, " +
                                                "不会显示标题")
                                        .setTouchInOutSideCancel(true)
                                        .show();
                            }
                            return true;
                        }
                    }).show();
        } else if (view.getId() == R.id.tv_103) {
            InfoDialog.newInstance(this)
                    .setTitle("设置按钮的文本和颜色")
                    .setMessage("按钮的文本可以自定义, 颜色也可以自己定义, 下面将为你演示一个自定义文本和颜色的dialog")
                    .setButtonTextColor(android.R.color.holo_blue_light)
                    .setButtonText("文本", "颜色")
                    .setMaterialDesign(true)
                    .setOnClickListener(new InfoDialog.OnClickListener<InfoDialog>() {
                        @Override
                        public boolean onClick(InfoDialog dialog, boolean confirm) {
                            if (confirm) {
                                InfoDialog.newInstance(DialogActivity.this)
                                        .setTitle("设置按钮文本")
                                        .setMessage("本机IP地址: " + NetworkHelper.getIPAddress(DialogActivity.this))
                                        .setButtonText("这是确定按钮", "这是取消按钮")
                                        .setBackground(DrawableHelper.solid(android.R.color.holo_blue_bright).radius(8).build())
                                        .setTouchInOutSideCancel(true)
                                        .setGravity(Gravity.BOTTOM)
                                        .show();
                            } else {
                                InfoDialog.newInstance(DialogActivity.this)
                                        .setTitle("设置按钮颜色")
                                        .setMessage("本机IP地址: " + NetworkHelper.getIPAddress(DialogActivity.this))
                                        .setButtonTextColor(R.color.colorDarkBlue,
                                                android.R.color.holo_red_dark)
                                        .setBackground(DrawableHelper.solid(android.R.color.holo_orange_light)
                                                .radius(8).build())
                                        .setTouchInOutSideCancel(true)
                                        .setGravity(Gravity.TOP)
                                        .show();

                            }
                            return true;
                        }
                    }).setTouchInOutSideCancel(true)
                    .show();
        } else if (view.getId() == R.id.tv_104) {
            InfoDialog.newInstance(this)
                    .setTitle("设置按钮")
                    .setMessage("有时候存在只需要一个按钮的情况, 所以预置了单按钮的选项, 只需要设置一个按钮的文本内容, " +
                            "将会自动只显示一个按钮")
                    .setButtonText("普通样式", "Material Design")
                    .setOnClickListener(new InfoDialog.OnClickListener<InfoDialog>() {
                        @Override
                        public boolean onClick(InfoDialog dialog, boolean confirm) {
                            if (confirm) {
                                InfoDialog.newInstance(DialogActivity.this)
                                        .setTitle("温馨提示")
                                        .setMessage("这是普通效果的单按钮设置")
                                        .setButtonText("确定")
                                        .setPrimaryColor(R.color.colorDarkBlue)
                                        .show();
                            } else {
                                InfoDialog.newInstance(DialogActivity.this)
                                        .setMessage("这是Material Design样式下的单按钮效果")
                                        .setButtonText("确定")
                                        .setMaterialDesign(true)
                                        .show();
                            }
                            return false;
                        }
                    }).show();
        } else if (view.getId() == R.id.tv_105) {
            InfoDialog.newInstance(this)
                    .setTitle("设置圆角")
                    .setMessage("直接设置圆角, 通过setRadiusCard方法设置, 如果设置了背景, 那么将会复杂一些, " +
                            "设置了背景之后也还是需要设置setRadiusCard, 因为按钮点击时有按压效果的圆角")
                    .setButtonText("圆角", "背景加圆角")
                    .setOnClickListener(new InfoDialog.OnInfoClickListener() {
                        @Override
                        public boolean onClick(InfoDialog dialog, boolean confirm) {
                            if (confirm) {
                                InfoDialog.newInstance(DialogActivity.this)
                                        .setTitle("设置圆角")
                                        .setMessage("这是Material Design样式下的单按钮效果")
                                        .setRadiusCard(4f)
                                        .show();
                            } else {
                                InfoDialog.newInstance(DialogActivity.this)
                                        .setMessage("这是Material Design样式下的单按钮效果")
                                        .setMaterialDesign(true)
                                        .setBackground(DrawableHelper.solid(android.R.color.holo_green_light).radius(20f).build())
                                        .setRadiusCard(20f)
                                        .show();
                            }
                            return true;
                        }
                    })
                    .show();
        } else if (view.getId() == R.id.tv_106) {
            InfoDialog.newInstance(this)
                    .setTitle("设置边距")
                    .setMessage("当dialog显示在center时, 基本上不需要设置margin, 但如果一定需要设置, " +
                            "也可以通过setMargin方法设置, 一般显示的bottom或者top时, 设置margin可以显示出不一样的效果")
                    .setButtonText("无边距", "有边距")
                    .setOnClickListener(new InfoDialog.OnClickListener<InfoDialog>() {
                        @Override
                        public boolean onClick(InfoDialog dialog, boolean confirm) {
                            if (confirm) {
                                InfoDialog.newInstance(DialogActivity.this)
                                        .setTitle("温馨提示")
                                        .setMessage("显示在底部,无边距,可以取消")
                                        .setGravity(Gravity.BOTTOM)
                                        .setBackground(DrawableHelper.solid(android.R.color.holo_orange_light).radius(0).build())
                                        .setMargin(0)
                                        .setTouchInOutSideCancel(true)
                                        .show();
                            } else {
                                InfoDialog.newInstance(DialogActivity.this)
                                        .setTitle("温馨提示")
                                        .setMessage("显示在底部,有边距,可以取消")
                                        .setGravity(Gravity.BOTTOM)
                                        .setBackground(DrawableHelper.solid(android.R.color.holo_blue_light).radius(20).build())
                                        .setMargin(20)
                                        .setRadiusCard(20)
                                        .setTouchInOutSideCancel(true)
                                        .show();
                            }
                            return false;
                        }
                    }).show();
        } else if (view.getId() == R.id.tv_107) {
            InfoDialog.newInstance(this)
                    .setTitle("设置样式")
                    .setMessage("预置了dialog多种样式, 包括Material Design和像ISO的dialog一样存在分割线, 或者无分割线, " +
                            "当前显示的是一个IOS样式的dialog")
                    .setButtonText("无分割线", "Material Design")
                    .setDivide(true)
                    .setOnClickListener(new InfoDialog.OnClickListener<InfoDialog>() {
                        @Override
                        public boolean onClick(InfoDialog dialog, boolean confirm) {
                            if (confirm) {
                                InfoDialog.newInstance(DialogActivity.this)
                                        .setTitle("设置样式")
                                        .setMessage("类IOS风格, 无分割线")
                                        .setBackground(DrawableHelper.solid(android.R.color.holo_purple).radius(8).build())
                                        .setTouchInOutSideCancel(true)
                                        .show();
                            } else {
                                InfoDialog.newInstance(DialogActivity.this)
                                        .setTitle("设置样式")
                                        .setMessage("这是一个Material Design样式的dialog")
                                        .setMaterialDesign(true)
                                        .setBackground(DrawableHelper.solid(android.R.color.holo_blue_bright).radius(8).build())
                                        .setTouchInOutSideCancel(true)
                                        .show();
                            }
                            return false;
                        }
                    }).show();
        } else if (view.getId() == R.id.tv_1071) {
            InfoDialog.newInstance(this)
                    .setTitleIcon(R.drawable.ic_warm)
                    .setMessage("dialog的位置是通过Gravity类来设置, 常用的有bottom和top")
                    .show();
        } else if (view.getId() == R.id.tv_108) {
            InfoDialog.newInstance(this)
                    .setTitle("设置编辑框的位置")
                    .setMessage("dialog的位置是通过Gravity类来设置, 常用的有bottom和top")
                    .setButtonText("bottom", "top")
                    .setMaterialDesign(true)
                    .setOnClickListener(new InfoDialog.OnClickListener<InfoDialog>() {
                        @Override
                        public boolean onClick(InfoDialog dialog, boolean confirm) {
                            if (confirm) {
                                EditDialog.newInstance(DialogActivity.this)
                                        .setTitle("测试")
                                        .setMessage("底部编辑框")
                                        .setBackground(DrawableHelper.solid(android.R.color.holo_green_dark).radius(8).build())
                                        .setTouchInOutSideCancel(true)
                                        .setGravity(Gravity.BOTTOM)
                                        .show();
                            } else {
                                EditDialog.newInstance(DialogActivity.this)
                                        .setTitle("测试")
                                        .setMessage("顶部编辑框")
                                        .setTouchInOutSideCancel(true)
                                        .setGravity(Gravity.TOP)
                                        .show();
                            }
                            return false;
                        }
                    }).show();
        } else if (view.getId() == R.id.tv_109) {
            InfoDialog.newInstance(this)
                    .setMessage("查看编辑框显示风格")
                    .setButtonText("无边框", "有边框")
                    .setOnClickListener(new InfoDialog.OnClickListener<InfoDialog>() {
                        @Override
                        public boolean onClick(InfoDialog dialog, boolean confirm) {
                            if (confirm) {
                                EditDialog.newInstance(DialogActivity.this)
                                        .setMessage("无边框")
                                        .setBackground(DrawableHelper.solid(android.R.color.holo_orange_dark).radius(8).build())
                                        .setTouchInOutSideCancel(true)
                                        .setGravity(Gravity.BOTTOM)
                                        .show();
                            } else {
                                EditDialog.newInstance(DialogActivity.this)
                                        .setMessage("有边框")
                                        .setDivide(true)
                                        .setTouchInOutSideCancel(true)
                                        .show();
                            }
                            return false;
                        }
                    }).show();
        } else if (view.getId() == R.id.tv_110) {
            String url = "https://downpack.baidu.com/appsearch_AndroidPhone_v8.0.3(1.0.65.172)_1012271b.apk";
            UpGradeDialog.newInstance(this)
                    .setUrl(url)
                    .setDesc("我也不知道更新了什么")
                    .setSize("25.9M")
                    .setVersionName("v2.8")
                    .setNewVersionCode(3)
                    .update();
        } else if (view.getId() == R.id.tv_1101) {
            String url = "https://downpack.baidu.com/appsearch_AndroidPhone_v8.0.3(1.0.65.172)_1012271b.apk";
            ToastHelper.show("开始下载...");
            DownloadHelper.newInstance(this)
                    .setUrl(url)
                    .startDownload();
        } else if (view.getId() == R.id.tv_111) {
            PhotoDialog.newInstance(this).setOnSelectPhotoListener(new PhotoSelector.OnSelectPhotoListener() {
                @Override
                public boolean onCrop(PhotoSelector helper, String filePath) {
                    logD("the filePath: " + filePath);
//                    String file = FileHelper.getInstance().getPath("/photo") + "/d.jpeg";
//                    FileHelper.getInstance().copyFile(filePath, file);
//                    FileHelper.getInstance().refreshGallery(filePath);
                    return true;
                }
            }).show();
        } else if (view.getId() == R.id.tv_112) {
            MenuDialog.newInstance(this)
                    .setItem("测试", "第二个", "取消", "确定")
                    .setOnItemClickListener(new MenuDialog.OnItemClickListener() {
                        @Override
                        public void onItemClick(int position) {
                            if (position == 0) {
                                ToastHelper.show("测试");
                            } else if (position == 2) {
                                ToastHelper.show("取消");
                            }
                        }
                    }).setRadiusCard(2f)
                    .show();
        } else if (view.getId() == R.id.tv_113) {
            InfoDialog.newInstance(this)
                    .setTitle("提示")
                    .setMessage("PopupWindow覆盖在dialog之上")
                    .setGravity(Gravity.TOP)
                    .setOnClickListener(new InfoDialog.OnInfoClickListener() {
                        @Override
                        public boolean onClick(InfoDialog dialog, boolean confirm) {
                            if (confirm) {
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
                                        .showAsDropDown(dialog.getView(), 100, 0);
                            } else {
                                dialog.dismiss();
                            }
                            return false;
                        }
                    }).show();
        } else if (view.getId() == R.id.tv_114) {
            InfoDialog.newInstance(this)
                    .setTitle("提示")
                    .setMessage("日期选择器")
                    .setButtonText("循环查看", "指定初始日期")
                    .setOnClickListener(new InfoDialog.OnInfoClickListener() {
                        @Override
                        public boolean onClick(InfoDialog dialog, boolean confirm) {
                            if (confirm) {
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
                            return false;
                        }
                    }).show();
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
                    .show("正在加载中, 请稍后...", R.drawable.one, true);
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
        DrawableHelper.radius(20).pressed(color, R.color.colorGray).into(viewBackground);
    }

    private void setRippleBackground(View viewBackground, int color) {
        DrawableHelper.radius(20).ripple(color).into(viewBackground);
    }
}
