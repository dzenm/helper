package com.dzenm.helper.ui;

import android.os.Bundle;
import android.view.View;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.databinding.DataBindingUtil;

import com.dzenm.helper.R;
import com.dzenm.helper.databinding.ActivityViewBinding;
import com.dzenm.lib.base.AbsBaseActivity;
import com.dzenm.lib.toast.ToastHelper;

public class ViewActivity extends AbsBaseActivity {

    private ActivityViewBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_view);
        setToolbarWithImmersiveStatusBar(binding.toolbar, R.color.colorPrimary);

        binding.btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (binding.editLayout.verify(false)) {
                    ToastHelper.show("校验成功");
                } else {
                    ToastHelper.show("校验失败");
                }
            }
        });
    }
}
