package com.dzenm.helper.base;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import java.util.ArrayList;
import java.util.List;

public class FragmentHelper {

    private AppCompatActivity mActivity;
    private Fragment mFragment;
    private List<Fragment> mFragments;
    private int mResourceID;

    public static FragmentHelper newInstance() {
        return new FragmentHelper();
    }

    public FragmentHelper with(AppCompatActivity activity) {
        mActivity = activity;
        return this;
    }

    public FragmentHelper with(Fragment fragment) {
        mFragment = fragment;
        return this;
    }

    public FragmentHelper container(int resourceID) {
        mResourceID = resourceID;
        return this;
    }

    public void addToStack(@NonNull List<Fragment> fragments) {
        mFragments = new ArrayList<>();
        FragmentTransaction transaction = mActivity.getSupportFragmentManager().beginTransaction();
        for (Fragment f : fragments) {
            if (!f.isAdded()) {
                mFragments.add(f);
                transaction.add(mResourceID, f).hide(f);
            }
        }
        transaction.commitAllowingStateLoss();
    }

    public int size() {
        return mFragments.size();
    }

    public Fragment get(int position) {
        return mFragments.get(position);
    }

    public void show(Fragment currentFragment) {
        FragmentTransaction transaction = mActivity.getSupportFragmentManager().beginTransaction();
        for (int i = 0; i < size(); i++) {
            if (isShow(i) && get(i) != currentFragment) {
                transaction.hide(get(i));
            } else {
                transaction.show(currentFragment);
            }
        }
        transaction.commitAllowingStateLoss();
    }

    public void showChild(Fragment currentFragment) {
        FragmentTransaction transaction = mFragment.getChildFragmentManager().beginTransaction();
        for (int i = 0; i < size(); i++) {
            if (isShow(i) && get(i) != currentFragment) {
                transaction.hide(get(i));
            } else {
                transaction.show(currentFragment);
            }
        }
        transaction.commitAllowingStateLoss();
    }

    private boolean isShow(int position) {
        return get(position).isAdded() && (get(position).isVisible() ||
                !get(position).isHidden());
    }

    public FragmentHelper hide() {
        FragmentTransaction transaction = mActivity.getSupportFragmentManager().beginTransaction();
        for (int i = 0; i < size(); i++) {
            if (isShow(i)) {
                transaction.hide(get(i));
            }
        }
        transaction.commitAllowingStateLoss();
        return this;
    }
}