package com.dzenm.ui;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.dzenm.R;
import com.dzenm.databinding.ActivityMainBinding;
import com.dzenm.helper.base.AbsActivity;
import com.dzenm.helper.base.FragmentHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * @author dinzhenyan
 * @date 2019-06-27 10:32
 */
public class MainActivity extends AbsActivity<ActivityMainBinding> {

    FragmentHelper fragmentHelper;
    HomeFragment f1;
    PersonalFragment f2;

    @Override
    protected int layoutId() {
        return R.layout.activity_main;
    }

    @Override
    protected void initializeView(@Nullable Bundle savedInstanceState) {
        super.initializeView(savedInstanceState);
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
