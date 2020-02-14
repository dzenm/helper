package com.dzenm.ui;

import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.PopupWindow;

import androidx.annotation.Nullable;

import com.dzenm.R;
import com.dzenm.databinding.ActivityOthersBinding;
import com.dzenm.helper.base.AbsActivity;
import com.dzenm.helper.dialog.EditDialog;
import com.dzenm.helper.dialog.InfoDialog;
import com.dzenm.helper.dialog.ViewHolder;
import com.dzenm.helper.draw.DrawableHelper;
import com.dzenm.helper.file.FileHelper;
import com.dzenm.helper.os.ScreenHelper;
import com.dzenm.helper.photo.PhotoSelector;
import com.dzenm.helper.popup.DropDownMenu;
import com.dzenm.helper.popup.PopupDialog;
import com.dzenm.helper.share.ShareHelper;
import com.dzenm.helper.toast.ToastHelper;

public class OthersActivity extends AbsActivity<ActivityOthersBinding> implements View.OnClickListener {

    @Override
    protected int layoutId() {
        return R.layout.activity_others;
    }

    @Override
    protected void initializeView(@Nullable Bundle savedInstanceState) {
        super.initializeView(savedInstanceState);
        setToolbarWithImmersiveStatusBar(getBinding().toolbar, R.color.colorAccent);

        setPressedBackground(getBinding().tv100, android.R.color.holo_blue_dark);
        setRippleBackground(getBinding().tv101, android.R.color.holo_red_dark);
        setPressedBackground(getBinding().tv102, android.R.color.holo_green_dark);
        setRippleBackground(getBinding().tv103, android.R.color.holo_orange_dark);
        setPressedBackground(getBinding().tv104, android.R.color.holo_blue_light);
        setRippleBackground(getBinding().tv105, android.R.color.holo_red_light);
        setPressedBackground(getBinding().tv106, android.R.color.holo_green_light);
        setRippleBackground(getBinding().tv107, android.R.color.holo_orange_light);
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
                                        .with(OthersActivity.this)
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
            PhotoSelector.getInstance().with(this).setOnSelectPhotoListener(new PhotoSelector.OnSelectPhotoListener() {
                @Override
                public boolean onGallery(PhotoSelector helper, String filePath) {
                    InfoDialog.newInstance(OthersActivity.this)
                            .setTitle("图片选择回调")
                            .setMessage(filePath)
                            .setOnClickListener(null)
                            .show();
                    getBinding().ivImage.setImageBitmap(FileHelper.getInstance().getPhoto(filePath));
                    return false;
                }
            }).gallery();
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
        } else if (view.getId() == R.id.tv_108) {

        } else if (view.getId() == R.id.tv_109) {

        } else if (view.getId() == R.id.tv_menu) {
            new DropDownMenu.Builder(this)
                    .setView(R.layout.dialog_login)
                    .setOnBindViewHolder(new DropDownMenu.OnBindViewHolder() {
                        @Override
                        public void onBinding(ViewHolder holder, final DropDownMenu dialog) {
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
                    .showAsDropDown(getBinding().tvMenu);
        }
    }

    private void setPressedBackground(View viewBackground, int color) {
        DrawableHelper.radius(10).pressed(color, R.color.colorGray).into(viewBackground);
    }

    private void setRippleBackground(View viewBackground, int color) {
        DrawableHelper.radius(10).ripple(color).into(viewBackground);
    }
}
