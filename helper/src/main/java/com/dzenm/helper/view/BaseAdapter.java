package com.dzenm.helper.view;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.dzenm.helper.dialog.ViewHolder;

import java.util.ArrayList;
import java.util.List;

/**
 * 适配器的封装，可以快速使用
 */
public class BaseAdapter<T> extends AbsAdapterLayout.AbsAdapter {

    protected int mLayoutId;
    protected List<T> mData;
    protected Context mContext;
    protected LayoutInflater mInflater;

    public BaseAdapter(Context context) {
        mContext = context;
    }

    public BaseAdapter(Context context, List<T> data) {
        this(context, data, 0);
    }

    public BaseAdapter(Context context, int layoutId) {
        this(context, null, layoutId);
    }

    public BaseAdapter(Context context, List<T> data, int layoutId) {
        mContext = context;
        mData = data == null ? new ArrayList<T>() : new ArrayList<T>(data);
        mLayoutId = layoutId;
        mInflater = LayoutInflater.from(mContext);
    }

    protected T getItem(int position) {
        return mData.get(position);
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    @Override
    public View onCreateView(ViewGroup parent) {
        int layoutId = layoutId();
        if (layoutId == 0) {
            return null;
        }
        return mInflater.inflate(layoutId, parent, false);
    }

    protected int layoutId() {
        return mLayoutId;
    }

    @Override
    public void onBindView(View view, int position) {
        ViewHolder viewHolder = ViewHolder.create(view);
        convert(viewHolder, mData.get(position), position);
    }

    protected void convert(ViewHolder holder, T item, int position) { }

    //========================================== 数据相关 ================================================//

    public void add(T elem) {
        mData.add(elem);
        notifyItemInserted(mData.size() - 1);
    }

    public void add(List<T> data) {
        mData.addAll(data);
        notifyItemInserted(mData.size(), data.size());
    }

    public void add(int position, T elem) {
        mData.add(position, elem);
        notifyItemInserted(position);
    }

    private void set(T oldElem, T newElem) {
        set(mData.indexOf(oldElem), newElem);
    }

    private void set(int index, T elem) {
        mData.set(index, elem);
        notifyDataSetChanged();
    }

    public void remove(T elem) {
        mData.remove(elem);
        notifyItemRemoved(mData.size() - 1);
    }

    public void remove(int index) {
        mData.remove(index);
        notifyItemRemoved(index, 1);
    }

    public void replace(List<T> elem) {
        mData.clear();
        if (elem != null && elem.isEmpty()) {
            mData.addAll(elem);
        }
        notifyDataSetChanged();
    }

    public void change(List<T> elem) {
        mData.clear();
        mData.addAll(elem);
    }

    public boolean contains(T elem) {
        return mData.contains(elem);
    }

    public void clear() {
        mData.clear();
        notifyDataSetChanged();
    }

    public List<T> getData() {
        return mData;
    }

    public boolean isLast(int position) {
        return mData != null && mData.size() - 1 == position;
    }
}
