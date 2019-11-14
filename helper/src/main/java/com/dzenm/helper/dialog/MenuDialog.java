package com.dzenm.helper.dialog;

import android.annotation.SuppressLint;
import android.graphics.drawable.Drawable;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Handler;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

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
@SuppressLint("ValidFragment")
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

    @Override
    public MenuDialog setMargin(int margin) {
        return super.setMargin(margin);
    }

    @Override
    public MenuDialog setGravity(int gravity) {
        return super.setGravity(gravity);
    }

    @Override
    public MenuDialog setAnimator(int animator) {
        return super.setAnimator(animator);
    }

    @Override
    public MenuDialog setBackground(Drawable background) {
        return super.setBackground(background);
    }

    @Override
    public MenuDialog setCenterWidth(int width) {
        return super.setCenterWidth(width);
    }

    @Override
    public MenuDialog setPrimaryColor(int primaryColor) {
        return super.setPrimaryColor(primaryColor);
    }

    @Override
    public MenuDialog setSecondaryColor(int secondaryColor) {
        return super.setSecondaryColor(secondaryColor);
    }

    @Override
    public MenuDialog setTranslucent(boolean translucent) {
        return super.setTranslucent(translucent);
    }

    @Override
    public MenuDialog setCancel(boolean cancel) {
        return super.setCancel(cancel);
    }

    @Override
    public MenuDialog setTouchInOutSideCancel(boolean cancel) {
        return super.setTouchInOutSideCancel(cancel);
    }

    @Override
    public MenuDialog setDivide(boolean divide) {
        return super.setDivide(divide);
    }

    @Override
    public MenuDialog setRadiusCard(float radiusCard) {
        return super.setRadiusCard(radiusCard);
    }

    /************************************* 以下为实现过程 *********************************/

    @Override
    protected void initView() {
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
        mView = layout;
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
                return new float[]{mRadiusCard, mRadiusCard, 0, 0};
            } else if (position == items.length - 1 && items.length > 1) {
                return new float[]{0, 0, mRadiusCard, mRadiusCard};
            } else if (items.length == 1) {
                return new float[]{mRadiusCard, mRadiusCard, mRadiusCard, mRadiusCard};
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
                        mOnItemClickListener.onItemClick(position);
                        dismiss();
                    }
                }, 150);
                break;
            }
        }
    }

    public interface OnItemClickListener {
        void onItemClick(int position);
    }
}
