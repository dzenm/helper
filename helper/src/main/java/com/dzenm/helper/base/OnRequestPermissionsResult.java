package com.dzenm.helper.base;

import androidx.annotation.NonNull;

/**
 * @author dzenm
 * @date 2019-10-19 12:29
 */
public interface OnRequestPermissionsResult {

    void onResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults);
}
