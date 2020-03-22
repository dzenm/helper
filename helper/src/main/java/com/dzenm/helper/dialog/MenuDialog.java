package com.dzenm.helper.dialog;

import android.os.Bundle;
import android.os.Handler;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.dzenm.helper.R;
import com.dzenm.helper.draw.DrawableHelper;
import com.dzenm.helper.os.OsHelper;

/**
 * @author dzenm
 * @date 2019-08-18 18:22
 * <pre>
 *  MenuDialog.newInstance(this)
 *          .setItem("测试", "第二个", "取消")
 *          .setRadiusCard(2f)
 *          .setOnItemClickListener(new MenuDialog.OnItemClickListener() {
 *              @Override
 *              public void onItemClick(Object tag) {
 *                  if (tag.equals("测试")) {
 *                      ToastHelper.show("测试");
 *                  } else if (tag.equals("取消")) {
 *                      ToastHelper.show("取消");
 *                  }
 *              }
 *          }).setGravity(Gravity.BOTTOM)
 *          .show();
 * </pre>
 */
public class MenuDialog extends AbsDialogFragment implements View.OnClickListener {

    /**
     * Item列表 {@link #setItem(String...)}
     */
    private String[] mItems;

    /**
     * Item点击事件 {@link #setOnItemClickListener(OnItemClickListener)}
     */
    private OnItemClickListener mOnItemClickListener;

    /************************************* 以下为自定义方法 *********************************/

    public static MenuDialog newInstance(AppCompatActivity activity) {
        return new MenuDialog(activity);
    }

    public MenuDialog setOnItemClickListener(OnItemClickListener onItemClickListener) {
        mOnItemClickListener = onItemClickListener;
        return this;
    }

    public MenuDialog(AppCompatActivity activity) {
        super(activity);
    }

    public MenuDialog setItem(String... item) {
        mItems = item;
        return this;
    }

    /************************************* 以下为实现过程 *********************************/

    @Override
    protected View inflater(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        setTouchInOutSideCancel(true);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        LinearLayout layout = new LinearLayout(mActivity);
        layout.setOrientation(LinearLayout.VERTICAL);
        if (isShowCenter()) {
            int paddingVertical = OsHelper.dp2px(8);
            layout.setPadding(0, paddingVertical, 0, paddingVertical);
        }
        layout.setLayoutParams(layoutParams);

        for (int i = 0; i < mItems.length; i++) {
            layout.addView(addItemView(mItems, i));
            if (!isDivide) continue;
            if (i == mItems.length - 1) continue;
            layout.addView(addDivideView());
        }
        return layout;
    }

    /**
     * 添加ItemView
     *
     * @param position item position
     */
    protected View addItemView(String[] items, int position) {
        View view = createItemView();
        if (view instanceof TextView) {
            TextView textView = (TextView) view;
            textView.setText(items[position]);
            textView.setTextColor(mPrimaryTextColor);
            textView.setTag(items[position]);
            textView.setOnClickListener(this);
            int color = isDefaultBackground ? R.color.colorDivideDark : R.color.colorDivideLight;
            DrawableHelper.radius(setPressedRadius(mItems, position))
                    .ripple(android.R.color.white, color)
                    .into(textView);
        }
        return view;
    }

    protected View createItemView() {
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        TextView textView = new TextView(mActivity);
        int paddingVertical, paddingHorizontal;
        if (isShowCenter()) {
            paddingVertical = OsHelper.dp2px(16);
            paddingHorizontal = OsHelper.dp2px(32);
        } else {
            textView.setGravity(Gravity.CENTER);
            paddingVertical = paddingHorizontal = OsHelper.dp2px(16);
        }
        textView.setPadding(paddingHorizontal, paddingVertical, paddingHorizontal, paddingVertical);
        textView.setLayoutParams(params);
        return textView;
    }

    /**
     * 设置点击的背景圆角效果
     *
     * @param items    item
     * @param position 位置
     * @return 圆角
     */
    private float[] setPressedRadius(String[] items, int position) {
        if (isShowCenter()) {
            return new float[]{0, 0, 0, 0};
        } else {
            if (position == 0 && items.length > 1) {
                return new float[]{mBackgroundRadius, mBackgroundRadius, 0, 0};
            } else if (position == items.length - 1 && items.length > 1) {
                return new float[]{0, 0, mBackgroundRadius, mBackgroundRadius};
            } else if (items.length == 1) {
                return new float[]{mBackgroundRadius, mBackgroundRadius, mBackgroundRadius, mBackgroundRadius};
            } else {
                return new float[]{0, 0, 0, 0};
            }
        }
    }

    /**
     * 添加分割线
     *
     * @return 分割线的view
     */
    private View addDivideView() {
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, 1);
        View view = new View(mActivity);
        view.setLayoutParams(params);
        int color = isDefaultBackground ? mPressedColor : android.R.color.white;
        view.setBackgroundColor(getColor(color));
        return view;
    }

    @Override
    public void onClick(View v) {
        for (int i = 0; i < mItems.length; i++) {
            if (v.getTag() == mItems[i]) {
                final int position = i;
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (onClick(position)) {
                            dismiss();
                        }
                    }
                }, 150);
                break;
            }
        }
    }

    protected boolean onClick(final int position) {
        mOnItemClickListener.onItemClick(position);
        return true;
    }

    public interface OnItemClickListener {
        void onItemClick(int position);
    }
}
