package com.dzenm.helper.net;


import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.provider.Settings;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.dzenm.helper.dialog.InfoDialog;
import com.dzenm.helper.log.Logger;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

/**
 * <pre>
 *     注册网络监听广播 NetworkHelper.getInstance().register(this).setOnNetworkChangeListener(this);
 *
 *     @Override
 *     public void onNetChange(boolean isConnect) {
 *         if (isConnect) {
 *         } else {
 *             NetworkHelper.setNetworkSetting(this);
 *         }
 *     }
 *     需要加入权限 <uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />
 * </pre>
 *
 * @author dinzhenyan
 * @date 2019-05-23 20:50
 * 网络工具类
 */
public class NetworkHelper {

    private static String TAG;
    private Context mContext;
    private static volatile NetworkBroadcast sNetworkBroadcast;
    private boolean isInitial = false;

    @SuppressLint("StaticFieldLeak")
    private static volatile NetworkHelper sNetworkHelper;

    private NetworkHelper() {
    }

    public static NetworkHelper getInstance() {
        if (sNetworkHelper == null) synchronized (NetworkHelper.class) {
            if (sNetworkHelper == null) {
                sNetworkHelper = new NetworkHelper();
                sNetworkBroadcast = NetworkBroadcast.getInstance();
            }
        }
        return sNetworkHelper;
    }

    /**
     * @param context
     * @return
     */
    public NetworkHelper init(Context context) {
        if (!isInitial) {
            TAG = context.getClass().getSimpleName() + "|";
            mContext = context;
            isInitial = true;
            Logger.i(TAG + "network broadcast init");
        }
        return this;
    }

    /**
     * 设置网络监听回调事件
     *
     * @param onNetworkChangeListener
     * @return
     */
    public NetworkHelper setOnNetworkChangeListener(NetworkHelper.OnNetworkChangeListener onNetworkChangeListener) {
        sNetworkBroadcast.setOnNetworkChangeListener(onNetworkChangeListener);
        return this;
    }

    /**
     * 注册广播
     *
     * @return
     */
    public NetworkHelper register(Context context) {
        init(context);
        register();
        return this;
    }

    /**
     * 注册广播
     *
     * @return
     */
    private NetworkHelper register() {
        if (!sNetworkBroadcast.isRegister()) {
            sNetworkBroadcast.registerNetwork(mContext);
        } else {
            Logger.i(TAG + "network broadcast is already register");
        }
        return this;
    }

    /**
     * 取消注册广播
     */
    public NetworkHelper unregister() {
        if (sNetworkBroadcast.isRegister()) {
            sNetworkBroadcast.unregisterNetwork(mContext);
        } else {
            Logger.i(TAG + "network broadcast is not register");
        }
        return this;
    }

    /**
     * 是否连接网络
     *
     * @return
     */
    public static boolean isConnected(Context context) {
        ConnectivityManager manager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        @SuppressLint("MissingPermission") NetworkInfo activeInfo = manager.getActiveNetworkInfo();
        return activeInfo != null && activeInfo.isConnected();
    }

    /**
     * 是否 Wi-Fi 连接
     *
     * @return
     */
    public static boolean isWifiConnected(Context context) {
        boolean isConnected = false;
        try {
            ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            @SuppressLint("MissingPermission") NetworkInfo info = cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
            isConnected = info.isConnected();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return isConnected;
    }

    /**
     * 是否 Mobile 连接
     *
     * @return
     */
    public static boolean isMobileConnected(Context context) {
        boolean isConnected = false;
        try {
            ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            @SuppressLint("MissingPermission") NetworkInfo info = cm.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
            isConnected = info.isConnected();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return isConnected;
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public static boolean isConnectNet(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        @SuppressLint("MissingPermission") NetworkCapabilities networkCapabilities = connectivityManager.getNetworkCapabilities(connectivityManager.getActiveNetwork());
        return networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED);
    }

    /**
     * 获取IP地址, 需要使用以下权限
     * <uses-permission android:name="android.permission.ACCESS_WIFI_STATE" />
     * <uses-permission android:name="android.permission.CHANGE_WIFI_STATE" />
     * <uses-permission android:name="android.permission.WAKE_LOCK" />
     * <uses-permission android:name="android.permission.INTERNET" />
     *
     * @param context
     * @return
     */
    public static String getIPAddress(Context context) {
        @SuppressLint("MissingPermission") NetworkInfo info = ((ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE)).getActiveNetworkInfo();
        if (info != null && info.isConnected()) {
            if (info.getType() == ConnectivityManager.TYPE_MOBILE) {        // 当前使用2G/3G/4G网络
                try {
                    // Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces();
                    for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements(); ) {
                        NetworkInterface ni = en.nextElement();
                        for (Enumeration<InetAddress> enumIpAddr = ni.getInetAddresses(); enumIpAddr.hasMoreElements(); ) {
                            InetAddress inetAddress = enumIpAddr.nextElement();
                            if (!inetAddress.isLoopbackAddress() && inetAddress instanceof Inet4Address) {
                                Logger.d(TAG + "获取移动网络IPv4地址: " + inetAddress.getHostAddress());
                                return inetAddress.getHostAddress();
                            }
                        }
                    }
                } catch (SocketException e) {
                    e.printStackTrace();
                    return null;
                }
            } else if (info.getType() == ConnectivityManager.TYPE_WIFI) {   // 当前使用无线网络
                @SuppressLint("WifiManagerPotentialLeak") WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
                if (wifiManager == null) return null;
                @SuppressLint("MissingPermission") WifiInfo wifiInfo = wifiManager.getConnectionInfo();
                Logger.d(TAG + "获取Wi-Fi网络IPv4地址: " + wifiInfo.getIpAddress());
                return intIP2StringIP(wifiInfo.getIpAddress());             // 得到IPV4地址
            }
        } else {
            // 当前无网络连接,请在设置中打开网络
            Logger.i(TAG + "当前无网络连接,请在设置中打开网络");
        }
        return null;
    }

    /**
     * 将得到的int类型的IP转换为String类型
     *
     * @param ip 需要转化的int类型的IP
     * @return 字符串类型的IP
     */
    public static String intIP2StringIP(int ip) {
        return (ip & 0xFF) + "." + ((ip >> 8) & 0xFF) + "." + ((ip >> 16) & 0xFF) + "." + (ip >> 24 & 0xFF);
    }

    /**
     * 打开设置网络界面
     */
    public static void setNetworkSetting(final Context context) {
        InfoDialog.newInstance((AppCompatActivity) context).setTitle("网络异常提示").setMessage("网络连接不可用,是否进行设置?").setButtonText("设置", "取消").setOnDialogClickListener(new InfoDialog.OnInfoClickListener() {
            @Override
            public boolean onClick(InfoDialog dialog, boolean confirm) {
                if (confirm) {
                    Intent intent = new Intent(Settings.ACTION_WIFI_SETTINGS);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(intent);
                }
                return true;
            }
        }).show();

    }

    /**
     * 网络异常提示界面
     */
    public static void setNetworkError(Context context) {
        //提示对话框
        InfoDialog.newInstance((AppCompatActivity) context).setTitle("网络异常提示").setMessage("网络连接异常，请再试一次?").setButtonText("确定").setOnDialogClickListener(new InfoDialog.OnInfoClickListener() {
            @Override
            public boolean onClick(InfoDialog dialog, boolean confirm) {
                return true;
            }
        }).show();
    }

    public interface OnNetworkChangeListener {
        void onNetwork(boolean connect);
    }
}