package com.dzenm.helper.dialog;

import android.view.Gravity;

import androidx.appcompat.app.AppCompatActivity;

import com.dzenm.helper.photo.PhotoSelector;

/**
 * @author dzenm
 * @date 2019-08-19 13:52
 * <pre>
 * PhotoDialog.newInstance(DrawableActivity.this).setOnSelectPhotoListener(new PhotoSelector.OnSelectPhotoListener() {
 *     @Override
 *     public boolean onGallery(PhotoSelector helper, String filePath) {
 *         layout.load(FileHelper.getInstance().getPhoto(filePath));
 *         return false;
 *     }
 *
 *     @Override
 *     public boolean onGraph(PhotoSelector helper, String filePath) {
 *         layout.load(FileHelper.getInstance().getPhoto(filePath));
 *         return false;
 *     }
 * }).show();
 * </pre>
 */
public class PhotoDialog extends MenuDialog implements MenuDialog.OnItemClickListener {

    private PhotoSelector.OnSelectPhotoListener mOnSelectPhotoListener;

    public PhotoDialog(AppCompatActivity activity) {
        super(activity);
    }

    public static PhotoDialog newInstance(AppCompatActivity activity) {
        return new PhotoDialog(activity);
    }

    public PhotoDialog setOnSelectPhotoListener(PhotoSelector.OnSelectPhotoListener onSelectPhotoListener) {
        mOnSelectPhotoListener = onSelectPhotoListener;
        return this;
    }

    @Override
    protected void initView() {
        setItem("拍照", "图片", "取消");
        setGravity(Gravity.BOTTOM);
        setDivide(true);
        setOnItemClickListener(this);
        PhotoSelector.getInstance().with(mActivity)
                .setOnSelectPhotoListener(mOnSelectPhotoListener);
        super.initView();
    }

    @Override
    public void onItemClick(int position) {
        if (position == 0) {
            PhotoSelector.getInstance().camera();
        } else if (position == 1) {
            PhotoSelector.getInstance().gallery();
        }
    }
}
