package com.dzenm.helper.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.dzenm.helper.R;
import com.dzenm.helper.databinding.FragmentPersonalBinding;
import com.dzenm.helper.util.Urls;
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

        int activeColor = ThemeHelper.getColor(mActivity, R.attr.activeColor);
        int inactiveColor = ThemeHelper.getColor(mActivity, R.attr.inactiveColor);
        DrawableHelper.radius(8).pressed(activeColor, inactiveColor)
                .into(binding.tvDraw);
        DrawableHelper.radius(8).pressed(activeColor, inactiveColor)
                .into(binding.tvPreview);
        DrawableHelper.radiusBL(20f)
                .radiusTR(20f)
                .pressed(android.R.color.holo_purple, android.R.color.holo_red_light)
                .into(binding.tvDrawableStroke);

        binding.tvDraw.setOnClickListener(this);
        binding.tvPreview.setOnClickListener(this);
        binding.ivHeader.setOnClickListener(this);
        binding.tvDrawableStroke.setOnClickListener(this);

        binding.tvDraw.setTextColor(ThemeHelper.getColor(mActivity, R.attr.dialogButtonTextColor));
        binding.tvPreview.setTextColor(ThemeHelper.getColor(mActivity, R.attr.dialogButtonTextColor));
        binding.tvDrawableStroke.setTextColor(ThemeHelper.getColor(mActivity, R.attr.primaryTextColor));

        Glide.with(this).load(Urls.URL_1).into(binding.ivHeader);
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
                    .load(Urls.URL_2)
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
            mActivity.toggleLocalTheme();
        }
    }
}
