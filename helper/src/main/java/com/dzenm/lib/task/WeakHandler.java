package com.dzenm.lib.task;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import java.lang.ref.WeakReference;

/**
 * @author dinzhenyan
 * @date 2019-05-30 20:14
 */
public class WeakHandler extends Handler {

    private WeakReference<Context> weakReference;

    public WeakHandler(Context context) {
        weakReference = new WeakReference<>(context);
    }

    public WeakHandler(Looper looper) {
        super(looper);
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
