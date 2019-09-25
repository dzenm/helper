package com.dzenm.ui;

import android.content.Intent;
import android.os.Handler;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;

import com.dzenm.R;
import com.dzenm.databinding.ActivityDrawableBinding;
import com.dzenm.helper.base.AbsBaseActivity;
import com.dzenm.helper.draw.BackGHelper;
import com.dzenm.helper.draw.Orientation;
import com.dzenm.helper.file.FileHelper;
import com.dzenm.helper.photo.PhotoHelper;
import com.dzenm.helper.toast.Toa;
import com.dzenm.helper.view.PhotoLayout;

import java.util.Arrays;

public class DrawableActivity extends AbsBaseActivity implements PhotoLayout.OnLoadPhotoListener {

    private PhotoLayout plAdd, plPreview;

    @Override
    protected void initializeView() {
        ActivityDrawableBinding binding =
                DataBindingUtil.setContentView(this, R.layout.activity_drawable);
        setToolbarWithGradientStatusBar(binding.toolbar,
                BackGHelper.orientation(Orientation.LEFT_RIGHT)
                        .gradient(android.R.color.holo_orange_light,
                                android.R.color.holo_blue_light).build());

        BackGHelper.orientation(Orientation.BOTTOM_TOP)
                .gradient(android.R.color.holo_red_light, android.R.color.holo_blue_light, android.R.color.holo_orange_light)
                .into(binding.linearGradient);
        BackGHelper.radialRadius(80)
                .gradient(android.R.color.holo_red_light, android.R.color.holo_blue_light, android.R.color.holo_orange_light)
                .into(binding.orgialGradient);
        BackGHelper.gradient(android.R.color.holo_red_light, android.R.color.holo_blue_light, android.R.color.holo_orange_light)
                .into(binding.sweepGradient);
        FileHelper.getInstance().setPersonFolder("/dinzhenyan").getPersonFolder("/personalAccount");

        binding.ripple.setOnClickListener(null);
        BackGHelper.radius(20).ripple(android.R.color.white,
                android.R.color.holo_red_dark
        ).into(binding.ripple);

        initPhotoLayout();
    }

    private void initPhotoLayout() {
        plAdd = findViewById(R.id.pl_add);
        plPreview = findViewById(R.id.pl_preview);
        plPreview.setPreview(true);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

            }
        }, 1000);
        addPhoto();
        previewPhoto();

    }

    private void addPhoto() {
        plAdd.setOnPhotoListener(this);
        plAdd.setImageLoader(new MyImageLoader());
    }

    @Override
    public void onLoad(final PhotoLayout layout) {
        PhotoHelper.getInstance().with(this).setOnSelectPhotoListener(new PhotoHelper.OnSelectPhotoListener() {
            @Override
            public boolean onGallery(PhotoHelper helper, String filePath) {
                layout.load(FileHelper.getInstance().getPhoto(filePath));
                return false;
            }
        }).gallery();
    }

    private void previewPhoto() {
        final String[] url = new String[]{
                "/storage/emulated/0/DCIM/Camera/IMG_20190622_173402.jpg",
                "/storage/emulated/0/DCIM/Camera/IMG_20190603_193257.jpg",
                "/storage/emulated/0/DCIM/Camera/IMG_20190519_134904.jpg"
        };

        plPreview.setOnItemClickListener(new PhotoLayout.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Toa.show("点击预览的是第: " + position);
            }
        });
        plPreview.setImageLoader(new MyImageLoader());
        plPreview.load(Arrays.asList(url));
    }
}
