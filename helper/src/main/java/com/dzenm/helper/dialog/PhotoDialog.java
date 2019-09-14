package com.dzenm.helper.dialog;

import android.annotation.SuppressLint;
import androidx.appcompat.app.AppCompatActivity;
import android.view.Gravity;

import com.dzenm.helper.file.PhotoHelper;

/**
 * @author dzenm
 * @date 2019-08-19 13:52
 * <pre>
 *      PhotoDialog.newInstance(this)
 *                     .setOnSelectPhotoListener(new PhotoHelper.OnSelectPhotoListener() {
 *                         @Override
 *                         public void onError(String msg) {
 *                             super.onError(msg);
 *                         }
 *
 *                         @Override
 *                         public boolean onCrop(PhotoHelper helper, Uri uri) {
 *                             binding.ivImage.setImageBitmap(helper.getPhoto(uri));
 *                             String filePath = helper.getRealFilePath(uri);
 *                             String file = FileHelper.getInstance().getPath("/photo") + "/d.jpeg";
 *                             Logger.d(TAG + "copy file path: " + file);
 *                             FileHelper.getInstance().copyFile(filePath, file);
 *                             return false;
 *                         }
 *                     }).show();
 * </pre>
 */
@SuppressLint("ValidFragment")
public class PhotoDialog extends MenuDialog implements MenuDialog.OnItemClickListener {

    private PhotoHelper.OnSelectPhotoListener mOnSelectPhotoListener;

    public PhotoDialog(AppCompatActivity activity) {
        super(activity);
    }

    public static PhotoDialog newInstance(AppCompatActivity activity) {
        return new PhotoDialog(activity);
    }

    public PhotoDialog setOnSelectPhotoListener(PhotoHelper.OnSelectPhotoListener onSelectPhotoListener) {
        mOnSelectPhotoListener = onSelectPhotoListener;
        return this;
    }

    @Override
    protected void initView() {
        setItem("拍照", "图片", "取消");
        setGravity(Gravity.BOTTOM);
        setDivide(true);
        setOnItemClickListener(this);
        PhotoHelper.getInstance().with(mActivity)
                .setOnSelectPhotoListener(mOnSelectPhotoListener);
        super.initView();
    }

    @Override
    public void onItemClick(Object tag) {
        if (tag.equals("拍照")) {
            PhotoHelper.getInstance().selectCamera();
        } else if (tag.equals("图片")) {
            PhotoHelper.getInstance().selectGallery();
        }
    }
}
