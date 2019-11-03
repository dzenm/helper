package com.dzenm.helper.base;

import android.content.Intent;

import androidx.annotation.Nullable;

/**
 * @author dzenm
 * @date 2019-10-19 12:30
 */
public interface OnActivityResult {

    void onResult(int requestCode, int resultCode, @Nullable Intent data);
}
