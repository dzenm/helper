package com.dzenm.helper.task;

import android.content.Context;
import android.os.Handler;
import android.os.Message;

import java.lang.ref.WeakReference;

/**
 * @author dinzhenyan
 * @date 2019-05-30 20:14
 */
public class WeakHandler extends Handler {

    WeakReference<Context> weakReference;

    public WeakHandler(Context context) {
        weakReference = new WeakReference<>(context);
    }

    @Override
    public void handleMessage(Message msg) {
        super.handleMessage(msg);
        Context activity = weakReference.get();
        if (activity == null) {
            return;
        }
    }
}
