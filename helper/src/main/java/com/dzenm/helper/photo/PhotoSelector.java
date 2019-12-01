package com.dzenm.helper.photo;

import android.Manifest;
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
import androidx.appcompat.app.AppCompatActivity;

import com.dzenm.helper.date.DateHelper;
import com.dzenm.helper.file.FileHelper;
import com.dzenm.helper.file.FileType;
import com.dzenm.helper.log.Logger;
import com.dzenm.helper.os.OsHelper;
import com.dzenm.helper.permission.PermissionManager;

import java.io.File;
import java.lang.ref.WeakReference;
import java.util.List;

/**
 * @author dinzhenyan
 * @date 2019-04-30 20:03
 * <p>
 * 照片相关的工具类
 * <pre>
 * PhotoSelector.getInstance()
 *        .with(this)
 *        .setOnSelectPhotoListener(new PhotoSelector.OnSelectPhotoListener() {
 *            @Override
 *            public boolean onGallery(PhotoSelector selector, String filePath) {
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
 *  PhotoSelector.getInstance().onPhotoResult(requestCode, resultCode, data);
 * </pre>
 */
public class PhotoSelector {

    private final String TAG = PhotoSelector.class.getSimpleName() + "| ";

    @SuppressLint("FieldLeak")
    private static volatile PhotoSelector sPhotoSelector;
    private Activity mActivity;

    /**
     * 拍照后的图片Uri, 裁剪后的图片Uri
     */
    private Uri mGraphUri, mCropUri;

    private String mGraphFilePath;

    /**
     * 裁剪比例
     */
    private int mAspectX = 1, mAspectY = 1;

    /**
     * 裁剪输出大小
     */
    private int mOutputWidth = 800, mOutputHeight = 800;

    private OnSelectPhotoListener mOnSelectPhotoListener;

    private PhotoSelector() {
    }

    public static PhotoSelector getInstance() {
        if (sPhotoSelector == null) synchronized (PhotoSelector.class) {
            if (sPhotoSelector == null) sPhotoSelector = new PhotoSelector();
        }
        return sPhotoSelector;
    }

    public PhotoSelector with(Activity activity) {
        mActivity = activity;
        return this;
    }

    /**
     * 裁剪图片比例设置
     *
     * @param x 裁剪图片的长度
     * @param y 裁剪图片的宽度
     * @return this
     */
    public PhotoSelector ratio(int x, int y) {
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
    public PhotoSelector size(int width, int height) {
        mOutputWidth = width;
        mOutputHeight = height;
        return this;
    }

    public PhotoSelector setOnSelectPhotoListener(OnSelectPhotoListener onSelectPhotoListener) {
        mOnSelectPhotoListener = onSelectPhotoListener;
        return this;
    }

    /**
     * 打开相机拍照
     */
    public void camera() {
        PermissionManager.getInstance()
                .with((AppCompatActivity) mActivity)
                .load(Manifest.permission.READ_EXTERNAL_STORAGE)
                .into(new PermissionManager.OnPermissionListener() {
                    @Override
                    public void onPermit(boolean isGrant) {
                        if (isGrant) {
                            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                            File file = createTempFile("/photo");
                            if (intent.resolveActivity(mActivity.getPackageManager()) != null) {
                                mGraphUri = FileHelper.getInstance().getUri(file);
                                mGraphFilePath = file.getPath();
                                // 添加Uri读取权限
                                intent.addFlags(Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION);
                                intent.putExtra(MediaStore.EXTRA_OUTPUT, mGraphUri);
                                mActivity.startActivityForResult(intent, PhotoType.GRAPH);
                            } else {
                                mOnSelectPhotoListener.onError("打开相机失败");
                            }
                        } else {
                            mOnSelectPhotoListener.onError("未授予照相机权限");
                        }
                    }
                }).request();
    }

    /**
     * 打开图库选择图片
     */
    public void gallery() {
        PermissionManager.getInstance()
                .with((AppCompatActivity) mActivity)
                .load(Manifest.permission.READ_EXTERNAL_STORAGE)
                .into(new PermissionManager.OnPermissionListener() {
                    @Override
                    public void onPermit(boolean isGrant) {
                        if (isGrant) {
                            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                            intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, FileType.IMAGE);
                            mActivity.startActivityForResult(intent, PhotoType.GALLERY);
                        } else {
                            mOnSelectPhotoListener.onError("未获取存储权限");
                        }
                    }
                }).request();
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
        mActivity.startActivityForResult(intent, PhotoType.CROP);    //设置裁剪参数显示图片至ImageVie
    }

    /**
     * 裁剪图片为缩略图
     *
     * @param imageUri 图片的Uri
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
     * @param requestCode 请求时的标志位
     * @param resultCode  回调结果的判断标志
     * @param data        回调结果的数据
     */
    public void onPhotoResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == PhotoType.GRAPH && resultCode == Activity.RESULT_OK) {
            Logger.d(TAG + "拍照后的图片路径: " + mGraphFilePath);
            mActivity.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, mGraphUri));
            String file = FileHelper.getInstance().getRealFilePath(mGraphUri);
            Logger.d(TAG + "拍照后的图片uri转换路径: " + file);
            if (mOnSelectPhotoListener.onGraph(this, mGraphFilePath)) {
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
            mActivity.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, mCropUri));
            File file = new File(getRealFilePath(mCropUri));
            Logger.d(TAG + "裁剪后的图片路径: " + file.getAbsolutePath());
            if (mOnSelectPhotoListener.onCrop(this, file.getAbsolutePath())) {
                // 删除文件
                Logger.d(TAG + "删除裁剪图片的文件路径: " + file.getPath());
                FileHelper.getInstance().delete(file.getParent());
            }
        } else if (requestCode == PhotoType.GRAPH && resultCode == Activity.RESULT_CANCELED) {
            String path = FileHelper.getInstance().getRealFilePath(mGraphUri);
            FileHelper.getInstance().delete(new File(path).getParent());
            Logger.d(TAG + "回调失败: " + mGraphUri);
            mGraphUri = null;
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
        FileHelper.getInstance().delete(direct);
        return new File(direct, DateHelper.getCurrentTimeMillis() + ".jpeg");
    }

    /**
     * 按比例获取bitmap
     *
     * @param path 图片的路径
     * @param w    需要获取的比例宽
     * @param h    需要获取的比例长
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
     * @param path 图片的路径
     * @return Bitmap
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
     * @param uri 图片的Uri
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
        public boolean onGraph(PhotoSelector selector, String filePath) {
            return true;
        }

        @Override
        public boolean onGallery(PhotoSelector selector, String filePath) {
            return true;
        }

        @Override
        public boolean onCrop(PhotoSelector selector, String filePath) {
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
         * @param selector 用于图片的uri和路径之间的操作
         * @param filePath 图片所在的路径
         * @return 返回true表示进行默认的操作(裁剪), 返回false表示自定义拍照的操作
         */
        boolean onGraph(PhotoSelector selector, String filePath);

        /**
         * @param selector 用于图片的uri和路径之间的操作
         * @param filePath 图片的路径
         * @return 返回true表示进行默认的操作(裁剪), 返回false表示自定义选择图片的操作
         */
        boolean onGallery(PhotoSelector selector, String filePath);

        /**
         * @param selector 用于图片的uri和路径之间的操作
         * @param filePath 图片的路径
         * @return 是否删除裁剪之后的图片
         */
        boolean onCrop(PhotoSelector selector, String filePath);

        /**
         * 在选择过程中未到达预期的效果(也就是产生错误), 调用该方法
         *
         * @param msg 错误信息
         */
        void onError(String msg);
    }
}