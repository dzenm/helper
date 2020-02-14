package com.dzenm.helper.log;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Looper;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.dzenm.helper.date.DateHelper;
import com.dzenm.helper.file.FileHelper;
import com.dzenm.helper.task.WeakHandler;

import java.io.File;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

/**
 * @author dzenm
 * @date 2020-02-13 15:22
 */
public class CrashHelper {

    private static final String TAG = CrashHelper.class.getSimpleName() + "|";
    private static final String PATH = "/crash";
    private static final String NAME = "crash_";
    private static final String SUFFIX = ".txt";

    private static CrashHelper mCrashHelper;
    private CaughtExceptionHandler mCaughtExceptionHandler;

    private Context mContext;
    private OnCaughtExceptionMessageListener mOnCaughtExceptionMessageListener;     // 自定义处理异常信息
    private boolean isCache = true;                                             // 是否保存为本地文件

    private CrashHelper() {
    }

    public static CrashHelper getInstance() {
        if (mCrashHelper == null) {
            synchronized (CrashHelper.class) {
                if (mCrashHelper == null) {
                    mCrashHelper = new CrashHelper();
                }
            }
        }
        return mCrashHelper;
    }

    public CrashHelper init(Context context) {
        mContext = context;
        interceptExceptionHandler();
        return this;
    }

    public CrashHelper setCache(boolean cache) {
        isCache = cache;
        return this;
    }

    public CrashHelper setOnCaughtExceptionMessageListener(OnCaughtExceptionMessageListener listener) {
        mOnCaughtExceptionMessageListener = listener;
        return this;
    }

    public CrashHelper setCaughtExceptionHandler(CaughtExceptionHandler caughtExceptionHandler) {
        mCaughtExceptionHandler = caughtExceptionHandler;
        return this;
    }

    /**
     * 拦截异常处理
     */
    private void interceptExceptionHandler() {
        // 主线程异常拦截
        final Looper looper = Looper.getMainLooper();
        new WeakHandler(looper).post(new Runnable() {
            @Override
            public void run() {
                for (; ; ) {
                    try {
                        Looper.loop();
                    } catch (Throwable e) {
                        catchException(looper.getThread(), e);
                    }
                }
            }
        });

        // 所有线程异常拦截，由于主线程的异常都被我们catch了，所以下面拦截的都是子线程的异常
        Thread.setDefaultUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
            @Override
            public void uncaughtException(@NonNull Thread t, @NonNull Throwable e) {
                catchException(t, e);
            }
        });
    }

    /**
     * 收集捕获的异常信息
     *
     * @param e 异常信息
     */
    private void catchException(Thread t, Throwable e) {
        if (e == null) return;

        String exception = printExceptionMessage(e);                               // 输出异常信息
        Map<String, String> phoneMessage = printPhoneMessage();                    // 收集设备参数信息
        String crashMessage = printSystemMessage(exception, phoneMessage);         // 收集崩溃日志的信息

        handlerExceptionMessage(crashMessage);
//        Process.killProcess(Process.myPid());                                      // 退出应用
        if (mCaughtExceptionHandler != null) {                                 // 处理异常
            mCaughtExceptionHandler.caughtException(t, e);
        } else {
            Toast.makeText(mContext, e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void handlerExceptionMessage(String crashMessage) {
        if (isCache) {
            String fileName = NAME + DateHelper.getCurrentTimeMillis() + SUFFIX;
            // 保存当前文件
            FileHelper.getInstance().newFile(getCrashDirect().getPath(), fileName, crashMessage);
            // 删除其它文件
            FileHelper.getInstance().delete(getCrashDirect(), fileName);
        }
        if (mOnCaughtExceptionMessageListener != null) {
            // 上传到服务器
            mOnCaughtExceptionMessageListener.onHandlerMessage(crashMessage);
        }
    }

    /**
     * 输出异常信息
     *
     * @param ex 异常信息
     * @return 格式化信息
     */
    private String printExceptionMessage(Throwable ex) {
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


    public interface CaughtExceptionHandler {
        void caughtException(Thread t, Throwable e);
    }

    public interface OnCaughtExceptionMessageListener {
        void onHandlerMessage(String message);
    }

}
