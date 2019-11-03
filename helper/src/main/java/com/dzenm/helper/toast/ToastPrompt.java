package com.dzenm.helper.toast;

import android.content.Context;
import android.graphics.drawable.Animatable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.VectorDrawable;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.IntDef;

import com.dzenm.helper.R;
import com.dzenm.helper.draw.DrawableHelper;
import com.dzenm.helper.os.OsHelper;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * @author dzenm
 * @date 2019-10-19 23:05
 */
public class ToastPrompt {

    /**
     * 图标状态 不显示图标
     */
    private static final int TYPE_HIDE = -1;
    /**
     * 图标状态 显示图标
     */
    private static final int TYPE_SHOW = 1;

    @IntDef({TYPE_HIDE, TYPE_SHOW})
    @Retention(RetentionPolicy.SOURCE)
    @interface Type {
    }

    private static volatile ToastPrompt sToast;
    private Context mContext;
    private Prompt mPrompt;

    private boolean isShowRepeat = true, isCustomize = false;
    private Drawable mBackground, mCustomizeBackground;
    private int mGravity, mCustomizeGravity, mOffset, sCustomizeOffset;

    private ToastPrompt() {

    }

    static ToastPrompt getInstance() {
        if (sToast == null) synchronized (ToastPrompt.class) {
            if (sToast == null) sToast = new ToastPrompt();
        }
        return sToast;
    }

    public ToastPrompt init(Context context) {
        mContext = context;
        return this;
    }

    /**
     * 为单个Toast显示设置属性时, 需要先调用该方法
     *
     * @return this
     */
    public ToastPrompt customize() {
        isCustomize = true;
        return this;
    }

    /**
     * @param background Toast背景drawable资源文件
     * @return this
     */
    public ToastPrompt setBackground(Drawable background) {
        if (isCustomize) {
            mCustomizeBackground = background;
        } else {
            mBackground = background;
        }
        return this;
    }

    /**
     * @param gravity 显示的位置
     * @param offset  垂直方向偏移量
     * @return this
     */
    public ToastPrompt setGravity(int gravity, int offset) {
        if (isCustomize) {
            mCustomizeGravity = gravity;
            sCustomizeOffset = offset;
        } else {
            mGravity = gravity;
            mOffset = offset;
        }
        return this;
    }

    /**
     * @param repeat true显示多次， false显示一次
     * @return this
     */
    public ToastPrompt isRepeat(boolean repeat) {
        isShowRepeat = repeat;
        return this;
    }

    /**
     * @param resId 显示的文本
     */
    public void show(int resId) {
        show(mContext.getString(resId));
    }

    /**
     * @param text 显示的文本
     */
    public void show(CharSequence text) {
        show(text, 0, Toast.LENGTH_SHORT, TYPE_HIDE);
    }

    /**
     * @param text  显示的文本
     * @param resId 显示的图片
     */
    public void show(CharSequence text, int resId) {
        show(text, resId, Toast.LENGTH_SHORT);
    }

    /**
     * @param text     显示的文本
     * @param resId    显示的图片
     * @param duration 显示的时间
     */
    public void show(CharSequence text, int resId, int duration) {
        show(text, resId, duration, TYPE_SHOW);
    }

    /**
     * @param text      显示的文本内容
     * @param resId     显示的图片资源
     * @param duration  显示的时间
     * @param showImage 是否显示图片
     */
    public void show(CharSequence text, int resId, int duration, @Type int showImage) {
        // 是否重复显示Toast
        if (isShowRepeat) {
            if (mPrompt != null) mPrompt.cancel();
            mPrompt = new Prompt(mContext);
        } else {
            if (mPrompt == null) mPrompt = new Prompt(mContext);
        }

        // Toast背景设置
        if (isCustomize) {
            if (mCustomizeBackground != null) mPrompt.setBackground(mCustomizeBackground);
        } else {
            if (mBackground != null) mPrompt.setBackground(mBackground);
        }

        // Toast位置设置
        if (isCustomize) {
            if (mCustomizeGravity != 0) mPrompt.setGravity(mCustomizeGravity, sCustomizeOffset);
        } else {
            if (mGravity != 0) mPrompt.setGravity(mGravity, mOffset);
        }

        if (isCustomize) {
            mCustomizeBackground = null;
            mCustomizeGravity = 0;
            sCustomizeOffset = 0;
            isCustomize = false;
        }
        mPrompt.showToast(text, resId, duration, showImage);
    }

    private class Prompt extends Toast {

        private ImageView mImageView;
        private TextView mTextView;
        private LinearLayout mLinearLayout;

        private Drawable mBackground;

        /**
         * Construct an empty Toast object.  You must call {@link #setView} before you
         * can call {@link #show}.
         *
         * @param context The context to use.  Usually your Application
         *                or Activity object.
         */
        private Prompt(Context context) {
            super(context);
            mBackground = DrawableHelper.solid(R.color.colorTranslucentDarkGray)
                    .radius(16)
                    .build();
        }

        /**
         * 设置Toast背景
         *
         * @param background drawable资源文件
         */
        private void setBackground(Drawable background) {
            mBackground = background;
        }

        /**
         * @param gravity 显示的位置
         * @param offset  垂直方向偏移量
         */
        private void setGravity(int gravity, int offset) {
            mPrompt.setGravity(gravity, offset, 0);
        }

        /**
         * @param text      显示文本
         * @param duration  显示时间
         * @param showImage 显示图片
         */
        private void showToast(CharSequence text, int resId, int duration, @Type int showImage) {
            mPrompt.setView(createView(mContext));
            mPrompt.setDuration(duration);

            mTextView.setText(text);
            mLinearLayout.setBackground(mBackground);

            if (showImage == TYPE_HIDE) {
                mImageView.setVisibility(View.GONE);
            } else if (showImage == TYPE_SHOW) {
                mImageView.setImageResource(resId);
                Drawable drawable = mImageView.getDrawable();
                if (!(drawable instanceof VectorDrawable)) {
                    ((Animatable) drawable).start();
                }
            }
            mPrompt.show();
        }

        /**
         * @param context
         * @return 创建View，自定义显示效果
         */
        private View createView(Context context) {
            // 显示的文本
            LinearLayout.LayoutParams textParams = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            mTextView = new TextView(context);
            mTextView.setTextColor(context.getResources().getColor(android.R.color.white));
            mTextView.setLayoutParams(textParams);

            // 图标的尺寸
            int size = OsHelper.dp2px(16);
            LinearLayout.LayoutParams imgParams = new LinearLayout.LayoutParams(size, size);

            // 显示的图片
            imgParams.setMargins(0, 0, OsHelper.dp2px(8), 0);
            mImageView = new ImageView(context);
            mImageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            mImageView.setLayoutParams(imgParams);

            // ViewGroup
            mLinearLayout = new LinearLayout(context);
            mLinearLayout.setGravity(Gravity.CENTER);
            int paddingVertical = OsHelper.dp2px(12);
            int paddingHorizontal = OsHelper.dp2px(16);
            mLinearLayout.setPadding(paddingHorizontal, paddingVertical, paddingHorizontal, paddingVertical);
            mLinearLayout.setOrientation(LinearLayout.HORIZONTAL);

            mLinearLayout.addView(mImageView);
            mLinearLayout.addView(mTextView);
            return mLinearLayout;
        }
    }
}
