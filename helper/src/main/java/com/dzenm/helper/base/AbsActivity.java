package com.dzenm.helper.base;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;

/**
 * @author dzenm
 * @date 2020-01-05 17:16
 */
public abstract class AbsActivity<V extends ViewDataBinding> extends AbsBaseActivity {

    private V binding;

    protected int layoutId() {
        return -1;
    }

    @Override
    protected void initializeView(@Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.setContentView(this, layoutId());
    }

    public V getBinding() {
        return binding;
    }
}
