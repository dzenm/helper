package com.dzenm.lib.material;

import android.app.DownloadManager;
import android.graphics.Color;
import android.net.Uri;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.dzenm.lib.R;
import com.dzenm.lib.animator.AnimatorHelper;
import com.dzenm.lib.download.DownloadHelper;
import com.dzenm.lib.download.NotificationType;
import com.dzenm.lib.drawable.DrawableHelper;
import com.dzenm.lib.os.OsHelper;
import com.dzenm.lib.view.ProgressBar;
import com.dzenm.lib.view.RatioImageView;

/**
 * @author dzenm
 * @date 2020/4/11 21:23
 * @IDE Android Studio
 */
public class UpGradeView implements IContentView, DownloadHelper.OnDownloadListener {

    private DialogDelegate mD;

    /**
     * 顶部的图片, 自定义图片
     */
    private int mUpgradeImage;

    /**
     * 更新下载的新版本名称 {@link #setVersionName(String)}, 更新下载文件的大小 {@link #setSize(String)}
     * 更新的内容 {@link #setDesc(String)}
     */
    private String mVersionName, mSize, mDesc;

    /**
     * 下载的进度条显示的View {@link ProgressBar}
     */
    private ProgressBar mProgressBar;

    /**
     * 升级按钮
     */
    private TextView mUpgradeButton;

    /**
     * 判断是否可以通过点击取消按钮取消下载
     */
    private boolean isCanCancel;

    /**
     * 判断是否需要更新, 根据当前versionCode与newVersionCode进行对比
     */
    private boolean isUpdate;

    /**
     * 下载管理器 {@link DownloadHelper}
     */
    private DownloadHelper mDownloadHelper;

    public UpGradeView(AppCompatActivity activity) {
        mDownloadHelper = new DownloadHelper(activity);
        mDownloadHelper.setOnDownloadListener(this);
    }

    /**
     * @param url 下载的url
     * @return this
     */
    public UpGradeView setUrl(String url) {
        mDownloadHelper.setUrl(url);
        return this;
    }

    /**
     * @param filePath 存储的文件路径
     * @return this
     */
    public UpGradeView setFilePath(String filePath) {
        mDownloadHelper.setFilePath(filePath);
        return this;
    }

    /**
     * @param versionName 下载的新版本名称
     * @return this
     */
    public UpGradeView setVersionName(@NonNull String versionName) {
        mDownloadHelper.setVersionName(versionName);
        mVersionName = versionName;
        return this;
    }

    /**
     * @param size 下载的安装包大小
     * @return this
     */
    public UpGradeView setSize(@NonNull String size) {
        mSize = size;
        return this;
    }

    /**
     * @param desc 下载新版本的更新内容
     * @return this
     */
    public UpGradeView setDesc(@NonNull String desc) {
        mDesc = desc;
        return this;
    }

    public UpGradeView setCanCancel(boolean canCancel) {
        isCanCancel = canCancel;
        return this;
    }

    public UpGradeView setUpgradeImage(int upgradeImage) {
        mUpgradeImage = upgradeImage;
        return this;
    }

    /**
     * @param newVersionCode 服务器上的最新版本号, 当服务器上最新版本高于当前安装的版本号时,会提示更新
     * @return this
     */
    public UpGradeView setNewVersionCode(long newVersionCode) {
        isUpdate = verifyVersion(newVersionCode);
        return this;
    }

    @Override
    public View onCreateView(DialogDelegate delegate) {
        mD = delegate;
        View decorView = createView();

        mD.isMaterialDesign = false;
        mD.mAnimator = AnimatorHelper.overshoot();
        mD.mBackgroundColor = Color.TRANSPARENT;

        return decorView;
    }

    private View createView() {
        // root view
        LinearLayout decorLayout = new LinearLayout(mD.mActivity);
        decorLayout.setOrientation(LinearLayout.VERTICAL);
        decorLayout.setGravity(Gravity.CENTER);

        int bigPadding = OsHelper.dp2px(16);
        int midPadding = OsHelper.dp2px(12);

        // content view
        LinearLayout contentView = new LinearLayout(mD.mActivity);
        contentView.setOrientation(LinearLayout.VERTICAL);
        contentView.setLayoutParams(new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT
        ));
        contentView.setPadding(bigPadding, bigPadding, bigPadding, bigPadding);

        // title layout with info, version and size text
        LinearLayout titleLayout = new LinearLayout(mD.mActivity);
        titleLayout.setOrientation(LinearLayout.HORIZONTAL);
        titleLayout.setLayoutParams(new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT
        ));
        TextView infoTextView = new TextView(mD.mActivity);
        infoTextView.setLayoutParams(new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT
        ));
        infoTextView.setText(mD.mActivity.getText(R.string.dialog_version_title));
        infoTextView.setTextColor(mD.mPrimaryTextColor);
        infoTextView.setTextSize(18);
        infoTextView.setMaxLines(1);
        infoTextView.setEllipsize(TextUtils.TruncateAt.END);
        TextView versionTextView = new TextView(mD.mActivity);
        versionTextView.setLayoutParams(new LinearLayout.LayoutParams(
                0, ViewGroup.LayoutParams.WRAP_CONTENT, 1
        ));
        versionTextView.setTextColor(mD.mPrimaryTextColor);
        versionTextView.setMaxLines(1);
        versionTextView.setEllipsize(TextUtils.TruncateAt.END);
        setText(versionTextView, mVersionName);
        TextView sizeTextView = new TextView(mD.mActivity);
        sizeTextView.setLayoutParams(new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT
        ));
        sizeTextView.setTextColor(mD.mPrimaryTextColor);
        sizeTextView.setMaxLines(1);
        sizeTextView.setEllipsize(TextUtils.TruncateAt.END);
        setText(sizeTextView, mSize);

        titleLayout.addView(infoTextView);
        titleLayout.addView(versionTextView);
        titleLayout.addView(sizeTextView);

        // desc文本
        TextView descTextView = new TextView(mD.mActivity);
        LinearLayout.LayoutParams descParams = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT
        );
        descParams.topMargin = descParams.bottomMargin = midPadding;
        descTextView.setLayoutParams(descParams);
        setText(descTextView, mDesc);

        int buttonHeight = OsHelper.dp2px(40);
        FrameLayout buttonLayout = new FrameLayout(mD.mActivity);
        buttonLayout.setLayoutParams(new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, buttonHeight
        ));
        buttonLayout.setForegroundGravity(Gravity.CENTER);

        // 升级按钮
        mUpgradeButton = new TextView(mD.mActivity);
        mUpgradeButton.setLayoutParams(new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT
        ));
        mUpgradeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mProgressBar.setCurrentValue(0);
                setLoadProgressVisible(true);
                // 注册下载监听广播并开始下载
                mDownloadHelper.download();
            }
        });
        mUpgradeButton.setGravity(Gravity.CENTER);
        mUpgradeButton.setText(mD.mActivity.getText(R.string.dialog_up_grade));
        DrawableHelper.radius(mD.mBackgroundRadius)
                .pressed(mD.mPrimaryColor, mD.mInactiveColor)
                .textColor(android.R.color.white, mD.mPrimaryColor)
                .into(mUpgradeButton);

        // 进度条
        mProgressBar = new ProgressBar(mD.mActivity);
        mProgressBar.setLayoutParams(new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT
        ));
        mProgressBar.setVisibility(View.GONE);
        mProgressBar.setProgressColor(mD.mPrimaryColor);

        buttonLayout.addView(mUpgradeButton);
        buttonLayout.addView(mProgressBar);

        contentView.addView(titleLayout);
        contentView.addView(descTextView);
        contentView.addView(buttonLayout);

        float rad = mD.mBackgroundRadius;
        float[] radii;
        if (mUpgradeImage != 0) {
            // 顶部的图片, 如果未设置则不显示
            ImageView topImageView = new ImageView(mD.mActivity);
            topImageView.setLayoutParams(new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT
            ));
            topImageView.setScaleType(ImageView.ScaleType.FIT_END);
            topImageView.setImageResource(mUpgradeImage);

            // 底部的取消按钮随着顶部的图片显示
            RatioImageView cancelImageView = new RatioImageView(mD.mActivity);
            cancelImageView.setLayoutParams(new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT
            ));
            cancelImageView.setImageResource(R.drawable.ic_upgrade_cancel);
            cancelImageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mDownloadHelper.isRunningDownload()) {
                        if (isCanCancel) {
                            mDownloadHelper.cancel();
                            mD.mDialogFragment.dismiss();
                        }
                    } else {
                        mD.mDialogFragment.dismiss();
                    }
                }
            });
            decorLayout.addView(topImageView);
            decorLayout.addView(contentView);
            decorLayout.addView(cancelImageView);
            radii = new float[]{0, 0, rad, rad};
        } else {
            decorLayout.addView(contentView);
            radii = new float[]{rad, rad, rad, rad};
        }
        DrawableHelper.solid(mD.mBackgroundColor)
                .radius(radii)
                .into(contentView);

        return decorLayout;
    }

    private void setText(TextView textView, String text) {
        if (TextUtils.isEmpty(text)) {
            textView.setVisibility(View.GONE);
        } else {
            textView.setText(text);
            textView.setTextColor(mD.mSecondaryTextColor);
        }
    }

    /**
     * @param visible 进度是否显示
     */
    public void setLoadProgressVisible(boolean visible) {
        mUpgradeButton.setVisibility(visible ? View.GONE : View.VISIBLE);
        mProgressBar.setVisibility(visible ? View.VISIBLE : View.GONE);
    }

    @Override
    public void onPrepared(DownloadManager.Request request) {
        // 默认在下载预备之前，隐藏通知栏的显示下载进度
        request.setNotificationVisibility(NotificationType.NOTIFICATION_HIDDEN);
    }

    @Override
    public void onProgress(long fileSize, long soFar, int value) {
        // 下载进度
        mProgressBar.setCurrentValue(value);
    }

    @Override
    public void onSuccess(Uri uri, String mimeType) {
        mD.mDialogFragment.dismiss();
    }

    @Override
    public void onFailed(String msg) {
        setLoadProgressVisible(false);
        Toast.makeText(mD.mActivity, "下载失败", Toast.LENGTH_SHORT);
    }

    /**
     * 根据当前versionCode与newVersionCode进行比较, 当服务器上最新版本高于当前安装的版本号时, 返回 true
     *
     * @param newVersionCode 服务器上的最新版本号
     * @return 是否需要更新
     */
    public boolean verifyVersion(long newVersionCode) {
        if (newVersionCode == 0) {
            throw new NullPointerException("the new version code is 0, please set a new version code");
        }
        // 将当前安装版本和服务器版本进行比较. 判断是否需要更新
        return newVersionCode > OsHelper.getVersionCode(mD.mActivity);
    }
}
