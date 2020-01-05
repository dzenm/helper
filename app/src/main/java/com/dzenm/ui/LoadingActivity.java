package com.dzenm.ui;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;

import com.dzenm.LottieDialog;
import com.dzenm.R;
import com.dzenm.databinding.ActivityLoadingBinding;
import com.dzenm.helper.base.AbsActivity;
import com.dzenm.helper.draw.DrawableHelper;

public class LoadingActivity extends AbsActivity<ActivityLoadingBinding> implements View.OnClickListener {

    @Override
    protected int layoutId() {
        return R.layout.activity_loading;
    }

    @Override
    protected void initializeView(@Nullable Bundle savedInstanceState) {
        super.initializeView(savedInstanceState);
        setToolbarWithImmersiveStatusBar(getBinding().toolbar, android.R.color.transparent);

        setPressedBackground(getBinding().tv131, android.R.color.holo_blue_dark);
        setRippleBackground(getBinding().tv132, android.R.color.holo_red_dark);
        setPressedBackground(getBinding().tv133, android.R.color.holo_green_dark);
        setRippleBackground(getBinding().tv134, android.R.color.holo_orange_dark);
        setPressedBackground(getBinding().tv135, android.R.color.holo_blue_light);
        setRippleBackground(getBinding().tv136, android.R.color.holo_red_light);
        setPressedBackground(getBinding().tv137, android.R.color.holo_green_light);
        setRippleBackground(getBinding().tv138, android.R.color.holo_orange_light);
        setPressedBackground(getBinding().tv139, android.R.color.darker_gray);
        setRippleBackground(getBinding().tv140, android.R.color.holo_blue_bright);
        setPressedBackground(getBinding().tv141, android.R.color.holo_purple);
        setPressedBackground(getBinding().tv142, android.R.color.holo_blue_dark);
        setRippleBackground(getBinding().tv143, android.R.color.holo_red_dark);
        setPressedBackground(getBinding().tv144, android.R.color.holo_green_dark);
        setRippleBackground(getBinding().tv145, android.R.color.holo_orange_dark);
        setPressedBackground(getBinding().tv146, android.R.color.holo_blue_light);
        setRippleBackground(getBinding().tv147, android.R.color.holo_red_light);
        setPressedBackground(getBinding().tv148, android.R.color.holo_green_light);
        setRippleBackground(getBinding().tv149, android.R.color.holo_orange_light);
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
