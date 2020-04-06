package com.dzenm.ui;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.dzenm.helper.view.ImageLoader;
import com.dzenm.helper.view.RatioImageView;

/**
 * @author dzenm
 * @date 2019-09-05 21:48
 */
public class MyImageLoader implements ImageLoader {

    @Override
    public void onLoader(RatioImageView imageView, Object object) {
        RoundedCorners rc = new RoundedCorners(12);
        RequestOptions options = RequestOptions.bitmapTransform(rc);
        Glide.with(imageView.getContext()).load(object).apply(options).into(imageView);
    }
}
