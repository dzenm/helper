package com.dzenm.lib.view;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.dzenm.lib.R;
import com.dzenm.lib.drawable.DrawableHelper;

import java.util.ArrayList;
import java.util.List;

public class ImageAdapter<T> extends AbsAdapterLayout.AbsAdapter {

    private int mLayoutId;
    private List<T> mData;
    private ImageLoader mLoader;
    private OnItemClickListener<T> mOnItemClickListener;
    private boolean isEditable = true;
    private int mMaxCount = 9;
    private int mNumber;

    public ImageAdapter(List<T> data) {
        this(data, 0);
    }

    public ImageAdapter(List<T> data, int layoutId) {
        mData = data == null ? new ArrayList<T>() : new ArrayList<>(data);
        mLayoutId = layoutId;
    }

    public void setImageLoader(ImageLoader imageLoader) {
        this.mLoader = imageLoader;
    }

    public void setEditable(boolean editable) {
        isEditable = editable;
    }

    public void setNumber(int number) {
        mNumber = number;
    }

    public void setOnItemClickListener(OnItemClickListener<T> mOnItemClickListener) {
        this.mOnItemClickListener = mOnItemClickListener;
    }

    protected T getItem(int position) {
        return mData.get(position);
    }

    @Override
    public int getItemCount() {
        return isEditable ? mData.size() + 1 : mData.size();
    }

    protected int layoutId() {
        return mLayoutId;
    }

    @Override
    public View onCreateView(ViewGroup parent) {
        if (mLayoutId != 0) {
            return LayoutInflater.from(parent.getContext()).inflate(mLayoutId, parent);
        } else {
            ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            RatioImageView imageView = new RatioImageView(parent.getContext());
            imageView.setLayoutParams(layoutParams);
            imageView.setPivotX(0);
            imageView.setPivotY(0);
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            return imageView;
        }
    }

    @Override
    public void onBindView(View view, final int position) {
        if (view instanceof RatioImageView) {
            final RatioImageView imageView = (RatioImageView) view;
            if (mLoader == null) {
                throw new NullPointerException("must be use a image loader");
            }
            if (position == mData.size()) {
                imageView.setImageResource(R.drawable.ic_add);
                DrawableHelper.ripple(R.color.colorLightGray, R.color.colorHint).into(imageView);
                if (mOnItemClickListener != null) {
                    imageView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            mOnItemClickListener.onLoad(ImageAdapter.this);
                        }
                    });
                }
            } else {
                mLoader.onLoader(imageView, getItem(position));
                if (getItemCount() == mMaxCount && !isEditable) {
                    imageView.setNumber(mNumber);
                }
                if (mOnItemClickListener != null) {
                    imageView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            mOnItemClickListener.onItemClick(imageView, getItem(position), position);
                        }
                    });
                }
            }
        }
    }

    public void add(T elem) {
        if (isEditable) {
            if (mData.size() + 1 == mMaxCount) {
                removeLast();
            }
        }
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

    public void removeLast() {
        notifyItemRemoved(mData.size(), 1);
    }

    public static class OnItemClickListener<T> {
        public void onItemClick(View view, T data, int position) { }
        public void onLoad(ImageAdapter adapter) { }
    }
}
