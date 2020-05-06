package com.dzenm.lib.material;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.app.AppCompatDialogFragment;

/**
 * @author dzenm
 * @date 2020/4/10 23:18
 * @IDE Android Studio
 */
public abstract class AbsDialogFragment extends AppCompatDialogFragment {

    /**
     * dialog delegate, {@link DialogDelegate}
     */
    protected DialogDelegate mD;

    /**
     * manager fragment's activity
     */
    protected AppCompatActivity mActivity;

    static {
        // 开启在TextView的drawableTop或者其他额外方式使用矢量图渲染
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
    }

    public AbsDialogFragment(AppCompatActivity activity) {
        mActivity = activity;
        mD = new DialogDelegate(this, activity);
    }

    /************************************* 以下为自定义方法 *********************************/

    /**
     * 设置一些属性完成之后，调用该方法创建fragment并显示。
     *
     * @return this
     */
    public void show() {
        show(this.getClass().getSimpleName());
    }

    /**
     * @param tag 给fragment添加tag
     * @return this
     */
    public void show(String tag) {
        show(mActivity.getSupportFragmentManager(), tag);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mD.onCreate();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return mD.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        mD.onViewCreated(view, savedInstanceState);
    }

    protected abstract void initView();

    @Override
    public void onStart() {
        super.onStart();
//        Animation startAnimator = OptAnimationLoader.loadAnimation(mActivity,
//                R.anim.dialog_scale_shrink_in);
//        mContentView.setAnimation(startAnimator);
        onDialogAnimator();
        onDialogMargin(getDecorView());
        apply(mD.getWindow());
    }

    protected void apply(Window window) {
        mD.apply(window);
    }

    protected LinearLayout getDecorView() {
        return mD.mDecorView;
    }

    /**
     * 初始化dialog的大小
     */
    protected void onDialogMargin(View decorView) {
        mD.setDialogMargin(decorView);
    }

    /**
     * 通过rootView的LayoutParams设定margin
     *
     * @param layoutParams
     * @param margin
     */
    protected void onDialogLayoutParams(ViewGroup.MarginLayoutParams layoutParams, int margin) {
        mD.onDialogLayoutParams(layoutParams, margin);
    }

    /**
     * 通过WindowManager的layoutParams设置显示位置
     *
     * @param layoutParams
     */
    protected void onDialogGravity(WindowManager.LayoutParams layoutParams) {
        mD.onDialogGravity(layoutParams);
    }

    protected void onDialogAnimator() {
        mD.onDialogAnimator();
    }
}
