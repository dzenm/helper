package com.dzenm.helper.view;

/**
 * 图片加载的方式, 可以使用第三方加载图片, 直接使用设置图片的方式可能会引起OOM
 * @author dzenm
 * @date 2019-09-05 21:40
 */
public interface ImageLoader {

    void onLoader(RatioImageView imageView, Object object);
}
