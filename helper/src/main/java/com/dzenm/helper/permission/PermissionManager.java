package com.dzenm.helper.permission;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.IntDef;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import com.dzenm.helper.R;
import com.dzenm.helper.dialog.AbsDialogFragment;
import com.dzenm.helper.dialog.DialogHelper;
import com.dzenm.helper.dialog.InfoDialog;
import com.dzenm.helper.dialog.ViewHolder;
import com.dzenm.helper.draw.DrawableHelper;
import com.dzenm.helper.log.Logger;
import com.dzenm.helper.os.OsHelper;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;
import java.util.List;

/**
 * <pre>
 *     PermissionManager.getInstance()
 *                     .with(this)
 *                     .load(permissions)
 *                     .into(this)
 *                     .requestPermission();
 *
 *    @Override
 *     protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
 *         super.onActivityResult(requestCode, resultCode, data);
 *         if (mMode != PermissionManager.MODE_ONCE) {
 *             PermissionManager.getInstance().onSettingResult(requestCode);
 *         }
 *     }
 *
 *     @Override
 *     public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
 *         super.onRequestPermissionsResult(requestCode, permissions, grantResults);
 *         PermissionManager.getInstance().onPermissionResult(requestCode, permissions, grantResults);
 *     }
 * </pre>
 *
 * @author dinzhenyan
 * @date 2019-04-30 20:03
 * <p>
 * 权限请求管理工具类
 */
public final class PermissionManager implements DialogHelper.OnConvertViewClickListener {

    private static final String TAG = PermissionManager.class.getSimpleName() + "|";

    /**
     * 权限请求之后, 会回调onRequestPermissionsResult()方法, 需要通过requestCode去接收权限请求的结果
     */
    private static final int REQUEST_PERMISSION = 100;

    /**
     * 当权限请求被拒绝之后, 提醒用户进入设置权限页面手动打开所需的权限, 用于接收回调的结果
     */
    static final int REQUEST_SETTING = 101;

    public static final int MODE_ONCE = 1001;
    public static final int MODE_ONCE_INFO = 1002;
    public static final int MODE_REPEAT = 1003;

    @IntDef({MODE_ONCE, MODE_ONCE_INFO, MODE_REPEAT})
    @Retention(RetentionPolicy.SOURCE)
    @interface Mode {
    }

    /**
     * 请求权限的模式 {@link #MODE_ONCE} 提示一次请求权限, 不管授予权限还是未授予权限, {@link #into(OnPermissionListener)}
     * 都会返回true. {@link #MODE_ONCE_INFO} 提示一次请求权限, 如果未授予则会提示手动打开. {@link #MODE_REPEAT}
     * 提示一次请求权限, 如果权限未授予, 则重复提示授权, 若用户点击不再询问, 则提示用户手动打开直至权限全部授予之后
     * 才回调
     */
    private @Mode
    int mRequestMode = MODE_ONCE_INFO;

    /**
     * 过滤 {@link #load(String[])} )} 中未被授权的权限
     */
    private String[] mPermissions;

    /**
     * 通过 {@link #load(String[])} )} 传入的权限
     */
    private String[] mAllPermissions;

    /**
     * {@link #into(OnPermissionListener)}请求权限回调, 当权限请求成功时, 回调的结果是true, 否则为false
     */
    private OnPermissionListener mOnPermissionListener;

    private AppCompatActivity mActivity;
    private Fragment mFragment;

    @SuppressLint("StaticFieldLeak")
    private static volatile PermissionManager sPermissionManager;

    private PermissionManager() {
    }

    public static PermissionManager getInstance() {
        if (sPermissionManager == null) synchronized (PermissionManager.class) {
            if (sPermissionManager == null)
                sPermissionManager = new PermissionManager();
        }
        return sPermissionManager;
    }

    public PermissionManager with(AppCompatActivity activity) {
        Logger.d(TAG + activity.getClass().getSimpleName() + " is requesting permission");
        mActivity = activity;
        return this;
    }

    public PermissionManager with(Fragment fragment) {
        Logger.d(TAG + fragment.getClass().getSimpleName() + " is requesting permission");
        mFragment = fragment;
        return this;
    }


    /**
     * @param requestMode {@link #mRequestMode}
     * @return
     */
    public PermissionManager mode(@Mode int requestMode) {
        mRequestMode = requestMode;
        return this;
    }

    /**
     * @param permissions {@link #mAllPermissions}
     * @return
     */
    public PermissionManager load(String permissions) {
        load(new String[]{permissions});
        return this;
    }

    /**
     * @param permissions {@link #mAllPermissions}
     * @return
     */
    public PermissionManager load(List<String> permissions) {
        load(permissions.toArray(new String[permissions.size()]));
        return this;
    }

    /**
     * @param permissions {@link #mAllPermissions}
     * @return
     */
    public PermissionManager load(String[] permissions) {
        mAllPermissions = permissions;
        return this;
    }

    /**
     * @param onPermissionListener {@link #mOnPermissionListener}
     * @return
     */
    public PermissionManager into(OnPermissionListener onPermissionListener) {
        mOnPermissionListener = onPermissionListener;
        return this;
    }

    /**
     * 开始请求权限
     */
    public void request() {
        startRequestPermission();
    }

    private void startRequestPermission() {
        // 过滤未授予的请求
        mPermissions = filterPermission(mAllPermissions);
        // 判断是否存在未请求的权限，如果存在则继续请求，不存在返回请求结果
        if (mPermissions == null || mPermissions.length == 0) {
            requestResult(true);
        } else {
            if (isRationaleAll()) {
                Logger.i("请求权限时被拒绝, 提示用户为什么要授予权限");
                openPromptPermissionDialog();
            } else {
                Logger.i("开始请求权限...");
                requestPermission();
            }
        }
    }

    /**
     * 请求权限
     */
    private void requestPermission() {
        ActivityCompat.requestPermissions(mActivity, mAllPermissions, REQUEST_PERMISSION);
    }

    /**
     * 权限处理结果（重写onRequestPermissionsResult方法，并调用该方法）
     * 当请求权限的模式为 MODE_ONCE_INFO 或 MODE_REPEAT 时，如果需要强制授予权限，不授予时不予进入，则需要执行该方法
     *
     * @param requestCode  请求权限结果回调的唯一码
     * @param permissions  未授权的权限调用请求权限的权限
     * @param grantResults 用于判断是否授权 grantResults[i] == PackageManager.PERMISSION_GRANTED
     */
    public void onPermissionResult(int requestCode, String[] permissions, int[] grantResults) {
        if (REQUEST_PERMISSION == requestCode) {
            // 第一次请求的处理结果，过滤已授予的权限
            mPermissions = filterPermission(permissions);
            onPermissionResult();
        }
    }

    private void onPermissionResult() {
        // 判断是否还存在未授予的权限
        if (mPermissions == null || mPermissions.length == 0) {
            requestResult(true);
        } else if (mRequestMode == MODE_ONCE) {
            openFailedDialog();
        } else if (mRequestMode == MODE_ONCE_INFO) {
            Logger.i("请求权限被拒绝并且记住, 提示用户手动打开权限");
            openSettingDialog();
        } else if (mRequestMode == MODE_REPEAT) {
            if (isRationaleAll()) {
                Logger.i("请求权限被拒绝未记住, 重复请求权限");
                startRequestPermission();
            } else {
                Logger.i("请求权限被拒绝并且记住, 提示用户手动打开权限");
                openSettingDialog();
            }
        }
    }

    /**
     * 手动授予权限回掉（重写onActivityResult，并调用该方法）
     *
     * @param requestCode
     * @param resultCode
     * @param data
     * @return
     */
    public void onSettingResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == REQUEST_SETTING) request();
    }

    /**
     * 权限请求结果
     *
     * @param result
     */
    private void requestResult(boolean result) {
        if (mOnPermissionListener != null) mOnPermissionListener.onPermit(result);
    }

    /**
     * 用于判断是否记住并拒绝授予权限
     *
     * @return 是否记住并拒绝授予所有请求的权限
     */
    private boolean isRationaleAll() {
        for (String permission : mPermissions) {
            if (!OsHelper.isRationale(mActivity, permission)) return false;
        }
        return true;
    }

    /**
     * 过滤未授予的权限
     *
     * @param permissions
     * @return
     */
    private String[] filterPermission(String[] permissions) {
        if (permissions == null || permissions.length == 0) return null;
        List<String> filterPermits = new ArrayList<>();
        for (String permission : permissions) {
            // 检查是否授予权限, 将未授予的权限将筛选出来
            Logger.d(TAG + "request permission: " + permission);
            if (!OsHelper.isGrant(mActivity, permission)) filterPermits.add(permission);
        }
        String[] res = new String[filterPermits.size()];
        return filterPermits.toArray(res);
    }

    /**
     * 打开设置权限的对话框
     */
    private void openSettingDialog() {
        String negativeText = mRequestMode == MODE_REPEAT ? "进入授权" : isRationaleAll() ? "进入授权" : "取消";
        getInfoDialog("拒绝授予权限将导致程序运行出现不可预料的错误, 请前往设置手动授予权限")
                .setButtonText("手动设置", negativeText)
                .setOnDialogClickListener(new InfoDialog.OnInfoClickListener() {
                    @Override
                    public boolean onClick(InfoDialog dialog, boolean confirm) {
                        if (confirm) {  // 点击确定按钮,进入设置页面
                            PermissionSetting.openSetting(mActivity, false);
                        } else {
                            if (isRationaleAll()) { // 拒绝权限并且未记住时, 提示用户授予权限并请求权限
                                startRequestPermission();
                            } else {    // 拒绝权限并且记住时, 取消授权
                                if (mRequestMode == MODE_REPEAT) {
                                    startRequestPermission();
                                } else {
                                    requestResult(false);
                                }
                            }
                        }
                        return true;
                    }
                }).show();
    }

    /**
     * 打开未授予权限的对话框
     */
    private void openFailedDialog() {
        getInfoDialog("拒绝授予程序运行需要的权限, 将出现不可预知的错误, 请授予权限后继续操作")
                .setButtonText("确定")
                .setOnDialogClickListener(new InfoDialog.OnInfoClickListener() {
                    @Override
                    public boolean onClick(InfoDialog dialog, boolean confirm) {
                        // 回调请求结果
                        requestResult(false);
                        return true;
                    }
                }).show();
    }

    /**
     * 打开权限请求提示框
     */
    private void openPromptPermissionDialog() {
        DialogHelper.newInstance(mActivity)
                .setLayout(R.layout.dialog_permission_prompt)
                .setCancel(false)
                .setOnConvertViewClickListener(this)
                .setBackground(DrawableHelper.solid(android.R.color.white)
                        .radius(10)
                        .build())
                .show();
    }

    @Override
    public void onConvertClick(ViewHolder holder, final AbsDialogFragment dialog) {
        ((TextView) holder.getView(R.id.tv_title)).setText("温馨提示");
        ((TextView) holder.getView(R.id.tv_message)).setText("程序运行所需以下权限");
        String permission = getPermissionPrompt();
        ((TextView) holder.getView(R.id.tv_permission)).setText(permission);

        TextView confirm = holder.getView(R.id.tv_confirm);
        confirm.setText("前往授权");
        ImageView cancel = holder.getView(R.id.iv_cancel);

        DrawableHelper.radius(10f)
                .pressed(R.color.colorLightGray)
                .into(cancel);

        DrawableHelper.radiusBL(10f)
                .radiusBR(10f)
                .pressed(R.color.colorDarkBlue, R.color.colorTranslucentDarkBlue)
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
        if (mRequestMode == MODE_REPEAT) {  // 重复授权时点击取消按钮进入设置提示框
            openSettingDialog();
        } else {                            // 非重复授权时点击取消取消授权
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

    private InfoDialog getInfoDialog(String message) {
        return InfoDialog.newInstance(mActivity)
                .setTitle("温馨提示")
                .setMessage(message)
                .setCancel(false);
    }

    /**
     * @return 权限提示文本
     */
    private String getPermissionPrompt() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < mPermissions.length; i++) {
            String permission = mPermissions[i];
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
    private String getPermissionText(String permission) {
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

    public interface OnPermissionListener {

        void onPermit(boolean isGrant);
    }
}
