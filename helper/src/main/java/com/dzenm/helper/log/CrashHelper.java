package com.dzenm.helper.log;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Looper;
import android.os.Process;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.dzenm.helper.date.DateHelper;
import com.dzenm.helper.file.FileHelper;

import java.io.File;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

/**
 * @author dinzhenyan
 * @date 2019-04-30 20:03
 * <p>
 * 异常日志收集的处理
 * 在Application中初始化
 * <pre>
 *     CrashHelper.getInstance()
 *              .init()
 * </pre>
 */
public class CrashHelper implements Thread.UncaughtExceptionHandler {

    private static final String TAG = CrashHelper.class.getSimpleName() + "|";
    private static final String PATH = "/crash";
    private static final String NAME = "crash_";
    private static final String SUFFIX = ".txt";

    private Context mContext;
    private Thread.UncaughtExceptionHandler mDefaultExceptionHandle;            // 系统默认的UncaughtException处理类
    private static CrashHelper sCrashHelper;
    private OnCrashExceptionMessageListener mOnCrashExceptionMessageListener;   // 自定义处理异常信息
    private boolean isCache = true;                                             // 是否保存为本地文件

    private CrashHelper() {
    }

    public static CrashHelper getInstance() {
        if (sCrashHelper == null) synchronized (CrashHelper.class) {
            if (sCrashHelper == null) sCrashHelper = new CrashHelper();
        }
        return sCrashHelper;
    }

    public CrashHelper init(Context context) {
        mContext = context.getApplicationContext();
        mDefaultExceptionHandle = Thread.getDefaultUncaughtExceptionHandler();  // 获取系统默认的UncaughtException处理器
        Thread.setDefaultUncaughtExceptionHandler(this);                        // 设置CrashHandler为程序的默认异常处理器
        return this;
    }

    public CrashHelper setCache(boolean cache) {
        isCache = cache;
        return this;
    }

    public CrashHelper setOnCrashExceptionMessageListener(OnCrashExceptionMessageListener listener) {
        mOnCrashExceptionMessageListener = listener;
        return this;
    }

    /**
     * 当程序中有未被捕获的异常，系统将会自动调用uncaughtException方法
     *
     * @param thread 出现未捕获异常的线程
     * @param ex     未捕获的异常
     */
    @Override
    public void uncaughtException(@NonNull Thread thread, @NonNull Throwable ex) {
        if (catchCaughtException(ex) && mDefaultExceptionHandle != null) {     // 如果系统提供了默认处理，则交给系统处理，否则就自己结束
            mDefaultExceptionHandle.uncaughtException(thread, ex);
        } else {
            Process.killProcess(Process.myPid());                              // 退出应用
        }
    }

    /**
     * 收集捕获的异常信息
     *
     * @param ex 异常信息
     * @return 是否捕捉异常
     */
    private boolean catchCaughtException(Throwable ex) {
        if (ex == null) return false;

        String exception = printCrashExceptionMessage(ex);                         // 输出异常信息
        Map<String, String> phoneMessage = printPhoneMessage();                    // 收集设备参数信息
        String crashMessage = printSystemMessage(exception, phoneMessage);       // 收集崩溃日志的信息

        handlerCrashExceptionMessage(crashMessage);
        Process.killProcess(Process.myPid());                                      // 退出应用
        return true;
    }

    private void handlerCrashExceptionMessage(String crashMessage) {
        if (isCache) {
            String fileName = NAME + DateHelper.getCurrentTimeMillis() + SUFFIX;
            // 保存当前文件
            FileHelper.getInstance().newFile(getCrashDirect().getPath(), fileName, crashMessage);
            // 删除其它文件
            FileHelper.getInstance().delete(getCrashDirect(), fileName);
        }
        if (mOnCrashExceptionMessageListener != null) {
            // 上传到服务器
            mOnCrashExceptionMessageListener.onHandlerMessage(mContext, crashMessage);
        }
    }

    /**
     * 输出异常信息
     *
     * @param ex 异常信息
     * @return 格式化信息
     */
    private String printCrashExceptionMessage(Throwable ex) {
        Logger.e(TAG + "开始输出异常信息");
        Writer writer = new StringWriter();
        PrintWriter printWriter = new PrintWriter(writer);
        ex.printStackTrace(printWriter);
        Throwable throwable = ex.getCause();

        while (throwable != null) {
            throwable.printStackTrace(printWriter);
            printWriter.append("\r\n");                                     // 换行 每个个异常栈之间换行
            throwable = throwable.getCause();
        }
        printWriter.close();
        Logger.e(TAG + "输出异常信息完成");
        return writer.toString();
    }

    /**
     * 收集设备参数信息
     */
    private Map<String, String> printPhoneMessage() {
        Logger.e(TAG + "开始收集设备参数信息");
        Map<String, String> info = new HashMap<String, String>();                      // 用来存储设备信息
        try {
            // 获取设备硬件信息
            Field[] fields = Build.class.getDeclaredFields();
            // 迭代Build的字段key-value 此处的信息主要是为了在服务器端手机各种版本手机报错的原因
            for (Field field : fields) {
                field.setAccessible(true);
                info.put(field.getName(), field.get("").toString());
            }

            // 获取应用包参数信息
            PackageInfo packageInfo = mContext.getPackageManager().getPackageInfo(
                    mContext.getPackageName(), PackageManager.GET_ACTIVITIES);
            if (packageInfo != null) {
                info.put("安卓版本号", Build.VERSION.RELEASE);         // Android版本号
                info.put("Target版本", Build.VERSION.SDK_INT + "");
                info.put("App版本名称", packageInfo.versionName);      // App版本名称
                info.put("App版本号", packageInfo.versionCode + "");   // App版本号
            }

        } catch (PackageManager.NameNotFoundException | IllegalAccessException e) {
            Logger.e(TAG + "收集设备参数信息失败: " + e);
        }
        Logger.e(TAG + "收集设备参数信息完成");
        return info;
    }

    /**
     * 收集所需信息
     *
     * @param throwable 异常信息
     * @param info      设备信息
     * @return 汇总信息
     */
    private String printSystemMessage(String throwable, Map<String, String> info) {
        StringBuilder stringBuffer = new StringBuilder();                      // 输出手机、系统、软件信息
        stringBuffer.append("-------- 开始收集设备信息 --------\n");

        for (Map.Entry<String, String> entry : info.entrySet()) {
            stringBuffer.append(entry.getKey())
                    .append(" ------ ")
                    .append(entry.getValue())
                    .append("\n");
        }
        stringBuffer.append("-------- 设备信息收集完成 --------\n\n\n");

        stringBuffer.append("-------- 开始收集异常信息 --------\n");
        stringBuffer.append(throwable)
                .append("\n");
        stringBuffer.append("-------- 异常信息收集完成 --------");
        return stringBuffer.toString();
    }

    /**
     * @return 崩溃日志文件夹
     */
    private File getCrashDirect() {
        return FileHelper.getInstance().getFolder(PATH);
    }

    public interface OnCrashExceptionMessageListener {
        void onHandlerMessage(Context context, String message);
    }
}