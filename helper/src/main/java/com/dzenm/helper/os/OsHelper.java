package com.dzenm.helper.os;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Binder;
import android.os.Build;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.TypedValue;

import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.PermissionChecker;

import com.dzenm.helper.file.FileType;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author dinzhenyan
 * @date 2019-04-28 21:46
 */
public final class OsHelper {

    private static final String KEY_VERSION_MIUI = "ro.miui.ui.version.name";
    private static final String KEY_VERSION_EMUI = "ro.build.version.emui";
    private static final String KEY_VERSION_OPPO = "ro.build.version.opporom";
    private static final String KEY_VERSION_SMARTISAN = "ro.smartisan.version";
    private static final String KEY_VERSION_VIVO = "ro.vivo.os.version";

    private static String sName;
    private static String sVersion;

    public static boolean isEmui() {
        return check(Rom.EMUI);
    }

    public static boolean isMiui() {
        return check(Rom.MIUI);
    }

    public static boolean isVivo() {
        return check(Rom.VIVO);
    }

    public static boolean isOppo() {
        return check(Rom.OPPO);
    }

    public static boolean isFlyme() {
        return check(Rom.FLYME);
    }

    public static boolean is360() {
        return check(Rom.QIKU) || check(Rom.THREE_NINE_ZERO);
    }

    public static boolean isSmartisan() {
        return check(Rom.SMARTISAN);
    }

    /**
     * @return 获取系统名称
     */
    public static String getName() {
        if (sName == null) check(Rom.NONE);
        return sName;
    }

    /**
     * @return 获取系统版本
     */
    public static String getVersion() {
        if (sVersion == null) check(Rom.NONE);
        return sVersion;
    }

    /**
     * @param rom 查询的Rom
     * @return Rom信息
     */
    public static boolean check(@Rom String rom) {
        if (sName != null) {
            return sName.equals(rom);
        }
        if (!TextUtils.isEmpty(sVersion = getProp(KEY_VERSION_MIUI))) {
            sName = Rom.MIUI;
        } else if (!TextUtils.isEmpty(sVersion = getProp(KEY_VERSION_EMUI))) {
            sName = Rom.EMUI;
        } else if (!TextUtils.isEmpty(sVersion = getProp(KEY_VERSION_OPPO))) {
            sName = Rom.OPPO;
        } else if (!TextUtils.isEmpty(sVersion = getProp(KEY_VERSION_VIVO))) {
            sName = Rom.VIVO;
        } else if (!TextUtils.isEmpty(sVersion = getProp(KEY_VERSION_SMARTISAN))) {
            sName = Rom.SMARTISAN;
        } else {
            sVersion = Build.DISPLAY;
            if (sVersion.toUpperCase().contains(Rom.FLYME)) {
                sName = Rom.FLYME;
            } else {
                sVersion = Build.UNKNOWN;
                sName = Build.MANUFACTURER.toUpperCase();
            }
        }
        return sName.equals(rom);
    }

    /**
     * @param name 系统Prop名称
     * @return 系统Prop
     */
    public static String getProp(String name) {
        String line = null;
        BufferedReader input = null;
        try {
            Process p = Runtime.getRuntime().exec("getprop " + name);
            input = new BufferedReader(new InputStreamReader(p.getInputStream()), 1024);
            line = input.readLine();
            input.close();
        } catch (IOException ex) {
            return null;
        } finally {
            if (input != null) {
                try {
                    input.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return line;
    }

    /**
     * @param context 上下文
     * @return 当前应用程序的名称
     */
    public static String getAppName(Context context) {
        try {
            PackageInfo packageInfo = context.getPackageManager()
                    .getPackageInfo(context.getPackageName(), 0);
            int labelRes = packageInfo.applicationInfo.labelRes;
            return context.getResources().getString(labelRes);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * @param context 上下文
     * @return 当前应用程序的版本名称信息
     */
    public static String getVersionName(Context context) {
        try {
            PackageInfo packageInfo = context.getPackageManager()
                    .getPackageInfo(context.getPackageName(), 0);
            return packageInfo.versionName;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * @param context 上下文
     * @return 当前应用程序的版本号
     */
    public static synchronized long getVersionCode(Context context) {
        try {
            PackageInfo packageInfo = context.getPackageManager()
                    .getPackageInfo(context.getPackageName(), 0);
            return packageInfo.versionCode;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    /**
     * @param context 上下文
     * @return 当前应用的图标
     */
    public static Bitmap getIcon(Context context) {
        PackageManager packageManager = null;
        ApplicationInfo applicationInfo;
        try {
            packageManager = context.getApplicationContext().getPackageManager();
            applicationInfo = packageManager.getApplicationInfo(context.getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e) {
            applicationInfo = null;
            e.printStackTrace();
        }
        Drawable drawable = packageManager.getApplicationIcon(applicationInfo); // [表情]x根据自己的情况获取drawable
        BitmapDrawable bitmapDrawable = (BitmapDrawable) drawable;
        return bitmapDrawable.getBitmap();
    }

    /**
     * @param activity 当前Activity
     * @param uri      APK文件的路径
     */
    public static boolean install(Activity activity, Uri uri) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setDataAndType(uri, FileType.MIME_TYPE);
        if (isNougat()) {
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);        // 表示对目标应用临时授权该Uri所代表的文件}
        }
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        if (intent.resolveActivity(activity.getPackageManager()) != null) {
            activity.startActivity(intent);
            return true;
        }
        return false;
    }

    /**
     * @param context 上下文
     * @return 当前 target sdk 版本 是否大于 23
     */
    public static boolean getTargetSdkVersion(Context context) {
        return context.getApplicationInfo().targetSdkVersion > Build.VERSION_CODES.M;
    }

    /**
     * @param context 上下文
     * @return 应用程序在 AndroidManifest 文件中注册的权限
     */
    public static List<String> getManifestPermissions(Context context) {
        try {
            return Arrays.asList(context.getPackageManager().getPackageInfo(context.getPackageName(),
                    PackageManager.GET_PERMISSIONS).requestedPermissions);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 判断权限是否在 AndroidManifest 文件中注册
     *
     * @param context    上下文
     * @param permission 需要判断的权限
     * @return 是否注册
     */
    public static boolean isExistInManifest(Context context, String permission) {
        List<String> manifestPermissions = getManifestPermissions(context);
        if (manifestPermissions == null && manifestPermissions.isEmpty()) {
            throw new NullPointerException("AndroidManifest permission is null");
        }
        if (manifestPermissions.equals(permission)) {
            return true;
        } else {
            throw new NullPointerException("AndroidManifest's permission is not found");
        }
    }

    /**
     * 判断单个权限是否授予
     * targetSdkVersion<23时 即便运行在android6及以上设备
     * ContextWrapper.checkSelfPermission和Context.checkSelfPermission失效
     * 返回值始终为PERMISSION_GRANTED,此时必须使用PermissionChecker.checkSelfPermission
     *
     * @param context    上下文
     * @param permission 判断的权限
     * @return 单个权限是否授予
     */
    @SuppressLint("WrongConstant")
    public static boolean isGrant(Context context, String permission) {
        if (getTargetSdkVersion(context)) {
            return PermissionChecker.checkPermission(context, permission, Binder.getCallingPid(),
                    Binder.getCallingUid(), context.getPackageName()) == PackageManager.PERMISSION_GRANTED;
        }
        return ActivityCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED;
    }

    /**
     * @param activity   当前Activity
     * @param permission 判断的权限
     * @return 是否显示解释权限
     */
    public static boolean isRationale(Activity activity, String permission) {
        return ActivityCompat.shouldShowRequestPermissionRationale(activity, permission);
    }

    /**
     * @param activity    当前Activity
     * @param permissions 判断的权限
     * @return 是否存在未授予的权限
     */
    public static boolean isRationaleAll(Activity activity, String[] permissions) {
        for (String permission : permissions) {
            if (!isRationale(activity, permission)) return false;
        }
        return true;
    }

    /**
     * 过滤未授予的权限
     *
     * @param activity    当前Activity
     * @param permissions 需要过滤的权限
     * @return 过滤后的权限
     */
    public static String[] filterPermissions(Activity activity, String[] permissions) {
        if (permissions == null || permissions.length == 0) return null;
        List<String> filterPermits = new ArrayList<>();
        for (String permission : permissions) {
            // 检查是否授予权限, 将未授予的权限将筛选出来
            if (!isGrant(activity, permission)) filterPermits.add(permission);
        }
        String[] res = new String[filterPermits.size()];
        return filterPermits.toArray(res);
    }

    /**
     * @param context 上下文
     * @return 是否有安装权限
     */
    public static boolean isInstallPermission(Context context) {
        if (isOreo()) return context.getPackageManager().canRequestPackageInstalls();
        return true;
    }

    /**
     * @param context 上下文
     * @return 是否有悬浮窗权限
     */
    public static boolean isOverlaysPermission(Context context) {
        if (isMarshmallow()) return Settings.canDrawOverlays(context);
        return true;
    }

    /**
     * 判断当前应用是否允许通知（消息推送）
     * areNotificationsEnabled 只对 API 19 及以上版本有效，低于API 19 会一直返回true
     *
     * @param context 上下文
     * @return 当前应用是否允许通知（消息推送）
     */
    public static boolean isNotificationEnabled(Context context) {
        boolean isEnabled;
        try {
            isEnabled = NotificationManagerCompat.from(context).areNotificationsEnabled();
        } catch (Exception e) {
            isEnabled = false;
            e.printStackTrace();
        }
        return isEnabled;
    }

    /**
     * 打开通知（消息推送）管理设置页面
     *
     * @param context 上下文
     */
    public static void openNotificationSetting(Context context) {
        Intent intent = new Intent();
        try {
            intent.setAction(Settings.ACTION_APP_NOTIFICATION_SETTINGS);
            if (isOreo()) {                 // 这种方案适用于 API 26, 即8.0（含8.0）以上可以用
                intent.putExtra(Settings.EXTRA_APP_PACKAGE, context.getPackageName());
                intent.putExtra(Settings.EXTRA_CHANNEL_ID, context.getApplicationInfo().uid);
            } else if (isLollipop()) {      // 这种方案适用于 API21—25，即 5.0—7.1 之间的版本可以使用
                intent.putExtra("app_package", context.getPackageName());
                intent.putExtra("app_uid", context.getApplicationInfo().uid);
            } else {
                intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                intent.setData(Uri.fromParts("package", context.getPackageName(), null));
            }
        } catch (Exception e) {
            // 出现异常则跳转到应用设置界面：锤子坚果3——OC105 API25
            intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
            intent.setData(Uri.fromParts("package", context.getPackageName(), null));
        }
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    /**
     * 判断当前系统版本是否大于Android 4.4(target 19)
     *
     * @return 是否大于Android 4.4
     */
    public static boolean isKitkat() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;
    }

    /**
     * 判断当前系统版本是否大于Android 5.0(target 21)
     *
     * @return 是否大于Android 5.0
     */
    public static boolean isLollipop() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP;
    }

    /**
     * 判断当前系统版本是否大于Android 6.0(target 23)
     *
     * @return 是否大于Android 6.0
     */
    public static boolean isMarshmallow() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.M;
    }

    /**
     * 判断当前系统版本是否大于Android 7.0(target 24)
     *
     * @return 是否大于Android 7.0
     */
    public static boolean isNougat() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.N;
    }

    /**
     * 判断当前系统版本是否大于Android 8.0(target 26)
     *
     * @return 是否大于Android 8.0
     */
    public static boolean isOreo() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.O;
    }

    /**
     * @param value 转换的值
     * @return 转换的dip值
     */
    public static int dp2px(float value) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, value, Resources.getSystem().getDisplayMetrics());
    }
}