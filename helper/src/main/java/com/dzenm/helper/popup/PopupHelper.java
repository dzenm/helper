package com.dzenm.helper.popup;

import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.PopupWindow;

import com.dzenm.helper.R;
import com.dzenm.helper.dialog.ViewHolder;
import com.dzenm.helper.draw.DrawableHelper;

/**
 * @author dinzhenyan
 * @date 2019-07-01 21:51
 * <pre>
 * new PopupHelper.Builder(this)
 *         .setView(R.layout.dialog_login)
 *         .setOnViewHolderCallback(new PopupHelper.OnViewHolderCallback() {
 *             @Override
 *             public void onCallback(ViewHolder holder, final PopupHelper popupWindow) {
 *                 holder.getView(R.id.tv_confirm).setOnClickListener(new View.OnClickListener() {
 *                     @Override
 *                     public void onClick(View v) {
 *                         ToastHelper.show("登录成功");
 *                         popupWindow.dismiss();
 *                     }
 *                 });
 *                 holder.getView(R.id.tv_cancel).setOnClickListener(new View.OnClickListener() {
 *                     @Override
 *                     public void onClick(View v) {
 *                         popupWindow.dismiss();
 *                     }
 *                 });
 *             }
 *         }).create()
 *         .showAsDropDown(binding.btn3);
 * </pre>
 */
public class PopupHelper extends PopupWindow {

    private final PopupController controller;

    private PopupHelper(Activity activity) {
        this(activity, null);
    }

    private PopupHelper(Activity activity, AttributeSet attrs) {
        this(activity, attrs, 0);
    }

    private PopupHelper(Activity activity, AttributeSet attrs, int defStyleAttr) {
        super(activity, attrs, defStyleAttr);
        controller = new PopupController(activity, this);
    }


    public static class Builder {

        public static Builder newInstance(Activity activity) {
            return new Builder(activity);
        }

        private final PopupController.Params params;

        public Builder(Activity activity) {
            params = new PopupController.Params(activity);

            // 默认设置
            params.mBackground = DrawableHelper.solid(android.R.color.white).radius(8).build();
            params.parentBackgroundAlpha = 0.6f;
            params.mTouchable = true;
            params.mAnimationStyle = R.style.BasePopup_Fade_Vertical_Animator;
        }

        /**
         * 设置View， 使用resource ID引入
         *
         * @param resId
         * @return
         */
        public Builder setView(int resId) {
            params.mPopupView = LayoutInflater.from(params.mActivity).inflate(resId, null, false);
            return this;
        }

        /**
         * 设置View， 直接通过View对象引入
         *
         * @param view
         * @return
         */
        public Builder setView(View view) {
            params.mPopupView = view;
            return this;
        }

        /**
         * 设置动画样式
         *
         * @param animationStyle
         * @return
         */
        public Builder setAnimationStyle(int animationStyle) {
            params.mAnimationStyle = animationStyle;
            return this;
        }

        /**
         * 设置通过触摸外部可以取消显示dialog
         *
         * @param touchable
         * @return
         */
        public Builder setOutsideTouchable(boolean touchable) {
            params.mTouchable = touchable;
            return this;
        }

        /**
         * 设置背景
         *
         * @param background
         * @return
         */
        public Builder setBackground(Drawable background) {
            params.mBackground = background;
            return this;
        }

        /**
         * 设置Z轴产生的阴影
         *
         * @param elevation
         * @return
         */
        public Builder setElevation(int elevation) {
            params.mElevation = elevation;
            return this;
        }

        /**
         * 设置父控件的背景颜色
         *
         * @param background
         * @return
         */
        public Builder setParentBackground(float background) {
            params.parentBackgroundAlpha = background;
            return this;
        }

        /**
         * 设置子View的属性
         *
         * @param onViewHolderCallback
         * @return
         */
        public Builder setOnViewHolderCallback(OnViewHolderCallback onViewHolderCallback) {
            params.mOnViewHolderCallback = onViewHolderCallback;
            return this;
        }

        /**
         * 最后调用该方法创建
         *
         * @return
         */
        public PopupHelper create() {
            PopupHelper mP = new PopupHelper(params.mActivity);
            params.apply(mP.controller);
            return mP;
        }
    }

    public interface OnViewHolderCallback {

        void onCallback(ViewHolder holder, PopupHelper popupWindow);
    }
}