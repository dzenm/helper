package com.dzenm.helper.dialog;

import android.app.DownloadManager;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.dzenm.helper.R;
import com.dzenm.helper.animator.AnimatorHelper;
import com.dzenm.helper.download.DownloadHelper;
import com.dzenm.helper.download.NotificationType;
import com.dzenm.helper.draw.DrawableHelper;
import com.dzenm.helper.os.OsHelper;
import com.dzenm.helper.os.ScreenHelper;
import com.dzenm.helper.view.ProgressBar;

/**
 * @author dinzhenyan
 * @date 2019-05-19 22:57
 * <pre>
 * String url = "https://downpack.baidu.com/appsearch_AndroidPhone_v8.0.3(1.0.65.172)_1012271b.apk";
 * UpGradeDialog.newInstance(this)
 *         .setUrl(url)
 *         .setDesc("我也不知道更新了什么")
 *         .setSize("25.9M")
 *         .setVersionName("v2.8")
 *         .setNewVersionCode(3)
 *         .update();
 * </pre>
 */
public class UpGradeDialog extends AbsDialogFragment implements View.OnClickListener, DownloadHelper.OnDownloadListener {

    /**
     * 顶部的图片, 自定义图片 {@link #setHeadImage(int)}
     */
    private int mHeadImage;

    /**
     * 更新下载的新版本名称 {@link #setVersionName(String)}
     */
    private String mVersionName;

    /**
     * 更新下载文件的大小 {@link #setSize(String)}
     */
    private String mSize;

    /**
     * 更新的内容 {@link #setDesc(String)}
     */
    private String mDesc;

    /**
     * 颜色风格 {@link #setStyleColor(int)}
     */
    private int mStyleColor;

    /**
     * 下载的进度条显示的View {@link ProgressBar}
     */
    private ProgressBar mProgressBar;

    /**
     * 判断是否需要更新, 根据当前versionCode与newVersionCode进行对比
     */
    private boolean isUpdate;

    /**
     * 判断是否可以通过点击取消按钮取消下载
     */
    private boolean isCanCancel;

    /**
     * 下载管理器 {@link DownloadHelper}
     */
    private DownloadHelper mDownloadHelper;

    /**
     * 升级按钮
     */
    private TextView tvUpgrade;

    /**
     * 设置下载的属性
     */
    private OnRequestListener mOnRequestListener;

    /************************************* 以下为自定义方法 *********************************/

    public static UpGradeDialog newInstance(AppCompatActivity activity) {
        return new UpGradeDialog(activity);
    }

    /**
     * @param url 下载的url
     * @return this
     */
    public UpGradeDialog setUrl(String url) {
        mDownloadHelper.setUrl(url);
        return this;
    }

    /**
     * @param filePath 存储的文件路径
     * @return this
     */
    public UpGradeDialog setFilePath(String filePath) {
        mDownloadHelper.setFilePath(filePath);
        return this;
    }

    /**
     * @param versionName 下载的新版本名称
     * @return this
     */
    public UpGradeDialog setVersionName(@NonNull String versionName) {
        mDownloadHelper.setVersionName(versionName);
        mVersionName = versionName;
        return this;
    }

    /**
     * @param size 下载的安装包大小
     * @return this
     */
    public UpGradeDialog setSize(@NonNull String size) {
        mSize = size;
        return this;
    }

    /**
     * @param desc 下载新版本的更新内容
     * @return this
     */
    public UpGradeDialog setDesc(@NonNull String desc) {
        mDesc = desc;
        return this;
    }

    /**
     * @param newVersionCode 服务器上的最新版本号, 当服务器上最新版本高于当前安装的版本号时,会提示更新
     * @return this
     */
    public UpGradeDialog setNewVersionCode(long newVersionCode) {
        isUpdate = verifyVersion(newVersionCode);
        return this;
    }

    /**
     * 调用该方法进行检测是否需要更新, 当需要更新时，显示更新的dialog
     */
    public void update() {
        if (!isCanCancel) setCancel(false);
        if (isUpdate) show();
    }

    /**
     * @param canCancel 是否可以取消(是否强制更新)
     * @return this
     */
    public UpGradeDialog setCanCancel(boolean canCancel) {
        isCanCancel = canCancel;
        return this;
    }

    /**
     * @param headImage 顶部显示的图片
     * @return this
     */
    public UpGradeDialog setHeadImage(int headImage) {
        mHeadImage = headImage;
        return this;
    }

    /**
     * @param styleColor 颜色风格
     * @return this
     */
    public UpGradeDialog setStyleColor(int styleColor) {
        mStyleColor = styleColor;
        return this;
    }

    /**
     * @param onRequestListener 下载管理器的参数设置
     * @return this
     */
    public UpGradeDialog setOnRequestListener(OnRequestListener onRequestListener) {
        mOnRequestListener = onRequestListener;
        return this;
    }

    /************************************* 以下为实现过程 *********************************/

    @Override
    protected int layoutId() {
        return R.layout.dialog_upgrade;
    }

    public UpGradeDialog(AppCompatActivity activity) {
        super(activity);
        mHeadImage = R.drawable.ic_upgrade_top;
        mStyleColor = android.R.color.holo_red_light;
        mAnimator = AnimatorHelper.overshoot();
        isCanCancel = true;
        mDownloadHelper = new DownloadHelper(activity);
        mDownloadHelper.setOnDownloadListener(this);
    }

    @Override
    protected void initView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState2
    ) {
        mBackground = DrawableHelper.solid(android.R.color.transparent).build();

        ImageView ivHead = findViewById(R.id.iv_header);
        TextView tvVersion = findViewById(R.id.tv_version);
        TextView tvSize = findViewById(R.id.tv_size);
        TextView tvDesc = findViewById(R.id.tv_desc);
        mProgressBar = findViewById(R.id.progress_bar);
        LinearLayout llContent = findViewById(R.id.ll_content);

        ivHead.setImageResource(mHeadImage);
        mProgressBar.setProgressColor(getColor(mStyleColor));

        if (TextUtils.isEmpty(mVersionName)) {
            tvVersion.setVisibility(View.GONE);
        } else {
            tvVersion.setText(mVersionName);
            tvVersion.setTextColor(mSecondaryTextColor);
        }

        if (TextUtils.isEmpty(mSize)) {
            tvSize.setVisibility(View.GONE);
        } else {
            tvSize.setText(mSize);
            tvSize.setTextColor(mSecondaryTextColor);
        }

        if (TextUtils.isEmpty(mDesc)) {
            tvDesc.setVisibility(View.GONE);
        } else {
            tvDesc.setText(mDesc);
            tvDesc.setTextColor(mSecondaryTextColor);
        }

        DrawableHelper.solid(android.R.color.white)
                .radiusBR(mBackgroundRadius)
                .radiusBL(mBackgroundRadius)
                .into(llContent);

        // 升级按钮
        tvUpgrade = findViewById(R.id.tv_upgrade);
        tvUpgrade.setOnClickListener(this);
        DrawableHelper.radius(mBackgroundRadius)
                .pressed(mStyleColor, mPressedColor)
                .textColor(android.R.color.white, mStyleColor)
                .into(tvUpgrade);

        // 取消按钮
        ImageView iv_cancel = findViewById(R.id.iv_cancel);
        iv_cancel.setOnClickListener(this);
        iv_cancel.setImageResource(R.drawable.ic_upgrade_cancel);
    }

    @Override
    protected void setLayoutParams(
            ViewGroup.MarginLayoutParams layoutParams,
            int centerWidth,
            int margin
    ) {
        // 设置dialog的宽度
        layoutParams.width = (int) (ScreenHelper.getDisplayWidth() * 0.7);
    }

    /**
     * @param visible 进度是否显示
     */
    public void setLoadProgressVisible(boolean visible) {
        tvUpgrade.setVisibility(visible ? View.GONE : View.VISIBLE);
        mProgressBar.setVisibility(visible ? View.VISIBLE : View.GONE);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.tv_upgrade) {
            mProgressBar.setCurrentValue(0);
            setLoadProgressVisible(true);
            // 注册下载监听广播并开始下载
            mDownloadHelper.startDownload();
        } else if (v.getId() == R.id.iv_cancel) {
            if (mDownloadHelper.isRunningDownload()) {
                if (isCanCancel) {
                    mDownloadHelper.cancel();
                    dismiss();
                }
            } else {
                dismiss();
            }
        }
    }

    @Override
    public void onPrepared(DownloadManager.Request request) {
        // 默认在下载预备之前，隐藏通知栏的显示下载进度
        request.setNotificationVisibility(NotificationType.NOTIFICATION_HIDDEN);
        if (mOnRequestListener != null) mOnRequestListener.onRequest(request);
    }

    @Override
    public void onProgress(long fileSize, long soFar, int value) {
        // 下载进度
        mProgressBar.setCurrentValue(value);
    }

    @Override
    public void onSuccess(Uri uri, String mimeType) {
        dismiss();
    }

    @Override
    public void onFailed(String msg) {
        setLoadProgressVisible(false);
        showDialog("下载失败", msg);
    }

    private void showDialog(String title, String msg) {
        InfoDialog.newInstance(mActivity)
                .setTitle(title)
                .setMessage(msg)
                .setButtonText("确定")
                .setOnClickListener(new InfoDialog.OnInfoClickListener() {
                    @Override
                    public boolean onClick(InfoDialog dialog, boolean confirm) {
                        return true;
                    }
                }).show();
    }

    /**
     * @param newVersionCode 校验的新版本
     * @return 是否需要更新
     */
    private boolean verifyVersion(long newVersionCode) {
        if (newVersionCode == 0) {
            throw new NullPointerException("the new version code is 0, please set a new version code");
        }
        // 将当前安装版本和服务器版本进行比较. 判断是否需要更新
        return newVersionCode > OsHelper.getVersionCode(mActivity);
    }

    public interface OnRequestListener {
        void onRequest(DownloadManager.Request request);
    }
}
