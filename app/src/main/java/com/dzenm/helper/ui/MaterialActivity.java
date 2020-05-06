package com.dzenm.helper.ui;

import android.os.Bundle;
import android.view.Gravity;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;

import com.dzenm.helper.R;
import com.dzenm.helper.databinding.ActivityMaterialBinding;
import com.dzenm.lib.base.AbsBaseActivity;
import com.dzenm.lib.dialog.ViewHolder;
import com.dzenm.lib.drawable.DrawableHelper;
import com.dzenm.lib.material.MaterialDialog;
import com.dzenm.lib.material.UpGradeView;
import com.dzenm.lib.photo.PhotoSelector;
import com.dzenm.lib.popupwindow.DropDownMenu;
import com.dzenm.lib.toast.ToastHelper;

public class MaterialActivity extends AbsBaseActivity {

    private ActivityMaterialBinding binding;
    private final String[] mItems = new String[]{"Item 1", "Item 2", "Item 3", "Item 4"};

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_material);
        setToolbarWithImmersiveStatusBar(binding.toolbar, R.color.colorPrimary);

        DrawableHelper.radius(8).ripple(android.R.color.holo_red_dark, R.color.colorDivide).into(binding.tv11);
        DrawableHelper.radius(8).pressed(android.R.color.holo_green_dark, R.color.colorDivide).into(binding.tv12);
        DrawableHelper.radius(8).ripple(android.R.color.holo_blue_bright, R.color.colorDivide).into(binding.tv13);
        DrawableHelper.radius(8).ripple(android.R.color.holo_red_light, R.color.colorDivide).into(binding.tv14);
        DrawableHelper.radius(8).ripple(android.R.color.holo_orange_dark, R.color.colorDivide).into(binding.tv15);

        DrawableHelper.radius(8).pressed(android.R.color.holo_green_light, R.color.colorDivide).into(binding.tv111);
        DrawableHelper.radius(8).ripple(android.R.color.holo_blue_bright, R.color.colorDivide).into(binding.tv121);
        DrawableHelper.radius(8).pressed(android.R.color.holo_orange_light, R.color.colorDivide).into(binding.tv131);
        DrawableHelper.radius(8).pressed(android.R.color.holo_red_dark, R.color.colorDivide).into(binding.tv141);

        DrawableHelper.radius(8).pressed(android.R.color.holo_blue_dark, R.color.colorDivide).into(binding.tv21);
        DrawableHelper.radius(8).pressed(android.R.color.holo_orange_dark, R.color.colorDivide).into(binding.tv22);
        DrawableHelper.radius(8).pressed(android.R.color.holo_red_dark, R.color.colorDivide).into(binding.tv23);
        DrawableHelper.radius(8).pressed(android.R.color.holo_green_dark, R.color.colorDivide).into(binding.tv24);
        DrawableHelper.radius(8).pressed(android.R.color.holo_red_light, R.color.colorDivide).into(binding.tv25);
        DrawableHelper.radius(8).pressed(android.R.color.holo_green_light, R.color.colorDivide).into(binding.tv26);

        DrawableHelper.radius(8).pressed(android.R.color.holo_orange_light, R.color.colorDivide).into(binding.tv40);
        DrawableHelper.radius(8).pressed(android.R.color.holo_red_light, R.color.colorDivide).into(binding.tv41);
        DrawableHelper.radius(8).pressed(android.R.color.holo_blue_dark, R.color.colorDivide).into(binding.tv42);

        DrawableHelper.radius(8).pressed(android.R.color.holo_orange_light, R.color.colorDivide).into(binding.tv43);
        DrawableHelper.radius(8).pressed(android.R.color.holo_red_light, R.color.colorDivide).into(binding.tv44);
        DrawableHelper.radius(8).pressed(android.R.color.holo_blue_dark, R.color.colorDivide).into(binding.tv45);

    }

    public void onClick(View view) {
        if (view.getId() == R.id.tv_drop_menu) {
            new DropDownMenu.Builder(this)
                    .setView(R.layout.dialog_login)
                    .setOnBindViewHolder(new DropDownMenu.OnBindViewHolder() {
                        @Override
                        public void onBinding(ViewHolder holder, final DropDownMenu.Builder dialog) {
                            holder.getView(R.id.tv_confirm).setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    ToastHelper.show("登录成功");
                                    dialog.dismiss();
                                }
                            });
                            holder.getView(R.id.tv_cancel).setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    dialog.dismiss();
                                }
                            });
                        }
                    }).create()
                    .showAsDropDown(binding.tvDropMenu);
        } else if (view.getId() == R.id.tv_11) {
            new MaterialDialog.Builder(this)
                    .setTitle("Title")
                    .setNeutralClickListener("CANCEL", new MaterialDialog.OnClickListener() {
                        @Override
                        public void onClick(MaterialDialog dialog, int which) {
                            dialog.dismiss();
                        }
                    }).setPositiveClickListener("ACCEPT", new MaterialDialog.OnClickListener() {
                @Override
                public void onClick(MaterialDialog dialog, int which) {
                    dialog.dismiss();
                }
            }).create().show();
        } else if (view.getId() == R.id.tv_12) {
            new MaterialDialog.Builder(this)
                    .setMessage("Message")
                    .setButtonText("ACCECP", "DECLINE")
                    .setOnClickListener(new MaterialDialog.OnClickListener() {
                        @Override
                        public void onClick(MaterialDialog dialog, int which) {
                            dialog.dismiss();
                        }
                    }).create().show();
        } else if (view.getId() == R.id.tv_13) {
            new MaterialDialog.Builder(this)
                    .setTitle("Title")
                    .setMessage("Message")
                    .setButtonText("ACCECP", "DECLINE")
                    .setOnClickListener(new MaterialDialog.OnClickListener() {
                        @Override
                        public void onClick(MaterialDialog dialog, int which) {
                            dialog.dismiss();
                        }
                    }).create().show();
        } else if (view.getId() == R.id.tv_14) {
            new MaterialDialog.Builder(this)
                    .setTitle("Title")
                    .setMessage("Message")
                    .setIcon(R.drawable.ic_warm)
                    .setButtonText("ACCECP", "DECLINE")
                    .setOnClickListener(new MaterialDialog.OnClickListener() {
                        @Override
                        public void onClick(MaterialDialog dialog, int which) {
                            dialog.dismiss();
                        }
                    }).create().show();
        } else if (view.getId() == R.id.tv_15) {
            new MaterialDialog.Builder(this)
                    .setTitle("Title")
                    .setMessage("Message")
                    .setButtonText("ACCECP", "DECLINE", "NEUTRAL")
                    .setOnClickListener(new MaterialDialog.OnClickListener() {
                        @Override
                        public void onClick(MaterialDialog dialog, int which) {
                            dialog.dismiss();
                        }
                    }).create().show();

        } else if (view.getId() == R.id.tv_111) {
            new MaterialDialog.Builder(this)
                    .setTitle("Title")
                    .setMaterialDesign(false)
                    .setButtonText("ACCECP")
                    .setOnClickListener(new MaterialDialog.OnClickListener() {
                        @Override
                        public void onClick(MaterialDialog dialog, int which) {
                            dialog.dismiss();
                        }
                    }).create().show();
        } else if (view.getId() == R.id.tv_121) {
            new MaterialDialog.Builder(this)
                    .setMessage("Message")
                    .setMaterialDesign(false)
                    .setButtonText("ACCECP", "DECLINE")
                    .setOnClickListener(new MaterialDialog.OnClickListener() {
                        @Override
                        public void onClick(MaterialDialog dialog, int which) {
                            dialog.dismiss();
                        }
                    }).create().show();
        } else if (view.getId() == R.id.tv_131) {
            new MaterialDialog.Builder(this)
                    .setTitle("Title")
                    .setMessage("Message")
                    .setMaterialDesign(false)
                    .setButtonText("ACCECP", "DECLINE")
                    .setOnClickListener(new MaterialDialog.OnClickListener() {
                        @Override
                        public void onClick(MaterialDialog dialog, int which) {
                            dialog.dismiss();
                        }
                    }).create().show();
        } else if (view.getId() == R.id.tv_141) {
            new MaterialDialog.Builder(this)
                    .setIcon(R.drawable.ic_warm)
                    .setMessage("Message")
                    .setMaterialDesign(false)
                    .setButtonText("ACCECP", "DECLINE")
                    .setOnClickListener(new MaterialDialog.OnClickListener() {
                        @Override
                        public void onClick(MaterialDialog dialog, int which) {
                            dialog.dismiss();
                        }
                    }).create().show();
        } else if (view.getId() == R.id.tv_21) {
            new MaterialDialog.Builder(this)
                    .setTitle("Title")
                    .setItem(mItems)
                    .setButtonText("ACCECP", "DECLINE")
                    .setOnItemClickListener(new MaterialDialog.OnItemClickListener() {
                        @Override
                        public void onClick(MaterialDialog dialog, int which) {
                            ToastHelper.show(mItems[which]);
                        }
                    }).create().show();
        } else if (view.getId() == R.id.tv_22) {
            new MaterialDialog.Builder(this)
                    .setTitle("Title")
                    .setItem(mItems)
                    .setNeutralClickListener("CANCEL", new MaterialDialog.OnClickListener() {
                        @Override
                        public void onClick(MaterialDialog dialog, int which) {
                            dialog.dismiss();
                        }
                    })
                    .setPositiveClickListener("ACCEPT", new MaterialDialog.OnClickListener() {
                        @Override
                        public void onClick(MaterialDialog dialog, int which) {
                            dialog.dismiss();
                        }
                    })
                    .setOnSingleClickListener(new MaterialDialog.OnSingleClickListener() {
                        @Override
                        public void onClick(MaterialDialog dialog, int which, boolean isChecked) {
                            ToastHelper.show(mItems[which]);
                        }
                    }).create().show();
        } else if (view.getId() == R.id.tv_23) {
            new MaterialDialog.Builder(this)
                    .setTitle("Title")
                    .setItem(mItems)
                    .setNeutralClickListener("CANCEL", new MaterialDialog.OnClickListener() {
                        @Override
                        public void onClick(MaterialDialog dialog, int which) {
                            dialog.dismiss();
                        }
                    })
                    .setPositiveClickListener("ACCEPT", new MaterialDialog.OnClickListener() {
                        @Override
                        public void onClick(MaterialDialog dialog, int which) {
                            dialog.dismiss();
                        }
                    })
                    .setOnMultipleClickListener(new MaterialDialog.OnMultipleClickListener() {
                        @Override
                        public void onClick(MaterialDialog dialog, int which, boolean isChecked) {
                            ToastHelper.show(mItems[which]);
                        }
                    }).create().show();
        } else if (view.getId() == R.id.tv_24) {
            new MaterialDialog.Builder(this)
                    .setItem(mItems)
                    .setMaterialDesign(false)
                    .setGravity(Gravity.BOTTOM)
                    .setOnItemClickListener(new MaterialDialog.OnItemClickListener() {
                        @Override
                        public void onClick(MaterialDialog dialog, int which) {
                            ToastHelper.show(mItems[which]);
                            dialog.dismiss();
                        }
                    }).create().show();
        } else if (view.getId() == R.id.tv_25) {
            new MaterialDialog.Builder(this)
                    .setTitle("Title")
                    .setItem(mItems)
                    .setMaterialDesign(false)
                    .setGravity(Gravity.BOTTOM)
                    .setOnSingleClickListener(new MaterialDialog.OnSingleClickListener() {
                        @Override
                        public void onClick(MaterialDialog dialog, int which, boolean isChecked) {
                            ToastHelper.show(mItems[which]);
                            dialog.dismiss();
                        }
                    }).create().show();
        } else if (view.getId() == R.id.tv_26) {
            new MaterialDialog.Builder(this)
                    .setTitle("Title")
                    .setItem(mItems)
                    .setMaterialDesign(false)
                    .setGravity(Gravity.BOTTOM)
                    .setOnMultipleClickListener(new MaterialDialog.OnMultipleClickListener() {
                        @Override
                        public void onClick(MaterialDialog dialog, int which, boolean isChecked) {
                            ToastHelper.show(mItems[which]);
                            dialog.dismiss();
                        }
                    }).create().show();
        } else if (view.getId() == R.id.tv_40) {
            String message = "从方法的名称中可以看出该方法主要是负责分发，是安卓事件分发过程中的核心。事件是如何传递的，主要就是看该方法，理解了这个方法，也就理解了安卓事件分发机制。\n" +
                    "\n" +
                    "在了解该方法的核心机制之前，需要知道一个结论：\n" +
                    "\n" +
                    "如果某个组件的该方法返回TRUE,则表示该组件已经对事件进行了处理，不用继续调用其余组件的分发方法，即停止分发。\n" +
                    "如果某个组件的该方法返回FALSE,则表示该组件不能对该事件进行处理，需要按照规则继续分发事件。在不复写该方法的情况下，除了一些特殊的组件，其余组件都是默认返回False的。后续有例子说明。\n" +
                    "为何返回TRUE就不用继续分发，而返回FALSE就停止分发呢？为了解决这个疑问，需要看一看该方法的具体分发逻辑。为了便于理解，下面对dispatchTouchEvent方法进行简化，只保留最核心的逻辑。" +
                    "从方法的名称中可以看出该方法主要是负责分发，是安卓事件分发过程中的核心。事件是如何传递的，主要就是看该方法，理解了这个方法，也就理解了安卓事件分发机制。\n" +
                    "\n" +
                    "在了解该方法的核心机制之前，需要知道一个结论：\n" +
                    "\n" +
                    "如果某个组件的该方法返回TRUE,则表示该组件已经对事件进行了处理，不用继续调用其余组件的分发方法，即停止分发。\n" +
                    "如果某个组件的该方法返回FALSE,则表示该组件不能对该事件进行处理，需要按照规则继续分发事件。在不复写该方法的情况下，除了一些特殊的组件，其余组件都是默认返回False的。后续有例子说明。\n" +
                    "为何返回TRUE就不用继续分发，而返回FALSE就停止分发呢？为了解决这个疑问，需要看一看该方法的具体分发逻辑。为了便于理解，下面对dispatchTouchEvent方法进行简化，只保留最核心的逻辑。";
            new MaterialDialog.Builder(this)
                    .setMessage(message)
                    .setButtonText("ACCECP", "DECLINE")
                    .setOnClickListener(new MaterialDialog.OnClickListener() {
                        @Override
                        public void onClick(MaterialDialog dialog, int which) {
                            dialog.dismiss();
                        }
                    }).create().show();
        } else if (view.getId() == R.id.tv_41) {
            String url = "https://downpack.baidu.com/appsearch_AndroidPhone_v8.0.3(1.0.65.172)_1012271b.apk";
            UpGradeView upGradeView = new UpGradeView(this);
            upGradeView.setUrl(url)
                    .setVersionName("v1.1")
                    .setDesc("我也不知道更新了什么")
                    .setSize("43.8M")
                    .setCanCancel(false);
            new MaterialDialog.Builder(this).setView(upGradeView).create().show();
        } else if (view.getId() == R.id.tv_42) {
            String url = "https://downpack.baidu.com/appsearch_AndroidPhone_v8.0.3(1.0.65.172)_1012271b.apk";
            UpGradeView upGradeView = new UpGradeView(this);
            upGradeView.setUrl(url)
                    .setVersionName("v1.1")
                    .setDesc("我也不知道更新了什么")
                    .setSize("43.8M")
                    .setUpgradeImage(R.drawable.ic_upgrade_top)
                    .setCanCancel(false);
            new MaterialDialog.Builder(this).setView(upGradeView).create().show();
        } else if (view.getId() == R.id.tv_43) {
            new MaterialDialog.Builder(this)
                    .setItem("拍照", "图片", "取消")
                    .setOnSelectedPhotoListener(new PhotoSelector.OnSelectedPhotoListener() {
                        @Override
                        public boolean onGraph(PhotoSelector selector, String filePath) {
                            return super.onGraph(selector, filePath);
                        }

                        @Override
                        public boolean onGallery(PhotoSelector selector, String filePath) {
                            return super.onGallery(selector, filePath);
                        }
                    }).create().show();
        } else if (view.getId() == R.id.tv_44) {
            new MaterialDialog.Builder(this)
                    .setItem("拍照", "图片", "取消")
                    .setGravity(Gravity.BOTTOM)
                    .setOnSelectedPhotoListener(new PhotoSelector.OnSelectedPhotoListener() {
                        @Override
                        public boolean onGraph(PhotoSelector selector, String filePath) {
                            return super.onGraph(selector, filePath);
                        }

                        @Override
                        public boolean onGallery(PhotoSelector selector, String filePath) {
                            return super.onGallery(selector, filePath);
                        }
                    }).create().show();
        } else if (view.getId() == R.id.tv_45) {
            new MaterialDialog.Builder(this)
                    .setItem("拍照", "图片")
                    .setMaterialDesign(false)
                    .setOnSelectedPhotoListener(new PhotoSelector.OnSelectedPhotoListener() {
                        @Override
                        public boolean onGraph(PhotoSelector selector, String filePath) {
                            return super.onGraph(selector, filePath);
                        }

                        @Override
                        public boolean onGallery(PhotoSelector selector, String filePath) {
                            return super.onGallery(selector, filePath);
                        }
                    }).create().show();
        }
    }
}
