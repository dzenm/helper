package com.dzenm.helper.dialog;

import android.view.Gravity;

import androidx.appcompat.app.AppCompatActivity;

import com.dzenm.helper.photo.PhotoHelper;

/**
 * @author dzenm
 * @date 2019-08-19 13:52
 * <pre>
 * PhotoDialog.newInstance(DrawableActivity.this).setOnSelectPhotoListener(new PhotoHelper.OnSelectPhotoListener() {
 *     @Override
 *     public boolean onGallery(PhotoHelper helper, String filePath) {
 *         layout.load(FileHelper.getInstance().getPhoto(filePath));
 *         return false;
 *     }
 *
 *     @Override
 *     public boolean onGraph(PhotoHelper helper, String filePath) {
 *         layout.load(FileHelper.getInstance().getPhoto(filePath));
 *         return false;
 *     }
 * }).show();
 * </pre>
 */
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
            PhotoHelper.getInstance().camera();
        } else if (tag.equals("图片")) {
            PhotoHelper.getInstance().gallery();
        }
    }
}
