package com.dzenm.lib.popupwindow;

import android.app.Activity;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.PopupWindow;

import com.dzenm.lib.R;
import com.dzenm.lib.drawable.DrawableHelper;

/**
 * @author dinzhenyan
 * @date 2019-07-03 16:54
 */
class PopupController<T extends PopupWindow> {

    private Activity mActivity;
    private T mPopupWindow;
    private View mDecorView;

    PopupController(Activity activity, T popupWindow) {
        mActivity = activity;
        mPopupWindow = popupWindow;
    }

    private void setView(View view) {
        mDecorView = view;
        mPopupWindow.setContentView(view);
    }

    private void setDecorView(View decorView) {
        mDecorView = decorView;
    }

    private void setAnimationStyle(int animationStyle) {
        mPopupWindow.setAnimationStyle(animationStyle);                 // 设置动画特效 即 展示和消失动画
    }

    private void setBackground(Drawable background) {
        mPopupWindow.getContentView().setBackground(background);
    }

    private void setOutsideTouchable(boolean touchable) {
        // 设置PopupWindow的背景。该属性不设置的会，会导致PopupWindow出现后不会消失，即便是点击back键也不起作用
        mPopupWindow.setBackgroundDrawable(new ColorDrawable());        // 设置背景透明
        mPopupWindow.setOutsideTouchable(touchable);
        mPopupWindow.setFocusable(touchable);                           // 设置popupWindow是否可以获取焦点
    }

    private void setElevation(int elevation) {
        mPopupWindow.setElevation(elevation);                          // 设置PopupWindow的高度，类似于3D效果的阴影
    }

    /**
     * @param alpha 父控件的背景透明度
     */
    private void setBackgroundAlpha(float alpha) {
        Window window = mActivity.getWindow();
        // 弹出popupWindow时父控件显示为灰色
        WindowManager.LayoutParams layoutParams = window.getAttributes();
        layoutParams.alpha = alpha;
        window.setAttributes(layoutParams);
        window.addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
    }

    private void setOnDismissListener(PopupWindow.OnDismissListener onDismissListener) {
        mPopupWindow.setOnDismissListener(onDismissListener);
    }

    private void dismiss() {
        mPopupWindow.dismiss();
    }

    private <P extends PopupWindow> P getPopupWindow() {
        return (P) mPopupWindow;
    }

    private void create() {
        mPopupWindow.setWidth(ViewGroup.LayoutParams.WRAP_CONTENT);     // 设置弹出窗口的宽
        mPopupWindow.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);    // 设置弹出窗口的高
        mPopupWindow.setIgnoreCheekPress();                             // Events都是有大小的,当触摸点大于手指头大小时，则为脸颊事件
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1) {
            mPopupWindow.setAttachedInDecor(false);                     // 主要作用是为了设置PopupWindow显示的时候是否会与StatusBar重叠（如果存在的话也包括SystemBar）
        }
        mPopupWindow.setFocusable(true);
        mPopupWindow.setTouchable(true);
        mPopupWindow.setInputMethodMode(PopupWindow.INPUT_METHOD_NEEDED);   // 在PopupWindow里面就加上下面代码，让键盘弹出时，不会挡住pop窗口。
        mPopupWindow.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        mPopupWindow.setBackgroundDrawable(new ColorDrawable(0x00000000));
        mPopupWindow.getContentView().measure(0, 0);
        mPopupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                // 退出popupWindow时显示父控件原来的颜色
                setBackgroundAlpha(1.0f);
            }
        });
    }

    static class Params {

        private PopupController mController;
        Activity mActivity;
        View mPopupView;
        int mAnimationStyle;
        int mElevation;
        float mBackgroundAlpha;
        Drawable mBackground;
        boolean mTouchable;
        PopupWindow.OnDismissListener mOnDismissListener;

        Params(Activity activity) {
            mActivity = activity;

            // 默认设置
            mBackground = DrawableHelper.solid(android.R.color.white).radius(8).build();
            mBackgroundAlpha = 0.6f;
            mTouchable = true;
            mAnimationStyle = R.style.BasePopup_Fade_Vertical_Animator;
        }

        void dismiss() {
            mController.dismiss();
        }

        void apply(final PopupController controller) {
            mController = controller;
            if (mPopupView == null) {
                throw new NullPointerException("PopupWindow's view is null");
            } else {
                controller.setView(mPopupView);
            }
            controller.setAnimationStyle(mAnimationStyle);
            controller.setBackground(mBackground);
            controller.setBackgroundAlpha(mBackgroundAlpha);
            controller.setElevation(mElevation);
            controller.setOutsideTouchable(mTouchable);
            controller.setOnDismissListener(mOnDismissListener);
            controller.create();
        }
    }
}
