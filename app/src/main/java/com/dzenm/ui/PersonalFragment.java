package com.dzenm.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.databinding.ViewDataBinding;

import com.dzenm.R;
import com.dzenm.databinding.FragmentPersonalBinding;
import com.dzenm.helper.base.AbsBaseFragment;
import com.dzenm.helper.draw.BackGHelper;
import com.dzenm.helper.os.StatusBarHelper;
import com.dzenm.helper.toast.Toa;

/**
 * @author dzenm
 * @date 2019-09-12 16:57
 */
public class PersonalFragment extends AbsBaseFragment<MainActivity> implements View.OnClickListener {

    private FragmentPersonalBinding binding;

    @Override
    protected int layoutId() {
        return R.layout.fragment_personal;
    }

    @Override
    protected boolean isDataBinding() {
        return true;
    }

    @Override
    public void initializeView(Bundle savedInstanceState, ViewDataBinding viewDataBinding) {
        binding = (FragmentPersonalBinding) viewDataBinding;
        setToolbarWithoutStatusBar(binding.toolbar);

        BackGHelper.radius(8).pressed(R.color.colorDarkBlue, R.color.colorTranslucentDarkBlue)
                .into(binding.tvDraw);
        BackGHelper.radius(8).pressed(R.color.colorDarkBlue, R.color.colorTranslucentDarkBlue)
                .into(binding.tvPreview);

        binding.tvDraw.setOnClickListener(this);
        binding.tvPreview.setOnClickListener(this);
        binding.rivImage.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.tv_draw) {
            Intent intent = new Intent(getActivity(), DrawActivity.class);
            startActivity(intent);
        } else if (v.getId() == R.id.riv_image) {
//            Bitmap bitmap = ScreenHelper.snapShotWithStatusBar(mActivity);
//            Glide.with(this).load(bitmap).into(binding.ivImage);
            Toa.show("点击");
        }
    }
}
