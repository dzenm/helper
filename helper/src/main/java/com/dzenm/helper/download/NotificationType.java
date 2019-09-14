package com.dzenm.helper.download;

import androidx.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * @author dzenm
 * @date 2019-09-02 14:31
 */
@IntDef({NotificationType.NOTIFICATION_VISIBLE,
        NotificationType.NOTIFICATION_VISIBLE_NOTIFY_COMPLETED,
        NotificationType.NOTIFICATION_HIDDEN,
        NotificationType.NOTIFICATION_VISIBLE_NOTIFY_ONLY_COMPLETION
})
@Retention(RetentionPolicy.SOURCE)
public @interface NotificationType {

    /**
     * 在下载过程中通知栏会一直显示该下载的Notification。在下载完毕后该Notification会继续显示。
     * 直到用户点击该Notification或者消除该Notification。这是默认的參数值。
     */
    int NOTIFICATION_VISIBLE = 0;

    /**
     * 在下载进行的过程中。通知栏中会一直显示该下载的Notification，当下载完毕时，该Notification
     * 会被移除。
     */
    int NOTIFICATION_VISIBLE_NOTIFY_COMPLETED = 1;

    /**
     * 不显示该下载请求的Notification。假设要使用这个參数，须要在应用的清单文件里加上
     * DOWNLOAD_WITHOUT_NOTIFICATION权限。
     */
    int NOTIFICATION_HIDDEN = 2;

    /**
     * 仅仅有在下载完毕后该Notification才会被显示。
     */
    int NOTIFICATION_VISIBLE_NOTIFY_ONLY_COMPLETION = 3;
}
