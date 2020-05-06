package com.dzenm.helper;

import android.os.Bundle;
import android.view.View;

import androidx.databinding.DataBindingUtil;

import com.bumptech.glide.Glide;
import com.dzenm.helper.databinding.ActivityPhotoSeletorBinding;
import com.dzenm.helper.ui.MyImageLoader;
import com.dzenm.lib.base.AbsBaseActivity;
import com.dzenm.lib.file.FileHelper;
import com.dzenm.lib.material.MaterialDialog;
import com.dzenm.lib.photo.PhotoSelector;
import com.dzenm.lib.view.ImageAdapter;
import com.dzenm.lib.view.ImageLoader;
import com.dzenm.lib.view.PhotoLayout;
import com.dzenm.lib.view.RatioImageView;

import java.util.Arrays;

public class PhotoSelectorActivity extends AbsBaseActivity implements PhotoLayout.OnLoadPhotoListener {

    private ActivityPhotoSeletorBinding binding;
    private PhotoLayout plAdd, plPreview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_photo_seletor);

        setToolbarWithImmersiveStatusBar(binding.toolbar, R.color.colorPrimary);

        final String[] images = new String[]{
                "/storage/emulated/0/DCIM/Camera/IMG_20190622_173402.jpg",
                "/storage/emulated/0/DCIM/Camera/IMG_20190603_193257.jpg",
                "/storage/emulated/0/DCIM/Camera/IMG_20190519_134904.jpg"
//                "/storage/emulated/0/DCIM/Camera/IMG_20190603_193257.jpg",
//                "/storage/emulated/0/DCIM/Camera/IMG_20190519_134904.jpg",
//                "/storage/emulated/0/DCIM/Camera/IMG_20190622_173402.jpg",
//                "/storage/emulated/0/DCIM/Camera/IMG_20190519_134904.jpg",
//                "/storage/emulated/0/DCIM/Camera/IMG_20190622_173402.jpg",
//                "/storage/emulated/0/DCIM/Camera/IMG_20190603_193257.jpg"
        };
        final ImageAdapter adapter = new ImageAdapter(Arrays.asList(images));
        adapter.setImageLoader(new ImageLoader() {
            @Override
            public void onLoader(RatioImageView imageView, Object image) {
                Glide.with(imageView.getContext()).load(image).into(imageView);
            }
        });
        adapter.setOnItemClickListener(new ImageAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, Object data, int position) {
//                Intent intent = new Intent(activity, ImagePreviewActivity.class);
//                List<ImageInfo> imageInfos = new ArrayList<>();
//                ImageInfo imageInfo = new ImageInfo();
//                imageInfo.bigImageUrl = R.mipmap.ic_launcher;
//                imageInfos.add(imageInfo);
//                Toast.makeText(activity, "hello", Toast.LENGTH_SHORT).show();
//                intent.putParcelableArrayListExtra(ImagePreviewActivity.IMAGE_INFO, (ArrayList<? extends Parcelable>) imageInfos);
//                intent.putExtra(ImagePreviewActivity.CURRENT_ITEM, 0);
            }

            @Override
            public void onLoad(final ImageAdapter adapter) {
                new MaterialDialog.Builder(PhotoSelectorActivity.this)
                        .setItem("拍照", "图片", "取消")
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
                        }).create().show();
            }
        });
        binding.gridlayout.setAdapter(adapter);


        initPhotoLayout();
//        binding.linearGradient.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                adapter.add(2, images[0]);
//            }
//        });
//
//        binding.orgialGradient.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                adapter.remove(1);
//            }
//        });
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
        new MaterialDialog.Builder(this).setItem("拍照", "图片", "取消")
                .setOnSelectedPhotoListener(new PhotoSelector.OnSelectedPhotoListener() {
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
                }).create().show();
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
