package com.dzenm.lib.base;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.dzenm.lib.log.Logger;

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
    private Fragment mCurrentFragment;

    private int mFrameLayoutResId;

    /**
     * @param activity Activity for fragment parent
     * @param resId    FrameLayout id in layout
     */
    public FragmentHelper(AppCompatActivity activity, int resId) {
        mActivity = activity;
        mFrameLayoutResId = resId;
    }

    /**
     * @param fragment fragment for child fragment parent
     * @param resId    FrameLayout id in layout
     */
    public FragmentHelper(Fragment fragment, int resId) {
        mFragment = fragment;
        mFrameLayoutResId = resId;
    }

    /**
     * 显示Activity的子Fragment
     *
     * @param targetFragment 需要显示的子Fragment
     * @return this
     */
    public FragmentHelper show(@NonNull Fragment targetFragment) {
        if (targetFragment == mCurrentFragment) {
            return this;
        }
        FragmentTransaction transaction = getFragmentTransaction(mActivity);
        String tag = targetFragment.getClass().getName();
        if (targetFragment.isAdded()) {
            show(transaction, targetFragment, getName(mActivity));
        } else {
            add(transaction, targetFragment, tag, getName(mActivity));
        }
        transaction.commitAllowingStateLoss();
        return this;
    }

    /**
     * 隐藏Activity的子Fragment
     *
     * @param fragment 隐藏的Fragment
     */
    public void hide(@NonNull Fragment fragment) {
        FragmentTransaction transaction = getFragmentTransaction(mActivity);
        if (fragment.isAdded() && fragment.isVisible()) {
            transaction.hide(fragment);
        }
        transaction.commitAllowingStateLoss();
    }

    /**
     * 显示Fragment的子Fragment
     *
     * @param targetFragment 需要显示的子Fragment
     * @return this
     */
    public FragmentHelper showChild(@NonNull Fragment targetFragment) {
        if (targetFragment == mCurrentFragment) {
            return this;
        }
        FragmentTransaction transaction = getFragmentTransaction(mFragment);
        String tag = targetFragment.getClass().getName();
        if (targetFragment.isAdded()) {
            show(transaction, targetFragment, getName(mFragment));
        } else {
            add(transaction, targetFragment, tag, getName(mFragment));
        }
        transaction.commitAllowingStateLoss();
        return this;
    }

    /**
     * 隐藏Fragment的子Fragment
     *
     * @param fragment 隐藏的Fragment
     */
    public void hideChild(@NonNull Fragment fragment) {
        FragmentTransaction transaction = getFragmentTransaction(mFragment);
        if (fragment.isAdded() && fragment.isVisible()) {
            transaction.hide(fragment);
        }
        transaction.commitAllowingStateLoss();
    }

    private FragmentTransaction getFragmentTransaction(AppCompatActivity activity) {
        return activity.getSupportFragmentManager().beginTransaction();
    }

    private FragmentTransaction getFragmentTransaction(Fragment fragment) {
        return fragment.getChildFragmentManager().beginTransaction();
    }

    private void show(FragmentTransaction transaction, Fragment fragment, String className) {
        if (mCurrentFragment.isAdded() && mCurrentFragment.isVisible() && mCurrentFragment != null) {
            transaction.hide(mCurrentFragment);
        }
        transaction.show(fragment);
        Logger.d(TAG + className + " show fragment: " +
                fragment.getClass().getSimpleName());
        mCurrentFragment = fragment;
    }

    private void add(FragmentTransaction transaction, Fragment fragment, String tag, String className) {
        if (mCurrentFragment != null && mCurrentFragment.isAdded() && mCurrentFragment.isVisible()) {
            transaction.hide(mCurrentFragment);
        }
        transaction.add(mFrameLayoutResId, fragment, tag);
        Logger.d(TAG + className + " add fragment: " +
                fragment.getClass().getSimpleName());
        mCurrentFragment = fragment;
    }

    public String getName(AppCompatActivity activity) {
        return activity.getClass().getSimpleName();
    }

    public String getName(Fragment fragment) {
        return fragment.getClass().getSimpleName();
    }
}