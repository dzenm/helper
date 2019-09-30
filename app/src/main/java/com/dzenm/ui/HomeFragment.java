package com.dzenm.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.databinding.ViewDataBinding;

import com.dzenm.R;
import com.dzenm.databinding.FragmentHomeBinding;
import com.dzenm.helper.base.AbsBaseFragment;
import com.dzenm.helper.draw.BackGHelper;

/**
 * @author dzenm
 * @date 2019-09-12 15:35
 */
public class HomeFragment extends AbsBaseFragment<MainActivity> implements View.OnClickListener {

    private FragmentHomeBinding binding;

    @Override
    protected int layoutId() {
        return R.layout.fragment_home;
    }

    @Override
    protected boolean isDataBinding() {
        return true;
    }

    @Override
    public void initializeView(Bundle savedInstanceState, ViewDataBinding viewDataBinding) {
        binding = (FragmentHomeBinding) viewDataBinding;
        setToolbarWithImmersiveStatusBar(binding.toolbar, R.color.colorDarkBlue);

        BackGHelper.radius(8).pressed(android.R.color.holo_blue_bright, R.color.colorDivide).into(binding.tv100);
        BackGHelper.radius(8).ripple(android.R.color.holo_blue_light, R.color.colorDivide).into(binding.tv101);
        BackGHelper.radius(8).pressed(android.R.color.holo_orange_light, R.color.colorDivide).into(binding.tv102);
        BackGHelper.radius(8).ripple(android.R.color.holo_green_light, R.color.colorDivide).into(binding.tv103);
        BackGHelper.radius(8).pressed(android.R.color.holo_purple, R.color.colorDivide).into(binding.tv104);
        BackGHelper.radius(8).ripple(android.R.color.holo_blue_bright, R.color.colorDivide).into(binding.tv105);

        binding.tv100.setOnClickListener(this);
        binding.tv101.setOnClickListener(this);
        binding.tv102.setOnClickListener(this);
        binding.tv103.setOnClickListener(this);
        binding.tv104.setOnClickListener(this);
        binding.tv105.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.tv_100) {
            Intent intent = new Intent(getActivity(), DialogActivity.class);
            startActivity(intent);
        } else if (view.getId() == R.id.tv_101) {
            Intent intent = new Intent(getActivity(), LoadingActivity.class);
            startActivity(intent);
        } else if (view.getId() == R.id.tv_102) {
            Intent intent = new Intent(getActivity(), GalleryActivity.class);
            startActivity(intent);
        } else if (view.getId() == R.id.tv_103) {
            Intent intent = new Intent(getActivity(), PermissionActivity.class);
            startActivity(intent);
        } else if (view.getId() == R.id.tv_104) {
            Intent intent = new Intent(getActivity(), DrawableActivity.class);
            startActivity(intent);
        } else if (view.getId() == R.id.tv_105) {
            Intent intent = new Intent(getActivity(), OthersActivity.class);
            startActivity(intent);
        }
    }
}
