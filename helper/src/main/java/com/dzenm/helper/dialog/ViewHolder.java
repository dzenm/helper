package com.dzenm.helper.dialog;

import android.graphics.drawable.Drawable;
import android.util.SparseArray;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.IdRes;

/**
 * @author dinzhenyan
 * @date 2019-05-21 21:13
 */
public class ViewHolder {

    private SparseArray<View> mViews;
    private View mConvertView;

    private ViewHolder(View view) {
        mConvertView = view;
        mViews = new SparseArray<>();
    }

    public static ViewHolder create(View view) {
        return new ViewHolder(view);
    }

    /**
     * 获取View
     *
     * @param viewId 通过View id获取View
     * @param <T>    获取的类型
     * @return 获取View的子类
     */
    public <T extends View> T getView(@IdRes int viewId) {
        View view = mViews.get(viewId);
        if (view == null) {
            view = mConvertView.findViewById(viewId);
            mViews.put(viewId, view);
        }
        return (T) view;
    }

    public View getConvertView() {
        return mConvertView;
    }

    public TextView getTextView(@IdRes int viewId) {
        return getView(viewId);
    }

    public ImageView getImageView(@IdRes int viewId) {
        return getView(viewId);
    }

    public ListView getListView(@IdRes int viewId) {
        return getView(viewId);
    }

    /**
     * 设置文本
     *
     * @param viewId
     * @param text
     */
    public void setText(int viewId, String text) {
        TextView textView = getView(viewId);
        textView.setText(text);
    }

    /**
     * 获取文本
     *
     * @param viewId
     * @return
     */
    public String getText(int viewId) {
        TextView textView = getView(viewId);
        return textView.getText().toString();
    }

    /**
     * 设置可见性
     *
     * @param viewId
     * @param visible
     */
    public void setVisible(int viewId, int visible) {
        getView(viewId).setVisibility(visible);
    }

    /**
     * 设置可见性
     *
     * @param viewId
     */
    public int getVisible(int viewId) {
        return getView(viewId).getVisibility();
    }

    /**
     * @param viewId
     * @param color
     */
    public void setTextColor(int viewId, int color) {
        TextView textView = getView(viewId);
        textView.setTextColor(color);
    }

    /**
     * 设置字体颜色
     *
     * @param viewId
     * @param size
     */
    public void setTextSize(int viewId, float size) {
        TextView textView = getView(viewId);
        textView.setTextSize(size);
    }

    /**
     * 设置背景图片
     *
     * @param viewId
     * @param drawable
     */
    public void setBackground(int viewId, Drawable drawable) {
        View view = getView(viewId);
        view.setBackground(drawable);
    }

    /**
     * 设置背景图片
     *
     * @param viewId
     * @param resId
     */
    public void setBackgroundResource(int viewId, int resId) {
        View view = getView(viewId);
        view.setBackgroundResource(resId);
    }

    /**
     * 设置背景颜色
     *
     * @param viewId
     * @param color
     */
    public void setBackgroundColor(int viewId, int color) {
        View view = getView(viewId);
        view.setBackgroundColor(color);
    }

    /**
     * 设置点击事件
     *
     * @param viewId
     * @param listener
     */
    public void setOnClickListener(int viewId, View.OnClickListener listener) {
        View view = getView(viewId);
        view.setOnClickListener(listener);
    }
}
