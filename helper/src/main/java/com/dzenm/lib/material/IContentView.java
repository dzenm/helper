package com.dzenm.lib.material;

import android.view.View;

/**
 * @author dzenm
 * @date 2020/4/12 13:51
 * @IDE Android Studio
 */
public interface IContentView {

    /**
     * create a content view for {@link MaterialDialog}
     * @param delegate set or get some properity
     * @return a view
     */
    View onCreateView(DialogDelegate delegate);
}
