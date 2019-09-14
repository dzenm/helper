package com.dzenm.helper.net;

import android.content.res.Resources;

import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;

/**
 * @author dinzhenyan
 * @date 2019-07-01 08:26
 */
public class ApiException {

    public static final String NETWORK_REQUEST_TIMEOUT = "网络请求超时";
    public static final String NETWORK_REQUEST_ERROR = "客户端出现异常";
    public static final String NETWORK_REQUEST_INTERNET_ERROR = "没有网络，请检查网络设置";
    public static final String NETWORK_REQUEST_HTTP_ERROR = "网络请求失败";
    public static final String NETWORK_REQUEST_CONNECT_ERROR = "网络连接失败";

    private static boolean isDebug = true;

    public static void isDebug(boolean isDebug) {
        ApiException.isDebug = isDebug;
    }

    public static boolean getDebug() {
        return ApiException.isDebug;
    }

    public static String getException(Throwable e) {
        String exception;
        if (e.getClass() == NullPointerException.class) {
            exception = isDebug ? NETWORK_REQUEST_ERROR + ", 错误码: 1001" : NETWORK_REQUEST_ERROR;
        } else if (e.getClass() == IndexOutOfBoundsException.class) {
            exception = isDebug ? NETWORK_REQUEST_ERROR + ", 错误码: 1002" : NETWORK_REQUEST_ERROR;
        } else if (e.getClass() == ArrayIndexOutOfBoundsException.class) {
            exception = isDebug ? NETWORK_REQUEST_ERROR + ", 错误码: 1003" : NETWORK_REQUEST_ERROR;
        } else if (e.getClass() == SocketTimeoutException.class) {
            exception = isDebug ? NETWORK_REQUEST_ERROR + ", 错误码: 1004" : NETWORK_REQUEST_ERROR;
        } else if (e.getClass() == IllegalStateException.class) {
            exception = isDebug ? NETWORK_REQUEST_ERROR + ", 错误码: 1005" : NETWORK_REQUEST_ERROR;
        } else if (e.getClass() == RuntimeException.class) {
            exception = isDebug ? NETWORK_REQUEST_ERROR + ", 错误码: 1006" : NETWORK_REQUEST_ERROR;
        } else if (e.getClass() == ClassCastException.class) {
            exception = isDebug ? NETWORK_REQUEST_ERROR + ", 错误码: 1007" : NETWORK_REQUEST_ERROR;
        } else if (e.getClass() == NoSuchMethodException.class) {
            exception = isDebug ? NETWORK_REQUEST_ERROR + ", 错误码: 1008" : NETWORK_REQUEST_ERROR;
        } else if (e.getClass() == NumberFormatException.class) {
            exception = isDebug ? NETWORK_REQUEST_ERROR + ", 错误码: 1009" : NETWORK_REQUEST_ERROR;
        } else if (e.getClass() == SecurityException.class) {
            exception = isDebug ? NETWORK_REQUEST_ERROR + ", 错误码: 1010" : NETWORK_REQUEST_ERROR;
        } else if (e.getClass() == Resources.NotFoundException.class) {
            exception = isDebug ? NETWORK_REQUEST_ERROR + ", 错误码: 1011" : NETWORK_REQUEST_ERROR;
        } else if (e.getClass() == SocketTimeoutException.class) {
            exception = isDebug ? NETWORK_REQUEST_TIMEOUT + ", 错误码: 401" : NETWORK_REQUEST_TIMEOUT;
        } else if (e.getClass() == UnknownHostException.class) {
            exception = isDebug ? NETWORK_REQUEST_INTERNET_ERROR + ", 错误码: 404" : NETWORK_REQUEST_INTERNET_ERROR;
        } else if (e.getClass() == ConnectException.class) {
            exception = isDebug ? NETWORK_REQUEST_CONNECT_ERROR + ", 错误码: 403" : NETWORK_REQUEST_CONNECT_ERROR;
        } else {
            exception = NETWORK_REQUEST_HTTP_ERROR;
        }
        return exception;
    }
}
