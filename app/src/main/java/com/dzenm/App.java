package com.dzenm;

import android.app.Application;
import android.view.Gravity;

import com.dzenm.helper.base.Helper;
import com.dzenm.helper.draw.BackGHelper;
import com.dzenm.helper.toast.Toa;

/**
 * @author dinzhenyan
 * @date 2019-06-07 21:44
 */
public class App extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        Helper.init(this);
        Toa.getInstance().setGravity(Gravity.CENTER, 0)
                .setBackground(BackGHelper.solid(android.R.color.holo_red_light).radius(16).build());
    }
}
