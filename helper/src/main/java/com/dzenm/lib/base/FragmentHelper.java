package com.dzenm.lib.base;

import com.dzenm.lib.log.Logger;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

/**
 * @author dinzhenyan
 * @date 2019-04-30 20:03
 *
 * <pre>
 * HomeFragment f1 = new HomeFragment();
 * PersonalFragment f2 = new PersonalFragment();
 * List<Fragment> fragments = new ArrayList<>();
 * fragments.add(f1);
 * fragments.add(f2);
 *
 * FragmentHelper fragmentHelper = new FragmentHelper(this);
 * fragmentHelper.inflate(R.id.frame_layout, fragments);
 * fragmentHelper.show(f1);
 * </pre>
 */
public class FragmentHelper {

    private static final String TAG = FragmentHelper.class.getSimpleName() + "| ";

    private AppCompatActivity mActivity;
    private Fragment mFragment;

    private List<Fragment> mFragments;
    private int mFrameLayoutResId;

    public FragmentHelper(AppCompatActivity activity) {
        mActivity = activity;
    }

    public FragmentHelper(Fragment fragment) {
        mFragment = fragment;
    }

    /**
     * @param resId     FrameLayout id in layout
     * @param fragments 管理的Fragment
     * @return this
     */
    public FragmentHelper inflate(int resId, @NonNull Fragment[] fragments) {
        mFrameLayoutResId = resId;
        addToStack(Arrays.asList(fragments));
        return this;
    }

    /**
     * @param resId     FrameLayout id in layout
     * @param fragments 管理的Fragment
     * @return this
     */
    public FragmentHelper inflate(int resId, @NonNull List<Fragment> fragments) {
        mFrameLayoutResId = resId;
        addToStack(fragments);
        return this;
    }

    private void addToStack(@NonNull List<Fragment> fragments) {
        mFragments = new ArrayList<>();
        mFragments.addAll(fragments);
    }

    /**
     * 显示Activity的子Fragment
     *
     * @param fragment 需要显示的子Fragment
     * @return this
     */
    public FragmentHelper show(Fragment fragment) {
        FragmentTransaction transaction = getFragmentTransaction(mActivity);
        for (Fragment f : mFragments) {
            if (f == fragment) {
                if (f.isAdded()) {
                    if (f.isHidden()) {
                        transaction.show(fragment);
                        Logger.d(TAG + getName(mActivity) + " show fragment: " + fragment.getClass().getSimpleName());
                    }
                } else {
                    transaction.add(mFrameLayoutResId, fragment);
                    Logger.d(TAG + getName(mActivity) + " add fragment: " + fragment.getClass().getSimpleName());
                }
            } else if (f.isAdded() && f.isVisible()) {
                transaction.hide(f);
            }
        }
        transaction.commitAllowingStateLoss();
        return this;
    }

    /**
     * 隐藏Activity的子Fragment
     *
     * @param fragment 隐藏的Fragment
     */
    public void hide(Fragment fragment) {
        FragmentTransaction transaction = getFragmentTransaction(mActivity);
        hide(transaction, fragment);
        transaction.commitAllowingStateLoss();
    }

    /**
     * 显示Fragment的子Fragment
     *
     * @param fragment 需要显示的子Fragment
     * @return this
     */
    public FragmentHelper showChild(Fragment fragment) {
        FragmentTransaction transaction = getFragmentTransaction(mFragment);
        for (Fragment f : mFragments) {
            if (f == fragment) {
                if (f.isAdded()) {
                    if (f.isHidden()) {
                        transaction.show(fragment);
                        Logger.d(TAG + getName(mFragment) + " show fragment: " + getName(fragment));
                    }
                } else {
                    transaction.add(mFrameLayoutResId, fragment);
                    Logger.d(TAG + getName(mFragment) + " add fragment: " + getName(fragment));
                }
            } else if (f.isAdded() && f.isVisible()) {
                transaction.hide(f);
            }
        }
        transaction.commitAllowingStateLoss();
        return this;
    }

    /**
     * 隐藏Fragment的子Fragment
     *
     * @param fragment 隐藏的Fragment
     */
    public void hideChild(Fragment fragment) {
        FragmentTransaction transaction = getFragmentTransaction(mFragment);
        hide(transaction, fragment);
        transaction.commitAllowingStateLoss();
    }

    /**
     * 隐藏Fragment
     *
     * @param transaction 事物管理
     * @param fragment    需要隐藏的Fragment
     */
    private void hide(FragmentTransaction transaction, Fragment fragment) {
        if (fragment.isAdded() && fragment.isVisible()) {
            transaction.hide(fragment);
        }
    }

    private FragmentTransaction getFragmentTransaction(AppCompatActivity activity) {
        return activity.getSupportFragmentManager().beginTransaction();
    }

    private FragmentTransaction getFragmentTransaction(Fragment fragment) {
        return fragment.getChildFragmentManager().beginTransaction();
    }

    public String getName(AppCompatActivity activity) {
        return activity.getClass().getSimpleName();
    }

    public String getName(Fragment fragment) {
        return fragment.getClass().getSimpleName();
    }
}