package com.dzenm.helper.ui;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import com.dzenm.helper.R;
import com.dzenm.helper.databinding.ActivityMainBinding;
import com.dzenm.lib.base.AbsBaseActivity;
import com.dzenm.lib.base.FragmentHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * @author dinzhenyan
 * @date 2019-06-27 10:32
 */
public class MainActivity extends AbsBaseActivity {

    private ActivityMainBinding binding;

    FragmentHelper fragmentHelper;
    HomeFragment f1;
    PersonalFragment f2;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        f1 = new HomeFragment();
        f2 = new PersonalFragment();
        List<Fragment> fragments = new ArrayList<>();
        fragments.add(f1);
        fragments.add(f2);

        fragmentHelper = new FragmentHelper(this);
        fragmentHelper.inflate(R.id.frame_layout, fragments);
        fragmentHelper.show(f1);
    }

    public void onClick(View view) {
        if (view.getId() == R.id.first) {
            fragmentHelper.show(f1);
        } else if (view.getId() == R.id.second) {
            fragmentHelper.show(f2);
        }
    }
}
