package com.dzenm.helper.dialog;

import android.annotation.SuppressLint;
import android.graphics.drawable.Drawable;
import android.os.CountDownTimer;
import androidx.annotation.IntDef;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.dzenm.helper.R;
import com.dzenm.helper.draw.BackGHelper;
import com.dzenm.helper.view.EditTextChangeHelper;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * @author dinzhenyan
 * @date 2019-05-19 21:38
 * 注册登录对话框，包含验证码和密码注册或登录
 * <pre>
 * LoginDialog.newInstance(this)
 *        .setLoginByPassword()
 *        .setOnClickListener(new LoginDialog.OnClickListener() {
 *            @Override
 *            public void onLoginClick(LoginDialog dialog) {
 *                Toa.show("登录成功", R.drawable.ic_prompt_success);
 *            }
 *
 *            @Override
 *            public void onRegisterClick(LoginDialog dialog) {
 *                Toa.show("注册成功", R.drawable.ic_prompt_success);
 *            }
 *
 *            @Override
 *            public void onVerifyClick() {
 *                Toa.show("请求验证码", R.drawable.ic_prompt_success);
 *            }
 *        }).show();
 * </pre>
 */
@SuppressLint("ValidFragment")
public class LoginDialog extends AbsDialogFragment implements View.OnClickListener {

    private static final int TYPE_LOGIN = 101;
    private static final int TYPE_REGISTER = 102;

    private static final int TYPE_LOGIN_BY_PASSWORD = 103;
    private static final int TYPE_LOGIN_BY_VERIFY = 104;

    private static final int DEFAULT_TIME = 60;

    @IntDef({TYPE_LOGIN, TYPE_REGISTER})
    @Retention(RetentionPolicy.SOURCE)
    @interface TabType {
    }

    @IntDef({TYPE_LOGIN_BY_PASSWORD, TYPE_LOGIN_BY_VERIFY})
    @Retention(RetentionPolicy.SOURCE)
    @interface Type {

    }

    /**
     * 控件
     */
    private TextView tvLogin, tvRegister, tvCountdown;
    private EditText etUsername, etPassword, etVerifyCode;
    private LinearLayout llVerify;

    /**
     * Tab切换的类型, 登录Tab {@link #TYPE_LOGIN}, 注册Tab {@link #TYPE_REGISTER}
     */
    private @TabType
    int mTabType;

    /**
     * 登录或注册的方式，密码登录 {@link #TYPE_LOGIN_BY_PASSWORD}，验证码登录 {@link #TYPE_LOGIN_BY_VERIFY}
     */
    private @Type
    int mLoginType;

    /**
     * 倒计时的总时间 {@link #DEFAULT_TIME}
     */
    private int mCountTime = DEFAULT_TIME;

    private OnClickListener onClickListener;

    private EditTextChangeHelper mEditTextChangeHelper;

    public LoginDialog setOnClickListener(OnClickListener onClickListener) {
        this.onClickListener = onClickListener;
        return this;
    }

    /************************************* 以下为自定义方法 *********************************/

    public static LoginDialog newInstance(AppCompatActivity activity) {
        LoginDialog loginDialog = new LoginDialog(activity);
        return loginDialog;
    }

    /**
     * @return 用户名输入框的文本
     */
    public String getUsername() {
        return etUsername.getText().toString().trim();
    }

    /**
     * @return 密码输入框的文本
     */
    public String getPassword() {
        return etPassword.getText().toString().trim();
    }

    /**
     * @return 验证码输入框的文本
     */
    public String getVerifyCode() {
        return etVerifyCode.getText().toString().trim();
    }

    /**
     * 通过密码登录
     *
     * @return this
     */
    public LoginDialog setLoginByPassword() {
        mLoginType = TYPE_LOGIN_BY_PASSWORD;
        return this;
    }

    /**
     * 通过验证码登录
     *
     * @return this
     */
    public LoginDialog setLoginByVerifyCode() {
        mLoginType = TYPE_LOGIN_BY_VERIFY;
        return this;
    }

    @Override
    public LoginDialog setMargin(int margin) {
        return super.setMargin(margin);
    }

    @Override
    public LoginDialog setGravity(int gravity) {
        return super.setGravity(gravity);
    }

    @Override
    public LoginDialog setAnimator(int animator) {
        return super.setAnimator(animator);
    }

    @Override
    public LoginDialog setBackground(Drawable background) {
        return super.setBackground(background);
    }

    @Override
    public LoginDialog setCenterWidth(int width) {
        return super.setCenterWidth(width);
    }

    @Override
    public LoginDialog setPrimaryColor(int primaryColor) {
        return super.setPrimaryColor(primaryColor);
    }

    @Override
    public LoginDialog setSecondaryColor(int secondaryColor) {
        return super.setSecondaryColor(secondaryColor);
    }

    @Override
    public LoginDialog setTranslucent(boolean translucent) {
        return super.setTranslucent(translucent);
    }

    @Override
    public LoginDialog setCancel(boolean cancel) {
        return super.setCancel(cancel);
    }

    @Override
    public LoginDialog setTouchInOutSideCancel(boolean cancel) {
        return super.setTouchInOutSideCancel(cancel);
    }

    /************************************* 以下为实现过程 *********************************/

    public LoginDialog(AppCompatActivity activity) {
        super(activity);
        mEditTextChangeHelper = EditTextChangeHelper.newInstance();
    }

    @Override
    protected int layoutId() {
        return R.layout.dialog_login;
    }

    @Override
    protected void initView() {
        tvLogin = findViewById(R.id.tv_login);
        tvRegister = findViewById(R.id.tv_register);

        etUsername = findViewById(R.id.et_username);
        etPassword = findViewById(R.id.et_password);

        llVerify = findViewById(R.id.ll_verife);
        tvCountdown = findViewById(R.id.tv_countdown);
        etVerifyCode = findViewById(R.id.et_verife_code);

        TextView tvConfirm = findViewById(R.id.tv_confirm);
        TextView tvCancel = findViewById(R.id.tv_cancel);

        View line_1 = findViewById(R.id.line_1);
        View line_2 = findViewById(R.id.line_2);

        // 点击事件
        tvLogin.setOnClickListener(this);
        tvRegister.setOnClickListener(this);
        tvConfirm.setOnClickListener(this);
        tvCancel.setOnClickListener(this);

        tvCountdown.setOnClickListener(this);

        // 登陆按钮和取消按钮点击颜色效果
        BackGHelper.radiusBR(mRadiusCard).pressed(mPressedColor).into(tvConfirm);
        BackGHelper.radiusBL(mRadiusCard).pressed(mPressedColor).into(tvCancel);

        // 默认Tab及Tab文本点击颜色样式
        setClickChangeTabStyle(tvLogin, tvRegister, mRadiusCard, 0f);

        // 设置输入框是否是Material Design颜色样式
        setEditTextStyle(tvCountdown, etUsername, etPassword, etVerifyCode, line_1, line_2);

        // 校验多个EditText是否输入了文本
        mEditTextChangeHelper.setEditText(etUsername, etPassword, etVerifyCode)
                .verify(true);
    }

    /**
     * 设置EditText样式
     *
     * @param tvCountdown  倒计时按钮
     * @param etUsername   用户名输入框
     * @param etPassword   密码输入框
     * @param etVerifyCode 验证码输入框
     * @param line_1       分割线
     * @param line_2       分割线
     */
    private void setEditTextStyle(TextView tvCountdown, EditText etUsername, EditText etPassword, EditText etVerifyCode, View line_1, View line_2) {
        if (isDivide) {

        } else {
            etUsername.setBackgroundResource(R.drawable.bg_gray_border);
            etPassword.setBackgroundResource(R.drawable.bg_gray_border);
            etVerifyCode.setBackgroundResource(R.drawable.bg_gray_border);
            line_1.setBackgroundColor(getColor(R.color.colorDivideDark));
            line_2.setBackgroundColor(getColor(R.color.colorDivideDark));
        }
        // 验证码按钮点击颜色效果
        BackGHelper.radiusTL(4f)
                .radiusTR(4f)
                .pressed(mPrimaryColor, mSecondaryColor)
                .into(tvCountdown);
        tvCountdown.setTextColor(getColor(android.R.color.white));

    }

    @Override
    public void onStart() {
        super.onStart();
        setLoginType(false);
    }

    @Override
    public void onClick(View view) {
        if (onClickListener != null) {
            if (view.getId() == R.id.tv_confirm) {             // 确认按钮
                if (isTabLogin()) {
                    if (isLoginType()) etVerifyCode.setText("empty");
                    else etPassword.setText("empty");
                }
                if (mEditTextChangeHelper.verify(false)) {
                    if (isTabLogin()) onClickListener.onLoginClick(this);
                    else onClickListener.onRegisterClick(this);
                }
            } else if (view.getId() == R.id.tv_cancel) {       // 取消按钮
                dismiss();
            } else if (view.getId() == R.id.tv_login) {        // 登录按钮（切换Tab）
                setTagSelected(TYPE_LOGIN);
            } else if (view.getId() == R.id.tv_register) {     // 注册按钮（切换Tab）
                setTagSelected(TYPE_REGISTER);
            } else if (view.getId() == R.id.tv_countdown) {    // 倒计时按钮
                // 倒计时按钮变灰并不可点击
                BackGHelper.solid(mSecondaryColor)
                        .radiusTR(4)
                        .radiusBR(4)
                        .into(tvCountdown);
                tvCountdown.setEnabled(false);
                // 倒计时开始
                mDownTimer.start();
                // 请求验证码
                onClickListener.onVerifyClick();
            }
        } else {
            throw new NullPointerException("onClickListener is null");
        }
    }

    /**
     * 选中的标题类型
     */
    private void setTagSelected(int type) {
        // 获取第一个EditText焦点
        etUsername.requestFocus();
        if (mTabType == type) return;
        mTabType = type;
        if (isTabLogin()) {             // 当前选中的Tab为登陆按钮
            setClickChangeTabStyle(tvLogin, tvRegister, mRadiusCard, 0);
            setLoginType(false);
        } else {                        // 当前选中的Tab为注册按钮
            setClickChangeTabStyle(tvRegister, tvLogin, 0, mRadiusCard);
            setLoginType(true);
        }
        reset();
    }

    /**
     * Tab点击切换的背景样式
     *
     * @param selectView   选中的View
     * @param unSelectView 未选中的View
     * @param leftRadius   左边的圆角
     * @param rightRadius  右边的圆角
     */
    private void setClickChangeTabStyle(TextView selectView, TextView unSelectView, float leftRadius, float rightRadius) {
        // 选中的Tab, 白色背景、主色边框, 点击显示灰色背景、副色边框,
        BackGHelper.pressed(BackGHelper.solid(android.R.color.white)
                        .radiusTL(rightRadius)
                        .radiusTR(leftRadius)
                        .stroke(1, mPrimaryColor)
                        .build(),
                BackGHelper.solid(mPressedColor)
                        .radiusTL(rightRadius)
                        .radiusTR(leftRadius)
                        .stroke(1, mSecondaryColor)
                        .build()
        ).into(unSelectView);
        unSelectView.setTextColor(getColor(mPrimaryColor));

        // 未选中的Tab, 主色背景, 点击显示副色背景
        BackGHelper.radiusTL(leftRadius)
                .radiusTR(rightRadius)
                .pressed(mPrimaryColor, mSecondaryColor)
                .into(selectView);
        selectView.setTextColor(getColor(android.R.color.white));
    }

    /**
     * 切换Tab时输入置空
     */
    private void reset() {
        resetCountDown();
        etUsername.setText("");
        etPassword.setText("");
        etVerifyCode.setText("");
        mEditTextChangeHelper.verify(true);
    }

    /**
     * 登录的方式
     */
    private void setLoginType(boolean visible) {
        if (isLoginType()) llVerify.setVisibility(visible ? View.VISIBLE : View.GONE);
        else etPassword.setVisibility(visible ? View.VISIBLE : View.GONE);
    }

    private CountDownTimer mDownTimer = new CountDownTimer(mCountTime * 1000, 1000) {
        @Override
        public void onTick(long millisUntilFinished) {
            tvCountdown.setText(millisUntilFinished / 1000 + "秒");
        }

        @Override
        public void onFinish() {
            if (isAdded()) resetCountDown();
        }
    };

    /**
     * 重置倒计时文本
     */
    private void resetCountDown() {
        if (mDownTimer != null) mDownTimer.cancel();
        // 倒计时结束时可重新点击获取并设置显式的可点击的颜色以及提示文字
        tvCountdown.setText(getString(R.string.dialog_reset_countdown));
        tvCountdown.setEnabled(true);
        BackGHelper.radiusTL(4f)
                .radiusTR(4f)
                .pressed(mPrimaryColor, mSecondaryColor)
                .into(tvCountdown);
    }

    /**
     * @return 是否是登录Tab
     */
    private boolean isTabLogin() {
        return mTabType == TYPE_LOGIN;
    }

    /**
     * @return 是否是登陆默认的登录方式
     */
    private boolean isLoginType() {
        return mLoginType == TYPE_LOGIN_BY_PASSWORD;
    }


    public interface OnClickListener {

        void onLoginClick(LoginDialog dialog);

        void onRegisterClick(LoginDialog dialog);

        void onVerifyClick();
    }
}
