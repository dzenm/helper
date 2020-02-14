package com.dzenm.helper.dialogs;

import androidx.appcompat.app.AppCompatActivity;

import com.dzenm.helper.dialog.AbsDialogFragment;

/**
 * @author dzenm
 * @date 2020-01-15 21:46
 */
public class MaterialDialog extends AbsDialogFragment {

    /************************************* 以下为实现过程 *********************************/

    public MaterialDialog(AppCompatActivity activity) {
        super(activity);
    }

    public class Builder {

        public Builder() {

        }
    }
}
