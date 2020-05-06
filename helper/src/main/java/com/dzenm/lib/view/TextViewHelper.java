package com.dzenm.lib.view;

import android.widget.TextView;

/**
 * @author dinzhenyan
 * @date 2019-05-27 16:27
 */
public class TextViewHelper {

    /**
     * 隐藏身份证号码出生年月
     *
     * @param textView 设置文本的TextView
     * @param idCard   身份证号码
     */
    public static void setHideIDCard(TextView textView, String idCard) {
        String regular = "(\\d{6})\\d{8}(\\w{4})";
        String idCardHide = idCard.replaceAll(regular, "$1********$2");
        textView.setText(idCardHide);
    }

    /**
     * 隐藏手机号中间四位
     *
     * @param textView 设置文本的TextView
     * @param phone    手机号
     */
    public static void setHidePhone(TextView textView, String phone) {
        String regular = "(\\d{3})\\d{4}(\\d{4})";
        String phoneHide = phone.replaceAll(regular, "$1****$2");
        textView.setText(phoneHide);
    }
}
