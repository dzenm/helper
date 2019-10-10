package com.dzenm.ui;

import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;

import android.os.Bundle;
import android.view.View;

import com.dzenm.LottieDialog;
import com.dzenm.R;
import com.dzenm.databinding.ActivityLoadingBinding;
import com.dzenm.helper.base.AbsBaseActivity;
import com.dzenm.helper.draw.DrawableHelper;

public class LoadingActivity extends AbsBaseActivity implements View.OnClickListener {

    @Override
    protected void initializeView(@Nullable Bundle savedInstanceState, @Nullable ViewDataBinding viewDataBinding) {
        ActivityLoadingBinding binding = DataBindingUtil.setContentView(this, R.layout.activity_loading);
        setToolbarWithImmersiveStatusBar(binding.toolbar, android.R.color.transparent);

        setPressedBackground(binding.tv131, android.R.color.holo_blue_dark);
        setRippleBackground(binding.tv132, android.R.color.holo_red_dark);
        setPressedBackground(binding.tv133, android.R.color.holo_green_dark);
        setRippleBackground(binding.tv134, android.R.color.holo_orange_dark);
        setPressedBackground(binding.tv135, android.R.color.holo_blue_light);
        setRippleBackground(binding.tv136, android.R.color.holo_red_light);
        setPressedBackground(binding.tv137, android.R.color.holo_green_light);
        setRippleBackground(binding.tv138, android.R.color.holo_orange_light);
        setPressedBackground(binding.tv139, android.R.color.darker_gray);
        setRippleBackground(binding.tv140, android.R.color.holo_blue_bright);
        setPressedBackground(binding.tv141, android.R.color.holo_purple);
        setPressedBackground(binding.tv142, android.R.color.holo_blue_dark);
        setRippleBackground(binding.tv143, android.R.color.holo_red_dark);
        setPressedBackground(binding.tv144, android.R.color.holo_green_dark);
        setRippleBackground(binding.tv145, android.R.color.holo_orange_dark);
        setPressedBackground(binding.tv146, android.R.color.holo_blue_light);
        setRippleBackground(binding.tv147, android.R.color.holo_red_light);
        setPressedBackground(binding.tv148, android.R.color.holo_green_light);
        setRippleBackground(binding.tv149, android.R.color.holo_orange_light);
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.tv_131) {
            LottieDialog.newInstance(LoadingActivity.this)
                    .showLoading("loading-clock.json");
        } else if (view.getId() == R.id.tv_132) {
            LottieDialog.newInstance(LoadingActivity.this)
                    .showLoading("loading-funnel.json");
        } else if (view.getId() == R.id.tv_133) {
            LottieDialog.newInstance(LoadingActivity.this)
                    .showLoading("loading-gear.json");
        } else if (view.getId() == R.id.tv_134) {
            LottieDialog.newInstance(LoadingActivity.this)
                    .showLoading("loading-gears.json");
        } else if (view.getId() == R.id.tv_135) {
            LottieDialog.newInstance(LoadingActivity.this)
                    .showLoading("loading-lego.json");
        } else if (view.getId() == R.id.tv_136) {
            LottieDialog.newInstance(LoadingActivity.this)
                    .showLoading("loading-ring.json");
        } else if (view.getId() == R.id.tv_137) {
            LottieDialog.newInstance(LoadingActivity.this)
                    .showLoading("loading-ring-hc.json");
        } else if (view.getId() == R.id.tv_138) {
            LottieDialog.newInstance(LoadingActivity.this)
                    .showLoading("loading-ring-spread.json");
        } else if (view.getId() == R.id.tv_139) {
            LottieDialog.newInstance(LoadingActivity.this)
                    .showLoading("loading-text.json");
        } else if (view.getId() == R.id.tv_140) {
            LottieDialog.newInstance(LoadingActivity.this)
                    .showLoading("loading-windmill.json");
        } else if (view.getId() == R.id.tv_141) {
            LottieDialog.newInstance(LoadingActivity.this)
                    .showLoading("loading-ball-taiji.json");
        } else if (view.getId() == R.id.tv_142) {
            LottieDialog.newInstance(LoadingActivity.this)
                    .showLoading("loading-ball-wave.json");
        } else if (view.getId() == R.id.tv_143) {
            LottieDialog.newInstance(LoadingActivity.this)
                    .showLoading("loading-ball-world.json");
        } else if (view.getId() == R.id.tv_144) {
            LottieDialog.newInstance(LoadingActivity.this)
                    .showLoading("loading-point-alpha.json");
        } else if (view.getId() == R.id.tv_145) {
            LottieDialog.newInstance(LoadingActivity.this)
                    .showLoading("loading-point-hexagon.json");
        } else if (view.getId() == R.id.tv_146) {
            LottieDialog.newInstance(LoadingActivity.this)
                    .showLoading("loading-point-scale.json");
        } else if (view.getId() == R.id.tv_147) {
            LottieDialog.newInstance(LoadingActivity.this)
                    .showLoading("loading-point-transfer.json");
        } else if (view.getId() == R.id.tv_148) {
            LottieDialog.newInstance(LoadingActivity.this)
                    .showLoading("loading-point-triangle.json");
        } else if (view.getId() == R.id.tv_149) {
            LottieDialog.newInstance(LoadingActivity.this)
                    .showLoading("loading-point-trifecta.json");
        }
    }

    private void setPressedBackground(View viewBackground, int color) {
        DrawableHelper.radius(8).pressed(color, R.color.colorDivide).into(viewBackground);
    }

    private void setRippleBackground(View viewBackground, int color) {
        DrawableHelper.radius(8).ripple(color).into(viewBackground);
    }
}
