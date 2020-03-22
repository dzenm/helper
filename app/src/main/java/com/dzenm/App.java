package com.dzenm;

import android.view.Gravity;

import com.dzenm.helper.base.Application;
import com.dzenm.helper.draw.DrawableHelper;
import com.dzenm.helper.toast.ToastHelper;

/**
 * @author dinzhenyan
 * @date 2019-06-07 21:44
 */
public class App extends android.app.Application {

    @Override
    public void onCreate() {
        super.onCreate();
        Application.init(this);
        ToastHelper.getInstance()
                .setGravity(Gravity.CENTER, 0)
                .setBackground(DrawableHelper.solid(android.R.color.holo_purple).radius(16).build());
    }
}
