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
 * new PopupDialog.Builder(this)
 *         .setView(R.layout.dialog_login)
 *         .setOnViewHolderCallback(new PopupDialog.OnViewHolderCallback() {
 *             @Override
 *             public void onCallback(ViewHolder holder, final PopupDialog popupWindow) {
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
public class PopupDialog extends PopupWindow {

    private final PopupController controller;

    private PopupDialog(Activity activity) {
        this(activity, null);
    }

    private PopupDialog(Activity activity, AttributeSet attrs) {
        this(activity, attrs, 0);
    }

    private PopupDialog(Activity activity, AttributeSet attrs, int defStyleAttr) {
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
         * @param resId 设置View， 使用resource ID引入
         * @return this
         */
        public Builder setView(int resId) {
            params.mPopupView = LayoutInflater.from(params.mActivity).inflate(resId, null, false);
            return this;
        }

        /**
         * @param view 设置View， 直接通过View对象引入
         * @return this
         */
        public Builder setView(View view) {
            params.mPopupView = view;
            return this;
        }

        /**
         * @param animationStyle 设置动画样式
         * @return this
         */
        public Builder setAnimationStyle(int animationStyle) {
            params.mAnimationStyle = animationStyle;
            return this;
        }

        /**
         * @param touchable 设置通过触摸外部可以取消显示dialog
         * @return this
         */
        public Builder setOutsideTouchable(boolean touchable) {
            params.mTouchable = touchable;
            return this;
        }

        /**
         * @param background 设置背景
         * @return this
         */
        public Builder setBackground(Drawable background) {
            params.mBackground = background;
            return this;
        }

        /**
         * @param elevation 设置Z轴产生的阴影
         * @return this
         */
        public Builder setElevation(int elevation) {
            params.mElevation = elevation;
            return this;
        }

        /**
         * @param background 设置父控件的背景颜色
         * @return this
         */
        public Builder setParentBackground(float background) {
            params.parentBackgroundAlpha = background;
            return this;
        }

        /**
         * @param onBindViewHolder 设置子View的属性
         * @return this
         */
        public Builder setOnBindViewHolder(OnBindViewHolder onBindViewHolder) {
            params.mOnBindViewHolder = onBindViewHolder;
            return this;
        }

        /**
         * 最后调用该方法创建
         *
         * @return {@link PopupDialog}
         */
        public PopupDialog create() {
            PopupDialog mP = new PopupDialog(params.mActivity);
            params.apply(mP.controller);
            return mP;
        }
    }

    public interface OnBindViewHolder {

        void onBinding(ViewHolder holder, PopupDialog popupDialog);
    }
}