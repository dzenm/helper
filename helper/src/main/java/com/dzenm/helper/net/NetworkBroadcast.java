package com.dzenm.helper.net;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;

import com.dzenm.helper.log.Logger;

/**
 * @author dinzhenyan
 * @date 2019-05-30 20:52
 * 网络监听 广播
 */
class NetworkBroadcast extends BroadcastReceiver {

    private static final String TAG = NetworkBroadcast.class.getSimpleName() + "| ";

    private boolean mRegister = false;
    private static volatile NetworkBroadcast sNetworkBroadcast;

    private NetworkBroadcast() {
    }

    public static NetworkBroadcast getInstance() {
        if (sNetworkBroadcast == null) synchronized (NetworkBroadcast.class) {
            if (sNetworkBroadcast == null) sNetworkBroadcast = new NetworkBroadcast();
        }
        return sNetworkBroadcast;
    }

    public boolean isRegister() {
        return mRegister;
    }

    /*
     * 网络广播监听回掉
     */
    private NetworkHelper.OnNetworkChangeListener mOnNetworkChangeListener;

    void setOnNetworkChangeListener(NetworkHelper.OnNetworkChangeListener onNetworkChangeListener) {
        mOnNetworkChangeListener = onNetworkChangeListener;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        // 如果相等的话就说明网络状态发生了变化
        if (intent.getAction() != null && intent.getAction().equals(ConnectivityManager.CONNECTIVITY_ACTION)) {
            boolean isConnected = NetworkHelper.isConnected(context);
            // 当网络发生变化，判断当前网络状态，并通过NetEvent回调当前网络状态
            if (mOnNetworkChangeListener != null) {
                Logger.i(TAG + "network broadcast receive connect state: " + isConnected);
                mOnNetworkChangeListener.onNetwork(isConnected);
            }
        }
    }

    /**
     * 注册广播
     *
     * @param context 上下文
     */
    void registerNetwork(Context context) {
        if (mRegister) return;
        try {
            //实例化IntentFilter对象
            IntentFilter intentFilter = new IntentFilter();
            intentFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
            context.registerReceiver(this, intentFilter);
            mRegister = true;
            Logger.i(TAG + "register network broadcast");
        } catch (Exception e) {
            Logger.i(TAG + "register network broadcast error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * 取消注册广播
     *
     * @param context 上下文
     */
    void unregisterNetwork(Context context) {
        if (!mRegister) return;
        try {
            context.unregisterReceiver(this);
            mRegister = false;
            Logger.i(TAG + "unregister network broadcast");
        } catch (Exception e) {
            Logger.i(TAG + "unregister network broadcast error: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
