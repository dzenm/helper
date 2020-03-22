package com.dzenm.helper.dialog;

import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
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
public class PhotoDialog extends MenuDialog implements MenuDialog.OnItemClickListener, PhotoSelector.OnFinishListener {

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
    protected View inflater(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        setItem("拍照", "图片", "取消");
        setGravity(Gravity.BOTTOM);
        setDivide(true);
        setOnItemClickListener(this);
        return super.inflater(inflater, container, savedInstanceState);
    }

    @Override
    protected void initView() {
        PhotoSelector.getInstance().with(this)
                .setOnSelectPhotoListener(mOnSelectPhotoListener)
                .setOnFinishListener(this);
    }

    @Override
    protected boolean onClick(int position) {
        super.onClick(position);
        return false;
    }

    @Override
    public void onItemClick(int position) {
        if (position == 0) {
            PhotoSelector.getInstance().camera();
        } else if (position == 1) {
            PhotoSelector.getInstance().gallery();
        } else if (position == 2) {
            dismiss();
        }
    }

    @Override
    public void onFinish(int type) {
        dismiss();
    }
}
