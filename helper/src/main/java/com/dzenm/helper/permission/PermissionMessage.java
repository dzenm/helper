package com.dzenm.helper.permission;

import android.Manifest;
import android.app.Activity;
import android.text.TextUtils;

import androidx.appcompat.app.AppCompatActivity;

import com.dzenm.helper.dialog.InfoDialog;

/**
 * @author dzenm
 * @date 2020/3/12 下午9:50
 */
class PermissionMessage {

    static InfoDialog getInfoDialog(Activity activity, String message) {
        return InfoDialog.newInstance((AppCompatActivity) activity)
                .setTitle("权限申请提示")
                .setMessage(message)
                .setCancel(false);
    }

    /**
     * @return 权限提示文本
     */
    static String getPermissionPrompt(String[] permissions) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < permissions.length; i++) {
            String permission = permissions[i];
            if (TextUtils.isEmpty(permission)) continue;
            sb.append(i == 0 ? "⊙\t\t" : "\n⊙\t\t").append(PermissionMessage.getPermissionText(permission));
        }
        return "".contentEquals(sb) ? "(空)" : sb.toString();
    }

    /**
     * 获取权限文本名称
     *
     * @param permission 需要转化的权限
     * @return 转化后的权限名称
     */
    private static String getPermissionText(String permission) {
        // 联系人权限
        switch (permission) {
            case Manifest.permission.WRITE_CONTACTS:
                return "联系人写入权限";
            case Manifest.permission.GET_ACCOUNTS:
                return "获取联系人账户权限";
            case Manifest.permission.READ_CONTACTS:
                return "读取联系人权限";
            case Manifest.permission.READ_CALL_LOG:
                return "读取通话记录权限";
            case Manifest.permission.READ_PHONE_STATE:
                return "读取手机状态权限";
            case Manifest.permission.CALL_PHONE:
                return "拨打电话权限";
            case Manifest.permission.WRITE_CALL_LOG:
                return "通话记录写入权限";
            case Manifest.permission.USE_SIP:
                return "使用SIP权限";
            case Manifest.permission.PROCESS_OUTGOING_CALLS:
                return "处理外呼电话权限";
            case Manifest.permission.ADD_VOICEMAIL:
                return "添加声音邮件权限";
            case Manifest.permission.READ_CALENDAR:
                return "读取日历权限";
            case Manifest.permission.WRITE_CALENDAR:
                return "日历写入权限";
            case Manifest.permission.CAMERA:
                return "照相机权限";
            case Manifest.permission.BODY_SENSORS:
                return "传感器权限";
            case Manifest.permission.ACCESS_FINE_LOCATION:
                return "访问精确位置权限";
            case Manifest.permission.ACCESS_COARSE_LOCATION:
                return "访问粗略位置权限";
            case Manifest.permission.READ_EXTERNAL_STORAGE:
                return "读取外部存储权限";
            case Manifest.permission.WRITE_EXTERNAL_STORAGE:
                return "外部存储写入权限";
            case Manifest.permission.RECORD_AUDIO:
                return "录制音频权限";
            case Manifest.permission.READ_SMS:
                return "读取短信权限";
            case Manifest.permission.RECEIVE_WAP_PUSH:
                return "接收WAP推送权限";
            case Manifest.permission.RECEIVE_MMS:
                return "接收彩信权限";
            case Manifest.permission.RECEIVE_SMS:
                return "接收短信权限";
            case Manifest.permission.SEND_SMS:
                return "发送短信权限";
        }
        return "";
    }
}
