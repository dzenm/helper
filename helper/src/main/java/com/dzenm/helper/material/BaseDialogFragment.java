package com.dzenm.helper.material;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

import androidx.annotation.IdRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.app.AppCompatDialogFragment;

/**
 * @author dinzhenyan
 * @date 2019-05-18 15:23
 */
public abstract class BaseDialogFragment extends AppCompatDialogFragment implements DialogFragmentInterface {

    private DialogFragmentDelegate mDelegate;
    protected AppCompatActivity mActivity;

    static {
        // 开启在TextView的drawableTop或者其他额外方式使用矢量图渲染
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
    }

    /************************************* 以下为自定义方法 *********************************/

    public <T extends BaseDialogFragment> T setMargin(int margin) {
        return (T) mDelegate.setMargin(margin);
    }

    public <T extends BaseDialogFragment> T setGravity(int gravity) {
        return (T) mDelegate.setGravity(gravity);
    }

    public <T extends BaseDialogFragment> T setAnimator(int animator) {
        return (T) mDelegate.setAnimator(animator);
    }

    public <T extends BaseDialogFragment> T setBackground(Drawable background) {
        return (T) mDelegate.setBackground(background);
    }

    public <T extends BaseDialogFragment> T setBackgroundRectangle() {
        return (T) mDelegate.setBackgroundRectangle();
    }

    public <T extends BaseDialogFragment> T setCenterWidth(int width) {
        return (T) mDelegate.setCenterWidth(width);
    }

    public <T extends BaseDialogFragment> T setPrimaryColor(int primaryColor) {
        return (T) mDelegate.setPrimaryColor(primaryColor);
    }

    public <T extends BaseDialogFragment> T setSecondaryColor(int secondaryColor) {
        return (T) mDelegate.setSecondaryColor(secondaryColor);
    }

    public <T extends BaseDialogFragment> T setDimAccount(float dimAccount) {
        return (T) mDelegate.setDimAccount(dimAccount);
    }

    public <T extends BaseDialogFragment> T setOnClickListener(DialogFragmentDelegate.OnClickListener onClickListener) {
        return (T) mDelegate.setOnClickListener(onClickListener);
    }

    public <T extends BaseDialogFragment> T setCancel(boolean cancel) {
        return (T) mDelegate.setCancel(cancel);
    }

    public <T extends BaseDialogFragment> T setTouchInOutSideCancel(boolean cancel) {
        return (T) mDelegate.setTouchInOutSideCancel(cancel);
    }

    public <T extends BaseDialogFragment> T setDivide(boolean divide) {
        return (T) mDelegate.setDivide(divide);
    }

    public <T extends BaseDialogFragment> T setMaterialDesign(boolean materialDesign) {
        return (T) mDelegate.setMaterialDesign(materialDesign);
    }

    public <T extends BaseDialogFragment> T setRadiusCard(float radiusCard) {
        return (T) mDelegate.setRadiusCard(radiusCard);
    }

    public <T extends BaseDialogFragment> T show() {
        return (T) mDelegate.show();
    }

    public <T extends BaseDialogFragment> T show(String tag) {
        return (T) mDelegate.show(tag);
    }

    /************************************* 以下为实现过程 *********************************/

    public BaseDialogFragment(AppCompatActivity activity) {
        mActivity = activity;
        mDelegate = new DialogFragmentDelegate(this, mActivity);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mDelegate.onCreate();
    }

    protected boolean isFullScreen() {
        return mDelegate.isFullScreen();
    }

    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState
    ) {
        return mDelegate.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public int layoutId() {
        return -1;
    }

    @Override
    public void initView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState
    ) {
    }

    public <T extends View> T findViewById(@IdRes int id) {
        return mDelegate.findViewById(id);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        mDelegate.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onStart() {
        super.onStart();
//                Animation startAnimator = OptAnimationLoader.loadAnimation(mActivity,
//                R.anim.dialog_scale_shrink_in);
//        mContentView.setAnimation(startAnimator);
        onDialogMargin(getDecorView());
        onDialogAnimator();
        apply(getWindow());
    }

    public View getDecorView() {
        return mDelegate.getView();
    }

    public void setDecorView(View view) {
        mDelegate.setView(view);
    }

    /**
     * 初始化dialog的大小
     */
    protected void onDialogMargin(View decorView) {
        mDelegate.setDialogMargin(decorView);
    }

    /**
     * 通过rootView的LayoutParams设定margin
     *
     * @param layoutParams
     * @param centerWidth
     * @param margin
     */
    @Override
    public void onDialogLayoutParams(ViewGroup.MarginLayoutParams layoutParams, int centerWidth, int margin) {
        mDelegate.onDialogLayoutParams(layoutParams, centerWidth, margin);
    }

    /**
     * 通过WindowManager的layoutParams设置显示位置
     *
     * @param layoutParams
     */
    @Override
    public void onDialogGravity(WindowManager.LayoutParams layoutParams) {
        mDelegate.onDialogGravity(layoutParams);
    }

    /**
     * 设置默认的动画效果
     */
    @Override
    public void onDialogAnimator() {
        mDelegate.onDialogAnimator();
    }

    /**
     * 设置Windows的属性
     *
     * @param window
     */
    @Override
    public void apply(Window window) {
        mDelegate.apply(window);
    }

    protected Window getWindow() {
        return mDelegate.getWindow();
    }

    /**
     * @return 是否显示在中间
     */
    boolean isDialogInCenter() {
        return mDelegate.isDialogInCenter();
    }

    /**
     * @param id 字符串id(位于 res/values/strings.xml)
     * @return 字符串
     */
    protected String getStrings(int id) {
        return mDelegate.getStrings(id);
    }

    /**
     * @param id 颜色id(位于 res/values/colors.xml)
     * @return 颜色值
     */
    protected int getColor(int id) {
        return mDelegate.getColor(id);
    }
}
