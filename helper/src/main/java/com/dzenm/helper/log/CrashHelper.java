package com.dzenm.helper.log;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Looper;
import android.os.Process;
import android.widget.Toast;

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
    @SuppressLint("StaticFieldLeak")
    private static CrashHelper sCrashHelper;

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

    /**
     * 当程序中有未被捕获的异常，系统将会自动调用uncaughtException方法
     *
     * @param thread 出现未捕获异常的线程
     * @param ex     未捕获的异常
     */
    @Override
    public void uncaughtException(Thread thread, Throwable ex) {
        if (catchCaughtException(ex) && mDefaultExceptionHandle != null) {     // 如果系统提供了默认处理，则交给系统处理，否则就自己结束
            mDefaultExceptionHandle.uncaughtException(thread, ex);
        } else {
            Process.killProcess(Process.myPid());                              // 退出应用
        }
    }

    /**
     * 收集捕获的异常信息
     *
     * @param ex
     * @return
     */
    private boolean catchCaughtException(Throwable ex) {
        if (ex == null) return false;
        new Thread(new Runnable() {
            @Override
            public void run() {
                Looper.prepare();
                Toast.makeText(mContext, "程序发生崩溃，请重新打开", Toast.LENGTH_SHORT).show();
                Looper.loop();
            }
        }).start();

        String throwable = outputExceptionInfo(ex);                                // 输出异常信息
        Map<String, String> info = collectDeviceInfo();                            // 收集设备参数信息
        String crashInfo = collectCrashInfo(throwable, info);                      // 收集崩溃日志的信息

        String fileName = NAME + DateHelper.getCurrentTimeMillis() + SUFFIX;
        FileHelper.getInstance().newFile(getCrashDirect().getPath(), fileName, crashInfo);  // 保存当前文件
        FileHelper.getInstance().delete(getCrashDirect(), fileName);                        // 删除其它文件
        return true;
    }

    /**
     * 输出异常信息
     *
     * @param ex
     * @return
     */
    private String outputExceptionInfo(Throwable ex) {
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
    private Map<String, String> collectDeviceInfo() {
        Logger.e(TAG + "开始收集设备参数信息");
        Map<String, String> info = new HashMap<String, String>();                      // 用来存储设备信息
        try {
            // 获取设备硬件信息
            Field[] fields = Build.class.getDeclaredFields();
            for (Field field : fields) { // 迭代Build的字段key-value 此处的信息主要是为了在服务器端手机各种版本手机报错的原因
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

        } catch (PackageManager.NameNotFoundException e) {
            Logger.e(TAG + "收集设备参数信息失败: " + e);
        } catch (IllegalAccessException e) {
            Logger.e(TAG + "收集设备参数信息失败: " + e);
        }
        Logger.e(TAG + "收集设备参数信息完成");
        return info;
    }

    /**
     * 收集所需信息
     *
     * @param throwable
     * @param info
     * @return
     */
    private String collectCrashInfo(String throwable, Map<String, String> info) {
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
}