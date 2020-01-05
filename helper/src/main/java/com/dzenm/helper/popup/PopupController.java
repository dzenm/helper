package com.dzenm.helper.popup;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.PopupWindow;

import com.dzenm.helper.dialog.ViewHolder;

/**
 * @author dinzhenyan
 * @date 2019-07-03 16:54
 */
class PopupController {

    private Activity mActivity;
    private PopupDialog mPopupWindow;

    PopupController(Activity activity, PopupDialog popupWindow) {
        mActivity = activity;
        mPopupWindow = popupWindow;
    }

    private void setView(View view) {
        mPopupWindow.setContentView(view);
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
    private void setParentBackgroundAlpha(float alpha) {
        Window window = mActivity.getWindow();
        // 弹出popupWindow时父控件显示为灰色
        WindowManager.LayoutParams layoutParams = window.getAttributes();
        layoutParams.alpha = alpha;
        window.setAttributes(layoutParams);
    }

    private PopupDialog getPopupWindow() {
        return mPopupWindow;
    }

    @SuppressLint("NewApi")
    private void create() {
        mPopupWindow.setWidth(ViewGroup.LayoutParams.WRAP_CONTENT);     // 设置弹出窗口的宽
        mPopupWindow.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);    // 设置弹出窗口的高
        mPopupWindow.setIgnoreCheekPress();                             // Events都是有大小的,当触摸点大于手指头大小时，则为脸颊事件
        mPopupWindow.setAttachedInDecor(false);                         // 主要作用是为了设置PopupWindow显示的时候是否会与StatusBar重叠（如果存在的话也包括SystemBar）
        mPopupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                setParentBackgroundAlpha(1.0f);
            }
        });                                                             // 退出popupWindow时显示父控件原来的颜色
        mPopupWindow.getContentView().measure(0, 0);
    }

    static class Params {

        Activity mActivity;
        View mPopupView;
        int mAnimationStyle;
        int mElevation;
        float parentBackgroundAlpha;
        Drawable mBackground;
        boolean mTouchable;

        PopupDialog.OnBindViewHolder mOnBindViewHolder;

        Params(Activity activity) {
            mActivity = activity;
        }

        void apply(PopupController controller) {
            if (mPopupView == null) {
                throw new NullPointerException("PopupWindow's view is null");
            } else {
                controller.setView(mPopupView);
            }
            controller.setAnimationStyle(mAnimationStyle);
            controller.setBackground(mBackground);
            controller.setParentBackgroundAlpha(parentBackgroundAlpha);
            controller.setElevation(mElevation);
            controller.setOutsideTouchable(mTouchable);
            mOnBindViewHolder.onBinding(ViewHolder.create(mPopupView), controller.getPopupWindow());
            controller.create();
        }
    }
}
