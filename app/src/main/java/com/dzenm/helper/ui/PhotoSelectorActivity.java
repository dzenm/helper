package com.dzenm.helper.ui;

import android.os.Bundle;
import android.view.View;

import androidx.databinding.DataBindingUtil;

import com.bumptech.glide.Glide;
import com.dzenm.helper.R;
import com.dzenm.helper.databinding.ActivityPhotoSeletorBinding;
import com.dzenm.lib.base.AbsBaseActivity;
import com.dzenm.lib.dialog.PreviewDialog;
import com.dzenm.lib.file.FileHelper;
import com.dzenm.lib.material.MaterialDialog;
import com.dzenm.lib.photo.PhotoSelector;
import com.dzenm.lib.view.GridLayout;
import com.dzenm.lib.view.ImageLoader;
import com.dzenm.lib.view.RatioImageView;

import java.util.Arrays;

public class PhotoSelectorActivity extends AbsBaseActivity {

    private ActivityPhotoSeletorBinding binding;
    private final String[] images = new String[]{
            "/storage/emulated/0/DCIM/Camera/IMG_20190622_173402.jpg",
            "/storage/emulated/0/DCIM/Camera/IMG_20190603_193257.jpg",
            "/storage/emulated/0/DCIM/Camera/IMG_20190519_134904.jpg",
            "/storage/emulated/0/DCIM/Camera/IMG_20190603_193257.jpg"
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_photo_seletor);

        setToolbarWithImmersiveStatusBar(binding.toolbar, R.color.colorPrimary);

        binding.gridlayout.setData(Arrays.asList(images));
        ImageLoader imageLoader = new ImageLoader() {
            @Override
            public void onLoader(RatioImageView imageView, Object image) {
                Glide.with(imageView.getContext()).load(image).into(imageView);
            }
        };

        binding.gridlayout.setImageLoader(imageLoader);
        binding.gridlayout.setOnItemClickListener(new GridLayout.OnItemClickListener() {
            @Override
            public void onLoad(final GridLayout.ImageAdapter adapter) {
                new MaterialDialog.Builder(PhotoSelectorActivity.this)
                        .setItem("拍照", "图片")
                        .setMaterialDesign(false)
                        .setOnSelectedPhotoListener(new PhotoSelector.OnSelectedPhotoListener() {
                            @Override
                            public boolean onGallery(PhotoSelector selector, String filePath) {
//                        layout.load(FileHelper.getInstance().getPhoto(filePath));
                                adapter.add(FileHelper.getInstance().getPhoto(filePath));
                                return false;
                            }

                            @Override
                            public boolean onGraph(PhotoSelector selector, String filePath) {
//                        layout.load(FileHelper.getInstance().getPhoto(filePath));
                                adapter.add(FileHelper.getInstance().getPhoto(filePath));
                                return false;
                            }
                        })
                        .create()
                        .show();
            }

            @Override
            public void onItemClick(View view, Object data, int position) {
                PreviewDialog.newInstance(PhotoSelectorActivity.this)
                        .setImageLoader(new MyImageLoader())
                        .load(data)
                        .show();
            }
        });

        binding.reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                reset();
            }
        });
        binding.addPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.gridlayout.add(2, images[0]);
            }
        });
        binding.deletePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.gridlayout.remove(1);
            }
        });
    }

    private void reset() {
        boolean gridMode = binding.gridMode.isChecked();
        boolean showMore = binding.showMore.isChecked();
        boolean singleWrap = binding.singleWrap.isChecked();
        boolean editable = binding.enabledEdit.isChecked();
        boolean deletable = binding.showDelete.isChecked();

        binding.gridlayout.setMode(gridMode ? GridLayout.MODE_GRID : GridLayout.MODE_FILL);
        binding.gridlayout.setShowMore(showMore);
        binding.gridlayout.setSingleWrap(singleWrap);
        binding.gridlayout.setEditable(editable);
        binding.gridlayout.setDeletable(deletable);
        binding.gridlayout.setData(Arrays.asList(images));


    }
}
