package com.dzenm.ui;

import android.content.Context;

import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.annotation.NonNull;
import androidx.databinding.ViewDataBinding;
import androidx.viewpager.widget.PagerAdapter;

import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.dzenm.R;
import com.dzenm.databinding.ActivityGalleryBinding;
import com.dzenm.helper.base.AbsBaseActivity;
import com.dzenm.helper.view.RatioImageView;

import java.util.ArrayList;
import java.util.List;

public class GalleryActivity extends AbsBaseActivity {

    private ActivityGalleryBinding binding;

    @Override
    protected void initializeView(@Nullable Bundle savedInstanceState, @Nullable ViewDataBinding viewDataBinding) {
        binding = DataBindingUtil.setContentView(this, R.layout.activity_gallery);
        setToolbar(binding.toolbar);

        int[] images = new int[]{R.drawable.one, R.drawable.two, R.drawable.three, R.drawable.four,
                R.drawable.five, R.drawable.six, R.drawable.seven};
        binding.viewPager.setOffscreenPageLimit(images.length);
        binding.viewPager.setPageMargin(20);
        binding.llRoot.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                binding.viewPager.dispatchTouchEvent(event);
                return true;
            }
        });

        binding.viewPager.setAdapter(new ViewPagerAdapter(this, images));
    }

    class ViewPagerAdapter extends PagerAdapter {

        private List<RatioImageView> mViews = new ArrayList<>();

        ViewPagerAdapter(Context context, int[] images) {
            for (int i = 0; i < images.length; i++) {
                RatioImageView imageView = (RatioImageView) View.inflate(context, R.layout.image_view, null);
                imageView.setImageResource(images[i]);
                mViews.add(imageView);
            }
        }

        @Override
        public int getCount() {
            return mViews.size();
        }

        @Override
        public boolean isViewFromObject(@NonNull View view, @NonNull Object o) {
            return view == o;
        }

        @NonNull
        @Override
        public Object instantiateItem(@NonNull ViewGroup container, int position) {
            container.addView(mViews.get(position));
            return mViews.get(position);
        }

        @Override
        public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
            container.removeView(mViews.get(position));
        }
    }
}
