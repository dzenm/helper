package com.dzenm.helper.log;

import android.content.Context;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;

import androidx.annotation.IntDef;

import com.dzenm.helper.date.DateHelper;
import com.dzenm.helper.file.FileHelper;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.Arrays;

/**
 * @author dinzhenyan
 * @date 2019-04-30 20:03
 * <p>
 * 打印日志以及log日志统计保存
 */
public class Logger {

    private static final String TAG = Logger.class.getSimpleName() + "|";

    private static final String SUFFIX = ".txt";            // 日志文件后缀

    private static String logcatPath;                       // log文件路径
    private LogDumper mLogDumper;                           // log输出文件线程
    private int mPID;                                       // 进程的pid
    private static String mTag = "DZY";                     // 日志TAG

    public static final int LEBEL = 0;
    public static final int VERBOSE = 1;
    public static final int DEBUG = 2;
    public static final int INFO = 3;
    public static final int WARN = 4;
    public static final int ERROR = 5;
    public static final int WTF = 6;
    public static final int ONTHING = 7;

    @IntDef({LEBEL, VERBOSE, DEBUG, INFO, WARN, ERROR, WTF, ONTHING})
    @Retention(RetentionPolicy.SOURCE)
    public @interface Level {
    }

    private @Level
    static int sLevel = VERBOSE;

    private Logger() {
        mPID = android.os.Process.myPid();
    }

    private static volatile Logger instance = null;

    public static Logger getInstance() {
        if (instance == null) synchronized (Logger.class) {
            if (instance == null) instance = new Logger();
        }
        return instance;
    }

    /**
     * 设置开启debug模式
     *
     * @return this
     */
    public Logger setDebug() {
        sLevel = LEBEL;
        return this;
    }

    public boolean isDebug() {
        return sLevel == LEBEL;
    }

    /**
     * @param level 设置Log日志显示的级别
     * @return this
     */
    public Logger setLevel(@Level int level) {
        sLevel = level;
        return this;
    }

    /**
     * @param tag 设置Log日志的tag
     * @return this
     */
    public Logger setTag(String tag) {
        mTag = tag;
        return this;
    }

    public static void v(String msg) {
        if (sLevel <= VERBOSE) {
            Log.v(mTag, msg);
        }
    }

    public static void d(String msg) {
        if (sLevel <= DEBUG) {
            Log.d(mTag, msg);
        }
    }

    public static void i(String msg) {
        if (sLevel <= INFO) {
            Log.i(mTag, msg);
        }
    }

    public static void w(String msg) {
        if (sLevel <= WARN) {
            Log.w(mTag, msg);
        }
    }

    public static void e(String msg) {
        if (sLevel <= ERROR) {
            Log.e(mTag, msg);
        }
    }

    public static void wtf(String msg) {
        if (sLevel <= WTF) {
            Log.wtf(mTag, msg);
        }
    }

    /**
     * 初始化日志存储目录（需要先申请文件读写权限）
     */
    public Logger init(Context context) {
        logcatPath = FileHelper.getInstance().getFolder("/log").getAbsolutePath();
        if (TextUtils.isEmpty(logcatPath)) {
            logcatPath = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "log";
        }
        Logger.d(TAG + "日志存储目录: " + logcatPath);
        return this;
    }

    /**
     * 初始化完成之后才可以开始保存日志
     */
    public void start() {
        if (mLogDumper == null) {
            mLogDumper = new LogDumper(String.valueOf(mPID), logcatPath);
        }
        if (!mLogDumper.isAlive()) {
            mLogDumper.start();
        }
        Logger.i(TAG + "开始保存日志");
    }

    /**
     * 停止输出日志
     */
    public void stop() {
        if (mLogDumper != null) {
            mLogDumper.stopLogs();
            mLogDumper = null;
        }
        Logger.i(TAG + "停止输出日志");
    }

    /**
     * 保存打印的日志信息
     *
     * @return this
     */
    public Logger cache(int level) {
        mLogDumper.cache(level);
        return this;
    }

    /**
     * 输出日志文件的线程
     */
    private class LogDumper extends Thread {

        private Process logcatProcess;
        private BufferedReader mBufferedReader;
        private FileOutputStream mFileOutputStream;

        private boolean mRunning = true;

        private String mCmds;
        private String mPID;

        LogDumper(String pid, String dir) {
            try {
                mPID = pid;
                mFileOutputStream = new FileOutputStream(new File(dir, DateHelper.getCurrentTimeMillis() + SUFFIX));
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            cache(sLevel);
        }

        private void cache(int level) {
            if (level == LEBEL) {
                Logger.i(TAG + "打印只带tag的日志信息");
                mCmds = "logcat -s " + mTag;
            } else if (level == VERBOSE) {
                mCmds = "logcat *:e *:v |grep\"(" + mPID + ")\"";
            } else if (level == DEBUG) {
                mCmds = "logcat *:e *:d |grep\"(" + mPID + ")\"";
            } else if (level == INFO) {
                mCmds = "logcat *:e *:i |grep\"(" + mPID + ")\"";
            } else if (level == WARN) {
                mCmds = "logcat *:e *:w |grep\"(" + mPID + ")\"";
            } else if (level == ERROR) {
                mCmds = "logcat *:e |grep\"(" + mPID + ")\"";
            } else {
                Logger.i(TAG + "打印所有的日志信息");
                mCmds = "logcat |grep\"(" + mPID + ")\"";
            }
        }

        private void stopLogs() {
            mRunning = false;
        }

        @Override
        public void run() {
            super.run();
            try {
                logcatProcess = Runtime.getRuntime().exec(mCmds);
                mBufferedReader = new BufferedReader(new InputStreamReader(logcatProcess.getInputStream()), 1024);
                String line;
                while (mRunning && (line = mBufferedReader.readLine()) != null) {
                    if (!mRunning) {
                        break;
                    }
                    if (line.length() == 0) {
                        continue;
                    }
                    if (mFileOutputStream != null && line.contains(mPID)) {
                        mFileOutputStream.write(("| " + DateHelper.getCurrentTimeMillis() + " | " + line + "\n").getBytes());
                    }
                }
                mBufferedReader.close();
                mFileOutputStream.close();
                logcatProcess.destroy();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (logcatProcess != null) {
                    logcatProcess.destroy();
                    logcatProcess = null;
                }
                if (mBufferedReader != null) {
                    try {
                        mBufferedReader.close();
                        mBufferedReader = null;
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                if (mFileOutputStream != null) {
                    try {
                        mFileOutputStream.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    mFileOutputStream = null;
                }
            }
        }
    }

    /**
     * 转化为string
     *
     * @param object 需要转为为string的对象
     * @return 转化后的string
     */
    public static String toString(Object object) {
        if (object == null) {
            return "null";
        }
        if (!object.getClass().isArray()) {
            return object.toString();
        }
        if (object instanceof boolean[]) {
            return Arrays.toString((boolean[]) object);
        }
        if (object instanceof byte[]) {
            return Arrays.toString((byte[]) object);
        }
        if (object instanceof char[]) {
            return Arrays.toString((char[]) object);
        }
        if (object instanceof short[]) {
            return Arrays.toString((short[]) object);
        }
        if (object instanceof int[]) {
            return Arrays.toString((int[]) object);
        }
        if (object instanceof long[]) {
            return Arrays.toString((long[]) object);
        }
        if (object instanceof float[]) {
            return Arrays.toString((float[]) object);
        }
        if (object instanceof double[]) {
            return Arrays.toString((double[]) object);
        }
        if (object instanceof Object[]) {
            return Arrays.deepToString((Object[]) object);
        }
        return "Couldn't find a correct type for the object";
    }
}