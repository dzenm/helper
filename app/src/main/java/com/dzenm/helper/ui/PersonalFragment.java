package com.dzenm.helper.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;

import com.dzenm.helper.R;
import com.dzenm.helper.databinding.FragmentPersonalBinding;
import com.dzenm.lib.base.AbsBaseFragment;
import com.dzenm.lib.dialog.PreviewDialog;
import com.dzenm.lib.drawable.DrawableHelper;
import com.dzenm.lib.os.ThemeHelper;

/**
 * @author dzenm
 * @date 2019-09-12 16:57
 */
public class PersonalFragment extends AbsBaseFragment<MainActivity> implements View.OnClickListener {

    private FragmentPersonalBinding binding;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, @Nullable Bundle savedInstanceState) {
        logD("onCreateView: ");
        binding = FragmentPersonalBinding.inflate(inflater);
        setToolbarWithImmersiveStatusBar(binding.toolbar, android.R.color.transparent);

        int colorActive = ThemeHelper.getColor(mActivity, R.attr.colorActive);
        int colorInactive = ThemeHelper.getColor(mActivity, R.attr.colorInactive);
        DrawableHelper.radius(8).pressed(colorActive, colorInactive)
                .into(binding.tvDraw);
        DrawableHelper.radius(8).pressed(colorActive, colorInactive)
                .into(binding.tvPreview);
        DrawableHelper
                .radiusBL(20f)
                .radiusTR(20f)
                .pressed(android.R.color.holo_purple, android.R.color.holo_red_light)
                .into(binding.tvDrawableStroke);

        binding.tvDraw.setOnClickListener(this);
        binding.tvPreview.setOnClickListener(this);
        binding.ivHeader.setOnClickListener(this);
        binding.tvDrawableStroke.setOnClickListener(this);

        binding.tvDraw.setTextColor(ThemeHelper.getColor(mActivity, R.attr.colorDialogButtonText));
        binding.tvPreview.setTextColor(ThemeHelper.getColor(mActivity, R.attr.colorDialogButtonText));
        binding.tvDrawableStroke.setTextColor(ThemeHelper.getColor(mActivity, R.attr.colorPrimaryText));

        return binding.getRoot();
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

                        }
                    })
                    .show();
        } else if (v.getId() == R.id.iv_header) {
            PreviewDialog.newInstance(mActivity)
                    .setImageLoader(new MyImageLoader())
                    .load(binding.ivHeader.getDrawable())
                    .show();
        } else if (v.getId() == R.id.tv_drawable_stroke) {
            int t = ThemeHelper.getTheme();
            mActivity.toggleTheme(t == R.style.AppTheme_Dark ? R.style.AppTheme_Light :
                    R.style.AppTheme_Dark);
        }
    }
}
