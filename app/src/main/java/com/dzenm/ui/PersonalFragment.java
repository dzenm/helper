package com.dzenm.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.databinding.ViewDataBinding;

import com.dzenm.R;
import com.dzenm.databinding.FragmentPersonalBinding;
import com.dzenm.helper.base.AbsBaseFragment;
import com.dzenm.helper.dialog.MenuDialog;
import com.dzenm.helper.dialog.PreviewDialog;
import com.dzenm.helper.draw.DrawableHelper;
import com.dzenm.helper.toast.ToastHelper;

/**
 * @author dzenm
 * @date 2019-09-12 16:57
 */
public class PersonalFragment extends AbsBaseFragment<MainActivity> implements View.OnClickListener {

    private FragmentPersonalBinding binding;

    @Override
    protected int layoutId() {
        return R.layout.fragment_personal;
    }

    @Override
    protected boolean isDataBinding() {
        return true;
    }

    @Override
    public void initializeView(Bundle savedInstanceState, ViewDataBinding viewDataBinding) {
        binding = (FragmentPersonalBinding) viewDataBinding;
        setToolbarWithImmersiveStatusBar(binding.toolbar, android.R.color.transparent);

        DrawableHelper.radius(8).pressed(R.color.colorDarkBlue, R.color.colorTranslucentDarkBlue)
                .into(binding.tvDraw);
        DrawableHelper.radius(8).pressed(R.color.colorDarkBlue, R.color.colorTranslucentDarkBlue)
                .into(binding.tvPreview);

        logD("test log");
        binding.tvDraw.setOnClickListener(this);
        binding.tvPreview.setOnClickListener(this);
        binding.ivHeader.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.tv_draw) {
            Intent intent = new Intent();
            startActivity(intent);
        } else if (v.getId() == R.id.tv_preview) {
            PreviewDialog.newInstance(mActivity)
                    .setImageLoader(new MyImageLoader())
                    .load(R.drawable.one)
                    .setOnLongClickListener(new PreviewDialog.OnLongClickListener() {
                        @Override
                        public void onLongClick() {
                            MenuDialog.newInstance(mActivity)
                                    .setItem("测试", "第二个", "取消", "确定")
                                    .setRadiusCard(2f)
                                    .setOnItemClickListener(new MenuDialog.OnItemClickListener() {
                                        @Override
                                        public void onItemClick(int position) {
                                            if (position == 0) {
                                                ToastHelper.show("测试");
                                            } else if (position == 3) {
                                                ToastHelper.show("取消");
                                            }
                                        }
                                    }).show();
                        }
                    })
                    .show();
        } else if (v.getId() == R.id.iv_header) {
            PreviewDialog.newInstance(mActivity)
                    .setImageLoader(new MyImageLoader())
                    .load(binding.ivHeader.getDrawable())
                    .show();
        }
    }
}
