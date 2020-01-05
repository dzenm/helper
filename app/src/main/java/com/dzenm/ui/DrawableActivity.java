package com.dzenm.ui;

import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.databinding.ViewDataBinding;

import com.dzenm.R;
import com.dzenm.databinding.ActivityDrawableBinding;
import com.dzenm.helper.base.AbsActivity;
import com.dzenm.helper.base.AbsBaseActivity;
import com.dzenm.helper.dialog.PhotoDialog;
import com.dzenm.helper.draw.DrawableHelper;
import com.dzenm.helper.file.FileHelper;
import com.dzenm.helper.photo.PhotoSelector;
import com.dzenm.helper.view.PhotoLayout;

import java.util.Arrays;

public class DrawableActivity extends AbsActivity<ActivityDrawableBinding> implements PhotoLayout.OnLoadPhotoListener {

    private PhotoLayout plAdd, plPreview;

    @Override
    protected int layoutId() {
        return R.layout.activity_drawable;
    }

    @Override
    protected void initializeView(@Nullable Bundle savedInstanceState) {
        super.initializeView(savedInstanceState);
        setToolbarWithGradientStatusBar(getBinding().toolbar,
                DrawableHelper.orientation(GradientDrawable.Orientation.LEFT_RIGHT)
                        .gradient(android.R.color.holo_orange_light,
                                android.R.color.holo_blue_light).build());

        DrawableHelper.orientation(GradientDrawable.Orientation.BOTTOM_TOP)
                .gradient(android.R.color.holo_red_light, android.R.color.holo_blue_light, android.R.color.holo_orange_light)
                .into(getBinding().linearGradient);
        DrawableHelper.radialRadius(80)
                .gradient(android.R.color.holo_red_light, android.R.color.holo_blue_light, android.R.color.holo_orange_light)
                .into(getBinding().orgialGradient);
        DrawableHelper.gradient(android.R.color.holo_red_light, android.R.color.holo_blue_light, android.R.color.holo_orange_light)
                .into(getBinding().sweepGradient);
        FileHelper.getInstance().setPersonFolder("/dinzhenyan").getPersonFolder("/personalAccount");

        getBinding().ripple.setOnClickListener(null);
        DrawableHelper.radius(20).ripple(android.R.color.white,
                android.R.color.holo_red_dark
        ).into(getBinding().ripple);

        initPhotoLayout();
    }

    private void initPhotoLayout() {
        plAdd = findViewById(R.id.pl_add);
        plPreview = findViewById(R.id.pl_preview);
        plPreview.setPreview(true);

        addPhoto();
        previewPhoto();
    }

    private void addPhoto() {
        plAdd.setOnPhotoListener(this);
        plAdd.setImageLoader(new MyImageLoader());
    }

    @Override
    public void onLoad(final PhotoLayout layout) {
        PhotoDialog.newInstance(this).setOnSelectPhotoListener(new PhotoSelector.OnSelectPhotoListener() {
            @Override
            public boolean onGallery(PhotoSelector selector, String filePath) {
                layout.load(FileHelper.getInstance().getPhoto(filePath));
                return false;
            }

            @Override
            public boolean onGraph(PhotoSelector selector, String filePath) {
                layout.load(FileHelper.getInstance().getPhoto(filePath));
                return false;
            }
        }).show();
    }

    private void previewPhoto() {
        final String[] url = new String[]{
                "/storage/emulated/0/DCIM/Camera/IMG_20190622_173402.jpg",
                "/storage/emulated/0/DCIM/Camera/IMG_20190603_193257.jpg",
                "/storage/emulated/0/DCIM/Camera/IMG_20190519_134904.jpg"
        };
        plPreview.setImageLoader(new MyImageLoader());
        plPreview.load(Arrays.asList(url));
    }
}
