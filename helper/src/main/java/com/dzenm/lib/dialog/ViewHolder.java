package com.dzenm.lib.dialog;

import android.graphics.Paint;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.util.SparseArray;
import android.view.View;
import android.widget.Checkable;
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
    private View mItemView;

    private ViewHolder(View view) {
        mItemView = view;
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
            view = mItemView.findViewById(viewId);
            mViews.put(viewId, view);
        }
        return (T) view;
    }

    public View getItemView() {
        return mItemView;
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
     * @param viewId 需要设置的View ID
     * @param text   文本
     */
    public void setText(int viewId, String text) {
        TextView textView = getView(viewId);
        textView.setText(text);
    }

    /**
     * 获取文本
     *
     * @param viewId 需要设置的View
     * @return 文本
     */
    public String getText(int viewId) {
        TextView textView = getView(viewId);
        return textView.getText().toString();
    }

    /**
     * 设置可见性
     *
     * @param viewId  需要设置的View ID
     * @param visible 可见性
     */
    public void setVisible(int viewId, int visible) {
        getView(viewId).setVisibility(visible);
    }

    /**
     * 设置控件是否可见
     */
    public ViewHolder setVisible(int viewId, boolean visible) {
        View view = getView(viewId);
        if (visible) {
            view.setVisibility(View.VISIBLE);
        } else {
            view.setVisibility(View.GONE);
        }
        return this;
    }

    /**
     * 设置可见性
     *
     * @param viewId 需要设置的View ID
     */
    public int getVisible(int viewId) {
        return getView(viewId).getVisibility();
    }

    /**
     * 设置字体颜色
     *
     * @param viewId 需要设置的View ID
     * @param color  颜色ID
     */
    public void setTextColor(int viewId, int color) {
        TextView textView = getView(viewId);
        if (textView != null) {
            textView.setTextColor(color);
        }
    }

    /**
     * 设置字体大小
     *
     * @param viewId 需要设置的View ID
     * @param size   颜色大小
     */
    public void setTextSize(int viewId, float size) {
        TextView textView = getView(viewId);
        textView.setTextSize(size);
    }

    /**
     * 设置背景图片
     *
     * @param viewId   需要设置的View ID
     * @param drawable drawable图片
     */
    public void setBackground(int viewId, Drawable drawable) {
        View view = getView(viewId);
        view.setBackground(drawable);
    }

    /**
     * 设置背景图片
     *
     * @param viewId 需要设置的View ID
     * @param resId  资源 ID
     */
    public void setBackgroundResource(int viewId, int resId) {
        View view = getView(viewId);
        view.setBackgroundResource(resId);
    }

    /**
     * 设置背景颜色
     *
     * @param viewId 需要设置的View ID
     * @param color  颜色 ID
     */
    public void setBackgroundColor(int viewId, int color) {
        View view = getView(viewId);
        view.setBackgroundColor(color);
    }

    /**
     * 设置加粗
     */
    public ViewHolder setBold(TextView tv, boolean isBold) {
        if (tv != null) {
            if (isBold) {
                tv.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
            } else {
                tv.setTypeface(Typeface.defaultFromStyle(Typeface.NORMAL));
            }
        }
        return this;
    }

    /**
     * 设置加粗
     */
    public ViewHolder setBold(int viewId, boolean isBold) {
        TextView tv = getView(viewId);
        if (tv != null) {
            if (isBold) {
                tv.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
            } else {
                tv.setTypeface(Typeface.defaultFromStyle(Typeface.NORMAL));
            }
        }
        return this;
    }

    /**
     * 设置下划线
     */
    public ViewHolder setUnderLine(TextView tv, boolean isUnderLine) {
        if (tv != null) {
            if (isUnderLine) {
                tv.setPaintFlags(tv.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
            } else {
                tv.setPaintFlags(tv.getPaintFlags() & (~Paint.UNDERLINE_TEXT_FLAG));
            }
        }
        return this;
    }

    /**
     * 设置下划线
     */
    public ViewHolder setUnderLine(int viewId, boolean isUnderLine) {
        TextView tv = getView(viewId);
        if (tv != null) {
            if (isUnderLine) {
                tv.setPaintFlags(tv.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
            } else {
                tv.setPaintFlags(tv.getPaintFlags() & (~Paint.UNDERLINE_TEXT_FLAG));
            }
        }
        return this;
    }

    /**
     * 设置删除线
     */
    public ViewHolder setDeleteLine(int viewId, boolean isDeleteLine) {
        TextView tv = getView(viewId);
        if (tv != null) {
            if (isDeleteLine) {
                tv.setPaintFlags(tv.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            } else {
                tv.setPaintFlags(tv.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
            }
        }
        return this;
    }

    /**
     * 设置删除线
     */
    public ViewHolder setDeleteLine(TextView tv, boolean isDeleteLine) {
        if (tv != null) {
            if (isDeleteLine) {
                tv.setPaintFlags(tv.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            } else {
                tv.setPaintFlags(tv.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
            }
        }
        return this;
    }

    /**
     * 设置控件选中
     */
    public ViewHolder setChecked(int viewId, boolean checked) {
        Checkable view = getView(viewId);
        view.setChecked(checked);
        return this;
    }

    /**
     * 设置 Item 点击事件
     *
     * @param viewId   需要设置的View ID
     * @param listener 监听事件
     */
    public void setOnClickListener(int viewId, View.OnClickListener listener) {
        View view = getView(viewId);
        if (view != null) {
            view.setOnClickListener(listener);
        }
    }

    /**
     * 设置 Item 点击事件
     */
    public void setOnClickListener(View.OnClickListener listener) {
        mItemView.setOnClickListener(listener);
    }

    /**
     * 设置 Item 长按事件
     */
    public void setOnLongClickListener(View.OnLongClickListener listener) {
        mItemView.setOnLongClickListener(listener);
    }

    /**
     * 设置 Item 点击事件
     */
    public void setOnLongClickListener(int viewId, View.OnLongClickListener listener) {
        View view = getView(viewId);
        if (view != null) {
            view.setOnLongClickListener(listener);
        }
    }
}
