package com.dzenm.helper.download;

import android.Manifest;
import android.app.DownloadManager;
import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;

import com.dzenm.helper.file.FileType;
import com.dzenm.helper.log.Logger;
import com.dzenm.helper.os.OsHelper;
import com.dzenm.helper.toast.ToastHelper;

import java.io.File;

/**
 * @author dzenm
 * @date 2019-09-01 10:33
 */
public class DownloadFileHelper {

    private static final String TAG = DownloadFileHelper.class.getSimpleName() + "|";
    private static final long DOWNLOAD_DEFAULT_ID = 0L;
    private static final long DOWNLOAD_ERROR_ID = -1L;

    private Context mContext;
    private DownloadReceiver mDownloadReceiver;
    private DownloadManager mDownloadManager;

    private long mDownloadId;
    private String mFilePath, mUrl;
    private boolean isShowRunningNotification = true;
    private OnDownloadListener mOnDownloadListener;

    public DownloadFileHelper(Context context) {
        mContext = context;
        mDownloadManager = (DownloadManager) mContext.getSystemService(Context.DOWNLOAD_SERVICE);
    }

    public static DownloadFileHelper newInstance(Context context) {
        return new DownloadFileHelper(context);
    }

    public DownloadFileHelper setUrl(String url) {
        mUrl = url;
        return this;
    }

    public DownloadFileHelper setFileFolder(String filePath) {
        mFilePath = filePath;
        return this;
    }

    public DownloadFileHelper setOnDownloadListener(OnDownloadListener onDownloadListener) {
        mOnDownloadListener = onDownloadListener;
        return this;
    }

    public DownloadFileHelper setShowRunningNotification(boolean showRunningNotification) {
        isShowRunningNotification = showRunningNotification;
        return this;
    }

    /**
     * 下载
     */
    public DownloadFileHelper download() {
        if (OsHelper.isGrant(mContext, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            registerDownloadBroadcast();
            if (isDownloadManager()) {
                if (mDownloadId != DOWNLOAD_DEFAULT_ID) clearCurrentTask(mDownloadId);  // 先清空之前的下载
                mDownloadId = showNotification();
            } else {
                setDownloadFailed("请开启下载管理器");
            }
        } else {
            setDownloadFailed("未开启存储权限， 请先打开存储权限");
        }
        return this;
    }

    /**
     * 注册广播
     */
    private void registerDownloadBroadcast() {
        mDownloadReceiver = new DownloadReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(DownloadManager.ACTION_DOWNLOAD_COMPLETE);
        intentFilter.addAction(DownloadManager.ACTION_NOTIFICATION_CLICKED);
        mContext.registerReceiver(mDownloadReceiver, intentFilter);
    }

    /**
     * 取消注册
     */
    private void unregisterDownloadBroadcast() {
        mContext.unregisterReceiver(mDownloadReceiver);
    }

    /**
     * @return 下载管理器是否可用
     */
    private boolean isDownloadManager() {
        String packageName = "com.android.providers.downloads";
        int state = mContext.getPackageManager().getApplicationEnabledSetting(packageName);
        if (state == PackageManager.COMPONENT_ENABLED_STATE_DISABLED
                || state == PackageManager.COMPONENT_ENABLED_STATE_DISABLED_USER
                || state == PackageManager.COMPONENT_ENABLED_STATE_DISABLED_UNTIL_USED) {
            try {
                Intent intent = new Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                intent.setData(Uri.parse("package:" + packageName));
                mContext.startActivity(intent);
            } catch (ActivityNotFoundException e) {
                e.printStackTrace();
                Intent intent = new Intent(android.provider.Settings.ACTION_MANAGE_APPLICATIONS_SETTINGS);
                mContext.startActivity(intent);
            }
            return false;
        }
        return true;
    }

    private void setDownloadFailed(String msg) {
        if (mOnDownloadListener != null) {
            mOnDownloadListener.onFailed(msg);
        } else {
            ToastHelper.show(msg);
        }
    }

    /**
     * 开始下载并设置通知显示状态
     *
     * @return 下载ID
     */
    private Long showNotification() {
        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(mUrl));
        request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI | DownloadManager.Request.NETWORK_MOBILE)   // 设置允许使用的网络类型，这里是移动网络和wifi都可以
                .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);                // 下载中和下载完后都显示通知栏

        //设置文件的保存的位置[三种方式]
        // 第一种 file:///storage/emulated/0/Android/data/your-package/files/Download/update.apk
        request.setDestinationInExternalFilesDir(mContext, Environment.DIRECTORY_DOWNLOADS, OsHelper.getAppName(mContext) + ".apk");
        //第二种 file:///storage/emulated/0/Download/update.apk
//        req.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, "update.apk")
        //第三种 自定义文件路径
        String appName = OsHelper.getAppName(mContext) + ".apk";
        File file = new File(mFilePath, appName);
        Logger.d(TAG + "下载文件存储的目录:" + file.getAbsolutePath());
        Uri uri = Uri.fromFile(file);
        Logger.d(TAG + "下载文件的uri:" + uri);
        // 如果使用content// 开头的Uri指定下载目标路径, 下载失败: Not a file URI: content://
        request.setDestinationUri(uri);
        //禁止发出通知，即后台下载
        request.setShowRunningNotification(isShowRunningNotification);
        request.setTitle(appName)                       // 通知栏标题
//                .setDescription(desc)                   // 通知栏描述信息
                .setMimeType(FileType.MIME_TYPE)        // 设置类型为.apk
                .allowScanningByMediaScanner();         // 设置为可被媒体扫描器找到
        // 设置为可见和可管理
        request.setVisibleInDownloadsUi(true);
        //获取下载任务ID
        try {
            return mDownloadManager.enqueue(request);
        } catch (Exception e) {
            e.printStackTrace();
            setDownloadFailed("找不到下载文件");
            return DOWNLOAD_ERROR_ID;
        }
    }

    /**
     * 下载前先移除前一个任务，防止重复下载
     *
     * @param downloadId 下载ID
     */
    private void clearCurrentTask(Long downloadId) {
        try {
            mDownloadManager.remove(downloadId);
        } catch (IllegalArgumentException ex) {
            ex.printStackTrace();
        }
    }

    private class DownloadReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(DownloadManager.ACTION_DOWNLOAD_COMPLETE)) {
                long id = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, DOWNLOAD_ERROR_ID);
                if (mDownloadId == id) {
                    installApk(context, id);
                }
            } else if (intent.getAction().equals(DownloadManager.ACTION_NOTIFICATION_CLICKED)) {
                Intent viewDownloadIntent = new Intent(DownloadManager.ACTION_VIEW_DOWNLOADS);
                viewDownloadIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(viewDownloadIntent);
            }
        }

        private void installApk(Context context, long downloadApkId) {
            DownloadManager manager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
            Uri downloadFileUri = manager.getUriForDownloadedFile(downloadApkId);
            if (downloadFileUri != null) {
                if (mOnDownloadListener != null) {
                    mOnDownloadListener.onSuccess(downloadFileUri);
                } else {
                    Intent install = new Intent(Intent.ACTION_VIEW);
                    install.setDataAndType(downloadFileUri, FileType.MIME_TYPE);
                    if ((Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)) {             // 判读版本是否在7.0以上
                        install.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);        // 表示对目标应用临时授权该Uri所代表的文件
                    }
                    install.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    if (install.resolveActivity(context.getPackageManager()) != null) {
                        context.startActivity(install);
                    } else {
                        setDownloadFailed("自动安装失败，请手动安装");
                    }
                }
            } else {
                setDownloadFailed("下载失败");
            }
            mDownloadId = DOWNLOAD_DEFAULT_ID;
            unregisterDownloadBroadcast();
        }
    }

    public interface OnDownloadListener {

        void onSuccess(Uri uri);

        void onFailed(String msg);
    }
}