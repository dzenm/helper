package com.dzenm.helper.download;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.os.Message;
import android.provider.Settings;
import android.text.TextUtils;

import com.dzenm.helper.file.FileHelper;
import com.dzenm.helper.file.FileType;
import com.dzenm.helper.file.SPHelper;
import com.dzenm.helper.log.Logger;
import com.dzenm.helper.os.OsHelper;
import com.dzenm.helper.task.WeakHandler;
import com.dzenm.helper.toast.ToastHelper;

import java.io.File;

/**
 * @author dinzhenyan
 * @date 2019-07-01 14:46
 *
 * <pre>
 * DownloadHelper mDownloadHelper = new DownloadHelper(activity);
 * DownloadHelper.newInstance(this)
 *        .setUrl(url)
 *        .setFilePath(Environment.getExternalStorageDirectory().getPath())
 *        .startDownload();
 * 需要添加网络权限和存储权限
 * <uses-permission android:name="android.permission.INTERNET" />
 * <uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE" />
 * 针对Android 8.0还需要添加 "安装其他应用" 权限
 * <uses-permission android:name="android.permission.REQUEST_INSTALL_PACKAGES"/>
 * </pre>
 */
public class DownloadHelper {

    private static final String TAG = DownloadHelper.class.getSimpleName() + "|";

    private static final long DOWNLOAD_DEFAULT_ID = 0L;
    private static final long DOWNLOAD_ERROR_ID = -1L;
    private static final int DOWNLOAD_PROGRESS = 1001;
    private static final int DOWNLOAD_FAILED = 1002;

    /**
     * 保存Download ID的shared_prefs文件名称
     */
    private static final String DOWNLOAD_PREF = "download_helper_pref";

    private Context mContext;

    /**
     * 下载监听广播
     */
    private DownloadReceiver mDownloadReceiver;

    /**
     * 下载管理器
     */
    private DownloadManager mDownloadManager;

    /**
     * 下载的任务ID
     */
    private long mDownloadId;

    /**
     * 下载监听回调事件 {@link #setOnDownloadListener(OnDownloadListener)}
     * 回调的方法说明参考{@link OnDownloadListener}
     */
    private OnDownloadListener mOnDownloadListener;

    /**
     * 下载apk文件的url {@link #setUrl(String)}
     */
    private String mUrl;

    /**
     * 文件下载存储的路径 {@link #setFilePath(String)}
     * 默认存储的路径为 storage/emulated/0/{app名称}/apk/{版本号}
     */
    private String mFilePath;

    /**
     * 下载文件的名称 {@link #setFileName(String)}, 默认下载文件为APP名称
     */
    private String mFileName;

    /**
     * 下载文件的版本 {@link #setVersionName(String)}, 默认下载文件版本为1
     */
    private String mVersionName = "1";

    /**
     * 判断是否正在下载状态 {@link #isRunningDownload()}
     */
    private boolean isRunningDownload = false;

    private boolean isDefaultPath = true;

    private @NotificationType
    int mNotificationType = NotificationType.NOTIFICATION_VISIBLE_NOTIFY_COMPLETED;

    public static DownloadHelper newInstance(Context context) {
        return new DownloadHelper(context);
    }

    public DownloadHelper(Context context) {
        mContext = context;
        mDownloadManager = (DownloadManager) mContext.getSystemService(Context.DOWNLOAD_SERVICE);
        mDownloadReceiver = new DownloadReceiver();
        mFileName = OsHelper.getAppName(context) + ".apk";
        mFilePath = FileHelper.getInstance().getFolder("/apk").getPath();
    }

    /**
     * @param url 下载的url {@link #mUrl}
     * @return this
     */
    public DownloadHelper setUrl(String url) {
        mUrl = url;
        return this;
    }

    /**
     * @param filePath 下载文件的路径  {@link #mFilePath}
     * @return this
     */
    public DownloadHelper setFilePath(String filePath) {
        mFilePath = filePath;
        isDefaultPath = false;
        return this;
    }

    /**
     * @param fileName 下载文件的名称 {@link #mFileName}
     * @return this
     */
    public DownloadHelper setFileName(String fileName) {
        mFileName = fileName;
        return this;
    }

    /**
     * @param versionName 下载文件的版本名称, 下载文件过多时区分版本  {@link #mVersionName}
     * @return this
     */
    public DownloadHelper setVersionName(String versionName) {
        mVersionName = versionName;
        return this;
    }

    /**
     * @return 下载的文件的路径  {@link #mFilePath}
     */
    public String getFilePath() {
        return mFilePath + File.separator + mVersionName + File.separator + mFileName;
    }

    /**
     * @param notificationType 通知栏的显示的方式  {@link #mNotificationType}
     * @return this
     */
    public DownloadHelper setNotificationType(@NotificationType int notificationType) {
        mNotificationType = notificationType;
        return this;
    }

    /**
     * @param onDownloadListener 下载监听回调  {@link #mOnDownloadListener}
     * @return this
     */
    public DownloadHelper setOnDownloadListener(OnDownloadListener onDownloadListener) {
        mOnDownloadListener = onDownloadListener;
        return this;
    }

    /**
     * @return 是否正在下载
     */
    public boolean isRunningDownload() {
        return isRunningDownload;
    }

    /**
     * 开始下载
     */
    public void startDownload() {
        checkPermissionAndDownloadFile();
    }

    /**
     * 检测权限和是否已经下载过
     */
    private void checkPermissionAndDownloadFile() {
        if (OsHelper.isGrant(mContext, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            if (isDefaultPath) {
                mFilePath = mFilePath + File.separator + mVersionName;
            }

            String filePath = (String) SPHelper.getInstance().get(DOWNLOAD_PREF, mVersionName, "");
            if (!TextUtils.isEmpty(filePath)) {
                File file = new File(filePath);
                if (file.exists() && file.isFile()) {
                    Logger.d(TAG + "已下载过文件, 版本号: " + mVersionName + ", 文件路径" + filePath);
                    apkFileDownloadSuccessCallback(mContext, FileHelper.getInstance().getUri(file));
                } else {
                    downloadFile();
                }
            } else {
                downloadFile();
            }
        } else {
            // No permission to write to /storage/emulated/0/360/Helper.apk:
            // Neither user 10336 nor current process has android.permission.WRITE_EXTERNAL_STORAGE.
            setDownloadFailed("未开启存储权限， 请先打开存储权限");
        }
    }

    /**
     * 取消下载
     */
    public void cancel() {
        removeDownloadManager();
    }

    /**
     * 注册下载监听广播
     */
    private void registerDownloadBroadcast() {
        if (!isRunningDownload) {
            IntentFilter intentFilter = new IntentFilter();
            intentFilter.addAction(DownloadManager.ACTION_DOWNLOAD_COMPLETE);
            intentFilter.addAction(DownloadManager.ACTION_NOTIFICATION_CLICKED);
            mContext.registerReceiver(mDownloadReceiver, intentFilter);
            isRunningDownload = true;
        }
    }

    /**
     * 取消注册下载监听广播
     */
    private void unregisterDownloadBroadcas() {
        if (isRunningDownload) {
            mContext.unregisterReceiver(mDownloadReceiver);
            isRunningDownload = false;
        }
    }

    /**
     * 判断当前手机是否可以使用 DownloadManager 下载更新
     *
     * @return
     */
    private boolean isDownloadManager() {
        String packageName = "com.android.providers.downloads";
        int state = mContext.getPackageManager().getApplicationEnabledSetting(packageName);
        if (state == PackageManager.COMPONENT_ENABLED_STATE_DISABLED ||
                state == PackageManager.COMPONENT_ENABLED_STATE_DISABLED_USER ||
                state == PackageManager.COMPONENT_ENABLED_STATE_DISABLED_UNTIL_USED) {
            try {
                Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                intent.setData(Uri.parse("package:" + packageName));
                mContext.startActivity(intent);
            } catch (Exception e) {
                e.printStackTrace();
                Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                mContext.startActivity(intent);
            }
            return false;
        }
        return true;
    }

    /**
     * 下载文件
     */
    private void downloadFile() {
        registerDownloadBroadcast();
        if (isDownloadManager()) {
            // 先清空之前的下载
            if (mDownloadId != DOWNLOAD_DEFAULT_ID) removeTask(mDownloadId);
            Logger.i(TAG + "已注册下载监听广播, 开始下载...");
            mHandler.post(mRunnable);
            mDownloadId = enqueue(getRequest(mUrl));
            Logger.i(TAG + "下载任务Download ID: " + mDownloadId);
        } else {
            loadBrowserDownload();
        }
    }

    /**
     * 如果DownloadManager不可用, 调用浏览器下载
     */
    private void loadBrowserDownload() {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse(mUrl));
        mContext.startActivity(intent);
        Logger.i(TAG + "已注册下载监听广播, 调用浏览器下载...");
    }

    /**
     * @param downloadId 移除任务的任务ID
     */
    private void removeTask(Long downloadId) {
        try {
            mDownloadManager.remove(downloadId);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        }
    }

    /**
     * 执行下载任务
     *
     * @param request 下载任务的参数
     * @return 下载任务ID
     */
    private Long enqueue(DownloadManager.Request request) {
        try {
            // 获取下载任务ID
            return mDownloadManager.enqueue(request);
        } catch (Exception e) {
            e.printStackTrace();
            removeDownloadManager();
            setDownloadFailed("找不到下载文件");
            return DOWNLOAD_ERROR_ID;
        }
    }

    /**
     * 移除下载任务, 移除进度查询， 取消广播的注册
     */
    private void removeDownloadManager() {
        // 移除查询下载进度执行事件
        mHandler.removeCallbacks(mRunnable);
        unregisterDownloadBroadcas();
        Logger.i(TAG + "移除下载任务, 移除进度查询, 取消注册下载监听广播");
    }

    /**
     * @param url 下载文件的url
     * @return Request下载设置
     */
    private DownloadManager.Request getRequest(String url) {
        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));
        Logger.d(TAG + "下载文件的url:" + url);

        //设置文件的保存的位置[三种方式]
        if (TextUtils.isEmpty(mFilePath)) {
            mFilePath = Environment.DIRECTORY_DOWNLOADS;
            // 第一种 file:///storage/emulated/0/Android/data/your-package/files/Download/appName.apk
            request.setDestinationInExternalFilesDir(mContext, mFilePath, mFileName);
            // 第二种 file:///storage/emulated/0/Download/appName.apk
//            request.setDestinationInExternalPublicDir(mFilePath, mFileName)
        } else {
            //第三种 自定义文件路径
            File file = new File(mFilePath, mFileName);
            Uri uri = Uri.fromFile(file);
            // 如果使用content// 开头的Uri指定下载目标路径, 下载失败: Not a file URI: content://
            request.setDestinationUri(uri);
        }
        Logger.d(TAG + "下载文件存储目录: " + mFilePath);

        // 设置允许使用的网络类型，这里是移动网络和wifi都可以
        request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI | DownloadManager.Request.NETWORK_MOBILE);
        // 默认为下载剩余时间
//        request.setDescription("");                                                 // 通知栏描述信息
        request.setMimeType(FileType.MIME_TYPE)                                     // 设置类型为安装包
                .setTitle(mFileName)                                                // 通知栏标题, 默认值为APP名称
                .setVisibleInDownloadsUi(true)                                      // 设置可以在下载UI界面显示
                .setNotificationVisibility(mNotificationType)                       // 显示通知栏的样式
                .setAllowedOverRoaming(true)
                .allowScanningByMediaScanner();                                     // 设置为可被媒体扫描器找到
        if (mOnDownloadListener != null) mOnDownloadListener.onPrepared(request);
        return request;
    }

    /**
     * 接收传递的进度
     */
    @SuppressLint("HandlerLeak")
    private final WeakHandler mHandler = new WeakHandler(mContext) {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == DOWNLOAD_PROGRESS) {
                long[] fileSize = (long[]) msg.obj;
                int current = (int) (100 * fileSize[0] / fileSize[1]);
                if (mOnDownloadListener != null)
                    mOnDownloadListener.onProgress(fileSize[1], fileSize[0], current);
            } else if (msg.what == DOWNLOAD_FAILED) {
                removeDownloadManager();
                setDownloadFailed((String) msg.obj);
            }
        }
    };

    /**
     * 执行查询下载任务
     */
    private final Runnable mRunnable = new Runnable() {
        @Override
        public void run() {
            getDownloadManagerQueryStatus();
            mHandler.post(mRunnable);
        }
    };

    /**
     * 下载进度值，执行查询次数过多，对进度值相同，不进行消息传送
     */
    private long mDownloadValue = 0;

    /**
     * 下载的状态, 防止弹出次数过多
     */
    private int mStatus = 1001;

    /**
     * 查询下载状态
     */
    private void getDownloadManagerQueryStatus() {
        DownloadManager.Query query = new DownloadManager.Query().setFilterById(mDownloadId);
        // 通过ID向下载管理查询下载情况，返回一个cursor
        Cursor cursor = mDownloadManager.query(query);
        if (cursor == null) return;
        if (!cursor.moveToFirst()) return;

        String msg = "下载失败:";
        int status = cursor.getInt(cursor.getColumnIndexOrThrow(DownloadManager.COLUMN_STATUS));
        if (status == DownloadManager.STATUS_PENDING) {
            if (mStatus != status) {
                Logger.i(TAG + "等待下载");
                mStatus = status;
            }

        } else if (status == DownloadManager.STATUS_RUNNING) {      // 查询下载进度
            // 以下是从游标中进行信息提取
            long downloadSoFar = cursor.getLong(cursor.getColumnIndex(DownloadManager.COLUMN_BYTES_DOWNLOADED_SO_FAR));
            long downloadTotalSize = cursor.getLong(cursor.getColumnIndex(DownloadManager.COLUMN_TOTAL_SIZE_BYTES));

            if (downloadTotalSize == -1) return;
            if (mDownloadValue == downloadSoFar) return;

            Logger.i(TAG + "正在下载进度: " + downloadSoFar);
            Logger.i(TAG + "总文件大小: " + downloadTotalSize);

            Message message = Message.obtain();
            message.what = DOWNLOAD_PROGRESS;

            long[] fileSize = new long[2];
            fileSize[0] = downloadSoFar;
            fileSize[1] = downloadTotalSize;
            message.obj = fileSize;

            mHandler.sendMessage(message);
            mDownloadValue = downloadSoFar;

        } else if (status == DownloadManager.STATUS_PAUSED) {         // 查看下载暂停的原因
            if (mStatus != status) {
                mStatus = status;

                // 以下是从游标中进行信息提取
                String title = cursor.getString(cursor.getColumnIndex(DownloadManager.COLUMN_TITLE));
                int reason = cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_REASON));
                if (reason == DownloadManager.PAUSED_QUEUED_FOR_WIFI) {
                    msg = title + ": 等待连接Wi-Fi网络";
                } else if (reason == DownloadManager.PAUSED_WAITING_FOR_NETWORK) {
                    msg = title + ": 等待连接网络";
                } else if (reason == DownloadManager.PAUSED_WAITING_TO_RETRY) {
                    msg = title + ": 等待重试...";
                }
            }
            Logger.e(TAG + "下载暂停: " + msg);
            removeDownloadManager();
        } else if (status == DownloadManager.STATUS_SUCCESSFUL) {
            if (mStatus != status) {
                Logger.i(TAG + "下载成功");
                mStatus = status;
            }
        } else if (status == DownloadManager.STATUS_FAILED) {      // 查看下载错误的原因
            if (mStatus == status) return;
            mStatus = status;

            int reason = cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_REASON));
            if (reason == DownloadManager.ERROR_FILE_ERROR) {
                msg = "文件错误";
            } else if (reason == DownloadManager.ERROR_UNHANDLED_HTTP_CODE) {
                msg = "未处理的HTTP错误码";
            } else if (reason == DownloadManager.ERROR_HTTP_DATA_ERROR) {
                msg = "数据接收或处理错误";
            } else if (reason == DownloadManager.ERROR_TOO_MANY_REDIRECTS) {
                msg = "重定向错误";
            } else if (reason == DownloadManager.ERROR_INSUFFICIENT_SPACE) {
                msg = "存储空间不足";
            } else if (reason == DownloadManager.ERROR_DEVICE_NOT_FOUND) {
                msg = "设备未找到";
            } else if (reason == DownloadManager.ERROR_CANNOT_RESUME) {
                msg = "恢复下载失败";
            } else if (reason == DownloadManager.ERROR_FILE_ALREADY_EXISTS) {
                msg = "文件已存在";
            } else if (reason == DownloadManager.ERROR_UNKNOWN) {
                msg = "未知错误";
            }
            Logger.e(TAG + "下载失败: " + msg);
            Message message = Message.obtain();
            message.what = DOWNLOAD_FAILED;
            message.obj = msg;
            mHandler.sendMessage(message);
        }
        if (!cursor.isClosed()) cursor.close();
    }

    /**
     * @param msg 设置失败回调
     */
    private void setDownloadFailed(String msg) {
        if (mOnDownloadListener == null) {
            ToastHelper.show(msg);
        } else {
            mOnDownloadListener.onFailed(msg);
        }
    }

    /**
     * 下载完成通知广播
     */
    private class DownloadReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (DownloadManager.ACTION_DOWNLOAD_COMPLETE.equals(intent.getAction())) {
                long id = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, DOWNLOAD_ERROR_ID);
                if (id == mDownloadId) {
                    verifyDownloadFile(context, id);
                }
            } else if (DownloadManager.ACTION_NOTIFICATION_CLICKED.equals(intent.getAction())) {
                // 进入下载详情
                Intent intentView = new Intent(DownloadManager.ACTION_VIEW_DOWNLOADS);
                intentView.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intentView);
            }
        }
    }

    /**
     * 校验下载文件
     */
    private void verifyDownloadFile(Context context, long id) {
        DownloadManager downloadManager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);

        Uri uri = downloadManager.getUriForDownloadedFile(id);                  // 下载文件的uri
        Logger.d(TAG + "接收下载文件的ID: " + id);
        Logger.d(TAG + "接收下载文件uri: " + uri);

        if (uri != null) {
            String type = downloadManager.getMimeTypeForDownloadedFile(id);    // 下载文件的ID
            // 保存文件的ID和存储路径
            SPHelper.getInstance().put(DOWNLOAD_PREF, mFileName, id);
            SPHelper.getInstance().put(DOWNLOAD_PREF, mVersionName,
                    FileHelper.getInstance().getRealFilePath(uri));

            // 当下载文件类型为安装版类型时, 进入安装APK界面
            if (FileType.MIME_TYPE.equals(type)) {
                apkFileDownloadSuccessCallback(context, uri);
            } else {
                fileDownloadSuccessCallback(uri);
            }
        } else {
            setDownloadFailed("下载失败, 网络错误");
        }
        // 先回调， 再移除任务，否则会出错
        mDownloadId = DOWNLOAD_DEFAULT_ID;
        removeDownloadManager();
    }

    /**
     * APK文件下载成功的回调
     *
     * @param context Context, 安装APK
     * @param uri     下载文件的uri
     */
    private void apkFileDownloadSuccessCallback(Context context, Uri uri) {
        if (!OsHelper.install((Activity) context, uri)) {
            Logger.d(TAG + "安装失败");
            setDownloadFailed("安装失败");
        } else {
            Logger.d(TAG + "进入安装APK");
        }
        fileDownloadSuccessCallback(uri);
    }

    /**
     * 文件下载成功的回调
     *
     * @param uri 下载文件的uri
     */
    private void fileDownloadSuccessCallback(Uri uri) {
        if (mOnDownloadListener != null) {
            mOnDownloadListener.onSuccess(uri, FileType.MIME_TYPE);
        }
    }

    public interface OnDownloadListener {

        /**
         * 下载前的准备
         *
         * @param request 设置request
         */
        void onPrepared(DownloadManager.Request request);

        /**
         * 正在下载
         *
         * @param fileSize 文件大小
         * @param soFar    已经下载的大小
         * @param value    获取下载进度
         */
        void onProgress(long fileSize, long soFar, int value);

        /**
         * 下载成功
         *
         * @param uri      下载完成的文件uri
         * @param mimeType 文件类型
         */
        void onSuccess(Uri uri, String mimeType);

        /**
         * 下载失败
         *
         * @param msg 下载失败的错误信息
         */
        void onFailed(String msg);
    }
}
