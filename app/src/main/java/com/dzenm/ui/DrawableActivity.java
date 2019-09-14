package com.dzenm.ui;

import android.content.Intent;
import androidx.databinding.DataBindingUtil;
import android.graphics.Bitmap;
import androidx.annotation.Nullable;
import android.view.View;

import com.dzenm.helper.view.PhotoLayout;
import com.dzenm.R;
import com.dzenm.databinding.ActivityDrawableBinding;
import com.dzenm.helper.base.AbsBaseActivity;
import com.dzenm.helper.draw.BackGHelper;
import com.dzenm.helper.draw.Orientation;
import com.dzenm.helper.file.FileHelper;
import com.dzenm.helper.file.PhotoHelper;
import com.dzenm.helper.toast.Toa;

import java.util.Arrays;

public class DrawableActivity extends AbsBaseActivity implements PhotoLayout.OnAddPhotoListener {

    private PhotoLayout plAdd, plPreview, plTest;

    @Override
    protected void initializeView() {
        ActivityDrawableBinding binding =
                DataBindingUtil.setContentView(this, R.layout.activity_drawable);
        setToolbar(binding.toolbar);

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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        PhotoHelper.getInstance().onPhotoResult(requestCode, resultCode, data);
    }

    private void initPhotoLayout() {
        plAdd = findViewById(R.id.pl_add);
        plPreview = findViewById(R.id.pl_preview);
        plTest = findViewById(R.id.pl_test);

        addPhoto();
        previewPhoto();
    }

    private void addPhoto() {
        plAdd.setOnPhotoListener(this);

        plTest.setTotalNumber(12);
        plTest.setColumnNumber(4);
        plTest.setOnPhotoListener(this);
    }

    @Override
    public void onAdd(final PhotoLayout layout) {
        PhotoHelper.getInstance()
                .with(DrawableActivity.this)
                .setOnSelectPhotoListener(new PhotoHelper.OnSelectPhotoListener() {
                    @Override
                    public boolean onGallery(PhotoHelper helper, String filePath) {
                        Bitmap bitmap = FileHelper.getInstance().getPhoto(filePath);
                        layout.load(bitmap);
                        layout.loader(new MyImageLoader());
                        return false;
                    }

                    @Override
                    public void onError(String msg) {
                        super.onError(msg);
                    }
                }).selectGallery();
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
                Toa.show("点击的是第: " + position);
            }
        });
        plPreview.setPreview(true);
        plPreview.loader(new MyImageLoader());
        plPreview.load(Arrays.asList(url));
    }
}
