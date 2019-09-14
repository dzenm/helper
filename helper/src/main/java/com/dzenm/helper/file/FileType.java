package com.dzenm.helper.file;

import androidx.annotation.StringDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * @author dzenm
 * @date 2019-07-31 23:19
 */
@StringDef({FileType.TEXT, FileType.IMAGE, FileType.VIDEO, FileType.FILE})
@Retention(RetentionPolicy.SOURCE)
public @interface FileType {

    /**
     * 文本类型
     */
    String TEXT = "text/plain";

    /**
     * 图片类型
     */
    String IMAGE = "image/*";

    /**
     * 视频类型
     */
    String VIDEO = "video/*";

    /**
     * 文件类型
     */
    String FILE = "*/*";

    /**
     * 安装包类型
     */
    String MIME_TYPE = "application/vnd.android.package-archive";
}
