package com.dzenm.helper.photo;

import androidx.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * @author dzenm
 * @date 2019-09-21 18:38
 */
@IntDef({PhotoType.GRAPH, PhotoType.GALLERY, PhotoType.CROP})
@Retention(RetentionPolicy.SOURCE)
public @interface PhotoType {

    //  图库
    int GRAPH = 131;

    //  拍照
    int GALLERY = 132;

    //  裁剪
    int CROP = 133;
}
