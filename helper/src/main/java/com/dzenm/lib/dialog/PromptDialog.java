package com.dzenm.lib.dialog;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.CountDownTimer;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.dzenm.lib.R;
import com.dzenm.lib.animator.AnimatorHelper;
import com.dzenm.lib.drawable.DrawableHelper;
import com.dzenm.lib.os.OsHelper;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import androidx.annotation.IntDef;
import androidx.annotation.NonNull;

/**
 * @author dinzhenyan
 * @date 2019-04-30 20:03
 * <p>
 * 提示对话框，不产生交互，仅做提示
 * <pre>
 *     PromptDialog.newInstance(MainActivity.this)
 *               .setTranslucent(true)
 *               .showLoading(PromptDialog.LOADING_RECT_SCALE);
 *     PromptDialog.newInstance(MainActivity.this)
 *              .setGravity(Gravity.BOTTOM)
 *              .setTranslucent(true)
 *              .showLoading(PromptDialog.LOADING_POINT_TRANS);
 *     PromptDialog.newInstance(this)
 *              .setTranslucent(true)
 *              .setOnCountListener(new PromptDialog.OnCountDownListener() {
 *                  @Override
 *                  public void onFinish() {
 *                      Logger.d(TAG + "回掉完成");
 *                      ToastHelper.show("回掉完成");
 *                  }
 *              }).showSuccess("登录成功");
 *     PromptDialog.newInstance(this).showError("禁止访问");
 * </pre>
 */
@SuppressLint("ValidFragment")
public class PromptDialog extends AbsDialog {

    /**
     * 加载框类型
     */
    public static final int LOADING_POINT_ALPHA = 1001;
    public static final int LOADING_POINT_TRANS = 1002;
    public static final int LOADING_POINT_SCALE = 1003;
    public static final int LOADING_RECT_SCALE = 1004;
    public static final int LOADING_RECT_ALPHA = 1005;

    /**
     * dialog是否可以按返回键取消
     */
    private boolean isCancel;

    private ImageView ivIconImage;

    private TextView tvLoadText;

    /**
     * 是否在动画显示完成之后自动关闭dialog, 为true时需要自己关闭dialog, 为false时显示1.5秒自动关闭
     * 自定义 {@link #setCancel(boolean, boolean)}
     */
    private boolean isAutoCancel;

    /**
     * 延时关闭dialog的时间, {@link #setCountTime(int)}
     */
    private float mCountTime = 1.5f;

    /**
     * 倒计时监听事件 {@link #setOnCountListener(OnCountDownListener)}
     */
    private OnCountDownListener mOnCountDownListener;

    @IntDef({LOADING_POINT_SCALE, LOADING_POINT_ALPHA, LOADING_POINT_TRANS,
            LOADING_RECT_SCALE, LOADING_RECT_ALPHA})
    @Retention(RetentionPolicy.SOURCE)
    @interface LoadingType {
    }

    /************************************* 以下为预定义的方法 *********************************/

    public static PromptDialog newInstance(@NonNull Context context) {
        return new PromptDialog(context);
    }

    /**
     * @param success 成功提示
     */
    public void showSuccess(String success) {
        show(success, R.drawable.prompt_success, true);
    }

    /**
     * @param error 错误提示
     */
    public void showError(String error) {
        show(error, R.drawable.prompt_error, true);
    }

    /**
     * @param warming 警告提示
     */
    public void showWarming(String warming) {
        show(warming, R.drawable.prompt_warming, true);
    }

    /**
     * @param refresh 刷新提示
     */
    public void showRefresh(String refresh) {
        show(refresh, R.drawable.prompt_refresh, true);
    }

    /**
     * 监听倒计时
     *
     * @param onCountDownListener 监听事件, {@link #mOnCountDownListener}
     * @return this
     */
    public PromptDialog setOnCountListener(OnCountDownListener onCountDownListener) {
        mOnCountDownListener = onCountDownListener;
        return this;
    }

    /**
     * @param countTime 倒计时
     * @return this
     */
    public PromptDialog setCountTime(int countTime) {
        mCountTime = countTime;
        return this;
    }

    /************************************* 以下为自定义方法 *********************************/

    public void showLoading(@LoadingType int type) {
        int icon = 0;
        if (type == LOADING_POINT_SCALE) {
            icon = R.drawable.prompt_loading_point_scale;
        } else if (type == LOADING_POINT_ALPHA) {
            icon = R.drawable.prompt_loading_point_alpha;
        } else if (type == LOADING_POINT_TRANS) {
            icon = R.drawable.prompt_loading_point_translate;
        } else if (type == LOADING_RECT_SCALE) {
            icon = R.drawable.prompt_loading_rectangle_scale;
        } else if (type == LOADING_RECT_ALPHA) {
            icon = R.drawable.prompt_loading_rectangle_alpha;
        }
        show(getContext().getResources().getString(R.string.dialog_loading), icon);
    }

    public void showLoading(String loadText, @LoadingType int icon) {
        show(loadText, icon, false);
    }

    public void show(String loadText, int loadImg) {
        show(loadText, loadImg, false);
    }

    public void show(String loadText, int loadImg, boolean autoCancel) {
        show(loadText, loadImg, autoCancel, !autoCancel);
    }

    /**
     * 自定义提示框
     *
     * @param loadText   提示文字
     * @param loadImg    提示图片
     * @param autoCancel 是否自动取消
     * @param cancel     是否可以通过返回键取消显示
     */
    public void show(String loadText, int loadImg, boolean autoCancel, boolean cancel) {
        setPromptText(loadText);
        setImageResourceType(loadImg);
        setCancel(autoCancel, cancel);
        show();
    }

    protected void setPromptText(String promptText) {
        tvLoadText.setText(promptText);
    }

    protected void setCancel(boolean autoCancel, boolean cancel) {
        isAutoCancel = autoCancel;
        isCancel = cancel;
    }

    @Override
    public PromptDialog setAnimator(int animator) {
        return super.setAnimator(animator);
    }

    @Override
    public PromptDialog setMargin(int margin) {
        return super.setMargin(margin);
    }

    @Override
    public PromptDialog setBackground(Drawable background) {
        return super.setBackground(background);
    }

    @Override
    public PromptDialog setTranslucent(boolean translucent) {
        return super.setTranslucent(translucent);
    }

    @Override
    public PromptDialog setTouchOutsideCancel(boolean cancel) {
        return super.setTouchOutsideCancel(cancel);
    }

    /************************************* 以下为实现过程 *********************************/

    protected PromptDialog(@NonNull Context context) {
        super(context);
        // 圆角，背景透明灰
        mBackground = DrawableHelper.solid(R.color.colorTranslucentDarkGray)
                .radius(8)
                .build();
        mAnimator = AnimatorHelper.alpha();
        isCancel = true;
    }

    @Override
    public void show() {
        super.show();
        setCancelable(isCancel);                                        // 设置不可通过返回键取消dialog
        if (isAutoCancel) {                                             // 是否在显示完成之后自动取消
            mCountDownTimer.start();
        }
    }

    private CountDownTimer mCountDownTimer = new CountDownTimer((long) (mCountTime * 1000), 1000) {
        @Override
        public void onTick(long millisUntilFinished) {
            if (mOnCountDownListener != null)
                mOnCountDownListener.onTick((int) (millisUntilFinished / 1000));
        }

        @Override
        public void onFinish() {
            // 在此执行定时操作
            dismiss();
            if (mCountDownTimer != null) mCountDownTimer.cancel();
            if (mOnCountDownListener != null) mOnCountDownListener.onFinish();
        }
    };

    @Override
    public PromptDialog setGravity(int gravity) {
        super.setGravity(gravity);
        isDefaultBackground = false;
        return this;
    }

    @Override
    protected void initView() {
        super.initView();
        setContentView(createView());
    }

    private View createView() {
        // 创建最外层ViewGroup
        LinearLayout.LayoutParams viewParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        LinearLayout linearLayout = new LinearLayout(getContext());
        int padding = OsHelper.dp2px(16);
        linearLayout.setPadding(padding, padding, padding, padding);
        linearLayout.setOrientation(LinearLayout.VERTICAL);
        linearLayout.setLayoutParams(viewParams);

        View imageView;
        if (isDefaultView()) {
            // 创建一个ImageView
            LinearLayout.LayoutParams imgParams = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            imgParams.gravity = Gravity.CENTER_HORIZONTAL;
            ivIconImage = new ImageView(getContext());
            imageView = ivIconImage;
            imageView.setLayoutParams(imgParams);
        } else {
            // 创建动画显示的View
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    OsHelper.dp2px(60), OsHelper.dp2px(60));
            params.gravity = Gravity.CENTER_HORIZONTAL;
            imageView = getCustomizeImageView(params);
        }

        // 创建一个TextView
        LinearLayout.LayoutParams textParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        textParams.topMargin = OsHelper.dp2px(8);

        tvLoadText = new TextView(getContext());
        tvLoadText.setMaxWidth(OsHelper.dp2px(100));
        tvLoadText.setMinWidth(OsHelper.dp2px(90));
        tvLoadText.setMaxLines(2);
        tvLoadText.setGravity(Gravity.CENTER_HORIZONTAL);
        tvLoadText.setTextColor(Color.WHITE);
        tvLoadText.setTextSize(12);
        tvLoadText.setLayoutParams(textParams);

        linearLayout.addView(imageView);
        linearLayout.addView(tvLoadText);
        return mView = linearLayout;
    }

    /**
     * 自定义View, 使用Lottie, 继承{@link #PromptDialog(Context)}, 重写该方法
     *
     * @return 默认为true, 重写改为false
     */
    protected boolean isDefaultView() {
        return true;
    }

    /**
     * 使用Lottie, 新建一个LottieView
     *
     * @param params Lottie显示的View的LayoutParams
     * @return Lottie显示的View
     */
    protected View getCustomizeImageView(LinearLayout.LayoutParams params) {
        return null;
    }

    /**
     * @param icon 图片资源
     */
    private void setImageResourceType(int icon) {
        LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) ivIconImage.getLayoutParams();
        if (icon == LOADING_POINT_TRANS) {
            layoutParams.width = layoutParams.height = OsHelper.dp2px(60);
        } else if (icon == LOADING_POINT_SCALE || icon == LOADING_POINT_ALPHA ||
                icon == LOADING_RECT_SCALE || icon == LOADING_RECT_ALPHA) {
            layoutParams.width = layoutParams.height = OsHelper.dp2px(60);
        } else {
            layoutParams.width = layoutParams.height = OsHelper.dp2px(60);
        }

        ivIconImage.setLayoutParams(layoutParams);
        ivIconImage.setImageResource(icon);

        AnimatorHelper.play(ivIconImage.getDrawable());
    }

    @Override
    protected void setDialogLayoutParams(ViewGroup.MarginLayoutParams params) {
        params.bottomMargin = params.topMargin = OsHelper.dp2px(8 * mMargin);
    }

    @Override
    protected boolean isPromptDialog() {
        return true;
    }

    public static abstract class OnCountDownListener implements OnCountDownTimerListener {

        @Override
        public void onTick(int count) {

        }

        @Override
        public void onFinish() {

        }
    }

    private interface OnCountDownTimerListener {

        void onTick(int count);

        void onFinish();
    }
}