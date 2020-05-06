package com.dzenm.lib.log;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Looper;

import androidx.annotation.NonNull;

import com.dzenm.lib.file.FileHelper;
import com.dzenm.lib.task.WeakHandler;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author dzenm
 * @date 2020-02-13 15:22
 */
public class CrashHelper implements Thread.UncaughtExceptionHandler {

    private static final String TAG = CrashHelper.class.getSimpleName() + "| ";
    private static final String FILE_NAME = "crash";    // log文件名
    private static final String SUFFIX = ".txt";        // log文件的后缀名
    private static final String PATH = "/crash";        // log文件文件夹

    private static CrashHelper mCrashHelper;
    private Context mContext;
    // 系统默认异常处理器, （默认情况下，系统会终止当前的异常程序）
    private Thread.UncaughtExceptionHandler mDefaultCrashHandler;

    private CaughtExceptionHandler mCaughtExceptionHandler;
    private OnCaughtExceptionMessageListener mOnCaughtExceptionMessageListener;     // 自定义处理异常信息
    private boolean isCache = true;                                                 // 是否保存为本地文件

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

    /**
     * 初始化异常捕获(默认全局捕获异常)
     *
     * @param context 上下文
     * @return this
     */
    public CrashHelper init(Context context) {
        init(context, true);
        return this;
    }

    /**
     * 初始化异常捕获
     *
     * @param context     上下文
     * @param globalCatch 是否全局捕获异常, 不退出程序
     * @return this
     */
    public CrashHelper init(Context context, boolean globalCatch) {
        if (globalCatch) interceptException();
        // 获取系统默认的异常处理器
        mDefaultCrashHandler = Thread.getDefaultUncaughtExceptionHandler();
        // 将当前实例设为系统默认的异常处理器
        Thread.setDefaultUncaughtExceptionHandler(this);
        mContext = context.getApplicationContext();
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
     * 这个是最关键的函数，当程序中有未被捕获的异常，系统将会自动调用 {@link #uncaughtException} 方法
     * thread为出现未捕获异常的线程，ex为未捕获的异常，有了这个ex，我们就可以得到异常信息。
     */
    @Override
    public void uncaughtException(@NonNull Thread t, @NonNull Throwable ex) {
        // 拦截子线程的异常
        if (handlerException(t, ex) && mDefaultCrashHandler != null) {
            // 如果系统提供了默认的异常处理器，则交给系统去结束我们的程序，否则就由我们自己结束自己
            mDefaultCrashHandler.uncaughtException(t, ex);
        } else {
            // 处理异常信息
            mCaughtExceptionHandler.caughtException(t, ex);
        }
    }

    /**
     * 拦截异常处理
     */
    private void interceptException() {
        // 主线程异常拦截
        final Looper looper = Looper.getMainLooper();
        new WeakHandler(looper).post(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    try {
                        Looper.loop();
                    } catch (Exception e) {
                        handlerException(looper.getThread(), e);
                    }
                }
            }
        });
    }

    private boolean handlerException(Thread t, Throwable ex) {
        if (ex == null) return false;
        try {
            // 导出异常信息到SD卡中
            String errorMessage = dumpExceptionToSDCard(t, ex);

            if (isCache) {
                FileHelper fileHelper = FileHelper.getInstance();
                // 获取当前时间以创建log文件
                long current = System.currentTimeMillis();
                String time = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss:SSS").format(new Date(current));
                // log文件名
                String fileName = FILE_NAME + "_" + time + SUFFIX;
                // log文件夹
                File parent = getCrashDirect();
                // 保存文件
                fileHelper.newFile(parent.getPath(), fileName, errorMessage);
                // 删除其它文件
                fileHelper.delete(parent, fileName);
            }
            // 这里可以通过网络上传异常信息到服务器，便于开发人员分析日志从而解决bug
            if (mOnCaughtExceptionMessageListener != null) {
                mOnCaughtExceptionMessageListener.onHandlerMessage(errorMessage);
            }
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    /**
     * 收集捕获的异常信息
     *
     * @param ex 异常信息
     */
    private String dumpExceptionToSDCard(Thread t, Throwable ex) throws IOException {
        StringBuffer sb = new StringBuffer();
        Logger.e(TAG + "开始输出异常信息");
        sb.append("-------- 开始收集设备信息 --------\n");
        // 导出手机信息
        dumpPhoneInfo(sb);
        sb.append("\n-------- 设备信息收集完成 --------\n\n");

        sb.append("-------- 开始收集异常信息 --------\n");
        // 导出异常的调用栈信息
        dumpExceptionInfo(sb, ex);
        sb.append("\n-------- 异常信息收集完成 --------");

        Logger.e(TAG + "输出异常信息完成\n");
        ex.printStackTrace();
        return sb.toString();
    }

    /**
     * 收集设备参数信息
     */
    private void dumpPhoneInfo(StringBuffer sb) {
        Logger.e(TAG + "开始收集设备参数信息");
        // 应用的版本名称和版本号
        try {
            PackageManager pm = mContext.getPackageManager();
            PackageInfo info = pm.getPackageInfo(mContext.getPackageName(), PackageManager.GET_ACTIVITIES);
            // App版本号
            sb.append("\nApp VersionName: ").append(info.versionName);
            sb.append("\nApp VersionCode: ").append(info.versionCode);
            // android版本号
            sb.append("\nOS VersionName: ").append(Build.VERSION.RELEASE);
            sb.append("\nOS VersionCode: ").append(Build.VERSION.SDK_INT);
            //手机制造商
            sb.append("\nVendor: ").append(Build.MANUFACTURER);
            //手机型号
            sb.append("\nModel: ").append(Build.MODEL);
            //cpu架构
            sb.append("\nCPU ABI: ").append(Build.CPU_ABI);
            // 获取设备硬件信息
            Field[] fields = Build.class.getDeclaredFields();
            // 迭代Build的字段key-value 此处的信息主要是为了在服务器端手机各种版本手机报错的原因
            for (Field field : fields) {
                field.setAccessible(true);
                sb.append("\n").append(field.getName()).append(": ").append(field.get("").toString());
            }
        } catch (PackageManager.NameNotFoundException | IllegalAccessException e) {
            e.printStackTrace();
            Logger.e(TAG + "收集设备参数信息失败: " + e);
        }
        Logger.e(TAG + "收集设备参数信息完成");
    }

    /**
     * 收集异常信息
     */
    private void dumpExceptionInfo(StringBuffer sb, Throwable e) throws IOException {
        Writer writer = new StringWriter();
        PrintWriter printWriter = new PrintWriter(writer);
        e.printStackTrace(printWriter);
        Throwable throwable = e.getCause();
        while (throwable != null) {
            throwable.printStackTrace(printWriter);
            printWriter.append("\r\n");                                     // 换行 每个个异常栈之间换行
            throwable = throwable.getCause();
        }
        printWriter.close();
        sb.append(writer.toString());
        writer.close();
    }

    /**
     * @return 崩溃日志文件夹
     */
    private File getCrashDirect() {
        return FileHelper.getInstance().getFile(PATH);
    }

    public interface CaughtExceptionHandler {
        void caughtException(Thread t, Throwable e);
    }

    public interface OnCaughtExceptionMessageListener {
        void onHandlerMessage(String message);
    }

}
