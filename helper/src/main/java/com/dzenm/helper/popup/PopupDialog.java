package com.dzenm.helper.popup;

import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.PopupWindow;

import com.dzenm.helper.dialog.ViewHolder;

/**
 * @author dinzhenyan
 * @date 2019-07-01 21:51
 * <pre>
 * new PopupDialog.Builder(this)
 *         .setView(R.layout.dialog_login)
 *         .setOnBindViewHolder(new PopupDialog.OnBindViewHolder() {
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

    protected PopupDialog(Activity activity) {
        this(activity, null);
    }

    protected PopupDialog(Activity activity, AttributeSet attrs) {
        this(activity, attrs, 0);
    }

    protected PopupDialog(Activity activity, AttributeSet attrs, int defStyleAttr) {
        super(activity, attrs, defStyleAttr);
        controller = new PopupController(activity, this);
    }

    public static class Builder {

        public static Builder newInstance(Activity activity) {
            return new Builder(activity);
        }

        private final PopupController.Params mParams;
        private PopupDialog.OnBindViewHolder mOnBindViewHolder;

        public Builder(Activity activity) {
            mParams = new PopupController.Params(activity);
        }

        /**
         * @param resId 设置View， 使用resource ID引入
         * @return this
         */
        public Builder setView(int resId) {
            setView(LayoutInflater.from(mParams.mActivity).inflate(resId, null, false));
            return this;
        }

        /**
         * @param view 设置View， 直接通过View对象引入
         * @return this
         */
        public Builder setView(View view) {
            mParams.mPopupView = view;
            return this;
        }

        /**
         * @param animationStyle 设置动画样式
         * @return this
         */
        public Builder setAnimationStyle(int animationStyle) {
            mParams.mAnimationStyle = animationStyle;
            return this;
        }

        /**
         * @param touchable 设置通过触摸外部可以取消显示dialog
         * @return this
         */
        public Builder setOutsideTouchable(boolean touchable) {
            mParams.mTouchable = touchable;
            return this;
        }

        /**
         * @param background 设置背景
         * @return this
         */
        public Builder setBackground(Drawable background) {
            mParams.mBackground = background;
            return this;
        }

        /**
         * @param elevation 设置Z轴产生的阴影
         * @return this
         */
        public Builder setElevation(int elevation) {
            mParams.mElevation = elevation;
            return this;
        }

        /**
         * @param background 设置父控件的背景颜色
         * @return this
         */
        public Builder seBackgroundAlpha(float background) {
            mParams.mBackgroundAlpha = background;
            return this;
        }

        /**
         * 关闭dialog
         *
         * @return this
         */
        public Builder dismiss() {
            mParams.dismiss();
            return this;
        }

        /**
         * @param onBindViewHolder 设置子View的属性
         * @return this
         */
        public Builder setOnBindViewHolder(OnBindViewHolder onBindViewHolder) {
            mOnBindViewHolder = onBindViewHolder;
            return this;
        }

        /**
         * 最后调用该方法创建
         *
         * @return {@link PopupDialog}
         */
        public PopupDialog create() {
            PopupDialog mP = new PopupDialog(mParams.mActivity);
            mOnBindViewHolder.onBinding(ViewHolder.create(mParams.mPopupView), mP);
            mParams.apply(mP.controller);
            return mP;
        }
    }

    public interface OnBindViewHolder<T extends PopupDialog> {
        void onBinding(ViewHolder holder, T dialog);
    }
}