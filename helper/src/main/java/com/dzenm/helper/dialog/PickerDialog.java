package com.dzenm.helper.dialog;

import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.annotation.SuppressLint;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.dzenm.helper.R;
import com.dzenm.helper.draw.DrawableHelper;
import com.dzenm.helper.view.PickerView;

import java.util.ArrayList;

/**
 * @author dzenm
 * @date 2019-08-25 22:54
 */
@SuppressLint("ValidFragment")
public class PickerDialog extends AbsDialogFragment implements View.OnClickListener, PickerView.onSelectListener {

    /**
     * 选中的数据
     */
    protected String[] mData;

    /**
     * 默认选中的数据 {@link #setSelected(int)} {@link #setSelected(String)}
     */
    protected String mSelectedData;

    /**
     * 标题文本 {@link #setTitle(String)}  {@link #setTitle(String)}
     */
    private String mTitle;

    /**
     * 是否可以循环显示 {@link #setLoop(boolean)}
     */
    protected boolean isLoop = false;

    /**
     * List数据
     */
    protected ArrayList<String> mPrimaryList, mSecondaryList, mUnitList;

    /**
     * 选择控件
     */
    protected PickerView pvPrimary, pvSecondary, pvUnit;

    /**
     * 选中回掉监听事件
     */
    protected OnSelectedCallback mOnSelectedCallback;

    /************************************* 以下为自定义方法 *********************************/

    public static DatePickerDialog newInstance(AppCompatActivity activity) {
        return new DatePickerDialog(activity);
    }

    /**
     * @param title 日期选择框的标题 {@link #mTitle}
     * @return this
     */
    public <T extends PickerDialog> T setTitle(String title) {
        mTitle = title;
        return (T) this;
    }

    /**
     * @param resId 日期选择框的标题 {@link #mTitle}
     * @return this
     */
    public <T extends PickerDialog> T setTitle(int resId) {
        mTitle = getStrings(resId);
        return (T) this;
    }

    /**
     * @param loop 数据列表是否循环显示 {@link #isLoop}
     * @return this
     */
    public <T extends PickerDialog> T setLoop(boolean loop) {
        isLoop = loop;
        return (T) this;
    }

    /**
     * @param date 当前选中的日期 {@link #mSelectedData}
     * @return this
     */
    public <T extends PickerDialog> T setSelected(String date) {
        mSelectedData = date;
        return (T) this;
    }

    /**
     * @param resId 当前选中的日期 {@link #mSelectedData}
     * @return this
     */
    public <T extends PickerDialog> T setSelected(int resId) {
        mSelectedData = getStrings(resId);
        return (T) this;
    }

    /**
     * @param onSelectedCallback 选中的监听回掉事件 {@link #mOnSelectedCallback}
     * @return this
     */
    public <T extends PickerDialog> T setOnSelectedCallback(OnSelectedCallback onSelectedCallback) {
        mOnSelectedCallback = onSelectedCallback;
        return (T) this;
    }

    /************************************* 以下为实现过程 *********************************/

    protected PickerDialog(AppCompatActivity activity) {
        super(activity);
        mGravity = Gravity.BOTTOM;
    }

    @Override
    protected int layoutId() {
        return R.layout.dialog_picker;
    }

    @Override
    protected void initView() {
        pvPrimary = findViewById(R.id.pv_primary);
        pvSecondary = findViewById(R.id.pv_secondary);
        pvUnit = findViewById(R.id.pv_unit);

        TextView tvCancel = findViewById(R.id.tv_cancel);
        TextView tvTitle = findViewById(R.id.tv_title);
        TextView tvConfirm = findViewById(R.id.tv_confirm);

        TextView tvPrimary = findViewById(R.id.tv_primary);
        TextView tvSecondary = findViewById(R.id.tv_secondary);
        TextView tvUnit = findViewById(R.id.tv_unit);

        tvCancel.setOnClickListener(this);
        tvConfirm.setOnClickListener(this);

        pvPrimary.setPrimaryTextColor(getColor(mPrimaryColor));
        pvSecondary.setPrimaryTextColor(getColor(mPrimaryColor));
        pvUnit.setPrimaryTextColor(getColor(mPrimaryColor));
        tvPrimary.setTextColor(getColor(mPrimaryColor));
        tvSecondary.setTextColor(getColor(mPrimaryColor));
        tvUnit.setTextColor(getColor(mPrimaryColor));
        tvCancel.setTextColor(getColor(mPrimaryColor));
        tvTitle.setTextColor(getColor(mPrimaryColor));
        tvConfirm.setTextColor(getColor(mPrimaryColor));

        // 设置标题文本
        if (mTitle != null) tvTitle.setText(mTitle);

        // 设置按钮背景
        DrawableHelper.radiusTL(mBackgroundRadius)
                .pressed(android.R.color.transparent, mPressedColor)
                .into(tvCancel);
        DrawableHelper.radiusTR(mBackgroundRadius)
                .pressed(android.R.color.transparent, mPressedColor)
                .into(tvConfirm);

        initialViewData();

        setPickerViewArrayList();
        initialPickerView(isLoop);
        setSelectedData();
    }

    protected void initialViewData() {
    }

    /**
     * 初始化List
     */
    protected void setPickerViewArrayList() {
        if (mPrimaryList == null) mPrimaryList = new ArrayList<>();
        if (mSecondaryList == null) mSecondaryList = new ArrayList<>();
        if (mUnitList == null) mUnitList = new ArrayList<>();
    }

    /**
     * 设置PickerView是否循环显示List
     */
    private void initialPickerView(boolean loop) {
        pvPrimary.setIsLoop(loop);
        pvSecondary.setIsLoop(loop);
        pvUnit.setIsLoop(loop);

        // 设置滑动选中监听事件
        pvPrimary.setOnSelectListener(this);
        pvSecondary.setOnSelectListener(this);
        pvUnit.setOnSelectListener(this);

        // 初始化PickerView加载数据
        pvPrimary.setData(mPrimaryList);
        pvSecondary.setData(mSecondaryList);
        pvUnit.setData(mUnitList);
    }

    /**
     * 初始化选中的数据
     */
    protected void setSelectedData() {
    }

    /**
     * @return 当前选中的数据
     */
    protected String getSelected() {
        return null;
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.tv_confirm) {
            mOnSelectedCallback.onSelect(getSelected());
            dismiss();
        } else if (view.getId() == R.id.tv_cancel) {
            dismiss();
        }
    }

    @Override
    public void onSelect(PickerView pickerView) {
    }

    /**
     * 联动的动画
     *
     * @param view 执行动画的view
     */
    protected void executeAnimator(View view) {
        PropertyValuesHolder pvhX = PropertyValuesHolder.ofFloat("alpha", 1f, 0f, 1f);
        PropertyValuesHolder pvhY = PropertyValuesHolder.ofFloat("scaleX", 1f, 1.3f, 1f);
        PropertyValuesHolder pvhZ = PropertyValuesHolder.ofFloat("scaleY", 1f, 1.3f, 1f);
        ObjectAnimator.ofPropertyValuesHolder(view, pvhX, pvhY, pvhZ)
                .setDuration(200L)
                .start();
    }

    public abstract static class OnSelectedCallback implements OnSelectedListener {

        @Override
        public void onFinish() {

        }

        @Override
        public void onSelect(String date) {

        }
    }

    public interface OnSelectedListener {

        void onFinish();

        void onSelect(String data);
    }
}
