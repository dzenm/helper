package com.dzenm.helper.permission;

import android.annotation.SuppressLint;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.dzenm.helper.log.Logger;

import java.util.List;

/**
 * <pre>
 *     PermissionManager.getInstance()
 *          .with(this)
 *          .load(permissions)
 *          .into(this)
 *          .requestPermission();
 * </pre>
 *
 * @author dinzhenyan
 * @date 2019-04-30 20:03
 * <p>
 * 权限请求管理工具类
 */
public final class PermissionManager {

    private static final String TAG = PermissionManager.class.getSimpleName() + "| ";

    public static final int MODE_ONCE = 1;
    public static final int MODE_ONCE_INFO = 2;
    public static final int MODE_REPEAT = 3;

    @SuppressLint("StaticFieldLeak")
    private static volatile PermissionManager sPermissionManager;

    private PermissionFragment mFragment;

    private PermissionManager() {
    }

    public static PermissionManager getInstance() {
        if (sPermissionManager == null) synchronized (PermissionManager.class) {
            if (sPermissionManager == null)
                sPermissionManager = new PermissionManager();
        }
        return sPermissionManager;
    }

    public PermissionManager with(AppCompatActivity activity) {
        Logger.d(TAG + activity.getClass().getSimpleName() + " is requesting permission");
        return beginFragmentTransaction(activity.getSupportFragmentManager());
    }

    public PermissionManager with(Fragment fragment) {
        Logger.d(TAG + fragment.getClass().getSimpleName() + " is requesting permission");
        return beginFragmentTransaction(fragment.getChildFragmentManager());
    }

    private PermissionManager beginFragmentTransaction(FragmentManager manager) {
        if (mFragment == null) {
            mFragment = new PermissionFragment();
        }
        if (!mFragment.isAdded()) {
            manager.beginTransaction()
                    .add(mFragment, TAG)
                    .commitNow();
        }
        return this;
    }

    /**
     * @param requestMode {@link }
     * @return this
     */
    public PermissionManager mode(@PermissionFragment.Mode int requestMode) {
        mFragment.mRequestMode = requestMode;
        return this;
    }

    /**
     * @param permissions {@link }
     * @return this
     */
    public PermissionManager load(String permissions) {
        mFragment.mAllPermissions = new String[]{permissions};
        return this;
    }

    /**
     * @param permissions {@link }
     * @return this
     */
    public PermissionManager load(List<String> permissions) {
        mFragment.mAllPermissions = permissions.toArray(new String[permissions.size()]);
        return this;
    }

    /**
     * @param permissions {@link}
     * @return this
     */
    public PermissionManager load(String[] permissions) {
        mFragment.mAllPermissions = permissions;
        return this;
    }

    /**
     * @param onPermissionListener {@link }
     * @return this
     */
    public PermissionManager into(OnPermissionListener onPermissionListener) {
        mFragment.mOnPermissionListener = onPermissionListener;
        return this;
    }

    public void request() {
        mFragment.startRequestPermission();
    }

    public interface OnPermissionListener {

        void onPermit(boolean isGrant);
    }
}
