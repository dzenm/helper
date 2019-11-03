package com.dzenm.helper.base;

import android.content.Context;

import com.dzenm.helper.draw.DrawableHelper;
import com.dzenm.helper.file.FileHelper;
import com.dzenm.helper.file.SPHelper;
import com.dzenm.helper.log.CrashHelper;
import com.dzenm.helper.log.Logger;
import com.dzenm.helper.toast.ToastHelper;

/**
 * @author dzenm
 * @date 2019-07-27 10:57
 */
public class Helper {

    public static void init(Context context) {
        // 初始化Logger工具, 将打印的log保存文件, 依赖FileHelper
        Logger.getInstance().setDebug(false).init(context);
        // 初始化背景Drawable工具
        DrawableHelper.getInstance().init(context);
        // 初始化Toa工具, 依赖BackGHelper
        ToastHelper.getInstance().init(context);
        // 初始化SharedPreferences工具
        SPHelper.getInstance().init(context);
    }
}
