package com.dzenm.helper.toast;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.graphics.drawable.Animatable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.VectorDrawable;
import androidx.annotation.IntDef;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.dzenm.helper.R;
import com.dzenm.helper.draw.BackGHelper;
import com.dzenm.helper.os.ScreenHelper;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * @author dinzhenyan
 * @date 2019-06-07 10:46
 * <pre>
 * 在Application里初始化
 * Toa.getInstance(this);
 *
 * 弹出一个文本Toast
 * Toa.show("自定义Toast");
 *
 * 弹一个带图标和背景颜色的Toast
 * Toa.setBackground(GradientHelper.get(getResources()
 *                     .getColor(android.R.color.holo_blue_bright), 12))
 *                     .show("带图标的toast", R.drawable.prompt_success);
 * </pre>
 */
public class Toa {

    @SuppressLint("StaticFieldLeak")
    private static Toa sToast;
    @SuppressLint("StaticFieldLeak")
    private static Context sContext;
    @SuppressLint("StaticFieldLeak")
    private static Prompt sPrompt;

    private static boolean isShowRepeat = true;
    private static Drawable sBackground;
    private static Drawable sCustomizeBackground;
    private static int sGravity;
    private static int sCustomizeGravity;
    private static int sOffset;
    private static int sCustomizeOffset;

    private static boolean isCustomize = false;

    private Toa() {

    }

    public static Toa getInstance() {
        if (sToast == null) synchronized (Toa.class) {
            if (sToast == null) sToast = new Toa();
        }
        return sToast;
    }

    public Toa init(Context context) {
        sContext = context;
        return this;
    }

    public static Toa customize() {
        isCustomize = true;
        return sToast;
    }

    /**
     * @param background Toast背景drawable资源文件
     * @return this
     */
    public Toa setBackground(Drawable background) {
        if (isCustomize) {
            sCustomizeBackground = background;
        } else {
            sBackground = background;
        }
        return sToast;
    }

    /**
     * @param gravity 显示的位置
     * @param offset  垂直方向偏移量
     * @return this
     */
    public Toa setGravity(int gravity, int offset) {
        if (isCustomize) {
            sCustomizeGravity = gravity;
            sCustomizeOffset = offset;
        } else {
            sGravity = gravity;
            sOffset = offset;
        }
        return sToast;
    }

    /**
     * @param once true显示一次， false显示多次
     * @return this
     */
    public Toa setOnce(boolean once) {
        isShowRepeat = !once;
        return sToast;
    }

    /**
     * @param resId 显示的文本
     */
    public static void show(int resId) {
        show(sContext.getString(resId));
    }

    /**
     * @param text 显示的文本
     */
    public static void show(CharSequence text) {
        show(text, 0, Toast.LENGTH_SHORT, Prompt.TYPE_HIDE);
    }

    /**
     * @param text  显示的文本
     * @param resId 显示的图片
     */
    public static void show(CharSequence text, int resId) {
        show(text, resId, Toast.LENGTH_SHORT);
    }

    /**
     * @param text     显示文本
     * @param resId    显示图片
     * @param duration 显示时间
     */
    public static void show(CharSequence text, int resId, int duration) {
        show(text, resId, duration, Prompt.TYPE_SHOW);
    }

    /**
     * @param text      显示的文本内容
     * @param resId     显示的图片资源
     * @param duration  显示的时间
     * @param showImage 是否显示图片
     */
    public static void show(CharSequence text, int resId, int duration, @Prompt.Type int showImage) {
        // 是否重复显示Toast
        if (isShowRepeat) {
            if (sPrompt != null) sPrompt.cancel();
            sPrompt = new Prompt(sContext);
        } else {
            if (sPrompt == null) sPrompt = new Prompt(sContext);
        }

        // Toast背景设置
        if (isCustomize) {
            if (sCustomizeBackground != null) sPrompt.setBackground(sCustomizeBackground);
        } else {
            if (sBackground != null) sPrompt.setBackground(sBackground);
        }

        // Toast位置设置
        if (isCustomize) {
            if (sCustomizeGravity != 0) sPrompt.setGravity(sCustomizeGravity, sCustomizeOffset);
        } else {
            if (sGravity != 0) sPrompt.setGravity(sGravity, sOffset);
        }

        if (isCustomize) {
            sCustomizeBackground = null;
            sCustomizeGravity = 0;
            sCustomizeOffset = 0;
            isCustomize = false;
        }
        sPrompt.showToast(text, resId, duration, showImage);
    }

    private static class Prompt extends Toast {

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

        private ImageView mImageView;
        private TextView mTextView;
        private LinearLayout mLinearLayout;

        private Drawable mBackground;


        /**
         * Construct an empty Toast object.  You must call {@link #setView} before you
         * can call {@link #show}.
         *
         * @param context The context to use.  Usually your {@link Application}
         *                or {@link Activity} object.
         */
        private Prompt(Context context) {
            super(context);
            mBackground = BackGHelper.solid(R.color.colorTranslucentDarkGray)
                    .radius(16)
                    .build();
        }

        /**
         * 设置Toast背景
         *
         * @param background drawable资源文件
         * @return this
         */
        private Prompt setBackground(Drawable background) {
            mBackground = background;
            return this;
        }

        /**
         * @param gravity 显示的位置
         * @param offset  垂直方向偏移量
         * @return this
         */
        private Prompt setGravity(int gravity, int offset) {
            sPrompt.setGravity(gravity, offset, 0);
            return this;
        }

        /**
         * @param text      显示文本
         * @param duration  显示时间
         * @param showImage 显示图片
         * @return this
         */
        private Prompt showToast(CharSequence text, int resId, int duration, @Type int showImage) {
            sPrompt.setView(initView(sContext));
            sPrompt.setDuration(duration);

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
            sPrompt.show();
            return this;
        }

        /**
         * @param context
         * @return 初始化自定义的View，自定义显示效果
         */
        private View initView(Context context) {
            // 显示的文本
            LinearLayout.LayoutParams textParams = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            mTextView = new TextView(context);
            mTextView.setTextColor(context.getResources().getColor(android.R.color.white));
            mTextView.setLayoutParams(textParams);

            // 图标的尺寸
            int size = ScreenHelper.dp2px(16);
            LinearLayout.LayoutParams imgParams = new LinearLayout.LayoutParams(size, size);

            // 显示的图片
            imgParams.setMargins(0, 0, ScreenHelper.dp2px(8), 0);
            mImageView = new ImageView(context);
            mImageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            mImageView.setLayoutParams(imgParams);

            // ViewGroup
            mLinearLayout = new LinearLayout(context);
            mLinearLayout.setGravity(Gravity.CENTER);
            int paddingVertical = ScreenHelper.dp2px(12);
            int paddingHorizontal = ScreenHelper.dp2px(16);
            mLinearLayout.setPadding(paddingHorizontal, paddingVertical, paddingHorizontal, paddingVertical);
            mLinearLayout.setOrientation(LinearLayout.HORIZONTAL);

            mLinearLayout.addView(mImageView);
            mLinearLayout.addView(mTextView);
            return mLinearLayout;
        }
    }
}
