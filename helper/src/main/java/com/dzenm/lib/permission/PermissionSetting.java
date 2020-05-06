package com.dzenm.lib.permission;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;

import com.dzenm.lib.log.Logger;

import androidx.fragment.app.Fragment;

/**
 * @author dinzhenyan
 * @date 2019-04-30 20:03
 * <p>
 * 权限设置页（兼容大部分国产手机）
 */
public final class PermissionSetting {

    public static final String TAG = PermissionSetting.class.getSimpleName() + "| ";

    /*
     * 华为手机的一些包名常量
     */
    private static final String HUAWEI_PACKAGE = "com.huawei.systemmanager";
    private static final String HUAWEI_UI_PERMISSION = "com.huawei.permissionmanager.ui.MainActivity";
    private static final String HUAWEI_UI_SYSTEM = "com.huawei.systemmanager.addviewmonitor.AddViewMonitorActivity";
    private static final String HUAWEI_UI_NOTIFICATION = "com.huawei.notificationmanager.ui.NotificationManagmentActivity";

    /*
     * 小米手机的一些包名常量
     */
    private static final String MIUI_INTENT = "miui.intent.action.APP_PERM_EDITOR";
    private static final String MIUI_PACKAGE = "com.miui.securitycenter";
    private static final String MIUI_UI_APP_PERMISSION = "com.miui.permcenter.permissions.AppPermissionsEditorActivity";
    private static final String MIUI_UI_PERMISSION = "com.miui.permcenter.permissions.PermissionsEditorActivity";

    /*
     * OPPO手机的一些包名常量
     */
    private static final String OPPO_PACKAGE_COLOR = "com.color.safecenter";
    private static final String OPPO_PACKAGE_COLOR_OS = "com.coloros.safecenter";
    private static final String OPPO_PACKAGE_OPPO = "com.oppo.safe";
    private static final String OPPO_UI_PERMISSION = "com.color.safecenter.permission.floatwindow.FloatWindowListActivity";
    private static final String OPPO_UI_SYS = "com.coloros.safecenter.sysfloatwindow.FloatWindowListActivity";
    private static final String OPPO_UI_SAFE = "com.oppo.safe.permission.PermissionAppListActivity";

    /*
     * VIVO手机的一些包名常量
     */
    private static final String VIVO_PACKAGE = "com.iqoo.secure";
    private static final String VIVO_UI_SECURE_PHONE = "com.iqoo.secure.ui.phoneoptimize.FloatWindowManager";
    private static final String VIVO_UI_SAFE_PERMISSION = "com.iqoo.secure.safeguard.SoftPermissionDetailActivity";

    /*
     * 魅族手机的一些包名常量
     */
    private static final String MEIZU_INTENT = "com.meizu.safe.security.SHOW_APPSEC";
    private static final String MEIZU_PACKAGE = "com.meizu.safe";
    private static final String MEIZU_UI_PERMISSION = "com.meizu.safe.security.AppSecActivity";

    private static final String DEFAULT_PACKAGE = "packageName";

    /**
     * @return 获取手机厂商名称
     */
    static String mark() {
        return Build.MANUFACTURER.toLowerCase();
    }

    /**
     * 一般手机通过该方法打开设置界面
     *
     * @param activity 当前Activity
     * @return intent
     */
    public static Intent normal(Activity activity) {
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        intent.setData(Uri.fromParts("package", activity.getPackageName(), null));
        return intent;
    }

    /**
     * 跳转华为设置页面的Intent
     *
     * @param activity 当前Activity
     * @return intent
     */
    public static Intent huawei(Activity activity) {
        Intent intent = new Intent();
        intent.setComponent(new ComponentName(HUAWEI_PACKAGE, HUAWEI_UI_PERMISSION));
        if (isEmptyIntent(activity, intent)) {
            intent.setComponent(new ComponentName(HUAWEI_PACKAGE, HUAWEI_UI_SYSTEM));
            if (isEmptyIntent(activity, intent)) {
                intent.setComponent(new ComponentName(HUAWEI_PACKAGE, HUAWEI_UI_NOTIFICATION));
            }
        }
        return intent;
    }

    /**
     * 跳转小米设置页面的Intent
     *
     * @param activity 当前Activity
     * @return intent
     */
    public static Intent xiaomi(Activity activity) {
        Intent intent = new Intent(MIUI_INTENT);
        intent.putExtra("extra_pkgname", activity.getPackageName());
        if (isEmptyIntent(activity, intent)) {
            intent.setPackage(MIUI_PACKAGE);
            if (isEmptyIntent(activity, intent)) {
                intent.setClassName(MIUI_PACKAGE, MIUI_UI_APP_PERMISSION);
                if (isEmptyIntent(activity, intent)) {
                    intent.setClassName(MIUI_PACKAGE, MIUI_UI_PERMISSION);
                }
            }
        }
        return intent;
    }

    /**
     * 跳转OPPO设置页面的Intent
     *
     * @param activity 当前Activity
     * @return
     */
    public static Intent oppo(Activity activity) {
        Intent intent = new Intent();
        intent.putExtra(DEFAULT_PACKAGE, activity.getPackageName());
        if (isEmptyIntent(activity, intent)) {
            intent.setClassName(OPPO_PACKAGE_COLOR, OPPO_UI_PERMISSION);
            if (isEmptyIntent(activity, intent)) {
                intent.setClassName(OPPO_PACKAGE_COLOR_OS, OPPO_UI_SYS);
                if (isEmptyIntent(activity, intent)) {
                    intent.setClassName(OPPO_PACKAGE_OPPO, OPPO_UI_SAFE);
                }
            }
        }
        return intent;
    }

    /**
     * 跳转VIVO设置页面的Intent
     *
     * @param activity 当前Activity
     * @return intent
     */
    public static Intent vivo(Activity activity) {
        Intent intent = new Intent();
        intent.setClassName(VIVO_PACKAGE, VIVO_UI_SECURE_PHONE);
        intent.putExtra(DEFAULT_PACKAGE, activity.getPackageName());
        if (isEmptyIntent(activity, intent)) {
            intent.setComponent(new ComponentName(VIVO_PACKAGE, VIVO_UI_SAFE_PERMISSION));
        }
        return intent;
    }

    /**
     * 跳转魅族设置页面的Intent
     *
     * @param activity 当前Activity
     * @return intent
     */
    public static Intent meizu(Activity activity) {
        Intent intent = new Intent(MEIZU_INTENT);
        intent.putExtra(DEFAULT_PACKAGE, activity.getPackageName());
        intent.setComponent(new ComponentName(MEIZU_PACKAGE, MEIZU_UI_PERMISSION));
        return intent;
    }

    /**
     * 跳转到应用权限设置页面
     *
     * @param fragment  通过Fragment打开设置页面
     * @param isNewTask 是否使用新的任务栈启动
     */
    public static void openSetting(Fragment fragment, boolean isNewTask) {
        Intent intent = null;
        Activity activity = fragment.getActivity();
        if (mark().equals("huawei")) {
            intent = huawei(activity);
        } else if (mark().equals("xiaomi")) {
            intent = xiaomi(activity);
        } else if (mark().equals("oppo")) {
            intent = oppo(activity);
        } else if (mark().equals("vivo")) {
            intent = vivo(activity);
        } else if (mark().equals("meizu")) {
            intent = meizu(activity);
        } else {
            intent = normal(activity);
        }
        if (isNewTask) {
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        }
        Logger.d(TAG + "当前手机厂商: " + mark());
        fragment.startActivityForResult(intent, PermissionFragment.REQUEST_SETTING);
    }

    /**
     * @param activity 当前Activity
     * @param intent   判断的Intent
     * @return 是否存在该Intent
     */
    private static boolean isEmptyIntent(Activity activity, Intent intent) {
        return activity.getPackageManager().queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY).isEmpty();
    }
}
