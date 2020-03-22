package com.dzenm.helper.photo;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.dzenm.helper.file.FileHelper;
import com.dzenm.helper.file.FileType;
import com.dzenm.helper.log.Logger;
import com.dzenm.helper.os.OsHelper;
import com.dzenm.helper.permission.PermissionManager;

import java.io.File;
import java.util.List;

/**
 * @author dzenm
 * @date 2020/3/21 下午3:18
 */
public class PhotoFragment extends Fragment {

    private static final String TAG = PhotoFragment.class.getSimpleName() + "| ";
    private AppCompatActivity mActivity;
    private PhotoSelector mPhotoSelector;

    private Uri mGraphUri,                                  // 拍照后的图片Uri
            mCropUri;                                       // 裁剪后的图片Uri
    private String mGraphFilePath;
    private int mAspectX = 1, mAspectY = 1;                 // 裁剪比例
    private int mOutputWidth = 800, mOutputHeight = 800;    // 裁剪输出大小

    private PhotoSelector.OnSelectPhotoListener mOnSelectPhotoListener;
    // 嵌套在Fragment里使用的时候, 被嵌套的Fragment应该在当前Fragment结束之后在关闭
    private PhotoSelector.OnFinishListener mOnFinishListener;

    void with(PhotoSelector selector) {
        mPhotoSelector = selector;
    }

    /**
     * 裁剪图片比例设置
     *
     * @param x 裁剪图片的长度
     * @param y 裁剪图片的宽度
     */
    void ratio(int x, int y) {
        mAspectX = x;
        mAspectY = y;
    }

    /**
     * 输出图片的大小设置
     *
     * @param width  输出图片的宽度
     * @param height 输出图片的高度
     */
    void size(int width, int height) {
        mOutputWidth = width;
        mOutputHeight = height;
    }

    void setOnSelectPhotoListener(PhotoSelector.OnSelectPhotoListener onSelectPhotoListener) {
        mOnSelectPhotoListener = onSelectPhotoListener;
    }

    void setOnFinishListener(PhotoSelector.OnFinishListener onFinishListener) {
        mOnFinishListener = onFinishListener;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        mActivity = (AppCompatActivity) getActivity();
    }

    /**
     * 处理的回调结果
     *
     * @param requestCode 请求时的标志位
     * @param resultCode  回调结果的判断标志
     * @param data        回调结果的数据
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PhotoType.GRAPH && resultCode == Activity.RESULT_OK) {
            mActivity.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, mGraphUri));

            String file = FileHelper.getInstance().getRealFilePath(mGraphUri);
            Logger.d(TAG + "拍照后的图片uri转换路径: " + file);

            if (mOnSelectPhotoListener.onGraph(mPhotoSelector, mGraphFilePath)) {
                crop(mGraphUri);
            } else {
                mOnFinishListener.onFinish(PhotoType.GRAPH);
            }
        } else if (requestCode == PhotoType.GALLERY && resultCode == Activity.RESULT_OK) {
            if (data != null) {
                String path = getRealFilePath(data.getData());
                Logger.d(TAG + "选择图库后的图片路径: " + path);

                if (mOnSelectPhotoListener.onGallery(mPhotoSelector, path)) {
                    // 直接使用uri，在小米机型上出现打开直接崩溃
                    crop(FileHelper.getInstance().getUri(new File(path)));
                } else {
                    mOnFinishListener.onFinish(PhotoType.GALLERY);
                }
            } else {
                mOnSelectPhotoListener.onError("图片不存在");
                mOnFinishListener.onFinish(PhotoType.GALLERY);
            }
        } else if (requestCode == PhotoType.CROP && resultCode == Activity.RESULT_OK) {
            mActivity.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, mCropUri));

            File file = new File(getRealFilePath(mCropUri));
            Logger.d(TAG + "裁剪后的图片路径: " + file.getAbsolutePath());

            if (mOnSelectPhotoListener.onCrop(mPhotoSelector, file.getAbsolutePath())) {
                // 删除文件
                Logger.d(TAG + "删除裁剪图片的文件路径: " + file.getPath());
                FileHelper.getInstance().delete(file.getParent());
                mOnFinishListener.onFinish(PhotoType.CROP);
            } else {
                mOnFinishListener.onFinish(PhotoType.CROP);
            }
        }
    }

    /**
     * 打开相机拍照
     */
    void camera() {
        PermissionManager.getInstance()
                .with(mActivity)
                .load(Manifest.permission.READ_EXTERNAL_STORAGE)
                .into(new PermissionManager.OnPermissionListener() {
                    @Override
                    public void onPermit(boolean isGrant) {
                        if (isGrant) {
                            loadCamera();
                        } else {
                            mOnSelectPhotoListener.onError("未授予照相机权限");
                        }
                    }
                }).request();
    }

    private void loadCamera() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        File file = createTempFile("/photo");
        if (intent.resolveActivity(mActivity.getPackageManager()) != null) {
            mGraphUri = FileHelper.getInstance().getUri(file);
            mGraphFilePath = file.getPath();
            // 添加Uri读取权限
            intent.addFlags(Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, mGraphUri);
            this.startActivityForResult(intent, PhotoType.GRAPH);
        } else {
            mOnSelectPhotoListener.onError("打开相机失败");
        }
    }

    /**
     * 打开图库选择图片
     */
    void gallery() {
        PermissionManager.getInstance()
                .with(mActivity)
                .load(Manifest.permission.READ_EXTERNAL_STORAGE)
                .into(new PermissionManager.OnPermissionListener() {
                    @Override
                    public void onPermit(boolean isGrant) {
                        if (isGrant) {
                            loadGallery();
                        } else {
                            mOnSelectPhotoListener.onError("未获取存储权限");
                        }
                    }
                }).request();
    }

    private void loadGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, FileType.IMAGE);
        startActivityForResult(intent, PhotoType.GALLERY);
    }

    /**
     * 打开图片裁剪
     *
     * @param uri 裁剪图片的Uri
     */
    void crop(Uri uri) {
        Intent intent = new Intent("com.android.camera.action.CROP");

        // 7.0以上进行适配
        if (OsHelper.isNougat()) {
            intent.setDataAndType(uri, FileType.IMAGE);
            // 临时赋予裁剪前图片读写Uri的权限，访问需要裁剪的图片Uri的权限，不添加会提示错误
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION |
                    Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
        } else {
            intent.setDataAndType(uri, FileType.IMAGE);
        }

        intent.putExtra("scale", true);             // 是否保留比例
        intent.putExtra("circleCrop", true);        // 是否设置圆形裁剪区域
        intent.putExtra("crop", true);              // 是否设置圆形裁剪区域

        intent.putExtra("aspectX", mAspectX);
        intent.putExtra("aspectY", mAspectY);              // 设置裁剪区域的宽高比例
        intent.putExtra("outputX", mOutputWidth);
        intent.putExtra("outputY", mOutputHeight);        // 设置裁剪区域的宽度和高度

        intent.putExtra("noFaceDetection", true);   // 取消人脸识别
        intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString()); // 图片输出格式

        intent.putExtra("return-data", false);     // 不返回数据, 避免图片太大异常

        // 从相册中选择, 那么裁剪的图片保存在 (APP名/crop/) 中
        // 裁切后保存的URI, 不属于我们向外共享的, 所以可以使用fill://类型的URI
        // 文件夹为临时文件夹, 用于存放临时的裁剪图片文件, 裁剪完成之后会删除该文件
        mCropUri = Uri.fromFile(createTempFile("/crop"));
        intent.putExtra(MediaStore.EXTRA_OUTPUT, mCropUri);

        // 裁剪后图片的Uri权限，需要添加访问Uri权限的请求，否则会出现Uri权限请求拒绝的错误
        List resInfoList = mActivity.getPackageManager().queryIntentActivities(
                intent, PackageManager.MATCH_DEFAULT_ONLY);
        if (!resInfoList.isEmpty()) {
            for (Object object : resInfoList) {
                ResolveInfo resolveInfo = (ResolveInfo) object;
                String packageName = resolveInfo.activityInfo.packageName;
                mActivity.grantUriPermission(packageName, mCropUri, Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
            }
        }
        this.startActivityForResult(intent, PhotoType.CROP);    // 设置裁剪参数显示图片至ImageVie
    }

    private File createTempFile(String folder) {
        return FileHelper.getInstance().createTempFile(folder);
    }

    public String getRealFilePath(Uri uri) {
        return FileHelper.getInstance().getRealFilePath(uri);
    }

}
