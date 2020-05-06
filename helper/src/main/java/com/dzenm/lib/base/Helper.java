package com.dzenm.lib.base;

import android.content.Context;

import com.dzenm.lib.drawable.DrawableHelper;
import com.dzenm.lib.file.FileHelper;
import com.dzenm.lib.file.SPHelper;
import com.dzenm.lib.log.CrashHelper;
import com.dzenm.lib.log.Logger;
import com.dzenm.lib.toast.ToastHelper;

/**
 * <pre>
 * Helper.init(this);
 * </pre>
 *
 * @author dzenm
 * @date 2019-07-27 10:57
 */
public class Helper {

    public static void init(final Context context) {
        // 初始化Logger工具, 将打印的log保存文件
        CrashHelper.getInstance().init(context);
        // 初始化APP文件夹
        FileHelper.getInstance().init(context);
        // 用于将捕捉到的异常保存为文件, 依赖FileHelper
        Logger.getInstance().setDebug(true).init();
        // 初始化背景Drawable工具
        DrawableHelper.getInstance().init(context);
        // 初始化Toa工具, 依赖BackGHelper
        ToastHelper.getInstance().init(context);
        // 初始化SharedPreferences工具
        SPHelper.getInstance().init(context);
    }
}
