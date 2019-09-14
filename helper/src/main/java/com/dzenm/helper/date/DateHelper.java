package com.dzenm.helper.date;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.widget.DatePicker;
import android.widget.TimePicker;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * @author dinzhenyan
 * @date 2019-04-30 20:03
 * <p>
 * 日期格式化的工具类
 */
public class DateHelper {

    public static final String YEAR = "yyyy";
    public static final String MONTH = "MM";
    public static final String DAY = "dd";
    public static final String HOUR = "HH";
    public static final String MINUTE = "mm";

    private final static String sSeparator = "-";

    private static final String DATE = YEAR + sSeparator + MONTH + sSeparator + DAY;
    private static final String TIME = HOUR + ":" + MINUTE;
    private static final String DATE_TIME = DATE + " " + TIME;

    /**
     * 获取当前时间（具体到毫秒），例：2019-04-29_21:44:08.801
     *
     * @return
     */
    public static String getCurrentTimeMillis() {
        return formatDate("yyyy-MM-dd HH:mm:ss.SSS", new Date());
    }

    /**
     * 获取当前时间（具体到分钟），例：2019-04-29 21:52
     *
     * @return
     */
    public static String getCurrentDateTime() {
        return formatDate(DATE_TIME, new Date());
    }

    /**
     * 获取当前日期（年月日），例：2019-04-29
     *
     * @return
     */
    public static String getCurrentDate() {
        return formatDate(DATE, new Date());
    }

    /**
     * 获取当前时间(只获取时分)例：21:56
     *
     * @return
     */
    public static String getCurrentTime() {
        return formatDate(TIME, new Date());
    }

    /**
     * 将时间戳转日期，例：1556546343688 转换的结果 2019-04-29
     *
     * @param timestamp
     * @return
     */
    public static String timestampToDate(String timestamp) {
        return formatDate(DATE, Long.parseLong(timestamp));
    }

    /**
     * 任意时间戳转时间，例：1556546343688 转换的结果 21:59
     *
     * @param timestamp
     * @return
     */
    public static String timestampToTime(String timestamp) {
        return formatDate(TIME, Long.parseLong(timestamp));
    }

    /**
     * 任意时间戳转时间，例：1556546343688 转换的结果 2019-04-29 21:59
     *
     * @param timestamp
     * @return
     */
    public static String timestampToDateTime(String timestamp) {
        return formatDate(DATE_TIME, Long.parseLong(timestamp));
    }


    /**
     * 日期转星期，例：2019-04-29 转换的结果 星期一
     *
     * @param date 格式必须为yyyy-MM-dd
     * @return
     */
    public static String dateToWeek(String date) {
        String[] weeks = {"星期天", "星期一", "星期二", "星期三", "星期四", "星期五", "星期六"};
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(parseDate(DATE, date));
        int position = calendar.get(Calendar.DAY_OF_WEEK) - 1;
        return weeks[position];
    }

    /**
     * 日期转时间戳 例：2019-04-29 21:59 转换的结果 1556546340000
     *
     * @param datetime 格式必须为yyyy-MM-dd HH:mm
     * @return
     */
    public static long dateTimeToTimestamp(String datetime) {
        return parseDate(DATE_TIME, datetime).getTime();
    }

    /**
     * 日期转时间戳 例：2019-04-29 转换的结果 1556546340000
     *
     * @param datetime 格式必须为yyyy-MM-dd
     * @return
     */
    public static long dateToTimestamp(String datetime) {
        return parseDate(DATE, datetime).getTime();
    }

    /**
     * 获取该时间在该星期的第一天和最后一天日期
     *
     * @param date 格式必须为yyyy-MM-dd
     * @return
     */
    public static String[] getFirstAndLastOfWeek(String date) {
        String[] weeks = new String[2];
        // 获取Calendar实例，并重新设置格式化的日期
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(parseDate(DATE, date));
        int day = (calendar.get(Calendar.DAY_OF_WEEK) == 1) ? -6 : 2 - calendar.get(Calendar.DAY_OF_WEEK);

        // 对日期重新调整之后获取该周的第一天
        calendar.add(Calendar.DAY_OF_WEEK, day);
        weeks[0] = formatDate(DATE, calendar.getTime());      // 所在星期开始日期
        // 对日期重新调整之后获取该周的最后一天
        calendar.add(Calendar.DAY_OF_WEEK, 6);
        weeks[1] = formatDate(DATE, calendar.getTime());      // 所在星期结束日期
        return weeks;
    }

    /**
     * 当前日期（通过Calendar类获取），例：2019-4-29
     *
     * @return
     */
    public static String nowDate() {
        Calendar calendar = Calendar.getInstance();
        return calendar.get(Calendar.YEAR) + sSeparator + (calendar.get(Calendar.MONTH) + 1) + sSeparator + calendar.get(Calendar.DAY_OF_MONTH);
    }

    /**
     * 当前时间（通过Calendar类获取），例：22:48
     *
     * @return
     */
    public static String nowTime() {
        return Calendar.getInstance().get(Calendar.HOUR_OF_DAY) + ":" + Calendar.getInstance().get(Calendar.MINUTE);
    }

    /**
     * 日期View获取PickerDialog的日期
     *
     * @param context
     */
    public static void dateTimeDialog(final Context context, final OnDateDialogListener onDateDialogListener) {
        final Calendar calendarDate = Calendar.getInstance();
        new DatePickerDialog(context, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                //--------monthOfYear 得到的月份会减1所以我们要加1
                String mMonth = monthOfYear < 10 ? "0" + (monthOfYear + 1) : String.valueOf(monthOfYear + 1);
                String mDay = dayOfMonth < 10 ? "0" + (dayOfMonth) : String.valueOf(dayOfMonth);
                onDateDialogListener.onDate(String.valueOf(year) + "-" + mMonth + "/" + mDay);
                Calendar calendarTime = Calendar.getInstance();
                new TimePickerDialog(context, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        //------对一位数的时间在其前面加一个0
                        String mHour = hourOfDay < 10 ? ("0" + hourOfDay) : String.valueOf(hourOfDay);
                        String mMinute = minute < 10 ? ("0" + minute) : String.valueOf(minute);

                        onDateDialogListener.onTime(mHour + ":" + mMinute);
                    }
                }, calendarTime.get(Calendar.HOUR_OF_DAY), calendarTime.get(Calendar.MINUTE), true).show();

            }

        }, calendarDate.get(Calendar.YEAR), calendarDate.get(Calendar.MONTH), calendarDate.get(Calendar.DAY_OF_MONTH)).show();
    }

    /**
     * 日期View获取PickerDialog的日期
     *
     * @param context
     */
    public static void currentDate(Context context, final OnDateDialogListener onDateDialogListener) {
        Calendar calendar = Calendar.getInstance();
        new DatePickerDialog(context, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                //------monthOfYear 得到的月份会减1所以我们要加1
                String mMonth = monthOfYear < 9 ? "0" + (monthOfYear + 1) : String.valueOf(monthOfYear + 1);
                String mDay = dayOfMonth < 10 ? "0" + dayOfMonth : String.valueOf(dayOfMonth);

                onDateDialogListener.onDate(String.valueOf(year) + "-" + mMonth + "-" + mDay);
            }
        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show();
    }

    /**
     * 日期View获取PickerDialog的日期
     *
     * @param context
     */
    public static void currentTime(Context context, final OnDateDialogListener onDateDialogListener) {
        Calendar calendar = Calendar.getInstance();
        new DatePickerDialog(context, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                //--------monthOfYear 得到的月份会减1所以我们要加1
                String mMonth = monthOfYear < 10 ? "0" + (monthOfYear + 1) : String.valueOf(monthOfYear + 1);
                String mDay = dayOfMonth < 10 ? "0" + (dayOfMonth) : String.valueOf(dayOfMonth);

                onDateDialogListener.onDate(mDay + "-" + mMonth + "月");
            }
        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show();
    }

    /**
     * 时间View获取PickerDialog的时间
     *
     * @param context
     */
    public static void timeDialog(Context context, final OnDateDialogListener onDateDialogListener) {
        Calendar calendar = Calendar.getInstance();
        new TimePickerDialog(context, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                onDateDialogListener.onTime(Integer.toString(hourOfDay) + ":" + Integer.toString(minute));
            }
        }, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), true).show();
    }

    /**
     * 字符串日期转化为Date
     *
     * @param pattern
     * @param source
     * @return
     */
    @SuppressLint("SimpleDateFormat")
    public static Date parseDate(String pattern, String source) {
        Date date = null;
        try {
            date = new SimpleDateFormat(pattern).parse(source);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date;
    }

    /**
     * 日期格式化
     *
     * @param pattern
     * @return
     */
    @SuppressLint({"SimpleDateFormat"})
    public static String formatDate(String pattern, Object date) {
        if (date instanceof Date) return new SimpleDateFormat(pattern).format((Date) date);
        return new SimpleDateFormat(pattern).format(date);
    }

    /**
     * 格式化单元
     *
     * @param unit 将个位的数字格式化为两位数字
     * @return 两位的数字
     */
    public static String formatUnit(int unit) {
        return unit < 10 ? "0" + unit : String.valueOf(unit);
    }

    /**
     * 格式化单元
     *
     * @param unit 将个位的数字格式化为两位数字
     * @return 两位的数字
     */
    public static String formatUnit(String unit) {
        int i = Integer.valueOf(unit);
        return i < 10 ? "0" + i : "" + unit;
    }

    public static String pattern(String separator) {
        return YEAR + separator + MONTH + separator + DAY;
    }

    public class OnDateDialogListener implements OnDateSetListener {

        @Override
        public void onDateTime(String date, String time) {

        }

        @Override
        public void onDate(String date) {

        }

        @Override
        public void onTime(String time) {

        }
    }

    public interface OnDateSetListener {

        void onDateTime(String date, String time);

        void onDate(String date);

        void onTime(String time);
    }
}
