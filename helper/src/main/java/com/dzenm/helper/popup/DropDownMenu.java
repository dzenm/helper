package com.dzenm.helper.popup;

import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.PopupWindow;

import com.dzenm.helper.R;
import com.dzenm.helper.dialog.ViewHolder;
import com.dzenm.helper.draw.DrawableHelper;
import com.dzenm.helper.os.ScreenHelper;

/**
 * @author dzenm
 * @date 2020-01-16 22:53
 */
public class DropDownMenu extends PopupWindow {

    private final PopupController controller;

    private DropDownMenu(Activity activity) {
        this(activity, null);
    }

    private DropDownMenu(Activity activity, AttributeSet attrs) {
        this(activity, attrs, 0);
    }

    private DropDownMenu(Activity activity, AttributeSet attrs, int defStyleAttr) {
        super(activity, attrs, defStyleAttr);
        controller = new PopupController(activity, this);
    }

    public static class Builder {

        public static Builder newInstance(Activity activity) {
            return new Builder(activity);
        }

        private final PopupController.Params params;
        private OnBindViewHolder mOnBindViewHolder;
        private Activity activity;
        private View maskView, contentView;
        private FrameLayout decorView;

        public Builder(Activity activity) {
            params = new PopupController.Params(activity);
            this.activity = activity;
            getDecorView();
            setBackground(null);
            seBackgroundAlpha(1.0f);
            setAnimationStyle(R.style.DropDownMenu_Alpha_Animation);
        }

        /**
         * @param resId 设置View， 使用resource ID引入
         * @return this
         */
        public Builder setView(int resId) {
            setView(LayoutInflater.from(params.mActivity).inflate(resId, null, false));
            return this;
        }

        /**
         * @param view 设置View， 直接通过View对象引入
         * @return this
         */
        public Builder setView(View view) {
            ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(
                    ScreenHelper.getDisplayWidth(), ViewGroup.LayoutParams.WRAP_CONTENT
            );
            view.setLayoutParams(layoutParams);
            DrawableHelper.solid(android.R.color.white)
                    .radiusBR(8)
                    .radiusBL(8)
                    .into(view);
            contentView = view;
            decorView.addView(maskView);
            decorView.addView(view);
            params.mPopupView = decorView;
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
        public Builder seBackgroundAlpha(float background) {
            params.mBackgroundAlpha = background;
            return this;
        }

        /**
         * 关闭dialog
         *
         * @return this
         */
        public Builder dismiss() {
            params.dismiss();
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
        public DropDownMenu create() {
            DropDownMenu mP = new DropDownMenu(params.mActivity);
            params.apply(mP.controller);
            mOnBindViewHolder.onBinding(ViewHolder.create(params.mPopupView), mP);
            showMask();
            return mP;
        }

        private void getDecorView() {
            FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(
                    ScreenHelper.getDisplayWidth(), FrameLayout.LayoutParams.MATCH_PARENT
            );
            decorView = new FrameLayout(activity);
            decorView.setLayoutParams(layoutParams);

            maskView = new View(activity);
            ViewGroup.LayoutParams maskParams = new ViewGroup.LayoutParams(
                    ScreenHelper.getDisplayWidth(), ViewGroup.LayoutParams.MATCH_PARENT
            );
            maskView.setLayoutParams(maskParams);
            maskView.setBackgroundColor(-2004318072);
            maskView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dismiss();
                }
            });
        }

        private void closeMask() {
            contentView.setAnimation(AnimationUtils.loadAnimation(activity, R.anim.popup_trans_out));
            maskView.setAnimation(AnimationUtils.loadAnimation(activity, R.anim.popup_alpha_out));
        }

        private void showMask() {
            contentView.setAnimation(AnimationUtils.loadAnimation(activity, R.anim.popup_trans_in));
            maskView.setAnimation(AnimationUtils.loadAnimation(activity, R.anim.popup_alpha_in));
        }
    }

    public interface OnBindViewHolder {
        void onBinding(ViewHolder holder, DropDownMenu dialog);
    }
}
