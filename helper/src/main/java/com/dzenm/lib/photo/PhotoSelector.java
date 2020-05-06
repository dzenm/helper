package com.dzenm.lib.photo;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;

import com.dzenm.lib.file.FileHelper;
import com.dzenm.lib.log.Logger;

import java.io.File;
import java.lang.ref.WeakReference;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

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
    private PhotoFragment mFragment;

    private PhotoSelector() {
    }

    public static PhotoSelector getInstance() {
        if (sPhotoSelector == null) synchronized (PhotoSelector.class) {
            if (sPhotoSelector == null) sPhotoSelector = new PhotoSelector();
        }
        return sPhotoSelector;
    }

    public PhotoSelector with(AppCompatActivity activity) {
        Logger.d(TAG + activity.getClass().getSimpleName() + " is select image");
        return beginFragmentTransaction(activity.getSupportFragmentManager());
    }

    public PhotoSelector with(Fragment fragment) {
        Logger.d(TAG + fragment.getClass().getSimpleName() + " is select image");
        return beginFragmentTransaction(fragment.getChildFragmentManager());
    }

    private PhotoSelector beginFragmentTransaction(FragmentManager manager) {
        if (mFragment == null) {
            mFragment = new PhotoFragment();
            mFragment.with(this);
        }

        if (!mFragment.isAdded()) {
            manager.beginTransaction()
                    .add(mFragment, mFragment.toString())
                    .commitNow();
        }
        return this;
    }

    public PhotoSelector ratio(int x, int y) {
        mFragment.ratio(x, y);
        return this;
    }

    public PhotoSelector size(int width, int height) {
        mFragment.size(width, height);
        return this;
    }

    public PhotoSelector setOnSelectedPhotoListener(OnSelectedPhotoListener onSelectPhotoListener) {
        mFragment.setOnSelectedPhotoListener(onSelectPhotoListener);
        return this;
    }

    public PhotoSelector setOnFinishListener(OnFinishListener listener) {
        mFragment.setOnFinishListener(listener);
        return this;
    }

    public void cameraVideo() {
        mFragment.cameraVideo();
    }

    public void camera() {
        mFragment.camera();
    }

    public void galleryVideo() {
        mFragment.galleryVideo();
    }

    public void gallery() {
        mFragment.gallery();
    }

    public void crop(Uri uri) {
        mFragment.crop(uri);
    }

    /**
     * 裁剪图片为缩略图
     *
     * @param imageUri 图片的Uri
     * @return this
     */
    public Bitmap convertToBitmap(Activity activity, Uri imageUri) {
        String[] filePathColumns = {MediaStore.Images.Media.DATA};
        Cursor cursor = activity.getContentResolver().query(imageUri, filePathColumns, null, null, null);
        assert cursor != null;
        cursor.moveToFirst();
        int columnIndex = cursor.getColumnIndex(filePathColumns[0]);
        String imagePath = cursor.getString(columnIndex);
        cursor.close();
        // 设置参数
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
     * 按比例获取bitmap
     *
     * @param path 图片的路径
     * @param w    需要获取的比例宽
     * @param h    需要获取的比例长
     * @return Bitmap
     */
    public Bitmap convertToBitmap(String path, int w, int h) {
        BitmapFactory.Options opts = new BitmapFactory.Options();
        // 设置为 true 仅仅获取图片大小
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
        // 设置为 true 仅仅获取图片大小
        opts.inJustDecodeBounds = true;
        opts.inPreferredConfig = Bitmap.Config.ARGB_8888;
        // 返回为空
        return BitmapFactory.decodeFile(path, opts);
    }

    /**
     * 通过Uri删除图片
     *
     * @param uri 图片的Uri
     */
    public void deleteBitmapByUri(Activity activity, Uri uri) {
        if (uri.toString().startsWith("content://")) {
            // content://开头的Uri
            activity.getContentResolver().delete(uri, null, null);
        } else {
            File file = new File(FileHelper.getInstance().getRealFilePath(uri));
            if (file.exists() && file.isFile()) file.delete();
        }
    }

    public static abstract class OnSelectedPhotoListener  {

        public void onGraphVideo(PhotoSelector selector, VideoBean videoBean) {

        }

        /**
         * 照相回调
         *
         * @param selector 用于图片的uri和路径之间的操作
         * @param filePath 图片所在的路径
         * @return 返回true表示进行默认的操作(裁剪), 返回false表示自定义拍照的操作
         */
        public boolean onGraph(PhotoSelector selector, String filePath) {
            return true;
        }

        public void onGalleryVideo(PhotoSelector selector, VideoBean videoBean) {

        }

        /**
         * @param selector 用于图片的uri和路径之间的操作
         * @param filePath 图片的路径
         * @return 返回true表示进行默认的操作(裁剪), 返回false表示自定义选择图片的操作
         */
        public boolean onGallery(PhotoSelector selector, String filePath) {
            return true;
        }

        /**
         * @param selector 用于图片的uri和路径之间的操作
         * @param filePath 图片的路径
         * @return 是否删除裁剪之后的图片
         */
        public boolean onCrop(PhotoSelector selector, String filePath) {
            return true;
        }

        /**
         * 在选择过程中未到达预期的效果(也就是产生错误), 调用该方法
         *
         * @param msg 错误信息
         */
        public void onError(String msg) {

        }
    }

    public interface OnFinishListener {
        void onFinish(int type);
    }
}