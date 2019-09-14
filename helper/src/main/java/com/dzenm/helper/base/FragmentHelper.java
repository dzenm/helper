package com.dzenm.helper.base;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.appcompat.app.AppCompatActivity;

import com.dzenm.helper.log.Logger;

import java.util.ArrayList;
import java.util.List;

/**
 * @author dzenm
 * @date 2019-09-12 14:54
 */
public class FragmentHelper {

    private static final String TAG = FragmentHelper.class.getSimpleName() + "|";
    private AppCompatActivity mActivity;
    private Fragment mFragment;
    private List<Fragment> mFragments;
    private int mLayoutId;

    /**
     * 适用于在Activity里添加Fragment
     *
     * @param activity
     * @param layoutId
     * @param fragments
     */
    public FragmentHelper(AppCompatActivity activity, int layoutId, Fragment[] fragments) {
        mActivity = activity;
        mLayoutId = layoutId;
        mFragments = new ArrayList<>();
        for (int i = 0; i < fragments.length; i++) {
            mFragments.add(fragments[i]);
        }
    }

    /**
     * 适用于在Fragment里添加Fragment
     *
     * @param fragment
     * @param layoutID
     * @param fragments
     */
    public FragmentHelper(Fragment fragment, int layoutID, Fragment[] fragments) {
        mFragment = fragment;
        mLayoutId = layoutID;
        mFragments = new ArrayList<>();
        for (int i = 0; i < fragments.length; i++) {
            mFragments.add(fragments[i]);
        }
    }

    /**
     * 隐藏Activity里所有的Fragment
     *
     * @return
     */
    public FragmentHelper hideAll() {
        FragmentTransaction transaction = mActivity.getSupportFragmentManager().beginTransaction();
        for (int i = 0; i < mFragments.size(); i++) {
            if (mFragments.get(i).isVisible()) {
                transaction.hide(mFragments.get(i));
            }
        }
        transaction.commitAllowingStateLoss();
        return this;
    }

    /**
     * 隐藏Fragment里所有的Fragment
     *
     * @return
     */
    public FragmentHelper hideChildAll() {
        FragmentTransaction transaction = mFragment.getChildFragmentManager().beginTransaction();
        for (int i = 0; i < mFragments.size(); i++) {
            if (mFragments.get(i).isVisible()) {
                transaction.hide(mFragments.get(i));
            }
        }
        transaction.commitAllowingStateLoss();
        return this;
    }

    /**
     * 显示Activity里的一个Fragment
     *
     * @param fragment
     * @return
     */
    public FragmentHelper show(Fragment fragment) {
        hideAll();
        FragmentTransaction transaction = mActivity.getSupportFragmentManager().beginTransaction();
        if (fragment.isAdded()) {
            transaction.show(fragment);
            Logger.d(TAG + mActivity.getClass().getSimpleName() + " show fragment: " + fragment.getClass().getSimpleName());
        } else {
            transaction.add(mLayoutId, fragment);
            mFragments.add(fragment);
            Logger.d(TAG + mActivity.getClass().getSimpleName() + " add and show fragment: " + fragment.getClass().getSimpleName());
        }
        transaction.commitAllowingStateLoss();
        return this;
    }

    /**
     * 显示Fragment里的一个Fragment
     *
     * @param fragment
     * @return
     */
    public FragmentHelper showChild(Fragment fragment) {
        hideChildAll();
        FragmentTransaction transaction = mFragment.getChildFragmentManager().beginTransaction();
        if (fragment.isAdded()) {
            transaction.show(fragment);
            Logger.d(TAG + mFragment.getClass().getSimpleName() + " show fragment: " + fragment.getClass().getSimpleName());
        } else {
            transaction.add(mLayoutId, fragment);
            mFragments.add(fragment);
            Logger.d(TAG + mFragment.getClass().getSimpleName() + " add and show fragment: " + fragment.getClass().getSimpleName());
        }
        transaction.commitAllowingStateLoss();
        return this;
    }
}
