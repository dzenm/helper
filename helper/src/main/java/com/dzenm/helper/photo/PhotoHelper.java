package com.dzenm.helper.photo;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;

import androidx.annotation.Nullable;

import com.dzenm.helper.file.FileHelper;
import com.dzenm.helper.file.FileType;
import com.dzenm.helper.log.Logger;
import com.dzenm.helper.os.OsHelper;

import java.io.File;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.List;

/**
 * @author dinzhenyan
 * @date 2019-04-30 20:03
 * <p>
 * 照片相关的工具类
 * <pre>
 * PhotoHelper.getInstance()
 *        .with(this)
 *        .setOnSelectPhotoListener(new PhotoHelper.OnSelectPhotoListener() {
 *            @Override
 *            public boolean onGallery(PhotoHelper helper, String filePath) {
 *                InfoDialog.newInstance(MainActivity.this)
 *                        .setTitle("图片选择回调")
 *                        .setMessage(filePath)
 *                        .setOnDialogClickListener(null)
 *                        .show();
 *                binding.ivImage.setImageBitmap(FileHelper.getInstance().getPhoto(filePath));
 *                return false;
 *            }
 *
 *            @Override
 *            public void onError(String msg) {
 *                super.onError(msg);
 *            }
 *        }).selectGallery();
 *
 *  重写onActivityResult方法
 *  PhotoHelper.getInstance().onPhotoResult(requestCode, resultCode, data);
 * </pre>
 */
public class PhotoHelper {

    private final String TAG = PhotoHelper.class.getSimpleName() + "|";

    @SuppressLint("FieldLeak")
    private static volatile PhotoHelper sPhotoHelper;
    private Activity mActivity;

    /**
     * 拍照后的图片Uri, 裁剪后的图片Uri
     */
    private Uri mGraphUri, mCropUri;

    /**
     * 裁剪比例
     */
    private int mAspectX = 1, mAspectY = 1;

    /**
     * 裁剪输出大小
     */
    private int mOutputWidth = 800, mOutputHeight = 800;

    private OnSelectPhotoListener mOnSelectPhotoListener;

    private PhotoHelper() {
    }

    public static PhotoHelper getInstance() {
        if (sPhotoHelper == null) synchronized (PhotoHelper.class) {
            if (sPhotoHelper == null) sPhotoHelper = new PhotoHelper();
        }
        return sPhotoHelper;
    }

    public PhotoHelper with(Activity activity) {
        mActivity = activity;
        mAspectX = mAspectY = mOutputWidth = mOutputHeight = 0;
        return this;
    }

    /**
     * 裁剪图片比例设置
     *
     * @param x 裁剪图片的长度
     * @param y 裁剪图片的宽度
     * @return this
     */
    public PhotoHelper ratio(int x, int y) {
        mAspectX = x;
        mAspectY = y;
        return this;
    }

    /**
     * 输出图片的大小设置
     *
     * @param width  输出图片的宽度
     * @param height 输出图片的高度
     * @return this
     */
    public PhotoHelper size(int width, int height) {
        mOutputWidth = width;
        mOutputHeight = height;
        return this;
    }

    public PhotoHelper setOnSelectPhotoListener(OnSelectPhotoListener onSelectPhotoListener) {
        mOnSelectPhotoListener = onSelectPhotoListener;
        return this;
    }

    /**
     * 打开相机拍照
     */
    public void camera() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        File file = createTempFile("/photo");
        if (file == null) return;
        if (intent.resolveActivity(mActivity.getPackageManager()) != null) {
            mGraphUri = FileHelper.getInstance().getUri(file);
            // 添加Uri读取权限
            intent.addFlags(Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, mGraphUri);
            mActivity.startActivityForResult(intent, PhotoType.GRAPH);
        } else {
            mOnSelectPhotoListener.onError("打开相机失败");
        }
    }

    /**
     * 打开图库选择图片
     */
    public void gallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, FileType.IMAGE);
        mActivity.startActivityForResult(intent, PhotoType.GALLERY);
    }

    /**
     * 打开图片裁剪
     *
     * @param uri 裁剪图片的Uri
     */
    public void crop(Uri uri) {
        Intent intent = new Intent("com.android.camera.action.CROP");

        // 7.0以上进行适配
        if (OsHelper.isNougat()) {
            intent.setDataAndType(uri, FileType.IMAGE);
            // 临时赋予裁剪前图片读写Uri的权限，访问需要裁剪的图片Uri的权限，不添加会提示错误
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
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

        // 从相册中选择, 那么裁剪的图片保存在 (包名/crop/) 中
        // 裁切后保存的URI, 不属于我们向外共享的, 所以可以使用fill://类型的URI
        // 文件夹为临时文件夹, 用于存放临时的裁剪图片文件, 裁剪完成之后会删除该文件
        mCropUri = Uri.fromFile(createTempFile("/crop"));
        intent.putExtra(MediaStore.EXTRA_OUTPUT, mCropUri);

        // 以广播方式刷新系统相册，以便能够在相册中找到刚刚所拍摄和裁剪的照片
        Intent intentBc = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        intentBc.setData(mCropUri);
        mActivity.sendBroadcast(intentBc);

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
        mActivity.startActivityForResult(intent, PhotoType.CROP);    //设置裁剪参数显示图片至ImageVie
    }

    /**
     * 裁剪图片为缩略图
     *
     * @param imageUri
     * @return this
     */
    public Bitmap convertToBitmap(Uri imageUri) {
        String[] filePathColumns = {MediaStore.Images.Media.DATA};
        Cursor cursor = mActivity.getContentResolver().query(imageUri, filePathColumns, null, null, null);
        assert cursor != null;
        cursor.moveToFirst();
        int columnIndex = cursor.getColumnIndex(filePathColumns[0]);
        String imagePath = cursor.getString(columnIndex);
        cursor.close();
        //  设置参数
        BitmapFactory.Options options = new BitmapFactory.Options();
        // 只获取图片的大小信息，而不是将整张图片载入在内存中，避免内存溢出
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(imagePath, options);
        int height = options.outHeight;
        int width = options.outWidth;
        int inSampleSize = 2;                       // 默认像素压缩比例，压缩为原图的1/2
        int minLen = Math.min(height, width);       // 原图的最小边长
        if (minLen > 100) {                         // 如果原始图像的最小边长大于100dp（此处单位我认为是dp，而非px）
            float ratio = (float) minLen / 100.0f;  // 计算像素压缩比例
            inSampleSize = (int) ratio;
        }
        options.inJustDecodeBounds = false;         // 计算好压缩比例后，这次可以去加载原图了
        options.inSampleSize = inSampleSize;        // 设置为刚才计算的压缩比例
        return BitmapFactory.decodeFile(imagePath, options);
    }

    /**
     * 处理的回调结果
     *
     * @param requestCode
     * @param resultCode
     * @param data
     */
    public void onPhotoResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == PhotoType.GRAPH && resultCode == Activity.RESULT_OK) {
            Logger.d(TAG + "拍照后的图片uri: " + mGraphUri);
            if (mOnSelectPhotoListener.onGraph(this, mGraphUri)) {
                crop(mGraphUri);
            }
        } else if (requestCode == PhotoType.GALLERY && resultCode == Activity.RESULT_OK) {
            if (data != null) {
                String path = getRealFilePath(data.getData());
                Logger.d(TAG + "选择图库后的图片路径: " + path);
                if (mOnSelectPhotoListener.onGallery(this, path)) {
                    // 直接使用uri，在小米机型上出现打开直接崩溃
                    crop(FileHelper.getInstance().getUri(new File(path)));
                }
            } else {
                mOnSelectPhotoListener.onError("图片不存在");
            }
        } else if (requestCode == PhotoType.CROP && resultCode == Activity.RESULT_OK) {
            Logger.d(TAG + "裁剪后的图片uri: " + mCropUri);
            if (mOnSelectPhotoListener.onCrop(this, mCropUri)) {
                // 删除文件
                String filePath = getRealFilePath(mCropUri);
                File file = new File(filePath);
                Logger.d(TAG + "删除裁剪图片的文件路径: " + file.getPath());
                FileHelper.getInstance().delete(file.getParent());
            }
        } else if (resultCode == Activity.RESULT_CANCELED) {
            Logger.d(TAG + "回调失败: " + mGraphUri + mCropUri);
            mGraphUri = mCropUri = null;
            Logger.d(TAG + "回调失败: " + mGraphUri + mCropUri);
        }
    }

    /**
     * 创建临时文件
     *
     * @return File
     */
    private File createTempFile(String folder) {
        File direct = FileHelper.getInstance().getFolder(folder);
        if (!direct.exists()) direct.mkdirs();
        try {
            return File.createTempFile("temp_", ".jpeg", direct);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 按比例获取bitmap
     *
     * @param path
     * @param w
     * @param h
     * @return Bitmap
     */
    public Bitmap convertToBitmap(String path, int w, int h) {
        BitmapFactory.Options opts = new BitmapFactory.Options();
        // 设置为ture仅仅获取图片大小
        opts.inJustDecodeBounds = true;
        opts.inPreferredConfig = Bitmap.Config.ARGB_8888;
        BitmapFactory.decodeFile(path, opts);
        int width = opts.outWidth;
        int height = opts.outHeight;
        float scaleWidth = 0.f, scaleHeight = 0.f;
        if (width > w || height > h) {
            // 缩放
            scaleWidth = ((float) width) / w;
            scaleHeight = ((float) height) / h;
        }
        opts.inJustDecodeBounds = false;
        float scale = Math.max(scaleWidth, scaleHeight);
        opts.inSampleSize = (int) scale;
        WeakReference<Bitmap> weak = new WeakReference<Bitmap>(BitmapFactory.decodeFile(path, opts));
        return Bitmap.createScaledBitmap(weak.get(), w, h, true);
    }

    /**
     * 获取原图bitmap
     *
     * @param path
     * @return
     */
    public Bitmap convertToBitmap(String path) {
        BitmapFactory.Options opts = new BitmapFactory.Options();
        // 设置为ture仅仅获取图片大小
        opts.inJustDecodeBounds = true;
        opts.inPreferredConfig = Bitmap.Config.ARGB_8888;
        // 返回为空
        BitmapFactory.decodeFile(path, opts);
        return BitmapFactory.decodeFile(path, opts);
    }

    /**
     * 通过Uri删除图片
     *
     * @param uri
     */
    public void deleteUriBitmap(Uri uri) {
        if (uri.toString().startsWith("content://")) {
            // content://开头的Uri
            mActivity.getContentResolver().delete(uri, null, null);
        } else {
            File file = new File(getRealFilePath(uri));
            if (file.exists() && file.isFile()) file.delete();
        }
    }

    /**
     * 通过uri获取真实的路径
     *
     * @param uri 文件的uri
     * @return 文件的路径
     */
    public String getRealFilePath(Uri uri) {
        return FileHelper.getInstance().getRealFilePath(uri);
    }

    public static abstract class OnSelectPhotoListener implements OnPhotoListener {

        @Override
        public boolean onGraph(PhotoHelper helper, Uri uri) {
            return true;
        }

        @Override
        public boolean onGallery(PhotoHelper helper, String filePath) {
            return true;
        }

        @Override
        public boolean onCrop(PhotoHelper helper, Uri uri) {
            return true;
        }

        @Override
        public void onError(String msg) {

        }
    }

    public interface OnPhotoListener {

        /**
         * 照相回调
         *
         * @param helper 用于图片的uri和路径之间的操作
         * @param uri    图片所在的Uri
         * @return 返回true表示进行默认的操作(裁剪), 返回false表示自定义拍照的操作
         */
        boolean onGraph(PhotoHelper helper, Uri uri);

        /**
         * @param helper   用于图片的uri和路径之间的操作
         * @param filePath 图片的路径
         * @return 返回true表示进行默认的操作(裁剪), 返回false表示自定义选择图片的操作
         */
        boolean onGallery(PhotoHelper helper, String filePath);

        /**
         * @param helper 用于图片的uri和路径之间的操作
         * @param uri    图片的路径
         * @return 是否删除裁剪之后的图片
         */
        boolean onCrop(PhotoHelper helper, Uri uri);

        /**
         * 在选择过程中未到达预期的效果(也就是产生错误), 调用该方法
         *
         * @param msg 错误信息
         */
        void onError(String msg);
    }
}