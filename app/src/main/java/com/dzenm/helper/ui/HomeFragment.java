package com.dzenm.helper.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;

import com.dzenm.helper.R;
import com.dzenm.helper.databinding.FragmentHomeBinding;
import com.dzenm.lib.base.AbsBaseFragment;
import com.dzenm.lib.drawable.DrawableHelper;

/**
 * @author dzenm
 * @date 2019-09-12 15:35
 */
public class HomeFragment extends AbsBaseFragment<MainActivity> implements View.OnClickListener {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, @Nullable Bundle savedInstanceState) {
        logD("onCreateView");

        FragmentHomeBinding binding = FragmentHomeBinding.inflate(inflater);
        setToolbarWithImmersiveStatusBar(binding.toolbar, R.color.colorMaterialLightBlue);

        DrawableHelper.radius(8).pressed(android.R.color.holo_blue_bright, R.attr.divideColor).into(binding.tv100);
        DrawableHelper.radius(8).ripple(android.R.color.holo_blue_light, R.attr.divideColor).into(binding.tv101);
        DrawableHelper.radius(8).pressed(android.R.color.holo_orange_light, R.attr.divideColor).into(binding.tv102);
        DrawableHelper.radius(8).ripple(android.R.color.holo_green_light, R.attr.divideColor).into(binding.tv103);
        DrawableHelper.radius(8).pressed(android.R.color.holo_purple, R.attr.divideColor).into(binding.tv104);
        DrawableHelper.radius(8).ripple(android.R.color.holo_blue_bright, R.attr.divideColor).into(binding.tv105);
        DrawableHelper.radius(8).pressed(android.R.color.holo_orange_dark, R.attr.divideColor).into(binding.tv106);
        DrawableHelper.radius(8).pressed(android.R.color.holo_blue_dark, R.attr.divideColor).into(binding.tv107);
        DrawableHelper.radius(8).pressed(android.R.color.holo_orange_light, R.attr.divideColor).into(binding.tv108);

        binding.tv100.setOnClickListener(this);
        binding.tv101.setOnClickListener(this);
        binding.tv102.setOnClickListener(this);
        binding.tv103.setOnClickListener(this);
        binding.tv104.setOnClickListener(this);
        binding.tv105.setOnClickListener(this);
        binding.tv106.setOnClickListener(this);
        binding.tv107.setOnClickListener(this);
        binding.tv108.setOnClickListener(this);
        return binding.getRoot();
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
//            Intent intent = new Intent(getActivity(), GalleryActivity.class);
//            startActivity(intent);
        } else if (view.getId() == R.id.tv_103) {
            Intent intent = new Intent(getActivity(), PermissionActivity.class);
            startActivity(intent);
        } else if (view.getId() == R.id.tv_104) {
            Intent intent = new Intent(getActivity(), DrawableActivity.class);
            startActivity(intent);
        } else if (view.getId() == R.id.tv_105) {
            Intent intent = new Intent(getActivity(), ToastSharedActivity.class);
            startActivity(intent);
        } else if (view.getId() == R.id.tv_106) {
            Intent intent = new Intent(getActivity(), MaterialActivity.class);
            startActivity(intent);
        } else if (view.getId() == R.id.tv_107) {
            Intent intent = new Intent(getActivity(), PhotoSelectorActivity.class);
            startActivity(intent);
        } else if (view.getId() == R.id.tv_108) {
            Intent intent = new Intent(getActivity(), ViewActivity.class);
            startActivity(intent);
        }
    }
}
