package com.dzenm.lib.permission;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.IntDef;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.dzenm.lib.R;
import com.dzenm.lib.dialog.AbsDialogFragment;
import com.dzenm.lib.dialog.DialogHelper;
import com.dzenm.lib.dialog.ViewHolder;
import com.dzenm.lib.drawable.DrawableHelper;
import com.dzenm.lib.log.Logger;
import com.dzenm.lib.material.MaterialDialog;
import com.dzenm.lib.os.OsHelper;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * @author dzenm
 * @date 2020/3/12 下午8:09
 */
public class PermissionFragment extends Fragment implements DialogHelper.OnBindViewHolder {

    private static final String TAG = PermissionFragment.class.getSimpleName() + "| ";

    /**
     * 权限请求之后, 会回调onRequestPermissionsResult()方法, 需要通过requestCode去接收权限请求的结果
     */
    private static final int REQUEST_PERMISSION = 0xF1;

    /**
     * 当权限请求被拒绝之后, 提醒用户进入设置权限页面手动打开所需的权限, 用于接收回调的结果
     */
    static final int REQUEST_SETTING = 0xF2;

    @IntDef({PermissionManager.MODE_ONCE,
            PermissionManager.MODE_ONCE_INFO,
            PermissionManager.MODE_REPEAT})
    @Retention(RetentionPolicy.SOURCE)
    @interface Mode {
    }

    /**
     * 请求权限的模式 {@link PermissionManager#MODE_ONCE} 提示一次请求权限, 不管授予权限还是未授予权限,
     * {@link PermissionManager#into(PermissionManager.OnPermissionListener)} ) }
     * 都会返回true. {@link PermissionManager#MODE_ONCE_INFO} 提示一次请求权限, 如果未授予则会提示手动打开.
     * {@link PermissionManager#MODE_REPEAT}
     * 提示一次请求权限, 如果权限未授予, 则重复提示授权, 若用户点击不再询问, 则提示用户手动打开直至权限全部授予之后
     * 才回调
     */
    @Mode
    int mRequestMode = PermissionManager.MODE_ONCE_INFO;

    private String[] mPermissions;                                  // 过滤未被授权的权限
    String[] mAllPermissions;                                       // 需要请求的所有的权限

    PermissionManager.OnPermissionListener mOnPermissionListener;   // 请求权限回调, 成功为true, 失败为false

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    void startRequestPermission() {
        // 过滤未授予的请求
        mPermissions = OsHelper.filterPermissions(getActivity(), mAllPermissions);
        // 判断是否存在未请求的权限，如果存在则继续请求，不存在返回请求结果
        if (mPermissions == null || mPermissions.length == 0) {
            requestResult(true);
        } else {
            if (isRationaleAll()) {
                Logger.i(TAG + "请求权限时被拒绝, 提示用户为什么要授予权限");
                openPromptPermissionDialog();
            } else {
                Logger.i(TAG + "开始请求权限...");
                requestPermission();
            }
        }
    }

    private void requestPermission() {
        this.requestPermissions(mAllPermissions, REQUEST_PERMISSION);               // 请求权限
    }

    private void onPermissionResult() {
        // 判断是否还存在未授予的权限
        if (mPermissions == null || mPermissions.length == 0) {
            requestResult(true);
        } else if (mRequestMode == PermissionManager.MODE_ONCE) {
            openFailedDialog();
        } else if (mRequestMode == PermissionManager.MODE_ONCE_INFO) {
            Logger.i(TAG + "请求权限被拒绝并且记住, 提示用户手动打开权限");
            openSettingDialog();
        } else if (mRequestMode == PermissionManager.MODE_REPEAT) {
            if (isRationaleAll()) {
                Logger.i(TAG + "请求权限被拒绝未记住, 重复请求权限");
                startRequestPermission();
            } else {
                Logger.i(TAG + "请求权限被拒绝并且记住, 提示用户手动打开权限");
                openSettingDialog();
            }
        }
    }

    private void requestResult(boolean result) {
        if (mOnPermissionListener != null) mOnPermissionListener.onPermit(result);  // 权限请求结果
    }

    /**
     * 手动授予权限回掉（重写onActivityResult，并调用该方法）
     *
     * @param requestCode 请求时的标志位
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_SETTING) startRequestPermission();
    }

    /**
     * 权限处理结果（重写onRequestPermissionsResult方法，并调用该方法）
     * 当请求权限的模式为 MODE_ONCE_INFO 或 MODE_REPEAT 时，如果需要强制授予权限，不授予时不予进入，则需要执行该方法
     *
     * @param requestCode  请求权限的标志位
     * @param permissions  未授权的权限调用请求权限的权限
     * @param grantResults 用于判断是否授权 grantResults[i] == PackageManager.PERMISSION_GRANTED
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (REQUEST_PERMISSION == requestCode) {
            // 第一次请求的处理结果，过滤已授予的权限
            mPermissions = OsHelper.filterPermissions(getActivity(), permissions);
            onPermissionResult();
        }
    }

    /**
     * 用于判断是否记住并拒绝授予权限
     *
     * @return 是否记住并拒绝授予所有请求的权限
     */
    private boolean isRationaleAll() {
        return OsHelper.isRationaleAll(getActivity(), mPermissions);
    }

    /**
     * 打开设置权限的对话框
     */
    private void openSettingDialog() {
        CharSequence negativeText = mRequestMode == PermissionManager.MODE_REPEAT
                ? getText(R.string.permission_btn_rationale)
                : isRationaleAll()
                ? getText(R.string.permission_btn_rationale)
                : getText(R.string.dialog_btn_cancel);
        new MaterialDialog.Builder((AppCompatActivity) getActivity())
                .setTitle(getText(R.string.permission_info))
                .setMessage(getText(R.string.permission_open_permission_in_setting))
                .setButtonText(getText(R.string.permission_btn_setting), negativeText)
                .setMaterialDesign(false)
                .setOnClickListener(new MaterialDialog.OnClickListener() {
                    @Override
                    public void onClick(MaterialDialog dialog, int which) {
                        if (which == MaterialDialog.OnClickListener.BUTTON_POSITIVE) {
                            // 点击确定按钮,进入设置页面
                            PermissionSetting.openSetting(PermissionFragment.this, false);
                        } else {
                            if (isRationaleAll()) { // 拒绝权限并且未记住时, 提示用户授予权限并请求权限
                                startRequestPermission();
                            } else {    // 拒绝权限并且记住时, 取消授权
                                if (mRequestMode == PermissionManager.MODE_REPEAT) {
                                    startRequestPermission();
                                } else {
                                    requestResult(false);
                                }
                            }
                        }
                        dialog.dismiss();
                    }
                })
                .create().show();
    }

    /**
     * 打开未授予权限的对话框
     */
    private void openFailedDialog() {
        new MaterialDialog.Builder((AppCompatActivity) getActivity())
                .setTitle(getText(R.string.permission_info))
                .setMessage(getText(R.string.permission_refuse_to_rationale_permission))
                .setMaterialDesign(false)
                .setButtonText(getText(R.string.dialog_btn_confirm))
                .setOnClickListener(new MaterialDialog.OnClickListener() {
                    @Override
                    public void onClick(MaterialDialog dialog, int which) {
                        // 回调请求结果
                        requestResult(false);
                        dialog.dismiss();
                    }
                }).create().show();
    }

    /**
     * 打开权限请求提示框
     */
    private void openPromptPermissionDialog() {
        DialogHelper.newInstance((AppCompatActivity) getActivity())
                .setLayout(R.layout.dialog_permission_prompt)
                .setOnBindViewHolder(this)
                .setCancel(false)
                .setBackground(DrawableHelper.solid(android.R.color.white)
                        .radius(10)
                        .build())
                .show();
    }

    @Override
    public void onBinding(ViewHolder holder, final AbsDialogFragment dialog) {
        ((TextView) holder.getView(R.id.tv_title)).setText(getText(R.string.dialog_info));
        ((TextView) holder.getView(R.id.tv_message)).setText(getText(R.string.permission_runtime_permission));
        String permission = getPermissionPrompt(mPermissions);
        ((TextView) holder.getView(R.id.tv_permission)).setText(permission);

        TextView confirm = holder.getView(R.id.tv_confirm);
        confirm.setText(getText(R.string.permission_btn_go_rationale));
        ImageView cancel = holder.getView(R.id.iv_cancel);

        DrawableHelper.radius(10f)
                .pressed(R.color.colorLightGray)
                .into(cancel);

        DrawableHelper.radiusBL(10f)
                .radiusBR(10f)
                .pressed(R.color.colorMaterialLightBlue, R.color.colorMaterialSecondLightBlue)
                .into(confirm);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setPromptCancelClick();
                dialog.dismiss();
            }
        });
        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setPromptConfirmClick();
                dialog.dismiss();
            }
        });
    }

    /**
     * 权限请求提示框取消按钮点击事件
     */
    private void setPromptCancelClick() {
        if (mRequestMode == PermissionManager.MODE_REPEAT) {    // 重复授权时点击取消按钮进入设置提示框
            openSettingDialog();
        } else {                                                // 非重复授权时点击取消取消授权
            requestResult(false);
        }
    }

    /**
     * 权限请求提示框确定按钮点击事件
     */
    private void setPromptConfirmClick() {
        if (isRationaleAll()) {     // 拒绝权限时未点击"不再询问", 重复请求权限
            requestPermission();
        } else {                    // 拒绝权限时点击"不再询问", 进入设置提示框
            openSettingDialog();
        }
    }

    /**
     * @return 权限提示文本
     */
    static String getPermissionPrompt(String[] permissions) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < permissions.length; i++) {
            String permission = permissions[i];
            if (TextUtils.isEmpty(permission)) continue;
            sb.append(i == 0 ? "⊙\t\t" : "\n⊙\t\t").append(getPermissionText(permission));
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