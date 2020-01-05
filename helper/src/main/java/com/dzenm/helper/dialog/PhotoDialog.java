package com.dzenm.helper.dialog;

import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.dzenm.helper.photo.PhotoSelector;

/**
 * @author dzenm
 * @date 2019-08-19 13:52
 * <pre>
 * PhotoDialog.newInstance(this).setOnSelectPhotoListener(new PhotoSelector.OnSelectPhotoListener() {
 *     @Override
 *     public boolean onGallery(PhotoSelector selector, String filePath) {
 *         layout.load(FileHelper.getInstance().getPhoto(filePath));
 *         return false;
 *     }
 *     @Override
 *     public boolean onGraph(PhotoSelector selector, String filePath) {
 *         layout.load(FileHelper.getInstance().getPhoto(filePath));
 *         return false;
 *     }
 *     @Override
 *     public boolean onCrop(PhotoSelector helper, String filePath) {
 *         logD("the filePath: " + filePath);
 *         String file = FileHelper.getInstance().getPath("/photo") + "/d.jpeg";
 *         FileHelper.getInstance().copyFile(filePath, file);
 *         FileHelper.getInstance().refreshGallery(filePath);
 *         return true;
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
    protected void initView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState2
    ) {
        setItem("拍照", "图片", "取消");
        setGravity(Gravity.BOTTOM);
        setDivide(true);
        setOnItemClickListener(this);
        PhotoSelector.getInstance().with(mActivity)
                .setOnSelectPhotoListener(mOnSelectPhotoListener);
        super.initView(inflater, container, savedInstanceState2);
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
