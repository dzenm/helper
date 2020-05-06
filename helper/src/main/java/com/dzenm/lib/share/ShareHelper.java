package com.dzenm.lib.share;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;

import com.dzenm.lib.file.FileHelper;
import com.dzenm.lib.file.FileType;
import com.dzenm.lib.log.Logger;

import java.io.File;

/**
 * <pre>
 * 分享文本
 * ShareHelper.newInstance(this)
 *         .setText("分享到QQ")
 *         .share();
 *
 * 分享图片/视频/文件，分享文件一定需要先获取读写权限
 * ShareHelper.newInstance(this)
 *         .setFile("sogou/sga", "12.jpg")
 *         .share();
 * </pre>
 *
 * @author dinzhenyan
 * @date 2019-05-22 19:35
 */
public class ShareHelper {
    /**
     * 分享回掉的requestCode
     */
    public static final int REQUEST_CODE = 141;

    private Activity mActivity;

    /**
     * 分享的类型
     */
    private @FileType
    String mType = FileType.TEXT;

    /**
     * 分享的文本内容
     */
    private String mMessage;

    /**
     * 分享的图片/视频/文件的uri
     */
    private Uri mUri;

    @SuppressLint("StaticFieldLeak")
    private static volatile ShareHelper sShareHelper;

    private OnShareListener mOnShareListener;

    public static ShareHelper newInstance(Activity activity) {
        return new ShareHelper();
    }

    private ShareHelper() {
    }

    public static ShareHelper getInstance() {
        if (sShareHelper == null) synchronized (ShareHelper.class) {
            if (sShareHelper == null) sShareHelper = new ShareHelper();
        }
        return sShareHelper;
    }

    public ShareHelper with(Activity activity) {
        mActivity = activity;
        return this;
    }

    /**
     * 分享文本
     *
     * @param text
     * @return
     */
    public ShareHelper setText(String text) {
        mMessage = text;
        mType = FileType.TEXT;
        return this;
    }

    /**
     * 分享本地图片
     *
     * @param parent 根目录下的文件夹
     * @param child  文件名
     * @return
     */
    public ShareHelper setImage(String parent, String child) {
        setFile(parent, child, FileType.IMAGE);
        return this;
    }

    /**
     * 分享本地视频
     *
     * @param parent 根目录下的文件夹
     * @param child  文件名
     * @return
     */
    public ShareHelper setVideo(String parent, String child) {
        setFile(parent, child, FileType.VIDEO);
        return this;
    }

    /**
     * 分享本地文件
     *
     * @param parent 根目录下的文件夹
     * @param child  文件名
     * @return
     */
    public ShareHelper setFile(String parent, String child) {
        setFile(parent, child, FileType.FILE);
        return this;
    }

    /**
     * 分享文件
     *
     * @param parent 根目录下的文件夹
     * @param child  文件名
     * @param type   分享的类型
     * @return
     */
    private ShareHelper setFile(String parent, String child, @FileType String type) {
        File file = new File(Environment.getExternalStorageDirectory() + File.separator + parent, child);
        if (file.isDirectory()) {
            Logger.i("file is a directory");
            return this;
        }
        if (!file.exists()) {
            Logger.i("file is not exists");
            return this;
        }
        mUri = FileHelper.getInstance().getUri(file);
        Logger.d("share file's uri is: " + mUri);
        mType = type;
        return this;
    }

    /**
     * 分享
     *
     * @return
     */
    public void share() {
        Intent intent = new Intent(Intent.ACTION_SEND);
        // 指定发送内容的类型
        intent.setType(mType);
        setIntentExtra(intent);
        mActivity.startActivityForResult(Intent.createChooser(intent, "分享到"), REQUEST_CODE);
    }

    /**
     * 分享回掉监听
     *
     * @param onShareListener
     * @return
     */
    public ShareHelper setOnShareListener(OnShareListener onShareListener) {
        mOnShareListener = onShareListener;
        return this;
    }

    /**
     * 设置Intent发送的类型
     *
     * @param intent
     */
    private void setIntentExtra(Intent intent) {
        if (mType.equals(FileType.TEXT)) {
            // 比如发送文本形式的数据内容
            intent.putExtra(Intent.EXTRA_TEXT, mMessage);
        } else if (mType.equals(FileType.IMAGE) || mType.equals(FileType.VIDEO) || mType.equals(FileType.FILE)) {
            // 比如发送二进制文件数据流内容（比如图片、视频、音频文件等等） (EXTRA_STREAM 对于文件 Uri )
            intent.putExtra(Intent.EXTRA_STREAM, mUri);
            // 申请临时访问权限
            intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION |
                    Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
        }
    }

    public void onShareResult(int requestCode, int resultCode) {
        if (requestCode == REQUEST_CODE) {
            mOnShareListener.onResult(resultCode == Activity.RESULT_OK);
        }
    }

    public interface OnShareListener {
        void onResult(boolean isShare);
    }
}
