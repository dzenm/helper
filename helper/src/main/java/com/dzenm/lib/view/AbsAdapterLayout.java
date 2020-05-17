package com.dzenm.lib.view;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.database.Observable;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

import com.dzenm.lib.log.Logger;
import com.dzenm.lib.os.LifecycleCallbacks;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 为多个子View定义出来的ViewGroup
 */
public abstract class AbsAdapterLayout extends ViewGroup {

    protected AdapterLayoutDataObserver mObserver = new AdapterLayoutDataObserver();
    protected AbsAdapter mAdapter;
    protected List<View> mViews;

    public AbsAdapterLayout(Context context) {
        this(context, null);
    }

    public AbsAdapterLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public AbsAdapterLayout(final Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mViews = new ArrayList<>();
        if (context instanceof Activity) {
            Application application = ((Activity) context).getApplication();
            application.registerActivityLifecycleCallbacks(new LifecycleCallbacks() {
                @Override
                public void onActivityDestroyed(Activity activity) {
                    if (activity == context) {
                        // 移除监听
                        if (mAdapter != null && mObserver != null) {
                            mAdapter.unregisterAdapterDataObserver(mObserver);
                            mAdapter = null;
                            mObserver = null;
                        }
                        ((Activity) context).getApplication().unregisterActivityLifecycleCallbacks(this);
                    }
                }
            });
        }
    }

    /**
     * 设置Adapter
     */
    public void setAdapter(AbsAdapter adapter) {
        setAdapterInternal(adapter);
    }

    protected void setAdapterInternal(AbsAdapter adapter) {
        if (mAdapter != null) {
            // 移除监听
            mViews.clear();
            mAdapter.unregisterAdapterDataObserver(mObserver);
        }

        mAdapter = adapter;
        if (mAdapter != null) {
            mAdapter.registerAdapterDataObserver(mObserver);
        }

        resetLayout();
    }

    private class AdapterLayoutDataObserver extends AdapterDataObserver {
        @Override
        public void onChanged() {
            resetLayout();
        }

        @Override
        public void onItemChanged(final int positionStart, final int itemCount) {
            post(new Runnable() {
                @Override
                public void run() {
                    onLayoutItemChanged(positionStart, itemCount);
                }
            });
        }

        @Override
        public void onItemInserted(final int positionStart, final int itemCount) {
            post(new Runnable() {
                @Override
                public void run() {
                    onLayoutItemInserted(positionStart, itemCount);
                }
            });
        }

        @Override
        public void onItemRemoved(final int positionStart, final int itemCount) {
            post(new Runnable() {
                @Override
                public void run() {
                    onLayoutItemRemoved(positionStart, itemCount);
                }
            });
        }

        @Override
        public void onItemMoved(final int fromPosition, final int toPosition, final int itemCount) {
            post(new Runnable() {
                @Override
                public void run() {
                    onLayoutItemMoved(fromPosition, toPosition, itemCount);
                }
            });
        }
    }

    protected void onLayoutItemChanged(int positionStart, int itemCount) {
        // 刷新View的位置
        invalidateItemView(positionStart, positionStart + itemCount);
    }

    protected void onLayoutItemInserted(int positionStart, int itemCount) {
        for (int i = positionStart; i < positionStart + itemCount; i++) {
            Logger.d("add layout view position: " + i);
            bindView(i);
        }
        // 刷新View的位置
        invalidateItemView(positionStart + itemCount - 1, getTotalCount());
    }

    protected void onLayoutItemRemoved(int positionStart, int itemCount) {
        for (int i = positionStart; i < positionStart + itemCount; i++) {
            Logger.d("remove layout view position: " + i);
            unbindView(i);
        }
        // 刷新View的位置
        invalidateItemView(positionStart, getTotalCount());
    }

    protected void onLayoutItemMoved(int fromPosition, int toPosition, int itemCount) {
    }

    /**
     * 重新添加布局
     */
    protected void resetLayout() {
        post(new Runnable() {
            @Override
            public void run() {
                mAdapter.onAttached(AbsAdapterLayout.this);
                if (getChildCount() > 0) {
                    removeAllViews();
                }

                int count = getTotalCount();
                for (int i = 0; i < count; i++) {
                    mAdapter.onBindView(bindView(i), i);
                }
                mAdapter.onDetached(AbsAdapterLayout.this);
            }
        });
    }

    /**
     * 添加View
     *
     * @param position 添加的位置
     */
    protected View bindView(int position) {
        View view = getItemView();
        view.setTag(position);
        mViews.add(position, view);
        addView(view, position);
        return view;
    }

    /**
     * 移除View
     *
     * @param position 移除的位置
     */
    private void unbindView(int position) {
        View itemView = mViews.get(position);
        if (itemView != null && itemView.getParent() != null) {
            if (itemView.getTag().equals(position)) {
                mViews.remove(position);
                removeViewAt(position);
            } else {
                throw new IllegalArgumentException("remove view's position is not equals view's tag");
            }
        }
    }

    /**
     * 刷新子View
     *
     * @param start 刷新的起始位置
     * @param end   刷新的末尾位置
     */
    private void invalidateItemView(int start, int end) {
        for (int i = start; i < end; i++) {
            View itemView = mViews.get(i);
            if (!itemView.getTag().equals(i)) {
                itemView.setTag(i);
            }
            mAdapter.onBindView(itemView, i);
        }
    }

    /**
     * 获取所有的Item数量, 包含未显示的
     *
     * @return 所有的Item
     */
    protected int getTotalCount() {
        return getItemCount();
    }

    /**
     * 获取Item View的个数
     *
     * @return 返回的数量
     */
    protected int getItemCount() {
        return mAdapter.getItemCount();
    }

    /**
     * 获取创建的Item View
     *
     * @return Item View
     */
    protected View getItemView() {
        return mAdapter.onCreateView(this);
    }

    /**
     * {@link AbsAdapterLayout} 适配器, 和 RecyclerView 的用法类似, 不可以使用多类型布局
     */
    public abstract static class AbsAdapter implements Serializable {

        private final AdapterDataObservable mObservable = new AdapterDataObservable();

        /**
         * Item数量
         */
        public abstract int getItemCount();

        /**
         * Item布局
         *
         * @param parent 父布局
         * @return Item
         */
        public abstract View onCreateView(ViewGroup parent);

        /**
         * 设置View参数
         *
         * @param view     当前View
         * @param position 所在的位置
         */
        public abstract void onBindView(View view, int position);

        /**
         * 注册数据监听
         */
        public void registerAdapterDataObserver(AdapterDataObserver observer) {
            try {
                mObservable.registerObserver(observer);
            } catch (Throwable e) {
                e.printStackTrace();
            }
        }

        /**
         * 移除数据监听
         */
        public void unregisterAdapterDataObserver(AdapterDataObserver observer) {
            try {
                mObservable.unregisterObserver(observer);
            } catch (Throwable e) {
                e.printStackTrace();
            }
        }

        public void onAttached(AbsAdapterLayout layout) {

        }

        public void onDetached(AbsAdapterLayout layout) {

        }

        /**
         * 内容改变
         */
        public final void notifyDataSetChanged() {
            mObservable.notifyChanged();
        }

        public final void notifyItemChanged(int position) {
            mObservable.onItemChanged(position, 1);
        }

        public final void notifyItemChanged(int positionStart, int itemCount) {
            mObservable.onItemChanged(positionStart, itemCount);
        }

        public final void notifyItemInserted(int position) {
            mObservable.onItemInserted(position, 1);
        }

        public final void notifyItemInserted(int positionStart, int itemCount) {
            mObservable.onItemInserted(positionStart, itemCount);
        }

        public final void notifyItemRemoved(int position) {
            mObservable.onItemRemoved(position, 1);
        }

        public final void notifyItemRemoved(int positionStart, int itemCount) {
            mObservable.onItemRemoved(positionStart, itemCount);
        }

        public final void notifyItemMoved(int fromPosition, int toPosition, int itemCount) {
            mObservable.onItemMoved(fromPosition, toPosition, itemCount);
        }

    }

    /**
     * 数据驱动的观察者, 驱动 {@link AdapterDataObserver} 更新数据
     */
    static class AdapterDataObservable extends Observable<AdapterDataObserver> {

        public boolean hasObservers() {
            return !mObservers.isEmpty();
        }

        public void notifyChanged() {
            for (int i = mObservers.size() - 1; i >= 0; i--) {
                mObservers.get(i).onChanged();
            }
        }

        public void onItemChanged(int positionStart, int itemCount) {
            for (int i = mObservers.size() - 1; i >= 0; i--) {
                mObservers.get(i).onItemChanged(positionStart, itemCount);
            }
        }

        public void onItemInserted(int positionStart, int itemCount) {
            for (int i = mObservers.size() - 1; i >= 0; i--) {
                mObservers.get(i).onItemInserted(positionStart, itemCount);
            }
        }

        public void onItemRemoved(int positionStart, int itemCount) {
            for (int i = mObservers.size() - 1; i >= 0; i--) {
                mObservers.get(i).onItemRemoved(positionStart, itemCount);
            }
        }

        public void onItemMoved(int fromPosition, int toPosition, int itemCount) {
            for (int i = mObservers.size() - 1; i >= 0; i--) {
                mObservers.get(i).onItemMoved(fromPosition, toPosition, itemCount);
            }
        }

    }

    /**
     * 数据驱动的被观察者, 更新 {@link AbsAdapter} 的数据
     */
    public abstract static class AdapterDataObserver {

        public void onChanged() {

        }

        public void onItemChanged(int positionStart, int itemCount) {
        }

        public void onItemInserted(int positionStart, int itemCount) {
        }

        public void onItemRemoved(int positionStart, int itemCount) {
        }

        public void onItemMoved(int fromPosition, int toPosition, int itemCount) {
        }
    }
}