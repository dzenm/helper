package com.dzenm.helper.draw;

import androidx.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * @author dzenm
 * @date 2019-08-19 22:02
 */
@IntDef({SelectorState.STATE_PRESSED, SelectorState.STATE_ENABLED, SelectorState.STATE_SELECTED,
        SelectorState.STATE_CHECKED, SelectorState.STATE_CHECKABLE, SelectorState.STATE_FOCUSED,
        SelectorState.STATE_WINDOW_FOCUSED, SelectorState.STATE_ACTIVATED, SelectorState.STATE_HOVERED})
@Retention(RetentionPolicy.SOURCE)
@interface SelectorState {

    // 按压状态
    int STATE_PRESSED = android.R.attr.state_pressed;

    // 可用状态(触摸或点击事件)
    int STATE_ENABLED = android.R.attr.state_enabled;

    // 选中状态
    int STATE_SELECTED = android.R.attr.state_selected;

    // 勾选状态(用于CheckBox和RadioButton)
    int STATE_CHECKED = android.R.attr.state_checked;

    // 勾选可用状态
    int STATE_CHECKABLE = android.R.attr.state_checkable;

    // 焦点获得状态
    int STATE_FOCUSED = android.R.attr.state_focused;

    // 当前窗口焦点获得状态(下拉通知栏或弹出对话框)
    int STATE_WINDOW_FOCUSED = android.R.attr.state_window_focused;

    // 是否激活状态(API 11以上支持, 可用setActivated()设置)
    int STATE_ACTIVATED = android.R.attr.state_activated;

    // 鼠标滑动状态(API 14以上才支持)
    int STATE_HOVERED = android.R.attr.state_hovered;
}
