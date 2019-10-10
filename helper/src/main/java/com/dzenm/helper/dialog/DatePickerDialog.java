package com.dzenm.helper.dialog;

import android.annotation.SuppressLint;
import android.graphics.drawable.Drawable;
import androidx.appcompat.app.AppCompatActivity;
import android.text.TextUtils;

import com.dzenm.helper.R;
import com.dzenm.helper.date.DateHelper;
import com.dzenm.helper.log.Logger;
import com.dzenm.helper.view.PickerView;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * @author dzenm
 * @date 2019-07-22 17:22
 * <pre>
 * DatePickerDialog.newInstance(MainActivity.this)
 *             .setLoop(true)
 *             .setPrimaryColor(getResources().getColor(android.R.color.holo_blue_light))
 *             .setOnSelectedCallback(new DatePickerDialog.OnSelectedCallback() {
 *                 @Override
 *                 public void onSelect(String date) {
 *                     ToastHelper.show(date);
 *                 }
 *             }).show();
 * </pre>
 */
@SuppressLint("ValidFragment")
public class DatePickerDialog extends PickerDialog {

    public static final String DEFAULT_START_YEAR = "1970";
    public static final String DEFAULT_END_YEAR = "2100";

    private int mMinDay = 1, mJanuary = 1, mDecember = 12;

    /**
     * 日期的分隔符 {@link #setSeparator(String)}
     */
    private String mSeparator = "/";

    /**
     * 起点日期与终点日期
     */
    private int mStartYear, mStartMonth, mStartDay, mEndYear, mEndMonth, mEndDay;

    /**
     * 起点日期、终点日期、当前选择的日期获取
     */
    private Calendar mStartCalendar, mEndCalendar, mSelectCalendar;

    /************************************* 以下为自定义方法 *********************************/

    public static DatePickerDialog newInstance(AppCompatActivity activity) {
        return new DatePickerDialog(activity);
    }

    /**
     * @param separator 日期显示的分隔符 {@link #mSeparator}
     * @return this
     */
    public DatePickerDialog setSeparator(String separator) {
        mSeparator = separator;
        return this;
    }

    /**
     * 设置日期间隔
     *
     * @param startDate 起始日期
     * @param endDate   终止日期
     * @return this
     */
    public DatePickerDialog setRange(String startDate, String endDate) {
        mStartCalendar.setTime(DateHelper.parseDate(DateHelper.pattern(mSeparator), startDate));
        mEndCalendar.setTime(DateHelper.parseDate(DateHelper.pattern(mSeparator), endDate));
        return this;
    }

    /**
     * 设置日期间隔
     *
     * @param startDateResId 起始日期
     * @param endDateResId   终止日期
     * @return this
     */
    public DatePickerDialog setRange(int startDateResId, int endDateResId) {
        mStartCalendar.setTime(DateHelper.parseDate(DateHelper.pattern(mSeparator),
                getStrings(startDateResId)));
        mEndCalendar.setTime(DateHelper.parseDate(DateHelper.pattern(mSeparator),
                getStrings(endDateResId)));
        return this;
    }

    @Override
    public DatePickerDialog setTitle(int resId) {
        return super.setTitle(resId);
    }

    @Override
    public DatePickerDialog setTitle(String title) {
        return super.setTitle(title);
    }

    @Override
    public DatePickerDialog setLoop(boolean loop) {
        return super.setLoop(loop);
    }

    @Override
    public DatePickerDialog setSelected(int resId) {
        return super.setSelected(resId);
    }

    @Override
    public DatePickerDialog setSelected(String date) {
        return super.setSelected(date);
    }

    @Override
    public DatePickerDialog setMargin(int margin) {
        return super.setMargin(margin);
    }

    @Override
    public DatePickerDialog setGravity(int gravity) {
        return super.setGravity(gravity);
    }

    @Override
    public DatePickerDialog setAnimator(int animator) {
        return super.setAnimator(animator);
    }

    @Override
    public DatePickerDialog setBackground(Drawable background) {
        return super.setBackground(background);
    }

    @Override
    public DatePickerDialog setCenterWidth(int width) {
        return super.setCenterWidth(width);
    }

    @Override
    public DatePickerDialog setPrimaryColor(int primaryColor) {
        return super.setPrimaryColor(primaryColor);
    }

    @Override
    public DatePickerDialog setSecondaryColor(int secondaryColor) {
        return super.setSecondaryColor(secondaryColor);
    }

    @Override
    public DatePickerDialog setTranslucent(boolean translucent) {
        return super.setTranslucent(translucent);
    }

    @Override
    public DatePickerDialog setCancel(boolean cancel) {
        return super.setCancel(cancel);
    }

    @Override
    public DatePickerDialog setTouchInOutSideCancel(boolean cancel) {
        return super.setTouchInOutSideCancel(cancel);
    }

    @Override
    public DatePickerDialog setDivide(boolean divide) {
        return super.setDivide(divide);
    }

    @Override
    public DatePickerDialog setRadiusCard(float radiusCard) {
        return super.setRadiusCard(radiusCard);
    }

    /************************************* 以下为实现过程 *********************************/

    public DatePickerDialog(AppCompatActivity activity) {
        super(activity);
        // 默认的起始日期到终止日期
        mStartCalendar = Calendar.getInstance();
        mEndCalendar = Calendar.getInstance();
        mStartCalendar.setTime(DateHelper.parseDate(DateHelper.pattern(mSeparator),
                DEFAULT_START_YEAR + mSeparator + mJanuary + mSeparator + mMinDay));
        mEndCalendar.setTime(DateHelper.parseDate(DateHelper.pattern(mSeparator),
                DEFAULT_END_YEAR + mSeparator + mJanuary + mSeparator + mMinDay));
        // 选中日期的获取
        mSelectCalendar = Calendar.getInstance();

        // 默认选中当前日期
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        mData = new String[3];
        mData[0] = String.valueOf(calendar.get(Calendar.YEAR));
        mData[1] = String.valueOf(calendar.get(Calendar.MONTH) + 1);
        mData[2] = String.valueOf(calendar.get(Calendar.DAY_OF_MONTH));
    }

    @Override
    protected void initialViewData() {
        if (!TextUtils.isEmpty(mSelectedData)) {
            mData = mSelectedData.split(mSeparator);
            // 进行数字处理01 -> 1
            for (int i = 0; i < mData.length; i++) {
                mData[i] = String.valueOf(Integer.parseInt(mData[i]));
            }
        }
    }

    /**
     * 初始化List
     */
    @Override
    protected void setPickerViewArrayList() {
        super.setPickerViewArrayList();
        mStartYear = mStartCalendar.get(Calendar.YEAR);
        mEndYear = mEndCalendar.get(Calendar.YEAR);

        mStartMonth = mStartCalendar.get(Calendar.MONTH) + 1;
        mEndMonth = mEndCalendar.get(Calendar.MONTH) + 1;

        mStartDay = mStartCalendar.get(Calendar.DAY_OF_MONTH);
        mEndDay = mEndCalendar.get(Calendar.DAY_OF_MONTH);

        // 开始年份必须小于结束年份
        if (mStartYear > mEndYear) throw new NullPointerException("起始时间年份和结尾时间年份顺序错乱");

        // 显示年份的数据
        getDataList(mPrimaryList, mStartYear, mEndYear);

        // 选中的年份, 获取该年的数据
        // (Calendar通过set方法设置月份时, 需要先将实际月份-1再进行set, 通过get方法取出时, 再+1得到实际的月份)
        mSelectCalendar.set(Calendar.YEAR, Integer.parseInt(mData[0]));
        mSelectCalendar.set(Calendar.MONTH, Integer.parseInt(mData[1]) - 1);
        mSelectCalendar.set(Calendar.DAY_OF_MONTH, Integer.parseInt(mData[2]));
        Logger.i("默认选中的日期: " + mData[0] + mSeparator + mData[1] + mSeparator + mData[2]);

        int year = mSelectCalendar.get(Calendar.YEAR);
        int month = mSelectCalendar.get(Calendar.MONTH) + 1;

        if (mStartYear == year) {
            getDataList(mSecondaryList, mStartMonth, mDecember);
            if (mStartMonth == month) {
                getDataList(mUnitList, mStartDay,
                        mSelectCalendar.getActualMaximum(Calendar.DAY_OF_MONTH));
            } else {
                getDataList(mUnitList, mMinDay, mSelectCalendar.getActualMaximum(Calendar.DAY_OF_MONTH));
            }
        } else if (mEndYear == year) {
            getDataList(mSecondaryList, mJanuary, mEndMonth);
            if (mEndMonth == month) {
                getDataList(mUnitList, mMinDay, mEndDay);
            } else {
                getDataList(mUnitList, mMinDay, mSelectCalendar.getActualMaximum(Calendar.DAY_OF_MONTH));
            }
        } else {
            getDataList(mSecondaryList, mJanuary, mDecember);
            getDataList(mUnitList, mMinDay, mSelectCalendar.getActualMaximum(Calendar.DAY_OF_MONTH));
        }
    }

    private void getDataList(List<String> list, int start, int end) {
        list.clear();
        Logger.i("起点: " + DateHelper.formatUnit(start) + ", 终点: " + DateHelper.formatUnit(end));
        for (int i = start; i <= end; i++) {
            list.add(DateHelper.formatUnit(i));
        }
    }

    /**
     * 初始化选中的日期
     */
    @Override
    protected void setSelectedData() {
        pvPrimary.setSelected(DateHelper.formatUnit(mData[0]));
        pvSecondary.setSelected(DateHelper.formatUnit(mData[1]));
        pvUnit.setSelected(DateHelper.formatUnit(mData[2]));
    }

    /**
     * @return 当前选中的日期
     */
    @Override
    protected String getSelected() {
        return pvPrimary.getSelected() + mSeparator + pvSecondary.getSelected() + mSeparator + pvUnit.getSelected();
    }

    @Override
    public void onSelect(PickerView pickerView) {
        String text = pickerView.getSelected();
        if (pickerView.getId() == R.id.pv_primary) {
            // 将Calendar设置为选中的年份, 用于获取该年的数据
            mSelectCalendar.set(Calendar.YEAR, Integer.parseInt(text));
            // 重置月份的数据
            resetMonthList();
        } else if (pickerView.getId() == R.id.pv_secondary) {
            // 将Calendar设置为选中的月份、日期, 用于获取该月的数据
            mSelectCalendar.set(Calendar.MONTH, Integer.valueOf(text) - 1);
            // 重置日期的数据
            resetDayList();
        } else if (pickerView.getId() == R.id.pv_unit) {
            mOnSelectedCallback.onFinish();
        }
    }

    /**
     * 重置月份数据
     */
    private void resetMonthList() {
        // 设定选中的年份数据, 用于获取选中的年份对应的月份数据
        int selectedYear = mSelectCalendar.get(Calendar.YEAR);

        // 控制月份的显示在有效的范围内
        if (selectedYear == mStartYear) {
            getDataList(mSecondaryList, mStartMonth, mDecember);
        } else if (selectedYear == mEndYear) {
            getDataList(mSecondaryList, mJanuary, mEndMonth);
        } else {
            getDataList(mSecondaryList, mJanuary, mDecember);
        }

        pvSecondary.setData(mSecondaryList);
        pvSecondary.setSelected(0);                 // 设置当前选中的为第一个日期

        // 设定选中的月份数据, 用于获取选中的月份对应的日期
        Logger.i("选中的月份数据: " + Integer.parseInt(mSecondaryList.get(0)));
        mSelectCalendar.set(Calendar.MONTH, Integer.parseInt(mSecondaryList.get(0)) - 1);

        executeAnimator(pvSecondary);                // 动画延时
        pvSecondary.postDelayed(new Runnable() {     // 联动日期重置日期
            @Override
            public void run() {
                resetDayList();
            }
        }, 100L);
    }

    /**
     * 重置日期数据
     */
    private void resetDayList() {
        // 设定选中的年份和月份数据, 用于获取选中的某年某月对应的日期数据
        int selectedYear = mSelectCalendar.get(Calendar.YEAR);
        int selectedMonth = mSelectCalendar.get(Calendar.MONTH) + 1;

        // 控制日期的显示在有效的范围内
        if (selectedYear == mStartYear && selectedMonth == mStartMonth) {
            getDataList(mUnitList, mStartDay, mSelectCalendar.getActualMaximum(Calendar.DAY_OF_MONTH));
        } else if (selectedYear == mEndYear && selectedMonth == mEndMonth) {
            getDataList(mUnitList, mMinDay, mEndDay);
        } else {
            getDataList(mUnitList, mMinDay, mSelectCalendar.getActualMaximum(Calendar.DAY_OF_MONTH));
        }

        pvUnit.setData(mUnitList);
        pvUnit.setSelected(0);               // 设置当前选中的为第一个日期

        Logger.i("选中的日期: " + Integer.parseInt(mUnitList.get(0)));
        mSelectCalendar.set(Calendar.DAY_OF_MONTH, Integer.parseInt(mUnitList.get(0)));
        executeAnimator(pvUnit);             // 动画延时
    }
}

