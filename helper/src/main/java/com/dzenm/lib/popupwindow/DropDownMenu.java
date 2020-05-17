package com.dzenm.lib.popupwindow;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.view.animation.DecelerateInterpolator;
import android.widget.FrameLayout;
import android.widget.PopupWindow;

import com.dzenm.lib.R;
import com.dzenm.lib.view.ViewHolder;
import com.dzenm.lib.os.ScreenHelper;

/**
 * <pre>
 * new DropDownMenu.Builder(this)
 *       .setView(R.layout.dialog_login)
 *       .setOnBindViewHolder(new DropDownMenu.OnBindViewHolder() {
 *           @Override
 *           public void onBinding(ViewHolder holder, final DropDownMenu.Builder dialog) {
 *               holder.getView(R.id.tv_confirm).setOnClickListener(new View.OnClickListener() {
 *                   @Override
 *                   public void onClick(View v) {
 *                       ToastHelper.show("登录成功");
 *                       dialog.dismiss();
 *                   }
 *               });
 *               holder.getView(R.id.tv_cancel).setOnClickListener(new View.OnClickListener() {
 *                   @Override
 *                   public void onClick(View v) {
 *                       dialog.dismiss();
 *                   }
 *               });
 *           }
 *       }).create()
 *       .showAsDropDown(getBinding().tvMenu);
 * </pre>
 *
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

        private final PopupController.Params mParams;
        private OnBindViewHolder mOnBindViewHolder;
        private Activity mActivity;
        private View mMaskView, mContentView;
        private FrameLayout mDecorView;

        public Builder(Activity activity) {
            mParams = new PopupController.Params(activity);
            mActivity = activity;
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
            setView(LayoutInflater.from(mParams.mActivity).inflate(resId, null, false));
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
            view.setBackgroundColor(mActivity.getColor(android.R.color.white));
            mContentView = view;
            mDecorView.addView(mMaskView);
            mDecorView.addView(view);

            mParams.mPopupView = mDecorView;
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
            closeMenu();
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
            DropDownMenu mP = new DropDownMenu(mParams.mActivity);
            mParams.mOnDismissListener = mPnDismissListener;
            mParams.apply(mP.controller);
            mOnBindViewHolder.onBinding(ViewHolder.create(mParams.mPopupView), this);
            showMenu();
            return mP;
        }

        private void getDecorView() {
            FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(
                    ScreenHelper.getDisplayWidth(), ViewGroup.LayoutParams.MATCH_PARENT
            );
            mDecorView = new FrameLayout(mActivity);
            mDecorView.setLayoutParams(layoutParams);

            mMaskView = new View(mActivity);
            ViewGroup.LayoutParams maskParams = new ViewGroup.LayoutParams(
                    ScreenHelper.getDisplayWidth(), ViewGroup.LayoutParams.MATCH_PARENT
            );
            mMaskView.setLayoutParams(maskParams);
//            mMaskView.setBackgroundColor(-2004318072);
            mMaskView.setBackgroundColor(mActivity.getResources().getColor(R.color.colorTranslucentDarkGrey));
            mMaskView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dismiss();
                }
            });
        }

        /**
         * Menu 显示
         */
        private void showMenu() {
            mContentView.setAnimation(AnimationUtils.loadAnimation(mActivity, R.anim.popup_trans_in));
            mMaskView.setAnimation(AnimationUtils.loadAnimation(mActivity, R.anim.popup_alpha_in));
        }

        /**
         * Menu 关闭
         */
        private void closeMenu() {
            // Menu层动画
            ObjectAnimator contentAnimator = ObjectAnimator.ofFloat(
                    mContentView, "translationY", 0f, -mContentView.getHeight());
            contentAnimator.setDuration(300);
            contentAnimator.setInterpolator(new DecelerateInterpolator());
            contentAnimator.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    super.onAnimationEnd(animation);
                    mParams.dismiss();
                }
            });
            contentAnimator.start();
            // 遮罩层动画
            ObjectAnimator maskAnimator = ObjectAnimator.ofFloat(
                    mMaskView, "alpha", 1f, 0.3f);
            maskAnimator.setDuration(300);
            maskAnimator.setInterpolator(new DecelerateInterpolator());
            maskAnimator.start();
        }

        private OnDismissListener mPnDismissListener = new OnDismissListener() {
            @Override
            public void onDismiss() {
                dismiss();
            }
        };
    }

    public interface OnBindViewHolder {
        void onBinding(ViewHolder holder, DropDownMenu.Builder dialog);
    }
}
