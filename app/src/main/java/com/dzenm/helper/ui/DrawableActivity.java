package com.dzenm.helper.ui;

import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;

import com.dzenm.helper.R;
import com.dzenm.helper.databinding.ActivityDrawableBinding;
import com.dzenm.lib.base.AbsBaseActivity;
import com.dzenm.lib.drawable.DrawableHelper;
import com.dzenm.lib.file.FileHelper;

public class DrawableActivity extends AbsBaseActivity {

    private ActivityDrawableBinding binding;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = DataBindingUtil.setContentView(this, R.layout.activity_drawable);
        setToolbarWithGradientStatusBar(binding.toolbar,
                DrawableHelper.orientation(GradientDrawable.Orientation.LEFT_RIGHT)
                        .gradient(android.R.color.holo_orange_light,
                                android.R.color.holo_blue_light).build());

        DrawableHelper.orientation(GradientDrawable.Orientation.BOTTOM_TOP)
                .gradient(android.R.color.holo_red_light,
                        android.R.color.holo_blue_light,
                        android.R.color.holo_orange_light)
                .into(binding.linearGradient);
        DrawableHelper.radialRadius(80)
                .gradient(android.R.color.holo_red_light,
                        android.R.color.holo_blue_light,
                        android.R.color.holo_orange_light)
                .into(binding.orgialGradient);
        DrawableHelper.gradient(android.R.color.holo_red_light,
                android.R.color.holo_blue_light,
                android.R.color.holo_orange_light)
                .into(binding.sweepGradient);

        FileHelper.getInstance().setUserFolder("/dinzhenyan").getUserFile("/personalAccount");
        binding.ripple.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        DrawableHelper.radius(20).ripple(android.R.color.holo_blue_bright,
                R.attr.dialogPrimaryTextColor
        ).into(binding.ripple);

        binding.updateView.setMax(100);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                count++;
                binding.updateView.setProgress(count);
            }
        }, 500);
    }

    private int count = 0;
}
