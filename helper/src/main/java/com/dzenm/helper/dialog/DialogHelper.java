package com.dzenm.helper.dialog;

import android.annotation.SuppressLint;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;

/**
 * @author dinzhenyan
 * @date 2019-05-21 21:39
 * <p>
 * 这是一个可以自定义布局的dialog， 通过setLayout方法设置布局
 * <pre>
 * DialogHelper.newInstance(mActivity)
 *         .setLayout(R.layout.dialog_permission_prompt)
 *         .setCancel(false)
 *         .setOnBindViewHolder(this)
 *         .setBackground(DrawableHelper.solid(android.R.color.white)
 *                 .radius(10)
 *                 .build())
 *         .show();
 * </pre>
 * DataBinding使用方法
 * <pre>
 * DialogHelper.newInstance(this)
 *         .setLayout(R.layout.dialog_info)
 *         .setOnViewDataBinding(new DialogHelper.OnViewDataBinding() {
 *             @Override
 *             public void onBinding(ViewDataBinding binding, final AbsDialogFragment dialog) {
 *                 DialogInfoBinding infoBinding = (DialogInfoBinding) binding;
 *                 infoBinding.etMessage.setVisibility(View.VISIBLE);
 *                 infoBinding.etMessage.setText("这是使用dataBinding的结果");
 *                 infoBinding.tvNegative.setOnClickListener(new View.OnClickListener() {
 *                     @Override
 *                     public void onClick(View v) {
 *                          dialog.dismiss();
 *                     }
 *                 });
 *                 infoBinding.tvPositive.setOnClickListener(new View.OnClickListener() {
 *                     @Override
 *                     public void onClick(View v) {
 *                         dialog.dismiss();
 *                     }
 *                             });
 *             }
 *         }).show();
 * </pre>
 */
@SuppressLint("ValidFragment")
public class DialogHelper extends AbsDialogFragment {

    private @LayoutRes
    int mLayoutId;

    private OnViewDataBinding mOnViewDataBinding;

    private OnBindViewHolder mOnBindViewHolder;

    /************************************* 以下为自定义提示内容 *********************************/

    public static DialogHelper newInstance(AppCompatActivity activity) {
        DialogHelper dialogHelper = new DialogHelper(activity);
        return dialogHelper;
    }

    /**
     * ViewHolder用于进行对一些控件的操作, 主要是通过对应的Id进行操作, 使用时直接通过
     * {@link OnBindViewHolder} 的 holder 参数进行view的操作
     *
     * @param onBindViewHolder 回调事件
     * @return this
     */
    public DialogHelper setOnBindViewHolder(OnBindViewHolder onBindViewHolder) {
        mOnBindViewHolder = onBindViewHolder;
        return this;
    }

    /**
     * 使用dataBinding进行一些控件的操作, 使用时, 在布局的最外层添加layout, 然后在使用
     * OnBindingListener 的 binding 参数进行View的操作, 由于类型的不匹配, 需要进行强制转换
     *
     * @param onViewDataBinding 回调事件
     * @return this
     */
    public DialogHelper setOnViewDataBinding(OnViewDataBinding onViewDataBinding) {
        mOnViewDataBinding = onViewDataBinding;
        return this;
    }

    /**
     * @param layoutId 自定义Layout的resId
     * @return this
     */
    public DialogHelper setLayout(@LayoutRes int layoutId) {
        mLayoutId = layoutId;
        return this;
    }

    public DialogHelper(AppCompatActivity activity) {
        super(activity);
    }

    @Override
    public DialogHelper setMargin(int margin) {
        return super.setMargin(margin);
    }

    @Override
    public DialogHelper setGravity(int gravity) {
        return super.setGravity(gravity);
    }

    @Override
    public DialogHelper setAnimator(int animator) {
        return super.setAnimator(animator);
    }

    @Override
    public DialogHelper setBackground(Drawable background) {
        return super.setBackground(background);
    }

    @Override
    public DialogHelper setCenterWidth(int width) {
        return super.setCenterWidth(width);
    }

    @Override
    public DialogHelper setTranslucent(boolean translucent) {
        return super.setTranslucent(translucent);
    }

    @Override
    public DialogHelper setCancel(boolean cancel) {
        return super.setCancel(cancel);
    }

    @Override
    public DialogHelper setTouchInOutSideCancel(boolean cancel) {
        return super.setTouchInOutSideCancel(cancel);
    }

    @Override
    public DialogHelper setRadiusCard(float radiusCard) {
        return super.setRadiusCard(radiusCard);
    }

    /************************************* 以下为实现过程 *********************************/

    @Override
    protected boolean isUseViewHolder() {
        return true;
    }

    @Override
    protected int layoutId() {
        return mLayoutId;
    }

    /**
     * 设置回掉之后，可以使用自定义布局的控件做自定义的功能
     */
    @Override
    protected void convertView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (mOnViewDataBinding != null) {
            ViewDataBinding binding = DataBindingUtil.inflate(inflater, layoutId(), container, false);
            mView = binding.getRoot();
            mOnViewDataBinding.onBinding(binding, this);
        } else if (mOnBindViewHolder != null) {
            ViewHolder holder = ViewHolder.create(mView);
            mOnBindViewHolder.onBinding(holder, this);
        }
    }

    public interface OnViewDataBinding<T extends AbsDialogFragment> {
        void onBinding(ViewDataBinding binding, T dialog);
    }

    public interface OnBindViewHolder<T extends AbsDialogFragment> {
        void onBinding(ViewHolder holder, T dialog);
    }
}
