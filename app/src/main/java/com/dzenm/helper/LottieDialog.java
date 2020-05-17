package com.dzenm.helper;

import android.content.Context;
import android.view.View;
import android.widget.LinearLayout;

import com.airbnb.lottie.LottieAnimationView;
import com.airbnb.lottie.LottieDrawable;
import com.dzenm.lib.material.PromptDialog;

import androidx.annotation.NonNull;

/**
 * @author dzenm
 * @date 2019-08-17 08:31
 */
public class LottieDialog extends PromptDialog {

    private LottieAnimationView lottieAnimationView;

    public void showLoading(String assetsResource) {
        show(getContext().getResources().getString(R.string.dialog_loading), assetsResource, false);
    }

    /**
     * 自定义提示框
     *
     * @param loadText          提示文字
     * @param animationResource 提示图片
     * @param autoCancel        是否自动取消
     */
    public void show(String loadText, String animationResource, boolean autoCancel) {
        setPromptText(loadText);
        playAnimation(animationResource);
        setCancel(autoCancel, true);
        show();
    }

    public static LottieDialog newInstance(@NonNull Context context) {
        return new LottieDialog(context);
    }

    private LottieDialog(@NonNull Context context) {
        super(context);
    }

    @Override
    protected View getCustomizeImageView(LinearLayout.LayoutParams params) {
        lottieAnimationView = new LottieAnimationView(getContext());
        lottieAnimationView.setLayoutParams(params);
        lottieAnimationView.setImageAssetsFolder("images");
        lottieAnimationView.setRepeatCount(LottieDrawable.INFINITE);
        lottieAnimationView.useHardwareAcceleration(true);
        return lottieAnimationView;
    }

    private void playAnimation(String resource) {
        lottieAnimationView.setAnimation(resource);
        lottieAnimationView.playAnimation();
    }

    @Override
    protected boolean isDefaultView() {
        return false;
    }
}
